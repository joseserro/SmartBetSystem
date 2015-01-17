package main.java.tsi.gui;

import main.java.tsi.neural.Neural;
import main.java.tsi.tools.DatabaseConnection;
import main.java.tsi.tools.Util;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

public class ConsolaAvancada {
	
	private static JFrame window = new JFrame("Consola Avançada");
	public static JComboBox<Object> homeList, awayList;
	public static JSpinner it;
	public static JTextArea out;
	public static JButton destruir;
	private static Neural builtAnn;
	
	public static void outLn(String str){
		out.setText(out.getText()+"\n"+str);
	}
	
	public ConsolaAvancada(){
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent arg0) {}
			@Override
			public void windowIconified(WindowEvent arg0) {}
			@Override
			public void windowDeiconified(WindowEvent arg0) {}
			@Override
			public void windowDeactivated(WindowEvent arg0) {}
			@Override
			public void windowClosing(WindowEvent arg0) {}
			@Override
			public void windowClosed(WindowEvent arg0) {
				FrameApostas.consolaFechada();
			}
			@Override
			public void windowActivated(WindowEvent arg0) {}
		});
		window.setSize(420, 500);
		window.setLocation((int)(Gui.scrSize.getWidth()/4)+440, (int)(Gui.scrSize.getHeight()/2)-250);
		
		Container cont = window.getContentPane();
		cont.setLayout(new BorderLayout());
		
		ArrayList<String> equipas = DatabaseConnection.getAllEquipas();
		equipas.add(0,"<Seleccionar>");
		homeList = new JComboBox<Object>(equipas.toArray());
		homeList.addActionListener(new HomeListAction());
		awayList = new JComboBox<Object>(equipas.toArray());
		awayList.setEnabled(false);
		
		JPanel top = new JPanel(new FlowLayout());
		JPanel mid = new JPanel(new FlowLayout());
		JPanel down = new JPanel(new FlowLayout());
		
		top.add(new JLabel("Casa:"));
		top.add(homeList);
		top.add(new JLabel("Convidada:"));
		top.add(awayList);

		out = new JTextArea();
		
		JScrollPane scroll = new JScrollPane(out);
	    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setPreferredSize(new Dimension(390,310));
		out.setEditable(false);
		((DefaultCaret)out.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		//out.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		mid.add(scroll);
		
		
		SpinnerNumberModel model1 = new SpinnerNumberModel(3.0, 1.0, 15.0, 1.0); 
		it = new JSpinner(model1);
		JButton calcular1 = new JButton("Factor de Confiança");
		JButton calcular2 = new JButton("Rede Neuronal");
		JButton calc3 = new JButton("Apostas Liga");
		destruir = new JButton("Destruir Rede Neuronal");
		calcular1.addActionListener(new ButtonConfianca(false));
		calc3.addActionListener(new ButtonApostas());
		calcular2.addActionListener(new ButtonRedeNeuronal());
		destruir.addActionListener(new ButtonDestruirRede());
		
		destruir.setEnabled(false);
		
		TitledBorder title = BorderFactory.createTitledBorder("Calcular");
		down.setBorder(title);
		down.add(new JLabel("C.Decimais:"));
		down.add(it);
		down.add(calcular1);
		down.add(calcular2);
		down.add(calc3);
		down.add(destruir);
		
		down.setPreferredSize(new Dimension(395,100));
		mid.add(down);
		
		cont.add(top, BorderLayout.NORTH);
		cont.add(mid, BorderLayout.CENTER);
		//cont.add(down, BorderLayout.SOUTH);
		window.setResizable(false);
		
		

		out.setText("A criar cache...");
		double time1 = System.currentTimeMillis();
		DatabaseConnection.populateConfiancaCache(false);
		double time2 = System.currentTimeMillis();
		outLn("Cache criada! ("+(time2-time1)+"ms)");
	}
	
	public void init(){
		window.setVisible(true);
	}
	
	static class HomeListAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(homeList.getSelectedIndex()!=0){
				awayList.removeAllItems();
				for(String away : DatabaseConnection.getExistingRivals(""+homeList.getSelectedItem())){
					awayList.addItem(away);
				}
				awayList.setEnabled(true);
			}
		}
	}
	
	static class ButtonApostas implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			outLn("A carregar base de dados para apostas");
			List<String[]> items = DatabaseConnection.getItemsToAdd();
			List<String[]> equipas = DatabaseConnection.getEquipasList();
			List<String[]> sites = DatabaseConnection.getSites();
			List<String[]> neural = DatabaseConnection.getNeuralScores();
			List<String[]> resultados = DatabaseConnection.getResultados();
			
			JTextField oddField = new JTextField("20.00");
			JLabel label = new JLabel("(Apostar €):");
			
			String[] casas = new String[sites.size()+1];
			casas[0] = "Todas";
			for(int i=1;i<casas.length;i++){
				casas[i] = sites.get(i-1)[1];
			}
			
			JComboBox<Object> boxSites = new JComboBox<Object>(casas);
			boxSites.setSelectedIndex(0);
			JPanel myPanel = new JPanel();
			myPanel.add(boxSites);
			myPanel.add(label);
			myPanel.add(oddField);
			
			double init = 20.00;
			
			int result = JOptionPane.showConfirmDialog(null, myPanel, 
						"Apostar quanto?", JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				init = Double.parseDouble(oddField.getText());
			} else {
				return;
			}
			
			double actual = init, perc = 0.35, valorTotalFinal = 0.0, melhorCarteira = 0.0, piorCarteira = 100000000.0, pico=0.0;
			String melhorSite = "", piorSite = "";
			outLn("Valor inicial: "+init+"€ Percentagem a apostar: "+(perc*100)+"%");
			int acertosTotal=0, falhasTotal=0;
			if(boxSites.getSelectedIndex()==0){
				for(String[] linhaSite : sites){
					int acertos=0,falhas=0;
					actual = init;
					int siteLP = Integer.parseInt(linhaSite[2]);
					outLn("A iniciar simulação de apostas para o site "+linhaSite[1]);
					int curItem = 0;
					for(String[] linhaItem : items){
						
						double valorAp = actual * perc;
						actual -= valorAp;
						
						String homeSig = linhaItem[0], awaySig = linhaItem[1];
						String home = "", away = "";
						for(String[] linhaEquipa : equipas){
							if(linhaEquipa[1].equals(homeSig))
								home = linhaEquipa[0];
							if(linhaEquipa[1].equals(awaySig))
								away = linhaEquipa[0];
						}
						
						double[] odds = new double[3];
						int[] pesos = new int[3];
						if(linhaItem[siteLP].isEmpty() || linhaItem[siteLP+1].isEmpty() || linhaItem[siteLP+2].isEmpty()){
							curItem++;
							actual += valorAp;
							outLn("Falha nos dados.");
							continue;
						}
						odds[0] = Double.parseDouble(linhaItem[siteLP]);
						odds[1] = Double.parseDouble(linhaItem[siteLP+1]);
						odds[2] = Double.parseDouble(linhaItem[siteLP+2]);
	
						int sel = 0;
						double afinidade = FrameApostas.getAfinidade();
						
						for(int i=0;i<3;i++){
							pesos[i] = (int) (odds[i] * 100.0);
							if(i==(Integer.parseInt(neural.get(curItem)[0])*-1+1)){
								pesos[i] = (int) ((double)pesos[i] * Util.calcPesoWin(afinidade));
							} else {
								pesos[i] = (int) ((double)pesos[i] * Util.calcPesoLose(afinidade));
							}
							double[] fconf = DatabaseConnection.getConfiancaBase(home, away);
							double[] fconf2 = DatabaseConnection.getConfiancaRel(home, away);
							double[] fconfm = {(fconf[0]+fconf2[0]) / 2, (fconf[1]+fconf2[1])/2,(fconf[2]+fconf2[2]) / 2};
							pesos[i] = (int) ((double)pesos[i] / fconfm[i]);
							
							double racioi = odds[i] / ((double)pesos[i]);
							double racioMax = odds[sel] / ((double)pesos[sel]);
							if(racioi > racioMax){
								sel = i;
							}
						}
						
						int res = Integer.parseInt(resultados.get(curItem)[2]);
						
						if(res == sel){
							acertos++;
							actual += (valorAp * odds[sel]);
							if(actual > pico){
								pico = actual;
							}
							outLn(homeSig+"-"+awaySig+": Acertou "+sel+" ("+resultados.get(curItem)[0]+"-"+resultados.get(curItem)[1]+") Carteira: "+Util.roundTo(actual,2)+"€");
						} else {
							falhas++;
							outLn(homeSig+"-"+awaySig+": Falhou "+sel+" ("+resultados.get(curItem)[0]+"-"+resultados.get(curItem)[1]+") Carteira: "+Util.roundTo(actual,2)+"€");
						}
						
						curItem++;
					}
					if(actual > melhorCarteira){
						melhorCarteira = actual;
						melhorSite = linhaSite[1];
					}
					if(actual < piorCarteira){
						piorCarteira = actual;
						piorSite = linhaSite[1];
					}
					valorTotalFinal += actual;
					falhasTotal += falhas;
					acertosTotal += acertos;
					outLn("Resultado para "+linhaSite[1]+": "+acertos+"/"+(acertos+falhas)+" ("+Util.roundTo(((double)acertos/((double)acertos+(double)falhas))*100,2)+"%)");
				}
			} else {
				int acertos=0,falhas=0;
				actual = init;
				String[] linhaSite = sites.get(boxSites.getSelectedIndex()-1);
				int siteLP = Integer.parseInt(linhaSite[2]);
				outLn("A iniciar simulação de apostas para o site "+linhaSite[1]);
				int curItem = 0;
				for(String[] linhaItem : items){
					
					double valorAp = actual * perc;
					actual -= valorAp;
					
					String homeSig = linhaItem[0], awaySig = linhaItem[1];
					String home = "", away = "";
					for(String[] linhaEquipa : equipas){
						if(linhaEquipa[1].equals(homeSig))
							home = linhaEquipa[0];
						if(linhaEquipa[1].equals(awaySig))
							away = linhaEquipa[0];
					}
					
					double[] odds = new double[3];
					int[] pesos = new int[3];
					if(linhaItem[siteLP].isEmpty() || linhaItem[siteLP+1].isEmpty() || linhaItem[siteLP+2].isEmpty()){
						curItem++;
						actual += valorAp;
						outLn("Falha nos dados.");
						continue;
					}
					odds[0] = Double.parseDouble(linhaItem[siteLP]);
					odds[1] = Double.parseDouble(linhaItem[siteLP+1]);
					odds[2] = Double.parseDouble(linhaItem[siteLP+2]);

					int sel = 0;
					double afinidade = FrameApostas.getAfinidade();
					
					for(int i=0;i<3;i++){
						pesos[i] = (int) (odds[i] * 100.0);
						if(i==(Integer.parseInt(neural.get(curItem)[0])*-1+1)){
							pesos[i] = (int) ((double)pesos[i] * Util.calcPesoWin(afinidade));
						} else {
							pesos[i] = (int) ((double)pesos[i] * Util.calcPesoLose(afinidade));
						}
						double[] fconf = DatabaseConnection.getConfiancaBase(home, away);
						double[] fconf2 = DatabaseConnection.getConfiancaRel(home, away);
						double[] fconfm = {(fconf[0]+fconf2[0]) / 2, (fconf[1]+fconf2[1])/2,(fconf[2]+fconf2[2]) / 2};
						pesos[i] = (int) ((double)pesos[i] / fconfm[i]);
						
						double racioi = odds[i] / ((double)pesos[i]);
						double racioMax = odds[sel] / ((double)pesos[sel]);
						if(racioi > racioMax){
							sel = i;
						}
					}
					
					int res = Integer.parseInt(resultados.get(curItem)[2]);
					
					if(res == sel){
						acertos++;
						actual += (valorAp * odds[sel]);
						if(actual > pico){
							pico = actual;
						}
						outLn(homeSig+"-"+awaySig+": Acertou "+sel+" ("+resultados.get(curItem)[0]+"-"+resultados.get(curItem)[1]+") Carteira: "+Util.roundTo(actual,2)+"€");
					} else {
						falhas++;
						outLn(homeSig+"-"+awaySig+": Falhou "+sel+" ("+resultados.get(curItem)[0]+"-"+resultados.get(curItem)[1]+") Carteira: "+Util.roundTo(actual,2)+"€");
					}
					
					curItem++;
				}
				if(actual > melhorCarteira){
					melhorCarteira = actual;
					melhorSite = linhaSite[1];
				}
				if(actual < piorCarteira){
					piorCarteira = actual;
					piorSite = linhaSite[1];
				}
				valorTotalFinal += actual;
				falhasTotal += falhas;
				acertosTotal += acertos;
				outLn("Resultado para "+linhaSite[1]+": "+acertos+"/"+(acertos+falhas)+" ("+Util.roundTo(((double)acertos/((double)acertos+(double)falhas))*100,2)+"%)");
			
			}
			outLn("Resultado final: "+acertosTotal+"/"+(acertosTotal+falhasTotal)+" ("+Util.roundTo(((double)acertosTotal/((double)acertosTotal+(double)falhasTotal))*100,2)+"%)");
			outLn(Util.roundTo(valorTotalFinal,2)+"€ nas carteiras.");
			outLn("Melhor carteira: "+melhorSite+" com "+Util.roundTo(melhorCarteira,2)+"€");
			outLn("Pior carteira: "+piorSite+" com "+Util.roundTo(piorCarteira,2)+"€");
			outLn("Pico: "+Util.roundTo(pico,2)+"€");
		}
	}
	
	static class ButtonDestruirRede implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			builtAnn.clear();
			builtAnn = null;
			destruir.setEnabled(false);
		}
	}
	
	static class ButtonConfianca implements ActionListener {
		private boolean base;
		public ButtonConfianca(boolean b){
			base = b;
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			String home = ""+homeList.getSelectedItem();
			String away = ""+awayList.getSelectedItem();
			if(home.equals(away)){
				outLn("Equipas escolhidas têm de ser diferentes!");
				return;
			}
			int round = ((Double)it.getModel().getValue()).intValue();
			double[] conf;
			if(base){
				conf = DatabaseConnection.getConfiancaBase(home, away);
			} else {
				conf = DatabaseConnection.getConfiancaRel(home, away);
			}
			outLn(home.toUpperCase()+" tem um factor de confiança de");
			outLn("\t"+Util.roundTo(conf[0],round)+" que vai ganhar,");
			outLn("\t"+Util.roundTo(conf[1],round)+" que vai empatar e");
			outLn("\t"+Util.roundTo(conf[2],round)+" que vai perder contra o "+away.toUpperCase());
			
			outLn("Podemos concluir que há maior chance de");
			if(conf[0] > conf[1] && conf[0] > conf[2])
				outLn(home.toUpperCase()+" ganhar ao "+away.toUpperCase());
			if(conf[1] > conf[0] && conf[1] > conf[2])
				outLn(home.toUpperCase()+" empatar com "+away.toUpperCase());
			if(conf[2] > conf[1] && conf[2] > conf[0])
				outLn(home.toUpperCase()+" perder com "+away.toUpperCase());
		}
	}
	
	static class ButtonRedeNeuronal implements ActionListener {
		Timer timer;
		@Override
		public void actionPerformed(ActionEvent arg0) {
			Runnable run = new Runnable(){
				@Override
				public void run() {
					neuralType();
				}
			};
			Thread thread = new Thread(run);
			thread.start();
		}
	}
	
	public static void neuralType(){
		JCheckBox demo = new JCheckBox("Modo Demonstração? (Avançado!)");
		int result = JOptionPane.showConfirmDialog(null, demo, 
				"ANN Type", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			neuralNetwork(demo.isSelected());
		} else {
			return;
		}
	}
	
	private static String queryNewInstance() {
		JTextField oddField = new JTextField("-1.00, 3.60, 3.30, 2.05, 3.30, 3.20, 2.15, 3.20, 3.10, 2.10, 3.60, 3.00, 2.05, 3.93, 3.14, 2.19, 3.40, 3.00, 2.15, 3.50, 3.20, 2.05, 3.80, 3.30, 2.10");
		JLabel label = new JLabel("ODDS (EXPECTED, [ODDS]):");
		JPanel myPanel = new JPanel();
		myPanel.add(label);
		myPanel.add(oddField);
		int result = JOptionPane.showConfirmDialog(null, myPanel, 
					"ODDS PARA TESTAR", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			return oddField.getText();
		} else {
			return queryNewInstance();
		}
	}

	public static String configureNeural(){
		JTextField lField = new JTextField("0.3");
		JTextField mField = new JTextField("0.2");
		JTextField nField = new JTextField("200");
		JTextField vField = new JTextField("0");
		JTextField sField = new JTextField("971"); //escolheu-se 971 com o nosso SeedTester
		JTextField eField = new JTextField("20");
		JTextField hField = new JTextField("3,6,4");

		JPanel myPanel = new JPanel();
		myPanel.add(new JLabel("Learning:"));
		myPanel.add(lField);
		myPanel.add(new JLabel("Momentum:"));
		myPanel.add(mField);
		myPanel.add(new JLabel("Iteraçoes:"));
		myPanel.add(nField);
		myPanel.add(new JLabel("Validation:"));
		myPanel.add(vField);
		myPanel.add(new JLabel("Seed:"));
		myPanel.add(sField);
		myPanel.add(new JLabel("TCE:"));
		myPanel.add(eField);
		myPanel.add(new JLabel("HLayers:"));
		myPanel.add(hField);

		int result = JOptionPane.showConfirmDialog(null, myPanel, 
				"Configuração Rede Neuronal", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			return "-L "+lField.getText()+" -M "+mField.getText()+" -N "+nField.getText()+" -V "+vField.getText()+
					" -S "+sField.getText()+" -E "+eField.getText()+" -H "+hField.getText();
		}
		return "-L 0.3 -M 0.2 -N 200 -V 0 -S 971 -E 20 -H 3,6,4"; //default values
	}
	
	public static void neuralNetwork(boolean demo){
		Neural ann=null;
		if(builtAnn==null){
			ann = new Neural("neuralknap",false);
			
			outLn("A carregar base de dados.");
			ann.loadData();
			outLn("Dados carregados..");
			
			ann.configure(configureNeural());
			outLn("Rede Neuronal Configurada.");
			outLn("A Construir Rede");
			ann.buildPerceptron();
			outLn("Rede Construida");
			
			builtAnn = ann;
			destruir.setEnabled(true);
		} else {
			ann = builtAnn;
		}

		if(!demo){
			String inst = queryNewInstance();
//			ann.setTestingEnv(inst);
			double prev = ann.testForOdds(inst);
			
			outLn("Resultado (Previsão: "+Util.roundTo(prev,3)+")");
			if(prev > 0.18){
				//home win
				outLn("Casa Ganha");
			} else if(prev < -0.18){
				//away win
				outLn("Equipa Convidada Ganha");
			} else {
				//draw
				outLn("Empate");
			}
		} else {
			ann.getTestSetValues();
			
			int numTestingInstances = ann.getNumTestingInstances();
			double[] classLabels = ann.getClassLabels();
			double[] predictedLabels = ann.getPredictedLabels();
			outLn("");

			outLn("Resultados");
			outLn("Actual\tPrevisto\tThreshold\tAcertou?");
			int certos = 0;
			for(int i=0; i<numTestingInstances;i++){
				double threshold=0.0;
				if(predictedLabels[i] > 0.18)
					threshold = 1.0;
				if(predictedLabels[i] < -0.18)
					threshold = -1.0;
				if(threshold==classLabels[i])
					certos++;
				outLn(classLabels[i]+"\t"+Util.roundTo(predictedLabels[i],3)+"\t"+threshold+"\t"+(threshold==classLabels[i]));
			}
			double perc = Util.roundTo(((double)certos)/((double)numTestingInstances) * 100, 2);
			outLn("Certos: "+certos+" em "+numTestingInstances+" ("+(perc)+"%)");
		}
//		ann.clear();
	}
}

package main.java.tsi.gui;

import main.java.tsi.tools.DatabaseConnection;
import main.java.tsi.tools.Util;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

public class AdicionarAposta extends JFrame {
	
	private static final long serialVersionUID = 8666597381871824963L;
	
	public static JComboBox<Object> homeList, awayList;
	private JFrame me;
	
	public AdicionarAposta() {
        System.out.println("A adicionar aposta... weka");
        me = this;
		setTitle("Adicionar Aposta");
		setSize(380,70);
		setLocation((int)(Gui.scrSize.getWidth()/4)+440, (int)(Gui.scrSize.getHeight()/2)-200);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		
		addWindowListener(new WindowListener() {
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
				FrameApostas.apostaManualFechada();
			}
			@Override
			public void windowActivated(WindowEvent arg0) {}
		});
		
		List<String[]> equipas = DatabaseConnection.getEquipasList();
		ArrayList<String> eCasa=new ArrayList<String>(),eConv=new ArrayList<String>();
		eCasa.add("< Casa >");
		eConv.add("< Convidada >");
		for(String[] linha : equipas){
			eCasa.add(linha[0]);
			eConv.add(linha[0]);
		}
		
		homeList = new JComboBox<Object>(eCasa.toArray());
		homeList.setBounds(10, 11, 130, 20);
		getContentPane().add(homeList);
		
		awayList = new JComboBox<Object>(eConv.toArray());
		awayList.setBounds(150, 11, 130, 20);
		getContentPane().add(awayList);
		
		JButton btnCalcular = new JButton("Adicionar");
		btnCalcular.setBounds(290, 10, 77, 23);
		btnCalcular.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String home = ""+homeList.getSelectedItem();
				String away = ""+awayList.getSelectedItem();
				if(home.equals(away)){
					JOptionPane.showMessageDialog(null, "Equipas escolhidas têm de ser diferentes!");
					return;
				}
				
				String homeSig="",awaySig="";
				
				List<String[]> equipas = DatabaseConnection.getEquipasList();
				List<String[]> items = DatabaseConnection.getItemsToAdd();
				List<String[]> sites = DatabaseConnection.getSites();
				List<String[]> neuralScores = DatabaseConnection.getNeuralScores();
				
				for(String[] linha : equipas){
					if(linha[0].equals(home))
						homeSig=linha[1];
					if(linha[0].equals(away))
						awaySig=linha[1];
				}
				
				
				int line = 0;
				String casaAp = FrameApostas.getCasaApostas();
				int siteInd = 0;
				for(String[] linha : sites){
					if(linha[1].equals(casaAp)){
						siteInd = Integer.parseInt(linha[2]);
					}
				}
				double[] odds = new double[3];
				int[] pesos = new int[3];
				for(int i = 0; i < items.size(); i++){
					String[] linha = items.get(i);
					if(homeSig.equals(linha[0]) && awaySig.equals(linha[1])){
						line = i;
						odds[0] = Double.parseDouble(linha[siteInd]);
						odds[1] = Double.parseDouble(linha[siteInd+1]);
						odds[2] = Double.parseDouble(linha[siteInd+2]);
					}
				}
				int sel = 0;
				double afinidade = FrameApostas.getAfinidade();
				
				for(int i=0;i<3;i++){
					pesos[i] = (int) (odds[i] * 100.0);
					if(i==(Integer.parseInt(neuralScores.get(line)[0])*-1+1)){
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
				
				FrameApostas.novaAposta(sel*240 + line, homeSig, awaySig, odds[sel], sel);;
				
				me.dispose();
			}
		});
		getContentPane().add(btnCalcular);
	}
	
	public void init(){
		setVisible(true);
	}
	
}

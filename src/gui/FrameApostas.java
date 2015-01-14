package gui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JFormattedTextField;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import knapsack.KnapsackTester;



import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import tools.DatabaseConnection;
import tools.Util;

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

public class FrameApostas extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5808584124366879216L;
	private JPanel contentPane, statusBarPanel;
	protected KnapsackTester test;
	private JButton btnKnapsack;
	public static JPanel listaResultados;
	public static JLabel valorMultiplas;
	public static JLabel valorLucro;
	public static JTextField valorAposta;
	public static JComboBox<String> casaApostas;
	private String[] casas;
	private JLabel label;
	
	private static int alpha=100,mu=25,lambda=25,iterations=5000, optimizador=2, idAposta = 0;
	private static double crossover=0.95, capacidade=15.0, afinidade=1.7, valorApostaTemp=20.0;
	
	public static JMenuItem mntmApostaManual = new JMenuItem("Adicionar Aposta"),
			mntmConsolaTestes = new JMenuItem("Consola de Testes"),
			mntmConfigKnap = new JMenuItem("Configuração Avançada"),
			mntmLimparApostas = new JMenuItem("Limpar Todas as Apostas");
	
	private static ConsolaAvancada consola;
	private static FrameKnapConfig knapConfig;
	private static AdicionarAposta apostaManual;
	
	private static ArrayList<Double> oddsEscolhidas = new ArrayList<Double>();
	private JPopupMenu popupMenu;

	public FrameApostas(ConsolaAvancada ca) {
		consola = ca;
		knapConfig = new FrameKnapConfig();
		apostaManual = new AdicionarAposta();
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		popupMenu = new JPopupMenu();
		popupMenu.setSize(91, 60);
		popupMenu.setLocation(590, 24);
		
		JButton butt = new JButton("");
		butt.setIcon(new ImageIcon("img/gear.png"));
		butt.setBounds(590,24,26,26);
		butt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				popupMenu.show(contentPane, 616, 24);
			}
		});
		
		
		
		mntmApostaManual.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				apostaManual.init();
				mntmApostaManual.setEnabled(false);
			}
		});
		mntmConsolaTestes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				consola.init();
				mntmConsolaTestes.setEnabled(false);
			}
		});
		mntmConfigKnap.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				knapConfig.setValues(alpha,mu,lambda,iterations, optimizador,crossover,capacidade,afinidade);
				knapConfig.init();
				mntmConfigKnap.setEnabled(false);
			}
		});
		mntmLimparApostas.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				limparApostas();
			}
		});
		popupMenu.add(mntmApostaManual);
		popupMenu.add(mntmConsolaTestes);
		popupMenu.add(mntmConfigKnap);
		popupMenu.add(mntmLimparApostas);
		
		contentPane.add(butt);
		
		
		

		statusBarPanel = new JPanel(new BorderLayout());

		statusBarPanel.setBounds(20, 430, 596, 21);
		contentPane.add(statusBarPanel);

		JLabel lblCasaDeApostas = new JLabel("Casa de apostas:");
		lblCasaDeApostas.setBounds(20, 11, 84, 14);
		contentPane.add(lblCasaDeApostas);
		
		List<String[]> sites = DatabaseConnection.getSites();
		casas = new String[sites.size()];
		
		for(int i=0;i<casas.length;i++){
			casas[i] = sites.get(i)[1];
		}
		
		casaApostas = new JComboBox<String>(casas);
		casaApostas.setBounds(20, 30, 186, 20);
		contentPane.add(casaApostas);

		JLabel lblValorAApostar = new JLabel("Valor a apostar:");
		lblValorAApostar.setHorizontalAlignment(SwingConstants.RIGHT);
		lblValorAApostar.setBounds(233, 11, 91, 14);
		contentPane.add(lblValorAApostar);
		
//		MaskFormatter form=null;
//		try{
//			form = new MaskFormatter("######.##");
//			form.setValidCharacters("0123456789");
//		} catch(ParseException e){
//			e.printStackTrace();
//		}

		valorAposta = new JTextField();
		valorAposta.setHorizontalAlignment(JFormattedTextField.RIGHT);
		valorAposta.setText("20.00");
		valorAposta.setBounds(253, 30, 71, 21);
		valorAposta.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					double valor = Double.parseDouble(valorAposta.getText());
					for(Component pac : listaResultados.getComponents()){
						PainelAposta pa = (PainelAposta) pac;
						pa.actualizarValor(valor);
					}
					valorApostaTemp = valor;
					actualizarOdds();
				} catch (Exception nme){
					valorAposta.setText(""+valorApostaTemp);
				}
			}
		});
		
		contentPane.add(valorAposta);
		
		JLabel lblResultados = new JLabel("Resultados:");
		lblResultados.setBounds(20, 67, 57, 14);
		JLabel lblOdds = new JLabel("Odd:");
		lblOdds.setBounds(290, 67, 35, 14);
		JLabel lblTipo = new JLabel("Apostar em:");
		lblTipo.setBounds(340, 67, 60, 14);
		JLabel lblLucInf = new JLabel("Lucro Individual:");
		lblLucInf.setBounds(470, 67, 150, 14);
		
		contentPane.add(lblResultados);
		contentPane.add(lblOdds);
		contentPane.add(lblTipo);
		contentPane.add(lblLucInf);

		listaResultados = new JPanel();
		listaResultados.setBackground(Color.WHITE);
		listaResultados.setBounds(20, 158, 596, 219);

		listaResultados.setLayout(new BoxLayout(listaResultados, BoxLayout.Y_AXIS));
		
		JScrollPane scroll = new JScrollPane(listaResultados);
	    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	    scroll.setBounds(20, 86, 596, 308);
		
	    
	    
		contentPane.add(scroll);
		
		JLabel lblNDeMltiplas = new JLabel("N\u00BA de m\u00FAltiplas:");
		lblNDeMltiplas.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNDeMltiplas.setBounds(20, 405, 75, 14);
		contentPane.add(lblNDeMltiplas);

		valorMultiplas = new JLabel("0");
		valorMultiplas.setHorizontalAlignment(SwingConstants.LEFT);
		valorMultiplas.setBounds(105, 405, 46, 14);
		contentPane.add(valorMultiplas);

		JLabel lblLucroDaAposta = new JLabel("Lucro da aposta m\u00FAltipla:");
		lblLucroDaAposta.setBounds(430, 405, 120, 14);
		contentPane.add(lblLucroDaAposta);

		valorLucro = new JLabel("0.00€");
		valorLucro.setHorizontalAlignment(JLabel.RIGHT);
		valorLucro.setBounds(555, 405, 60, 14);
		contentPane.add(valorLucro);

		btnKnapsack = new JButton("Calcular Apostas");
		btnKnapsack.setBounds(460, 24, 121, 26);
		btnKnapsack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String casaAp = (String)casaApostas.getSelectedItem();
				double valAp=valorApostaTemp;
				try{
					valAp = Double.parseDouble(valorAposta.getText());
					valorApostaTemp = valAp;
				} catch (Exception nme){
					valorAposta.setText(""+valorApostaTemp);
				}
				
				if(valAp<=0){
					JOptionPane.showMessageDialog(null, "Valor de aposta introduzido tem de ser positivo");
				} else {
					
					//limparApostas();
					
					test = new KnapsackTester(casaAp, optimizador, capacidade, afinidade, valAp, crossover, mu, alpha, lambda, iterations);
					Runnable run = new Runnable(){
						@Override
						public void run() {
							btnKnapsack.setEnabled(false);
							test.run();
							btnKnapsack.setEnabled(true);
						}
					};
					new Thread(run).start();
				}
			}
		});
		contentPane.add(btnKnapsack);
		
		label = new JLabel("\u20AC"); //€
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setBounds(327, 34, 6, 14);
		contentPane.add(label);
	}
	
	public static void limparApostas(){
		listaResultados.removeAll();
		listaResultados.revalidate();
		listaResultados.repaint();
		oddsEscolhidas.clear();
		valorMultiplas.setText("0");
		valorLucro.setText("0.00€");
	}

	public void start() {
		setTitle("Projecto TSI - Sistema Inteligente de Apostas");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setSize(640, 500);
		setLocation((int)(Gui.scrSize.getWidth()/4)-210, (int)(Gui.scrSize.getHeight()/2)-250);
		setVisible(true);
	}
	
	public static double getAfinidade(){
		return afinidade;
	}
	
	public static String getCasaApostas(){
		return (String)casaApostas.getSelectedItem();
	}

	public JPanel getStatusBarPanel() {
		return statusBarPanel;
	}

	public static void novaAposta(int itemId, String home, String away, double odd, int tipo) {
		listaResultados.add(new PainelAposta(idAposta, itemId, home, away, (String)casaApostas.getSelectedItem(), odd, tipo));
		actualizarOdds();
		idAposta++;
		listaResultados.revalidate();
	}

	private static void actualizarOdds() {
		oddsEscolhidas.clear();
		
		Component[] cps = listaResultados.getComponents();
		for(int i = 0; i < cps.length; i++){
			PainelAposta pa = (PainelAposta) cps[i];
			oddsEscolhidas.add(pa.getOdd());
		}
		valorMultiplas.setText(""+oddsEscolhidas.size());
		if(oddsEscolhidas.size()>0){
			double mult = oddsEscolhidas.get(0);
			for(int i=1;i<oddsEscolhidas.size();i++){
				mult *= oddsEscolhidas.get(i);
			}
			double val = Double.parseDouble((String)valorAposta.getText());
			mult *= val;
			mult = mult - val;
			String multStr = ""+Util.roundTo(mult, 2);
			if(multStr.split("\\.")[1].length()==1)
				multStr+="0";
			multStr+="€";
			valorLucro.setText(multStr);
		} else {
			valorLucro.setText("0.00€");
		}
		valorMultiplas.revalidate();
		valorLucro.revalidate();
	}

	public static void consolaFechada() {
		mntmConsolaTestes.setEnabled(true);
	}
	
	public static void configFechada() {
		mntmConfigKnap.setEnabled(true);
	}
	
	public static void apostaManualFechada() {
		mntmApostaManual.setEnabled(true);
	}
	
	public static void setValues(int a,int m,int l,int i,int o,double c,double ca,double af){
		alpha = a;
		mu = m;
		lambda = l;
		iterations = i;
		optimizador = o;
		crossover = c;
		capacidade = ca;
		afinidade = af;
	}

	public static void deleteAposta(int id) {
		Component[] cps = listaResultados.getComponents();
		for(int i = 0; i < cps.length; i++){
			PainelAposta pa = (PainelAposta) cps[i];
			if(pa.getId()==id){
				listaResultados.remove(pa);
				listaResultados.revalidate();
				listaResultados.repaint();
				actualizarOdds();
				return;
			}
		}
	}
}

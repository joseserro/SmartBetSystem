package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.JSpinner;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class FrameKnapConfig extends JFrame {
	private static final long serialVersionUID = 3808484122366879216L;
	private static JLabel lblAfini,lblCapa,lblCruz;
	private static JSlider sliderAfini,sliderCapa,sliderCruz;
	private static JSpinner gera,ascendentes,popu,lambdaSp;
	private static JComboBox<String> optimizacao;
	private static FrameKnapConfig me;
	public FrameKnapConfig(){
		me = this;
		setTitle("Configuração Knapsack");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		setSize(380, 310);
		setLocation((int)(Gui.scrSize.getWidth()/4)+440, (int)(Gui.scrSize.getHeight()/2)-200);
		
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
				FrameApostas.configFechada();
			}
			@Override
			public void windowActivated(WindowEvent arg0) {}
		});
		
		getContentPane().setLayout(null);
		
		String[] opti = {"Rede Neuronal","Factor de Confiança","Híbrido"};
		optimizacao = new JComboBox<String>(opti);
		optimizacao.setSelectedIndex(2);
		optimizacao.setBounds(18, 38, 186, 20);
		getContentPane().add(optimizacao);
		
		JLabel lblOptimizacao = new JLabel("Método Optimização");
		lblOptimizacao.setBounds(18, 19, 114, 14);
		getContentPane().add(lblOptimizacao);
		
		JLabel lblAfinidadeDosItens = new JLabel("Afinidade dos itens");
		lblAfinidadeDosItens.setBounds(19, 73, 95, 14);
		getContentPane().add(lblAfinidadeDosItens);
		
		sliderAfini = new JSlider(SwingConstants.HORIZONTAL, 10, 100, 19);
		sliderAfini.setBounds(19, 93, 156, 23);
		sliderAfini.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				lblAfini.setText(""+((double)sliderAfini.getValue() / 10.0));
			}
		});
		getContentPane().add(sliderAfini);
		
		lblAfini = new JLabel("1.9");
		lblAfini.setBounds(185, 96, 26, 14);
		getContentPane().add(lblAfini);
		
		JLabel lblCapacidadeDoSaco = new JLabel("Capacidade do saco");
		lblCapacidadeDoSaco.setBounds(19, 130, 100, 14);
		getContentPane().add(lblCapacidadeDoSaco);
		
		sliderCapa = new JSlider(SwingConstants.HORIZONTAL, 1, 1000, 150);
		sliderCapa.setBounds(19, 150, 157, 23);
		sliderCapa.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				lblCapa.setText(""+((double)sliderCapa.getValue() / 10.0));
			}
		});
		getContentPane().add(sliderCapa);
		
		lblCapa = new JLabel("15.0");
		lblCapa.setBounds(179, 153, 32, 14);
		getContentPane().add(lblCapa);
		
		JLabel lblGera = new JLabel("Iterações");
		lblGera.setBounds(303, 19, 46, 14);
		getContentPane().add(lblGera);
		
		gera = new JSpinner();
		gera.setBounds(267, 38, 82, 20);
		getContentPane().add(gera);
		
		JLabel lblAscendentes = new JLabel("# Ascendentes");
		lblAscendentes.setBounds(274, 79, 73, 14);
		getContentPane().add(lblAscendentes);
		
		ascendentes = new JSpinner();
		ascendentes.setBounds(267, 96, 82, 20);
		getContentPane().add(ascendentes);
		

		
		JLabel lblNivelDeCruzamento = new JLabel("Taxa de Cruzamento");
		lblNivelDeCruzamento.setBounds(19, 185, 100, 14);
		getContentPane().add(lblNivelDeCruzamento);
		
		sliderCruz = new JSlider(SwingConstants.HORIZONTAL, 1, 100, 95);
		sliderCruz.setBounds(19, 205, 157, 23);
		sliderCruz.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				lblCruz.setText(""+sliderCruz.getValue()+"%");
			}
		});
		getContentPane().add(sliderCruz);
		
		lblCruz = new JLabel("95%");
		lblCruz.setBounds(179, 208, 32, 14);
		getContentPane().add(lblCruz);
		
		JLabel lblAlpha = new JLabel("População");
		lblAlpha.setBounds(298, 134, 49, 14);
		getContentPane().add(lblAlpha);
		
		popu = new JSpinner();
		popu.setBounds(267, 153, 82, 20);
		getContentPane().add(popu);
		
		JLabel lblLambda = new JLabel("# Descendentes");
		lblLambda.setBounds(268, 189, 79, 14);
		getContentPane().add(lblLambda);
		
		lambdaSp = new JSpinner();
		lambdaSp.setBounds(267, 208, 82, 20);
		getContentPane().add(lambdaSp);
		
		
		
		JButton btnOk = new JButton("OK");
		btnOk.setBounds(185, 248, 72, 23);
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int alpha=(int)popu.getModel().getValue();
				int mu=(int)ascendentes.getModel().getValue();
				int lambda=(int)lambdaSp.getModel().getValue();
				int iterations=(int)gera.getModel().getValue();
				int  optimizador=optimizacao.getSelectedIndex();
				double crossover=(((double)sliderCruz.getValue()) / 100.0);
				double  capacidade=(((double)sliderCapa.getValue()) / 10.0);
				double  afinidade=(((double)sliderAfini.getValue()) / 10.0);
				FrameApostas.setValues(alpha, mu, lambda, iterations, optimizador, crossover, capacidade, afinidade);
				me.dispose();
			}
		});
		getContentPane().add(btnOk);
		
		JButton btnCancelar = new JButton("Cancelar");
		btnCancelar.setBounds(267, 248, 82, 23);
		btnCancelar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				me.dispose();
			}
		});
		getContentPane().add(btnCancelar);
		
	}
	
	public void init() {
		setVisible(true);
	}

	public void setValues(int a,int m,int l,int i,int o,double c,double ca,double af){
			popu.setValue(a);				//		alpha = a;
			ascendentes.setValue(m);				//		mu = m;
			lambdaSp.setValue(l);				//		lambda = l;
			gera.setValue(i);				//		iterations = i;
			optimizacao.setSelectedIndex(o);				//		optimizador = o;
			sliderCruz.setValue((int) (c * 100.0));				//		crossover = c;
			sliderCapa.setValue((int) (ca * 10.0));				//		capacidade = ca;
			sliderAfini.setValue((int) (af * 10.0));				//		afinidade = af;
	}
}

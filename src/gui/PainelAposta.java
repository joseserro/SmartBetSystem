package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import tools.DatabaseConnection;
import tools.Util;

public class PainelAposta extends JPanel {
	private static final long serialVersionUID = 5808562856366879216L;
	
	private static JFrame janelaDetalhes;
	
	private BufferedImage delImg;
	
	{
		try {
			delImg = ImageIO.read(new File("img/del.png"));
		} catch (IOException e) {
		}
	}
	public enum TipoAposta {
		CASA (0,"Casa Ganha"),
		EMPATE (1,"Empate"),
		CONVIDADA (2,"Convidada Ganha");
		private int i;
		private String tipo;
		TipoAposta(int i,String tipo){
			this.i=i;
			this.tipo=tipo;
		}
		public int getTipo(){
			return i;
		}
		@Override
		public String toString(){
			return tipo;
		}
	}
	
	private int id, idItem;
	private String home, away, site;
	private BufferedImage homeImg, awayImg;
	private double odd, valorAposta=20.0;
	private Color oddColor;
	private TipoAposta tipo;
	
	private PainelAposta me;
	
	public PainelAposta(int id, int idItem, String home, String away, String site, double odd, int tipo){
		super();
		me = this;
		super.setPreferredSize(new Dimension(576,36));
		super.setMaximumSize(new Dimension(576,36));
		super.addMouseListener(new ApostasMouseListener());
		this.id = id;
		this.site = site;
		this.idItem = idItem;
		this.odd = odd;
		this.tipo = TipoAposta.values()[tipo];
		
		double o = (Util.clamp(odd, 1, 4)-1) / 3.0;
		int r=0, g=0;
		if(0<=o && o<0.5){
		    g = 255;
		    r = (int)(510 * o);
		}
		if(0.5<=o && o<=1){
		    r = 255;
		    g = (int)(255 - 510 * (o-0.5));
		}
		oddColor = new Color(r,g,0);
		
		try {
			homeImg = ImageIO.read(new File("img/clubes/"+home+".png"));
			awayImg = ImageIO.read(new File("img/clubes/"+away+".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<String[]> equipas = DatabaseConnection.getEquipasList();
		for(String[] linha : equipas){
			if(linha[1].equals(home))
				this.home=linha[0];
			if(linha[1].equals(away))
				this.away=linha[0];
		}
	}
	
	public int getId(){
		return id;
	}
	
	public double getOdd(){
		return odd;
	}
	
	public int getIdItem(){
		return idItem;
	}
	
	@Override
	public void paintComponent (Graphics g) {
	    super.paintComponent(g);
	    
	    g.setColor(oddColor);
	    g.fillRect(0, 0, 575, 35);
	    
	    g.setColor(Color.BLACK);
	    g.drawRect(0, 0, 575, 35);
	    if (homeImg != null) {
	        g.drawImage(homeImg,2,2,this);
	    }
	    g.setFont(new Font("Arial",Font.PLAIN,13));
	    String str = home+" vs. "+away;
	    int dist = g.getFontMetrics().stringWidth(str);
	    g.drawString(str, 125-dist/2, 23);
	    if (awayImg != null) {
	        g.drawImage(awayImg,218,2,this);
	    }
	    String strOdd = ""+odd;
	    if(strOdd.length()==3)
	    	strOdd+="0";
	    g.drawString(strOdd, 270, 23);
	    g.drawString(tipo.toString(),320,23);
	    
	    String lucro = ""+Util.roundTo(valorAposta*odd - valorAposta,2);
	    if(lucro.split("\\.")[1].length()==1)
	    	lucro+="0";
	   	lucro+="€";
	    int distL = g.getFontMetrics().stringWidth(lucro);
	    g.drawString(lucro, 526-distL, 23);
	    
	    g.drawImage(delImg, 544, 6, this);
	 }
	
	class ApostasMouseListener implements MouseListener{
		@Override
		public void mouseClicked(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {
			oddColor=new Color(Util.clamp(oddColor.getRed()-40,0,255), Util.clamp(oddColor.getGreen()-40,0,255), 0);
			me.repaint();
		}
		@Override
		public void mouseExited(MouseEvent e) {
			oddColor=new Color(Util.clamp(oddColor.getRed()+40,0,255), Util.clamp(oddColor.getGreen()+40,0,255), 0);
			me.repaint();
		}
		@Override
		public void mousePressed(MouseEvent e) {
			int x = e.getX(), y = e.getY();
			if(x > 544 && x < 568 && y > 6 && y < 30){
				me.deleteAposta();
			} else {
				if(janelaDetalhes!=null){
					janelaDetalhes.setVisible(false);
					janelaDetalhes.dispose();
				}
				janelaDetalhes = new JFrame(home+" - "+away);
				janelaDetalhes.setLayout(null);
				janelaDetalhes.setSize(285,200);
				Point location = me.getLocationOnScreen();
				janelaDetalhes.setLocation(location.x+605, location.y);
				janelaDetalhes.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
				JPanel contentPane = new JPanel();
				contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
				janelaDetalhes.setContentPane(contentPane);
				contentPane.setLayout(null);
				
				JLabel idLbl = new JLabel("ID: "+id+"   -   IDAposta: "+idItem);
				idLbl.setBounds(10,11,140,14);
				contentPane.add(idLbl);
				
				
				List<String[]> neuralScores = DatabaseConnection.getNeuralScores();
				int cop=idItem;
				while(cop>=240){
					cop-=240;
				}
				int neur = (Integer.parseInt(neuralScores.get(cop)[0]));
				double prev = (Double.parseDouble(neuralScores.get(cop)[1]));
				String lblNeur = "";
				if(neur==-1){
					lblNeur = "Convidada Ganha ("+prev+")";
				} else if(neur==0){
					lblNeur = "Empate ("+prev+")";
				} else if(neur==1){
					lblNeur = "Casa Ganha ("+prev+")";
				}
				JLabel lblModoDeOptimizao = new JLabel("Rede Neuronal: "+lblNeur);
				lblModoDeOptimizao.setBounds(10, 137, 264, 14);
				contentPane.add(lblModoDeOptimizao);
				
				JLabel lblGanhar = new JLabel("Ganhar:");
				lblGanhar.setBounds(10, 62, 39, 14);
				contentPane.add(lblGanhar);
				
				JLabel lblOddPerder = new JLabel("Perder:");
				lblOddPerder.setBounds(10, 112, 36, 14);
				contentPane.add(lblOddPerder);
				
				JLabel lblOddEmpatar = new JLabel("Empatar:");
				lblOddEmpatar.setBounds(10, 87, 44, 14);
				contentPane.add(lblOddEmpatar);
				
				JLabel lblOdd = new JLabel("Odd:");
				lblOdd.setBounds(66, 42, 24, 14);
				contentPane.add(lblOdd);
				
				JLabel lblFactorConfiana = new JLabel("Factor confiança:");
				lblFactorConfiana.setBounds(162, 42, 84, 14);
				contentPane.add(lblFactorConfiana);
				
				List<String[]> sites = DatabaseConnection.getSites();
				
				int siteInd = 0;
				for(String[] linha : sites){
					if(linha[1].equals(site)){
						siteInd = Integer.parseInt(linha[2]);
					}
				}
				
				List<String[]> itemsToAdd = DatabaseConnection.getItemsToAdd();
				
				String win = itemsToAdd.get(cop)[siteInd];
				String draw = itemsToAdd.get(cop)[siteInd+1];
				String lose = itemsToAdd.get(cop)[siteInd+2];
				
				if(tipo==TipoAposta.CASA){
					win += " <-";
				} else if(tipo==TipoAposta.EMPATE){
					draw += " <-";
				} else {
					lose += " <-";
				}
				
				
				JLabel oddGanhar = new JLabel(win);
				oddGanhar.setHorizontalAlignment(SwingConstants.LEFT);
				oddGanhar.setBounds(65, 62, 60, 14);
				contentPane.add(oddGanhar);
				
				JLabel oddEmpatar = new JLabel(draw);
				oddEmpatar.setHorizontalAlignment(SwingConstants.LEFT);
				oddEmpatar.setBounds(65, 87, 60, 14);
				contentPane.add(oddEmpatar);
				
				JLabel oddPerder = new JLabel(lose);
				oddPerder.setHorizontalAlignment(SwingConstants.LEFT);
				oddPerder.setBounds(65, 112, 60, 14);
				contentPane.add(oddPerder);
				
				double[] fconf = DatabaseConnection.getConfiancaRel(home, away);
				
				JLabel confiancaPerder = new JLabel(""+Util.roundTo(fconf[2],3));
				confiancaPerder.setHorizontalAlignment(SwingConstants.CENTER);
				confiancaPerder.setBounds(170, 112, 60, 14);
				contentPane.add(confiancaPerder);
				
				JLabel confiancaEmpatar = new JLabel(""+Util.roundTo(fconf[1],3));
				confiancaEmpatar.setHorizontalAlignment(SwingConstants.CENTER);
				confiancaEmpatar.setBounds(170, 87, 60, 14);
				contentPane.add(confiancaEmpatar);

				
				JLabel confiancaGanhar = new JLabel(""+Util.roundTo(fconf[0],3));
				confiancaGanhar.setHorizontalAlignment(SwingConstants.CENTER);
				confiancaGanhar.setBounds(170, 62, 60, 14);
				contentPane.add(confiancaGanhar);
				
				janelaDetalhes.setVisible(true);
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {}
	}

	public void actualizarValor(double valor) {
		valorAposta = valor;
		me.repaint();
	}
	
	public void deleteAposta() {
		FrameApostas.deleteAposta(id);
	}
}

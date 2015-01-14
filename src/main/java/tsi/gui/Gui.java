package main.java.tsi.gui;

import main.java.tsi.viewer.StatusBarNovo;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

//import java.awt.Color;
//import java.awt.Point;
//import java.awt.image.BufferedImage;
//import java.io.File;
//
//import javax.imageio.ImageIO;
//import javax.sound.sampled.AudioInputStream;
//import javax.sound.sampled.AudioSystem;
//import javax.sound.sampled.Clip;

public class Gui {
	static final Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
	
	public static FrameApostas fa;
	public static ConsolaAvancada ca;
	
	public static void createGui(){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            ca = new ConsolaAvancada();
            fa = new FrameApostas(ca);
            fa.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void eh(){
		try{
		final JFrame botelho = new JFrame();
		botelho.setSize(198,204);
		botelho.setLocation((int)scrSize.getWidth(), ((int)scrSize.getHeight())-244);

		BufferedImage myPicture = ImageIO.read(new File(Gui.class.getResource("/main/resources/tsi/img/botelho.png").toURI()));
		JLabel picLabel = new JLabel(new ImageIcon(myPicture));

		botelho.add(picLabel);

		botelho.setUndecorated(true);
		botelho.setBackground(new Color(1.0f,1.0f,1.0f,0.0f));
		botelho.setVisible(true);

		Runnable run = new Runnable(){
			int move = 0;
			int moveBy = 1;
			@Override
			public void run() {
				Point loc = botelho.getLocation();
				while(true){
					try {
						botelho.setLocation(loc.x-move, loc.y);
						move+=moveBy;
						Thread.sleep(2);
						if(move > 400){
							moveBy = -1;
							AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File("ueh.wav").getAbsoluteFile());
							Clip clip = AudioSystem.getClip();
							clip.open(audioIn);
							clip.start();
							Thread.sleep(1000);
						}
						if(move < 0)
							break;
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				botelho.dispose();
			}
		};
		new Thread(run).start();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	

	public static void setStatusBar(StatusBarNovo statusBar) {
		fa.getStatusBarPanel().add(statusBar.getPanel(), BorderLayout.SOUTH);
		fa.revalidate();
	}
}

package main.java.tsi.gui;

import main.java.tsi.viewer.StatusBarNovo;

import javax.swing.*;
import java.awt.*;

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
	

	public static void setStatusBar(StatusBarNovo statusBar) {
		fa.getStatusBarPanel().add(statusBar.getPanel(), BorderLayout.SOUTH);
		fa.revalidate();
	}
}

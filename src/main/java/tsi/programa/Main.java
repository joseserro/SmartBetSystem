package main.java.tsi.programa;

import main.java.tsi.gui.Gui;
import main.java.tsi.tools.DatabaseConnection;

public class Main {	

	public static void main(String[] args) {
		DatabaseConnection.create();

		//Util.teste();

		Gui.createGui();

		//		double time1 = System.currentTimeMillis();
		//		computarConfianca(database);
		//		double time2 = System.currentTimeMillis();
		//		System.out.println("Tempo: "+(time2-time1));

		
		
//		System.out.println(KnapNoSacoProblem.calcPesoLose(1.8));
		
		//[Item325, Item187, Item650, Item516, Item419, Item488]

	}



}

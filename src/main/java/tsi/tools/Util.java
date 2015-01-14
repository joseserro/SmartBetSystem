package main.java.tsi.tools;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;


public class Util {
	public static double roundTo(double num, int casa){
		double sc = Math.pow(10.0,(double)casa);
		return (double)((Math.round((num*sc)))/sc);
	}
	
	public static int clamp(int val, int min, int max){
		if(val > max)
			return max;
		if(val < min)
			return min;
		return val;
	}
	
	public static void testeTempo() throws IOException{
		int ano = 2014, mes = 05, dia = 21;
		String local = "LPFR";
		URL oracle = new URL("http://www.wunderground.com/history/airport/"+local+"/"+ano+"/"+mes+"/"+dia+"/DailyHistory.html?format=1");
		//http://www.wunderground.com/history/airport/KSAN/2012/10/1/MonthlyHistory.html?format=1
        BufferedReader reader = new BufferedReader(new InputStreamReader(oracle.openStream()));
		CSVReader<String[]> csvParser = CSVReaderBuilder.newDefaultReader(reader);
		List<String[]> data = csvParser.readAll();
		
		for(String[] strs : data){
			for(String str : strs){
				System.out.print(str+";");
			}
			System.out.println();
		}
	}

	public static double clamp(double val, double min, double max) {
		if(val > max)
			return max;
		if(val < min)
			return min;
		return val;
	}

	public static double calcPesoWin(double agressividade){
		return (Math.log10(1.0/2.0 * agressividade + 0.1)/2.0) * -1.0 + 0.5;
	}

	public static double calcPesoLose(double agressividade){
		return Math.log10(0.3 * agressividade + 0.1) + 2.0;
	}
}

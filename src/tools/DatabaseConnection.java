package tools;
import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;

import java.io.*;
import java.net.URL;
import java.util.*;


public class DatabaseConnection {
	
	public static String[] anos;
	private static ArrayList<List<String[]>> db = new ArrayList<List<String[]>>();
	private static HashMap<String,HashMap<String,double[]>> confiancas = new HashMap<String,HashMap<String,double[]>>();
	//private static HashMap<String,HashMap<String,double[]>> confiancasGolos = new HashMap<String,HashMap<String,double[]>>();
	
	private static List<String[]> itemsToAdd, equipas, sites, neuralScores, resultados;
	
	private static Random rand = new Random();
	
	public static void create(){
		try {
			//URI uri = DatabaseConnection.class.getResource("/db").toURI();
			File folder = new File("db");
			File[] listOfFiles = folder.listFiles();
			anos = new String[listOfFiles.length];
			for (int i = 0; i < listOfFiles.length; i++) {
				System.out.println(listOfFiles[i].getName());
				anos[i] = listOfFiles[i].getName().substring(0, 4);
				//System.out.println(i+": "+listOfFiles[i].getName());
				URL urlToText = System.class.getResource("/db/"+listOfFiles[i].getName());
				InputStream is = urlToText.openStream();
				Reader reader = new InputStreamReader(is);
				//Reader reader = new FileReader("db/"+listOfFiles[i].getName());
				CSVReader<String[]> csvParser = CSVReaderBuilder.newDefaultReader(reader);
				List<String[]> data = csvParser.readAll();
				db.add(data);
			}
			
			
			itemsToAdd = CSVReaderBuilder.newDefaultReader(new InputStreamReader(DatabaseConnection.class.getResource("/knap/items.csv").openStream())).readAll();
			equipas = CSVReaderBuilder.newDefaultReader(new InputStreamReader(DatabaseConnection.class.getResource("/knap/equipas.csv").openStream())).readAll();
			sites = CSVReaderBuilder.newDefaultReader(new InputStreamReader(DatabaseConnection.class.getResource("/knap/sites.csv").openStream())).readAll();
			neuralScores = CSVReaderBuilder.newDefaultReader(new InputStreamReader(DatabaseConnection.class.getResource("/knap/neural.csv").openStream())).readAll();
			resultados = CSVReaderBuilder.newDefaultReader(new InputStreamReader(DatabaseConnection.class.getResource("/knap/resultados.csv").openStream())).readAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static List<String[]> getNeuralScores(){
		return neuralScores;
	}
	
	public static List<String[]> getItemsToAdd(){
		return itemsToAdd;
	}
	
	public static List<String[]> getEquipasList(){
		return equipas;
	}
	
	public static List<String[]> getSites(){
		return sites;
	}
	
	public static List<String[]> getResultados(){
		return resultados;
	}
	
	public static ArrayList<List<String[]>> getDatabase(){
		return db;
	}
	
	public static List<String[]> getDatabase(String years){
		switch(years){
		case "0001":
			return db.get(0);
		case "0102":
			return db.get(1);
		case "0203":
			return db.get(2);
		case "0304":
			return db.get(3);
		case "0405":
			return db.get(4);
		case "0506":
			return db.get(5);
		case "0607":
			return db.get(6);
		case "0708":
			return db.get(7);
		case "0809":
			return db.get(8);
		case "0910":
			return db.get(9);
		case "1011":
			return db.get(10);
		case "1112":
			return db.get(11);
		case "1213":
			return db.get(12);
		case "1314":
			return db.get(13);
		case "9495":
			return db.get(14);
		case "9596":
			return db.get(15);
		case "9697":
			return db.get(16);
		case "9798":
			return db.get(17);
		case "9899":
			return db.get(18);
		case "9900":
			return db.get(19);
		default:
			System.out.println("something wrong! "+years);
			return null;
		}
	}
	
	public static ArrayList<String> getAllEquipas(){
		ArrayList<String> equipasL = new ArrayList<String>();
		for(String ano : anos){
			ArrayList<String> tempEqsL = new ArrayList<String>();
			for(String eq : getEquipas(ano)){
				tempEqsL.add(eq);
			}
			equipasL.removeAll(tempEqsL);
			equipasL.addAll(tempEqsL);
		}
		Collections.sort(equipasL);
		return equipasL;
	}
	
	public static String[] getEquipas(String years){
		List<String[]> data = getDatabase(years);
		int nequipas = Integer.parseInt(data.get(0)[0]);
		String[] equipas = new String[nequipas];
		int found = 0, i = 0;
		while(found < nequipas){
			i++;
			boolean cont = false;
			String[] linha = data.get(i);
			for(int j = 0; j < found; j++){
				if(linha[2].equals(equipas[j])){
					cont = true;
					break;
				}
			}
			if(cont)
				continue;
			equipas[found] = linha[2].trim();
			found++;
		}
		return equipas;
	}
	
	public static String getResultado(String years, String home, String away){
		List<String[]> data = getDatabase(years);
		int i = 0;
		for(String[] linha : data){
			if(i != 0){
				 if(linha[2].equals(home) && linha[3].equals(away)){
					 return linha[4]+"-"+linha[5];
				 }
			}
			i++;
		}
		return "Game not found";
	}
	
	public static String getMelhorJogo(){
		String abc = "";
		int maisGolos = 0;
		for(String ano : anos){
			List<String[]> data = getDatabase(ano);
			int i = 0;
			for(String[] linha : data){
				if(i != 0 && linha.length > 7){
					int l4 = Integer.parseInt(linha[4]);
					int l5 = Integer.parseInt(linha[5]);
					if(l4 > maisGolos || l5 > maisGolos){
						if(l4 > l5){
							maisGolos = l4;
						}else{
							maisGolos = l5;
						}
						abc = linha[2]+"-"+linha[3]+": "+linha[4]+"-"+linha[5];
					}
				}
				i++;
			}
		}
		return abc;
	}
	
	public static String getVencedor(String years, String home, String away){
		List<String[]> data = getDatabase(years);
		int i = 0;
		for(String[] linha : data){
			if(i != 0){
				if(linha.length < 3){
					System.out.println("something wrong with "+years+" at line "+i);
					i++;
					continue;
				}
				if(linha[2].equals(home) && linha[3].equals(away)){
					 if(linha[6].equals("H")){
					 return home;
				} else if(linha[6].equals("A")){
					 return away;
				} else {
					 return "Empate";
					 }
				}
			}
			i++;
		}
		return "Game not found";
	}
	
	public static double[] getConfiancaRel(String home, String away){
		double[] confA = getConfiancaBase(home,away);
		double[] out = new double[3];
		ArrayList<String> eq = getAllEquipas();
		for (int e=0;e<eq.size();e++) {
			String nE = eq.get(e);
			if(!nE.equals(away) && !nE.equals(home)){
				double[] confB = getConfiancaBase(home,nE);
				double[] confC = getConfiancaBase(nE,away);
				if(confA[0]==confA[0]&&confB[0]==confB[0]&&confC[0]==confC[0]){
					for (int i=0;i<3;i++) {
						out[i] = (confA[i] + (confB[i] * confC[i]))/2;
					}
				}
			}
		}
		return out;
	}
	
	public static int getGolosHome(String year, String home, String away){
		return 0;
	}
	
//	public double[] getConfiancaGolos(String home, String away){
//		double[] conf;
//		
//		HashMap<String,double[]> homeConf = confiancasGolos.get(home);
//		if(homeConf != null){
//			conf = homeConf.get(away);
//			if(conf != null){
//				return conf;
//			}
//		}
//		conf = new double[3];
//		
//		int vitorias = 0;
//		int derrotas = 0;
//		int empates = 0;
//		
//		for(String ano : anos){
//			String vencedor = getVencedor(ano, home, away);
//			//System.out.println("ano: "+ano+" vencedor: "+vencedor);
//			if(vencedor.equals(home))
//				vitorias++;
//			if(vencedor.equals(away))
//				derrotas++;
//			if(vencedor.equals("Empate"))
//				empates++;
//		}
//		
//		//System.out.println(vitorias + " "+ empates + " "+ derrotas);
//		int total = vitorias + derrotas + empates;
//		conf[0] = (double)vitorias / (double)total; //vitorias
//		conf[1] = (double)empates / (double)total; //empates
//		conf[2] = (double)derrotas / (double)total; //derrotas
//		
////		System.out.println("TEST: "+conf[0]+"\t"+conf[1]+"\t"+conf[2]+"\t");
////		System.out.println("TEST2: "+conf[0]+"\t"+conf[1]+"\t"+conf[2]+"\t");
//		
//		if(homeConf == null){
//			homeConf = new HashMap<String,double[]>();
//			homeConf.put(away, conf);
//			confiancasGolos.put(home, homeConf);
//		}
//		confiancasGolos.get(home).put(away, conf);
//		
//		return conf;
//	}
	
	public static double[] getConfiancaBase(String home, String away){
		double[] conf;
		
		HashMap<String,double[]> homeConf = confiancas.get(home);
		if(homeConf != null){
			conf = homeConf.get(away);
			if(conf != null){
				return conf;
			}
		}
		conf = new double[3];
		
		int vitorias = 0;
		int derrotas = 0;
		int empates = 0;
		
		for(String ano : anos){
			String vencedor = getVencedor(ano, home, away);
			//System.out.println("ano: "+ano+" vencedor: "+vencedor);
			if(vencedor.equals(home))
				vitorias++;
			if(vencedor.equals(away))
				derrotas++;
			if(vencedor.equals("Empate"))
				empates++;
		}
		
		//System.out.println(vitorias + " "+ empates + " "+ derrotas);
		int total = vitorias + derrotas + empates;
		conf[0] = (double)vitorias / (double)total; //vitorias
		conf[1] = (double)empates / (double)total; //empates
		conf[2] = (double)derrotas / (double)total; //derrotas
		
		//garantir uma distribui��o credivel
		if(conf[1]>=0.9){
			double t = 1 - (conf[1]*0.2);
			conf[0] = (t/2.0)*(1.0-(rand.nextDouble()/10.0));
			conf[2] = (t/2.0)*(1.0-(rand.nextDouble()/10.0));
			conf[1] = 1 - (conf[0]+conf[2]);
		}
		if(conf[2]<0.1){
			double t = 1 - conf[1];
			conf[0] *= (1.0-(rand.nextDouble()/10.0));
			conf[2] = t - conf[0];
		}
		if(conf[0]<0.1){
			double t = 1 - conf[1];
			conf[2] *= (1.0-(rand.nextDouble()/10.0));
			conf[0] = t - conf[2];
		}
		if(conf[1]<0.1){
			conf[0] *= 0.9;
			conf[2] *= 0.9;
			conf[1] = 1 - (conf[0]+conf[2]);
		}
		
		if(homeConf == null){
			homeConf = new HashMap<String,double[]>();
			homeConf.put(away, conf);
			confiancas.put(home, homeConf);
		}
		confiancas.get(home).put(away, conf);
		
		return conf;
	}
	
	public static ArrayList<String> getExistingRivals(String home){
		ArrayList<String> rivals = new ArrayList<String>();
		for(String away : getAllEquipas()){
			if(!((Double)getConfiancaBase(home, away)[0]).isNaN()){
				rivals.add(away);
			}
		}
		return rivals;
	}
	
	public static void populateConfiancaCache(boolean printOutput){
		for(String home : getAllEquipas()){
			if(printOutput)
				System.out.println(home+":");
			for(String away : getAllEquipas()){
				if(!home.equals(away)){
					double[] conf = getConfiancaBase(home, away);
					if(!((Double)conf[0]).isNaN() && printOutput){
						System.out.print("\t"+away + ":");
						if(away.length()<=12)
							System.out.print("\t"); //make even
						for(int i=0;i<3;i++){
							double c = Util.roundTo(conf[i],3);
							System.out.print("\t"+c);
						}
						System.out.println();
					}
				}
			}
		}
	}
	
	public static void printDatabase(){
		for(String ano : anos){
			String[] equipas = getEquipas(ano);
			System.out.println(ano+":");
			for(String home : equipas){
				System.out.println("\t"+home+":");
				for(String away : equipas){
					if(home.equals(away))
						continue;
					System.out.println("\t\t"+away+" -> Resultado: "+getResultado(ano, home, away)+" Vencedor: "+getVencedor(ano, home, away));
				}
			}
		}
	}
}

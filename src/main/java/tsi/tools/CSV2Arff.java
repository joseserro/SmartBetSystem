package main.java.tsi.tools;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
 
import java.io.File;
 
public class CSV2Arff {
  /**
   * takes 2 arguments:
   * - CSV input file
   * - ARFF output file
   */
	public static void main(String[] vagina) throws Exception {
		String[] args = {"trainingSet.csv","penis.arff"};

		// load CSV
		CSVLoader loader = new CSVLoader();
		loader.setSource(new File(args[0]));
		Instances data = loader.getDataSet();

		// save ARFF
		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);
		File penis = new File(args[1]);
		saver.setFile(penis);
		//    saver.setDestination(penis);
		saver.writeBatch();
	}
  
}
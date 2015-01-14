package main.java.tsi.neural;

import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.File;
import java.io.PrintWriter;

public class Neural {

	private Instances trainSet, testSet, tempSet;
	private MultilayerPerceptron percep;

	public int numTestingInstances;
	public double[] classLabels;
	public double[] predictedLabels;

	private String folder;
	
	public Neural(String fol){
		folder = fol;
	}
	
	public void loadData(){
		//System.out.println("Neural Network Test");
//		DataSource trainingSource, testingSource;
		try {
			
			CSVLoader trainLoader = new CSVLoader(), testLoader = new CSVLoader();
			trainLoader.setSource(new File(Neural.class.getResource("/main/resources/tsi/"+folder+"/train.csv").toURI()));
			//testLoader.setSource(new File(folder+"/test.csv"));
            testLoader.setSource(new File(Neural.class.getResource("/main/resources/tsi/"+folder+"/test.csv").toURI()));

			//trainingSource = new DataSource(DatabaseConnection.class.getResource("/"+folder+"/train.csv").openStream());
			//testingSource = new DataSource(DatabaseConnection.class.getResource("/"+folder+"/test.csv").openStream());
			//trainSet = trainingSource.getDataSet(0);
			trainSet = trainLoader.getDataSet();
			trainSet.setClassIndex(0);
			//testSet = testingSource.getDataSet(0);
			testSet = testLoader.getDataSet();
			testSet.setClassIndex(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void clear(){
		numTestingInstances = 0;
		classLabels = null;
		predictedLabels = null;
	}
	
//	public void setTestingEnv(String dados){
//		try {
//			PrintWriter writer = new PrintWriter("/temp.csv", "UTF-8");
//			writer.println("SIGN, B365H, B365D, B365A, BWH, BWD, BWA, IWH, IWD, IWA, LBH, LBD, LBA, PSH, PSD, PSA, WHH, WHD, WHA, SJH, SJD, SJA, VCH, VCD, VCA");
//			writer.println(dados);
//			writer.close();
//			
//			DataSource testEnvSource = new DataSource(DatabaseConnection.class.getResource("/temp.csv").openStream());
//			testSet = testEnvSource.getDataSet(0);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//	}

	public void configure(String options){
		//System.out.println("Data Loaded.");

		boolean UseInTrain = true;  // controls whether to add the Instance at the training set or at the testing set

		//Enumeration<Instance> enumInstances = trainSet.enumerateInstances();

		//System.out.println("Instances: "+trainSet.numInstances());
		int numInst = trainSet.numInstances();
		for(int c=0; c < numInst; c++){
			Instance currInstance  = trainSet.instance(c);
			if (UseInTrain){
				trainSet.add(currInstance);
			} else {
				testSet.add(currInstance);
				UseInTrain = !UseInTrain;
			}
		}

		//System.out.println("Instances Set");

		percep = new MultilayerPerceptron();
		try {
			percep.setOptions(weka.core.Utils.splitOptions(options));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getNumTestingInstances() {
		return numTestingInstances;
	}

	public double[] getClassLabels() {
		return classLabels;
	}

	public double[] getPredictedLabels() {
		return predictedLabels;
	}

	public void buildPerceptron(){
		//System.out.println("MLP Set. Building");
		try {
			percep.buildClassifier(trainSet);

			//System.out.println("trainSet built for percep");

			numTestingInstances = testSet.numInstances();
			classLabels = new double[numTestingInstances];
			predictedLabels = new double[numTestingInstances];

			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getTestSetValues(){
		try {
			int classIdx = testSet.classIndex();
			for(int cnt = 0; cnt < numTestingInstances; cnt++){
				Instance currInstance = testSet.instance(cnt);
				double[] distForInstance = percep.distributionForInstance(currInstance);
				classLabels[cnt] = currInstance.value(classIdx);
				predictedLabels[cnt] = distForInstance[0];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public double testForOdds(String odds){
		try {
			String header = "SIGN, B365H, B365D, B365A, BWH, BWD, BWA, IWH, IWD, IWA, LBH, LBD, LBA, PSH, PSD, PSA, WHH, WHD, WHA, SJH, SJD, SJA, VCH, VCD, VCA";
			PrintWriter writer = new PrintWriter("/main/resources/tsi/"+folder+"/temp.csv", "UTF-8");
			writer.println(header);
			writer.println(odds);
			writer.close();
			DataSource tempSource = new DataSource(Neural.class.getResource("/main/resources/tsi/"+folder+"/temp.csv").openStream());
			tempSet = tempSource.getDataSet(0);
			Instance currInstance = tempSet.instance(0);
			double[] distForInstance = percep.distributionForInstance(currInstance);
			double oioi=distForInstance[0];
			return oioi;
		} catch (Exception e) {
		}
		return Double.NaN;
	}
	
}

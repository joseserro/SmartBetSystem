package main.java.tsi.neural;

import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Neural {

    private Boolean useMultiple;
    private static final Integer MULTIPLE_SIZE = 3; //para esta rede neuronal em especifico

    public int numTestingInstances;

    //region NORMAL
    private Instances trainSet, testSet; //deprecated
    private MultilayerPerceptron percep;
    public double[] classLabels;
    public double[] predictedLabels;
    //endregion

    private List<Instances> trainSetList = new ArrayList<>();
    private List<Instances> testSetList = new ArrayList<>();
    private List<MultilayerPerceptron> perceptronList = new ArrayList<>();

    public List<Double[]> predictedLabelsList = new ArrayList<>();

    private String folder;

    public Neural(String fol, Boolean useMultiple) {
        folder = fol;
        this.useMultiple = useMultiple;
    }

    public void loadData() {
        try {
            if (useMultiple) {
                String[] loaders = {
                        "home",
                        "draw",
                        "away"
                };
                for (String str : loaders) {
                    CSVLoader trainLoader = new CSVLoader();
                    CSVLoader testLoader = new CSVLoader();
                    trainLoader.setSource(new File(Neural.class.getResource("/main/resources/tsi/" + folder + "/train" + str + ".csv").toURI()));
                    testLoader.setSource(new File(Neural.class.getResource("/main/resources/tsi/" + folder + "/test" + str + ".csv").toURI()));

                    Instances trainSet = trainLoader.getDataSet();
                    trainSet.setClassIndex(0);
                    //testSet = testingSource.getDataSet(0);
                    Instances testSet = testLoader.getDataSet();
                    testSet.setClassIndex(0);

                    trainSetList.add(trainSet);
                    testSetList.add(testSet);
                }
            } else {
                CSVLoader trainLoader = new CSVLoader(), testLoader = new CSVLoader();
                trainLoader.setSource(new File(Neural.class.getResource("/main/resources/tsi/" + folder + "/train.csv").toURI()));
                //testLoader.setSource(new File(folder+"/test.csv"));
                testLoader.setSource(new File(Neural.class.getResource("/main/resources/tsi/" + folder + "/test.csv").toURI()));

                trainSet = trainLoader.getDataSet();
                trainSet.setClassIndex(0);
                //testSet = testingSource.getDataSet(0);
                testSet = testLoader.getDataSet();
                testSet.setClassIndex(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        numTestingInstances = 0;
        classLabels = null;
        if(useMultiple){
            predictedLabelsList.clear();
        } else {
            predictedLabels = null;
        }
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

    public void configure(String options) {
        //System.out.println("Data Loaded.");

        boolean UseInTrain = true;  // controls whether to add the Instance at the training set or at the testing set

        //Enumeration<Instance> enumInstances = trainSet.enumerateInstances();

        //System.out.println("Instances: "+trainSet.numInstances());
        if (useMultiple) {
            for (int b = 0; b < MULTIPLE_SIZE; b++) {
                Instances trainSet = trainSetList.get(b);
                Instances testSet = testSetList.get(b);
                int numInst = trainSet.numInstances();
                for (int c = 0; c < numInst; c++) {
                    Instance currInstance = trainSet.instance(c);
                    if (UseInTrain) {
                        trainSet.add(currInstance);
                    } else {
                        testSet.add(currInstance);
                        UseInTrain = !UseInTrain;
                    }
                }
                perceptronList.add(new MultilayerPerceptron());
            }
        } else {
            int numInst = trainSet.numInstances();
            for (int c = 0; c < numInst; c++) {
                Instance currInstance = trainSet.instance(c);
                if (UseInTrain) {
                    trainSet.add(currInstance);
                } else {
                    testSet.add(currInstance);
                    UseInTrain = !UseInTrain;
                }
            }
            percep = new MultilayerPerceptron();
        }

        //System.out.println("Instances Set");


        try {
            if (useMultiple) {
                for(MultilayerPerceptron perceptron : perceptronList){
                    perceptron.setOptions(weka.core.Utils.splitOptions(options));
                }
            } else {
                percep.setOptions(weka.core.Utils.splitOptions(options));
            }
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

    public List<Double[]> getPredictedLabelsList() {
        return predictedLabelsList;
    }

    public void buildPerceptron() {
        //System.out.println("MLP Set. Building");
        try {
            if (useMultiple) {
                for (int b = 0; b < MULTIPLE_SIZE; b++) {
                    Instances trainSet = trainSetList.get(b);
                    Instances testSet = testSetList.get(b);
                    perceptronList.get(b).buildClassifier(trainSet);
                    numTestingInstances = testSet.numInstances();
                    classLabels = new double[numTestingInstances];
                    predictedLabelsList.add(new Double[numTestingInstances]);
                }
            } else {
                percep.buildClassifier(trainSet);

                //System.out.println("trainSet built for percep");

                numTestingInstances = testSet.numInstances();
                classLabels = new double[numTestingInstances];
                predictedLabels = new double[numTestingInstances];
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getTestSetValues() {
        try {
            if(useMultiple){
                for (int b = 0; b < MULTIPLE_SIZE; b++) {
                    Instances trainSet = trainSetList.get(b);
                    Instances testSet = testSetList.get(b);
                    int classIdx = testSet.classIndex();
                    for (int cnt = 0; cnt < numTestingInstances; cnt++) {
                        Instance currInstance = testSet.instance(cnt);
                        double[] distForInstance = perceptronList.get(b).distributionForInstance(currInstance);
                        classLabels[cnt] = currInstance.value(classIdx);
                        predictedLabelsList.get(b)[cnt] = distForInstance[0];
                    }
                }
            } else {
                int classIdx = testSet.classIndex();
                for (int cnt = 0; cnt < numTestingInstances; cnt++) {
                    Instance currInstance = testSet.instance(cnt);
                    double[] distForInstance = percep.distributionForInstance(currInstance);
                    classLabels[cnt] = currInstance.value(classIdx);
                    predictedLabels[cnt] = distForInstance[0];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean getUseMultiple() {
        return useMultiple;
    }

    private String[] breakOdds(String odds){
        String[] brokenOdds = {"","",""};
        String[] splitOdds = odds.split(", ");
        int place = 0;
        for(int b = 1; b < splitOdds.length; b++){
            brokenOdds[place] += ", "+ splitOdds[b];
            place++;
            if(place>2) place=0;
        }
        return brokenOdds;
    }

    public double[] testForOddsMultiple(String odds){
        double[] finalOdds = new double[MULTIPLE_SIZE];
        String[] headers = {
                "SIGN, B365H, BWH, IWH, LBH, PSH, WHH, SJH, VCH",
                "SIGN, B365D, BWD, IWD, LBD, PSD, WHD, SJD, VCD",
                "SIGN, B365A, BWA, IWA, LBA, PSA, WHA, SJA, VCA"
        };
        String[] brokenInputOdds = breakOdds(odds);
        try {
            for(int b = 0; b < MULTIPLE_SIZE; b++){
                String header = headers[b];
                File tempFile = new File("C:/aa_temp"+b+".csv");
                if(!tempFile.exists()) {
                    tempFile.createNewFile();
                    tempFile.deleteOnExit();
                }
                PrintWriter writer = new PrintWriter("C:/aa_temp"+b+".csv", "UTF-8");
                writer.println(header);
                writer.println(brokenInputOdds[b]);
                writer.close();
                InputStream inputStream = new FileInputStream(tempFile);
                DataSource tempSource = new DataSource("C:/aa_temp"+b+".csv");
                //DataSource tempSource = new DataSource(Neural.class.getResource("/main/resources/tsi/" + folder + "/temp"+b+".csv").openStream());
                Instances tempSet = tempSource.getDataSet(0);
                Instance currInstance = tempSet.instance(0);
                double[] distForInstance = perceptronList.get(b).distributionForInstance(currInstance);
                finalOdds[b] = distForInstance[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalOdds;
    }

    public double testForOdds(String odds) {
        try {
            String header = "SIGN, B365H, B365D, B365A, BWH, BWD, BWA, IWH, IWD, IWA, LBH, LBD, LBA, PSH, PSD, PSA, WHH, WHD, WHA, SJH, SJD, SJA, VCH, VCD, VCA";
            PrintWriter writer = new PrintWriter("/main/resources/tsi/" + folder + "/temp.csv", "UTF-8");
            writer.println(header);
            writer.println(odds);
            writer.close();
            DataSource tempSource = new DataSource(Neural.class.getResource("/main/resources/tsi/" + folder + "/temp.csv").openStream());
            Instances tempSet = tempSource.getDataSet(0);
            Instance currInstance = tempSet.instance(0);
            double[] distForInstance = percep.distributionForInstance(currInstance);
            double oioi = distForInstance[0];
            return oioi;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Double.NaN;
    }

}

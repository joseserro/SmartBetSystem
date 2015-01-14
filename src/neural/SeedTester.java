package neural;

public class SeedTester {
	
	public static final int NUM_SEEDS = 1000;
	
	public static void main(String[] args) {
		
		final int cores = Runtime.getRuntime().availableProcessors();
		
		final int seedsPerCore = NUM_SEEDS / cores;
		
		System.out.println("Number of cores: "+cores+" Per Core: "+seedsPerCore);
		
		Thread[] threads = new Thread[cores];
		
		for(int c=0; c < cores; c++){
			final int ac = c;
			Runnable run = new Runnable(){
				@Override
				public void run() {
					
					int seedMaisCerta = 0;
					int certosNaSeedMaisCerta = 0;
					long totalTempo = 0;
					
					for(int i=ac*seedsPerCore; i < (ac+1)*seedsPerCore; i++){
						long time1 = System.currentTimeMillis();
						Neural ann = new Neural("neuralknap");
						ann.loadData();
						ann.configure("-L 0.3 -M 0.2 -N 200 -V 0 -S "+i+" -E 20 -H 3,6,4");
						ann.buildPerceptron();
						ann.getTestSetValues();
						int numTestingInstances = ann.getNumTestingInstances();
						double[] classLabels = ann.getClassLabels();
						double[] predictedLabels = ann.getPredictedLabels();
						int certos = 0;
						for(int j=0; j<numTestingInstances;j++){
							double threshold=0.0;
							if(predictedLabels[j] > 0.18)
								threshold = 1.0;
							if(predictedLabels[j] < -0.18)
								threshold = -1.0;
							if(threshold==classLabels[j])
								certos++;
						}
						if(certos > certosNaSeedMaisCerta){
							certosNaSeedMaisCerta = certos;
							seedMaisCerta = i;
						}
						long time2 = System.currentTimeMillis();
						
						totalTempo += (time2 - time1);
						
						double seg = ((double)time2 - (double)time1)/1000.0;
						
						double media = ((double)totalTempo / ((double)i+1.0)) / 1000.0;
						double res = media * (double)(NUM_SEEDS - (i+1));
						
						System.out.println("CORE "+ac+" - Iteração "+i+" demorou "+seg+" seg. Média: "+media+" seg. Restante: "+res+" seg.");
					}
					
					System.out.println("CORE "+ac+" - Seed mais certa: "+seedMaisCerta+" com "+certosNaSeedMaisCerta+" de acertos (em 240)");
				}
			};
			threads[c] = new Thread(run);
		}
		
		for(int c=0; c < cores; c++){
			threads[c].start();
		}
	}
}

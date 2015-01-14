package main.java.tsi.knapsack;

import main.java.tsi.gui.FrameApostas;

import java.util.List;

import org.opt4j.benchmarks.knapsack.Item;
import org.opt4j.benchmarks.knapsack.ItemSelection;
import org.opt4j.core.Individual;
import org.opt4j.core.optimizer.Archive;
import org.opt4j.core.start.Opt4JTask;
import org.opt4j.optimizers.ea.EvolutionaryAlgorithmModule;

import main.java.tsi.viewer.ViewerNovoModule;
import main.java.tsi.tools.DatabaseConnection;

public class KnapsackTester {
	
	private double valorAposta,capacidade,afinidade,crossover;
	private String casaApostas;
	private int comp,mu,alpha,lambda,iterations;
	
	public KnapsackTester(String casaApostas, int comp,
			double capacidade, double afinidade, double valorAposta, double crossover, int mu, int alpha, int lambda, int iterations) {
		this.casaApostas = casaApostas;
		this.comp = comp;
		this.capacidade = capacidade;
		this.afinidade = afinidade;
		this.valorAposta = valorAposta;
		this.crossover = crossover;
		this.mu = mu;
		this.alpha = alpha;
		this.lambda = lambda;
		this.iterations = iterations;
	}

	public void run() {
		List<String[]> itemsToAdd = DatabaseConnection.getItemsToAdd();
		//List<String[]> equipas = DatabaseConnection.getEquipasList();
		List<String[]> sites = DatabaseConnection.getSites();
		

		int siteInd = 0;
		for(String[] linha : sites){
			if(linha[1].equals(casaApostas)){
				siteInd = Integer.parseInt(linha[2]);
			}
		}

		double[][] odds = new double[3][];
		odds[0] = new double[240]; //home
		odds[1] = new double[240]; //draw
		odds[2] = new double[240]; //away

		for(int i=0;i<3;i++){
			for(int j=0; j<itemsToAdd.size(); j++){
				odds[i][j] = Double.parseDouble(itemsToAdd.get(j)[siteInd+i]);
			}
		}

		EvolutionaryAlgorithmModule ea = new EvolutionaryAlgorithmModule();
		ea.setGenerations(iterations);
		ea.setAlpha(alpha);
		ea.setCrossoverRate(crossover);
		ea.setLambda(lambda);
		ea.setMu(mu);
		
		System.out.println("alpha: "+alpha);
		System.out.println("it: "+iterations);
		System.out.println("cross: "+crossover);
		System.out.println("lambda: "+lambda);
		System.out.println("mu: "+mu);
		
		//		KnapNoSacoModule knap = new KnapNoSacoModule("Bet365", 100, 2.5, 1.9, KnapNoSacoModule.NEURAL);
		//		KnapNoSacoModule knap = new KnapNoSacoModule("Bet365", 100, 20, 1.9, KnapNoSacoModule.FCONF);
		//valorAposta default = 100, capacidade default = 15.0, afinidade default = 1.9;
		KnapNoSacoModule knap = new KnapNoSacoModule(casaApostas, valorAposta, capacidade, afinidade, comp);
		ViewerNovoModule viewer = new ViewerNovoModule();
		Opt4JTask task = new Opt4JTask(false);
		task.init(ea,knap,viewer);



		try {
			task.execute();

			Archive archive = task.getInstance(Archive.class);
			int i=0;
			for (Individual individual : archive) {
				System.out.println("Knapsack "+i);
				i++;
				ItemSelection isel =  (ItemSelection)individual.getPhenotype();
				for(Item item : isel){
					int itemId = Integer.parseInt(item.toString().substring(4));
					int a=0,cop=itemId;
					while(cop>=240){
						cop-=240;
						a++;
					}
					String home=itemsToAdd.get(cop)[0],away=itemsToAdd.get(cop)[1];
					
					FrameApostas.novaAposta(itemId, home, away, odds[a][cop], a);
					
//					System.out.println("\t"+item + " - "+itemId+": ");
//					System.out.println("\t\t"+a+", "+cop+": "+odds[a][cop]+" ("+home+" - "+away+") "+resultado);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			task.close();
		} 
	}
}

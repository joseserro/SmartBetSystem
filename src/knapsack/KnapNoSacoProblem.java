package knapsack;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.opt4j.benchmarks.knapsack.Item;
import org.opt4j.benchmarks.knapsack.Knapsack;
import org.opt4j.benchmarks.knapsack.KnapsackProblem;
import org.opt4j.core.start.Constant;

import tools.DatabaseConnection;
import tools.Util;

import com.google.inject.Inject;

public class KnapNoSacoProblem implements KnapsackProblem {
	
	protected final List<Item> items = new ArrayList<Item>();
	protected final List<Knapsack> knapsacks = new ArrayList<Knapsack>();
	
	/**
	 * Creates a new {@link KnapNoSacoProblem}.
	 * 
	 * @param site
	 *            site
	 * @param valorApostar
	 *            valor a apostar
	 * @param capacidade
	 *            capacidade
	 * @param agressividade
	 * 			  agressividade
	 * @param comp
	 * 			  comp
	 */
	@Inject
	public KnapNoSacoProblem(@Constant(value = "site", namespace = KnapNoSacoProblem.class) String site,
			@Constant(value = "valorApostar", namespace = KnapNoSacoProblem.class) double valorApostar,
			@Constant(value = "capacidade", namespace = KnapNoSacoProblem.class) double capacidade,
			@Constant(value = "agressividade", namespace = KnapNoSacoProblem.class) double agressividade,
			@Constant(value = "comp", namespace = KnapNoSacoProblem.class) int comp){
		try {
			List<String[]> itemsToAdd = DatabaseConnection.getItemsToAdd();
			List<String[]> sites = DatabaseConnection.getSites();
			List<String[]> equipas = DatabaseConnection.getEquipasList();
			List<String[]> neuralScores = DatabaseConnection.getNeuralScores();
			
			int siteInd = 0;
			for(String[] linha : sites){
				if(linha[1].equals(site)){
					siteInd = Integer.parseInt(linha[2]);
				}
			}

			double[][] odds = new double[3][];
			odds[0] = new double[240]; //home
			odds[1] = new double[240]; //draw
			odds[2] = new double[240]; //away

			int ind = 0;
			for(int i=0;i<3;i++){
				for(int j=0; j<itemsToAdd.size(); j++){
					odds[i][j] = Double.parseDouble(itemsToAdd.get(j)[siteInd+i]);
					this.items.add(new Item(ind));
					ind++;
				}
			}
			
			
			Knapsack saco = new Knapsack(0);
			for(int i=0; i<3; i++){
				for (int j=0; j<240; j++) {
					Item item = this.items.get((240*i)+j);
					int peso = (int) (odds[i][j] * 100.0);
					
					System.out.print(((240*i)+j)+" peso original: "+peso+" ");
					if(comp==KnapNoSacoModule.NEURAL){
						if(i==(Integer.parseInt(neuralScores.get(j)[0])*-1+1)){
							peso = (int) ((double)peso * Util.calcPesoWin(agressividade));
						} else {
							peso = (int) ((double)peso * Util.calcPesoLose(agressividade));
						}
					} else if(comp==KnapNoSacoModule.FCONF){
						String home=itemsToAdd.get(j)[0],away=itemsToAdd.get(j)[1];
						for(String[] linha : equipas){
							if(linha[1].equals(home))
								home=linha[0];
							if(linha[1].equals(away))
								away=linha[0];
						}
						double[] fconf = DatabaseConnection.getConfiancaRel(home, away);
						peso = (int) ((double)peso / fconf[i]);
					} else if(comp==KnapNoSacoModule.HYBRID) {
						if(i==(Integer.parseInt(neuralScores.get(j)[0])*-1+1)){
							peso = (int) ((double)peso * Util.calcPesoWin(agressividade));
						} else {
							peso = (int) ((double)peso * Util.calcPesoLose(agressividade));
						}
						String home=itemsToAdd.get(j)[0],away=itemsToAdd.get(j)[1];
						for(String[] linha : equipas){
							if(linha[1].equals(home))
								home=linha[0];
							if(linha[1].equals(away))
								away=linha[0];
						}
						double[] fconf = DatabaseConnection.getConfiancaBase(home, away);
						double[] fconf2 = DatabaseConnection.getConfiancaRel(home, away);
						double[] fconfm = {(fconf[0]+fconf2[0]) / 2, (fconf[1]+fconf2[1])/2,(fconf[2]+fconf2[2]) / 2};
						peso = (int) ((double)peso / fconfm[i]);
					}
					System.out.println(" novo peso: "+peso);
					
					int ganho = (int) (((odds[i][j] * (double)valorApostar) - (double)valorApostar) * 100.0);
					saco.setWeight(item, peso);
					saco.setProfit(item, ganho);
				}
			}
			
			saco.setCapacity((int) (capacidade * 100.0));

			this.knapsacks.add(saco);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Collection<Item> getItems() {
		return items;
	}

	@Override
	public Collection<Knapsack> getKnapsacks() {
		return knapsacks;
	}
}

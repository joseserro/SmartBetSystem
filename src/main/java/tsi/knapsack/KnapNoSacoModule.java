package main.java.tsi.knapsack;


import org.opt4j.benchmarks.knapsack.KnapsackBinaryCreatorDecoder;
import org.opt4j.benchmarks.knapsack.KnapsackOverloadEvaluator;
import org.opt4j.benchmarks.knapsack.KnapsackProblem;
import org.opt4j.benchmarks.knapsack.KnapsackProfitEvaluator;
import org.opt4j.core.problem.ProblemModule;
import org.opt4j.core.start.Constant;


public class KnapNoSacoModule extends ProblemModule {
	
	public static final int NEURAL = 0;
	public static final int FCONF = 1;
	public static final int HYBRID = 2;
	
	protected int knapsacks = 1;
	protected int items = 720;
	
	@Constant(value = "site", namespace = KnapNoSacoProblem.class)
	protected String site;
	
	@Constant(value = "valorApostar", namespace = KnapNoSacoProblem.class)
	protected double valorApostar;
	
	@Constant(value = "capacidade", namespace = KnapNoSacoProblem.class)
	protected double capacidade;
	
	@Constant(value = "agressividade", namespace = KnapNoSacoProblem.class)
	protected double agressividade;
	
	@Constant(value = "comp", namespace = KnapNoSacoProblem.class)
	protected int comp;
	
	public KnapNoSacoModule(String site, double valorApostar, double capacidade, double agressividade, int comp) {
		this.site = site;
		this.valorApostar = valorApostar;
		this.capacidade = capacidade;
		this.agressividade = agressividade;
		this.comp = comp;
	}
	
	@Override
	protected void config(){
		bindConstant("site", KnapNoSacoProblem.class).to(site);
		bindConstant("valorApostar", KnapNoSacoProblem.class).to(valorApostar);
		bindConstant("capacidade", KnapNoSacoProblem.class).to(capacidade);
		bindConstant("agressividade", KnapNoSacoProblem.class).to(agressividade);
		bindConstant("comp", KnapNoSacoProblem.class).to(comp);
		bind(KnapsackProblem.class).to(KnapNoSacoProblem.class).in(SINGLETON);
		bindProblem(KnapsackBinaryCreatorDecoder.class, KnapsackBinaryCreatorDecoder.class, KnapsackProfitEvaluator.class);
		
		addEvaluator(KnapsackOverloadEvaluator.class);
	}
	
}

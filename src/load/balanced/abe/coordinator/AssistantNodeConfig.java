package load.balanced.abe.coordinator;

public class AssistantNodeConfig {


	private double assitantNodeWeight; // GFLOPs per computer  
	private double coreWeight; //  GFLOPs per core 
	private int threads; //Number of threads per computer  
	private String cpu_model;
	private int cores;
	
	/**
	 * 
	 * @param cpu_model
	 * @param cores
	 * @param threads
	 * @param computer_gflops
	 * @param core_gflops
	 */
	public AssistantNodeConfig(String cpu_model, int cores, int threads, double computer_gflops, double core_gflops) {

        this.cpu_model = cpu_model;
        this.cores = cores;
		this.threads = threads;
		this.assitantNodeWeight =computer_gflops;
		this.coreWeight = core_gflops;
	}

	public String getCPUModel() {
		return cpu_model;
	}

	public double getAssitantNodeWeight() {
		return assitantNodeWeight;
	}

	public double getCoreWeight() {
		return coreWeight;
	}
	/**
	 * Number of cores (threads) per computer  
	 * @return
	 */
	public int getThreads() {
		return threads;
	}	
	/**
	 * 
	 */
	public String toString() {
		
		String s = "\n"+getCPUModel()+", \n ";
		s +="Core Weight :["+getCoreWeight()+" GFLOPS/sec],\n ";
		s +="#Resources["+threads+"] \n ";
		return s;
	}
}

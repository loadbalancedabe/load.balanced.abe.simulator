package load.balanced.abe.sensors;

import java.util.LinkedList;


public class AbstractWorkload {

	private int secretValue; //S 
	private LinkedList<Integer> qy0Set = new LinkedList<Integer>();
	private LinkedList<AbstractWorkload> subWorkloads = null;

	
	private int estimatedExecutionTime=0;
	private int estimatedCumulativeExecutionTime = 0; // We add the waitingTime in queue to the estimatedExecutionTime 
	public AbstractWorkload(int secretValue) {
		this.secretValue = secretValue;
		subWorkloads = new LinkedList<AbstractWorkload>();
	}
	public AbstractWorkload() {
		
	}
	public int getSecretValue() {
		return secretValue;
	}
	@Override
	public String toString() {
		String s = "Secret Value: "+secretValue+" ";
		for (Integer qy0 : qy0Set) {
			s += ", qy0:"+ qy0;
		}
		
		return s;
	}
	public void setSecretValue(int secretValue) {
		this.secretValue = secretValue;
	}
	public LinkedList<Integer> getQy0Set() {
		return qy0Set;
	}
	public int getNumberOfAttributes() {
		return qy0Set.size();
	}
	public int getEstimatedExecutionTime() {
		return estimatedExecutionTime;
	}
	public LinkedList<AbstractWorkload> getSubWorkloads() {
		return subWorkloads;
	}
	public void setEstimatedExecutionTime(int estimatedExecutionTime) {
		this.estimatedExecutionTime = estimatedExecutionTime;
	}
	public void setEstimatedCumulativeExecutionTime(int estimatedCumulativeExecutionTime) {
		this.estimatedCumulativeExecutionTime = estimatedCumulativeExecutionTime;
	}
	public AbstractWorkload clone() {
		AbstractWorkload workload  = new AbstractWorkload(this.getSecretValue());
	
		for (Integer integer : qy0Set) {
			workload.getQy0Set().addLast(integer);
		}
		if(subWorkloads!= null) {
			for (AbstractWorkload subWorkload : subWorkloads) {
				workload.getSubWorkloads().add(subWorkload.clone());
			}
		}
		
		return workload;
		
	}
	public int getWorkloadToCompute() {
		// H^S_i : ((S_i -1) is the number of multiplication operations )  
		// (e(g,g)^alpha)^S_i ((S_i -1) is the number of multiplication operations ) 
		int totalWorkload = ((getSecretValue() -1) *2) ;

		int sumWorkloadQy0 =0; 
		// for each attribute y in Y, we calculate g^qy0i  and H(att(y))^qy0i 
		for (Integer Qy0rj_i : getQy0Set()) {
			// g^qy0i : ((qy0i -1) is the number of multiplication operations)
			sumWorkloadQy0 = sumWorkloadQy0 + (Qy0rj_i -1) ;
		}
		sumWorkloadQy0 = sumWorkloadQy0 *2;
		
		
		totalWorkload = totalWorkload + sumWorkloadQy0 ;
		return totalWorkload;
	}
	public boolean isDivisible(int nbrResources) {
		boolean bool = true;
		if(secretValue < nbrResources)
			bool = false;
		for (Integer qy0 : qy0Set) {
			if(qy0<nbrResources) {
				bool = false;
			}
		}
		return false;
	}
	private int getAllWorkloadToCompute() {
				
		int totalWorkload = getWorkloadToCompute();
		for (AbstractWorkload subWorkload : getSubWorkloads()) {
			totalWorkload = totalWorkload + subWorkload.getWorkloadToCompute();
		}
		return totalWorkload;
	}
	public int getNbrNeedResources() {

		int minValue = secretValue;
		
		for (Integer qy0 : qy0Set) {
			if(qy0 < secretValue) {
				minValue = secretValue;
			}
		}
		return minValue;
	}
	
}

package load.balanced.abe.simulation;

import java.util.LinkedList;

import load.balanced.abe.sensors.AbstractWorkload;

public class SavedSubWorkloads {

	private AbstractWorkload workload;
	private int numberOfResources;
	private LinkedList<AbstractWorkload> subWorkloads = new LinkedList<AbstractWorkload>();
	
	public SavedSubWorkloads() {
		// TODO Auto-generated constructor stub
	}
	public AbstractWorkload getWorkload() {
		return workload;
	}
	public void setWorkload(AbstractWorkload workload) {
		this.workload = workload;
	}
	public int getNumberOfResources() {
		return numberOfResources;
	}
	public void setNumberOfResources(int numberOfResources) {
		this.numberOfResources = numberOfResources;
	}
	public LinkedList<AbstractWorkload> getSubWorkloads() {
		return subWorkloads;
	}
	public void setSubWorkloads(LinkedList<AbstractWorkload> subWorkloads) {
		this.subWorkloads = subWorkloads;
	}
	
	
}

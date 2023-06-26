package load.balanced.abe.coordinator;

import java.util.ArrayList;
import java.util.List;

import load.balanced.abe.sensors.AbstractSensor;
import load.balanced.abe.sensors.AbstractSensorSRequest;

/**
 * 
 */
public class AssistantNode {
	
	private String ID ;
	
	private AssistantNodeConfig assistantNodeConfig;
	private List<Resource> resources = new ArrayList<Resource>(); // cores
	private int totalExecutionTime = 0;
	private List<AbstractSensor> servingSensors;

	/**
	 * 
	 * @param weight
	 * @param coreWeight
	 * @param cores
	 */
	public AssistantNode(String ID, AssistantNodeConfig assistantNodeConfig ) {
		this.ID=ID;
		this.assistantNodeConfig =assistantNodeConfig;
		for (int i = 0; i < assistantNodeConfig.getThreads(); i++) {
			Resource r = new Resource(this,assistantNodeConfig.getCoreWeight());
			resources.add(r);
		}

		
	}
	public boolean isAvailable() {
		for (Resource resource : resources) {
			if(resource.isFree())
				return true;
		}
		return false;
	}

	
	public List<Resource> getAvailableResources() {
		List<Resource> availableResources  =new ArrayList<Resource>();
		for (Resource resource : resources) {
			if(resource.isFree()) {
				availableResources.add(resource);
			}
		}
		return availableResources;
	} 
	public List<Resource> getResources() {
		return resources;
	}
	/**
	 * Makes the assistant node works for one unit time.
	 */
	public void work() {
		
		totalExecutionTime++;

		for (Resource resource : resources) {
			resource.work();
		}
	}

	
	

	public List<AbstractSensor> getServingSensors() {
		return servingSensors;
	}

	public void setServingSensors(List<AbstractSensor> servingSensors) {
		this.servingSensors = servingSensors;
	}

	public AssistantNodeConfig getAssistantNodeConfig() {
		return assistantNodeConfig;
	}
	
	public String getID() {
		return ID;
	}
	public String toString() {
		
		String s = ID+"\n CPU Model :"+getAssistantNodeConfig().getCPUModel()+"\n ";
		s +="Core weight : "+getAssistantNodeConfig().getCoreWeight()+" GFLOPS/sec \n ";
		s +="Resources (threads) size["+resources.size()+"] \n [";
		for (Resource resource : resources) {
			if(resource.getServingSensor() !=null)
			s +="Resource [Serving Sensor : "+resource.getServingSensor().toString()+", Remains : "+ resource.getRemainingEncryptionTime() +", "+resource.getServingSensorRequest().getMessage().toString()+" ] ";
			else
				s +="Resource [Serving Sensor : None] ";
			
		}
		s +="]";
		return s;
		//return s;
	}

}

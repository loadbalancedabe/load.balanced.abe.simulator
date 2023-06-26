package load.balanced.abe.coordinator;

import java.util.LinkedList;

import load.balanced.abe.sensors.AbstractSensorSRequest;

public abstract class AbstractAssignmentStrategy {

	private LinkedList<AssistantNode> assistantNodes;
	private LinkedList<AbstractSensorSRequest> sensorSRequests;
	private LinkedList<Resource> availableResources;

	public AbstractAssignmentStrategy(LinkedList<AssistantNode> assistantNodes, LinkedList<AbstractSensorSRequest> sensorSRequests) {
		this.assistantNodes =assistantNodes;
		this.sensorSRequests = sensorSRequests;
		availableResources = new LinkedList<Resource>();
	}
	public abstract void assignResourcesToSN();
	public LinkedList<AbstractSensorSRequest> getSensorSRequests() {
		return sensorSRequests;
	}
	public LinkedList<AssistantNode> getFreeAssistantNodes() {
		return assistantNodes;
	}
	public void askAvailabalityResources() {
		
		availableResources = new LinkedList<Resource>();
		
		for (AssistantNode assistantNode : assistantNodes) {
			if(assistantNode.isAvailable()) {
				
			 availableResources.addAll(assistantNode.getAvailableResources());
			}
		}
	}
	public LinkedList<Resource> receiveAvailableResources() {
		
		return availableResources;
	}
	public int getNbrAvailableResources() {
		int totalNbrOfAR = 0;
		for (AssistantNode assistantNode : getFreeAssistantNodes()) {
			if(assistantNode.isAvailable()) {
			 totalNbrOfAR += assistantNode.getAvailableResources().size();
			}
		}
		return totalNbrOfAR;
	}
}

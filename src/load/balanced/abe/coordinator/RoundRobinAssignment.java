package load.balanced.abe.coordinator;

import java.util.LinkedList;

import load.balanced.abe.sensors.AbstractSensorSRequest;

public class RoundRobinAssignment extends AbstractAssignmentStrategy{

		
	public RoundRobinAssignment(LinkedList<AssistantNode> freeAssistantNodes, LinkedList<AbstractSensorSRequest> sensorSRequests) {
		super(freeAssistantNodes, sensorSRequests);
	}

	public void assignResourcesToSN() {
		
		int next =0;
		if(getFreeAssistantNodes().size()>1) { // we use at least two different assistant nodes 
			
			askAvailabalityResources();
			
			LinkedList<Resource> receivedAvailableResources = receiveAvailableResources();
			
			if(receivedAvailableResources.size()>0) {
				while(next<receivedAvailableResources.size() && getSensorSRequests().size()>0) {
					for (AbstractSensorSRequest sensorRequest : getSensorSRequests()) {
						if(next<receivedAvailableResources.size()) {
							Resource resource = receivedAvailableResources.get(next);
							next++;
							if(resource !=null) {
								sensorRequest.getAssignedResources().add(resource);
							}
						}
					}
				}
			}
		}
	}
	

	
	
	
}

package load.balanced.abe.coordinator;

import java.util.LinkedList;

import load.balanced.abe.sensors.AbstractSensorSRequest;

public class UrgentMessRRAssignment extends AbstractAssignmentStrategy{
  
		
	public UrgentMessRRAssignment(LinkedList<AssistantNode> assistantNodes, LinkedList<AbstractSensorSRequest> sensorSRequests) {
		super(assistantNodes, sensorSRequests);
	}

	public void assignResourcesToSN() {
		
		
		int next =0;
		askAvailabalityResources();
		LinkedList<Resource> receivedAvailableResources = receiveAvailableResources();
		
	//	System.out.println("Number of available resources :"+receivedAvailableResources.size());

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

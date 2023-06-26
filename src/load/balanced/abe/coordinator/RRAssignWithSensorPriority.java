package load.balanced.abe.coordinator;

import java.util.LinkedList;

import load.balanced.abe.sensors.AbstractSensorSRequest;


public class RRAssignWithSensorPriority extends AbstractAssignmentStrategy{

	public RRAssignWithSensorPriority(LinkedList<AssistantNode> freeAssistantNodes, LinkedList<AbstractSensorSRequest> sensorSRequests) {
		super(freeAssistantNodes, sensorSRequests);
	}

	@Override
	public void assignResourcesToSN() {
		// TODO Auto-generated method stub
		/* simulationEntry.getPrioritySensorRate(); 
		  if (SimulationUtility.isPrioritySensor(prioritySensorRate)) { 
			  
		  */ 
			  
	}

}

package load.balanced.abe.coordinator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import load.balanced.abe.sensors.AbstractSensorSRequest;

/**
 * A FIFO queue for sensor requests.
 */
public class Queue {
	private LinkedList<AbstractSensorSRequest> sensorSRequests = new LinkedList<AbstractSensorSRequest>();

	public boolean isEmpty() {
		return sensorSRequests.isEmpty();
	}

	public void addQueueLast(AbstractSensorSRequest sRequest) {
		sensorSRequests.addLast(sRequest);
	}

	/*public AbstractSensorSRequest getQueueFirst() {
		return sensorSRequests.removeFirst();
	}*/

	public LinkedList<AbstractSensorSRequest> getSensorSRequests() {
		return sensorSRequests;
	}

	public AbstractSensorSRequest findPrioritySensorRequest() {
		for (AbstractSensorSRequest sensorRequest : sensorSRequests) {
			if (sensorRequest.isPriority()) {
				return sensorRequest;
			}
		}
		return null;
	}

	public boolean containsRequestOfSensor(int sensorNumber) {
		for (AbstractSensorSRequest sensorRequest : sensorSRequests) {
			if (sensorRequest.getSensor().getID() == sensorNumber) {
				return true;
			}
		}
		return false;
	}

	public String toString() {
		StringBuffer results = new StringBuffer();
		results.append(" size[" + sensorSRequests.size() + "] : \n");
		for (AbstractSensorSRequest request : sensorSRequests) {
			results.append(request.toString() + "-->\n");
		}
		return results.toString();
	}

	/*
	 * public void removePrioritySensorRequest(AbstractSensorSRequest request) {
	 * sensorSRequests.remove(request); }
	 */
	public void updateSensorSRequestPatience() {
		for (AbstractSensorSRequest sensorSRequest : sensorSRequests) {
			sensorSRequest.reducePatience();
		}
	}

	public List<AbstractSensorSRequest> removeImpatientSensorSRequest() {
		
		List<AbstractSensorSRequest> removeList = new ArrayList<AbstractSensorSRequest>();
		for (AbstractSensorSRequest sensorSRequest : sensorSRequests) {
			if (!sensorSRequest.isPatient()) {
				removeList.add(sensorSRequest);
				
			}
		}

		for (AbstractSensorSRequest sensorSRequest : removeList) {
			sensorSRequests.remove(sensorSRequest);
		}
		return removeList;
	}

	public int size() {
		// TODO Auto-generated method stub
		return sensorSRequests.size();
	}

	public List<AbstractSensorSRequest> removeSensorSRequestOutOfDate() {
			
			List<AbstractSensorSRequest> removeList = new ArrayList<AbstractSensorSRequest>();
			for (AbstractSensorSRequest sensorSRequest : sensorSRequests) {
				
					removeList.add(sensorSRequest);
				
			}

			for (AbstractSensorSRequest sensorSRequest : removeList) {
				sensorSRequests.remove(sensorSRequest);
			}
			return removeList;
	}
	

}

package load.balanced.abe.sensors;

import java.util.ArrayList;
import java.util.List;

import load.balanced.abe.coordinator.Resource;

/**
 * We need to know when a sensors request assistant nodes, when the heavy calculation starts and finishes.
 * When creating the sensor, only his arrival request time can be defined.
 */
public abstract class AbstractSensorSRequest {
	private int arrivalTime;
	private int encryptionStartTime;
	private int encyptionFinishTime;
	private int waitingInQueueTime = 0;
	private int departureTime;
	private AbstractMessage message;
	private int patienceTime;
	private AbstractSensor sensor;
    private boolean isServed =false;
	private List<Resource> assignedResources = new ArrayList<Resource>();


	public AbstractSensorSRequest(int arrivalTime, AbstractMessage message,AbstractSensor sensor,int patienceTime) {
		this.arrivalTime = arrivalTime;
		this.message = message;
		this.patienceTime = patienceTime;
		this.sensor=sensor;
	}
	public AbstractSensor getSensor() {
		return sensor;
	}
	public abstract boolean isPriority();

	
	public int getArrivalTime() {
		return arrivalTime;
	}

	public int getEncryptionStartTime() {
		return encryptionStartTime;
	}
	public void setEncryptionStartTime(int encryptionStartTime) {
		this.encryptionStartTime = encryptionStartTime;
	}
	public int getEncyptionFinishTime() {
		return encyptionFinishTime;
	}
	public void setEncyptionFinishTime(int encyptionFinishTime) {
		this.encyptionFinishTime = encyptionFinishTime;
	}
	

	

	

	public void setServiceStartTime(int serviceStartTime) {
		this.encryptionStartTime = serviceStartTime;
	}

	public List<Resource> getAssignedResources() {
		return assignedResources;
	}
	public int getDepartureTime() {
		return departureTime;
	}
	public void setDepartureTime(int departureTime) {
		this.departureTime = departureTime;
	}
	public AbstractMessage getMessage() {
		return message;
	}

	public void setMessage(AbstractMessage message) {
		this.message = message;
	}
	public void reducePatience() {
		if (patienceTime > 0) {
			patienceTime--;
		}
	}

	/*public boolean isPatient() {
		return patienceTime > 0 || message.isUrgent();
	}*/
	public boolean isPatient() {
		return patienceTime > 0 ;
	}
	public boolean isServed() {
		return isServed;
	}
	public void setServed(boolean isServed) {
		this.isServed = isServed;
	}
	public String toString() {
		return "Sensor Request [ "+sensor.toString()+" arrival : " + arrivalTime + ", patience time : " + patienceTime + ", " + message.toString() + "]";
	}
}

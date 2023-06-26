package load.balanced.abe.coordinator;

import load.balanced.abe.sensors.AbstractSensor;
import load.balanced.abe.sensors.AbstractSensorSRequest;

/***
 * A resource represents a core in the assistant node
 * 
 * @author Kerdoudi-PC
 *
 */
public class Resource {

	private double coreWeight;
	private AssistantNode assistantNode;
	private int encryptionTime = 0;
	private int remainingEncryptionTime = 0;

	private AbstractSensor servingSensor = null;

	private AbstractSensorSRequest servingSensorRequest;

	public Resource(AssistantNode assistantNode, double coreWeight) {
		this.assistantNode = assistantNode;
		this.coreWeight = coreWeight;
	}

	public boolean isFree() {
		return servingSensor == null;
	}

	/**
	 * Verifies if the resource finishes the current encryptionwork.
	 * 
	 * @return true if the resource just finished the current work
	 */
	public boolean computeFinished() {
		return servingSensor != null && remainingEncryptionTime == 0;
	}

	public void work() {
		if (remainingEncryptionTime > 0) {
			remainingEncryptionTime--;
			setEncryptionTime(getEncryptionTime() + 1);
		}
	}

	public int getRemainingEncryptionTime() {
		// TODO Auto-generated method stub
		return remainingEncryptionTime;
	}

	public void setRemainingEncryptionTime(int remainingEncryptionTime) {
		this.remainingEncryptionTime = remainingEncryptionTime;
	}

	public AbstractSensor getServingSensor() {
		return servingSensor;
	}

	public void setServingSensor(AbstractSensor servingSensor) {
		this.servingSensor = servingSensor;
	}

	public AbstractSensorSRequest getServingSensorRequest() {
		return servingSensorRequest;
	}

	public double getCoreWeight() {
		return coreWeight;
	}

	public AssistantNode getAssistantNode() {
		return assistantNode;
	}

	public void setServingSensorRequest(AbstractSensorSRequest servingSensorRequest) {
		this.servingSensorRequest = servingSensorRequest;
	}

	public int getEncryptionTime() {
		return encryptionTime;
	}

	public void setEncryptionTime(int encryptionTime) {
		this.encryptionTime = encryptionTime;
	}
}

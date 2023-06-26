package load.balanced.abe.sensors;

public abstract class AbstractSubEncryption {

	private int arrivalTime;
	private int encryptionStartTime;
	private int departureTime;
	private AbstractMessage message;
	private int patienceTime;
	private AbstractSensor sensor;

	public AbstractSubEncryption(int arrivalTime, AbstractMessage message, int patienceTime) {
		this.arrivalTime = arrivalTime;
		this.message = message;
		this.patienceTime = patienceTime;
	}
	
	public abstract boolean isPriority();

	public int getArrivalTime() {
		return arrivalTime;
	}

	public int getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(int departureTime) {
		this.departureTime = departureTime;
	}

	public int getServiceStartTime() {
		return encryptionStartTime;
	}

	public void setEncryptionStartTime(int serviceStartTime) {
		this.encryptionStartTime = serviceStartTime;
	}

	public AbstractMessage getMessage() {
		return message;
	}

	public void reducePatience() {
		if (patienceTime > 0) {
			patienceTime--;
		}
	}

	public boolean isPatient() {
		return patienceTime > 0 || message.isUrgent();
	}

	public String toString() {
		return "Sub Encryption [ : "+ sensor.toString()+", arrival : " + arrivalTime + ", patience time : " + patienceTime + ", " + message.toString() + "]";
	}
}

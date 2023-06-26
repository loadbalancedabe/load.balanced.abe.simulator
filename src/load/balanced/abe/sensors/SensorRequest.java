package load.balanced.abe.sensors;

public class SensorRequest extends AbstractSensorSRequest {

	public SensorRequest(int arrivalTime, AbstractMessage message, AbstractSensor sensor,int patienceTime ) {
		super(arrivalTime, message, sensor, patienceTime);		
	}

	/**  is  for urgent message
	 * 	@Override
	 */
	public boolean isPriority() {
		return false;
	}

}

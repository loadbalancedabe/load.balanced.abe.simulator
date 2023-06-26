package load.balanced.abe.sensors;

public class VIPSensor extends AbstractSensor {

	

    public VIPSensor(int i,int minSecretValue,int maxSecretValue, int minNumberOfAttibutes, int maxNumberOfAttibutes, int qy0Interval) {
		super(i, minSecretValue,maxSecretValue,minNumberOfAttibutes, maxNumberOfAttibutes, qy0Interval);
		// TODO Auto-generated constructor stub
	}

	//	@Override
	public boolean isPriority() {
		return true;
	}

	@Override
	public String toString() {
		return "Priority " + super.toString();
	}

}

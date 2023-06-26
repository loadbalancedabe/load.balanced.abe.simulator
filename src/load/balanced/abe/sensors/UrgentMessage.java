package load.balanced.abe.sensors;

public class UrgentMessage extends AbstractMessage{

	public UrgentMessage() {
	}

	@Override
	public boolean isUrgent() {
		return true;
	}
	
	@Override
	public String toString() {
		return "Message : Urgent";
	}

}

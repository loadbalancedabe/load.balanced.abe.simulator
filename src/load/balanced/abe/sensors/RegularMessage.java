package load.balanced.abe.sensors;

public class RegularMessage extends AbstractMessage {

	public RegularMessage() {
	}

	@Override
	public boolean isUrgent() {
		return false;
	}
	
	@Override
	public String toString() {
		return "Message : Regular";
	}
}

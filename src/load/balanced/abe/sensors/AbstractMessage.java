package load.balanced.abe.sensors;

public abstract class AbstractMessage {
	private int totalEncryptionTime; // M * e(gg)^alpha^S
    private AbstractWorkload workload ;
	public AbstractMessage() {
		
	}



	public AbstractWorkload getWorkload() {
		return workload;
	}

	public void setWorkload(AbstractWorkload workload) {
		this.workload = workload;
	}


	public int getTotalEncryptionTime() {
		totalEncryptionTime =workload.getEstimatedExecutionTime() + 1;

		return totalEncryptionTime;
	}



	public abstract boolean isUrgent();
}

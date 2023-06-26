package load.balanced.abe.simulation;

import java.util.List;

import load.balanced.abe.coordinator.AssistantNodeConfig;
import load.balanced.abe.sensors.AbstractSensor;

/**
 * This class regroups simulation entry parameters.
 */
public class SimulationEntry {
	private int waitTime;
	private int simulationDuration;
	private int assistantNodeCount;
	private int sensorCount;
	private int firstSensorRequestArrival;
	private int sensorRequestsArrivalInterval;
	private double urgentMessageRate;
	private int sensorRequestPatienceTime;
	private UsedApproach approach;
	
	private int minSecretValue ;
	private int maxSecretValue;
	private int qy0Interval ;
	private int minNumberOfAttibutes;
	private int maxNumberOfAttibutes ;
	
	private List<AssistantNodeConfig> selectedAssistantNodeConfigs ;
	private List<AbstractSensor> sensors;
	private boolean isRandomSelectionOfAN ;
	private boolean isRandomSelectionOfSensors;
	private boolean printDetails;
	private InputAssistantNodes used_cpu_models;

	public SimulationEntry(int simulationDuration, int waitTime, int assistantNodeCount, int sensorCount,int firstSensorRequestArrival, int sensorRequestsArrivalInterval, double urgentMessageRate,
			int sensorRequestPatienceTime, int minSecretValue,int maxSecretValue, int qy0Interval, int minNumberOfAttibutes, int maxNumberOfAttibutes, boolean isRandomSelectionOfAN, boolean isRandomSelectionOfSensors, boolean printDetails,InputAssistantNodes  used_cpu_models) {
		this.simulationDuration = simulationDuration;
		//this.waitTime=waitTime;
		this.waitTime=sensorCount+1;
		this.assistantNodeCount = assistantNodeCount;
		this.sensorCount=sensorCount;
		this.firstSensorRequestArrival = firstSensorRequestArrival;
		this.sensorRequestsArrivalInterval = sensorRequestsArrivalInterval;
		this.urgentMessageRate = urgentMessageRate;
		this.sensorRequestPatienceTime = sensorRequestPatienceTime;
		this.approach =approach;
		
		this.minSecretValue = minSecretValue;
		this.maxSecretValue = maxSecretValue;
		this.qy0Interval = qy0Interval;
		this.minNumberOfAttibutes = minNumberOfAttibutes;
		this.maxNumberOfAttibutes = maxNumberOfAttibutes;
		this.isRandomSelectionOfAN  = isRandomSelectionOfAN;
		this.isRandomSelectionOfSensors = isRandomSelectionOfSensors;
		this.printDetails=printDetails;
		this.used_cpu_models = used_cpu_models;
		if(isRandomSelectionOfAN) {
			selectedAssistantNodeConfigs = SimulationUtility.generateRandomAssistantNodeConfigs(assistantNodeCount,used_cpu_models);
		}
		else {
			selectedAssistantNodeConfigs = SimulationUtility.selectSetOfAssistantNodeConfigs(assistantNodeCount,used_cpu_models);
		}
		
		sensors = SimulationUtility.generateRandomSensors(sensorCount, minSecretValue, maxSecretValue, qy0Interval, minNumberOfAttibutes, maxNumberOfAttibutes);


	}

	public int getSimulationDuration() {
		return simulationDuration;
	}

	public UsedApproach getApproach() {
		return approach;
	}

	public int getWaitTime() {
		return waitTime;
	}

	public int getAssistantNodeCount() {
		return assistantNodeCount;
	}

	public int getSensorCount() {
		return sensorCount;
	}

	
	public int getSensorRequestsArrivalInterval() {
		return sensorRequestsArrivalInterval;
	}

	public double getUrgentMessageRate() {
		return urgentMessageRate;
	}

	public int getSensorRequestPatienceTime() {
		return sensorRequestPatienceTime;
	}

	public int getMinSecretValue() {
		return minSecretValue;
	}

	public int getMaxSecretValue() {
		return maxSecretValue;
	}

	public int getQy0Interval() {
		return qy0Interval;
	}

	public int getMinNumberOfAttibutes() {
		return minNumberOfAttibutes;
	}

	public int getMaxNumberOfAttibutes() {
		return maxNumberOfAttibutes;
	}

	public List<AssistantNodeConfig> getSelectedAssistantNodeConfigs() {
		return selectedAssistantNodeConfigs;
	}

	public List<AbstractSensor> getSensors() {
		return sensors;
	}

	public void setSensorRequestPatienceTime(int sensorRequestPatienceTime) {
		this.sensorRequestPatienceTime = sensorRequestPatienceTime;
	}

	public boolean isRandomSelectionOfSensors() {
		return isRandomSelectionOfSensors;
	}

	public boolean isPrintDetails() {
		return printDetails;
	}

	public int getFirstSensorRequestArrival() {
		return firstSensorRequestArrival;
	}
}

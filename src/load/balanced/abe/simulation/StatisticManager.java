package load.balanced.abe.simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import load.balanced.abe.sensors.AbstractSensorSRequest;

/**
 * This class regroup statistic information. It calculates the simulation
 * result.
 */
public class StatisticManager {

	private int totalNumberofSensorSRequests = 0;
	
	private int totalNumberofUrgentSensorRequests = 0;
	private int effectiveSimulationDuration;

	private ArrayList<AbstractSensorSRequest> servedSensorSRequests ;
	private ArrayList<AbstractSensorSRequest> servedUrgentSensorRequests ;

	private ArrayList<AbstractSensorSRequest> nonServedSensorSRequests ;
	public ArrayList<AbstractSensorSRequest> nonServedUrgentSensorRequests ;

	private int totalEstimatedTimeforRM = 0;
	private int totalEstimatedTimeforUM = 0;

	
	public StatisticManager() {

		servedSensorSRequests = new ArrayList<AbstractSensorSRequest>();
		servedUrgentSensorRequests = new ArrayList<AbstractSensorSRequest>();
		nonServedSensorSRequests = new ArrayList<AbstractSensorSRequest>();
		nonServedUrgentSensorRequests = new ArrayList<AbstractSensorSRequest>();
	}
	/**
	 * Effective simulation duration.
	 */
	private int simulationDuration = 0;

	/**
	 * Time unit count of occupied assistant nodes. For each unit time, if we have
	 * an occupied assistant node, we get this count + 1.
	 */
	private int occupiedAssistantNode = 0;

	private int occupiedResource = 0;

	public void registerServedSensorSRequest(AbstractSensorSRequest sensorRequest) {
		if (sensorRequest.getAssignedResources().isEmpty()) {

			sensorRequest.setServed(true);
			servedSensorSRequests.add(sensorRequest);
		}
	}

	public void registerServedUrgentSensorRequest(AbstractSensorSRequest sensorRequest) {
		if (sensorRequest.getAssignedResources().isEmpty()) {

			sensorRequest.setServed(true);
			servedUrgentSensorRequests.add(sensorRequest);
		}
	}

	public int getEffectiveSimulationDuration() {
		return effectiveSimulationDuration;
	}

	public void setEffectiveSimulationDuration(int effectiveSimulationDuration) {
		this.effectiveSimulationDuration = effectiveSimulationDuration;
	}

	public void increaseNumberOfArrivedSensorSRequests() {
		totalNumberofSensorSRequests = getTotalNumberofSensorSRequests() + 1;
	}

	public void increaseNumberofUrgentSensorRequests() {
		totalNumberofUrgentSensorRequests++;
	}

	public void registerNonServedSensorSRequest(AbstractSensorSRequest sensorRequest) {
		if (sensorRequest.getAssignedResources().isEmpty()) {

			nonServedSensorSRequests.add(sensorRequest);
		}
	}

	public void registerNonServedUrgentSensorSRequest(AbstractSensorSRequest sensorRequest) {
		if (sensorRequest.getAssignedResources().isEmpty()) {

			if(!nonServedUrgentSensorRequests.contains(sensorRequest)) {
				
				nonServedUrgentSensorRequests.add(sensorRequest);
			}
		}
	}

	public void simulationDurationRecord() {
		simulationDuration++;
	}

	public void AssistantNodeOccupationRecord() {
		occupiedAssistantNode++;
	}

	public void ResourceOccupationRecord() {
		occupiedResource++;
	}

	/**
	 * Calculates the average occupation percentage per Assistant Node.
	 * 
	 * @param assistantNodeCount Assistant Node count
	 * @return occupation percentage
	 */
	public double calculateAverageAssistantNodeOccupationRate(int assistantNodeCount, int simulationDuration) {
		/*System.out.println("(" + occupiedAssistantNode + "*" + 100 + "/" + simulationDuration + " )/"
				+ assistantNodeCount);
				*/
		
        if(simulationDuration>0) {
		return (occupiedAssistantNode * 100 / simulationDuration) / assistantNodeCount;
        }
        else {
        	return 100;
        }
		
	}

	public double calculateAverageResourceOccupationRate(int resourceCount, int simulationDuration) {
		/*System.out.println(
				"(resources :" + occupiedResource + "*" + 100 + "/" + simulationDuration + " )/" + resourceCount);
		 */
		if(simulationDuration>0) {
		return (occupiedResource * 100 / simulationDuration) / resourceCount;
		}
		else {
			return 100;
		}
	}

	/**
	 * Calculates the average waiting time per regular sensor Request.
	 * 
	 * @return average waiting time
	 */
	public double calculateAverageSensorSRequestWaitingTime() {
		int totalWaitingTime = 0;
		for (AbstractSensorSRequest sensorRequest : servedSensorSRequests) {
			int encryptionStartTime = sensorRequest.getEncryptionStartTime();
			int arrivalTime = sensorRequest.getArrivalTime();
			totalWaitingTime += encryptionStartTime - arrivalTime;
		}

		int servedSensorSRequestCount = servedSensorSRequestCount();
		if (servedSensorSRequestCount > 0)
			return totalWaitingTime / servedSensorSRequestCount;
		else
			return totalWaitingTime;
	}

	/**
	 * Calculates the Median waiting time per regular sensor Request.
	 * 
	 * @return Median waiting time
	 */
	public double calculateMedianSensorSRequestWaitingTime() {
		int totalWaitingTime = 0;
		List<Double> listWaitingTime = new ArrayList<Double>();
		for (AbstractSensorSRequest sensorRequest : servedSensorSRequests) {
			int encryptionStartTime = sensorRequest.getEncryptionStartTime();
			int arrivalTime = sensorRequest.getArrivalTime();
			int duration = encryptionStartTime - arrivalTime;
			totalWaitingTime += duration;
			listWaitingTime.add((double) duration);
		}

		int servedSensorSRequestCount = servedSensorSRequestCount();
		if (servedSensorSRequestCount > 0)
			return SimulationUtility.median(listWaitingTime);

		else
			return totalWaitingTime;
	}

	/**
	 * Calculates the average waiting time per urgent sensor Request.
	 * 
	 * @return average waiting time
	 */
	public double calculateAverageUrgentSensorRequestWaitingTime() {
		int totalWaitingTime = 0;
		for (AbstractSensorSRequest sensorRequest : servedUrgentSensorRequests) {
			int encryptionStartTime = sensorRequest.getEncryptionStartTime();
			int arrivalTime = sensorRequest.getArrivalTime();
			totalWaitingTime += encryptionStartTime - arrivalTime;
		}
		int servedUrgentSensorSRequestCount = servedUrgentSensorRequestCount();
		if (servedUrgentSensorSRequestCount > 0)
			return totalWaitingTime / servedUrgentSensorSRequestCount;
		else
			return totalWaitingTime;
	}

	/**
	 * Calculates the Median waiting time per urgent sensor Request. TODO
	 * 
	 * @return Median waiting time
	 */
	public double calculateMedianUrgentSensorRequestWaitingTime() {
		int totalWaitingTime = 0;
		List<Double> listWaitingTime = new ArrayList<Double>();

		for (AbstractSensorSRequest sensorRequest : servedUrgentSensorRequests) {
			int encryptionStartTime = sensorRequest.getEncryptionStartTime();
			int arrivalTime = sensorRequest.getArrivalTime();
			int duration = encryptionStartTime - arrivalTime;

			totalWaitingTime += duration;
			listWaitingTime.add((double) duration);

		}
		int servedUrgentSensorSRequestCount = servedUrgentSensorRequestCount();
		if (servedUrgentSensorSRequestCount > 0)
			return SimulationUtility.median(listWaitingTime);
		else
			return totalWaitingTime;
	}

	/**
	 * Calculates the average effective encryption time per regular sensor Request.
	 * 
	 * @return average encryption time
	 */
	public double calculateAverageSensorRequestEncryptionTime() {
		double totalEncryptionTime = 0;
		for (AbstractSensorSRequest sensorRequest : servedSensorSRequests) {
			int encryptionFinishTime = sensorRequest.getEncyptionFinishTime();
			int encryptionStartTime = sensorRequest.getEncryptionStartTime();
			totalEncryptionTime += encryptionFinishTime - encryptionStartTime;
		}
		if (servedSensorSRequestCount() > 0) {
			return totalEncryptionTime / servedSensorSRequestCount();
		} else {
			return totalEncryptionTime;
		}
	}

	/**
	 * get total Encryption Time for all Regular Messages.
	 * 
	 * @return total Encryption Time
	 */
	public double getEncryptionTime4RegularMessages() {
		double totalEncryptionTime = 0;
		
		for (AbstractSensorSRequest sensorRequest : servedSensorSRequests) {
			int encryptionFinishTime = sensorRequest.getEncyptionFinishTime();
			int encryptionStartTime = sensorRequest.getEncryptionStartTime();
			int encryptionTime = encryptionFinishTime - encryptionStartTime;
			totalEncryptionTime += encryptionTime;
		}
		return totalEncryptionTime;
	}

	/**
	 * get total Encryption Time for all Regular Messages.
	 * 
	 * @return total Encryption Time
	 */
	public double getEncryptionTime4UrgentMessages() {
		double totalEncryptionTime = 0;
		

		for (AbstractSensorSRequest sensorRequest : servedUrgentSensorRequests) {
			int encryptionFinishTime = sensorRequest.getEncyptionFinishTime();
			int encryptionStartTime = sensorRequest.getEncryptionStartTime();
			int encryptionTime = encryptionFinishTime - encryptionStartTime;
			totalEncryptionTime += encryptionTime;
		}
		return totalEncryptionTime;
	}

	/**
	 * Calculates the Median effective encryption time per regular sensor Request.
	 * 
	 * @return Median encryption time
	 */
	public double calculateMedianSensorRequestEncryptionTime() {
		double totalEncryptionTime = 0;
		List<Double> listTotalEncryptionTime = new ArrayList<Double>();
 //System.out.println("  "  );
		for (AbstractSensorSRequest sensorRequest : servedSensorSRequests) {
			int encryptionFinishTime = sensorRequest.getEncyptionFinishTime();
			int encryptionStartTime = sensorRequest.getEncryptionStartTime();
			int duration = encryptionFinishTime - encryptionStartTime;
			
			int cumulativeDuration = encryptionFinishTime - sensorRequest.getArrivalTime();
		//	System.out.println("duration : "+duration);
			totalEncryptionTime += duration;
			listTotalEncryptionTime.add((double) duration);
			
		}
		if (servedSensorSRequestCount() > 0) {
			return SimulationUtility.median(listTotalEncryptionTime);
		} else {
			return totalEncryptionTime;
		}
	}

	
	/**
	 * Calculates the Median effective cumulative encryption time per regular sensor Request.
	 * 
	 * @return Median encryption time
	 */
	public double calculateMedianSReqCumulativeEncryptionTime() {
		double totalEncryptionTime = 0;
		List<Double> listTotalCumulativeEncryptionTime = new ArrayList<Double>();
 //System.out.println("  "  );
		for (AbstractSensorSRequest sensorRequest : servedSensorSRequests) {
			int encryptionFinishTime = sensorRequest.getEncyptionFinishTime();
			int cumulativeDuration = encryptionFinishTime - sensorRequest.getArrivalTime();
		//	System.out.println("duration : "+duration);
			totalEncryptionTime += cumulativeDuration;
			listTotalCumulativeEncryptionTime.add((double) cumulativeDuration);
		}
		if (servedSensorSRequestCount() > 0) {
			return SimulationUtility.median(listTotalCumulativeEncryptionTime);
		} else {
			return totalEncryptionTime;
		}
	}
	/**
	 * Calculates the Median effective cumulative encryption time per urgent sensorRequest.
	 * 
	 * @return Median encryption time
	 */
	public double calculateMedianUrgentSReqCumulativeEncryptionTime() {
		double totalEncryptionTime = 0;
		List<Double> listTotalCumulativeEncryptionTime = new ArrayList<Double>();
 //System.out.println("  "  );
		for (AbstractSensorSRequest sensorRequest : servedUrgentSensorRequests) {
			int encryptionFinishTime = sensorRequest.getEncyptionFinishTime();
			int cumulativeDuration = encryptionFinishTime - sensorRequest.getArrivalTime();
		//	System.out.println("duration : "+duration);
			totalEncryptionTime += cumulativeDuration;
			listTotalCumulativeEncryptionTime.add((double) cumulativeDuration);
		}
		if (servedUrgentSensorRequestCount() > 0) {
			return SimulationUtility.median(listTotalCumulativeEncryptionTime);
		} else {
			return totalEncryptionTime;
		}
	}
	
	/**
	 * Calculates the Median effective encryption time per urgent sensorRequest.
	 * 
	 * @return Median encryption time
	 */
	public double calculateMedianUrgentSensorRequestEncryptionTime() {
		double totalEncryptionTime = 0;
		List<Double> listTotalEncryptionTime = new ArrayList<Double>();

		for (AbstractSensorSRequest sensorRequest : servedUrgentSensorRequests) {
			int encryptionFinishTime = sensorRequest.getEncyptionFinishTime();
			int encryptionStartTime = sensorRequest.getEncryptionStartTime();
			int duration = encryptionFinishTime - encryptionStartTime;
			totalEncryptionTime += duration;
			listTotalEncryptionTime.add((double) duration);

		}
		if (servedUrgentSensorRequestCount() > 0) {
			return SimulationUtility.median(listTotalEncryptionTime);
		} else {
			return totalEncryptionTime;
		}
	}

	/**
	 * Calculates the Average effective encryption time per urgent sensorRequest.
	 * 
	 * @return Average encryption time
	 */
	public double calculateAverageUrgentSensorRequestEncryptionTime() {
		double totalEncryptionTime = 0;
		for (AbstractSensorSRequest sensorRequest : servedUrgentSensorRequests) {
			int encryptionFinishTime = sensorRequest.getEncyptionFinishTime();
			int encryptionStartTime = sensorRequest.getEncryptionStartTime();
			totalEncryptionTime += encryptionFinishTime - encryptionStartTime;
		}
		if (servedUrgentSensorRequestCount() > 0) {
			return totalEncryptionTime / servedUrgentSensorRequestCount();
		} else {
			return totalEncryptionTime;
		}
	}

	public int servedSensorSRequestCount() {
		return servedSensorSRequests.size();
	}

	public int servedUrgentSensorRequestCount() {
		return servedUrgentSensorRequests.size();
	}

	public int getTotalEstimatedTimeforRM() {
		return totalEstimatedTimeforRM;
	}

	public int getTotalEstimatedTimeforUM() {
		return totalEstimatedTimeforUM;
	}

	public void increaseTotalEstimatedTimeforRM(int totalEstimatedTimeforRM) {
		this.totalEstimatedTimeforRM = this.totalEstimatedTimeforRM+totalEstimatedTimeforRM;
	}

	public void increaseTotalEstimatedTimeforUM(int totalEstimatedTimeforUM) {
		this.totalEstimatedTimeforUM = this.totalEstimatedTimeforUM+totalEstimatedTimeforUM;
	}

	public int nonServedSensorRequestCount() {
		return totalNumberofSensorSRequests-servedSensorSRequests.size();

		//return nonServedSensorSRequests.size();
	}

	public int nonServedUrgentSensorRequestCount() {

		return totalNumberofUrgentSensorRequests-servedUrgentSensorRequests.size();
	}

	public int getTotalNumberofSensorSRequests() {
		return totalNumberofSensorSRequests;
	}

	public int getTotalNumberofUrgentSensorRequests() {
		return totalNumberofUrgentSensorRequests;
	}

	/**
	 * Calculates the rate of served SensorRequest among all Sensor Requests. This
	 * indicates the sensor satisfaction.
	 * 
	 * @return the rate of served Sensor Request
	 */
	public double calculateSensorRequestSatisfactionRate() {
		// return servedSensorSRequestCount() * 100 / (servedSensorSRequestCount() +
		// nonServedSensorRequestCount());
		
		if((servedSensorSRequestCount() + servedUrgentSensorRequestCount() + nonServedSensorRequestCount()
			+ nonServedUrgentSensorRequestCount())>0) {
		double sensorRequestSatisfactionRate = ((servedSensorSRequestCount() + servedUrgentSensorRequestCount()) * 100)
	/ (servedSensorSRequestCount() + servedUrgentSensorRequestCount() + nonServedSensorRequestCount()
			+ nonServedUrgentSensorRequestCount());
		return sensorRequestSatisfactionRate;
		}
		return 0.0;
	}

	
	/**
	 * Calculates the rate of served SensorRequest among all Sensor Requests. This
	 * indicates the sensor satisfaction.
	 * 
	 * @return the rate of served Sensor Request
	 */
	public double calculateSensorUrgentRequestSatisfactionRate() {
		
		// return servedSensorSRequestCount() * 100 / (servedSensorSRequestCount() +
		// nonServedSensorRequestCount());
		
		if((servedSensorSRequestCount() + servedUrgentSensorRequestCount() + nonServedSensorRequestCount()
			+ nonServedUrgentSensorRequestCount())>0) {
		double sensorRequestSatisfactionRate = (servedUrgentSensorRequestCount() * 100)
	/ (servedUrgentSensorRequestCount() + nonServedUrgentSensorRequestCount());
		return sensorRequestSatisfactionRate;
		}
		return 0.0;
	}
	
	public ArrayList<AbstractSensorSRequest> getNonServedSensorSRequests() {
		return nonServedSensorSRequests;
	}

	public ArrayList<AbstractSensorSRequest> getNonServedUrgentSensorRequests() {
		return nonServedUrgentSensorRequests;
	}

	public double getLastFinishTime4RM() {

		double lastFinishTime = 0;
		for (AbstractSensorSRequest sensorRequest : servedSensorSRequests) {
			if(lastFinishTime<sensorRequest.getEncyptionFinishTime()) {
				lastFinishTime = sensorRequest.getEncyptionFinishTime();
			}
			
		}
		return lastFinishTime;
		
	}
	
	public double getLastFinishTime4UM() {

		double lastFinishTime = 0;
		for (AbstractSensorSRequest sensorRequest : servedUrgentSensorRequests) {
			if(lastFinishTime<sensorRequest.getEncyptionFinishTime()) {
				lastFinishTime = sensorRequest.getEncyptionFinishTime();
			}
			
		}
		return lastFinishTime;
		
	}

}

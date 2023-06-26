package load.balanced.abe.sensors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import load.balanced.abe.coordinator.Coordinator;
import load.balanced.abe.coordinator.Resource;
import load.balanced.abe.simulation.SavedSubWorkloads;
import load.balanced.abe.simulation.Simulation;
import load.balanced.abe.simulation.SimulationUtility;
import load.balanced.abe.simulation.StatisticManager;
import load.balanced.abe.simulation.UsedApproach;
import load.balanced.abe.simulation.System;

public class AbstractSensor {

	private int ID =0;
//	private int workload;
	private int minNumberOfAttibutes;
	private int maxNumberOfAttibutes;

	int minSecretValue ;
	int maxSecretValue;
	int qy0Interval;
	

	//private Map<AbstractSensorSRequest, AbstractWorkload> mapRequestWorkloadList = new HashMap<AbstractSensorSRequest, AbstractWorkload>();
	private LinkedList<AbstractSensorSRequest> sensorScheduleRequests = new LinkedList<AbstractSensorSRequest>();

	
	public AbstractSensor(int i,int minSecretValue,int maxSecretValue, int minNumberOfAttibutes, int maxNumberOfAttibutes, int qy0Interval) {
		ID = i;
		this.minSecretValue =minSecretValue;
		this.maxSecretValue =maxSecretValue;
		this.qy0Interval=qy0Interval;
		this.minNumberOfAttibutes =minNumberOfAttibutes;
		this.maxNumberOfAttibutes =maxNumberOfAttibutes;

		
	}
	public LinkedList<AbstractSensorSRequest> getSensorScheduleRequests() {
		return sensorScheduleRequests;
	}
	public int getID() {
		return ID;
	}
	
	
	
	public String toString() {
		return "Sensor_"+ID; 
	}
	

	/***
	 * Begins the encryption for a Sensor Request.
	 * 
	 * 
	 * @param sensorRequest
	 * @param currentSystemTime 
	 * @param coordinator 
	 * @param splitApproach 
	 * @return 
	 */
	public boolean treatSensorRequest(AbstractSensorSRequest sensorRequest, int currentSystemTime, Coordinator coordinator) {

		boolean threated =false;
		if(!sensorRequest.getAssignedResources().isEmpty() && sensorRequest.getAssignedResources().size()>=1) {
			// If it not divisible, we make some resources
			if(!sensorRequest.getMessage().getWorkload().isDivisible(sensorRequest.getAssignedResources().size())) {
				int nbrNeedResources = sensorRequest.getMessage().getWorkload().getNbrNeedResources();
				
				Iterator<Resource> itr = sensorRequest.getAssignedResources().iterator();
				int i=0;
				while(itr.hasNext()){
					Resource resource = itr.next();
					if(i>=nbrNeedResources-1) {
						itr.remove();

					}
					i++;
				}
				threated =true;
			//	System.out.println("nbr need Resources"+nbrNeedResources);
			//	System.out.println("nbr assigned Resources"+sensorRequest.getAssignedResources().size());
			}
			
			
			for (Resource resource : sensorRequest.getAssignedResources()) {
				resource.setServingSensor(sensorRequest.getSensor());
				resource.setServingSensorRequest(sensorRequest);
				sensorRequest.setEncryptionStartTime(currentSystemTime);
			}
			 
			
			 
			/* if(Simulation.areSavedSubWorkloads) {
				//	System.out.println("yes it is saved here");
				// sensorRequest.getMessage().getWorkload().getSubWorkloads().addLast(Simulation.savedSubWorkloads.get(0));
					
			 
			 }*/
			
			  List<AbstractWorkload> subWorkLoads = splitWorkload(sensorRequest, coordinator);


			int r_j=0;
			//System.out.println("Coordinator "+coordinator.getID()+", Estimate execution Time: ");
			
			int totalETimePerWorkload = 0;
			for (Resource resource : sensorRequest.getAssignedResources()) {
				
					int totalWorkload = subWorkLoads.get(r_j).getWorkloadToCompute();
					
					int executionTime = (int) estimateTime(resource, totalWorkload);
					resource.setRemainingEncryptionTime(executionTime);

			//		System.out.println("Estimated Encryption Time for Resource "+r_j+"("+resource.getCoreWeight()+")"+ " (SubWorkload = "+totalWorkload+") is : "+executionTime);
		
					if(totalETimePerWorkload<executionTime) {
						totalETimePerWorkload = executionTime; 
					}
					subWorkLoads.get(r_j).setEstimatedExecutionTime(executionTime);
				//	totalETimePerWorkload  = totalETimePerWorkload + executionTime;

					r_j++;
			}
			sensorRequest.getMessage().getWorkload().setEstimatedExecutionTime(totalETimePerWorkload);
			int waitingTimeinQueue = currentSystemTime - sensorRequest.getArrivalTime();
			//System.out.println("waiting Time in Queue"+waitingTimeinQueue );
			sensorRequest.getMessage().getWorkload().setEstimatedCumulativeExecutionTime(totalETimePerWorkload+waitingTimeinQueue);

		//	System.out.println("Sensor "+sensorRequest.getSensor().getID()+",\n Estimated Total Encryption Time for Workload: "+sensorRequest.getMessage().getWorkload().toString()+" is : "+totalETimePerWorkload);

		//	System.out.println("Start Encrypting");
		}
		else {
		//	System.out.println("no assigned resources for sensor : "+sensorRequest.getSensor().getID());
			threated=false;
		}
		return threated;
		
		
		
	}
	private List<AbstractWorkload> splitWorkload(AbstractSensorSRequest sensorRequest, Coordinator coordinator) {
		
		List<AbstractWorkload> subWorkLoads =null;
		if(sensorRequest.getMessage().getWorkload().getSecretValue()>= sensorRequest.getAssignedResources().size()) {
			if(coordinator.getApproach()==UsedApproach.GasmiApproach) {
				 
			    
				 subWorkLoads=  SimulationUtility.split2NotEqualPartsWithGasmiApproach(sensorRequest.getMessage().getWorkload(), sensorRequest.getAssignedResources());
	//		 subWorkLoads=  SimulationUtility.splitWorkloadRandomlyWithGasmiApp1(sensorRequest.getMessage().getWorkload(), sensorRequest.getAssignedResources());
	   
			//	 subWorkLoads=  SimulationUtility.splitWorkloadBOnResoucesWeights(sensorRequest.getMessage().getWorkload(), sensorRequest.getAssignedResources());
	
				
			 //  subSecretValue = SimulationUtility.splitValueRandomly(getMapRequestWorkloadList().get(sensorRequest).getSecretValue(), sensorRequest.getAssignedResources().size());
			 }
			 else if(coordinator.getApproach()==UsedApproach.RandomSplitApproach1) {
				 subWorkLoads=  SimulationUtility.split2NotEqualPartsWithRandomAppr1(sensorRequest.getMessage().getWorkload(), sensorRequest.getAssignedResources());
	
			 }
			 else if(coordinator.getApproach()==UsedApproach.RandomSplitApproach2) {
				 subWorkLoads=  SimulationUtility.split2NotEqualPartsWithRandomAppr2(sensorRequest.getMessage().getWorkload(), sensorRequest.getAssignedResources());
	
			 }
			 else if(coordinator.getApproach()==UsedApproach.TouatiApproach) {
				 subWorkLoads=SimulationUtility.split2EqualPartWithTouatiApp(sensorRequest.getMessage().getWorkload(), sensorRequest.getAssignedResources().size());
			 }
	
	// subWorkLoads = sensorRequest.getMessage().getWorkload().getSubWorkloads();
			}else {
				java.lang.System.err.println("Number of resources is greater than workload");
			}
		return subWorkLoads;
	}
	private double estimateTime(Resource resource, int totalWorkload) {
		double coreWeight = resource.getCoreWeight();
		double executionTime =1;
		if(totalWorkload>coreWeight) {
			executionTime = totalWorkload/coreWeight;
		}
		return executionTime;
	}
	
	/*public Map<AbstractSensorSRequest, AbstractWorkload> getMapRequestWorkloadList() {
		return mapRequestWorkloadList;
	}*/
	public AbstractSensorSRequest generateSRequest(int currentSystemTime, AbstractMessage message, AbstractSensor sensor, int requestPatienceTime) {

		AbstractSensorSRequest sensorSRequest = new SensorRequest(currentSystemTime, message, sensor,requestPatienceTime);
		getSensorScheduleRequests().addLast(sensorSRequest);
	//	AbstractWorkload workload = generateRandomlyWorkload();
	//	sensorSRequest.getMessage().setWorkload(workload);
		return sensorSRequest;
	}
	
	
}

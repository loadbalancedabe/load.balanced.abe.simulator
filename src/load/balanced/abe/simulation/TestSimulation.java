package load.balanced.abe.simulation;

import java.util.LinkedList;
import java.util.List;

import load.balanced.abe.coordinator.AssistantNodeConfig;
import load.balanced.abe.coordinator.Coordinator;

public class TestSimulation {
	// Unit of Time is nanosecond				   
	private static final int SIMULATION_DURATION =20000000;// 90000000;
	
	private static final int ASSISTANT_NODE_COUNT =3;
	private static final int SENSOR_COUNT =9;
	private static final int FIRST_SENSOR_REQUEST_ARRIVAL =500000;
	private static final int SENSOR_REQUEST_ARRIVAL_INTERVAL =70000000;
	
	private static final int WAIT_TIME =SENSOR_COUNT+1;
	private static final double URGENT_MESSAGE_RATE =0.0000002;
	private static final int SENSOR_REQUEST_PATIENCE_TIME =5000000;

	private static final int MIN_SECRET_VALUE  =9810780;// 1000000000;//9810780;//10000000;
	private  static final int MAX_SECRET_VALUE =9810780;// 1000000000; 9810780
	private  static final int Qy0_INTERVAL  =50 ;  //Min_Qy0 = SECRET_VALUE,   Max_Qy0 <= (SECRET_VALUE+Qy0_INTERVAL)
	private  static final int MIN_NUMBER_ATTIBUTES  =1;
	private  static final int MAX_NUMBER_ATTIBUTES =1;
	
	private static final boolean IS_RANDOM_SELECTION_OF_AN = false;
	private static final boolean IS_RANDOM_SELECTION_OF_SENSORS= false;
	private static final boolean PRINT_DETAILS=false;
	private static final InputAssistantNodes used_cpu_models=InputAssistantNodes.different_cpu_models;


	public static void main(String[] args) {
		
		SimulationEntry simulationEntry = new SimulationEntry(SIMULATION_DURATION, WAIT_TIME, ASSISTANT_NODE_COUNT, SENSOR_COUNT,FIRST_SENSOR_REQUEST_ARRIVAL, SENSOR_REQUEST_ARRIVAL_INTERVAL, URGENT_MESSAGE_RATE, SENSOR_REQUEST_PATIENCE_TIME,  MIN_SECRET_VALUE, MAX_SECRET_VALUE, Qy0_INTERVAL, MIN_NUMBER_ATTIBUTES, MAX_NUMBER_ATTIBUTES,IS_RANDOM_SELECTION_OF_AN,IS_RANDOM_SELECTION_OF_SENSORS,PRINT_DETAILS,used_cpu_models);
		Simulation simulation = new Simulation(simulationEntry);
		
		/*
		simulation.simulateWithoutComparing();
		System.out.println(simulation.simulationResults());
		 */	
		/// compare 
		//SimulationEntry simulationEntry = new SimulationEntry(SIMULATION_DURATION, WAIT_TIME, ASSISTANT_NODE_COUNT, SENSOR_COUNT, SENSOR_REQUEST_ARRIVAL_INTERVAL, PRIORITY_SENSOR_RATE, SENSOR_REQUEST_PATIENCE_TIME, APPROACH, MIN_SECRET_VALUE, MAX_SECRET_VALUE, Qy0_INTERVAL, MIN_NUMBER_ATTIBUTES, MAX_NUMBER_ATTIBUTES);
		
		LinkedList<Coordinator> coordinators  = new LinkedList<Coordinator>();
		LinkedList<StatisticManager> statisticManagers  = new LinkedList<StatisticManager>();
		 List<AssistantNodeConfig> selectedAssistantNodeConfigs = simulationEntry.getSelectedAssistantNodeConfigs();
		 // Gasmi approach 
		 Coordinator coordinator = new Coordinator(selectedAssistantNodeConfigs);//, assistantNodeCount, sensorCount, minSecretValue, maxSecretValue, qy0Interval, minNumberOfAttibutes, maxNumberOfAttibutes);
		 	coordinator.setID(0);
		 	coordinator.setDescription("Gasmi Approach");
			coordinator.sortAssistantNodes();
			coordinator.setSensors(simulationEntry.getSensors());
			coordinator.setApproach(UsedApproach.GasmiApproach);
			StatisticManager	statisticManager = new StatisticManager();
			coordinator.setStatisticManager(statisticManager);

			coordinators.addLast(coordinator);
			statisticManagers.addLast(statisticManager);
			// using Random algorithm 1 to split with sorting assistant nodes based on their weights
			 Coordinator coordinator1 = new Coordinator(selectedAssistantNodeConfigs);//, assistantNodeCount, sensorCount, minSecretValue, maxSecretValue, qy0Interval, minNumberOfAttibutes, maxNumberOfAttibutes);
			 	coordinator1.setID(1);
			 	coordinator1.setDescription("Random algo 1 to split + sorting assistant nodes based on their weights");

				coordinator1.sortAssistantNodes();
				coordinator1.setSensors(simulationEntry.getSensors());
				coordinator1.setApproach(UsedApproach.RandomSplitApproach1);
				StatisticManager	statisticManager1 = new StatisticManager();
				coordinator1.setStatisticManager(statisticManager1);

				coordinators.addLast(coordinator1);
				statisticManagers.addLast(statisticManager1);

				// using Random algorithm 1 to split without sorting assistant nodes 
				 Coordinator coordinator2 = new Coordinator(selectedAssistantNodeConfigs);//, assistantNodeCount, sensorCount, minSecretValue, maxSecretValue, qy0Interval, minNumberOfAttibutes, maxNumberOfAttibutes);
				 	coordinator2.setID(2);
				 	coordinator2.setDescription("Random algo 1 to split without sorting assistant nodes ");
					coordinator2.setSensors(simulationEntry.getSensors());
					coordinator2.setApproach(UsedApproach.RandomSplitApproach1);
					StatisticManager	statisticManager2 = new StatisticManager();
					coordinator2.setStatisticManager(statisticManager2);

					coordinators.addLast(coordinator2);
					statisticManagers.addLast(statisticManager2);

				
			/*	// Using Random algorithm 2 to split with sorting assistant nodes based on their weights
				 Coordinator coordinator2 = new Coordinator(selectedAssistantNodeConfigs);//, assistantNodeCount, sensorCount, minSecretValue, maxSecretValue, qy0Interval, minNumberOfAttibutes, maxNumberOfAttibutes);
				 	coordinator2.setID(2);
				 	coordinator2.setDescription("Random algo 2 to split + sorting assistant nodes based on their weights");

					coordinator2.sortAssistantNodes();
					coordinator2.setSensors(simulationEntry.getSensors());
					coordinator2.setApproach(UsedApproach.RandomSplitApproach2);
					StatisticManager	statisticManager2 = new StatisticManager();
					coordinators.addLast(coordinator2);
					statisticManagers.addLast(statisticManager2);
			*/		
				// Touati approach without sorting	
			Coordinator coordinator3 = new Coordinator(selectedAssistantNodeConfigs);//, assistantNodeCount, sensorCount, minSecretValue, maxSecretValue, qy0Interval, minNumberOfAttibutes, maxNumberOfAttibutes);
		 	coordinator3.setID(3);
		 	coordinator3.setDescription("Touati approach Without sorting  assistant nodes");

			//coordinator3.sortAssistantNodes();
			coordinator3.setSensors(simulationEntry.getSensors());
			coordinator3.setApproach(UsedApproach.TouatiApproach);
			StatisticManager	statisticManager3 = new StatisticManager();
			coordinator3.setStatisticManager(statisticManager3);

			coordinators.addLast(coordinator3);
			statisticManagers.addLast(statisticManager3);
			
			//  Touati approach with sorting assistant nodes based on their weights
			Coordinator coordinator4 = new Coordinator(selectedAssistantNodeConfigs);//, assistantNodeCount, sensorCount, minSecretValue, maxSecretValue, qy0Interval, minNumberOfAttibutes, maxNumberOfAttibutes);
		 	coordinator4.setID(4);
		 	coordinator4.setDescription("Touati approach with sorting  assistant nodes based on their weights");

			coordinator4.sortAssistantNodes();
			coordinator4.setSensors(simulationEntry.getSensors());
			coordinator4.setApproach(UsedApproach.TouatiApproach);
			StatisticManager	statisticManager4 = new StatisticManager();
			coordinator4.setStatisticManager(statisticManager4);
			coordinators.addLast(coordinator4);
			statisticManagers.addLast(statisticManager4);
	
			Simulation simulation2 = new Simulation(simulationEntry,coordinators,statisticManagers);
		
		
		
		if(goodConfiguration(simulationEntry)) {
		 simulation2.simulateAndCompareApproches(coordinators,statisticManagers);
		//System.out.println(simulation2.printSimulationEntry());
		
		System.out.println(simulation2.simulationResults(statisticManagers,coordinators));
		simulation2.writeResultsToExcel(statisticManagers, coordinators);
        }
		else {
		 System.out.println("Number of Sensors must be less then number of available resources");
		}
	}
    /***
     * Test is the number of sensors is less than the number of available resources
     * @param simulationEntry
     * @return
     */
	private static boolean goodConfiguration(SimulationEntry simulationEntry) {
		 if(simulationEntry.getSensorCount() <= getAvailableResources(simulationEntry.getSelectedAssistantNodeConfigs())) {

			 return true;
		 }
		return false;
	}
	private static int getAvailableResources(List<AssistantNodeConfig> selectedAssistantNodeConfigs) {
				int nbrResources = 0; 
				for (AssistantNodeConfig assistantNodeConfig : selectedAssistantNodeConfigs) {
					nbrResources +=assistantNodeConfig.getThreads();
				}
				return nbrResources;
	}
}

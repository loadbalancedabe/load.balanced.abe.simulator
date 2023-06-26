package load.balanced.abe.simulation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import load.balanced.abe.coordinator.AssistantNode;
import load.balanced.abe.coordinator.AssistantNodeConfig;
import load.balanced.abe.coordinator.Coordinator;
import load.balanced.abe.coordinator.Queue;
import load.balanced.abe.coordinator.Resource;
import load.balanced.abe.coordinator.RoundRobinAssignment;
import load.balanced.abe.coordinator.UrgentMessRRAssignment;
import load.balanced.abe.sensors.AbstractMessage;
import load.balanced.abe.sensors.AbstractSensor;
import load.balanced.abe.sensors.AbstractSensorSRequest;
import load.balanced.abe.sensors.AbstractWorkload;

/**
 * System simulation.
 */
public class Simulation {
	private boolean printdetails;
	// private Coordinator coordinator;
	private StatisticManager statisticManager;
	private SimulationEntry simulationEntry;

	private List<Coordinator> coordinators = new ArrayList<Coordinator>();
	private List<StatisticManager> statisticManagers = new ArrayList<StatisticManager>();

	// public static boolean areSavedSubWorkloads = false;
//	public static LinkedList<SavedSubWorkloads> savedSubWorkloads = new LinkedList<SavedSubWorkloads>();

	public Simulation(SimulationEntry simulationEntry) {
		this.simulationEntry = simulationEntry;
		int assistantNodeCount = simulationEntry.getAssistantNodeCount();
		int sensorCount = simulationEntry.getSensorCount();

		int minSecretValue = simulationEntry.getMinSecretValue();
		int maxSecretValue = simulationEntry.getMaxSecretValue();
		int qy0Interval = simulationEntry.getQy0Interval();
		int minNumberOfAttibutes = simulationEntry.getMinNumberOfAttibutes();
		int maxNumberOfAttibutes = simulationEntry.getMaxNumberOfAttibutes();

		statisticManager = new StatisticManager();
	}

	public Simulation(SimulationEntry simulationEntry, List<Coordinator> coordinators,
			List<StatisticManager> statisticManagers) {
		this.simulationEntry = simulationEntry;
		this.coordinators = coordinators;
		this.statisticManagers = statisticManagers;
	}

	public void simulateAndCompareApproches(List<Coordinator> coordinators, List<StatisticManager> statisticManagers2) {

		int simulationDuration = simulationEntry.getSimulationDuration();
		System.out.println("start simulating....");
		int sensorNumber = 0;

		int sensorRequestsArrivalInterval = simulationEntry.getSensorRequestsArrivalInterval();

		for (int currentSystemTime = 0; isSatisfied2(currentSystemTime); currentSystemTime++) {

			int coordinatorIndex = 0;

			boolean newSensorRequestsArrival = false;
		int firstSensorRequestArrival = simulationEntry.getFirstSensorRequestArrival();
		//	randomValue = 1000000;

			newSensorRequestsArrival = newSensorRequestsArrival(sensorRequestsArrivalInterval, currentSystemTime,firstSensorRequestArrival);

			for (Coordinator coordinator : coordinators) {
				//StatisticManager statisticManager = statisticManagers.get(coordinatorIndex);
				StatisticManager statisticManager =coordinator.getStatisticManager();
				coordinatorIndex++;
				statisticManager.setEffectiveSimulationDuration(currentSystemTime);

				statisticManager.simulationDurationRecord();
				if (simulationEntry.isPrintDetails()) {
					SimulationUtility.printCoordinatorStat(currentSystemTime, coordinator);
				}
				updateSystemWithCompare(currentSystemTime, coordinator);

				treatUrgentMessagesInUrgentQueue(coordinator, coordinator.getUrgentMessageQueue(), currentSystemTime);

				if (coordinator.getWaitTime() == 0) {
					// time to wait is finished
					treatRegularMessagesInQueue(coordinator, coordinator.getQueue(), currentSystemTime);
					// define a the time to receive new regular messages
					// sensorNumber = 0;

					if (newSensorRequestsArrival) { // Start Waiting for receiving Schedule requests
						coordinator.setWaitTime(simulationEntry.getWaitTime());
						sensorNumber = 0;

					}

				} else {
					coordinator.setWaitTime(coordinator.getWaitTime() - 1);
					// waitTime--;
					if (coordinator.getWaitTime() == 0) {
					//	System.out.println("Finish waiting for Schedule requests");

					}

				}

				if (coordinatorIndex == 1) {
					// areSavedSubWorkloads =true;
				}
			}

			if (simulationEntry.isRandomSelectionOfSensors()) {
				sensorNumber = SimulationUtility.getRandomNumber(1, (simulationEntry.getSensorCount()));
			} else {
				if (sensorNumber < simulationEntry.getSensorCount()) {
					sensorNumber++;
				}
			}
			coordinatorIndex = 0;
			// int messageChoice = SimulationUtility.getRandomNumber(1, 10000000);

			// the sensor has a message to encrypt
			boolean isUrgentMessageArrival = SimulationUtility
					.isUrgentMessageRate(simulationEntry.getUrgentMessageRate());

			// int sensorNumberforUgentMsg = SimulationUtility.getRandomNumber(1,
			// (simulationEntry.getSensorCount()));
			AbstractWorkload generatedWorkload = SimulationUtility.generateRandomlyWorkload(
					simulationEntry.getMinSecretValue(), simulationEntry.getMaxSecretValue(),
					simulationEntry.getMinNumberOfAttibutes(), simulationEntry.getMaxNumberOfAttibutes(),
					simulationEntry.getQy0Interval());

			for (Coordinator coordinator : coordinators) {
				AbstractMessage message = SimulationUtility.getMessageToEncrypt(isUrgentMessageArrival);

				AbstractWorkload workload = generatedWorkload.clone();
				StatisticManager statisticManager = statisticManagers.get(coordinatorIndex);
				coordinatorIndex++;

				if (sensorNumber <= simulationEntry.getSensorCount()
						&& !coordinator.getQueue().containsRequestOfSensor(sensorNumber)) {

					if (coordinator.getApproach() == UsedApproach.GasmiApproach
							|| coordinator.getApproach() == UsedApproach.RandomSplitApproach1
							|| coordinator.getApproach() == UsedApproach.RandomSplitApproach2) {

						if (message.isUrgent()) {
							int sn4UrgentMsg = SimulationUtility.getRandomNumber(1, (simulationEntry.getSensorCount()));
							if (simulationEntry.isPrintDetails()) {
								SimulationUtility.printUrgentMessageArrival(currentSystemTime, sensorNumber);
							}
							if (sn4UrgentMsg <= simulationEntry.getSensorCount()) {

								AbstractSensorSRequest sensorSRequest = receiveResourceAvailabilityRequest(coordinator,
										currentSystemTime, message, sn4UrgentMsg);
								if (sensorSRequest != null) {
									statisticManager.increaseNumberofUrgentSensorRequests();

									message.setWorkload(workload);
									// assign all
									// System.out.println("Gasmi time " + currentSystemTime);
									// System.out.println("Start Resources Assignment for Urgent Message from
									// sensor" + sn4UrgentMsg);
									LinkedList<AssistantNode> freeAssistantNodes = coordinator
											.getAvailableAssistantNodes();

									if (freeAssistantNodes.size() > 0) {
										LinkedList<AbstractSensorSRequest> sensorSRequests = new LinkedList<AbstractSensorSRequest>();
										sensorSRequests.addLast(sensorSRequest);
										UrgentMessRRAssignment roundRobinAssignment = new UrgentMessRRAssignment(
												freeAssistantNodes, sensorSRequests);
										roundRobinAssignment.assignResourcesToSN();
										sensorsSplitWorkloadsForUrgentMessages(sensorSRequest, currentSystemTime,
												coordinator);
									} else {
										coordinator.getUrgentMessageQueue().addQueueLast(sensorSRequest);
									}
								}
							} else {
								java.lang.System.err.println("wrong number of sensor");
							}
						} else if (coordinator.getWaitTime() > 0) {

							AbstractSensorSRequest sensorSRequest = receiveSensorScheduleRequest(currentSystemTime,
									message, sensorNumber, coordinator, statisticManager);
							if (sensorSRequest != null) {
								statisticManager.increaseNumberOfArrivedSensorSRequests();

								// workload =
								// SimulationUtility.generateRandomlyWorkload(simulationEntry.getMinSecretValue(),
								// simulationEntry.getMaxSecretValue(),
								// simulationEntry.getMinNumberOfAttibutes(),
								// simulationEntry.getMaxNumberOfAttibutes(), simulationEntry.getQy0Interval())
								// ;
								message.setWorkload(workload);

								// sensorSRequest.setMessage(message);
								coordinator.getQueue().addQueueLast(sensorSRequest);
								if(printdetails) {
									SimulationUtility.printSensorRequestArrival(currentSystemTime, sensorNumber);
								}
							}

						}
					} else if (coordinator.getApproach() == UsedApproach.TouatiApproach) {

						if (message.isUrgent()) {
							int sn4UrgentMsg = SimulationUtility.getRandomNumber(1, (simulationEntry.getSensorCount()));

							if (simulationEntry.isPrintDetails()) {
								SimulationUtility.printUrgentMessageArrival(currentSystemTime, sensorNumber);
							}

							if (sn4UrgentMsg <= simulationEntry.getSensorCount()) {
								AbstractSensorSRequest sensorSRequest = receiveSensorScheduleRequest(currentSystemTime,
										message, sn4UrgentMsg, coordinator, statisticManager);
								if (sensorSRequest != null) {
									statisticManager.increaseNumberofUrgentSensorRequests();
									message.setWorkload(workload);
									// System.out.println("Touati time " + currentSystemTime);
									// System.out.println("Start Schedule Assignment for Urgent Message from sensor"
									// + sensorNumber);

									coordinator.getQueue().addQueueLast(sensorSRequest);
								}
							} else {
								java.lang.System.err.println("wrong number of sensor");
							}
						} else if (coordinator.getWaitTime() > 0) {

							AbstractSensorSRequest sensorSRequest = receiveSensorScheduleRequest(currentSystemTime,
									message, sensorNumber, coordinator, statisticManager);
							if (sensorSRequest != null) {
								if (!message.isUrgent()) {
									statisticManager.increaseNumberOfArrivedSensorSRequests();
								}
								// workload =
								// SimulationUtility.generateRandomlyWorkload(simulationEntry.getMinSecretValue(),
								// simulationEntry.getMaxSecretValue(),
								// simulationEntry.getMinNumberOfAttibutes(),
								// simulationEntry.getMaxNumberOfAttibutes(), simulationEntry.getQy0Interval())
								// ;
								message.setWorkload(workload);

								// sensorSRequest.setMessage(message);
								coordinator.getQueue().addQueueLast(sensorSRequest);
								if (message.isUrgent()) {
									SimulationUtility.printUrgentMessageArrival(currentSystemTime, sensorNumber);
								} else {
									if(printdetails) {
										SimulationUtility.printSensorRequestArrival(currentSystemTime, sensorNumber);
									}
								}
							}

						}
					}
				}

			}
		}

		// count Non Served Messages
		int coordinatorIndex = 0;
		for (Coordinator coordinator : coordinators) {
			StatisticManager statisticManager = statisticManagers.get(coordinatorIndex);
			coordinatorIndex++;
			statisticManager.setEffectiveSimulationDuration(simulationEntry.getSimulationDuration() + 1);
		//	countNonServedMessages(simulationEntry.getSimulationDuration() + 1, coordinator, statisticManager);
		}

	}

	/*
	 * public void simulateWithoutComparing() { int simulationDuration =
	 * simulationEntry.getSimulationDuration(); System.out.println("start....");
	 * 
	 * int waitTime = 0;
	 * 
	 * int sensorRequestsArrivalInterval =
	 * simulationEntry.getSensorRequestsArrivalInterval();
	 * 
	 * System.out.println("Number of Sensors in the body is : "
	 * +coordinator.getSensors().size()); for (int currentSystemTime = 0;
	 * isSatisfied(currentSystemTime) ; currentSystemTime++) {
	 * 
	 * statisticManager.setEffectiveSimulationDuration(currentSystemTime);
	 * statisticManager.simulationDurationRecord();
	 * SimulationUtility.printCoordinatorStat(currentSystemTime, coordinator);
	 * 
	 * 
	 * updateSystem(currentSystemTime);
	 * 
	 * treatUrgentMessagesInUrgentQueue(coordinator,
	 * coordinator.getUrgentMessageQueue(),currentSystemTime);
	 * 
	 * boolean newSensorRequestsArrival = false; if (waitTime == 0 ) { // time to
	 * wait is finished treatRegularMessagesInQueue(coordinator,
	 * coordinator.getQueue() ,currentSystemTime); // define a the time to receive
	 * new regular messages newSensorRequestsArrival =
	 * newSensorRequestsArrival(sensorRequestsArrivalInterval, currentSystemTime);
	 * if(newSensorRequestsArrival) { waitTime = simulationEntry.getWaitTime(); } }
	 * else { waitTime--; }
	 * 
	 * int sensorNumber = SimulationUtility.getRandomNumber(1,
	 * (simulationEntry.getSensorCount()*2)); if (sensorNumber <=
	 * simulationEntry.getSensorCount() &&
	 * !coordinator.getQueue().containsRequestOfSensor(sensorNumber) ) {
	 * 
	 * // int messageChoice = SimulationUtility.getRandomNumber(1, 10000000);
	 * 
	 * boolean isUrgentMessageArrival=
	 * SimulationUtility.isUrgentMessageRate(simulationEntry.getUrgentMessageRate())
	 * ;
	 * 
	 * // the sensor has a message to encrypt AbstractMessage message =
	 * SimulationUtility.getMessageToEncrypt(isUrgentMessageArrival);
	 * 
	 * if (message.isUrgent()) { // int sensorNumber =
	 * SimulationUtility.getRandomNumber(1, (simulationEntry.getSensorCount()));
	 * SimulationUtility.printUrgentMessageArrival(currentSystemTime, sensorNumber);
	 * if (sensorNumber <= simulationEntry.getSensorCount()) {
	 * AbstractSensorSRequest sensorSRequest =
	 * receiveResourceAvailabilityRequest(coordinator, currentSystemTime, message,
	 * sensorNumber); if(sensorSRequest!=null) {
	 * statisticManager.increaseNumberofUrgentSensorRequests();
	 * 
	 * AbstractWorkload workload =
	 * SimulationUtility.generateRandomlyWorkload(simulationEntry.getMinSecretValue(
	 * ), simulationEntry.getMaxSecretValue(),
	 * simulationEntry.getMinNumberOfAttibutes(),
	 * simulationEntry.getMaxNumberOfAttibutes(), simulationEntry.getQy0Interval())
	 * ; message.setWorkload(workload); // assign all
	 * System.out.println("Start Schedule Assignment for Urgent Message");
	 * LinkedList<AssistantNode> freeAssistantNodes =
	 * coordinator.getAvailableAssistantNodes();
	 * 
	 * if (freeAssistantNodes.size() > 0) { LinkedList<AbstractSensorSRequest>
	 * sensorSRequests = new LinkedList<AbstractSensorSRequest>();
	 * sensorSRequests.addLast(sensorSRequest); UrgentMessRRAssignment
	 * roundRobinAssignment = new UrgentMessRRAssignment(freeAssistantNodes,
	 * sensorSRequests); roundRobinAssignment.assignResourcesToSN();
	 * sensorsSplitWorkloadsForUrgentMessages(sensorSRequest,currentSystemTime,
	 * coordinator); } else {
	 * coordinator.getUrgentMessageQueue().addQueueLast(sensorSRequest); } } } else
	 * { java.lang.System.err.println("wrong number of sensor"); } } else if
	 * (waitTime > 0) {
	 * 
	 * AbstractSensorSRequest sensorSRequest =
	 * receiveSensorScheduleRequest(currentSystemTime, message,
	 * sensorNumber,coordinator,statisticManager); if(sensorSRequest!=null) {
	 * statisticManager.increaseNumberOfArrivedSensorSRequests();
	 * 
	 * 
	 * AbstractWorkload workload =
	 * SimulationUtility.generateRandomlyWorkload(simulationEntry.getMinSecretValue(
	 * ), simulationEntry.getMaxSecretValue(),
	 * simulationEntry.getMinNumberOfAttibutes(),
	 * simulationEntry.getMaxNumberOfAttibutes(), simulationEntry.getQy0Interval())
	 * ; message.setWorkload(workload);
	 * coordinator.getQueue().addQueueLast(sensorSRequest);
	 * SimulationUtility.printSensorRequestArrival(currentSystemTime, sensorNumber);
	 * }
	 * 
	 * } }
	 * 
	 * }
	 * 
	 * 
	 * }
	 * 
	 */

	public boolean isSatisfied2(int currentSystemTime) {

		boolean bool = false;
		for (Coordinator coordinator : coordinators) {
			bool = bool || (currentSystemTime <= simulationEntry.getSimulationDuration()
			// || coordinator.getQueue().getSensorSRequests().size() > 0
			// || coordinator.getUrgentMessageQueue().getSensorSRequests().size() > 0
			// || coordinator.isThereAnyAssistantNodeWorks()
			);

		}

		return bool;
	}

	/*
	 * public boolean isSatisfied(int currentSystemTime) {
	 * 
	 * boolean bool = currentSystemTime <= simulationEntry.getSimulationDuration()
	 * || coordinator.getQueue().getSensorSRequests().size()>0 ||
	 * coordinator.getUrgentMessageQueue().getSensorSRequests().size()>0 ||
	 * coordinator.isThereAnyAssistantNodeWorks();
	 * 
	 * return bool; }
	 */
	/***
	 * When time to when is finished, coordinator start treating the received
	 * schedule requests
	 * 
	 * @param currentSystemTime
	 */
	private void treatRegularMessagesInQueue(Coordinator coordinator, Queue queue, int currentSystemTime) {

		if (!queue.isEmpty()) {
			LinkedList<AssistantNode> freeAssistantNodes = coordinator.getAvailableAssistantNodes();

			if (freeAssistantNodes.size() > 0) {
				// System.out.println("Coordinator "+coordinator.getID()+" : Starts Schedule
				// Assignment for Requests in Queue");
				// System.out.println("Number of Regular Schedule Requests is :
				// "+coordinator.getQueue().size());

				RoundRobinAssignment roundRobinAssignment = new RoundRobinAssignment(freeAssistantNodes,
						queue.getSensorSRequests());
				roundRobinAssignment.assignResourcesToSN();
				// System.out.println("Coordinator "+coordinator.getID()+ " Finishs Schedule
				// Assignment");

				// System.out.println("Sensors Start Splitting their Secret Values ");
			}

			sensorsSplitWorkloads(coordinator, coordinator.getQueue(), currentSystemTime);

		}

	}

	/**
	 * This method should be called only for the case of Gasmi approach The
	 * coordinator threats the received urgent requests
	 * 
	 * @param urgentMessageQueue
	 * @param currentSystemTime
	 */
	private void treatUrgentMessagesInUrgentQueue(Coordinator coordinator, Queue urgentMessageQueue,
			int currentSystemTime) {

		if (!urgentMessageQueue.isEmpty()) {
			if (coordinator.getApproach() == UsedApproach.TouatiApproach) {
				System.out.println("*******************-------------------*************");
			}

			LinkedList<AssistantNode> freeAssistantNodes = coordinator.getAvailableAssistantNodes();

			if (freeAssistantNodes.size() > 0) {
				// System.out.println("Coordinator "+coordinator.getID()+" Starts Resource
				// Assignment for Requests in Urgent Queue");
				// System.out.println("Number of Urgent Schedule Requests is :
				// "+coordinator.getUrgentMessageQueue().size());

				UrgentMessRRAssignment roundRobinAssignment = new UrgentMessRRAssignment(freeAssistantNodes,
						urgentMessageQueue.getSensorSRequests());
				roundRobinAssignment.assignResourcesToSN();
				// System.out.println("Coordinator "+coordinator.getID()+ "Finishs Resource
				// Assignment");

				// System.out.println("Sensors Start Splitting their Secret Values ");

			}
			sensorsSplitWorkloads(coordinator, coordinator.getUrgentMessageQueue(), currentSystemTime);

		}

	}

	/**
	 * At time currentSystemTime each sensor should split its workload (urgent or
	 * regular) on its assigned resources
	 * 
	 * @param coordinator
	 * @param queue
	 * @param currentSystemTime
	 */
	private void sensorsSplitWorkloads(Coordinator coordinator, Queue queue, int currentSystemTime) {
		Iterator<AbstractSensorSRequest> itr = queue.getSensorSRequests().iterator();
		// int i=queue.size();
		while (itr.hasNext()) {
			// i--;
			AbstractSensorSRequest sensorRequest = itr.next();

			boolean threated = sensorRequest.getSensor().treatSensorRequest(sensorRequest, currentSystemTime,
					coordinator);
			sensorRequest.getMessage().getWorkload().getEstimatedExecutionTime();

			StatisticManager statisticManager = statisticManagers.get(coordinator.getID());
			if (sensorRequest.getMessage().isUrgent()) {
				statisticManager.increaseTotalEstimatedTimeforUM(
						sensorRequest.getMessage().getWorkload().getEstimatedExecutionTime());
			} else {
				statisticManager.increaseTotalEstimatedTimeforRM(
						sensorRequest.getMessage().getWorkload().getEstimatedExecutionTime());

			}
			// remove treated request from the queue
			// System.out.println("threated : "+threated);
			// System.out.println("queue.size()"+queue.size());
			// if(!threated) {
			if (!sensorRequest.getAssignedResources().isEmpty()) {
				itr.remove();
				coordinator.getQueue().getSensorSRequests().remove(sensorRequest);
			} else {
				// System.out.println("--->queue.size()"+queue.size());
			}
			// }
			// }
		}
	}

	/*
	 * private void workloadSplittingBySensorsInUrgentQueue(Queue queue, int
	 * currentSystemTime) { Iterator<AbstractSensorSRequest> itr =
	 * queue.getSensorSRequests().iterator(); while(itr.hasNext()){
	 * AbstractSensorSRequest sensorRequest = itr.next(); // for
	 * (AbstractSensorSRequest sensorRequest :
	 * coordinator.getQueue().getSensorSRequests()) {
	 * System.out.println(sensorRequest.getMessage().toString()+": Workload "
	 * +sensorRequest.getSensor().getWorkload());
	 * System.out.println("Assigned Resources :"+sensorRequest.getAssignedResources(
	 * ).size()); treatSensorRequest(sensorRequest, currentSystemTime);
	 * if(!sensorRequest.getAssignedResources().isEmpty()) { itr.remove(); //
	 * coordinator.getQueue().getSensorSRequests().remove(sensorRequest); } // } } }
	 */
	/*
	 * public LinkedList<SavedSubWorkloads> getSavedSubWorkloads() { return
	 * savedSubWorkloads; }
	 */
	private void sensorsSplitWorkloadsForUrgentMessages(AbstractSensorSRequest sensorRequest, int currentSystemTime,
			Coordinator coordinator) {

		// for (AbstractSensorSRequest sensorRequest :
		// coordinator.getQueue().getSensorSRequests()) {
		// System.out.println("Sensor_" + sensorRequest.getSensor().getID() + ", " +
		// sensorRequest.getMessage().toString()
		// + ": Workload " + sensorRequest.getMessage().getWorkload().getSecretValue());
		// System.out.println("Assigned Resources :" +
		// sensorRequest.getAssignedResources().size());
		sensorRequest.getSensor().treatSensorRequest(sensorRequest, currentSystemTime, coordinator);

	}

	/**
	 * time for start scheduling
	 * 
	 * @param sensorRequestsArrivalInterval
	 * @param simIter
	 * @return
	 */
	private boolean newSensorRequestsArrival(int sensorRequestsArrivalInterval, int simIter, int firstSensorRequestArrival) {
		if (simIter <= simulationEntry.getSimulationDuration()) {
			
			return (simIter -firstSensorRequestArrival) % sensorRequestsArrivalInterval == 0;
		} else {
			return false;
		}
	}

	public AbstractSensorSRequest receiveResourceAvailabilityRequest(Coordinator coordinator, int currentSystemTime,
			AbstractMessage message, int sensorNumber) {

		if (currentSystemTime <= simulationEntry.getSimulationDuration()) {
			AbstractSensor sensor = coordinator.getSensor(sensorNumber);

			AbstractSensorSRequest sensorSRequest = sensor.generateSRequest(currentSystemTime, message, sensor,
					simulationEntry.getSensorRequestPatienceTime());

			// statisticManager.increaseNumberofUrgentSensorRequests();

			return sensorSRequest;
		}
		return null;
	}

	public AbstractSensorSRequest receiveSensorScheduleRequest(int currentSystemTime, AbstractMessage message,
			int sensorNumber, Coordinator coordinator, StatisticManager statisticManager) {

		if (currentSystemTime <= simulationEntry.getSimulationDuration()) {
			AbstractSensor sensor = coordinator.getSensor(sensorNumber);
			AbstractSensorSRequest sensorSRequest = sensor.generateSRequest(currentSystemTime, message, sensor,
					simulationEntry.getSensorRequestPatienceTime());
			// statisticManager.increaseNumberOfArrivedSensorSRequests();

			return sensorSRequest;
		}
		return null;
	}

	/*
	 * private void updateSystem(int currentSystemTime) { List<AssistantNode>
	 * assistantNodes = coordinator.getAssistantNodes(); // Queue queue =
	 * coordinator.getQueue(); for (AssistantNode assistantNode : assistantNodes) {
	 * 
	 * if(currentSystemTime< simulationEntry.getSimulationDuration()) { if
	 * (!assistantNode.isAvailable() ) {
	 * statisticManager.AssistantNodeOccupationRecord(); }
	 * 
	 * for (Resource resource : assistantNode.getResources()) {
	 * if(!resource.isFree()) { statisticManager.ResourceOccupationRecord(); } } }
	 * assistantNode.work();
	 * 
	 * for (Resource resource : assistantNode.getResources()) {
	 * if(resource.computeFinished()) {
	 * 
	 * resource.getServingSensorRequest().getAssignedResources().remove(resource);
	 * 
	 * resource.getServingSensorRequest().setEncyptionFinishTime(currentSystemTime);
	 * SimulationUtility.printSensorRequestDeparture(currentSystemTime,resource.
	 * getServingSensor().getID());
	 * 
	 * if(resource.getServingSensorRequest().getMessage().isUrgent()) {
	 * statisticManager.registerServedUrgentSensorRequest(resource.
	 * getServingSensorRequest()); } else{
	 * statisticManager.registerServedSensorSRequest(resource.
	 * getServingSensorRequest()); } //
	 * queue.getSensorSRequests().remove(resource.getServingSensorRequest());
	 * resource.setServingSensor(null); } } }
	 * 
	 * Queue queue = coordinator.getQueue();
	 * 
	 * // Leaving impatient sensor request queue.updateSensorSRequestPatience();
	 * List<AbstractSensorSRequest> impatientSensorSRequests =
	 * queue.removeImpatientSensorSRequest(); for (AbstractSensorSRequest
	 * sensorSRequest : impatientSensorSRequests) {
	 * sensorSRequest.setDepartureTime(currentSystemTime);
	 * statisticManager.registerNonServedSensorSRequest(sensorSRequest);
	 * SimulationUtility.printSensorRequestDepartureWithoutBeingServed(
	 * currentSystemTime,sensorSRequest.getSensor().getID()); }
	 * 
	 * Queue urgentQueue = coordinator.getUrgentMessageQueue();
	 * 
	 * // Leaving impatient urgent sensor request
	 * urgentQueue.updateSensorSRequestPatience(); List<AbstractSensorSRequest>
	 * impatientUrgentSensorSRequests = urgentQueue.removeImpatientSensorSRequest();
	 * for (AbstractSensorSRequest sensorSRequest : impatientUrgentSensorSRequests)
	 * { sensorSRequest.setDepartureTime(currentSystemTime);
	 * statisticManager.registerNonServedUrgentSensorSRequest(sensorSRequest);
	 * SimulationUtility.printUrgentSensorRequestDepartureWithoutBeingServed(
	 * currentSystemTime,sensorSRequest.getSensor().getID()); }
	 * 
	 * 
	 * 
	 * 
	 * }
	 * 
	 * 
	 */

	private void updateSystemWithCompare(int currentSystemTime, Coordinator coordinator) {
		
		StatisticManager statisticManager = coordinator.getStatisticManager();
		List<AssistantNode> assistantNodes = coordinator.getAssistantNodes();
		// Queue queue = coordinator.getQueue();
		for (AssistantNode assistantNode : assistantNodes) {

			if (currentSystemTime < simulationEntry.getSimulationDuration()) {
				if (!assistantNode.isAvailable()) {
					statisticManager.AssistantNodeOccupationRecord();
				}

				for (Resource resource : assistantNode.getResources()) {
					if (!resource.isFree()) {
						statisticManager.ResourceOccupationRecord();
					}
				}
			}
			assistantNode.work();

			for (Resource resource : assistantNode.getResources()) {
				if (resource.computeFinished()) {

					resource.getServingSensorRequest().getAssignedResources().remove(resource);

					resource.getServingSensorRequest().setEncyptionFinishTime(currentSystemTime);
					if (simulationEntry.isPrintDetails()) {
						SimulationUtility.printResourceInfo(resource);
						SimulationUtility.printSensorRequestDeparture(currentSystemTime,
								resource.getServingSensor().getID());
					}
					if (resource.getServingSensorRequest().getMessage().isUrgent()) {
						statisticManager.registerServedUrgentSensorRequest(resource.getServingSensorRequest());
					} else {
						statisticManager.registerServedSensorSRequest(resource.getServingSensorRequest());
					}
					// queue.getSensorSRequests().remove(resource.getServingSensorRequest());
					resource.setServingSensor(null);
				}
			}
		}

		Queue queue = coordinator.getQueue();

		// Leaving impatient sensor request
		queue.updateSensorSRequestPatience();
		List<AbstractSensorSRequest> impatientSensorSRequests = queue.removeImpatientSensorSRequest();
        
		for (AbstractSensorSRequest sensorSRequest : impatientSensorSRequests) {
			sensorSRequest.setDepartureTime(currentSystemTime);
			System.out.println("Used Approach...  : "+coordinator.getDescription() +": ");

			if (sensorSRequest.getMessage().isUrgent()) {// Case of Touati approach
				if (coordinator.getApproach() == UsedApproach.GasmiApproach) {
					java.lang.System.err.println("there is an erreur here");
				}
				statisticManager.registerNonServedUrgentSensorSRequest(sensorSRequest);
				SimulationUtility.printUrgentSensorRequestDepartureWithoutBeingServed(currentSystemTime,
						sensorSRequest.getSensor().getID());
			} else {
				statisticManager.registerNonServedSensorSRequest(sensorSRequest);
				SimulationUtility.printSensorRequestDepartureWithoutBeingServed(currentSystemTime,
						sensorSRequest.getSensor().getID());
			}

			
		}
		impatientSensorSRequests = null;
		Queue urgentQueue = coordinator.getUrgentMessageQueue();

		// Leaving impatient urgent sensor request
		urgentQueue.updateSensorSRequestPatience();
		List<AbstractSensorSRequest> impatientUrgentSensorSRequests = urgentQueue.removeImpatientSensorSRequest();
		for (AbstractSensorSRequest sensorSRequest : impatientUrgentSensorSRequests) {

			sensorSRequest.setDepartureTime(currentSystemTime);
			statisticManager.registerNonServedUrgentSensorSRequest(sensorSRequest);
			System.out.println("Used Approach : "+coordinator.getDescription() +": ");

			SimulationUtility.printUrgentSensorRequestDepartureWithoutBeingServed(currentSystemTime,
					sensorSRequest.getSensor().getID());
		}
		impatientUrgentSensorSRequests = null;
	}

	/**
	 * Case of end of simulation duration
	 * 
	 * @param currentSystemTime
	 * @param coordinator
	 * @param statisticManager
	 */
	private void countNonServedMessages(int currentSystemTime, Coordinator coordinator,
			StatisticManager statisticManager) {

		for (AbstractSensor sensor : coordinator.getSensors()) {
			for (AbstractSensorSRequest sensorSRequest : sensor.getSensorScheduleRequests()) {
				if (!sensorSRequest.isServed()) {
					if (sensorSRequest.getMessage().isUrgent()) {
						if (isNotRegistredAsNonServed(statisticManager.getNonServedUrgentSensorRequests(),
								sensorSRequest)) {
							statisticManager.getNonServedUrgentSensorRequests().add(sensorSRequest);
						}
					} else {
						if (isNotRegistredAsNonServed(statisticManager.getNonServedSensorSRequests(), sensorSRequest)) {

							statisticManager.getNonServedSensorSRequests().add(sensorSRequest);
						}
					}
				}
			}
		}

		/*
		 * Queue queue = coordinator.getQueue();
		 * 
		 * System.out.println("queue size after simulation : " +queue.size());
		 * List<AbstractSensorSRequest> outOfDateSensorSRequests =
		 * queue.removeSensorSRequestOutOfDate();
		 * 
		 * for (AbstractSensorSRequest sensorSRequest : outOfDateSensorSRequests) {
		 * sensorSRequest.setDepartureTime(currentSystemTime); if
		 * (sensorSRequest.getMessage().isUrgent()) {// Case of Touati approach
		 * if(coordinator.getApproach() == UsedApproach.GasmiApproach) {
		 * java.lang.System.err.println("there is an erreur here"); }
		 * statisticManager.registerNonServedUrgentSensorSRequest(sensorSRequest); }
		 * else { statisticManager.registerNonServedSensorSRequest(sensorSRequest); }
		 * SimulationUtility.printSensorRequestDepartureWithoutBeingServed(
		 * currentSystemTime, sensorSRequest.getSensor().getID()); }
		 * 
		 * 
		 * 
		 * Queue urgentQueue = coordinator.getUrgentMessageQueue();
		 * System.out.println("urgent queue size after simulation : "
		 * +urgentQueue.size());
		 * 
		 * // Leaving impatient urgent sensor request
		 * urgentQueue.updateSensorSRequestPatience(); List<AbstractSensorSRequest>
		 * outOfDateUrgentSensorSRequests = urgentQueue.removeSensorSRequestOutOfDate();
		 * for (AbstractSensorSRequest sensorSRequest : outOfDateUrgentSensorSRequests)
		 * { System.out.println("out Of Date urgent message case of gasmi approach");
		 * System.out.println("approach" + coordinator.getApproach());
		 * sensorSRequest.setDepartureTime(currentSystemTime);
		 * statisticManager.registerNonServedUrgentSensorSRequest(sensorSRequest);
		 * SimulationUtility.printUrgentSensorRequestDepartureWithoutBeingServed(
		 * currentSystemTime, sensorSRequest.getSensor().getID()); }
		 */
	}

	private boolean isNotRegistredAsNonServed(ArrayList<AbstractSensorSRequest> nonServedSensorRequests,
			AbstractSensorSRequest sensorSRequest) {

		for (AbstractSensorSRequest abstractSensorSRequest : nonServedSensorRequests) {
			if (abstractSensorSRequest.getArrivalTime() == sensorSRequest.getArrivalTime()
					&& abstractSensorSRequest.getSensor().getID() == sensorSRequest.getSensor().getID()) {
				return false;
			}
		}
		return true;
	}

	public String simulationResults() {
		StringBuffer results = new StringBuffer();

		results.append("########## Simulation results : #####################\n");
		results.append("Simulation Duration : " + simulationEntry.getSimulationDuration() + "\n");
		results.append("Effective Simulation Duration : " + statisticManager.getEffectiveSimulationDuration() + "\n");
		results.append("Total Number of sensor schedule requests : "
				+ statisticManager.getTotalNumberofSensorSRequests() + "\n");
		results.append("Total Number of urgent sensor requests : "
				+ statisticManager.getTotalNumberofUrgentSensorRequests() + "\n");

		results.append("Served sensor schedule request count : " + statisticManager.servedSensorSRequestCount() + "\n");
		results.append(
				"Served urgent encryption request count : " + statisticManager.servedUrgentSensorRequestCount() + "\n");

		results.append("Non-Served regular sensor schedule request count : "
				+ statisticManager.nonServedSensorRequestCount() + "\n");
		results.append("Non-Served urgent sensor request count : "
				+ statisticManager.nonServedUrgentSensorRequestCount() + "\n");

		results.append("Average  regular sensor schedule request waiting time : "
				+ statisticManager.calculateAverageSensorSRequestWaitingTime() + " \n");

		results.append("Average  urgent sensor request waiting time : "
				+ statisticManager.calculateAverageUrgentSensorRequestWaitingTime() + " \n");

		results.append("Average Sensor Request encryption time : "
				+ statisticManager.calculateAverageSensorRequestEncryptionTime() + " \n");
		results.append("Average Urgent Sensor Request encryption time : "
				+ statisticManager.calculateAverageUrgentSensorRequestEncryptionTime() + " \n");

		results.append(
				"Assistant Node occupation rate : "
						+ statisticManager.calculateAverageAssistantNodeOccupationRate(
								simulationEntry.getAssistantNodeCount(), simulationEntry.getSimulationDuration())
						+ " % \n");

		/*
		 * results.append("Resource occupation rate : " +
		 * statisticManager.calculateAverageResourceOccupationRate(coordinator.
		 * getResourceCount(),simulationEntry.getSimulationDuration()) + " % \n");
		 */
		results.append(
				"Sensor satisfaction rate : " + statisticManager.calculateSensorRequestSatisfactionRate() + " %"+ " \n");
		results.append(
				"Sensor satisfaction rate for urgent message: " + statisticManager.calculateSensorUrgentRequestSatisfactionRate() + " %"+ " \n");

		
		return results.toString();
	}

	public String simulationResults(List<StatisticManager> statisticManagers, List<Coordinator> coordinators2) {

		StringBuffer results = new StringBuffer();
		int i = 0;
		
		
		System.out.println(results.toString());
		for (Coordinator coordinator : coordinators2) {
			
		//	StatisticManager statisticManager = statisticManagers.get(i);
			StatisticManager statisticManager = coordinator.getStatisticManager();

			double lastTimeOfWork = statisticManager.getLastFinishTime4RM()>statisticManager.getLastFinishTime4UM() ? statisticManager.getLastFinishTime4RM() :statisticManager.getLastFinishTime4UM();

			results.append("########## Simulation results : for Coordinator " + coordinator.getID()
					+ " #####################\n");
			results.append("# Approach : " + coordinator.getDescription()
			+ "\n");
			results.append("Simulation Duration : " + simulationEntry.getSimulationDuration() + "\n");
			results.append(
					"Effective Simulation Duration : " + statisticManager.getEffectiveSimulationDuration() + "\n");
			results.append("Last regular message is finished at  " + statisticManager.getLastFinishTime4RM() + "\n");

			results.append("Last urgent messages is finished at  " + statisticManager.getLastFinishTime4UM() + "\n");

			results.append("Total  :Encryption Time for all served regular messages "
					+ statisticManager.getEncryptionTime4RegularMessages() + "\n");
			results.append("Total  :Encryption Time for all served urgent messages "
					+ statisticManager.getEncryptionTime4UrgentMessages() + "\n");

			results.append("Estimated Encryption Time for all regular messages "
					+ statisticManager.getTotalEstimatedTimeforRM() + "\n");
			results.append("Estimated Encryption Time for all urgent messages "
					+ statisticManager.getTotalEstimatedTimeforUM() + "\n");

			results.append("Total Number of sensor schedule requests : "
					+ statisticManager.getTotalNumberofSensorSRequests() + "\n");
			results.append("Total Number of urgent sensor requests : "
					+ statisticManager.getTotalNumberofUrgentSensorRequests() + "\n");

			results.append(
					"Served sensor schedule request count : " + statisticManager.servedSensorSRequestCount() + "\n");
			results.append("Served urgent encryption request count : "
					+ statisticManager.servedUrgentSensorRequestCount() + "\n");

			results.append("Non-Served regular sensor schedule request count : "
					+ statisticManager.nonServedSensorRequestCount() + "\n");
			results.append("Non-Served urgent sensor request count : "
					+ statisticManager.nonServedUrgentSensorRequestCount() + "\n");
			

			results.append("Average  regular sensor schedule request waiting time : "
					+ statisticManager.calculateAverageSensorSRequestWaitingTime() + " \n");
			results.append("Median  regular sensor schedule request waiting time : "
					+ statisticManager.calculateMedianSensorSRequestWaitingTime() + " \n");

			results.append("Average  urgent sensor request waiting time : "
					+ statisticManager.calculateAverageUrgentSensorRequestWaitingTime() + " \n");
			results.append("Median  urgent sensor request waiting time : "
					+ statisticManager.calculateMedianUrgentSensorRequestWaitingTime() + " \n");

			results.append("Average Sensor Request encryption time : "
					+ statisticManager.calculateAverageSensorRequestEncryptionTime() + " \n");
			results.append("Median Sensor Request encryption time : "
					+ statisticManager.calculateMedianSensorRequestEncryptionTime() + " \n");

			results.append("Median Sensor Request Cumulative encryption time : "
					+ statisticManager.calculateMedianSReqCumulativeEncryptionTime() + " \n");

			results.append("Average Urgent Sensor Request encryption time : "
					+ statisticManager.calculateAverageUrgentSensorRequestEncryptionTime() + " \n");
			results.append("Median Urgent Sensor Request encryption time : "
					+ statisticManager.calculateMedianUrgentSensorRequestEncryptionTime() + " \n");

			results.append("Median Urgent Sensor Request Cumulative encryption time : "
					+ statisticManager.calculateMedianUrgentSReqCumulativeEncryptionTime() + " \n");

		/*	
			results.append("Assistant Node occupation rate (refering to simulation duration)  : "
					+ statisticManager.calculateAverageAssistantNodeOccupationRate(
							simulationEntry.getAssistantNodeCount(), simulationEntry.getSimulationDuration())
					+ " % \n");

			results.append("Resource occupation rate (refering to simulation duration) : " + statisticManager.calculateAverageResourceOccupationRate(
					coordinator.getResourceCount(), simulationEntry.getSimulationDuration()) + " % \n");
			
			results.append("Assistant Node occupation rate (refering to last encrypted message): "
					+ statisticManager.calculateAverageAssistantNodeOccupationRate(
							simulationEntry.getAssistantNodeCount(), (int) lastTimeOfWork)
					+ " % \n");

			results.append("Resource occupation rate (refering to last encrypted message) : " + statisticManager.calculateAverageResourceOccupationRate(
					coordinator.getResourceCount(), (int) lastTimeOfWork) + " % \n");0*/
			results.append(
					"Sensor satisfaction rate : " + statisticManager.calculateSensorRequestSatisfactionRate() + " %"+ " \n");
			results.append(
					"Sensor satisfaction rate for urgent message: " + statisticManager.calculateSensorUrgentRequestSatisfactionRate() + " %"+ " \n");

			results.append("\n########## End results : #####################\n");

			i++;
		}
		return results.toString();
	}

	public String writeResultsToExcel(List<StatisticManager> statisticManagers, List<Coordinator> coordinators2) {

		StringBuffer results = new StringBuffer();
		int i = 0;
		List<List<Object>> rows = new ArrayList<List<Object>>();
		rows.add(Arrays.asList("Obtained Results"));
		for (Coordinator coordinator : coordinators2) {

			StatisticManager statisticManager = statisticManagers.get(i);
			
			double lastTimeOfWork = statisticManager.getLastFinishTime4RM()>statisticManager.getLastFinishTime4UM() ? statisticManager.getLastFinishTime4RM() :statisticManager.getLastFinishTime4UM();

			rows.add(Arrays.asList("Simulation results for :", coordinator.getID() + " " + coordinator.getApproach()));

			rows.add(Arrays.asList("Simulation Duration : ", simulationEntry.getSimulationDuration()));

			rows.add(Arrays.asList("Number of Sensors : ", simulationEntry.getSensorCount()));

			rows.add(Arrays.asList("Min Secret Value : ", simulationEntry.getMinSecretValue()));
			rows.add(Arrays.asList("Max Secret Value : ", simulationEntry.getMaxSecretValue()));

			rows.add(Arrays.asList("Min Number Attribues : ", simulationEntry.getMinNumberOfAttibutes()));
			rows.add(Arrays.asList("Max Number Attribues : ", simulationEntry.getMaxNumberOfAttibutes()));

			List<Object> assistantNodes = new ArrayList<Object>();
			assistantNodes.add("Number of Assistant Nodes: ");
			assistantNodes.add(simulationEntry.getAssistantNodeCount());
			for (AssistantNodeConfig config : simulationEntry.getSelectedAssistantNodeConfigs()) {
				assistantNodes.add(config.toString());
			}

			rows.add(assistantNodes);
			assistantNodes.add(simulationEntry.getSelectedAssistantNodeConfigs().get(0).toString());

			// rows.add(Arrays.asList("Number of Assistant Nodes: ");
			rows.add(Arrays.asList("---", "---"));

			rows.add(Arrays.asList("Total Number of Sensor Schedule Requests : ",
					statisticManager.getTotalNumberofSensorSRequests()));
			rows.add(Arrays.asList("Served sensor Schedule Request Count : ",
					statisticManager.servedSensorSRequestCount()));
			rows.add(Arrays.asList("Non-Served regular sensor schedule request count : ",
					statisticManager.nonServedSensorRequestCount()));

			rows.add(Arrays.asList("Total Number of urgent sensor requests : ",
					statisticManager.getTotalNumberofUrgentSensorRequests()));
			rows.add(Arrays.asList("Served urgent encryption request count : ",
					statisticManager.servedUrgentSensorRequestCount()));
			rows.add(Arrays.asList("Non-Served urgent sensor request count : ",
					statisticManager.nonServedUrgentSensorRequestCount()));

			rows.add(Arrays.asList("Total Encryption Time for regular messages",
					statisticManager.getEncryptionTime4RegularMessages()));
			rows.add(Arrays.asList("Total Encryption Time for urgent messages ",
					statisticManager.getEncryptionTime4UrgentMessages()));
			rows.add(Arrays.asList("Finish encrypting at : ",
					lastTimeOfWork));
			rows.add(Arrays.asList("Average Sensor Request encryption time : ",
					statisticManager.calculateAverageSensorRequestEncryptionTime()));
			rows.add(Arrays.asList("Median Sensor Request encryption time : ",
					statisticManager.calculateMedianSensorRequestEncryptionTime()));
			rows.add(Arrays.asList("Median Sensor Request Cumulative encryption time : ",
					statisticManager.calculateMedianSReqCumulativeEncryptionTime()));
			rows.add(Arrays.asList("Average Urgent Sensor Request encryption time : ",
					statisticManager.calculateAverageUrgentSensorRequestEncryptionTime()));
			rows.add(Arrays.asList("Median Urgent Sensor Request encryption time : ",
					statisticManager.calculateMedianUrgentSensorRequestEncryptionTime()));
			rows.add(Arrays.asList("Median Urgent Sensor Request Cumulative encryption time : ",
					statisticManager.calculateMedianUrgentSReqCumulativeEncryptionTime()));
			rows.add(Arrays.asList("Sensor satisfaction rate : ",
					statisticManager.calculateSensorRequestSatisfactionRate()));
			rows.add(Arrays.asList("Sensor satisfaction rate for urgent message: " ,
					statisticManager.calculateSensorUrgentRequestSatisfactionRate()));

		/*	rows.add(Arrays.asList("Assistant Node occupation rate : ",
					statisticManager.calculateAverageAssistantNodeOccupationRate(
							simulationEntry.getAssistantNodeCount(), (int) lastTimeOfWork)));
			
			rows.add(Arrays.asList("Resource occupation rate : ",
					statisticManager.calculateAverageResourceOccupationRate(
							coordinator.getResourceCount(), (int) lastTimeOfWork)));
			
			rows.add(Arrays.asList("########## End results : #####################"));*/
			i++;
		}

		try {
			File file = new File("results.xlsx");
			XSSFWorkbook workbook;
			if (file.exists()) {
				FileInputStream inputStream = new FileInputStream(file);
				workbook = new XSSFWorkbook(inputStream);

			} else {
				workbook = new XSSFWorkbook();

			}
			XSSFSheet sheet = workbook.createSheet("Results" + (new Date()).getTime());

			int rowNum = 0;
			for (List<Object> rowData : rows) {
				Row row = sheet.createRow(rowNum++);
				int colNum = 0;
				for (Object field : rowData) {
					Cell cell = row.createCell(colNum++);
					if (field instanceof String) {
						cell.setCellValue((String) field);
					} else if (field instanceof Integer) {
						cell.setCellValue((Integer) field);
					} else if (field instanceof Double) {
						cell.setCellValue((Double) field);
					}
				}
			}

			FileOutputStream outputStream = new FileOutputStream("results.xlsx");

			workbook.write(outputStream);
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results.toString();
	}

	public String printSimulationEntry() {
		StringBuffer results = new StringBuffer();
		results.append("########## Simulation Inputs :#####################\n");

		results.append("Number of Assistant nodes : " + simulationEntry.getAssistantNodeCount());
		results.append("Number of Sensors : " + simulationEntry.getSensorCount());
		results.append("URGENT MESSAGE RATE : " + simulationEntry.getUrgentMessageRate());

		results.append("Min Secret Value : " + simulationEntry.getMinSecretValue());
		results.append("Max Secret Value : " + simulationEntry.getMaxSecretValue());
		results.append("Sensor Request Patience Time : " + simulationEntry.getSensorRequestPatienceTime());
		results.append("Sensor Requests Arrival Interval: " + simulationEntry.getSensorRequestsArrivalInterval());

		return results.toString();
	}

}

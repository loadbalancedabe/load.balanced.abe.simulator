package load.balanced.abe.simulation;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import load.balanced.abe.coordinator.AssistantNodeConfig;
import load.balanced.abe.coordinator.Coordinator;
import load.balanced.abe.coordinator.Resource;
import load.balanced.abe.sensors.AbstractMessage;
import load.balanced.abe.sensors.AbstractSensor;
import load.balanced.abe.sensors.AbstractSensorSRequest;
import load.balanced.abe.sensors.AbstractWorkload;
import load.balanced.abe.sensors.RegularMessage;
import load.balanced.abe.sensors.SensorRequest;
import load.balanced.abe.sensors.UrgentMessage;

/**
 * Provides utility static methods for simulation.
 */
public class SimulationUtility {

	public static int getRandomNumber(int min, int max) {
		return (int) (Math.random() * (max + 1 - min)) + min;
	}

	public static AbstractWorkload generateRandomlyWorkload(int minSecretValue, int maxSecretValue,
			int minNumberOfAttibutes, int maxNumberOfAttibutes, int qy0Interval) {

		int secretValue = SimulationUtility.getRandomNumber(minSecretValue, maxSecretValue);
		int numberOfAttributes = SimulationUtility.getRandomNumber(minNumberOfAttibutes, maxNumberOfAttibutes);

		AbstractWorkload workload = new AbstractWorkload(secretValue);

		// Qy(0) is initialized by S
		workload.getQy0Set().addLast(secretValue);

		for (int i = 1; i < numberOfAttributes; i++) {
			// int qy0 =
			// SimulationUtility.getRandomNumber(secretValue,secretValue+qy0Interval);
			int qy0 = SimulationUtility.getRandomNumber(secretValue + qy0Interval, secretValue + qy0Interval);

			workload.getQy0Set().addLast(qy0);
		}
		return workload;
	}

	public static void printUrgentMessageArrival(int currentSystemTime, int sensorNumber) {

		System.out.println("A new urgent message encryption request arrives from Sensor_" + sensorNumber);
		// System.out.println("Time is "+currentSystemTime);
	}

	public static void printSensorRequestArrival(int currentSystemTime, int sensorNumber) {

		System.out.println("A new sensor schedule request arrives from Sensor_" + sensorNumber);

	}

	public static void printSensorRequestDeparture(int currentSystemTime, int sensorNumber) {

		System.out.println("A sensor request received from Sensor_" + sensorNumber + " is threated after encryption.");
	}

	public static void printSensorRequestDepartureWithoutBeingServed(int currentSystemTime, int sensorNumber) {
		System.out.println("A sensor request received from Sensor_" + sensorNumber + " has not being threated.");
	}

	public static void printUrgentSensorRequestDepartureWithoutBeingServed(int currentSystemTime, int sensorNumber) {
		System.out
				.println("An Urgent sensor request received from Sensor_" + sensorNumber + " has not being threated.");
	}

	public static void printCoordinatorStat(int currentSystemTime, Coordinator coordinator) {
		System.out.println("########### At time : " + currentSystemTime + " ##########");
		System.out.println(coordinator.toString());
	}

	/*
	 * public static boolean isPriorityClient(double priorityClientRate) { double
	 * random = Math.random(); return random < priorityClientRate; }
	 */
	public static AbstractMessage getMessageToEncrypt(boolean isUrgentMessage) {

		AbstractMessage message;
		if (isUrgentMessage) {
			message = new UrgentMessage();
		} else {
			message = new RegularMessage();
		}

		return message;
	}

	public static boolean isUrgentMessageRate(double urgentMessageRate) {
		double random = Math.random();
		return random < urgentMessageRate;
	}

	public static boolean areQy0SetEquals(LinkedList<Integer> qy0Set1, LinkedList<Integer> qy0Set2) {
		boolean bool = true;
		int i = 0;
		for (Integer qy01 : qy0Set1) {
			Integer qy02 = qy0Set2.get(i);
			if (qy01 == qy02) {
				bool = false;
			}
		}
		return bool;
	}

	/**
	 * Split Workload Randomly to Not Equal Parts With Gasmi Approach without taking
	 * into consideration the weights if resources
	 * 
	 * @param workload
	 * @param resources
	 * @return
	 */
	public static LinkedList<AbstractWorkload> split2NotEqualPartsWithRandomAppr1(AbstractWorkload workload,
			List<Resource> resources) {

		int numberOfResources = resources.size();

		// System.out.println("Split randomly workload : ");
		// Split S to set of S_i
		List<Integer> listSi = splitRandomlytoNotEqualParts1(workload.getSecretValue(), numberOfResources);
		// finalList.add(j*2); // H^S_i and e(g,g)^alpha^S_i

		List<List<Integer>> listOfListQy0i = new ArrayList<List<Integer>>();
		// for each attribute in Y Split Qy0 to set of Qy0_i
		// System.out.println("Number Of attributes is :
		// "+workload.getNumberOfAttributes());
		for (Integer Qy0i : workload.getQy0Set()) {
			// System.out.println("Split Qy0i : ");
			List<Integer> listQy0i = splitRandomlytoNotEqualParts1(Qy0i, numberOfResources);
			listOfListQy0i.add(listQy0i);
		}

		// create SubWorkloads
		for (int i = 0; i < numberOfResources; i++) {
			AbstractWorkload subWorkload = new AbstractWorkload();
			int S_i = listSi.get(i);
			subWorkload.setSecretValue(S_i);

			for (List<Integer> listQy0_i : listOfListQy0i) {
				subWorkload.getQy0Set().add(listQy0_i.get(i));
			}
			workload.getSubWorkloads().add(subWorkload);
		}

		return workload.getSubWorkloads();

//		LinkedList<AbstractWorkload> subWorkloads = workload.getSubWorkloads();
	}

	/**
	 * Split Workload Randomly to Not Equal Parts With Gasmi Approach without taking
	 * into consideration the weights if resources
	 * 
	 * @param workload
	 * @param resources
	 * @return
	 */
	public static LinkedList<AbstractWorkload> split2NotEqualPartsWithRandomAppr2(AbstractWorkload workload,
			List<Resource> resources) {

		int numberOfResources = resources.size();

		// System.out.println("Split randomly workload : ");
		// Split S to set of S_i
		List<Integer> listSi = splitRandomlytoNotEqualParts2(workload.getSecretValue(), numberOfResources);
		// finalList.add(j*2); // H^S_i and e(g,g)^alpha^S_i

		List<List<Integer>> listOfListQy0i = new ArrayList<List<Integer>>();
		// for each attribute in Y Split Qy0 to set of Qy0_i
		// System.out.println("Number Of attributes is :
		// "+workload.getNumberOfAttributes());
		for (Integer Qy0i : workload.getQy0Set()) {
			// System.out.println("Split Qy0i : ");
			List<Integer> listQy0i = splitRandomlytoNotEqualParts2(Qy0i, numberOfResources);
			listOfListQy0i.add(listQy0i);
		}

		// create SubWorkloads
		for (int i = 0; i < numberOfResources; i++) {
			AbstractWorkload subWorkload = new AbstractWorkload();
			int S_i = listSi.get(i);
			subWorkload.setSecretValue(S_i);

			for (List<Integer> listQy0_i : listOfListQy0i) {
				subWorkload.getQy0Set().add(listQy0_i.get(i));
			}
			workload.getSubWorkloads().add(subWorkload);
		}

		return workload.getSubWorkloads();

//		LinkedList<AbstractWorkload> subWorkloads = workload.getSubWorkloads();
	}

	/**
	 * Split Workload Randomly With Gasmi Approach with taking into consideration
	 * the weights if resources
	 * 
	 * @param workload
	 * @param resources
	 * @return
	 */
	public static LinkedList<AbstractWorkload> split2NotEqualPartsWithGasmiApproach(AbstractWorkload workload,
			List<Resource> resources) {

		int numberOfResources = resources.size();

		List<Double> listOfResourcesWeigths = new ArrayList<Double>();

		for (Resource resource : resources) {

			listOfResourcesWeigths.add(resource.getCoreWeight());
		}
		// System.out.println("Split randomly workload : ");
		// Split S to set of S_i
		List<Integer> listSi = splitValueRandomlyWithRWeights(workload.getSecretValue(), numberOfResources,
				listOfResourcesWeigths);
		// finalList.add(j*2); // H^S_i and e(g,g)^alpha^S_i

		List<List<Integer>> listOfListQy0i = new ArrayList<List<Integer>>();
		// for each attribute in Y Split Qy0 to set of Qy0_i
		// System.out.println("Number Of attributes is :
		// "+workload.getNumberOfAttributes());
		int sizeQy0Set = workload.getQy0Set().size();
		for (Integer Qy0i : workload.getQy0Set()) {
		//	System.out.println("Split Qy0i : " + Qy0i);
			List<Integer> listQy0i = splitValueRandomlyWithRWeights(Qy0i, numberOfResources, listOfResourcesWeigths);
			listOfListQy0i.add(listQy0i);
		}

		// create SubWorkloads
		for (int i = 0; i < numberOfResources; i++) {
			AbstractWorkload subWorkload = new AbstractWorkload();
			int S_i = listSi.get(i);
			subWorkload.setSecretValue(S_i);

			for (List<Integer> listQy0_i : listOfListQy0i) {
				subWorkload.getQy0Set().add(listQy0_i.get(i));
			}
			workload.getSubWorkloads().add(subWorkload);
		}

		return workload.getSubWorkloads();

//		LinkedList<AbstractWorkload> subWorkloads = workload.getSubWorkloads();
	}

	// must be sorted list
	public static double median(List<Double> list) {
	//	System.out.println(list.size());
		if (list.size() == 1) {
			return list.get(0);
		} else {
			Collections.sort(list);
			int middle = list.size() / 2;
			if (list.size() % 2 == 1) {
				return list.get(middle);
			} else {
				return (list.get(middle - 1) + list.get(middle)) / 2.0;
			}
		}
	}

	public static void main(String[] args) {

		List<Double> list = new ArrayList<Double>();
		list.add(2.0);
		list.add(2.0);
		list.add(5.0);
		list.add(5.0);
		list.add(2.0);
		list.add(2.0);
		list.add(2.0);
		list.add(2.0);
		System.out.println(": " + median(list));
	}

	public static double standardDeviation(List<Double> list, Double mean) {
		Double numerator = 0.0;
		for (int i = 0; i < list.size(); i++) {
			numerator += Math.pow((list.get(i) - mean), 2);
		}
		numerator = numerator / list.size();
		return Math.sqrt(numerator);
	}

	public static List<AssistantNodeConfig> generateRandomAssistantNodeConfigs(int assistantNodeCount, InputAssistantNodes used_cpu_models) {
		ANConfigReader anConfigReader;
		List<AssistantNodeConfig> assistantNodeConfigs = new ArrayList<AssistantNodeConfig>();
		for (int count = 1; count <= assistantNodeCount; count++) {
			/* lire à partir d'un fichier */
			File file = getANFile(used_cpu_models);
			anConfigReader = new ANConfigReader(file.getAbsolutePath());
			AssistantNodeConfig assistantNodeConfig = anConfigReader.getRandomConfiguration();

			// AssistantNodeConfig assistantNodeConfig = getAssistantNodeConfig();
			if (assistantNodeConfig != null) {
				assistantNodeConfigs.add(assistantNodeConfig);
			}
		}
		return assistantNodeConfigs;
	}

	private static File getANFile(InputAssistantNodes used_cpu_models) {
		File file =null;
		
		if(used_cpu_models == InputAssistantNodes.different_cpu_models) {
		 file = new File("input/cpu_models.xml");
		}
		else if (used_cpu_models == InputAssistantNodes.same_weak_cpu_models) {
			file = new File("input/same_weak_cpu_models.xml");
		}
		else if (used_cpu_models == InputAssistantNodes.same_powerful_cpu_models) {
			file = new File("input/same_powerful_cpu_models.xml");
		}
		else if (used_cpu_models == InputAssistantNodes.same_medium_cpu_models) {
			file = new File("input/same_medium_cpu_models.xml");
		}
		return file;
	}

	public static List<AssistantNodeConfig> selectSetOfAssistantNodeConfigs(int assistantNodeCount, InputAssistantNodes used_cpu_models) {
		ANConfigReader anConfigReader;
		List<AssistantNodeConfig> assistantNodeConfigs = new ArrayList<AssistantNodeConfig>();

		/* lire à partir d'un fichier */
		File file = getANFile(used_cpu_models);
		anConfigReader = new ANConfigReader(file.getAbsolutePath());

		assistantNodeConfigs = anConfigReader.getNeededANConfigurations(assistantNodeCount);

		return assistantNodeConfigs;
	}

	public static List<AbstractSensor> generateRandomSensors(int sensorCount, int minSecretValue, int maxSecretValue,
			int minNumberOfAttibutes, int maxNumberOfAttibutes, int qy0Interval) {
		List<AbstractSensor> sensors = new ArrayList<AbstractSensor>();
		for (int i = 0; i < sensorCount; i++) {
			AbstractSensor sensor = new AbstractSensor(i + 1, minSecretValue, maxSecretValue, minNumberOfAttibutes,
					maxNumberOfAttibutes, qy0Interval);
			sensors.add(sensor);
		}
		return sensors;
	}
	/*
	 * private static AssistantNodeConfig getAssistantNodeConfig() {
	 * 
	 * AssistantNodeConfig
	 * assistantNodeConfig=anConfigReader.getRandomConfiguration(); //
	 * AssistantNodeConfig assistantNodeConfig = new AssistantNodeConfig(37.73,
	 * 6.29, 6); return assistantNodeConfig; }
	 */

	private static List<Integer> splitValueRandomlyWithRWeights(int valueToSplit, int parts,
			List<Double> coefficients) {

		List<Integer> listValues = new ArrayList<>();
		Random random = new Random();

		double total = 0;
		for (Double coefficient : coefficients) {
			total += coefficient;
		}

		int remaining = valueToSplit;
		for (int i = 0; i < parts - 1; i++) {
			int part = (int) Math.round(valueToSplit * coefficients.get(i) / total);
			listValues.add(part);
			remaining -= part;
		}
		listValues.add(remaining);
		Collections.sort(listValues, Collections.reverseOrder());
		return listValues;
	}

	public static List<Integer> splitRandomlytoNotEqualParts1(int valueToSplit, int parts) {
		List<Integer> listValues = new ArrayList<>();
		Random random = new Random();

		int remaining = valueToSplit;
		for (int i = 0; i < parts - 1; i++) {
			int part = random.nextInt(remaining / (parts - i)) + 1;
			listValues.add(part);
			remaining -= part;
		}
		listValues.add(remaining);
		Collections.sort(listValues, Collections.reverseOrder());

		return listValues;
	}

	private static List<Integer> splitRandomlytoNotEqualParts2(int valueToSplit, int n) {
		List<Integer> listValues = new ArrayList<Integer>();
		Random randomSi = new Random(java.lang.System.currentTimeMillis());
		int subValue = valueToSplit;
		for (int i = 0; i < n - 1; i++) {
			int j = randomSi.nextInt(subValue - (n - i)) + 1;
			listValues.add(j);
			// System.out.println("SubValue_"+i+" : "+j);
			subValue -= j;
		}
		listValues.add(subValue);
		// System.out.println("SubValue_"+(n-1)+" : "+subValue);
		Collections.sort(listValues, Collections.reverseOrder());
		return listValues;
	}

	private static List<Integer> splitRandomlytoNotEqualParts3(int valueToSplit, int n) {
		List<Integer> listValues = new ArrayList<Integer>();

		int[] parts = new int[n];
		Random rand = new Random();
		int sum = 0;
		for (int i = 0; i < n - 1; i++) {
			parts[i] = rand.nextInt(valueToSplit - sum);
			sum += parts[i];
		}
		parts[n - 1] = valueToSplit - sum;
		for (int i = 0; i < n; i++) {
			listValues.add(parts[i]);

			System.out.println("Part " + (i + 1) + ": " + parts[i]);
		}
		Collections.sort(listValues, Collections.reverseOrder());
		return listValues;
	}

	private static List<Integer> splitValueEqually(int valueToSplit, int n) {
		List<Integer> listValues = new ArrayList<Integer>();
		int subValue = valueToSplit;

		if (subValue % n == 0) {
			int nbr = subValue / n;
			for (int i = 0; i < n; i++) {
				listValues.add(nbr);
				// System.out.println(nbr);
			}
		} else {
			int nbr = subValue / n;

			for (int i = 0; i < n - 1; i++) {
				listValues.add(nbr);
				// System.out.println(nbr);

			}
			listValues.add(subValue - (nbr * (n - 1)));

			// System.out.println("SubValue_"+(subValue-(nbr*(n-1))));
			Collections.sort(listValues, Collections.reverseOrder());
		}
		return listValues;
	}

	public static LinkedList<AbstractWorkload> split2EqualPartWithTouatiApp(AbstractWorkload workload,
			int numberOfResources) {
		// System.out.println("Split equally workload : ");
		// Split S to set of S_i
		List<Integer> listSi = splitValueEqually(workload.getSecretValue(), numberOfResources);
		// finalList.add(j*2); // H^S_i and e(g,g)^alpha^S_i

		List<List<Integer>> listOfListQy0i = new ArrayList<List<Integer>>();
		// for each attribute in Y Split Qy0 to set of Qy0_i
		for (Integer Qy0i : workload.getQy0Set()) {
			List<Integer> listQy0i = splitValueEqually(Qy0i, numberOfResources);
			listOfListQy0i.add(listQy0i);
		}

		// create SubWorkloads
		for (int i = 0; i < numberOfResources; i++) {
			AbstractWorkload subWorkload = new AbstractWorkload();
			int S_i = listSi.get(i);
			subWorkload.setSecretValue(S_i);

			for (List<Integer> listQy0_i : listOfListQy0i) {
				subWorkload.getQy0Set().add(listQy0_i.get(i));
			}
			workload.getSubWorkloads().add(subWorkload);
		}

		return workload.getSubWorkloads();
	}

	public static void printResourceInfo(Resource resource) {
		System.out.println("I'm resource with weight : " + resource.getCoreWeight() + ", Assistant Node ID is "
				+ resource.getAssistantNode().getID());

	}

	public static List<AbstractWorkload> splitWorkloadBOnResoucesWeights(AbstractWorkload workload,
			List<Resource> assignedResources) {
		// TODO Auto-generated method stub
		return null;
	}

}

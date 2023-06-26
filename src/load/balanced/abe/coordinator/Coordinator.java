package load.balanced.abe.coordinator;
      
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import load.balanced.abe.sensors.AbstractSensor;
import load.balanced.abe.simulation.StatisticManager;
import load.balanced.abe.simulation.UsedApproach;

/**
 * A Coordinator is composed of Assistant Nodes and a queue.
 */
public class Coordinator {
	private int ID  =0;
	private String description;
	private LinkedList<AssistantNode> assistantNodes = new LinkedList<AssistantNode>();
	private List<AbstractSensor> sensors = new ArrayList<AbstractSensor>();
	private Queue queue = new Queue();
	private Queue urgentMessageQueue = new Queue();
	private UsedApproach approach;
	private int waitTime =0;
	private StatisticManager statisticManager;   

/*	public Coordinator(int assistantNodeCount, int sensorCount,int minSecretValue,int maxSecretValue, int qy0Interval, int minNumberOfAttibutes, int maxNumberOfAttibutes) {
		
		for (int count = 1; count <= assistantNodeCount; count++) {
			
			// lire à partir d'un fichier
			File file =new File("input/cpu_models.xml");
			anConfigReader= new ANConfigReader(file.getAbsolutePath());
			AssistantNodeConfig assistantNodeConfig = getAssistantNodeConfig();
			if(assistantNodeConfig!=null) {
				AssistantNode assistantNode = new AssistantNode("AssistantNode_"+count,assistantNodeConfig);
				assistantNodes.addLast(assistantNode);
			}
		}
		sortAssistantNodes();
		for (int i = 0; i < sensorCount; i++) {
			AbstractSensor sensor = new AbstractSensor(i+1,minSecretValue,maxSecretValue, minNumberOfAttibutes,maxNumberOfAttibutes,qy0Interval);
			sensors.add(sensor);
		}
	}
	 */
	    
		public Coordinator(List<AssistantNodeConfig> assistantNodeConfigs) {//, int assistantNodeCount, int sensorCount,int minSecretValue,int maxSecretValue, int qy0Interval, int minNumberOfAttibutes, int maxNumberOfAttibutes) {
			int count =0;
			for (AssistantNodeConfig assistantNodeConfig : assistantNodeConfigs) {
				if(assistantNodeConfig!=null) {
					AssistantNode assistantNode = new AssistantNode("AssistantNode_"+count,assistantNodeConfig);
					assistantNodes.addLast(assistantNode);
				}
				//ID = count;
				count++;
			}
		}
	
	public void sortAssistantNodes() {
		Collections.sort(assistantNodes, new Comparator<AssistantNode>() {
			@Override
			public int compare(AssistantNode a1, AssistantNode a2) {
				// TODO Auto-generated method stub
				if(a1.getAssistantNodeConfig().getCoreWeight() < a2.getAssistantNodeConfig().getCoreWeight())
					return 1;
				else if(a1.getAssistantNodeConfig().getCoreWeight() > a2.getAssistantNodeConfig().getCoreWeight()) 
					return -1;
				else 
					return 0;
			}
		});
	}

	

	public void sortAssistantNodesByCoreWeight() {
		
	}
	public List<AssistantNode> getAssistantNodes() {
		return assistantNodes;
	}

	public int getResourceCount() {

      int somme =0;
      for (AssistantNode assistantNode : assistantNodes) {
		somme +=assistantNode.getResources().size();
	  }
		return somme;  
	}
	public List<AbstractSensor> getSensors() {
		return sensors;
	}

	public void setSensors(List<AbstractSensor> sensors) {
		this.sensors = sensors;
	}

	public Queue getQueue() {
		return queue;
	}

	public Queue getUrgentMessageQueue() {
		return urgentMessageQueue;
	}

	public boolean isThereAnyAssistantNodeWorks() {
		for (AssistantNode assistantNode : assistantNodes) {
			
			if(assistantNode.getAvailableResources().size() != assistantNode.getResources().size())
				return true;
			
		}
		return false;
	}
	public AssistantNode getFirstAvailableAssistantNode() {
		for (AssistantNode assistantNode : assistantNodes) {
			if (assistantNode.isAvailable()) {
				return assistantNode;
			}
		}
		return null;
	}
	
	public LinkedList<AssistantNode> getAvailableAssistantNodes() {
		LinkedList<AssistantNode> availableAssistantNodes = new LinkedList<AssistantNode>();
		for (AssistantNode assistantNode : assistantNodes) {
			if (assistantNode.isAvailable()) {
				availableAssistantNodes.addLast(assistantNode) ;
			}
		}
		return availableAssistantNodes;
	}

	public String toString() {
		StringBuffer results = new StringBuffer();
		results.append("Coordinator : "+ID + "\n");

		results.append("Regular Message Queue : \n"+queue.toString() + "\n");
		results.append("Urgent Message Queue : \n"+urgentMessageQueue.toString() + "\n");

		results.append("Availability of Assistant Nodes in the System: "+"\n");
		for (AssistantNode assistantNode : assistantNodes) {
			results.append(assistantNode.toString());
			results.append("\n");

		}
		return results.toString();
	}

	public UsedApproach getApproach() {
		return approach;
	}

	public void setApproach(UsedApproach approach) {
		this.approach = approach;
	}

	public AbstractSensor getSensor(int sensorNumber) {
		
		for (AbstractSensor sensor : sensors) {
			if(sensor.getID()== sensorNumber)
				return sensor;
		}
		return null;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getID() {
		return ID;
	}

	public int getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setStatisticManager(StatisticManager statisticManager) {

		this.statisticManager = statisticManager;
		
	}

	public StatisticManager getStatisticManager() {
		// TODO Auto-generated method stub
		return statisticManager;
	}

	
}
package load.balanced.abe.simulation;




import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import load.balanced.abe.coordinator.AssistantNodeConfig;

public class ANConfigReader {

	private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	private static DocumentBuilder docBuilder ;
	private static Document doc;
	public ANConfigReader(String path) {
		initialize(path);
	}
	private static void initialize(String path){
		
		if(docBuilder==null){
		
			try {
				docBuilder = factory.newDocumentBuilder();
					File f = new File(path);
					if(f.canRead()){
						doc = docBuilder.parse(new File(path));
				         doc.getDocumentElement().normalize();

					}
				
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException | IOException e) {
			    e.printStackTrace();
		    }
		}
	}
	public int getNumberOfConfigs() {
		if(doc.getDocumentElement().getNodeName().equals("cpu_models")) {
			NodeList nodeList =doc.getElementsByTagName("assistantNode");
			if(nodeList !=null) {
				return nodeList.getLength();
			}
		}
		return 0;
	}

	public ArrayList<AssistantNodeConfig> getNeededANConfigurations(int neededNbrOfConfigs) {
		ArrayList<AssistantNodeConfig> neededANConfigurations =null;
		// System.out.println(numberOfConfigurations);
		int exisingNumberOfConfigs = getNumberOfConfigs();
		if (neededNbrOfConfigs > 0 && neededNbrOfConfigs <= exisingNumberOfConfigs) {
			neededANConfigurations = new ArrayList<AssistantNodeConfig>();

			for (int selectedAssistantNode = 0; selectedAssistantNode < neededNbrOfConfigs; selectedAssistantNode++) {
				// int selectedAssistantNode = SimulationUtility.getRandomNumber(0,
				// numberOfConfigurations-1);
				//System.out.println(selectedAssistantNode);

				// System.out.println("Root element :" +
				// doc.getDocumentElement().getNodeName());
				NodeList nList = doc.getElementsByTagName("assistantNode");
				System.out.println("----------------------------");

				// for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(selectedAssistantNode);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					String cpu_model = eElement.getElementsByTagName("cpu_model").item(0).getTextContent().trim();

					String cores = eElement.getElementsByTagName("cores").item(0).getTextContent().trim();
					String threads = eElement.getElementsByTagName("threads").item(0).getTextContent().trim();
					String computer_gflops = eElement.getElementsByTagName("computer_gflops").item(0).getTextContent()
							.trim();
					String core_gflops = eElement.getElementsByTagName("core_gflops").item(0).getTextContent().trim();

					AssistantNodeConfig assistantNodeConfig = new AssistantNodeConfig(cpu_model,
							Integer.parseInt(cores), Integer.parseInt(threads), Double.parseDouble(computer_gflops),
							Double.parseDouble(core_gflops));

					System.out.println(
							"cpu_model : " + eElement.getElementsByTagName("cpu_model").item(0).getTextContent());
					System.out.println("cores : " + eElement.getElementsByTagName("cores").item(0).getTextContent());
					System.out
							.println("threads : " + eElement.getElementsByTagName("threads").item(0).getTextContent());
					System.out.println("computer_gflops : "
							+ eElement.getElementsByTagName("computer_gflops").item(0).getTextContent());
					System.out.println(
							"core_gflops : " + eElement.getElementsByTagName("core_gflops").item(0).getTextContent());
					neededANConfigurations.add(assistantNodeConfig);
				}
			}
		}

		
		return neededANConfigurations;
	}
	
	/***-
	 * 
	 * @return
	 */
	public AssistantNodeConfig getRandomConfiguration() {
		int max = getNumberOfConfigs();
		System.out.println("max"+max);

		if(max>0) {
		int selectedAssistantNode = SimulationUtility.getRandomNumber(0, max-1);
		System.out.println("selectedAssistantNode"+selectedAssistantNode);


       // System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
        NodeList nList = doc.getElementsByTagName("assistantNode");
        System.out.println("----------------------------");
        
       // for (int temp = 0; temp < nList.getLength(); temp++) {
           Node nNode = nList.item(selectedAssistantNode);
           if (nNode.getNodeType() == Node.ELEMENT_NODE) {
              Element eElement = (Element) nNode;
            
              String cpu_model = eElement.getElementsByTagName("cpu_model").item(0).getTextContent().trim();
              
              String cores = eElement.getElementsByTagName("cores").item(0).getTextContent().trim();
              String threads = eElement.getElementsByTagName("threads").item(0).getTextContent().trim();
              String computer_gflops = eElement.getElementsByTagName("computer_gflops").item(0).getTextContent().trim();
              String core_gflops = eElement.getElementsByTagName("core_gflops").item(0).getTextContent().trim();

              
      		AssistantNodeConfig assistantNodeConfig =new AssistantNodeConfig(cpu_model, 
      				Integer.parseInt(cores), 
      				Integer.parseInt(threads), 
      				Double.parseDouble(computer_gflops), 
      				Double.parseDouble(core_gflops));

      		 System.out.println("cpu_model : " 
                     + eElement
                     .getElementsByTagName("cpu_model")
                     .item(0)
                     .getTextContent());
              System.out.println("cores : " 
                 + eElement
                 .getElementsByTagName("cores")
                 .item(0)
                 .getTextContent());
              System.out.println("threads : " 
                 + eElement
                 .getElementsByTagName("threads")
                 .item(0)
                 .getTextContent());
              System.out.println("computer_gflops : " 
                 + eElement
                 .getElementsByTagName("computer_gflops")
                 .item(0)
                 .getTextContent());
              System.out.println("core_gflops : " 
                      + eElement
                      .getElementsByTagName("core_gflops")
                      .item(0)
                      .getTextContent());
          	return assistantNodeConfig;
           }
		}
	
		
	//	}
		return null;
	}
	
	public static void main(String[] args) {
		File file = new File("input/cpu_models.xml");
		ANConfigReader ANConfigReader= new ANConfigReader(file.getAbsolutePath());
		AssistantNodeConfig conf = ANConfigReader.getRandomConfiguration();
	}
}

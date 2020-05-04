package LogFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Filter {
	
	private static Path roadPrice;
	private static Path roadReservations;
	private static Path agentPriorityMoney;
	private static Path agentPriorityDistance;
	private static Path agentPriorityTime;
	private static Path agentCurrentToReserved;
	
	private static DocumentBuilderFactory docBuilderFactory;
	private static  DocumentBuilder docBuilder;
	private static XPath xPath;

	public Filter(){
		initializePaths();
	    docBuilderFactory = DocumentBuilderFactory.newInstance();
	    xPath =  XPathFactory.newInstance().newXPath();
	}
	
	public void roadPriceFilter(){
		
		try{
			 docBuilder = docBuilderFactory.newDocumentBuilder();
			 Document doc = docBuilder.parse(new FileInputStream(roadPrice.toFile()));
			 doc.getDocumentElement().normalize();
			 
			 Document filtered = docBuilder.newDocument();
			 Node root = filtered.createElement("List");
			 filtered.appendChild(root);
			 
			 Object result = xPath.compile("/roadCostPerStep/step2/road[@cost = '5.0']").evaluate(doc, XPathConstants.NODESET);
			 NodeList nodes = (NodeList) result;
		 
		    for (int i = 0; i < nodes.getLength(); i++) {

		        Node node = nodes.item(i);
		        filtered.adoptNode(node);
		        root.appendChild(node);

		    }
		    try{
		        Transformer tf = TransformerFactory.newInstance().newTransformer();
		        tf.setOutputProperty(OutputKeys.INDENT, "yes");
		        tf.setOutputProperty(OutputKeys.METHOD, "xml");
		        tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		        DOMSource domSource = new DOMSource(filtered);
		        StreamResult sr = new StreamResult(new File("./stats/result_roadPrice.xml"));
		        tf.transform(domSource, sr);
		        
		    } catch (TransformerException ex) {
		        ex.printStackTrace();
		    }
		    
		    
		}catch(IOException e){
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}
	
	
	
	public void roadReservationsFilter(){
		
		try{
			 docBuilder = docBuilderFactory.newDocumentBuilder();
			 Document doc = docBuilder.parse(new FileInputStream(roadReservations.toFile()));
			 doc.getDocumentElement().normalize();
			 
			 Document filtered = docBuilder.newDocument();
			 Node root = filtered.createElement("List");
			 filtered.appendChild(root);
			 
			 Object result = xPath.compile("/reservationsPerStep/step2[@roads_full = '0']").evaluate(doc, XPathConstants.NODESET);
			 NodeList nodes = (NodeList) result;
		 
		    for (int i = 0; i < nodes.getLength(); i++) {

		        Node node = nodes.item(i);
		        filtered.adoptNode(node);
		        root.appendChild(node);

		    }
		    try{
		        Transformer tf = TransformerFactory.newInstance().newTransformer();
		        tf.setOutputProperty(OutputKeys.INDENT, "yes");
		        tf.setOutputProperty(OutputKeys.METHOD, "xml");
		        tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		        DOMSource domSource = new DOMSource(filtered);
		        StreamResult sr = new StreamResult(new File("./stats/result_roadReservations.xml"));
		        tf.transform(domSource, sr);
		        
		    } catch (TransformerException ex) {
		        ex.printStackTrace();
		    }
		    
		    
		}catch(IOException e){
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		
	}
	public void currentToReservedFilter(){
		
		try{
			 docBuilder = docBuilderFactory.newDocumentBuilder();
			 Document doc = docBuilder.parse(new FileInputStream(agentCurrentToReserved.toFile()));
			 doc.getDocumentElement().normalize();
			 
			 Document filtered = docBuilder.newDocument();
			 Node root = filtered.createElement("List");
			 filtered.appendChild(root);
			 
			 Object result = xPath.compile("/roadCostPerStep/step277[@CurrentPrice='0.08124999701976776']").evaluate(doc, XPathConstants.NODESET);
			 NodeList nodes = (NodeList) result;
		 
		    for (int i = 0; i < nodes.getLength(); i++) {

		        Node node = nodes.item(i);
		        filtered.adoptNode(node);
		        root.appendChild(node);

		    }
		    try{
		        Transformer tf = TransformerFactory.newInstance().newTransformer();
		        tf.setOutputProperty(OutputKeys.INDENT, "yes");
		        tf.setOutputProperty(OutputKeys.METHOD, "xml");
		        tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		        DOMSource domSource = new DOMSource(filtered);
		        StreamResult sr = new StreamResult(new File("./stats/result_currentToReserved.xml"));
		        tf.transform(domSource, sr);
		        
		    } catch (TransformerException ex) {
		        ex.printStackTrace();
		    }
		    
		    
		}catch(IOException e){
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	
	public void agentMoneyPriorityFilter(){
		
		try{
			 docBuilder = docBuilderFactory.newDocumentBuilder();
			 Document doc = docBuilder.parse(new FileInputStream(agentPriorityMoney.toFile()));
			 doc.getDocumentElement().normalize();
			 
			 Document filtered = docBuilder.newDocument();
			 Node root = filtered.createElement("List");
			 filtered.appendChild(root);
			 
			 Object result = xPath.compile("/agents_paths/agent2").evaluate(doc, XPathConstants.NODESET);
			 NodeList nodes = (NodeList) result;
		 
		    for (int i = 0; i < nodes.getLength(); i++) {

		        Node node = nodes.item(i);
		        filtered.adoptNode(node);
		        root.appendChild(node);

		    }
		    try{
		        Transformer tf = TransformerFactory.newInstance().newTransformer();
		        tf.setOutputProperty(OutputKeys.INDENT, "yes");
		        tf.setOutputProperty(OutputKeys.METHOD, "xml");
		        tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		        DOMSource domSource = new DOMSource(filtered);
		        StreamResult sr = new StreamResult(new File("./stats/result_CostPriority.xml"));
		        tf.transform(domSource, sr);
		        
		    } catch (TransformerException ex) {
		        ex.printStackTrace();
		    }
		    
		    
		}catch(IOException e){
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public void agentTimePriorityFilter(){
		
		try{
			 docBuilder = docBuilderFactory.newDocumentBuilder();
			 Document doc = docBuilder.parse(new FileInputStream(agentPriorityTime.toFile()));
			 doc.getDocumentElement().normalize();
			 
			 Document filtered = docBuilder.newDocument();
			 Node root = filtered.createElement("List");
			 filtered.appendChild(root);
			 
			 Object result = xPath.compile("/agents_paths/agent1").evaluate(doc, XPathConstants.NODESET);
			 NodeList nodes = (NodeList) result;
		 
		    for (int i = 0; i < nodes.getLength(); i++) {

		        Node node = nodes.item(i);
		        filtered.adoptNode(node);
		        root.appendChild(node);

		    }
		    try{
		        Transformer tf = TransformerFactory.newInstance().newTransformer();
		        tf.setOutputProperty(OutputKeys.INDENT, "yes");
		        tf.setOutputProperty(OutputKeys.METHOD, "xml");
		        tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		        DOMSource domSource = new DOMSource(filtered);
		        StreamResult sr = new StreamResult(new File("./stats/result_TimePriority.xml"));
		        tf.transform(domSource, sr);
		        
		    } catch (TransformerException ex) {
		        ex.printStackTrace();
		    }
		    
		    
		}catch(IOException e){
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void agentDistancePriorityFilter(){
		
		try{
			 docBuilder = docBuilderFactory.newDocumentBuilder();
			 Document doc = docBuilder.parse(new FileInputStream(agentPriorityDistance.toFile()));
			 doc.getDocumentElement().normalize();
			 
			 Document filtered = docBuilder.newDocument();
			 Node root = filtered.createElement("List");
			 filtered.appendChild(root);
			 
			 Object result = xPath.compile("/agents_paths/agent0").evaluate(doc, XPathConstants.NODESET);
			 NodeList nodes = (NodeList) result;
		 
		    for (int i = 0; i < nodes.getLength(); i++) {

		        Node node = nodes.item(i);
		        filtered.adoptNode(node);
		        root.appendChild(node);

		    }
		    try{
		        Transformer tf = TransformerFactory.newInstance().newTransformer();
		        tf.setOutputProperty(OutputKeys.INDENT, "yes");
		        tf.setOutputProperty(OutputKeys.METHOD, "xml");
		        tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		        DOMSource domSource = new DOMSource(filtered);
		        StreamResult sr = new StreamResult(new File("./stats/result_DistancePriority.xml"));
		        tf.transform(domSource, sr);
		        
		    } catch (TransformerException ex) {
		        ex.printStackTrace();
		    }
		    
		    
		}catch(IOException e){
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void initializePaths(){
		roadPrice = Paths.get("./stats/roads_price.xml");
		roadReservations = Paths.get("./stats/roads_reservations.xml");
		agentCurrentToReserved = Paths.get("./stats/Current_to_reserved.xml");
		agentPriorityMoney = Paths.get("./stats/priority/agents_money.xml");
		agentPriorityDistance = Paths.get("./stats/priority/agents_distance.xml");
		agentPriorityTime = Paths.get("./stats/priority/agents_time.xml");
	}	

}

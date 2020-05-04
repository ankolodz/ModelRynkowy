package agentMarketTrafficSimulation.context.generators;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import agentMarketTrafficSimulation.agent.DefaultAgent;
import agentMarketTrafficSimulation.context.ContextManager;
import agentMarketTrafficSimulation.environment.fixedgeography.Junction;
import agentMarketTrafficSimulation.environment.fixedgeography.NetworkEdge;
import agentMarketTrafficSimulation.environment.fixedgeography.Road;
import agentMarketTrafficSimulation.utils.DijkstraAlgorithm;
import agentMarketTrafficSimulation.utils.Graph;
import agentMarketTrafficSimulation.utils.TravelUtils;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.collections.IndexedIterable;

public class XMLStatsGenerator {

	private static int stepNum = 0;
	private static Path roadPrice;
	private static Path roadReservations;
	private static Path agentPriorityMoney;
	private static Path agentPriorityDistance;
	private static Path agentPriorityTime;
	private static Path agentCurrentToReserved;

	private boolean createdReservations = false;
	private boolean createdPrice = false;
	private boolean createdAgentsPrice = false;
	private boolean createdAgentsTime = false;
	private boolean createdAgentsDistance = false;
	private boolean createdCurrentToReserved = false;

	public void generateStats(ContextManager contextManager) {
		if (stepNum == 0) {
			initializePaths();
			clearFiles();
		}

		try {
			Iterable<DefaultAgent> agents = ContextManager.getAgentContext().getObjects(DefaultAgent.class);

			for (DefaultAgent agent : agents) {

				if (!agent.hasReservations() && agent.started()) {
					if (agent.getCurrentJunction() != agent.getLastJunction()) {
						generateAgentStats(agent);
					}

				}

			}
			generateCurrentToReservedPrice(agents);
			generateRoadStats(ContextManager.getRoadContext().getObjects(Road.class));
		} catch (IOException | SAXException e) {
			System.out.println(e.getStackTrace());
		}
		stepNum++;
	}

	private void generateAgentStats(DefaultAgent agent) throws IOException {
		if (!agent.isTravelEnded()) {

			if (agent.getTimePriority() > agent.getCostPriority() + agent.getDistancePriority()) {
				try {
					countTimeAgentWeight(agent, agentPriorityTime);
				} catch (SAXException e) {
					e.printStackTrace();
				}
			} else if (agent.getCostPriority() > agent.getTimePriority() + agent.getDistancePriority()) {
				try {
					countPriceAgentWeight(agent, agentPriorityMoney);
				} catch (SAXException e) {
					e.printStackTrace();
				}
			} else if (agent.getDistancePriority() > agent.getCostPriority() + agent.getTimePriority()) {
				try {
					countDistanceAgentWeight(agent, agentPriorityDistance);
				} catch (SAXException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private List<RepastEdge<Junction>> findPath(DefaultAgent agent, Junction junction, Junction destination) {

		List<RepastEdge<Junction>> shortestPath = TravelUtils.getShortestRoute(junction, destination,
				agent.getTimePriority(), agent.getCostPriority(), agent.getDistancePriority(), agent);
		return shortestPath;
	}

	private void countTimeAgentWeight(DefaultAgent agent, Path path) throws SAXException, IOException {

		DijkstraAlgorithm da = new DijkstraAlgorithm(new Graph(ContextManager.getAllRoads()));
		List<RepastEdge<Junction>> junctions = null;

		NetworkEdge<Junction> edge;
		Road road;
		double totalWeight = 0.0;
		double edgeWeight = 0;
		double reservationPrice = 0;

		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("agents_paths");

			if (createdAgentsTime) {
				junctions = findPath(agent, agent.getCurrentJunction(), agent.getEndJunction());
				doc = docBuilder.parse(path.toString());
				rootElement = doc.getDocumentElement();

			} else {
				junctions = findPath(agent, agent.getStartJunction(), agent.getEndJunction());
				doc.appendChild(rootElement);
			}

			Element agentElement = doc.createElement("agent" + Integer.toString(agent.getId()));
			agentElement.setAttribute("agent_id", Integer.toString(agent.getId()));
			rootElement.appendChild(agentElement);
			agentElement.setAttribute("agent_type", agent.getAgentTypeString());

			Element newPath = doc.createElement("path");
			newPath.setAttribute("step", Integer.toString(stepNum));

			for (RepastEdge<Junction> e : junctions) {

				edge = (NetworkEdge<Junction>) e;
				road = edge.getRoad();
				reservationPrice += road.getOwner().getPriceForRoad(road);

				edgeWeight = da.getDistance(road.getJunctions().get(0),
						road.getJunctions().get(road.getJunctions().size() - 1), agent.getTimePriority(),
						agent.getCostPriority(), agent.getDistancePriority(), agent);
				totalWeight += edgeWeight;

				Element edgeElement = doc.createElement("edge");

				Element startJunction = doc.createElement("startJunction");
				startJunction.setAttribute("JunctionID", road.getStartPointID());
				startJunction.setAttribute("Coord_X", road.getStartCoordX());
				startJunction.setAttribute("Coord_Y", road.getStartCoordY());

				Element endJunction = doc.createElement("endJunction");
				endJunction.setAttribute("JunctionID", road.getEndPointID());
				endJunction.setAttribute("Coord_X", road.getEndCoordX());
				endJunction.setAttribute("Coord_Y", road.getEndCoordY());

				edgeElement.setAttribute("edge_weight", Double.toString(edgeWeight));

				edgeElement.appendChild(startJunction);
				edgeElement.appendChild(endJunction);
				newPath.appendChild(edgeElement);
			}

			newPath.setAttribute("reservation_cost", Double.toString((float) reservationPrice));
			newPath.setAttribute("path_weight", Double.toString(totalWeight));
			agentElement.appendChild(newPath);

			if (!createdAgentsTime) {
				createdAgentsTime = true;
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(path.toString());
			transformer.transform(source, result);
			// }
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
		agent.setLastJunction(agent.getCurrentJunction());
	}

	private void countDistanceAgentWeight(DefaultAgent agent, Path path) throws SAXException, IOException {

		DijkstraAlgorithm da = new DijkstraAlgorithm(new Graph(ContextManager.getAllRoads()));
		List<RepastEdge<Junction>> junctions = null;// findPath(agent, agent.getStartJunction(),
													// agent.getEndJunction());
		NetworkEdge<Junction> edge;
		Road road;
		double totalWeight = 0.0;
		double edgeWeight = 0;

		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("agents_paths");

			if (createdAgentsDistance) {
				junctions = findPath(agent, agent.getCurrentJunction(), agent.getEndJunction());
				doc = docBuilder.parse(path.toString());
				rootElement = doc.getDocumentElement();
			} else {
				junctions = findPath(agent, agent.getStartJunction(), agent.getEndJunction());
				doc.appendChild(rootElement);
			}

			Element agentElement = doc.createElement("agent" + Integer.toString(agent.getId()));
			agentElement.setAttribute("agent_id", Integer.toString(agent.getId()));
			rootElement.appendChild(agentElement);

			Element newPath = doc.createElement("path");

			for (RepastEdge<Junction> e : junctions) {

				edge = (NetworkEdge<Junction>) e;
				road = edge.getRoad();
				edgeWeight = da.getDistance(road.getJunctions().get(0),
						road.getJunctions().get(road.getJunctions().size() - 1), agent.getTimePriority(),
						agent.getCostPriority(), agent.getDistancePriority(), agent);
				totalWeight += edgeWeight;

				Element edgeElement = doc.createElement("edge");

				Element startJunction = doc.createElement("startJunction");
				startJunction.setAttribute("JunctionID", road.getStartPointID());
				startJunction.setAttribute("Coord_X", road.getStartCoordX());
				startJunction.setAttribute("Coord_Y", road.getStartCoordY());

				Element endJunction = doc.createElement("endJunction");
				endJunction.setAttribute("JunctionID", road.getEndPointID());
				endJunction.setAttribute("Coord_X", road.getEndCoordX());
				endJunction.setAttribute("Coord_Y", road.getEndCoordY());

				edgeElement.setAttribute("edge_weight", Double.toString(edgeWeight));

				edgeElement.appendChild(startJunction);
				edgeElement.appendChild(endJunction);
				newPath.appendChild(edgeElement);
			}

			newPath.setAttribute("path_weight", Double.toString(totalWeight));
			agentElement.appendChild(newPath);

			if (!createdAgentsDistance) {
				createdAgentsDistance = true;
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(path.toString());
			transformer.transform(source, result);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
		agent.setLastJunction(agent.getCurrentJunction());
	}

	private void countPriceAgentWeight(DefaultAgent agent, Path path) throws SAXException, IOException {
		DijkstraAlgorithm da = new DijkstraAlgorithm(new Graph(ContextManager.getAllRoads()));
		List<RepastEdge<Junction>> junctions = null;// findPath(agent, agent.getStartJunction(),
													// agent.getEndJunction());
		NetworkEdge<Junction> edge;
		Road road;
		double totalWeight = 0.0;
		double edgeWeight = 0;

		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = null;
			Element rootElement = null;

			if (createdAgentsPrice) {
				junctions = findPath(agent, agent.getCurrentJunction(), agent.getEndJunction());
				doc = docBuilder.parse(path.toString());
				rootElement = doc.getDocumentElement();
			} else {
				junctions = findPath(agent, agent.getStartJunction(), agent.getEndJunction());
				doc = docBuilder.newDocument();
				rootElement = doc.createElement("agents_paths");
				doc.appendChild(rootElement);
			}

			Element agentElement = doc.createElement("agent" + Integer.toString(agent.getId()));
			agentElement.setAttribute("agent_id", Integer.toString(agent.getId()));
			rootElement.appendChild(agentElement);

			Element newPath = doc.createElement("path");

			for (RepastEdge<Junction> e : junctions) {

				edge = (NetworkEdge<Junction>) e;
				road = edge.getRoad();
				edgeWeight = da.getDistance(road.getJunctions().get(0),
						road.getJunctions().get(road.getJunctions().size() - 1), agent.getTimePriority(),
						agent.getCostPriority(), agent.getDistancePriority(), agent);
				totalWeight += edgeWeight;

				Element edgeElement = doc.createElement("edge");

				Element startJunction = doc.createElement("startJunction");
				startJunction.setAttribute("JunctionID", road.getStartPointID());
				startJunction.setAttribute("Coord_X", road.getStartCoordX());
				startJunction.setAttribute("Coord_Y", road.getStartCoordY());

				Element endJunction = doc.createElement("endJunction");
				endJunction.setAttribute("JunctionID", road.getEndPointID());
				endJunction.setAttribute("Coord_X", road.getEndCoordX());
				endJunction.setAttribute("Coord_Y", road.getEndCoordY());

				edgeElement.setAttribute("edge_weight", Double.toString(edgeWeight));

				edgeElement.appendChild(startJunction);
				edgeElement.appendChild(endJunction);
				newPath.appendChild(edgeElement);
			}

			newPath.setAttribute("path_weight", Double.toString(totalWeight));
			agentElement.appendChild(newPath);

			if (!createdAgentsPrice) {
				createdAgentsPrice = true;
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(path.toString());
			transformer.transform(source, result);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
		agent.setLastJunction(agent.getCurrentJunction());
	}

	private void generateCurrentToReservedPrice(Iterable<DefaultAgent> agents) throws IOException, SAXException {
		Double currentSum = 0.0;
		Double reservedSum = 0.0;
		int count = 0;
		for (DefaultAgent agent : agents) {
			currentSum += agent.getCurrentPrice();
			reservedSum += agent.getReservationPrice();
			count++;
		}

		if (currentSum > 0 && reservedSum > 0) {

			try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

				Document doc = null;
				Element rootElement = null;

				if (createdCurrentToReserved) {
					doc = docBuilder.parse(agentCurrentToReserved.toString());
					rootElement = doc.getDocumentElement();
				} else {
					doc = docBuilder.newDocument();
					rootElement = doc.createElement("CurrentToReservedPrice");
					doc.appendChild(rootElement);
				}

				Element stepElement = doc.createElement("step" + Integer.toString(stepNum));
				stepElement.setAttribute("step", Integer.toString(stepNum));
				stepElement.setAttribute("CurrentPrice", Double.toString((float) (currentSum / count)));
				stepElement.setAttribute("ReservedPrice", Double.toString((float) (reservedSum / count)));

				rootElement.appendChild(stepElement);

				if (!createdCurrentToReserved) {
					createdCurrentToReserved = true;
				}

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(agentCurrentToReserved.toString());
				transformer.transform(source, result);

			} catch (ParserConfigurationException pce) {
				pce.printStackTrace();
			} catch (TransformerException tfe) {
				tfe.printStackTrace();
			}
		}
	}

	private void generateRoadStats(IndexedIterable<Road> roads) throws IOException, SAXException {
		getPrice(roads);
		getReservations(roads);
	}

	private void getPrice(IndexedIterable<Road> roads) throws IOException, SAXException {

		int totalSum = 0;
		double maxPrice = 0;
		double minPrice = Double.MAX_VALUE;

		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = null;
			Element rootElement = null;

			if (createdPrice) {
				doc = docBuilder.parse("./stats/roads_price.xml");
				rootElement = doc.getDocumentElement();
			} else {
				doc = docBuilder.newDocument();
				rootElement = doc.createElement("roadCostPerStep");
				doc.appendChild(rootElement);
			}

			Element stepElement = doc.createElement("step" + Integer.toString(stepNum));
			stepElement.setAttribute("step", Integer.toString(stepNum));

			for (Road road : roads) {

				double Price = road.getOwner().getPriceForRoad(road);

				if (Price > maxPrice) {
					maxPrice = Price;
				}
				if (Price < minPrice) {
					minPrice = Price;
				}

				totalSum += Price;

				if (Price != road.getOwner().getBasePrice()) {

					Element newRoad = doc.createElement("road");

					Element startJunction = doc.createElement("startJunction");
					startJunction.setAttribute("JunctionID", road.getStartPointID());
					startJunction.setAttribute("Coord_X", road.getStartCoordX());
					startJunction.setAttribute("Coord_Y", road.getStartCoordY());

					Element endJunction = doc.createElement("endJunction");
					endJunction.setAttribute("JunctionID", road.getEndPointID());
					endJunction.setAttribute("Coord_X", road.getEndCoordX());
					endJunction.setAttribute("Coord_Y", road.getEndCoordY());

					newRoad.appendChild(startJunction);
					newRoad.appendChild(endJunction);
					newRoad.setAttribute("cost", Double.toString(Price));

					stepElement.appendChild(newRoad);

					if (!createdPrice) {
						createdPrice = true;
					}
				}
			}
			// --------------summary--------------
			Element stepSummary = doc.createElement("summary");
			stepSummary.setAttribute("step", Integer.toString(stepNum));
			stepSummary.setAttribute("average_price", Double.toString((float) totalSum / roads.size()));
			stepSummary.setAttribute("max_price", Double.toString(maxPrice));
			stepSummary.setAttribute("min_price", Double.toString(minPrice));
			stepElement.appendChild(stepSummary);

			rootElement.appendChild(stepElement);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult("./stats/roads_price.xml");
			transformer.transform(source, result);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}

	}

	private void getReservations(IndexedIterable<Road> roads) throws IOException, SAXException {
		int Reservations = 0;
		int blockedRoads = 0;

		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = null;
			Element rootElement = null;

			if (createdReservations) {
				doc = docBuilder.parse("./stats/roads_reservations.xml");
				rootElement = doc.getDocumentElement();
			} else {
				doc = docBuilder.newDocument();
				rootElement = doc.createElement("reservationsPerStep");
				doc.appendChild(rootElement);
			}

			Element stepElement = doc.createElement("step" + Integer.toString(stepNum));
			stepElement.setAttribute("step", Integer.toString(stepNum));

			for (Road road : roads) {

				Reservations = road.getRoadReservations();
				if (Reservations > 0) {
					if (road.isBlocked()) {
						blockedRoads++;
					}

					Element newReservation = doc.createElement("reservation");

					newReservation.setAttribute("step", Integer.toString(stepNum));

					Element startJunction = doc.createElement("startJunction");
					startJunction.setAttribute("JunctionID", road.getStartPointID());
					startJunction.setAttribute("Coord_X", road.getStartCoordX());
					startJunction.setAttribute("Coord_Y", road.getStartCoordY());

					Element endJunction = doc.createElement("endJunction");
					endJunction.setAttribute("JunctionID", road.getEndPointID());
					endJunction.setAttribute("Coord_X", road.getEndCoordX());
					endJunction.setAttribute("Coord_Y", road.getEndCoordY());

					newReservation.setAttribute("rezervation_count", Integer.toString(Reservations));
					newReservation.appendChild(startJunction);
					newReservation.appendChild(endJunction);

					stepElement.appendChild(newReservation);
					stepElement.setAttribute("roads_full", Integer.toString(blockedRoads));

				}
			}

			rootElement.appendChild(stepElement);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult("./stats/roads_reservations.xml");
			transformer.transform(source, result);

			if (!createdReservations) {
				createdReservations = true;
			}

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}

	private void initializePaths() {
		File dir = new File("stats");
		dir.mkdir();
		roadPrice = Paths.get("./stats/roads_price.xml");
		roadReservations = Paths.get("./stats/roads_reservations.xml");
		agentCurrentToReserved = Paths.get("./stats/Current_to_reserved.xml");
		agentPriorityMoney = Paths.get("./stats/priority/agents_money.xml");
		agentPriorityDistance = Paths.get("./stats/priority/agents_distance.xml");
		agentPriorityTime = Paths.get("./stats/priority/agents_time.xml");

	}

	private void clearFiles() {
		roadPrice.toFile().delete();
		roadReservations.toFile().delete();
		agentPriorityMoney.toFile().delete();
		agentPriorityDistance.toFile().delete();
		agentPriorityTime.toFile().delete();
		agentCurrentToReserved.toFile().delete();
	}
}

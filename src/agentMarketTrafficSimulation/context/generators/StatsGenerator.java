package agentMarketTrafficSimulation.context.generators;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

//import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;
import java.util.*;

import agentMarketTrafficSimulation.agent.AgentType;
import agentMarketTrafficSimulation.agent.DefaultAgent;
import agentMarketTrafficSimulation.context.ContextManager;
import agentMarketTrafficSimulation.environment.fixedgeography.Road;
import repast.simphony.util.collections.IndexedIterable;

public class StatsGenerator {

	private static int stepNum = 0;
	private static Path agentMoney;
	private static Path agentTravelEnded;
	private static Path agentWaiting;
	private static Path agentDistance;
	private static Path agentTime;
	private static Path roadPrice;
	private static Path roadReservations;
	private static Path agentPriorityMoney;
	private static Path agentPriorityDistance;
	private static Path agentPriorityTime;
	private static Path agentCurrentToReserved;
	private static Path roadReservationsQuantiles;

	public void generateStats(ContextManager contextManager) {
		if (stepNum == 0) {
			initializePaths();
			clearFiles();
			List<String> lines = new LinkedList<>();
			lines.add("   step\t time\t cost\tdistance");
			try {
				saveToFile(agentPriorityMoney, lines);
				saveToFile(agentPriorityDistance, lines);
				saveToFile(agentPriorityTime, lines);
			} catch (IOException e) {
				e.printStackTrace();
			}
			lines.clear();
			StringBuilder sb = new StringBuilder();
			sb.append("   step");
			for (AgentType t : AgentType.values()) {
				sb.append("\t " + t);
			}
			lines.add(sb.toString());
			try {
				saveToFile(agentMoney, lines);
				saveToFile(agentDistance, lines);
				saveToFile(agentTime, lines);
				saveToFile(agentWaiting, lines);
				saveToFile(agentTravelEnded, lines);
			} catch (IOException e) {
				e.printStackTrace();
			}

			lines.clear();
			lines.add("step\t currentPrice\t reservedPrice\t alternativeChosen");
			try {
				saveToFile(agentCurrentToReserved, lines);
			} catch (IOException e) {
				e.printStackTrace();
			}
			lines.clear();
			lines.add("step\t averageRoadPrice\t maxPrice\t variation");
			try {
				saveToFile(roadPrice, lines);
			} catch (IOException e) {
				e.printStackTrace();
			}
			lines.clear();
			lines.add("step\t averageReservationCount\t blockedRoads\t maxReservations\t variation\t "
					+ ContextManager.getRoadContext().getObjects(Road.class).size());
			try {
				saveToFile(roadReservations, lines);
			} catch (IOException e) {
				e.printStackTrace();
			}
			lines.clear();
			lines.add("step\t five\t six\t seven\t eight\t nine");
			try {
				saveToFile(roadReservationsQuantiles, lines);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		try {
			generateAgentStats(ContextManager.getAgentContext().getObjects(DefaultAgent.class));
			generateRoadStats(ContextManager.getRoadContext().getObjects(Road.class));
		} catch (IOException e) {
			e.printStackTrace();
		}
		stepNum++;
	}

	private void generateAgentStats(Iterable<DefaultAgent> agents) throws IOException {
		countTotalMoney(agents, agentMoney, agentPriorityMoney);
		countAgentsEnded(agents, agentTravelEnded);
		countAgentsWaiting(agents, agentWaiting);
		getTravelDistance(agents, agentDistance, agentPriorityDistance);
		getTravelTime(agents, agentTime, agentPriorityTime);
		getCurrentToReserved(agents);
	}

	private Iterable<DefaultAgent> getAgentsByType(Iterable<DefaultAgent> agents, AgentType type) {
		List<DefaultAgent> agentsByType = new LinkedList<>();
		for (DefaultAgent agent : agents) {
			if (agent.getAgentType().equals(type)) {
				agentsByType.add(agent);
			}
		}
		return agentsByType;
	}

	private Iterable<DefaultAgent> getAgentsByPriority(Iterable<DefaultAgent> agents, int type) {
		List<DefaultAgent> agentsByPriority = new LinkedList<>();
		for (DefaultAgent agent : agents) {
			if (type == 0) {
				if (agent.getTimePriority() > agent.getCostPriority()
						&& agent.getTimePriority() > agent.getDistancePriority()) {
					agentsByPriority.add(agent);
				}
			} else if (type == 1) {
				if (agent.getCostPriority() > agent.getTimePriority()
						&& agent.getCostPriority() > agent.getDistancePriority()) {
					agentsByPriority.add(agent);
				}
			} else {
				if (agent.getDistancePriority() > agent.getCostPriority()
						&& agent.getDistancePriority() > agent.getTimePriority()) {
					agentsByPriority.add(agent);
				}
			}
		}
		return agentsByPriority;
	}

	private void getCurrentToReserved(Iterable<DefaultAgent> agents) throws IOException {
		List<String> lines = new ArrayList<>();
		Double currentSum = 0.0;
		Double reservedSum = 0.0;
		int count = 0;
		int alternativeChosen = 0;
		for (DefaultAgent agent : agents) {
			currentSum += agent.getCurrentPrice();
			reservedSum += agent.getReservationPrice();
			count++;
			alternativeChosen += agent.getAlternativeChosen();
			agent.setAlternativeChosen(0);
		}
		lines.add(stepNum + "\t" + (double) currentSum / count + "\t" + (double) reservedSum / count + "\t"
				+ alternativeChosen);
		saveToFile(agentCurrentToReserved, lines);

	}

	private void generateRoadStats(IndexedIterable<Road> roads) throws IOException {
		getPrice(roads);
		getReservations(roads);
		getReservationsQuantiles(roads);
	}
	
	private void getReservationsQuantiles(IndexedIterable<Road> roads) throws IOException {
		ArrayList<Integer> roadsLoad = new ArrayList<>();
		
		for (Road road : roads) {
			roadsLoad.add(road.getAgentsOnRoad());
		}
		Collections.sort(roadsLoad);

		int first = roadsLoad.size()/10;
		int median = 5*first;
		int sixth = 6*first;
		int seventh = 7*first;
		int eighth = 8*first;
		int ninth = 9*first;
		
		List<String> lines = new ArrayList<>();
		lines.add(stepNum + "\t" + roadsLoad.get(median) + "\t" + roadsLoad.get(sixth) + "\t" + roadsLoad.get(seventh) + "\t" + roadsLoad.get(eighth) + "\t" + roadsLoad.get(ninth));
		saveToFile(roadReservationsQuantiles, lines);
	}

	private void getPrice(IndexedIterable<Road> roads) throws IOException {
		List<String> lines = new ArrayList<>();
		int totalSum = 0;
		double maxPrice = 0;
		double price = 0;
		double variation = 0.0;
		double averagePrice = 0.0;
		for (Road road : roads) {
			price = road.getOwner().getPriceForRoad(road);
			totalSum += price;
			if (maxPrice < price) {
				maxPrice = price;
			}
		}

		averagePrice = totalSum / roads.size();
		for (Road road : roads) {
			variation += Math.pow((road.getOwner().getPriceForRoad(road) - averagePrice), 2);
		}

		variation = variation / roads.size();
		variation = Math.sqrt(variation);

		lines.add(stepNum + "\t" + (double) averagePrice + "\t" + maxPrice + "\t" + variation);
		saveToFile(roadPrice, lines);
	}

	private void getReservations(IndexedIterable<Road> roads) throws IOException {
		List<String> lines = new ArrayList<>();
		int totalSum = 0;
		int blocked = 0;
		int maxReservations = 0;
		double variation = 0.0;
		double averageReservations = 0.0;
		for (Road road : roads) {
			/*
			 * System.out.print(road.getJunctions().get(0).getCoords() + "\t");
			 * System.out.print(road.getJunctions().get(1).getCoords() + "\t");
			 * System.out.println(road.getRoadReservations());
			 */
			totalSum += road.getRoadReservations();
			if (road.isBlocked()) {
				blocked++;
			}
			/*
			 * if(isBridge(road)){ System.out.println(road.getRoadReservations()); }
			 */
			if (maxReservations < road.getRoadReservations()) {
				maxReservations = road.getRoadReservations();
			}

		}
		averageReservations = totalSum / roads.size();
		for (Road road : roads) {
			variation += Math.pow((road.getRoadReservations() - averageReservations), 2);
		}

		variation = variation / roads.size();
		variation = Math.sqrt(variation);

		lines.add(stepNum + "\t" + (double) averageReservations + "\t" + blocked + "\t" + maxReservations + "\t"
				+ variation);
		saveToFile(roadReservations, lines);
	}

	private void countTotalMoney(Iterable<DefaultAgent> agents, Path path, Path priorityPath) throws IOException {
		List<String> line = new ArrayList<>();
		List<String> values = new ArrayList<>();
		double totalSum = 0.0;
		int counter = 0;
		Iterable<DefaultAgent> agentsByType;
		values.add(String.format("%5d", stepNum));
		for (AgentType type : AgentType.values()) {
			totalSum = 0.0;
			counter = 0;
			agentsByType = getAgentsByType(agents, type);
			for (DefaultAgent agent : agentsByType) {
				totalSum += agent.getTotalMoneyPaid();
				counter++;
			}
			values.add(String.format("\t%5.1f", totalSum / counter));
		}
		StringBuilder sb = new StringBuilder();
		for (String s : values) {
			sb.append(s);
		}
		line.add(sb.toString());
		saveToFile(path, line);

		line.clear();
		values.clear();

		values.add(String.format("%5d", stepNum));
		Iterable<DefaultAgent> agentsByPriority;
		for (int i = 0; i < 3; i++) {
			totalSum = 0.0;
			counter = 0;
			agentsByPriority = getAgentsByPriority(agents, i);
			for (DefaultAgent agent : agentsByPriority) {
				totalSum += agent.getTotalMoneyPaid();
				counter++;
			}
			values.add(String.format("\t%5.1f", totalSum / counter));
		}
		StringBuilder sb1 = new StringBuilder();
		for (String s : values) {
			sb1.append(s);
		}
		line.add(sb1.toString());
		saveToFile(priorityPath, line);
	}

	private void countAgentsEnded(Iterable<DefaultAgent> agents, Path path) throws IOException {
		List<String> line = new ArrayList<>();
		List<String> values = new ArrayList<>();
		int agentsTravelEnded = 0;
		Iterable<DefaultAgent> agentsByType;
		values.add(String.format("%5d", stepNum));
		for (AgentType type : AgentType.values()) {
			agentsTravelEnded = 0;
			agentsByType = getAgentsByType(agents, type);
			for (DefaultAgent agent : agentsByType) {
				agentsTravelEnded += agent.isTravelEnded() ? 1 : 0;
			}
			values.add(String.format("\t%5d", agentsTravelEnded));
		}
		StringBuilder sb = new StringBuilder();
		for (String s : values) {
			sb.append(s);
		}
		line.add(sb.toString());
		saveToFile(path, line);
	}

	private void countAgentsWaiting(Iterable<DefaultAgent> agents, Path path) throws IOException {
		List<String> line = new ArrayList<>();
		List<String> values = new ArrayList<>();
		int agentsTravelEnded = 0;
		Iterable<DefaultAgent> agentsByType;
		values.add(String.format("%5d", stepNum));
		for (AgentType type : AgentType.values()) {
			agentsTravelEnded = 0;
			agentsByType = getAgentsByType(agents, type);
			for (DefaultAgent agent : agentsByType) {
				agentsTravelEnded += agent.isAgentWaiting() ? 1 : 0;
			}
			values.add(String.format("\t%5d", agentsTravelEnded));
		}
		StringBuilder sb = new StringBuilder();
		for (String s : values) {
			sb.append(s);
		}
		line.add(sb.toString());
		saveToFile(path, line);
	}

	private void getTravelDistance(Iterable<DefaultAgent> agents, Path path, Path priorityPath) throws IOException {
		List<String> line = new ArrayList<>();
		List<String> values = new ArrayList<>();
		double distanceSum = 0;
		int agentsTravelling = 0;
		Iterable<DefaultAgent> agentsByType;
		Iterable<DefaultAgent> agentsByPriority;
		values.add(String.format("%5d", stepNum));
		for (AgentType type : AgentType.values()) {
			distanceSum = 0;
			agentsTravelling = 0;
			agentsByType = getAgentsByType(agents, type);
			for (DefaultAgent agent : agentsByType) {
				distanceSum += agent.getTravelledDistance();
				agentsTravelling++;
			}
			values.add(String.format("\t%5.1f", (distanceSum / agentsTravelling)));
		}
		StringBuilder sb = new StringBuilder();
		for (String s : values) {
			sb.append(s);
		}
		line.add(sb.toString());
		saveToFile(path, line);

		line.clear();
		values.clear();
		values.add(String.format("%5d", stepNum));
		for (int i = 0; i < 3; i++) {
			distanceSum = 0.0;
			agentsTravelling = 0;
			agentsByPriority = getAgentsByPriority(agents, i);
			for (DefaultAgent agent : agentsByPriority) {
				distanceSum += agent.getTravelledDistance();
				agentsTravelling++;
			}
			values.add(String.format("\t%5.1f", distanceSum / agentsTravelling));
		}
		StringBuilder sb1 = new StringBuilder();
		for (String s : values) {
			sb1.append(s);
		}
		line.add(sb1.toString());
		saveToFile(priorityPath, line);
	}

	private void getTravelTime(Iterable<DefaultAgent> agents, Path path, Path priorityPath) throws IOException {
		List<String> line = new ArrayList<>();
		List<String> values = new ArrayList<>();
		int totalSum = 0;
		int counter = 0;
		Iterable<DefaultAgent> agentsByType;
		values.add(String.format("%5d", stepNum));
		for (AgentType type : AgentType.values()) {
			totalSum = 0;
			counter = 0;
			agentsByType = getAgentsByType(agents, type);
			for (DefaultAgent agent : agentsByType) {
				totalSum += agent.getTicks();
				counter++;
			}
			values.add(String.format("\t%5.1f", (double) totalSum / counter));
		}
		StringBuilder sb = new StringBuilder();
		for (String s : values) {
			sb.append(s);
		}
		line.add(sb.toString());
		saveToFile(path, line);

		line.clear();
		values.clear();
		values.add(String.format("%5d", stepNum));
		Iterable<DefaultAgent> agentsByPriority;
		for (int i = 0; i < 3; i++) {
			totalSum = 0;
			counter = 0;
			agentsByPriority = getAgentsByPriority(agents, i);
			for (DefaultAgent agent : agentsByPriority) {
				totalSum += agent.getTicks();
				counter++;
			}
			values.add(String.format("\t%5.1f", (double) totalSum / counter));
		}
		StringBuilder sb1 = new StringBuilder();
		for (String s : values) {
			sb1.append(s);
		}
		line.add(sb1.toString());
		saveToFile(priorityPath, line);
	}

	private void saveToFile(Path path, List<String> lines) throws IOException {
		Files.write(path, lines, UTF_8, APPEND, CREATE);
	}

	private void initializePaths() {
		File dir = new File("stats");
		dir.mkdir();
		agentMoney = Paths.get("./stats/agents_money.txt");
		agentTravelEnded = Paths.get("./stats/agents_travel_ended.txt");
		agentWaiting = Paths.get("./stats/agents_waiting.txt");
		agentDistance = Paths.get("./stats/agents_distance.txt");
		agentTime = Paths.get("./stats/agents_time.txt");
		roadPrice = Paths.get("./stats/roads_price.txt");
		roadReservations = Paths.get("./stats/roads_reservations.txt");
		agentCurrentToReserved = Paths.get("./stats/Current_to_reserved.txt");
		roadReservationsQuantiles = Paths.get("./stats/roads_reservations_quantiles.txt");

		agentPriorityMoney = Paths.get("./stats/priority/agents_money.txt");
		agentPriorityDistance = Paths.get("./stats/priority/agents_distance.txt");
		agentPriorityTime = Paths.get("./stats/priority/agents_time.txt");

	}

	private void clearFiles() {
		agentMoney.toFile().delete();
		agentTravelEnded.toFile().delete();
		agentWaiting.toFile().delete();
		agentDistance.toFile().delete();
		agentTime.toFile().delete();
		roadPrice.toFile().delete();
		roadReservations.toFile().delete();
		agentPriorityMoney.toFile().delete();
		agentPriorityDistance.toFile().delete();
		agentPriorityTime.toFile().delete();
		agentCurrentToReserved.toFile().delete();
		roadReservationsQuantiles.toFile().delete();
	}
}

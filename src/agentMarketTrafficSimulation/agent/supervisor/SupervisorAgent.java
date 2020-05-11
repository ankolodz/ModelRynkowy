package agentMarketTrafficSimulation.agent.supervisor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import agentMarketTrafficSimulation.agent.DefaultAgent;
import agentMarketTrafficSimulation.agent.IAgent;
import agentMarketTrafficSimulation.agent.TravellerAgent;
import agentMarketTrafficSimulation.bidding.BidFunctionUtils;
import agentMarketTrafficSimulation.environment.fixedgeography.Road;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

public class SupervisorAgent implements IAgent {
	Parameters params = RunEnvironment.getInstance().getParameters();
	private final int basePrice = (Integer) params.getValue("basePrice"); // parametr sym
	private final int priceGrow = (Integer) params.getValue("priceGrowth"); // parametr sym
	private Set<Road> roads = new HashSet<>();
	private Map<Road, List<DefaultAgent>> roadAndAgents = new HashMap<>();
	private ReservationHolder reservations = new ReservationHolder();
	private final double A, B; // Greenshield constants

	public SupervisorAgent() {
		Random r = new Random();
		A = r.nextDouble() * 5.0 + 2.0;
		B = r.nextDouble() * 0.4;
	}

	public int getBasePrice() {
		return basePrice;
	}

	public int getPriceGrow() {
		return priceGrow;
	}

	// --------------change---------------------------------------------
	public boolean makeReservation(TravellerAgent agent, Road road) {

		if (road.isBlocked()) {
			reservations.addAgentToRoadQueue(agent, road);
			setRoadWeigth(road);
			return false;
		}

		reservations.addReservation(agent, road);
		setRoadWeigth(road);
		return true;
	}

	private void removeAgentFromRoadQueue(TravellerAgent agent, Road road) {
		reservations.removeFromQueue(agent, road);
		setRoadWeigth(road);
	}

	public void endReservation(TravellerAgent agent, Road road) {
		reservations.endReservation(agent, road);
		setRoadWeigth(road);
		removeAgentFromRoadQueue(agent, road);
		TravellerAgent first = reservations.getFirstInQueue(road);
		if (first != null) {
			reservations.addReservation(first, road);
		}
	}

	public void endReservationAndPay(DefaultAgent agent, Road road) {
		if (!roadAndAgents.containsKey(road)) {
			List<DefaultAgent> list = new LinkedList<DefaultAgent>();
			list.add(agent);
			roadAndAgents.put(road, list);
		} else {
			roadAndAgents.get(road).add(agent);
		}
		reservations.endReservationAndPay(agent, road);
		setRoadWeigth(road);
		removeAgentFromRoadQueue(agent, road);
		TravellerAgent first = reservations.getFirstInQueue(road);
		if (first != null) {
			reservations.addReservation(first, road);
		}
	}

	public void addRoad(Road road) {
		roads.add(road);
	}

	// This uses agent to determine if he was on the road before so he can't return
	public double getPriceForRoad(Road road, DefaultAgent agent2) {
//		if (roadAndAgents.containsKey(road)) {
//			if (roadAndAgents.get(road).contains(agent2)) {
//				return (Double.MAX_VALUE / 2);
//			}
//		}
//		List<TravellerAgent> agents = reservations.getRoadReservations(road);
//		double price;
//		if (agents == null) {
//			road.setPrice(basePrice);
//		} else {
//			for (TravellerAgent agent : agents) {
//				BidFunctionUtils.setUpBidFunction(agent.getBidFunction(), agent.getTime(), agent.getDist(),
//						agent.getCost(), 0.2, agent.getRisk(), road.getTravelTime(), road.getLength(),
//						road.getEdge().getWeight(), road.getTrafficJamRisk());
//			}
//
//			Map<TravellerAgent, Double> agentMap = BidFunctionUtils.getPriceAndAgentsBasedOnBidFunctions(agents, road);
//			// price = agentMap.get(agents.get(0));
//		}
//		road.setPrice(basePrice, priceGrow);
//		Random random = new Random();
//		road.setPrice(random.nextDouble() * 290 + 10);
		return road.getPrice();
	}

	public double getA() {
		return A;
	}

	public double getB() {
		return B;
	}

	public boolean agentHasReservation(DefaultAgent agent, Road road) {
		return reservations.agentHasReservation(agent, road);
	}

	public void agentEnterRoad(Road r) {
		r.agentEnterRoad();
	}

	public void agentExitRoad(Road r) {
		r.agentExitRoad();
	}

	public List<TravellerAgent> getRoadReservations(Road road) {
		return reservations.getRoadReservations(road);
	}

	// ----------set road weight depending on reservation count--------------
	private void setRoadWeigth(Road road) {
		road.getEdge().setWeight((reservations.getQueueLength(road) * 0.5 + road.getRoadReservations()) / road.getLength());
	}

	// ------------------------------------------------------------------------
	public double getPriceForRoad(Road road) {
		List<TravellerAgent> agents = reservations.getRoadReservations(road);
		double price;
		if (agents == null) {
			road.setPrice(basePrice);
		} else {
			for (TravellerAgent agent : agents) {
				BidFunctionUtils.setUpBidFunction(agent.getBidFunction(), agent.getTime(), agent.getDist(),
						agent.getCost(), 0.2, agent.getRisk(), road.getTravelTime(), road.getLength(),
						road.getEdge().getWeight(), road.getTrafficJamRisk());
			}

			Map<TravellerAgent, Double> agentMap = BidFunctionUtils.getPriceAndAgentsBasedOnBidFunctions(agents, road);
			// price = agentMap.get(agents.get(0));
		}
		// road.setPrice(basePrice, priceGrow);
		return road.getPrice();
	}
}

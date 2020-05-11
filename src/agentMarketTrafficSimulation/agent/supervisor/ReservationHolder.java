package agentMarketTrafficSimulation.agent.supervisor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agentMarketTrafficSimulation.agent.DefaultAgent;
import agentMarketTrafficSimulation.agent.TravellerAgent;
import agentMarketTrafficSimulation.environment.fixedgeography.Road;

public class ReservationHolder {

	private Map<TravellerAgent, List<RoadAndPrice>> reservations = new HashMap<>();
	private Map<Road, List<TravellerAgent>> roadReservations = new HashMap<>();
	private Map<Road, List<TravellerAgent>> queues = new HashMap<>();

	public void addReservation(TravellerAgent agent, Road road) {
		if (!reservations.containsKey(agent)) {
			initializeAgentReservationList(agent);
		}
		if (!roadReservations.containsKey(road)) {
			initializeRoadReservationsList(road);
		}
		addReservationToExistingAgent(agent, road);
		road.increasePrice();
	}

	public void addAgentToRoadQueue(TravellerAgent agent, Road road) {
		if (!queues.containsKey(road)) {
			initializeRoadQueueList(road);
		}
		addAgentToExistingRoadQueue(agent, road);
	}

	private void initializeAgentReservationList(TravellerAgent agent) {
		List<RoadAndPrice> reservationRoads = new ArrayList<>();
		reservations.put(agent, reservationRoads);
	}

	private void initializeRoadReservationsList(Road road) {
		List<TravellerAgent> reservationAgents = new ArrayList<>();
		roadReservations.put(road, reservationAgents);
	}

	private void initializeRoadQueueList(Road road) {
		List<TravellerAgent> queueAgents = new ArrayList<>();
		queues.put(road, queueAgents);
	}

	private void addReservationToExistingAgent(TravellerAgent agent, Road road) {
		List<RoadAndPrice> reservationRoads = reservations.get(agent);
		List<TravellerAgent> reservationAgents = roadReservations.get(road);
		if (!roadAndPriceListContains(reservationRoads, road)) {
			if (road.getRoadCapacity() > road.getRoadReservations()) {
				road.incrementRoadReservations();

				reservationRoads.add(new RoadAndPrice(road, road.getOwner().getPriceForRoad(road)));
				reservationAgents.add(agent);
				reservations.put(agent, reservationRoads);
				roadReservations.put(road, reservationAgents);
			}
		}
	}

	private void addAgentToExistingRoadQueue(TravellerAgent agent, Road road) {
		List<TravellerAgent> queue = queues.get(road);
		List<TravellerAgent> reservationAgents = roadReservations.get(road);
		if (!reservationAgents.contains(agent) && !queue.contains(agent)) {
			queue.add(agent);
			queues.put(road, queue);
		}
	}

	public void endReservation(TravellerAgent agent, Road road) {
		if (reservations.containsKey(agent)) {
			List<RoadAndPrice> reservationRoads = reservations.get(agent);
			List<TravellerAgent> reservationAgents = roadReservations.get(road);
			if (roadAndPriceListContains(reservationRoads, road)) {
				road.decrementRoadReservations();
				roadAndPriceListRemove(reservationRoads, road);
				reservationAgents.remove(agent);
				reservations.put(agent, reservationRoads);
				roadReservations.put(road, reservationAgents);
			}
		}
	}

	// ------------------pay for road-----------------------------------
	// change: pay reservation cost, not current price for roads
	public void endReservationAndPay(DefaultAgent agent, Road road) {

		if (reservations.containsKey(agent)) {
			List<RoadAndPrice> reservationRoads = reservations.get(agent);
			List<TravellerAgent> reservationAgents = roadReservations.get(road);
			if (roadAndPriceListContains(reservationRoads, road)) {
				for (RoadAndPrice r : reservationRoads) {
					if (r.getRoad().equals(road)) {

						agent.setCurrentPrice(road.getOwner().getPriceForRoad(road));
						agent.setReservationPrice(r.getPrice());
						agent.payForRoad(agent.getReservationPrice());
						break;
					}
				}

				road.decrementRoadReservations();
				roadAndPriceListRemove(reservationRoads, road);
				reservationAgents.remove(agent);
				reservations.put(agent, reservationRoads);
				roadReservations.put(road, reservationAgents);
			}
		}
	}

	public void removeFromQueue(TravellerAgent agent, Road road) {
		if (queues.containsKey(road)) {
			List<TravellerAgent> queue = queues.get(road);
			if (queue.contains(agent)) {
				queue.remove(agent);
				queues.put(road, queue);
			}
		}
	}

	public boolean agentHasReservation(TravellerAgent agent, Road road) {
		if (reservations.containsKey(agent)) {
			List<RoadAndPrice> reservationRoads = reservations.get(agent);
			if (roadAndPriceListContains(reservationRoads, road)) {
				return true;
			}
		}
		return false;
	}

	public TravellerAgent getFirstInQueue(Road road) {
		if (queues.containsKey(road)) {
			List<TravellerAgent> queue = queues.get(road);
			if (!queue.isEmpty()) {
				TravellerAgent ret = queue.get(0);
				queue.remove(0);
				return ret;
			}
		}
		return null;
	}

	public int getQueueLength(Road road) {
		if (queues.containsKey(road)) {
			return queues.get(road).size();
		}
		return 0;
	}

	public List<TravellerAgent> getRoadReservations(Road road) {
		if (roadReservations.containsKey(road)) {
			return roadReservations.get(road);
		}
		return null;
	}

	private boolean roadAndPriceListContains(List<RoadAndPrice> list, Road road) {
		for (RoadAndPrice r : list) {
			if (r.getRoad().equals(road)) {
				return true;
			}
		}
		return false;
	}

	private void roadAndPriceListRemove(List<RoadAndPrice> list, Road road) {
		for (RoadAndPrice r : list) {
			if (r.getRoad().equals(road)) {
				list.remove(r);
				return;
			}
		}
	}

	public class RoadAndPrice {
		private double price;
		private Road road;

		RoadAndPrice(Road road, double price) {
			this.setRoad(road);
			this.setPrice(price);
		}

		public double getPrice() {
			return price;
		}

		public void setPrice(double price) {
			this.price = price;
		}

		public Road getRoad() {
			return road;
		}

		public void setRoad(Road road) {
			this.road = road;
		}
	}
}

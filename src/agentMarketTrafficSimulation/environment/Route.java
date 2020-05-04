package agentMarketTrafficSimulation.environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.vividsolutions.jts.geom.Coordinate;

import agentMarketTrafficSimulation.agent.DefaultAgent;
import agentMarketTrafficSimulation.agent.TravellerAgent;
import agentMarketTrafficSimulation.environment.fixedgeography.Road;

public class Route {
	private List<Coordinate> coordinates;
	private List<Road> roads;

	public Route() {
		coordinates = new ArrayList<>();
		roads = new ArrayList<>();
	}

	public void addToRoute(Coordinate coord, Road road) {
		this.coordinates.add(coord);
		this.roads.add(road);
	}

	public void add(Route route) {
		coordinates.addAll(route.getCoordinates());
		roads.addAll(route.getRoads());
	}

	public void makeReservations(int from, int to, DefaultAgent agent) {
		int i = from;
		for (i = from; i < to && i < roads.size(); i++) {
			if (!makeReservation(i, agent))
				break;
		}
	}

	public int getLastReservation(DefaultAgent agent, int start) {
		for (int i = start; i < roads.size(); i++) {
			if (!agentHasReservation(agent, i)) {
				return i;
			}
		}
		return roads.size();
	}

	public boolean makeReservation(int position, DefaultAgent agent) {
		Road road = roads.get(position);
		if (agentHasReservation(agent, position)) {
			return true;
		}
		return road.getOwner().makeReservation(agent, road);
	}

	public void endAllReservations(DefaultAgent agent) {
		for (Road road : roads) {
			road.getOwner().endReservation(agent, road);
		}
	}

	public void endReservation(int position, DefaultAgent agent) {
		Road road = roads.get(position);
		road.getOwner().endReservation(agent, road);
	}

	public boolean canMakeReservation(DefaultAgent agent, int from, int to) {
		Road road;
		for (int i = from; i < to && i < roads.size(); i++) {
			road = roads.get(i);
			if (road.isBlocked() && !agentHasReservation(agent, i)) {
				return false;
			}
		}
		return true;
	}

	public boolean agentHasReservation(DefaultAgent agent, int pos) {
		if (pos >= roads.size())
			return false;
		return roads.get(pos).getOwner().agentHasReservation(agent, roads.get(pos));
	}

	public List<Coordinate> getCoordinates() {
		return coordinates;
	}

	public List<Road> getRoads() {
		return roads;
	}

	public void newRoute(Route route, int from, int to) {

		// System.out.println("actual: " + coordinates + " new: " +
		// route.getCoordinates() + " from, to: " + from + " " + to);

		List<Road> tmpRoads = new ArrayList<Road>();
		for (int i = from; i < to; i++) {
			tmpRoads.add(roads.get(i));
		}
		tmpRoads.addAll(route.getRoads());
		roads = tmpRoads;

		List<Coordinate> tmpCoords = new ArrayList<Coordinate>();
		for (int i = from; i < to; i++) {
			tmpCoords.add(coordinates.get(i));
		}
		tmpCoords.addAll(route.getCoordinates());
		coordinates = tmpCoords;
	}

	public void agentEnterRoad(int currentPosition, DefaultAgent agent) {
		roads.get(currentPosition).getOwner().agentEnterRoad(roads.get(currentPosition));
	}

	public void agentExitRoad(int currentPosition, DefaultAgent agent) {
		roads.get(currentPosition).getOwner().agentExitRoad(roads.get(currentPosition));
	}

	public boolean canEnterRoad(DefaultAgent agent, int currentPosition) {
		if (roads.get(currentPosition).getRoadCapacity() <= roads.get(currentPosition).getAgentsOnRoad())
			return false;

		Random rand = new Random();
		if (rand.nextDouble() < roads.get(currentPosition).getTrafficJamRisk())
			return false;

		if (agentHasReservation(agent, currentPosition))
			return true;
		// get agents having reservations
		List<TravellerAgent> agents = roads.get(currentPosition).getOwner()
				.getRoadReservations(roads.get(currentPosition));

		// get their current position
		for (TravellerAgent a : agents) {
			// calculate their time to enter the road
			if (a.getTravel().getTimeToTarget(roads.get(currentPosition)) * 1.1 < roads.get(currentPosition)
					.getTravelTime())
				return true;
		}
		return false;
	}

	public Road getRoad(int i) {
		if (i >= roads.size())
			return null;
		return roads.get(i);
	}
}

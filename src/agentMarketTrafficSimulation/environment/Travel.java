package agentMarketTrafficSimulation.environment;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

import agentMarketTrafficSimulation.agent.AgentType;
import agentMarketTrafficSimulation.agent.DefaultAgent;
import agentMarketTrafficSimulation.context.ContextManager;
import agentMarketTrafficSimulation.environment.fixedgeography.Junction;
import agentMarketTrafficSimulation.environment.fixedgeography.Road;
import agentMarketTrafficSimulation.environment.utils.DistanceCalculator;
import agentMarketTrafficSimulation.utils.TravelUtils;
import repast.simphony.space.graph.RepastEdge;

public class Travel {
	private Junction destination;
	private DefaultAgent agent;
	private Junction lastJunction;

	private Route route;
	private int currentPosition = 0;
	private static final double DISTANCE_PER_TURN = 20;

	private boolean reserved = false;

	public Travel(DefaultAgent agent) {
		this.destination = agent.getEndJunction();
		this.agent = agent;
		this.lastJunction = null;
	}

	public void travel() {
		agent.setStartTime();
		if (lastJunction == null) {
			agentEnterJunction(0.0, agent.getStartJunction());
		} else {
			ride(0.0, route.getRoads().get(currentPosition));
		}
	}

	private Route calculateNewRoute(Junction junction) {
		Route newRoute;
		List<RepastEdge<Junction>> shortestPath = TravelUtils.getShortestRoute(junction, destination,
				agent.getTimePriority(), agent.getCostPriority(), agent.getDistancePriority(), agent);
		newRoute = RouteFinder.getRouteBetweenJunctions(shortestPath, junction);
		int pos = agent.getAgentType().type();
		if (pos == -1)
			pos = newRoute.getRoads().size();
		if (newRoute.canMakeReservation(agent, currentPosition, pos))
			return newRoute;
		return null;
	}

	private void endTravel() {
		route = null;
		agent.endTravel();
		agent = null;
	}

	private void setRoute(Route r) {
		if (route != null && r != null) {
			int roadsReserved = route.getLastReservation(agent, currentPosition + 1);
			route.endAllReservations(agent);
			route.newRoute(r, 0, roadsReserved);
			makeReservations();
		} else if (r != null) {
			route = r;
			makeReservations();
		}
	}

	// ----------------------------added--------------------
	public Route getRoute() {
		return route;
	}

	private void makeReservations() {
		if (agent.getAgentType().equals(AgentType.WHOLE)) {
			route.makeReservations(currentPosition, route.getRoads().size(), agent);
		} else {
			route.makeReservations(currentPosition, agent.getAgentType().type() + currentPosition, agent);
		}
		reserved = route.agentHasReservation(agent, currentPosition + 1);
	}

	private void agentEnterJunction(double distanceTravelled, Junction junction) {
		// check if agent at destination
		if (junction.equals(destination)) {
			endTravel();
			return;
		}
		agentLeaveJunction(distanceTravelled, junction);
	}

	private void agentLeaveJunction(double distanceTravelled, Junction junction) {
		if (!junction.equals(agent.getStartJunction()) || currentPosition != 0) {
			currentPosition++;
		}
		if (route == null) {
			setRoute(calculateNewRoute(junction));
			if (route == null) {
				agent.waitRandomSteps();
				return;
			}
		} else {
			// calculate new possibly better route
			if (!agent.getAgentType().equals(AgentType.WHOLE)) {
				Coordinate coord = route.getCoordinates().get(route.getLastReservation(agent, currentPosition + 1) - 1);
				for (Junction j : ContextManager.getAllJunctions()) {
					if (j.getCoords().equals(coord)) {
						setRoute(calculateNewRoute(j));
						agent.setAlternativeChosen(agent.getAlternativeChosen() + 1);
						if (route == null) {
							System.out.println("how null here?");
							agent.waitRandomSteps();
							return;
						}
						break;
					}
				}
			}
		}

		// System.out.println("start: " + agent.getStartJunction().getCoords() + " end:
		// " + agent.getEndJunction().getCoords() + " route: " + route.getCoordinates()
		// + " leaving: " + junction.getCoords());

		makeReservations();
		if (!route.canEnterRoad(agent, currentPosition)) { // predykcja
			// if(!route.agentHasReservation(agent, currentPosition)){ // bez predykcji
			if (currentPosition != 0)
				currentPosition--;
			agent.waitRandomSteps();
			return;
		}
		lastJunction = junction;
		agent.setCurrentJunction(lastJunction);

		agentEnterRoad(distanceTravelled, route.getRoads().get(currentPosition));
	}

	private void agentEnterRoad(double distanceTravelled, Road road) {
		// road.getOwner().agentEnterRoad(road);
		route.agentEnterRoad(currentPosition, agent);
		ride(distanceTravelled, road);
	}

	private void ride(double distTravelled, Road road) {
		if (!reserved) {
			makeReservations();
		}

		Coordinate target = route.getCoordinates().get(currentPosition);
		Coordinate currentCoord = ContextManager.getAgentGeometry(this.agent).getCoordinate();
		GeometryFactory geomFac = new GeometryFactory();
		double speed = road.getSpeed();
		double[] distAndAngle = new double[2];
		DistanceCalculator.calculateDistance(currentCoord, target, distAndAngle);
		double distToTarget = distAndAngle[0] / speed;

		if (distTravelled + distToTarget <= DISTANCE_PER_TURN) {
			distTravelled += distToTarget;
			ContextManager.moveAgent(this.agent, geomFac.createPoint(target));
			agentLeaveRoad(distTravelled, road);
		} else {
			double distToTravel = (DISTANCE_PER_TURN - distTravelled) * speed;
			ContextManager.moveAgentByVector(this.agent, distToTravel, distAndAngle[1]);
		}
	}

	private void agentLeaveRoad(double distanceTravelled, Road road) {
		road.getOwner().endReservationAndPay(agent, road);
		// change!!
		// agent.setCurrentPrice(road.getOwner().getPriceForRoad(road));
		if (distanceTravelled != 0) {
			agent.addDistance(road.getLength());
			// road.getOwner().agentExitRoad(road);
			route.agentExitRoad(currentPosition, agent);
		}
		reserved = false;
		for (Junction j : road.getJunctions()) {
			if (!j.equals(lastJunction)) {
				agentEnterJunction(distanceTravelled, j);
				break;
			}
		}
	}

	public double getTimeToTarget(Road road) {
		double time = 0.0;
		Coordinate currentCoord = ContextManager.getAgentGeometry(this.agent).getCoordinate();
		Coordinate target = route.getCoordinates().get(currentPosition);
		double speed = road.getSpeed();
		double[] distAndAngle = new double[2];
		DistanceCalculator.calculateDistance(currentCoord, target, distAndAngle);
		time += distAndAngle[0] / speed;
		if (route.getRoad(currentPosition).equals(road))
			return time;
		for (int i = currentPosition + 1; !route.getRoad(i).equals(road); i++) {
			time += route.getRoad(i).getTravelTime();
		}
		return time;
	}

	public int getCurrentPosition() {
		return currentPosition;
	}
}

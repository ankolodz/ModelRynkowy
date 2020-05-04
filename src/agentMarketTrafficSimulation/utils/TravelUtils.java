package agentMarketTrafficSimulation.utils;

import java.util.List;

import agentMarketTrafficSimulation.agent.DefaultAgent;
import agentMarketTrafficSimulation.context.ContextManager;
import agentMarketTrafficSimulation.environment.fixedgeography.Junction;
import agentMarketTrafficSimulation.environment.fixedgeography.Road;
import repast.simphony.space.graph.RepastEdge;

public class TravelUtils {

	private static List<List<RepastEdge<Junction>>> paths;
	private static List<List<Junction>> pathList;
	
	private static List<Road> roads = null;

	public static List<RepastEdge<Junction>> getShortestRoute(Junction start, Junction destination, double time,
			double cost, double distance, DefaultAgent agent) {
		if (roads == null) {
			 roads = ContextManager.getAllRoads();
		}
		// ShortestPath<Junction> p;
		List<RepastEdge<Junction>> shortestPath = null;
		// p = new ShortestPath<Junction>(ContextManager.getRoadNetwork());
		// shortestPath = p.getPath(start, destination);
		// ShortestPath.finished(p);
		PathFindingAlgorithm pathFinder = new AStarAlgorithm(new Graph(roads));
		shortestPath = pathFinder.getPath(start, destination, time, cost, distance, agent);
		return shortestPath;
	}

	public static double getMinDist() {
		if (roads == null) {
			 roads = ContextManager.getAllRoads();
		}
		double min = roads.get(0).getLength();
		for (Road r : roads) {
			if (r.getLength() < min)
				min = r.getLength();
		}
		return (min - 0.01);
	}

	public static double getMaxDist() {
		if (roads == null) {
			 roads = ContextManager.getAllRoads();
		}
		double max = 0;
		for (Road r : roads) {
			if (r.getLength() > max)
				max = r.getLength();
		}
		return (max + 0.01);
	}

	// CHANGED
	/*
	 * from public static double getMinCost(){ List<Road> roads =
	 * roads; double min =
	 * roads.get(0).getOwner().getPriceForRoad(roads.get(0)); for(Road r : roads){
	 * if(r.getOwner().getPriceForRoad(r) < min) min =
	 * r.getOwner().getPriceForRoad(r); } return (min - 0.01); }
	 * 
	 * public static double getMaxCost(){ List<Road> roads =
	 * roads; double max = 0; for(Road r : roads){
	 * if(r.getOwner().getPriceForRoad(r) > max) max =
	 * r.getOwner().getPriceForRoad(r); } return (max + 0.01); }
	 * 
	 */
	public static double getMinCost() {
		if (roads == null) {
			 roads = ContextManager.getAllRoads();
		}
		double min = roads.get(0).getPrice();
		for (Road r : roads) {
			if (r.getPrice() < min)
				min = r.getPrice();
		}
		return (min - 0.01);
	}

	public static double getMaxCost() {
		if (roads == null) {
			 roads = ContextManager.getAllRoads();
		}
		double max = 0;
		for (Road r : roads) {
			if (r.getPrice() > max)
				max = r.getPrice();
		}
		return (max + 0.01);
	}

	public static double getMinWeight() {
		if (roads == null) {
			 roads = ContextManager.getAllRoads();
		}
		double min = roads.get(0).getEdge().getWeight();
		for (Road r : roads) {
			if (r.getEdge().getWeight() < min)
				min = r.getEdge().getWeight();
		}
		return (min - 0.01);
	}

	public static double getMaxWeight() {
		if (roads == null) {
			 roads = ContextManager.getAllRoads();
		}
		double max = 0;
		for (Road r : roads) {
			if (r.getEdge().getWeight() > max)
				max = r.getEdge().getWeight();
		}
		return (max + 0.01);
	}

	public static double getMinTime() {
		if (roads == null) {
			 roads = ContextManager.getAllRoads();
		}
		double min = roads.get(0).getTravelTime();
		for (Road r : roads) {
			if (r.getTravelTime() < min)
				min = r.getTravelTime();
		}
		return (min - 0.01);
	}

	public static double getMaxTime() {
		if (roads == null) {
			 roads = ContextManager.getAllRoads();
		}
		double max = 0;
		for (Road r : roads) {
			if (r.getTravelTime() > max)
				max = r.getTravelTime();
		}
		return (max + 0.01);
	}

	public static double getMinRisk() {
		if (roads == null) {
			 roads = ContextManager.getAllRoads();
		}
		double min = roads.get(0).getTrafficJamRisk();
		for (Road r : roads) {
			if (r.getTrafficJamRisk() < min)
				min = r.getTrafficJamRisk();
		}
		return (min - 0.01);
	}

	public static double getMaxRisk() {
		if (roads == null) {
			 roads = ContextManager.getAllRoads();
		}
		double max = 0;
		for (Road r : roads) {
			if (r.getTrafficJamRisk() > max)
				max = r.getTrafficJamRisk();
		}
		return (max + 0.01);
	}
	
	public static void setRoads(List<Road> newRoads) {
		roads = newRoads;
	}
	
	public static List<Road> getRoads(){
		return roads;
	}
}

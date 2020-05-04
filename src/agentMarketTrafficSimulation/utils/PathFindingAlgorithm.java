package agentMarketTrafficSimulation.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import agentMarketTrafficSimulation.agent.DefaultAgent;
import agentMarketTrafficSimulation.environment.fixedgeography.Junction;
import agentMarketTrafficSimulation.environment.fixedgeography.NetworkEdge;
import agentMarketTrafficSimulation.environment.fixedgeography.Road;
import repast.simphony.space.graph.RepastEdge;

public abstract class PathFindingAlgorithm {
	protected final List<NetworkEdge<Junction>> edges;
	protected Set<Junction> settledNodes;
	protected Set<Junction> unSettledNodes;
	protected Map<Junction, Junction> predecessors;
	protected Map<Junction, Double> distance;
	protected double minDist, maxDist, minCost, maxCost, minWeight, maxWeight, minTime, maxTime;

	public PathFindingAlgorithm(Graph graph) {
		// create a copy of the array so that we can operate on this array
		this.edges = new ArrayList<NetworkEdge<Junction>>(graph.getEdges());
		minDist = TravelUtils.getMinDist();
		maxDist = TravelUtils.getMaxDist();
		minCost = TravelUtils.getMinCost();
		maxCost = TravelUtils.getMaxCost();
		minWeight = TravelUtils.getMinWeight();
		maxWeight = TravelUtils.getMaxWeight();
		minTime = TravelUtils.getMinTime();
		maxTime = TravelUtils.getMaxTime();
	}

	/*
	 * This method returns the path from the source to the selected target and NULL
	 * if no path exists
	 */
	public List<RepastEdge<Junction>> getPath(Junction start, Junction target, double time, double cost,
			double distance, DefaultAgent agent) {
		execute(start, target, time, cost, distance, agent);
		LinkedList<Junction> path = new LinkedList<Junction>();
		Junction step = target;
		path.add(step);
		while (predecessors.get(step) != null) {
			step = predecessors.get(step);
			path.add(step);
		}
		// Put it into the correct order
		Collections.reverse(path);
		List<RepastEdge<Junction>> result = new LinkedList<>();
		for (int i = 0; i < path.size() - 1; i++) {
			for (Road r : path.get(i).getRoads()) {
				if (r.getJunctions().get(1).equals(path.get(i + 1))) {
					result.add(r.getEdge());
					break;
				}
			}
		}
		return result;
	}

	protected abstract void execute(Junction source, Junction target, double time, double cost, double dist,
			DefaultAgent agent);

}

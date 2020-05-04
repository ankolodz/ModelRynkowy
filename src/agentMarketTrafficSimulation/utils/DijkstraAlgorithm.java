package agentMarketTrafficSimulation.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import agentMarketTrafficSimulation.agent.DefaultAgent;
import agentMarketTrafficSimulation.environment.fixedgeography.Junction;
import agentMarketTrafficSimulation.environment.fixedgeography.NetworkEdge;

public class DijkstraAlgorithm extends PathFindingAlgorithm {

	public DijkstraAlgorithm(Graph graph) {
		super(graph);
	}

	@Override
	protected void execute(Junction source, Junction target, double time, double cost, double dist,
			DefaultAgent agent) {
		settledNodes = new HashSet<Junction>();
		unSettledNodes = new HashSet<Junction>();
		distance = new HashMap<Junction, Double>();
		predecessors = new HashMap<Junction, Junction>();
		distance.put(source, 0.0);
		unSettledNodes.add(source);
		while (unSettledNodes.size() > 0) {
			Junction node = getMinimum(unSettledNodes);
			settledNodes.add(node);
			unSettledNodes.remove(node);
			findMinimalDistances(node, time, cost, dist, agent);
		}
	}

	private void findMinimalDistances(Junction node, double time, double cost, double dist, DefaultAgent agent) {
		List<Junction> adjacentNodes = getNeighbors(node);
		for (Junction target : adjacentNodes) {
			if (getShortestDistance(target) > getShortestDistance(node)
					+ getDistance(node, target, time, cost, dist, agent)) {
				distance.put(target, getShortestDistance(node) + getDistance(node, target, time, cost, dist, agent));
				predecessors.put(target, node);
				unSettledNodes.add(target);
			}
		}

	}

	public double getDistance(Junction node, Junction target, double time, double cost, double dist,
			DefaultAgent agent) {
		for (NetworkEdge<Junction> edge : edges) {
			if (edge.getSource().equals(node) && edge.getTarget().equals(target)) {
				if (((edge.getRoad().getTravelTime() - minTime) / (maxTime - minTime)) <= 0) {
					System.out.println("Dijkstra ERROR Time " + edge.getRoad().getTravelTime() + " - " + minTime
							+ " / (" + maxTime + " - " + minTime + ")");
				}
				if ((edge.getRoad().getLength() - minDist) / (maxDist - minDist) <= 0) {
					System.out.println("Dijkstra ERROR Dist " + edge.getRoad().getLength() + " - " + maxDist + " / ("
							+ maxTime + " - " + minDist + ")");
				}
				if ((edge.getRoad().getOwner().getPriceForRoad(edge.getRoad(), agent) - minCost)
						/ (maxCost - minCost) <= 0) {
					System.out.println(
							"Dijkstra ERROR Cost " + edge.getRoad().getOwner().getPriceForRoad(edge.getRoad(), agent)
									+ " - " + minCost + " / (" + maxCost + " - " + minCost + ")");
				}
				if ((edge.getWeight() - minWeight) / (maxWeight - minWeight) <= 0) {
					System.out.println("Dijkstra ERROR Weight " + edge.getWeight() + " - " + minWeight + " / ("
							+ maxWeight + " - " + minWeight + ")");
				}
				return (time * ((edge.getRoad().getTravelTime() - minTime) / (maxTime - minTime))
						+ dist * ((edge.getRoad().getLength() - minDist) / (maxDist - minDist))
						+ cost * ((edge.getRoad().getOwner().getPriceForRoad(edge.getRoad(), agent) - minCost)
								/ (maxCost - minCost))
						+ 0.2 * ((edge.getWeight() - minWeight) / (maxWeight - minWeight)));
			}
		}
		throw new RuntimeException("Should not happen");
	}

	private List<Junction> getNeighbors(Junction node) {
		List<Junction> neighbors = new ArrayList<Junction>();
		for (NetworkEdge<Junction> edge : edges) {
			if (edge.getSource().equals(node) && !isSettled(edge.getTarget())) {
				neighbors.add(edge.getTarget());
			}
		}
		return neighbors;
	}

//	List<Junction> getNeighborsAlternative(Junction node) {
//		List<Junction> neighbors = new ArrayList<Junction>();
//		for (NetworkEdge<Junction> edge : edges) {
//			if (edge.getSource().equals(node)) {
//				neighbors.add(edge.getTarget());
//			}
//		}
//		return neighbors;
//	}

	private Junction getMinimum(Set<Junction> vertexes) {
		Junction minimum = null;
		for (Junction vertex : vertexes) {
			if (minimum == null) {
				minimum = vertex;
			} else {
				if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
					minimum = vertex;
				}
			}
		}
		return minimum;
	}

	private boolean isSettled(Junction vertex) {
		return settledNodes.contains(vertex);
	}

	private Double getShortestDistance(Junction destination) {
		Double d = distance.get(destination);
		if (d == null) {
			return Double.MAX_VALUE;
		} else {
			return d;
		}
	}

}

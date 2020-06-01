package agentMarketTrafficSimulation.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import agentMarketTrafficSimulation.agent.DefaultAgent;
import agentMarketTrafficSimulation.environment.fixedgeography.Junction;
import agentMarketTrafficSimulation.environment.fixedgeography.NetworkEdge;
import agentMarketTrafficSimulation.environment.fixedgeography.Road;

public class AStarAlgorithm extends PathFindingAlgorithm {

	private HashMap<Junction, Double> passingCost;

	public AStarAlgorithm(Graph graph) {
		super(graph);
	}

	@Override
	protected void execute(Junction source, Junction target, double time, double cost, double dist,
			DefaultAgent agent) {
		settledNodes = new HashSet<Junction>();
		unSettledNodes = new HashSet<Junction>();
		predecessors = new HashMap<Junction, Junction>();
		distance = new HashMap<Junction, Double>(); // distance[x] is a cost of getting to x
		passingCost = new HashMap<>(); // passingCost[x] is a cost of getting to target via x, partially "guessed"
		passingCost.put(source, getHeuristicCostEstimate(source, target, time, cost, dist));
		distance.put(source, 0.0);
		unSettledNodes.add(source);
		while (unSettledNodes.size() > 0) {
			Junction node = getMinimum(unSettledNodes);
			settledNodes.add(node);
			unSettledNodes.remove(node);
			findMinimalDistances(node, target, time, cost, dist, agent);
		}
	}

	private double getHeuristicCostEstimate(Junction start, Junction target, double time, double cost, double dist) {

		List<Road> roads = start.getRoads();

		double maxSpeed = 0.0;
		double minPrice = Double.MAX_VALUE;
		for (Road r : roads) {
			double testSpeed = r.getSpeed();
			double testPrice = r.getPrice();
			if (testPrice < minPrice)
				minPrice = testPrice;
			if (testSpeed > maxSpeed)
				maxSpeed = testSpeed;
		}

		return dist * start.getCoords().distance(target.getCoords()) + time * (2 / maxSpeed) + cost * minPrice;
//		return 0.0; // this simulates the Dijkstra algorithm. Should ultimately vary somehow.
	}

	private void findMinimalDistances(Junction node, Junction target, double time, double cost, double dist, DefaultAgent agent) {
		List<Junction> adjacentNodes = getNeighbors(node);
		for (Junction neighbor : adjacentNodes) {
			if (settledNodes.contains(neighbor))
				continue;
			double distanceToNeighbor = getShortestDistance(node)
					+ getDistance(node, neighbor, time, cost, dist, agent);
			if (!unSettledNodes.contains(neighbor)) {
				unSettledNodes.add(neighbor);
			} else if (distanceToNeighbor >= getShortestDistance(neighbor)) {
				continue;
			}
			predecessors.put(neighbor, node);
			distance.put(neighbor, distanceToNeighbor);
			passingCost.put(neighbor,
					distanceToNeighbor + getHeuristicCostEstimate(neighbor, target, time, cost, dist));
		}
	}

	private Junction getMinimum(Set<Junction> vertexes) {
		Junction minimum = null;
		for (Junction vertex : vertexes) {
			if (minimum == null) {
				minimum = vertex;
			} else {
				if (getPassingCost(vertex) < getPassingCost(minimum)) {
					minimum = vertex;
				}
			}
		}
		return minimum;
	}

	private Double getPassingCost(Junction destination) {
		return passingCost.get(destination) == null ? Double.MAX_VALUE : passingCost.get(destination);
	}

	private Double getShortestDistance(Junction destination) {
		Double d = distance.get(destination);
		if (d == null) {
			return Double.MAX_VALUE;
		} else {
			return d;
		}
	}

	public double getDistance(Junction node, Junction target, double time, double cost, double dist,
			DefaultAgent agent) {
		for (NetworkEdge<Junction> edge : edges) {
			if (edge.getSource().equals(node) && edge.getTarget().equals(target)) {
				if (((edge.getRoad().getTravelTime() - minTime) / (maxTime - minTime)) <= 0) {
					System.out.println("ERROR Time " + edge.getRoad().getTravelTime() + " - " + minTime + " / ("
							+ maxTime + " - " + minTime + ")");
				}
				if ((edge.getRoad().getLength() - minDist) / (maxDist - minDist) <= 0) {
					System.out.println("ERROR Dist " + edge.getRoad().getLength() + " - " + maxDist + " / (" + maxTime
							+ " - " + minDist + ")");
				}
				if ((edge.getRoad().getOwner().getPriceForRoad(edge.getRoad(), agent) - minCost)
						/ (maxCost - minCost) <= 0) {
					System.out.println("ERROR Cost " + edge.getRoad().getOwner().getPriceForRoad(edge.getRoad(), agent)
							+ " - " + minCost + " / (" + maxCost + " - " + minCost + ")");
				}
				if ((edge.getWeight() - minWeight) / (maxWeight - minWeight) <= 0) {
					System.out.println("ERROR Weight " + edge.getWeight() + " - " + minWeight + " / (" + maxWeight
							+ " - " + minWeight + ")");
				}
				double distance = (time * ((edge.getRoad().getTravelTime() - minTime) / (maxTime - minTime))
						+ dist * ((edge.getRoad().getLength() - minDist) / (maxDist - minDist))
						+ cost * ((edge.getRoad().getOwner().getPriceForRoad(edge.getRoad(), agent) - minCost)
								/ (maxCost - minCost))
						+ 0 * ((edge.getWeight() - minWeight) / (maxWeight - minWeight)));
				if(cost == 1.0d) {
					System.out.println("Road price: " + edge.getRoad().getOwner().getPriceForRoad(edge.getRoad(), agent) + ", minCost: " + minCost + ", maxCost: " + maxCost);
				}
				return distance;
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

	private boolean isSettled(Junction vertex) {
		return settledNodes.contains(vertex);
	}
}

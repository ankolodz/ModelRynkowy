package agentMarketTrafficSimulation.utils;

import java.util.LinkedList;
import java.util.List;

import agentMarketTrafficSimulation.environment.fixedgeography.Junction;
import agentMarketTrafficSimulation.environment.fixedgeography.NetworkEdge;
import agentMarketTrafficSimulation.environment.fixedgeography.Road;

public class Graph {
	private final List<NetworkEdge<Junction>> edges;

	public Graph(List<Road> roads) {
		edges = new LinkedList<>();
		for (Road r : roads) {
			edges.add(r.getEdge());
		}
	}

	public List<NetworkEdge<Junction>> getEdges() {
		return edges;
	}
}
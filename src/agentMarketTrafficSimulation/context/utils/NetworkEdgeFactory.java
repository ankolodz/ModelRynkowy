package agentMarketTrafficSimulation.context.utils;

import agentMarketTrafficSimulation.environment.fixedgeography.NetworkEdge;
import repast.simphony.space.graph.EdgeCreator;

@SuppressWarnings("hiding")
public class NetworkEdgeFactory<T> implements EdgeCreator<NetworkEdge<T>, T> {
	public NetworkEdge<T> createEdge(T source, T target, boolean isDirected, double weight) {
		return new NetworkEdge<T>(source, target, isDirected, weight);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Class<NetworkEdge> getEdgeType() {
		return NetworkEdge.class;
	}
}

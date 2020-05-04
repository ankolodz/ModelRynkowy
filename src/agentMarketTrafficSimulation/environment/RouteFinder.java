package agentMarketTrafficSimulation.environment;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.vividsolutions.jts.geom.Coordinate;

import agentMarketTrafficSimulation.context.ContextManager;
import agentMarketTrafficSimulation.environment.fixedgeography.Junction;
import agentMarketTrafficSimulation.environment.fixedgeography.NetworkEdge;
import agentMarketTrafficSimulation.environment.fixedgeography.Road;
import repast.simphony.space.graph.RepastEdge;

public class RouteFinder {
	public static Route getRouteBetweenJunctions(List<RepastEdge<Junction>> shortestPath, Junction startingJunction) {
		Route route = new Route();
		NetworkEdge<Junction> edge;
		Road road;
		boolean sourceFirst;
		for (RepastEdge<Junction> e : shortestPath) {
			edge = (NetworkEdge<Junction>) e;
			sourceFirst = calculateDirection(edge, startingJunction, route);
			road = edge.getRoad();

			Coordinate[] roadCoords = ContextManager.getRoadProjection().getGeometry(road).getCoordinates();
			if (!sourceFirst) {
				ArrayUtils.reverse(roadCoords);
			}
			// for (Coordinate coord: roadCoords) {
			route.addToRoute(roadCoords[1], road);
			// }
		}
		return route;
	}

	private static boolean calculateDirection(NetworkEdge<Junction> edge, Junction startingJunction, Route route) {
		if (route.getCoordinates().isEmpty()) {
			// No coords in route yet, compare the source to the starting junction
			return (edge.getSource().equals(startingJunction)) ? true : false;
		} else {
			// Otherwise compare the source to the last coord added to the list
			return (edge.getSource().getCoords().equals(route.getCoordinates().get(route.getCoordinates().size() - 1)))
					? true
					: false;
		}
	}
}

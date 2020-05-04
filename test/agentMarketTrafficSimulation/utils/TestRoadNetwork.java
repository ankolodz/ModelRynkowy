package agentMarketTrafficSimulation.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.mockito.Mockito;

import com.vividsolutions.jts.geom.Coordinate;

import agentMarketTrafficSimulation.agent.supervisor.SupervisorAgent;
import agentMarketTrafficSimulation.environment.fixedgeography.Junction;
import agentMarketTrafficSimulation.environment.fixedgeography.NetworkEdge;
import agentMarketTrafficSimulation.environment.fixedgeography.Road;

public class TestRoadNetwork {
	
	private static List<Road> roads;
	private static List<Junction> junctions;
	private static List<NetworkEdge> networkEdges;
	
	public static List<Road> getTestRoadNetwork() {
		Road road1 = new Road();
		Road road2 = new Road();
		Road road3 = new Road();
		Road road4 = new Road();
		Road road5 = new Road();
		Road road6 = new Road();
		Road road7 = new Road();
		Road road8 = new Road();
		Road road9 = new Road();
		Coordinate coords1 = new Coordinate(1, 3);
		Coordinate coords2 = new Coordinate(2, 6);
		Coordinate coords3 = new Coordinate(4, 4);
		Coordinate coords4 = new Coordinate(3, 1);
		Coordinate coords5 = new Coordinate(6, 2);
		Coordinate coords6 = new Coordinate(5, 7);
		Coordinate coords7 = new Coordinate(7, 5);
		Junction junc1 = new Junction();
		Junction junc2 = new Junction();
		Junction junc3 = new Junction();
		Junction junc4 = new Junction();
		Junction junc5 = new Junction();
		Junction junc6 = new Junction();
		Junction junc7 = new Junction();
		junc1.setCoords(coords1);
		junc2.setCoords(coords2);
		junc3.setCoords(coords3);
		junc4.setCoords(coords4);
		junc5.setCoords(coords5);
		junc6.setCoords(coords6);
		junc7.setCoords(coords7);
		road1.addJunction(junc1);
		road1.addJunction(junc2);
		road2.addJunction(junc1);
		road2.addJunction(junc4);
		road3.addJunction(junc2);
		road3.addJunction(junc3);
		road4.addJunction(junc3);
		road4.addJunction(junc4);
		road5.addJunction(junc2);
		road5.addJunction(junc7);
		road6.addJunction(junc3);
		road6.addJunction(junc7);
		road7.addJunction(junc3);
		road7.addJunction(junc6);
		road8.addJunction(junc3);
		road8.addJunction(junc5);
		road9.addJunction(junc5);
		road9.addJunction(junc6);
		junc1.addRoad(road1);
		junc1.addRoad(road2);
		junc2.addRoad(road1);
		junc2.addRoad(road3);
		junc2.addRoad(road5);
		junc3.addRoad(road3);
		junc3.addRoad(road4);
		junc3.addRoad(road6);
		junc3.addRoad(road7);
		junc3.addRoad(road8);
		junc4.addRoad(road2);
		junc4.addRoad(road4);
		junc5.addRoad(road8);
		junc5.addRoad(road9);
		junc6.addRoad(road7);
		junc6.addRoad(road9);
		junc7.addRoad(road5);
		junc7.addRoad(road6);
		NetworkEdge<Junction> networkEdge1 = new NetworkEdge<Junction>(junc1, junc2, false, coords1.distance(coords2));
		NetworkEdge<Junction> networkEdge2 = new NetworkEdge<Junction>(junc1, junc4, false, coords1.distance(coords4));
		NetworkEdge<Junction> networkEdge3 = new NetworkEdge<Junction>(junc2, junc3, false, coords2.distance(coords3));
		NetworkEdge<Junction> networkEdge4 = new NetworkEdge<Junction>(junc3, junc4, false, coords3.distance(coords4));
		NetworkEdge<Junction> networkEdge5 = new NetworkEdge<Junction>(junc2, junc7, false, coords2.distance(coords7));
		NetworkEdge<Junction> networkEdge6 = new NetworkEdge<Junction>(junc3, junc7, false, coords3.distance(coords7));
		NetworkEdge<Junction> networkEdge7 = new NetworkEdge<Junction>(junc3, junc6, false, coords3.distance(coords6));
		NetworkEdge<Junction> networkEdge8 = new NetworkEdge<Junction>(junc3, junc5, false, coords3.distance(coords5));
		NetworkEdge<Junction> networkEdge9 = new NetworkEdge<Junction>(junc5, junc6, false, coords5.distance(coords6));
		networkEdge1.setRoad(road1);
		networkEdge2.setRoad(road2);
		networkEdge3.setRoad(road3);
		networkEdge4.setRoad(road4);
		networkEdge5.setRoad(road5);
		networkEdge6.setRoad(road6);
		networkEdge7.setRoad(road7);
		networkEdge8.setRoad(road8);
		networkEdge9.setRoad(road9);
		road1.setEdge(networkEdge1);
		road2.setEdge(networkEdge2);
		road3.setEdge(networkEdge3);
		road4.setEdge(networkEdge4);
		road5.setEdge(networkEdge5);
		road6.setEdge(networkEdge6);
		road7.setEdge(networkEdge7);
		road8.setEdge(networkEdge8);
		road9.setEdge(networkEdge9);
		Road[] roadArray = {road1, road2, road3, road4, road5, road6, road7, road8, road9};
		roads = new ArrayList<>(Arrays.asList(roadArray));
		Junction[] junctionArray = {junc1, junc2, junc3, junc4, junc5, junc6, junc6};
		junctions = new ArrayList<>(Arrays.asList(junctionArray));
		NetworkEdge[] networkEdgeArray = {networkEdge1, networkEdge2, networkEdge3, networkEdge4,
				networkEdge5, networkEdge6, networkEdge7, networkEdge8, networkEdge9};
		networkEdges = new ArrayList<>(Arrays.asList(networkEdgeArray));
		double basePrice = 300.0;
		for (Road road : roads) {
			road.setPrice(basePrice);
		}
		double baseSpeed = 60.0;
		for (Road road : roads) {
			road.setSpeed(baseSpeed);
		}
		int baseCapacity = 5;
		for (Road road : roads) {
			road.setCapacity(baseCapacity);
		}
		SupervisorAgent supervisor = Mockito.mock(SupervisorAgent.class);
		Mockito.when(supervisor.getPriceForRoad(Mockito.anyObject())).thenReturn(100.0);
		for (Road road : roads) {
			road.setOwner(supervisor);
		}
		return roads;
	}
	
	public static List<Road> getRoads(){
		if (roads == null) {
			return getTestRoadNetwork();
		}else {
			return roads;
		}
	}
	
	public static List<Junction> getJunctions(){
		if (roads == null) {
			getTestRoadNetwork();
		}
		return junctions;
	}
	
	public static List<NetworkEdge> getNetworkEdges(){
		if (roads == null) {
			getTestRoadNetwork();
		}
		return networkEdges;
	}
}

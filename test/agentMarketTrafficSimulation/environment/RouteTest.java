package agentMarketTrafficSimulation.environment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.vividsolutions.jts.geom.Coordinate;

import agentMarketTrafficSimulation.agent.DefaultAgent;
import agentMarketTrafficSimulation.environment.fixedgeography.Road;

public class RouteTest {

	private Route route;
	private Coordinate coordinate;
	private Road road;
	private DefaultAgent agent;
	
	@Before
	public void setUp() {
		route = new Route();
		coordinate = Mockito.mock(Coordinate.class);
		road = Mockito.mock(Road.class);
		agent = Mockito.mock(DefaultAgent.class);
		route.addToRoute(coordinate, road);
	}
	
	@Test
	public void canEnterRoadFullTest() {
		//given route
		//when
		Mockito.when(road.getRoadCapacity()).thenReturn(1);
		Mockito.when(road.getAgentsOnRoad()).thenReturn(1);
		//then
		assertFalse(route.canEnterRoad(agent, 0));
	}
	
	@Test
	public void canEnterRoadHighTrafficJamTest() {
		//given route
		//when
		Mockito.when(road.getRoadCapacity()).thenReturn(2);
		Mockito.when(road.getAgentsOnRoad()).thenReturn(1);
		Mockito.when(road.getTrafficJamRisk()).thenReturn(1.0);
		//then
		assertFalse(route.canEnterRoad(agent, 0));
	}
	
	
	@Test
	public void coordinatesTest() {
		//given route
		//when
		List<Coordinate> coordinates = new ArrayList<>();
		coordinates.add(coordinate);
		//then
		assertEquals(coordinates, route.getCoordinates());
	}
	
	@Test
	public void roadsTest() {
		//given route
		//when
		List<Road> roads = new ArrayList<>();
		roads.add(road);
		//then
		assertEquals(roads, route.getRoads());
	}
	
	@Test
	public void roadTest() {
		//given route
		//when
		//then
		assertEquals(road, route.getRoad(0));
	}
	
}

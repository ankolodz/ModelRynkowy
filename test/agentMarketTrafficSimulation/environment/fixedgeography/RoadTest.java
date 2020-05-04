package agentMarketTrafficSimulation.environment.fixedgeography;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.vividsolutions.jts.geom.Coordinate;

import agentMarketTrafficSimulation.agent.supervisor.SupervisorAgent;

public class RoadTest {

	private Road road;
	private Junction source;
	private Junction target;
	private Coordinate coordinate;
	private SupervisorAgent supervisorAgent;
	private double price = 10.0;
	private double speed = 1.0;
	private int capacity = 1;
	
	@Before
	public void setUp() {
		road = new Road();
		source = Mockito.mock(Junction.class);
		target = Mockito.mock(Junction.class);
		road.addJunction(source);
		road.addJunction(target);
		
		Mockito.when(source.getId()).thenReturn(1);
		Mockito.when(source.getCoordString()).thenReturn("1\t(1,1)");
		Mockito.when(source.getCoordXString()).thenReturn("1");
		Mockito.when(source.getCoordYString()).thenReturn("1");
		Mockito.when(target.getId()).thenReturn(2);
		Mockito.when(target.getCoordString()).thenReturn("2\t(2,2)");
		Mockito.when(target.getCoordXString()).thenReturn("2");
		Mockito.when(target.getCoordYString()).thenReturn("2");
		
		coordinate = Mockito.mock(Coordinate.class);
		road.setCoords(coordinate);
		
		supervisorAgent = Mockito.mock(SupervisorAgent.class);
		Mockito.when(supervisorAgent.getA()).thenReturn(1.0);
		road.setOwner(supervisorAgent);
		
		road.setSpeed(speed);
		road.setCapacity(capacity);
		road.setPrice(price);
	}
	
	@Test
	public void getStartAndEndPointTest() {
		//given road
		//when
		//then
		assertEquals("1\t(1,1)\t--->\t2\t(2,2)", road.getStartAndEndPoint());
	}
	
	@Test
	public void getStartAndEndPointIDTest() {
		//given road
		//when
		//then
		assertEquals("1  --->  2", road.getStartAndEndPointID());
	}
	
	@Test
	public void getStartPointIDTest() {
		//given road
		//when
		//then
		assertEquals("1", road.getStartPointID());
	}
	
	@Test
	public void getEndPointIDTest() {
		//given road
		//when
		//then
		assertEquals("2", road.getEndPointID());
	}
	
	@Test
	public void getStartCoordXTest() {
		//given road
		//when
		//then
		assertEquals("1", road.getStartCoordX());
	}
	
	@Test
	public void getStartCoordYTest() {
		//given road
		//when
		//then
		assertEquals("1", road.getStartCoordY());
	}
	
	@Test
	public void getEndCoordXTest() {
		//given road
		//when
		//then
		assertEquals("2", road.getEndCoordX());
	}
	
	@Test
	public void getEndCoordYTest() {
		//given road
		//when
		//then
		assertEquals("2", road.getEndCoordY());
	}
	
	@Test
	public void getJunctionsTest() {
		//given road
		//when
		ArrayList<Junction> list = new ArrayList<>();
		list.add(source);
		list.add(target);
		//then
		assertEquals(list, road.getJunctions());
	}
	
	@Test
	public void priceTest() {
		//given road
		//when
		//then
		assertEquals(price, road.getPrice(), 0.01);
	}
	
	@Test
	public void coordTest() {
		//given road
		//when
		//then
		assertEquals(coordinate, road.getCoords());
	}
	
	@Test
	public void ownerTest() {
		//given road
		//when
		//then
		assertEquals(supervisorAgent, road.getOwner());
	}
	
	@Test
	public void capacityTest() {
		//given road
		//when
		//then
		assertEquals(capacity, road.getRoadCapacity());
	}
	
	@Test
	public void reservationsTest() {
		//given road
		//when
		//then
		assertEquals(0, road.getRoadReservations());
	}
	
	@Test
	public void speedTest() {
		//given road
		//when
		//then
		assertEquals(speed, road.getSpeed(), 0.01);
	}
	
	@Test
	public void travelTimeTest() {
		//given road
		//when
		//then
		assertEquals(0.0, road.getTravelTime(), 0.01);
	}
	
	@Test
	public void isBlockedTest() {
		//given road
		//when
		//then
		assertFalse(road.isBlocked());
	}
	
	@Test
	public void lengthTest() {
		//given road
		//when
		//then
		assertEquals(0.0, road.getLength(), 0.01);
	}
	
	@Test
	public void agentsOnRoadTest() {
		//given road
		//when
		//then
		assertEquals(0, road.getAgentsOnRoad());
	}
	
	@Test
	public void calculateTrafficJamRiskTest() {
		//given road
		//when
		//then
		assertEquals(0.0, road.calculateTrafficJamRisk(), 0.01);
	}
}

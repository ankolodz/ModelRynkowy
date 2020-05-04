package agentMarketTrafficSimulation.environment.fixedgeography;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.vividsolutions.jts.geom.Coordinate;

public class JunctionTest {
	private Junction junction;
	private Coordinate coordinate;
	private Road road;
	
	@Before
	public void setUp() {
		coordinate = Mockito.mock(Coordinate.class);
		road = Mockito.mock(Road.class);
		junction = new Junction();
		junction.addRoad(road);
		junction.setCoords(coordinate);
	}
	
	@Test
	public void roadsTest() {
		//given junction
		//when
		List<Road> roads = new ArrayList<>();
		roads.add(road);
		//then
		assertEquals(roads, junction.getRoads());
	}
	
	@Test
	public void coordTest() {
		//given junction
		//when
		//then
		assertEquals(coordinate, junction.getCoords());
	}
	
	
}

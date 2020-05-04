package agentMarketTrafficSimulation.environment.fixedgeography;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


public class NetworkEdgeTest {

	private NetworkEdge<Junction> edge;
	
	@Before
	public void setUp() {
		Junction source = Mockito.mock(Junction.class);
		Junction target = Mockito.mock(Junction.class);
		edge = new NetworkEdge<>(source, target, false, 1.0);
	}
	
	@Test
	public void roadTest() {
		//given
		Road road = Mockito.mock(Road.class);
		//when
		edge.setRoad(road);
		//then
		assertEquals(road, edge.getRoad());
	}
}

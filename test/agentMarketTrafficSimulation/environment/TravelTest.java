package agentMarketTrafficSimulation.environment;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import agentMarketTrafficSimulation.agent.DefaultAgent;
import agentMarketTrafficSimulation.environment.fixedgeography.Junction;

public class TravelTest {

	private Travel travel;
	private DefaultAgent agent;
	private Junction endJunction;
	
	@Before
	public void setUp() {
		agent = Mockito.mock(DefaultAgent.class);
		endJunction = Mockito.mock(Junction.class);
		Mockito.when(agent.getEndJunction()).thenReturn(endJunction);
		travel = new Travel(agent);
	}
	
	@Test
	public void currentPositionTest() {
		//given travel
		//when
		//then
		assertEquals(0, travel.getCurrentPosition());
	}
	
}

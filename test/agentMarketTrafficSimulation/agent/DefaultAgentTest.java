package agentMarketTrafficSimulation.agent;

import static org.junit.Assert.*;

import java.util.Objects;

import org.junit.Before;
import org.junit.Test;

public class DefaultAgentTest {
	private DefaultAgent agent;

	@Before
	public void setUp() {
		agent = new DefaultAgent(null);
	}

	@Test
	public void distanceTravelledTest() {
		// given agent
		// when
		agent.addDistance(10);
		agent.addDistance(20);
		// then
		assertEquals(30, agent.getTravelledDistance(), 0.01);
	}

	@Test
	public void moneyPaidTest() {
		// given agent
		// when
		agent.payForRoad(20);
		agent.payForRoad(30);
		// then
		assertEquals(50, agent.getTotalMoneyPaid(), 0.01);
	}
	
	@Test
	public void subsequentlyCreatedAgentArentEqualTest() {
		// given
		DefaultAgent agent1 = new DefaultAgent(null);
		DefaultAgent agent2 = new DefaultAgent(null);
		// when 
		boolean areEquals = Objects.equals(agent1, agent2);
		// then
		assertFalse(areEquals);
	}
}

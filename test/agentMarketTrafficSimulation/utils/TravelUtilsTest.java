package agentMarketTrafficSimulation.utils;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import junit.framework.TestCase;

public class TravelUtilsTest extends TestCase {
	
	@BeforeEach
	protected void setUp() throws Exception {
		if (TravelUtils.getRoads() == null) {
			TravelUtils.setRoads(TestRoadNetwork.getTestRoadNetwork());
		}
	}
	
	@Test
	public void testDist() {
		assertEquals(TravelUtils.getMinDist(), 28284.2612, 0.0001);
		assertEquals(TravelUtils.getMaxDist(), 50990.2051, 0.0001);
	}
	
	
	@Test
	public void testWeight() {
		assertEquals(TravelUtils.getMinWeight(), 2.8184, 0.0001);
		assertEquals(TravelUtils.getMaxWeight(), 5.1090, 0.0001);
	}
	
	@Test
	public void testTime() {
		assertEquals(TravelUtils.getMinTime(), 471.3945, 0.0001);
		assertEquals(TravelUtils.getMaxTime(), 849.8465, 0.0001);
	}
	
	
	@Test
	public void testRisk() {
		assertEquals(TravelUtils.getMinRisk(), -0.01, 0.01);
		assertEquals(TravelUtils.getMaxRisk(), 0.01, 0.01);
	}
	
	

}

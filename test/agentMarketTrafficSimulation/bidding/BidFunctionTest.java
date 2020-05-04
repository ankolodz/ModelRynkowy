package agentMarketTrafficSimulation.bidding;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class BidFunctionTest {
	
	private BidFunction bidFunction;
	
	@Before
	public void setUp() {
		double E = -1;
		double cost = 1;
		double maxCost = 2;
		double minCost = 1;
		bidFunction = new BidFunction(E, cost, minCost, maxCost);
	}

	@Test
	public void zeroCrossingCalculationTest() {
		// given default bidFunction
		// when
		double zeroCrossing = bidFunction.getZeroCrossing();
		// then
		assertEquals(2, zeroCrossing, 0.01);
	}
	
	@Test
	public void functionValueTest() {
		// given default bidFunction
		// when
		double functionValue = bidFunction.getFunctionValue(1);
		// then
		assertEquals(-1, functionValue, 0.01);
	}
}

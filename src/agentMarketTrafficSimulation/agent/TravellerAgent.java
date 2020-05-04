package agentMarketTrafficSimulation.agent;

import agentMarketTrafficSimulation.bidding.BidFunction;
import agentMarketTrafficSimulation.environment.Travel;
import agentMarketTrafficSimulation.environment.fixedgeography.Junction;

public interface TravellerAgent extends IAgent {
	void step() throws Exception;

	void payForRoad(double d);

	Junction getEndJunction();

	Junction getStartJunction();

	void setEndJunction(Junction junction);

	Travel getTravel();

	boolean isTravelEnded();

	// CHANGED
	BidFunction getBidFunction();

	public double getTime();

	public double getDist();

	public double getCost();

	public double getRisk();

	// -------------change---------------
	public void setReservationPrice(double price);

	public double getReservationPrice();

	public void setCurrentPrice(double currentPrice);

	public double getCurrentPrice();
}

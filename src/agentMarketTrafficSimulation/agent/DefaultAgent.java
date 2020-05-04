package agentMarketTrafficSimulation.agent;

import java.util.Random;

import agentMarketTrafficSimulation.bidding.BidFunction;
import agentMarketTrafficSimulation.context.ContextManager;
import agentMarketTrafficSimulation.environment.Travel;
import agentMarketTrafficSimulation.environment.fixedgeography.Junction;
import agentMarketTrafficSimulation.environment.fixedgeography.Road;
import repast.simphony.util.collections.IndexedIterable;

public class DefaultAgent implements TravellerAgent {
	private static int uniqueID = 0;
	private int id;
	private Junction startJunction;
	private Junction endJunction;
	public Travel travel;
	private double totalMoneyPaid = 0.0;

	private int waitingSteps = 0;
	private boolean ended = false;
	private AgentType type;
	private static Random rand = new Random();
	private double distanceTravelled = 0;
	private int ticksTravelled = 0;
	private boolean start = false;
	private double time, dist, cost;
	private double reservationPrice = 0;
	private double currentPrice = 0;
	private Junction currentJunction;
	private Junction lastJunction;
	private int alternativeChosen = 0;

	public double getTime() {
		return time;
	}

	public double getDist() {
		return dist;
	}

	public double getCost() {
		return cost;
	}

	@Override
	public double getRisk() {
		// TODO change traffic jam risk preferences from random
		return rand.nextDouble();
	}

	// CHANGED added bidfunction to agent
	private BidFunction bidFunction;

	public AgentType getAgentType() {
		return type;
	}

	public String getAgentTypeString() {
		return "" + type;
	}

	public int getAgentStyleIndex() {
		if(this.isTravelEnded()) {
			return 0;
		}
		if (this.time == 1.0) {
			if (this.type == AgentType.ONE) {
				return 10;
			} else if (this.type == AgentType.TWO) {
				return 11;
			} else if (this.type == AgentType.THREE) {
				return 12;
			} else {
				return 13;
			}
		} else if (this.dist == 1.0) {
			if (this.type == AgentType.ONE) {
				return 20;
			} else if (this.type == AgentType.TWO) {
				return 21;
			} else if (this.type == AgentType.THREE) {
				return 22;
			} else {
				return 23;
			}
		} else {
			if (this.type == AgentType.ONE) {
				return 30;
			} else if (this.type == AgentType.TWO) {
				return 31;
			} else if (this.type == AgentType.THREE) {
				return 32;
			} else {
				return 33;
			}
		}
	}

	public void setAgentType(AgentType type) {
		this.type = type;
	}

	public DefaultAgent(Junction junction) {
		this.id = uniqueID++;
		this.startJunction = junction;
		this.type = AgentType.WHOLE;
		this.bidFunction = new BidFunction();
	}

	public DefaultAgent(Junction junction, int waitingSteps) {
		this.id = uniqueID++;
		this.startJunction = junction;
		this.waitingSteps = waitingSteps;
		this.type = AgentType.WHOLE;
		this.bidFunction = new BidFunction();
	}

	public DefaultAgent(Junction junction, int waitingSteps, AgentType type) {
		this.id = uniqueID++;
		this.startJunction = junction;
		this.waitingSteps = waitingSteps + rand.nextInt(6);
		this.type = type;
		this.bidFunction = new BidFunction();
	}

	public DefaultAgent(Junction junction, int waitingSteps, AgentType type, double time, double dist, double cost) {
		this.id = uniqueID++;
		this.startJunction = junction;
		this.waitingSteps = waitingSteps + rand.nextInt(6);
		this.type = type;
		this.time = time;
		this.dist = dist;
		this.cost = cost;
		this.bidFunction = new BidFunction();
		bidFunction.setCost(cost);
	}

	@Override
	public synchronized void step() throws Exception {
		// System.out.println("AGENT " + this.agentType + " " + this.id + " start " +
		// this.startJunction + " end " + this.endJunction);
		if (!ended) {
			if (start) {
				ticksTravelled++;
			}
			if (waitingSteps > 0) {
				waitingSteps--;
				return;
			}
			if (travel == null) {
				this.travel = new Travel(this);
			}
			travel.travel();
		}
	}

	public void endTravel() {
		this.travel = null;
		ended = true;
		// System.out.println("Agent " + this.id + " travel ended");
	}

	@Override
	public void payForRoad(double priceForRoad) {
		totalMoneyPaid += priceForRoad;
	}

	@Override
	public Junction getStartJunction() {
		return startJunction;
	}

	public void setStartJunction(Junction startJunction) {
		this.startJunction = startJunction;
	}

	@Override
	public Junction getEndJunction() {
		return endJunction;
	}

	@Override
	public void setEndJunction(Junction endJunction) {
		this.endJunction = endJunction;
	}

	public Travel getTravel() {
		return travel;
	}

	public void setTravel(Travel travel) {
		this.travel = travel;
	}

	@Override
	public String toString() {
		return "Agent " + this.id;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DefaultAgent))
			return false;
		DefaultAgent b = (DefaultAgent) obj;
		return this.id == b.id;
	}

	@Override
	public int hashCode() {
		return this.id;
	}

	public double getTotalMoneyPaid() {
		return totalMoneyPaid;
	}

	public int getId() {
		return id;
	}

	public boolean isTravelEnded() {
		return ended;
	}

	public boolean isAgentWaiting() {
		return waitingSteps > 0;
	}

	public void waitRandomSteps() {
		waitingSteps += rand.nextInt(15);
	}

	public void addDistance(double d) {
		distanceTravelled += d;
	}

	public double getTravelledDistance() {
		return distanceTravelled;
	}

	public int getTicks() {
		return start ? ticksTravelled : 0;
	}

	public void setStartTime() {
		if (!start) {
			ticksTravelled = 0;
			start = true;
		}
	}

	public double getTimePriority() {
		return time;
	}

	public double getDistancePriority() {
		return dist;
	}

	public double getCostPriority() {
		return cost;
	}

	public boolean started() {
		return start;
	}

	@Override
	public BidFunction getBidFunction() {
		return this.bidFunction;
	}

	public boolean hasReservations() {
		IndexedIterable<Road> roads = (ContextManager.getRoadContext().getObjects(Road.class));
		for (Road r : roads) {
			if (r.getOwner().agentHasReservation(this, r)) {
				return true;
			}
			return false;
		}
		return false;
	}

	// ---------------added-----------------------
	public double getReservationPrice() {
		return reservationPrice;
	}

	public void setReservationPrice(double price) {
		this.reservationPrice = price;
	}

	public double getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}

	public Junction getCurrentJunction() {
		return currentJunction;
	}

	public void setCurrentJunction(Junction currentjunction) {
		this.currentJunction = currentjunction;
	}

	public Junction getLastJunction() {
		return lastJunction;
	}

	public void setLastJunction(Junction lastJunction) {
		this.lastJunction = lastJunction;
	}

	public int getAlternativeChosen() {
		return alternativeChosen;
	}

	public void setAlternativeChosen(int alternativeChosen) {
		this.alternativeChosen = alternativeChosen;
	}
		
	public boolean isEnded() {
		return this.ended;
	}
}

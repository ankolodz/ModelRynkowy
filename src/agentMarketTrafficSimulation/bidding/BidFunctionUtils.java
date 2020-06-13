package agentMarketTrafficSimulation.bidding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agentMarketTrafficSimulation.agent.TravellerAgent;
import agentMarketTrafficSimulation.environment.fixedgeography.Road;
import agentMarketTrafficSimulation.utils.TravelUtils;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

public class BidFunctionUtils {
	static Parameters params = RunEnvironment.getInstance().getParameters();
	private final static int basePrice = (Integer) params.getValue("basePrice"); // parametr sym
	private final static int priceGrow = (Integer) params.getValue("priceGrowth"); // parametr sym

	public static Map<TravellerAgent, Double> getPriceAndAgentsBasedOnBidFunctions(List<TravellerAgent> agents,
			Road road) {
		double result = 0;
		Map<TravellerAgent, Double> zeroCrossings = new HashMap<TravellerAgent, Double>();
		for (TravellerAgent agent : agents) {
			double zeroCrossing = agent.getBidFunction().getZeroCrossing();
			zeroCrossings.put(agent, zeroCrossing);
		}
		Map<TravellerAgent, Double> successfullAgents = new HashMap<TravellerAgent, Double>();
		double roadCapacity = road.getRoadCapacity();
		double roadPrice = priceGrow;
		if (roadCapacity < agents.size()) {
			// searching for max zeroCrossing in agents
			TravellerAgent maxAgent = null;
			while (roadCapacity > 0) {
				maxAgent = getMaxZeroCrossing(zeroCrossings);
				successfullAgents.put(maxAgent, zeroCrossings.get(maxAgent));
				zeroCrossings.remove(maxAgent);
				--roadCapacity;
			}

			// CHANGED algorithm of counting price for road
			if (successfullAgents.get(maxAgent) == Double.MAX_VALUE) {
				if (road.getPrice() == 1.0) {
					roadPrice = basePrice + priceGrow * road.getRoadCapacity();
				} else {
					roadPrice = road.getPrice() + priceGrow * road.getRoadCapacity();
				}

			} else {
				// we are taking the agent first who didnt get to the road
				// and increasing his price by priceGrow
				roadPrice = zeroCrossings.get(getMaxZeroCrossing(zeroCrossings)).doubleValue() + priceGrow;
			}

		}
		// the case when there is less agents than road size
		else {
			if (road.getPrice() == 1.0) {
				roadPrice = basePrice + priceGrow * (agents.size() - road.getRoadCapacity());
			} else {
				roadPrice = road.getPrice() + priceGrow * (agents.size() - road.getRoadCapacity());
			}
			// checking if price is higher than 0

			if (roadPrice < priceGrow) {
				roadPrice = priceGrow;
			}
		}

		makeEqualPrice(successfullAgents, new Double(roadPrice));

		return successfullAgents;
	}

	private static void makeEqualPrice(Map<TravellerAgent, Double> map, Double value) {

		for (Map.Entry<TravellerAgent, Double> entry : map.entrySet()) {
			entry.setValue(value);
		}
	}

	private static TravellerAgent getMaxZeroCrossing(Map<TravellerAgent, Double> zeroCrossings) {
		Map.Entry<TravellerAgent, Double> maxEntry = null;

		for (Map.Entry<TravellerAgent, Double> entry : zeroCrossings.entrySet()) {
			if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
				maxEntry = entry;
			}
		}
		return maxEntry.getKey();
	}

	public static void calculateEforBidFunction(BidFunction bidFunction, double time, double length, double cost,
			double weigth, double risk, double edgeTime, double edgeLength, double edgeWeigth, double edgeRisk,
			double minTime, double maxTime, double minLength, double maxLength, double minCost, double maxCost,
			double minWeigth, double maxWeigth, double minRisk, double maxRisk) {

		double E = time * ((edgeTime - minTime) / (maxTime - minTime))
				+ length * ((edgeLength - minLength) / (maxLength - minLength))
				+ weigth * ((edgeWeigth - minWeigth) / (maxWeigth - minWeigth))
				+ risk * ((edgeRisk - minRisk) / (maxRisk - minRisk));

		bidFunction.setE(E);
	}

	public static void setUpBidFunction(BidFunction bidFunction, double time, double length, double cost, double weigth,
			double risk, double edgeTime, double edgeLength, double edgeWeigth, double edgeRisk) {
		bidFunction.setCost(cost);
		bidFunction.setMaxCost(TravelUtils.getMaxCost());
		bidFunction.setMinCost(TravelUtils.getMinCost());
		calculateEforBidFunction(bidFunction, time, length, cost, weigth, risk, edgeTime, edgeLength, edgeWeigth,
				edgeRisk, TravelUtils.getMinTime(), TravelUtils.getMaxTime(), TravelUtils.getMinDist(),
				TravelUtils.getMaxDist(), TravelUtils.getMinCost(), TravelUtils.getMaxCost(),
				TravelUtils.getMinWeight(), TravelUtils.getMaxWeight(), TravelUtils.getMinRisk(),
				TravelUtils.getMaxRisk());

	}

}

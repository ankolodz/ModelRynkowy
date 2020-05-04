package agentMarketTrafficSimulation.agent;

import java.util.List;
import java.util.Random;

import agentMarketTrafficSimulation.context.ContextManager;
import agentMarketTrafficSimulation.environment.fixedgeography.Junction;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

public class AgentFactory {
	// private static int j = 0;

	private AgentFactory() {

	}

	Parameters params = RunEnvironment.getInstance().getParameters();

	public static void createRandomAgentsStartingInStep(int wholeNumber, int oneNumber, int twoNumber, int threeNumber,
			int startStep, double time, double dist, double cost) {
		int wholeCreated = 0;
		int oneCreated = 0;
		int twoCreated = 0;
		int threeCreated = 0;
		int rand;
		List<Junction> gates = ContextManager.getGates();
		Random r = new Random();
		/*
		 * for(int i = 0; i < 4; i++){ wholeCreated = 0; oneCreated = 0; twoCreated = 0;
		 * threeCreated = 0; while (wholeCreated < wholeNumber/4 || oneCreated <
		 * oneNumber/4 || twoCreated < twoNumber/4 || threeCreated < threeNumber/4) {
		 */
		while (wholeCreated < wholeNumber || oneCreated < oneNumber || twoCreated < twoNumber
				|| threeCreated < threeNumber) {

			if (wholeCreated < wholeNumber) {
				rand = r.nextInt(gates.size());
				createNewAgent(gates.get(rand), startStep, AgentType.WHOLE, time, dist, cost);
				wholeCreated++;
			}
			if (oneCreated < oneNumber) {
				rand = r.nextInt(gates.size());
				createNewAgent(gates.get(rand), startStep, AgentType.ONE, time, dist, cost);
				oneCreated++;
			}
			if (twoCreated < twoNumber) {
				rand = r.nextInt(gates.size());
				createNewAgent(gates.get(rand), startStep, AgentType.TWO, time, dist, cost);
				twoCreated++;
			}
			if (threeCreated < threeNumber) {
				rand = r.nextInt(gates.size());
				createNewAgent(gates.get(rand), startStep, AgentType.THREE, time, dist, cost);
				threeCreated++;
			}
		}
	}

	private static void createNewAgent(Junction startJunction, int startStep, AgentType type, double time, double dist,
			double cost) {
		DefaultAgent agent;
		double rand = new Random().nextDouble() * (time + cost + dist);
		if (rand < time) {
			agent = new DefaultAgent(startJunction, startStep, type, 1, 0, 0);
		} else if (rand < time + dist) {
			agent = new DefaultAgent(startJunction, startStep, type, 0, 1, 0);
		} else {
			agent = new DefaultAgent(startJunction, startStep, type, 0, 0, 1);
		}

		/*
		 * if(time > ((1-time)/2)){ agent = new DefaultAgent(startJunction, startStep,
		 * type, time, (1-time)/2, (1-time)/2); } else if(dist > ((1-dist)/2)){ agent =
		 * new DefaultAgent(startJunction, startStep, type, dist, (1-dist)/2,
		 * (1-dist)/2); } else if(cost > ((1-cost)/2)){ agent = new
		 * DefaultAgent(startJunction, startStep, type, cost, (1-cost)/2, (1-cost)/2);
		 */

		/*
		 * if(j == 0){ agent = new DefaultAgent(startJunction, startStep, type, 1, 0,
		 * 0); } else if(j == 1){ agent = new DefaultAgent(startJunction, startStep,
		 * type, 0, 1, 0); } else { agent = new DefaultAgent(startJunction, startStep,
		 * type, 0, 0, 1); } j = (j + 1) % 3;
		 */

		List<Junction> gates = ContextManager.getGates();
		int randId = new Random().nextInt(gates.size());

		Junction endJunction = gates.get(randId);
		while (endJunction.equals(agent.getStartJunction())) {
			randId = new Random().nextInt(gates.size());
			endJunction = gates.get(randId);
		}
		agent.setEndJunction(endJunction);
		addAgentToConext(agent, startJunction);
	}

	private static void addAgentToConext(DefaultAgent agent, Junction startJunction) {
		ContextManager.addAgentToContext(agent);
		ContextManager.moveAgent(agent, ContextManager.getJunctionGeography().getGeometry(startJunction).getCentroid());
	}

}

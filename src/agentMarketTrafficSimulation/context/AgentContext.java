package agentMarketTrafficSimulation.context;

import repast.simphony.context.DefaultContext;
import agentMarketTrafficSimulation.agent.DefaultAgent;

public class AgentContext extends DefaultContext<DefaultAgent> {
	public static final String AGENT_CONTEXT = "AgentContext";
	public AgentContext() {
		super(AGENT_CONTEXT);
	}
}

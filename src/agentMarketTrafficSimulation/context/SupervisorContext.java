package agentMarketTrafficSimulation.context;

import agentMarketTrafficSimulation.agent.supervisor.SupervisorAgent;
import repast.simphony.context.DefaultContext;

public class SupervisorContext extends DefaultContext<SupervisorAgent> {
	public static final String SUPERVISOR_CONTEXT = "SupervisorContext";

	public SupervisorContext() {
		super(SUPERVISOR_CONTEXT);
	}
}

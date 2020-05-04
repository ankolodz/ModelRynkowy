package agentMarketTrafficSimulation.context;

import agentMarketTrafficSimulation.environment.fixedgeography.Junction;
import repast.simphony.context.DefaultContext;

public class JunctionContext extends DefaultContext<Junction> {
	public static final String JUNCTION_CONTEXT = "JunctionContext";

	public JunctionContext() {
		super(JUNCTION_CONTEXT);
	}
}

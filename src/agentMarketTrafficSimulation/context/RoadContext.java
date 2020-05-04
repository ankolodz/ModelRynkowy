package agentMarketTrafficSimulation.context;

import agentMarketTrafficSimulation.environment.fixedgeography.Road;
import repast.simphony.context.DefaultContext;

public class RoadContext extends DefaultContext<Road> {
	public static final String ROAD_CONTEXT = "RoadContext";

	public RoadContext() {
		super(ROAD_CONTEXT);
	}
}

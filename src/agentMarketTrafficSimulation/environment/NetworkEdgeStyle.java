package agentMarketTrafficSimulation.environment;

import java.awt.Color;
import java.util.List;

import org.jmock.core.constraint.IsInstanceOf;

import agentMarketTrafficSimulation.environment.fixedgeography.NetworkEdge;
import agentMarketTrafficSimulation.environment.fixedgeography.Road;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.visualization.gis3D.style.DefaultNetworkStyleGIS;

public class NetworkEdgeStyle extends DefaultNetworkStyleGIS{
	
	Integer maxPrice;
	
	public NetworkEdgeStyle() {
		Parameters params = RunEnvironment.getInstance().getParameters();
		this.maxPrice = params.getInteger("basePrice");
	}
	
	
	@Override
	public Color getLineColor(RepastEdge edge) {
		NetworkEdge<Object> networkEdge = (NetworkEdge<Object>)edge;
		float coef = (float) (1.0 * networkEdge.getRoad().getPrice() / this.maxPrice);
		if (coef > 1.0) {coef = 1.0f;}
		return new Color((1.0f - coef) * 0.7f, 0.05f * (float)(getLineWidth(edge)-1.0), coef * 0.7f);
	}


	@Override
	public double getLineWidth(RepastEdge edge) {
		Road road = ((NetworkEdge) edge).getRoad();
		return road.isRoadBidirectional() ? 3.0 : 1.0;
	}
}

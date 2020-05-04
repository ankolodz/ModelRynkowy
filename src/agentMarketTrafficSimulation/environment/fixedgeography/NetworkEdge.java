package agentMarketTrafficSimulation.environment.fixedgeography;

import repast.simphony.space.graph.RepastEdge;

public class NetworkEdge<T> extends RepastEdge<T> {
	private Road road;
	
	public NetworkEdge(T source, T target, boolean directed, double weight) {
		super(source, target, directed, weight);
	}	
	
	public Road getRoad() {
		return road;
	}
	
	public void setRoad(Road road) {
		this.road = road;
	}

	@Override
	public String toString() {
		return "Edge between "+this.getSource()+"->"+this.getTarget();
	}
}


// waga = dlugosc * predkosc bez samochodow
// czas = dlugosc * aktualna predkosc

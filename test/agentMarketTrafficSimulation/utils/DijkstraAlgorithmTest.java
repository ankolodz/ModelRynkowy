package agentMarketTrafficSimulation.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import agentMarketTrafficSimulation.agent.DefaultAgent;
import agentMarketTrafficSimulation.environment.fixedgeography.Junction;
import agentMarketTrafficSimulation.environment.fixedgeography.NetworkEdge;
import repast.simphony.space.graph.RepastEdge;

class DijkstraAlgorithmTest {

	private DijkstraAlgorithm algorithm;
	private DefaultAgent agent;
	
	@BeforeEach
	void setUp() throws Exception {
		TravelUtils.setRoads(TestRoadNetwork.getTestRoadNetwork());
		Graph graph = new Graph(TestRoadNetwork.getTestRoadNetwork());
		this.algorithm = new DijkstraAlgorithm(graph);
		this.agent = new DefaultAgent(TestRoadNetwork.getJunctions().get(0));
	}

	@Test
	void test() {
		Junction start = TestRoadNetwork.getJunctions().get(0);
		Junction end = TestRoadNetwork.getJunctions().get(6);
		this.algorithm.execute(start, end, 1.0, 0.0, 0.0, agent);
		List<RepastEdge<Junction>> pathTime = this.algorithm.getPath(start, end, 1.0, 0.0, 0.0, agent);
		List<RepastEdge<Junction>> pathCost = this.algorithm.getPath(start, end, 0.0, 1.0, 0.0, agent);
		List<RepastEdge<Junction>> pathDist = this.algorithm.getPath(start, end, 0.0, 0.0, 1.0, agent);
		double distTime = 0.0;
		for (RepastEdge<Junction> edge : pathTime) {
			NetworkEdge<Junction> networkEdge = (NetworkEdge<Junction>)edge;
			distTime += networkEdge.getRoad().getLength();
		}
		double distCost = 0.0;
		for (RepastEdge<Junction> edge : pathCost) {
			NetworkEdge<Junction> networkEdge = (NetworkEdge<Junction>)edge;
			distCost += networkEdge.getRoad().getLength();
		}
		double distDist = 0.0;
		for (RepastEdge<Junction> edge : pathDist) {
			NetworkEdge<Junction> networkEdge = (NetworkEdge<Junction>)edge;
			distDist += networkEdge.getRoad().getLength();
		}
		assertEquals(distTime, 0.0, 0.0001);
		assertEquals(distCost, 0.0, 0.0001);
		assertEquals(distDist, 0.0, 0.0001);
	}

}

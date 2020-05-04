package agentMarketTrafficSimulation.context.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import agentMarketTrafficSimulation.context.ContextManager;
import agentMarketTrafficSimulation.environment.fixedgeography.Junction;
import agentMarketTrafficSimulation.environment.fixedgeography.Road;

public class ContextManagerUtils {

	public static synchronized List<Junction> iteratorToList(Iterator<Junction> iterator) {
		List<Junction> list = new ArrayList<>();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}
		Collections.sort(list);
		return list;
	}

	public static List<Junction> createGates(List<Junction> junctions) {
		List<Junction> gates = new LinkedList<>();
		int check = 0;
		for (Junction j : junctions) {
			if (j.getRoads().size() == 2) {
				gates.add(j);
			}
		}
		while (gates.size() > junctions.size() / 24) {
			gates.remove(ContextManager.rand.nextInt(gates.size()));
		}
		for (int i = gates.size(); i < junctions.size() / 8; i++) {
			boolean ok = true;
			Junction gate = getRandomJunction(ContextManager.getAllJunctions());
			for (Road road : gate.getRoads()) {
				check = 0;
				if (road.getJunctions().get(0).equals(gate))
					check = 1;
				if (gates.contains(road.getJunctions().get(check))) {
					i--;
					ok = false;
					break;
				}
			}
			if (ok)
				gates.add(gate);
		}
		return gates;
	}

	public static synchronized Junction getRandomJunction(List<Junction> junctions) {
		int randomIndex = ContextManager.rand.nextInt(junctions.size());
		return junctions.get(randomIndex);
	}

	public static synchronized List<Junction> getRandomJunctions(int numberOfJunctions) {
		List<Junction> junctionList = new ArrayList<Junction>();
		for (int i = 0; i < numberOfJunctions; i++) {
			junctionList.add(getRandomJunction(ContextManager.getAllJunctions()));
		}
		return junctionList;
	}

}

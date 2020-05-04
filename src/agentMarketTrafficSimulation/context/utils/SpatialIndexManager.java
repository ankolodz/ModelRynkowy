package agentMarketTrafficSimulation.context.utils;

import java.util.HashMap;
import java.util.Map;

import repast.simphony.space.gis.Geography;

public class SpatialIndexManager {
	private static Map<Geography<?>, Index<?>> indices = new HashMap<Geography<?>, Index<?>>();

	public static <T> void createIndex(Geography<T> geog, Class<T> clazz) {
		Index<T> i = new Index<T>(geog, clazz);
		SpatialIndexManager.indices.put(geog, i);
	}
}

package agentMarketTrafficSimulation.context.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.index.strtree.STRtree;

import repast.simphony.space.gis.Geography;

public class Index<T> {
	SpatialIndex si;

	private Map<Geometry, T> featureLookup;

	public Index(Geography<T> geog, Class<T> clazz) {
		this.si = new STRtree();
		this.featureLookup = new HashMap<Geometry, T>();
		this.createIndex(geog, clazz);
	}

	private void createIndex(Geography<T> geog, Class<T> clazz) {
		Geometry geom;
		Envelope bounds;
		for (T t : geog.getAllObjects()) {
			geom = (Geometry) geog.getGeometry(t);
			bounds = geom.getEnvelopeInternal();
			this.si.insert(bounds, geom);
			this.featureLookup.put(geom, t);
		}
	}

	public T lookupFeature(Geometry geom) throws NoSuchElementException {
		assert this.featureLookup.containsKey(geom) : "Internal error: for some reason the "
				+ "given geometry is not a key in the feature lookup table.";
		return this.featureLookup.get(geom);
	}
}

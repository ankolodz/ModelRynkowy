package agentMarketTrafficSimulation.context.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import agentMarketTrafficSimulation.environment.fixedgeography.FixedGeography;
import agentMarketTrafficSimulation.environment.fixedgeography.Junction;
import agentMarketTrafficSimulation.environment.fixedgeography.NetworkEdge;
import agentMarketTrafficSimulation.environment.fixedgeography.Road;
import repast.simphony.context.Context;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.gis.ShapefileLoader;
import repast.simphony.space.graph.Network;

public class GISFunctions {

	public static <T extends FixedGeography> void readShapefile(Class<T> cl, String shapefileLocation,
			Geography<T> geog, Context<T> context) throws MalformedURLException, FileNotFoundException {
		File shapefile = null;
		ShapefileLoader<T> loader = null;
		shapefile = new File(shapefileLocation);
		if (!shapefile.exists()) {
			throw new FileNotFoundException("Could not find the given shapefile: " + shapefile.getAbsolutePath());
		}
		loader = new ShapefileLoader<T>(cl, shapefile.toURI().toURL(), geog, context);
		while (loader.hasNext()) {
			loader.next();
		}
		for (T obj : context.getObjects(cl)) {
			obj.setCoords(geog.getGeometry(obj).getCentroid().getCoordinate());
		}
	}

	public static void buildGISRoadNetwork(Geography<Road> roadGeography,
			Context<Junction> junctionContext, Context<Object> defaultContext,
			Geography<Junction> junctionGeography, Geography<Object> defaultGeography, Network<Junction> roadNetwork,  Network<Object> defaultRoadNetwork) {

		GeometryFactory geomFac = new GeometryFactory();
		Map<Coordinate, Junction> coordMap = new HashMap<Coordinate, Junction>();
		Map<Coordinate, Junction> defaultCoordMap = new HashMap<Coordinate, Junction>();
		
		for (Road road : roadGeography.getAllObjects()) {
			// Create a LineString from the road so we can extract coordinates
			Geometry roadGeom = roadGeography.getGeometry(road);
			Coordinate c1 = roadGeom.getCoordinates()[0];
			Coordinate c2 = roadGeom.getCoordinates()[roadGeom.getNumPoints() - 1];
			
			Junction junc1, junc2;
			Junction djunc1, djunc2;
			if (coordMap.containsKey(c1)) {
				junc1 = coordMap.get(c1);
				djunc1 = defaultCoordMap.get(c1);
			} else {
				junc1 = createNewJunction(c1, junctionContext, geomFac, junctionGeography, coordMap);
				djunc1 = new Junction();
				djunc1.setCoords(c1);
				defaultContext.add(djunc1);
				defaultCoordMap.put(c1, djunc1);
				defaultGeography.move(djunc1, geomFac.createPoint(c1));
			}
			if (coordMap.containsKey(c2)) {
				junc2 = coordMap.get(c2);
				djunc2 = defaultCoordMap.get(c2);
			} else {
				junc2 = createNewJunction(c2, junctionContext, geomFac, junctionGeography, coordMap);
				djunc2 = new Junction();
				djunc2.setCoords(c2);
				defaultContext.add(djunc2);
				defaultCoordMap.put(c2, djunc2);
				defaultGeography.move(djunc2, geomFac.createPoint(c2));
			}
			
			road.addJunction(junc1);
			road.addJunction(junc2);
			junc1.addRoad(road);
			junc2.addRoad(road);

			NetworkEdge<Junction> edge = new NetworkEdge<Junction>(junc1,
					junc2, false, roadGeom.getLength());
			
			road.setEdge(edge);
			edge.setRoad(road);
			if (!roadNetwork.containsEdge(edge)) {
				roadNetwork.addEdge(edge);
			}
			
			NetworkEdge<Object> defaultEdge = new NetworkEdge<Object>(djunc1,
					djunc2, false, roadGeom.getLength());
			defaultEdge.setRoad(road);
			if (!defaultRoadNetwork.containsEdge(defaultEdge)) {
				defaultRoadNetwork.addEdge(defaultEdge);
			}
		}
	}

	private static Junction createNewJunction(Coordinate c, Context<Junction> junctionContext, GeometryFactory geomFac,
			Geography<Junction> junctionGeography, Map<Coordinate, Junction> coordMap) {
		Junction junction = new Junction();
		junction.setCoords(c);
		junctionContext.add(junction);
		coordMap.put(c, junction);
		Point p2 = geomFac.createPoint(c);
		junctionGeography.move(junction, p2);
		return junction;
	}

}

package agentMarketTrafficSimulation.context;

import static agentMarketTrafficSimulation.context.ContextConstants.AGENT_GEOGRAPHY;
import static agentMarketTrafficSimulation.context.ContextConstants.JUNCTION_GEOGRAPHY;
import static agentMarketTrafficSimulation.context.ContextConstants.MAIN_CONTEXT;
import static agentMarketTrafficSimulation.context.ContextConstants.ROAD_GEOGRAPHY;
import static agentMarketTrafficSimulation.context.ContextConstants.ROAD_NETWORK;
import static agentMarketTrafficSimulation.context.ContextConstants.ROAD_SHAREFILE;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import agentMarketTrafficSimulation.agent.AgentFactory;
import agentMarketTrafficSimulation.agent.DefaultAgent;
import agentMarketTrafficSimulation.agent.supervisor.SupervisorAgent;
import agentMarketTrafficSimulation.context.generators.StatsGenerator;
import agentMarketTrafficSimulation.context.utils.ContextManagerUtils;
import agentMarketTrafficSimulation.context.utils.GISFunctions;
import agentMarketTrafficSimulation.context.utils.NetworkEdgeFactory;
import agentMarketTrafficSimulation.context.utils.SpatialIndexManager;
import agentMarketTrafficSimulation.environment.fixedgeography.Junction;
import agentMarketTrafficSimulation.environment.fixedgeography.Road;
import agentMarketTrafficSimulation.environment.fixedgeography.FixedGeography;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.gis.GeographyFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.gis.DefaultGeography;
import repast.simphony.space.gis.GISAdder;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.gis.GeographyParameters;
import repast.simphony.space.gis.SimpleAdder;
import repast.simphony.space.graph.Network;

public class ContextManager implements ContextBuilder<Object> {
	private static Context<Object> mainContext;
	private static Context<Road> roadContext;
	private static Geography<Road> roadProjection;
	private static Context<Junction> junctionContext;
	private static Geography<Junction> junctionGeography;
	private static Network<Junction> roadNetwork;
	private static Context<DefaultAgent> agentContext;
	private static Geography<DefaultAgent> agentGeography;
	private static Context<SupervisorAgent> supervisorContext;

	private static DefaultContext<Object> defaultContext;
	private static DefaultGeography<Object> defaultGeography;
	private static Network<Object> defaultRoadNetwork;

	private static List<Junction> junctions;
	private static List<Junction> gates;
	private static List<Road> roads;
	private static final String gisDataDir = "./data/gis_data/rzadka/";
	// private static final String gisDataDir = "./data/gis_data/gesta/";

	private static final StatsGenerator statsGenerator = new StatsGenerator();
	public static Random rand;

	@Override
	public Context<Object> build(Context<Object> context) {
		Parameters params = RunEnvironment.getInstance().getParameters();
		junctions = null;
		gates = null;
		roads = null;

		mainContext = context;
		mainContext.setId(MAIN_CONTEXT);
		GISAdder<Object> adder = createContents(params);

		for (Object object : roadProjection.getAllObjects()) {
			adder.add(defaultGeography, object);
			defaultGeography.move(object, roadProjection.getGeometry(object));
		}
		defaultContext.addProjection(defaultGeography);
		mainContext.addSubContext(defaultContext);
		return mainContext;
	}

	private GISAdder<Object> createContents(Parameters params) {
		int wholeNumber = (Integer) params.getValue("wholeNumber"); // parametr sym
		int oneNumber = (Integer) params.getValue("oneNumber"); // parametr sym
		int twoNumber = (Integer) params.getValue("twoNumber"); // parametr sym
		int threeNumber = (Integer) params.getValue("threeNumber"); // parametr sym
		double costPriorityNumber = (Double) params.getValue("costPriorityNumber"); // parametr sym
		double timePriorityNumber = (Double) params.getValue("timePriorityNumber"); // parametr sym
		double distancePriorityNumber = (Double) params.getValue("distancePriorityNumber"); // parametr sym
		int supervisorNumber = (Integer) params.getValue("supervisorNumber"); // parametr sym
		int maxRoadCapacity = (Integer) params.getValue("maxRoadCapacity"); // parametr sym
		int randomSeed = (Integer) params.getValue("randomSeed"); // parametr sym
		rand = new Random(randomSeed);

		defaultContext = new DefaultContext<Object>("DefaultContext");
		defaultGeography = new DefaultGeography<Object>("DefaultGeography");
		GISAdder<Object> adder = new SimpleAdder<>();

		try {
			createRoads(gisDataDir);
			createJunctions(gisDataDir);
			buildRoadNetwork(maxRoadCapacity);
			buildSupervisors(supervisorNumber);
			createAgents(wholeNumber, oneNumber, twoNumber, threeNumber, timePriorityNumber, distancePriorityNumber,
					costPriorityNumber);
			createSchedule();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return adder;
	}

	public void createRoads(String gisDataDir) throws MalformedURLException, FileNotFoundException {
		roadContext = new RoadContext();
		roadProjection = GeographyFactoryFinder.createGeographyFactory(null).createGeography(ROAD_GEOGRAPHY,
				roadContext, new GeographyParameters<Road>(new SimpleAdder<Road>()));
		String roadFile = gisDataDir + ROAD_SHAREFILE;
		
		GISFunctions.readShapefile(Road.class, roadFile, roadProjection, roadContext);
		mainContext.addSubContext(roadContext);
		SpatialIndexManager.createIndex(roadProjection, Road.class);
	}

	private void createJunctions(String gisDataDir) {
		junctionContext = new JunctionContext();
		mainContext.addSubContext(junctionContext);
		junctionGeography = GeographyFactoryFinder.createGeographyFactory(null).createGeography(JUNCTION_GEOGRAPHY,
				junctionContext, new GeographyParameters<Junction>(new SimpleAdder<Junction>()));
	}

	private void buildRoadNetwork(int maxRoadCapacity) {
		NetworkBuilder<Junction> builder = new NetworkBuilder<Junction>(ROAD_NETWORK,
				junctionContext, true);
		builder.setEdgeCreator(new NetworkEdgeFactory<Junction>());
		roadNetwork = builder.buildNetwork();
		
		NetworkBuilder<Object> defaultBuilder = new NetworkBuilder<Object>("DefaultNetwork",
				defaultContext, true);
		builder.setEdgeCreator(new NetworkEdgeFactory<Junction>());
		defaultRoadNetwork = defaultBuilder.buildNetwork();
		GISFunctions.buildGISRoadNetwork(roadProjection, junctionContext, defaultContext, junctionGeography, defaultGeography, roadNetwork, defaultRoadNetwork);
		initializeRoads();
		
		double maxLen = roads.get(0).getLength();
		double minLen = roads.get(0).getLength();
		
		for (Road road : roads) {
			if(road.getLength() > maxLen) maxLen = road.getLength();
			if(road.getLength() < minLen) minLen = road.getLength();
		}
		
		for (Road road : roads) {
			if(maxLen - minLen < minLen * 0.3){
				road.setCapacity(maxRoadCapacity);
			} else {
				road.setCapacity((int) ((maxRoadCapacity - getGates().size()) * ((road.getLength() - minLen) / (maxLen - minLen))) + getGates().size());
			}
		}
		SpatialIndexManager.createIndex(junctionGeography, Junction.class);
	}

	private void buildSupervisors(int supervisorCount) {
		supervisorContext = new SupervisorContext();
		mainContext.addSubContext(roadContext);
		for (int i = 0; i <= supervisorCount; i++) {
			SupervisorAgent agent = new SupervisorAgent();
			supervisorContext.add(agent);
		}
		matchRoadsWithSupervisors();
	}

	private void matchRoadsWithSupervisors() {
		for (Road road : roadContext.getObjects(Road.class)) {
			SupervisorAgent agent = supervisorContext.getRandomObject();
			road.setOwner(agent);
			agent.addRoad(road);
		}
	}

	private void createSchedule() {
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		ScheduleParameters agentStepParams = ScheduleParameters.createRepeating(1, 1, 0);
		// Schedule the agents' step methods.
		for (DefaultAgent a : agentContext.getObjects(DefaultAgent.class)) {
			schedule.schedule(agentStepParams, a, "step");
		}
		schedule.schedule(agentStepParams, statsGenerator, "generateStats", this);
		// schedule.schedule(agentStepParams, XMLStatsGenerator, "generateStats", this);
	}

	private void createAgents(int wholeNumber, int oneNumber, int twoNumber, int threeNumber, double timePriorityNumber,
			double distancePriorityNumber, double costPriorityNumber) {
		agentContext = new AgentContext();
		mainContext.addSubContext(agentContext);
		agentGeography = GeographyFactoryFinder.createGeographyFactory(null).createGeography(AGENT_GEOGRAPHY,
				agentContext, new GeographyParameters<DefaultAgent>(new SimpleAdder<DefaultAgent>()));
		int startStep = 0;
		Parameters params = RunEnvironment.getInstance().getParameters();
		int numberOfWaves = (Integer) params.getValue("numberOfWaves"); // parametr sym
		int stepsBetweenWaves = (Integer) params.getValue("stepsBetweenWaves"); // parametr sym

		for (int i = 0; i < numberOfWaves; i++) {
			AgentFactory.createRandomAgentsStartingInStep(wholeNumber, oneNumber, twoNumber, threeNumber, startStep,
					(double) timePriorityNumber, (double) distancePriorityNumber, (double) costPriorityNumber);
			startStep += stepsBetweenWaves;
		}
	}

	public static Geography<Road> getRoadProjection() {
		return roadProjection;
	}

	public static Network<Junction> getRoadNetwork() {
		return roadNetwork;
	}

	public static Geography<Junction> getJunctionGeography() {
		return junctionGeography;
	}

	public static synchronized List<Junction> getGates() {
		if (!(gates == null))
			return gates;

		gates = ContextManagerUtils.createGates(getAllJunctions());
		return gates;
	}

	public static synchronized List<Junction> getAllJunctions() {
		if (junctions == null) {
			initializeJunctions();
		}
		return junctions;
	}

	public static List<Road> getAllRoads() {
		if (roads == null) {
			initializeRoads();
		}
		return roads;
	}

	private static void initializeRoads() {
		roads = new LinkedList<>();
		for (Junction j : getAllJunctions()) {
			for (Road r : j.getRoads()) {
				if (!roads.contains(r)) {
					roads.add(r);
				}
			}
		}
	}

	private static synchronized void initializeJunctions() {
		junctions = ContextManagerUtils.iteratorToList(junctionContext.iterator());
	}

	public static synchronized void addAgentToContext(DefaultAgent agent) {
		ContextManager.defaultContext.add(agent);
		ContextManager.agentContext.add(agent);
	}

	public static synchronized void moveAgent(DefaultAgent agent, Point point) {
		ContextManager.defaultGeography.move(agent, point);
		ContextManager.agentGeography.move(agent, point);
	}

	public static synchronized Geometry getAgentGeometry(DefaultAgent agent) {
		return ContextManager.agentGeography.getGeometry(agent);
	}

	public static synchronized void moveAgentByVector(DefaultAgent agent, double distToTravel, double angle) {
		ContextManager.defaultGeography.moveByVector(agent, distToTravel, angle);
		ContextManager.agentGeography.moveByVector(agent, distToTravel, angle);
	}

	public static Context<Road> getRoadContext() {
		return roadContext;
	}

	public static Context<DefaultAgent> getAgentContext() {
		return agentContext;
	}

	public static Context<SupervisorAgent> getSupervisorContext() {
		return supervisorContext;
	}
}

package agentMarketTrafficSimulation.environment.fixedgeography;

import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;

import agentMarketTrafficSimulation.agent.supervisor.SupervisorAgent;
import agentMarketTrafficSimulation.environment.utils.DistanceCalculator;

public class Road implements FixedGeography {

	// The junctions at either end of the road
	private ArrayList<Junction> junctions;
	private Coordinate coord;

	// The NetworkEdge which represents this Road in the roadNetwork
	transient private NetworkEdge<Junction> edge;

	private SupervisorAgent owner;
	// CHANGED default price for road is 1, the bidFunctonUtils changes that
	private double price = 1.0;
	private double speed;
	private double roadCapacity;
	private int roadReservations = 0;
	private double length;
	private int agentsOnRoad = 0;

	private double traficJamRisk;

	private final double RISK_THRESHOLD = 0.2;

	public Road() {
		this.junctions = new ArrayList<Junction>();
		this.traficJamRisk = calculateTrafficJamRisk();
	}

	// ---------------start--------------
	public String getStartAndEndPoint() {
		Junction startJunction = junctions.get(0);
		Junction endJunction = junctions.get(junctions.size() - 1);

		return "" + startJunction.getCoordString() + "\t--->\t" + endJunction.getCoordString();
	}

	public String getStartAndEndPointID() {
		Junction startJunction = junctions.get(0);
		Junction endJunction = junctions.get(junctions.size() - 1);

		return "" + startJunction.getId() + "  --->  " + endJunction.getId();
	}

	public String getStartPointID() {
		return Integer.toString(junctions.get(0).getId());
	}

	public String getEndPointID() {
		return Integer.toString(junctions.get(junctions.size() - 1).getId());
	}

	public String getStartCoordX() {
		return junctions.get(0).getCoordXString();
	}

	public String getStartCoordY() {
		return junctions.get(0).getCoordYString();
	}

	public String getEndCoordX() {
		return junctions.get(junctions.size() - 1).getCoordXString();
	}

	public String getEndCoordY() {
		return junctions.get(junctions.size() - 1).getCoordYString();
	}

	// -------------------end------------------

	public void addJunction(Junction j) {
		if (this.junctions.size() == 2) {
			System.err.println("Road: Error: this Road object already has two Junctions.");
		}
		this.junctions.add(j);
	}

	public void setOwner(SupervisorAgent owner) {
		this.owner = owner;
		speed = owner.getA();
	}

	public ArrayList<Junction> getJunctions() {
		if (this.junctions.size() != 2) {
			System.err.println("Road: Error: This Road does not have two Junctions");
		}
		return this.junctions;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getPrice() {
		return this.price;
	}

	public NetworkEdge<Junction> getEdge() {
		return edge;
	}

	public Coordinate getCoords() {
		return coord;
	}

	public void setCoords(Coordinate coord) {
		this.coord = coord;
	}

	public void setEdge(NetworkEdge<Junction> edge) {
		this.edge = edge;
		/*double distanceAndAngle[] = new double[2];
		 *DistanceCalculator.calculateDistance(edge.getSource().getCoords(), edge.getTarget().getCoords(),
		 *		distanceAndAngle);
		 *System.out.println(distanceAndAngle[0] + ", "  edge.getSource().getCoords().distance3D(edge.getTarget().getCoords()));
		 *this.length = distanceAndAngle[0] / 1000;
		 */
		this.length = edge.getSource().getCoords().distance(edge.getTarget().getCoords()) * 10000;
	}

	public SupervisorAgent getOwner() {
		return this.owner;
	}

	public int getRoadCapacity() {
		return (int) roadCapacity;
	}

	public int getRoadReservations() {
		return roadReservations;
	}

	public double getSpeed() {
		return speed;
	}

	public double getTravelTime() {
		return length / speed;
	}

	public void setSpeed(double speed) {
		if (speed < 0.1) {
			this.speed = 0.1;
		}
		this.speed = speed;
	}

	public void incrementRoadReservations() {
		roadReservations++;
	}

	public void decrementRoadReservations() {
		if (roadReservations == (int) roadCapacity) {
			roadCapacity *= 1.1;
		}
		roadReservations--;
	}

	public boolean isBlocked() {
		return roadReservations >= roadCapacity;
	}

	public double getLength() {
		return length;
	}

	public void agentEnterRoad() {
		agentsOnRoad++;

		if (getRoadCapacity() < getAgentsOnRoad())
			System.out.println("ERROR - Road overloaded");

		// greenshield model
		setSpeed(owner.getA() - owner.getB() * (agentsOnRoad / length));
	}

	public void agentExitRoad() {
		agentsOnRoad--;

		// greenshield model
		setSpeed(owner.getA() - owner.getB() * (agentsOnRoad / length));
	}

	public int getAgentsOnRoad() {
		return agentsOnRoad;
	}

	public void setCapacity(int i) {
		roadCapacity = i;

	}

	public double getTrafficJamRisk() {
		updateTrafficJamRisk();
		return this.traficJamRisk;
	}

	public double calculateTrafficJamRisk() {
		double risk = this.agentsOnRoad / this.roadCapacity;
		return risk < RISK_THRESHOLD ? 0.0 : risk;
	}

	public void updateTrafficJamRisk() {
		this.traficJamRisk = calculateTrafficJamRisk();
	}

	public void setTrafficJamRisk(double risk) {
		if (risk > 1.0) {
			this.traficJamRisk = 1.0;
		} else if (risk < 0.0) {
			this.traficJamRisk = 0.0;
		} else {
			this.traficJamRisk = risk;
		}
	}

}

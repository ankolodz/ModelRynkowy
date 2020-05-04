package agentMarketTrafficSimulation.environment.fixedgeography;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

public class Junction implements FixedGeography, Comparable<Junction> {
	public static int UniqueID = 0;
	private int id ;
	private Coordinate coord;
	private List<Road> roads; // The Roads connected to this Junction, used in GIS road network
	
	public Junction() {
		this.id = UniqueID++;
		this.roads = new ArrayList<Road>();
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return "Junction "+this.id+" ("+this.coord.x+","+this.coord.y+")";
	}
	
	public String getCoordString(){
		return ""+this.id+"\t("+(float) this.coord.x+","+ (float) this.coord.y+ ")";
	}
	
	public String getCoordXString(){
		return ""+(float) this.coord.x;
	}
	
	public String getCoordYString(){
		return ""+(float) this.coord.y;
	}
	
	public List<Road> getRoads() {
		return this.roads;
	}
	
	public void addRoad(Road road) {
		this.roads.add(road);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Junction)) {
			return false;
		}
		Junction j = (Junction) obj;
		return this.getCoords().equals(j.getCoords());
	}

	/**
	 * Get the coordinate of this junction
	 */
	public Coordinate getCoords() {
		return coord;
	}
	
	@Override
	public void setCoords(Coordinate c) {
		this.coord = c;
		
	}

	@Override
	public int compareTo(Junction o) {
		return o.coord.compareTo(this.coord);
	}
	
	public boolean checkRoadsJunction(Road road, Junction junction) {
		for (int i = 0; i < roads.size(); i++) {
			if (!road.equals(roads.get(i)) && roads.get(i).checkJunction(junction)) {
				System.out.println("Droga dwukierunkowa!");
				return true;
			}
		}
		return false;
	}
}

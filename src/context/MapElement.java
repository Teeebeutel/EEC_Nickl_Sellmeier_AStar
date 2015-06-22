package context;

import java.util.ArrayList;

public class MapElement {
	private int posX; // position in the map
	private int posY;

	private boolean occupied; // element is known in advance to be occupied

	/* variables for A* implementation */

	private boolean start;
	private boolean goal;

	/* Nodes that this is connected to */
	Map map;

	ArrayList<MapElement> neighborList;

	boolean visited;

	float distanceFromStart;
	float heuristicDistanceFromGoal;

	MapElement previousNode;
	private double f;

	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	public boolean isOccupied() {
		return occupied;
	}

	public boolean isStart() {
		return start;
	}

	public boolean isGoal() {
		return goal;
	}

	public MapElement(int posX, int posY) {
		super();
		neighborList = new ArrayList<MapElement>();
		this.posX = posX;
		this.posY = posY;
		this.visited = false;
		this.occupied = false;
		this.start = false;
		this.goal = false;
	}

	public MapElement(int posX, int posY, boolean occupied, boolean start, boolean goal) {
		super();
		neighborList = new ArrayList<MapElement>();
		this.posX = posX;
		this.posY = posY;
		this.visited = false;
		this.occupied = occupied;
		this.start = start;
		this.goal = goal;
	}

	public void setOccupied() {
		occupied = true;
	}

	public void setStart() {
		start = true;
	}

	public void setGoal() {
		goal = true;
	}

	public float getDistanceFromStart() {
		return distanceFromStart;
	}

	public void setDistanceFromStart(float f) {
		this.distanceFromStart = f;
	}

	public MapElement getPreviousNode() {
		return previousNode;
	}

	public void setPreviousNode(MapElement previousNode) {
		this.previousNode = previousNode;
	}

	public boolean equals(MapElement node) {
		return (node.getPosX() == posX) && (node.getPosY() == posY);
	}

	public ArrayList<MapElement> getNeighborList() {
		return neighborList;
	}

	public void setNorth(MapElement north) {
		neighborList.add(north);

	}

	public void setEast(MapElement east) {
		neighborList.add(east);
	}

	public void setSouth(MapElement south) {
		neighborList.add(south);
	}

	public void setWest(MapElement west) {
		neighborList.add(west);

	}

	public void setF(double f) {
		this.f = f;

	}

	public double getF() {
		return f;
	}
}

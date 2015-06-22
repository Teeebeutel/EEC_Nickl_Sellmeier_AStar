package context;

import java.util.ArrayList;

public class MapElement {
	private int posX;
	private int posY;

	/**
	 *  variables for A* implementation 
	 */
	private boolean start;
	private boolean goal;
	private boolean occupied;
	MapElement previousNode;
	float distanceFromStart;
	private double f;

	/**
	 *  Nodes this is connected to 
	 */
	ArrayList<MapElement> neighborList;



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
		this.occupied = false;
		this.start = false;
		this.goal = false;
	}

	public MapElement(int posX, int posY, boolean occupied, boolean start, boolean goal) {
		super();
		neighborList = new ArrayList<MapElement>();
		this.posX = posX;
		this.posY = posY;
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

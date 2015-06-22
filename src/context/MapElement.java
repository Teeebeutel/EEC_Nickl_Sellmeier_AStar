package context;

import java.util.ArrayList;

public class MapElement implements Comparable<MapElement> {
	private int posX; // position in the map
	private int posY;

	private boolean occupied; // element is known in advance to be occupied
	private double probOccupied; // estimation of the state of this element
									// from observation

	private boolean onBeam; // set temporarily if the element is hit by infrared
							// beam

	/* variables for A* implementation */

	private boolean start;
	private boolean goal;

	/* Nodes that this is connected to */
	Map map;
	MapElement north;
	// Node northEast;
	MapElement east;
	// Node southEast;
	MapElement south;
	// Node southWest;
	MapElement west;
	// Node northWest;

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

	public double getProbOccupied() {
		return probOccupied;
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

	public MapElement(int posX, int posY, boolean occupied, boolean start,
			boolean goal) {
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

	public boolean onBeam() {
		return onBeam;
	}

	public void setOnBeam(boolean onBeam) {
		this.onBeam = onBeam;
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

	public float getHeuristicDistanceFromGoal() {
		return heuristicDistanceFromGoal;
	}

	public void setHeuristicDistanceFromGoal(float heuristicDistanceFromGoal) {
		this.heuristicDistanceFromGoal = heuristicDistanceFromGoal;
	}

	public ArrayList<MapElement> getNeighborList() {
		return neighborList;
	}

	@Override
	public int compareTo(MapElement otherNode) {
		float thisTotalDistanceFromGoal = heuristicDistanceFromGoal
				+ distanceFromStart;
		float otherTotalDistanceFromGoal = otherNode
				.getHeuristicDistanceFromGoal()
				+ otherNode.getDistanceFromStart();

		if (thisTotalDistanceFromGoal < otherTotalDistanceFromGoal) {
			return -1;
		} else if (thisTotalDistanceFromGoal > otherTotalDistanceFromGoal) {
			return 1;
		} else {
			return 0;
		}
	}

	public MapElement getNorth() {
		return north;
	}

	public void setNorth(MapElement north) {
		// replace the old Node with the new one in the neighborList
		if (neighborList.contains(this.north))
			neighborList.remove(this.north);

		neighborList.add(north);

		// set the new Node
		this.north = north;
	}

	public MapElement getEast() {
		return east;
	}

	public void setEast(MapElement east) {
		// replace the old Node with the new one in the neighborList
		if (neighborList.contains(this.east))
			neighborList.remove(this.east);

		neighborList.add(east);

		// set the new Node
		this.east = east;
	}

	public MapElement getSouth() {
		return south;
	}

	public void setSouth(MapElement south) {
		// replace the old Node with the new one in the neighborList
		if (neighborList.contains(this.south))
			neighborList.remove(this.south);

		neighborList.add(south);

		// set the new Node
		this.south = south;
	}

	public MapElement getWest() {
		return west;
	}

	public void setWest(MapElement west) {
		// replace the old Node with the new one in the neighborList
		if (neighborList.contains(this.west))
			neighborList.remove(this.west);

		neighborList.add(west);

		// set the new Node
		this.west = west;
	}

	public void setF(double f) {
		this.f = f;
		
	}

	public double getF() {
		return f;
	}
}

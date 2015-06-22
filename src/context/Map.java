package context;

import java.util.ArrayList;
import java.util.Random;

public class Map {
	private int sizeX, sizeY; // number of elements in each direction
								// used to get a discrete model of the
								// environmet

	private double posX, posY; // current position

	private MapElement[][] elements; // Array of MapElement representing the
										// environment

	private int occupied = 20; // number of occupied elements

	private int goalLocationX;
	private int goalLocationY;
	
	private AStarHeuristic h;

	public Map(int sx, int sy, int goalx, int goaly, int occupied, int startX, int startY, AStarHeuristic h) {
		this.sizeX = sx;
		this.sizeY = sy;
		this.occupied = occupied;
		this.h = h;

		elements = new MapElement[sx][sy];

		initMap(goalx, goaly, startX, startY);
		registerEdges();
	}

	public double getPosX() {
		return posX;
	}

	public double getPosY() {
		return posY;
	}

	private void initMap(int goalx, int goaly, int startX, int startY) {

		// initialize each element of the map

		for (int x = 0; x < sizeX; x++) {
			for (int y = 0; y < sizeY; y++) {
				elements[x][y] = new MapElement(x, y);
			}
		}

		elements[startX][startY].setStart();
		initObstacles();
		goalLocationX = goalx; // sizeX;
		goalLocationY = goaly; // sizeY;
		elements[goalLocationX][goalLocationY].setGoal();
	}

	private void initObstacles() {
		// collect N distinct random numbers between 0 and the max number of
		// MapElements in this Map
		ArrayList<Integer> occupiedElements = new ArrayList<Integer>();
		Random r = new Random();
		while (occupiedElements.size() < occupied) {
			Integer pos = new Integer(r.nextInt((sizeX * sizeY) - 2) + 1);
			if (!occupiedElements.contains(pos))
				occupiedElements.add(pos);
		}

		// find MapElement corresponding to each of the numbers and set its
		// state to occupied

		for (Integer pos : occupiedElements) {

			int x = pos / sizeY; // integer division by number of columns
			int y = pos % sizeX; // rest of integer division by number of rows

			elements[x][y].setOccupied();
		}
	}

	public void printMap() {
		for (int y = 0; y < sizeY; y++) {
			for (int x = 0; x < sizeX; x++) {
				MapElement e = elements[x][y];

				System.out.print(e.isOccupied() ? "[X]" : "[ ]");
				System.out.print("");
			}

			System.out.print("\n");
		}
	}

	public int getSizeX() {
		return sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

	/*  */
	public MapElement getElement(int x, int y) {
		return elements[x][y];
	}

	public float getDistanceBetween(MapElement current, MapElement neighbor) {
		return (float)  h.getHeuristicDistance(current, neighbor);
	}

	public int getGoalLocationX() {
		return goalLocationX;
	}

	public int getGoalLocationY() {
		return goalLocationY;
	}

	private void registerEdges() {
		for (int x = 0; x < sizeX; x++) {
			for (int y = 0; y < sizeY; y++) {
				int i = 0;
				MapElement node = elements[x][y];
				if (!(y == 0) && !elements[x][y - 1].isOccupied()) {
					node.setNorth(elements[x][y - 1]);
					i++;
				}
				if (!(x >= sizeX-1)) {
					if (!elements[x + 1][y].isOccupied()) {
						node.setEast(elements[x + 1][y]);
						i++;
					}
				}
				if (!(y == sizeY-1)) {
					if (!elements[x][y + 1].isOccupied()) {
						node.setSouth(elements[x][y + 1]);
						i++;
					}
				}
				if (!(x == 0) && !elements[x - 1][y].isOccupied()) {
					node.setWest(elements[x - 1][y]);
					i++;
				}
				System.out.println("node " + x + "|" + y + " has " + i + " neighbours");
			}
		}
	}
}

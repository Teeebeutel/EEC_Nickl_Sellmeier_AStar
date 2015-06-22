package AStarAlgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import context.Map;
import context.MapElement;

public class AStar {
	private Map map;
	private AStarHeuristic heuristic;
	private int startX;
	private int startY;
	private int goalX;
	private int goalY;
	/**
	 * closedList The list of Nodes not searched yet, sorted by their distance
	 * to the goal as guessed by our heuristic.
	 */

	private ArrayList<MapElement> closedList;
	private OpenList ol;
	private SortedNodeList openList;
	private Path shortestPath;
	private boolean ludwigAstar = true;

	public AStar(Map map, AStarHeuristic heuristic) {
		this.map = map;
		this.heuristic = heuristic;

		closedList = new ArrayList<MapElement>();
		if (ludwigAstar) {
			ol = new OpenList();
		} else {
			openList = new SortedNodeList();
		}
	}

	public Path findPath(int startX, int startY, int goalX, int goalY) {
		this.goalX = goalX;
		this.goalY = goalY;
		ol.enqueue(map.getElement(startX, startY), 0);
		MapElement curNode;
		while (!ol.isEmpty()) {
			curNode = ol.removeMin();
			if (curNode.getPosX() == map.getGoalLocationX() && curNode.getPosY() == map.getGoalLocationY()) {
				System.out.println("Done");// with cost of: " + ol.getCost());
				Path p = reconstructPath(map.getElement(goalX, goalY));
				printPath();
				return p;
			}
			closedList.add(curNode);
			expandNode(curNode);
		}
		System.out.println("No Path");
		return null;
	}

	private void expandNode(MapElement n) {
		double tentative_g;
		for (MapElement nextNode : n.getNeighborList()) {
			if (closedList.contains(nextNode)) {
				continue;
			}
			tentative_g = n.getDistanceFromStart() + map.getDistanceBetween(n, nextNode);
			if (ol.contains(nextNode) && tentative_g >= nextNode.getDistanceFromStart()) {
				continue;
			}
			nextNode.setPreviousNode(n);
			nextNode.setDistanceFromStart((float) tentative_g);
			// TODO: possible source of error: getDistfrom goal
			double f = tentative_g + map.getDistanceBetween(nextNode, map.getElement(goalX, goalY));
			if (ol.contains(nextNode)) {
				ol.decreaseKey(nextNode, f);
			} else {
				ol.enqueue(nextNode, f);
			}
		}
	}

	public Path calcShortestPath(int startX, int startY, int goalX, int goalY) {
		this.startX = startX;
		this.startY = startY;
		this.goalX = goalX;
		this.goalY = goalY;

		// System.out.println("Start: "+ startX + ", " +startY + "\nGoal: " +
		// goalX + ", " + goalY);

		// mark start and goal node
		// map.setStartLocation(startX, startY);
		// map.setGoalLocation(goalX, goalY);

		// Check if the goal node is blocked (if it is, it is impossible to find
		// a path there)
		if (map.getElement(goalX, goalY).isOccupied()) {
			return null;
		}

		map.getElement(startX, startY).setDistanceFromStart(0);
		closedList.clear();
		openList.clear();
		openList.add(map.getElement(startX, startY));

		// while we haven't reached the goal yet
		while (openList.size() != 0) {
			System.out.println("\n");
			// get the first Node from non-searched Node list, sorted by lowest
			// distance from our goal as guessed by our heuristic
			MapElement current = openList.getFirst();

			// check if our current Node location is the goal Node. If it is, we
			// are done.
			// System.out.println(map.getGoalLocationX() + ", " +
			// map.getGoalLocationY());
			if (current.getPosX() == map.getGoalLocationX() && current.getPosY() == map.getGoalLocationY()) {
				Path path = reconstructPath(current);
				printPath();
				return path;
			}

			// move current Node to the closed (already searched) list
			openList.remove(current);
			closedList.add(current);

			// go through all the current Nodes neighbors and calculate if one
			// should be our next step
			for (MapElement neighbor : current.getNeighborList()) {
				boolean neighborIsBetter;

				// if we have already searched this Node, don't bother and
				// continue to the next one
				if (closedList.contains(neighbor)) {
					System.out.println("already searched " + neighbor.getPosX() + "|" + neighbor.getPosY());
					continue;
				}
				// also just continue if the neighbor is an obstacle
				if (!neighbor.isOccupied()) {

					// calculate how long the path is if we choose this neighbor
					// as the next step in the path
					float currentDistanceFromStart = current.getDistanceFromStart();
					float distanceBetween = map.getDistanceBetween(current, neighbor);
					float neighborDistanceFromStart = (currentDistanceFromStart + distanceBetween);
					System.out.println("currentdistfromstart: " + currentDistanceFromStart + " distance between: " + distanceBetween
							+ " neightbordistance :" + neighborDistanceFromStart);

					// add neighbor to the open list if it is not there
					if (!openList.contains(neighbor)) {
						openList.add(neighbor);
						neighborIsBetter = true;
						// if neighbor is closer to start it could also be
						// better
					} else if (neighborDistanceFromStart < current.getDistanceFromStart()) {
						neighborIsBetter = true;
					} else {
						neighborIsBetter = false;
					}
					// set neighbors parameters if it is better
					if (neighborIsBetter) {
						System.out.println("node at " + neighbor.getPosX() + "|" + neighbor.getPosY() + " is better");
						neighbor.setPreviousNode(current);
						neighbor.setDistanceFromStart(neighborDistanceFromStart);
						neighbor.setHeuristicDistanceFromGoal(heuristic.getEstimatedDistanceToGoal(neighbor.getPosX(), neighbor.getPosY(),
								map.getGoalLocationX(), map.getGoalLocationY()));
					}
				}
			}
		}
		Path p = reconstructPath(map.getElement(goalX, goalY));

		return p;
	}

	public void printPath() {
		MapElement node;
		for (int x = 0; x < map.getSizeX(); x++) {

			if (x == 0) {
				for (int i = 0; i <= map.getSizeX(); i++)
					System.out.print("-");

				System.out.println();
			}

			System.out.print("|");

			for (int y = 0; y < map.getSizeY(); y++) {
				node = map.getElement(x, y);
				// System.out.println(map);
				// System.out.println(map.getSizeY());
				// System.out.println(node);
				// System.out.println(node.getPosX());
				// System.out.println(node.getPosY());
				if (node.isOccupied()) {
					System.out.print("[X]");
				} else if (node.isStart()) {
					System.out.print("[S}");
				} else if (node.isGoal()) {
					System.out.print("[G]");
				} else if (shortestPath.contains(node.getPosX(), node.getPosY())) {
					System.out.print("[O]");
				} else {
					System.out.print("[ ]");
				}

				if (y == map.getSizeY())
					System.out.print("_");
			}

			System.out.print("|");
			System.out.println();
		}

		for (int i = 0; i <= map.getSizeX(); i++)
			System.out.print("-");
	}

	private Path reconstructPath(MapElement node) {
		Path path = new Path();

		while (node != null) {
			path.prependWayPoint(node);
			node = node.getPreviousNode();
		}

		this.shortestPath = path;
		return path;
	}

	private class SortedNodeList {

		private ArrayList<MapElement> list = new ArrayList<MapElement>();

		public MapElement getFirst() {
			return list.get(0);
		}

		public void clear() {
			list.clear();
		}

		public void add(MapElement node) {
			list.add(node);
			Collections.sort(list);
		}

		public void remove(MapElement n) {
			list.remove(n);
		}

		public int size() {
			return list.size();
		}

		public boolean contains(MapElement n) {
			return list.contains(n);
		}
	}

	private class OpenList {
		ArrayList<MapElement> list = new ArrayList<>();

		public void decreaseKey(MapElement nextNode, double f) {
			list.remove(nextNode);
			nextNode.setF(f);
			list.add(nextNode);

		}

		public void enqueue(MapElement nextNode, double f) {
			nextNode.setF(f);
			list.add(nextNode);
		}

		public MapElement removeMin() {
			double oldCost = Double.POSITIVE_INFINITY;
			MapElement toreturn = null;
			for (MapElement e : list) {
				if (e.getF() < oldCost) {
					oldCost = e.getF();
					toreturn = e;
				}
			}
			list.remove(toreturn);
			return toreturn;
		}

		public boolean contains(MapElement nextNode) {
			return list.contains(nextNode);
		}

		public boolean isEmpty() {
			return list.isEmpty();
		}
	}

	/*
	 * private class OpenList extends ArrayList<MapElement> { private double
	 * cost;
	 * 
	 * public double getCost(){ return cost; }
	 * 
	 * public void enqueue(MapElement n, couble c){ cost = c; add(n); }
	 * 
	 * public void decreaseKey(MapElement n, double c){
	 * 
	 * }
	 * 
	 * public MapElement removeMin(){ MapElement n = get(0); int index = 0;
	 * for(int i = 1; i<this.size();i++){ if(n.getF() > get(i).getF()){ n =
	 * get(i); index = i; } } n = remove(index); return n; }
	 * 
	 * 
	 * }
	 */

	public void printPath(boolean b) {
		MapElement node;
		for (int x = 0; x < map.getSizeX(); x++) {

			if (x == 0) {
				for (int i = 0; i <= map.getSizeX(); i++)
					System.out.print("-");

				System.out.println();
			}

			System.out.print("|");

			for (int y = 0; y < map.getSizeY(); y++) {
				node = map.getElement(x, y);
				// System.out.println(map);
				// System.out.println(map.getSizeY());
				// System.out.println(node);
				// System.out.println(node.getPosX());
				// System.out.println(node.getPosY());
				if (node.isOccupied()) {
					System.out.print("[X]");
				} else if (node.isStart()) {
					System.out.print("[S}");
				} else if (node.isGoal()) {
					System.out.print("[G]");
				} else {
					System.out.print("[ ]");
				}

				if (y == map.getSizeY())
					System.out.print("_");
			}

			System.out.print("|");
			System.out.println();
		}

		for (int i = 0; i <= map.getSizeX(); i++)
			System.out.print("-");
	}
	
	private char getCurrentOrientation(MapElement current){
		if(current.getPreviousNode() == null){
			return 's';
		}
		char result = 'x';
		int cx = current.getPosX();
		int cy = current.getPosY();
		int px = current.getPreviousNode().getPosX();
		int py = current.getPreviousNode().getPosY();
		if(cx>px){
			return 's';
		}else if(cx<px){
			return 'n';
		}else if(cy>py){
			return 'e';
		}else if(cy<py){
			return 'w';
		}
		return result;
	}
}

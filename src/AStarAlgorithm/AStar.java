package AStarAlgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import context.Map;
import context.MapElement;

public class AStar {
	private Map map;
	private int goalX;
	private int goalY;


	private ArrayList<MapElement> closedList;
	private OpenList ol;
	private Path shortestPath;

	public AStar(Map map) {
		this.map = map;

		closedList = new ArrayList<MapElement>();
		ol = new OpenList();

	}

	public Path findPath(int startX, int startY, int goalX, int goalY) {
		this.goalX = goalX;
		this.goalY = goalY;
		ol.enqueue(map.getElement(startX, startY), 0);
		MapElement curNode;
		while (!ol.isEmpty()) {
			curNode = ol.removeMin();
			if (curNode.getPosX() == map.getGoalLocationX() && curNode.getPosY() == map.getGoalLocationY()) {
				System.out.println("Found a Path:");
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
		int i = 0;
		for (MapElement nextNode : n.getNeighborList()) {
			i++;
			if (closedList.contains(nextNode)) {
				continue;
			}
			tentative_g = n.getDistanceFromStart() + map.getDistanceBetween(n, nextNode);
			if (ol.contains(nextNode) && tentative_g >= nextNode.getDistanceFromStart()) {
				continue;
			}
			nextNode.setPreviousNode(n);
			nextNode.setDistanceFromStart((float) tentative_g);
			double f = tentative_g + map.getDistanceBetween(nextNode, map.getElement(goalX, goalY));
			if (ol.contains(nextNode)) {
				ol.decreaseKey(nextNode, f);
			} else {
				ol.enqueue(nextNode, f);
			}
		}
	}
	
	/**
	 *Unneccesary Method, only used for Showing the calculated path 
	 */
	public void printPath() {
		MapElement node;
		for (int y = 0; y < map.getSizeY(); y++) {
			for (int x= 0; x < map.getSizeY(); x++) {
				node = map.getElement(x, y);
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
			System.out.println();
		}
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

}

package context;

import java.util.ArrayList;
import java.util.Random;

import main.MainController;
import math.KalmanFilter;

import org.ejml.data.DenseMatrix64F;

import RoutePlanner.RouteReader;
import observer.MapPanel;

public class Map {
	private int sizeX, sizeY; // number of elements in each direction
								// used to get a discrete model of the
								// environmet
	private int thymioX, thymioY; // coordinates of MapElement where Thymio is
									// currently located on.
	private double posX, posY; // current position of Thymio in real units
	private double thymioTheta; // current orientation of Thymio in the global
								// coordinate system
	private double estPosX, estPosY; // estimated current position of Thymio in
										// real units
	private double estTheta; // estimated current orientation of Thymio in the
								// global coordinate system
	private MapElement[][] elements; // Array of MapElement representing the
										// environment
	private Path path;

	private double edgelength; // each element in this maps covers edgelength^2
								// square units.

	public static final int N = 20; // number of occupied elements

	private KalmanFilter posEstimate;
	private int goalLocationX;
	private int goalLocationY;
	private static final float COSTFORTURN = 2;
	private static final String FILEPATH = "res/map.txt";/* path to file */
	private static final String DILIMITER = ",";

	public Map(int x, int y, double l, int goalx, int goaly) {
		edgelength = l;
		sizeX = x;
		sizeY = y;

		elements = new MapElement[sizeX][sizeY];

		initMap(goalx, goaly);
		initFilter();
		registerEdges();
	}

	private void initFilter() {
		DenseMatrix64F F;
		DenseMatrix64F Q;
		DenseMatrix64F P;

		// state transition

		double[][] valF = { { 1, 0, 0, 0, 0 }, { 0, 1, 0, 0, 0 }, { 0, 0, 1, 0, 0 }, { 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0 } };
		F = new DenseMatrix64F(valF);

		// process noise

		double[][] valQ = { { 0.0005, 0.00002, 0.00002, 0.00002, 0.00002 }, { 0.00002, 0.001, 0, 0, 0 }, { 0.00002, 0, 0.00001, 0, 0 },
				{ 0.00002, 0, 0, 0.001, 0 }, { 0.00002, 0, 0, 0, 0.00001 } };
		Q = new DenseMatrix64F(valQ);

		// initial state

		double[][] valP = { { 0.000001, 0, 0, 0, 0 }, { 0, 0.000001, 0, 0, 0 }, { 0, 0, 0.000001, 0, 0 }, { 0, 0, 0, 0.000001, 0 },
				{ 0, 0, 0, 0, 0.000001 } };
		P = new DenseMatrix64F(valP);

		double[] state = { 0, 0, 0, 0, 0 };

		posEstimate = new KalmanFilter();
		posEstimate.configure(F, Q);
		posEstimate.setState(DenseMatrix64F.wrap(5, 1, state), P);
	}

	public double getEdgeLength() {
		return edgelength;
	}

	public void setPose(double x, double y, double theta) {
		posX = x;
		posY = y;
		thymioTheta = theta;

		updateCurrentPos();
	}

	public void updatePose(double dF, double dR, double dt) {
		double[] delta = new double[5];

		delta[0] = Math.cos(thymioTheta) * dF;
		delta[1] = Math.sin(thymioTheta) * dF;
		delta[2] = dR;
		delta[3] = dF;
		delta[4] = dR;

		DenseMatrix64F Gu = DenseMatrix64F.wrap(5, 1, delta);

		thymioTheta = thymioTheta + dR;
		posX += delta[0];
		posY += delta[1];

		// observation model

		double[][] valH = { { 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0 } };
		valH[0][3] = 1 / dt;
		valH[1][4] = 1 / dt;
		DenseMatrix64F H = new DenseMatrix64F(valH);

		// sensor noise

		double[][] valR = { { 0.01, 0.0001 }, { 0.0001, 0.001 } };
		DenseMatrix64F R = new DenseMatrix64F(valR);

		// sensor values

		double[] speed = { dF / dt, dR / dt };

		posEstimate.predict(Gu);
		// System.out.println("cov x predict: " + posEstimate.getCovariance());
		posEstimate.update(DenseMatrix64F.wrap(2, 1, speed), H, R);

		DenseMatrix64F estimState = posEstimate.getState();
		estPosX = estimState.get(0);
		estPosY = estimState.get(1);
		estTheta = estimState.get(2);

		// System.out.println("cov x update : " + posEstimate.getCovariance());

		updateCurrentPos();
	}

	private void updateCurrentPos() {
		thymioX = (int) (posX / MapPanel.LENGTH_EDGE_CM);
		thymioY = (int) (posY / MapPanel.LENGTH_EDGE_CM);
	}

	public int getThymioX() {
		return thymioX;
	}

	public int getThymioY() {
		return thymioY;
	}

	public double getEstimPosX() {
		return estPosX;
	}

	public double getEstimPosY() {
		return estPosY;
	}

	public double getPosX() {
		return posX;
	}

	public double getPosY() {
		return posY;
	}

	public double getThymioOrientation() {
		return thymioTheta;
	}

	public double getEstimOrientation() {
		return estTheta;
	}

	private void initMap(int goalx, int goaly) {

		// initialize each element of the map

		for (int x = 0; x < sizeX; x++) {
			for (int y = 0; y < sizeY; y++) {
				elements[x][y] = new MapElement(x, y);
			}
		}
		initObstacles();
		if (MainController.DOHACKYSTUFF) {
			doHackyStuff();
		}
		elements[0][0].setStart();

		goalLocationX = goalx; // sizeX;
		goalLocationY = goaly; // sizeY;
		elements[goalLocationX][goalLocationY].setGoal();
		/*
		 * // collect N distinct random numbers between 0 and the max number of
		 * MapElements in this Map while (occupiedElements.size() < N) { Integer
		 * pos = new Integer(r.nextInt(sizeX * sizeY)); if
		 * (!occupiedElements.contains(pos)) occupiedElements.add(pos); }
		 * 
		 * // find MapElement corresponding to each of the numbers and set its
		 * // state to occupied
		 * 
		 * for (int i = 0; i < N; i++) { Integer pos = occupiedElements.get(i);
		 * int x = pos / sizeY; // integer division by number of columns int y =
		 * pos % sizeX; // rest of integer division by number of rows
		 * 
		 * elements[x][y].setOccupied(); }
		 */
	}

	private void doHackyStuff() {
		for (int i = 0; i < elements.length; i++) {
			elements[i][9].setOccupied();
		}
	}

	private void initObstacles() {
		ArrayList<Integer[]> thingy = RouteReader.getMapPoints(FILEPATH, DILIMITER);
		System.out.println(elements.length);
		System.out.println(elements[0].length);
		for (Integer[] i : thingy) {
			// System.out.println(i[0]+";"+i[1]);
			elements[i[1]][i[0]].setOccupied();
		}
	}

	private ArrayList<Integer[]> initRandomMap() {
		ArrayList<Integer[]> thingy = new ArrayList<Integer[]>();
		Random rGen = new Random();
		do {
			int x = rGen.nextInt(sizeX);
			int y = rGen.nextInt(sizeY);
			Integer[] i = { x, y };
			if (!thingy.contains(i)) {
				thingy.add(i);
			}
		} while (thingy.size() < 20);
		return thingy;
	}

	public void printMap() {
		for (int x = 0; x < sizeX; x++) {
			for (int y = 0; y < sizeY; y++) {
				MapElement e = elements[x][y];

				System.out.print(e.isOccupied() ? "T" : "F");
				System.out.print(e.onBeam() ? "B" : "-");
				System.out.print("\t");
			}

			System.out.print("\n");
		}
	}

	public void followBeam(int x1, int y1, int x2, int y2) {
		path = new Path();
		int x = x1, y = y1;

		int w = x2 - x;
		int h = y2 - y;
		int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;

		if (w < 0) {
			dx1 = -1;
		} else if (w > 0) {
			dx1 = 1;
		}

		if (h < 0) {
			dy1 = -1;
		} else if (h > 0) {
			dy1 = 1;
		}

		if (w < 0) {
			dx2 = -1;
		} else if (w > 0) {
			dx2 = 1;
		}
		int longest = Math.abs(w);
		int shortest = Math.abs(h);
		if (!(longest > shortest)) {
			longest = Math.abs(h);
			shortest = Math.abs(w);
			if (h < 0)
				dy2 = -1;
			else if (h > 0)
				dy2 = 1;
			dx2 = 0;
		}
		int numerator = longest >> 1;
		for (int i = 0; i <= longest; i++) {
			path.add(new Coordinate(x, y));
			numerator += shortest;
			if (!(numerator < longest)) {
				numerator -= longest;
				x += dx1;
				y += dy1;
			} else {
				x += dx2;
				y += dy2;
			}
		}

		// return path;
	}

	public ArrayList<Integer[]> getCoordTupels() {
		ArrayList<Integer[]> list = new ArrayList<Integer[]>();
		for (Coordinate c : path) {
			Integer[] arr = { c.getX(), c.getY() };
			list.add(arr);
		}
		return list;
	}

	public int getSizeX() {
		return sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

	public boolean isOnBeam(int x, int y) {
		return elements[x][y].onBeam();
	}

	/*  */
	public MapElement getElement(int x, int y) {
		return elements[x][y];
	}

	public float getDistanceBetween(MapElement current, MapElement neighbor) {
		double distance = 0;
		int additionalCost = 0;

		float x1 = current.getPosX();
		float y1 = current.getPosY();
		float x2 = neighbor.getPosX();
		float y2 = neighbor.getPosY();
		if (current.getPreviousNode() != null) {
			int x0 = current.getPreviousNode().getPosX();
			int y0 = current.getPreviousNode().getPosY();
			if (x0 == x1 && x1 != x2 || y0 == y1 && y1 != y2) {
				additionalCost += COSTFORTURN;
			}
		}

		distance = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2)) + additionalCost;
		return (float) distance;
	}

	public int getGoalLocationX() {
		return goalLocationX;
	}

	public int getGoalLocationY() {
		return goalLocationY;
	}

	public MapElement getStartNode() {
		// TODO add non hard coded case?
		return elements[0][0];
	}

	private void registerEdges() {
		for (int x = 0; x < sizeX - 1; x++) {
			for (int y = 0; y < sizeY - 1; y++) {
				MapElement node = elements[x][y];
				if (!(y == 0) && !elements[x][y - 1].isOccupied())
					node.setNorth(elements[x][y - 1]);
				if (!(x == sizeX) && !elements[x + 1][y].isOccupied())
					node.setEast(elements[x + 1][y]);
				if (!(y == sizeY) && !elements[x][y + 1].isOccupied())
					node.setSouth(elements[x][y + 1]);
				if (!(x == 0) && !elements[x - 1][y].isOccupied())
					node.setWest(elements[x - 1][y]);
			}
		}
	}
}

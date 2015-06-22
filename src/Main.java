import AStarAlgorithm.AStar;
import AStarAlgorithm.Path;
import context.AStarHeuristic;
import context.Map;
import context.MapElement;

public class Main {
	private static final int MAP_SIZE_X = 10;
	private static final int MAP_SIZE_Y = 10;
	private static final int TARGET_X = 9;
	private static final int TARGET_Y = 9;
	private static final int START_X = 0;
	private static final int START_Y = 1;
	private static final int NUM_OBSTACLES = 10;
	
	/*constants used in our heuristic*/
	private static final int COSTFORTURN = 2;
	
	public static void main(String[] args) {
		Map m = new Map(MAP_SIZE_X, MAP_SIZE_Y, TARGET_X, TARGET_Y, NUM_OBSTACLES, START_X, START_Y, new AStarHeuristic() {
			
			@Override
			public double getHeuristicDistance(MapElement current, MapElement nextNode) {
				double distance = 0;
				int additionalCost = 0;

				float x1 = current.getPosX();
				float y1 = current.getPosY();
				float x2 = nextNode.getPosX();
				float y2 = nextNode.getPosY();
				if (current.getPreviousNode() != null) {
					int x0 = current.getPreviousNode().getPosX();
					int y0 = current.getPreviousNode().getPosY();
					if (x0 == x1 && x1 != x2 || y0 == y1 && y1 != y2) {
						additionalCost += COSTFORTURN;
					}
				}

				distance = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2)) + additionalCost;
				return distance;
			}
		} );
		
		m.printMap();
		
		AStar a = new AStar(m);

		Path p = a.findPath(START_X, START_Y, TARGET_X, TARGET_Y);
	}
}

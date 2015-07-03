import AStarAlgorithm.AStar;
import AStarAlgorithm.AStarHeuristic;
import AStarAlgorithm.Path;
import context.Map;
import context.MapElement;
/**
 * This is a simple implementation for a A*-Algorithm.
 * We used a modified version of our Code from SS14 Informationssysteme(Ludwig)
 * To have a more general use, a Interface for calculating the heuristic distance between two Nodes was added.
 * This can be used to assign different cost to certain Edges. In this case we Want to keep the number of turns as low as possible
 * 
 * The Map used only has vertiacal/horizontal edges and is initialized with a set of blocked fields.
 * To generalize this, the registerEdges()(Map.java) can be moved to an interface just 
 * like the getHeuristicDistance(), recieving the Maps array of MapElements
 * 
 * Run and
 */
public class Main {
	
	/*information used to build the map*/
	private static final int MAP_SIZE_X = 10;
	private static final int MAP_SIZE_Y = 10;
	private static final int TARGET_X = 9;
	private static final int TARGET_Y = 9;
	private static final int START_X = 0;
	private static final int START_Y = 0;
	private static final int NUM_OBSTACLES = 10;
	
	/*constants used in our heuristic*/
	private static final int COSTFORTURN = 1;
	
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

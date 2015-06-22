package context;

public interface AStarHeuristic {

	/*used to calculate additional cost depending on the surrounding nodes*/
	double getHeuristicDistance(MapElement current, MapElement nextNode);

}

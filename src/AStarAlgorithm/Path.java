package AStarAlgorithm;

import java.util.ArrayList;

import context.MapElement;

public class Path {
	
    // The waypoints in the path (list of coordiantes making up the path)
	private ArrayList<MapElement> waypoints = new ArrayList<MapElement>();
    
    public Path() {
    }
    
    public int getLength() {
        return waypoints.size();
    }

    public MapElement getWayPoint(int index) {
        return waypoints.get(index);
    }

    /**
     * Get the x-coordinate for the waypoiny at the given index.
     * 
     * @param index The index of the waypoint to get the x-coordinate of.
     * @return The x coordinate at the waypoint.
     */
    public int getX(int index) {
        return getWayPoint(index).getPosX();
    }

    /**
     * Get the y-coordinate for the waypoint at the given index.
     * 
     * @param index The index of the waypoint to get the y-coordinate of.
     * @return The y coordinate at the waypoint.
     */
    public int getY(int index) {
        return getWayPoint(index).getPosY();
    }

    /**
     * Append a waypoint to the path.  
     * 
     * @param x The x coordinate of the waypoint.
     * @param y The y coordinate of the waypoint.
     */
    public void appendWayPoint(MapElement n) {
        waypoints.add(n);
    }

    /**
     * Add a waypoint to the beginning of the path.  
     * 
     * @param x The x coordinate of the waypoint.
     * @param y The y coordinate of the waypoint.
     */
    public void prependWayPoint(MapElement n) {
    	waypoints.add(0, n);
    }

	/**
	 * Check if this path contains the WayPoint
	 * 
	 * @param x The x coordinate of the waypoint.
	 * @param y The y coordinate of the waypoint.
	 * @return True if the path contains the waypoint.
	 */
    public boolean contains(int x, int y) {
        for(MapElement node : waypoints) {
            if (node.getPosX() == x && node.getPosY() == y)
                return true;
        }
        return false;
    }
    
    public ArrayList<MapElement> getArrayList(){
    	return waypoints;
    }

}
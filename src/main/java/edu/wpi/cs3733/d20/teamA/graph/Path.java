package edu.wpi.cs3733.d20.teamA.graph;

import java.util.*;

/** Represents a path along the graph */
public class Path extends PathAlgo implements IStrategyPath {

  /**
   * Creates a new Path based off what graph we are trying to path find on
   *
   * @param graph Graph to that the path will be found on
   */
  public Path(Graph graph) {
    super(graph);
  }

  /**
   * Uses the A Star algorithm to locate the optimal path between the given start and end node
   *
   * @param start the starting location to path find
   * @param start the starting location to path find
   * @param end the ending location/ destination
   */
  @Override
  public void findPath(Node start, Node end) {
    Map<Node, Node> path = new HashMap<>();
    ArrayList<Node> visited = new ArrayList<>();
    PriorityQueue<Node> minPQ = new PriorityQueue<>();

    // For every node in the graph (excluding the start node) set their total cost infinity
    for (Node a : graph.getNodes().values()) {
      a.setCost(Integer.MAX_VALUE);
    }

    // Sets the cost to go from the start to 0
    start.setCost(0);

    // Add start node to the minimum priority queue
    minPQ.add(start);

    // While the priority queue is not empty
    while (!minPQ.isEmpty()) {
      // Get the next smallest cost node
      Node current = minPQ.poll();
      visited.add(current);

      // Early exit condition
      if (current.equals(end)) break;

      // Look at all of that nodes neighbors
      for (Edge edge : current.getEdges().values()) {
        Node neighbor = edge.getEnd();

        // Only consider nodes on this floor for now.
        // if (neighbor.getFloor() != start.getFloor()) {
        //  continue;
        // }

        // if the neighbor hasn't been visited
        if (!visited.contains(neighbor)) {

          // Calculate the new cost of getting there with the heuristic
          int newCost =
              Math.abs(current.getCost() + edge.getWeight() + calcHeuristic(neighbor, end));

          // If the new cost is less than the current cost, set the total cost to get there to new
          // cost
          if (neighbor.getCost() > newCost) {
            neighbor.setCost(newCost);

            // Update the map
            path.put(neighbor, current);
          }

          // Add the neighbors to the priority queue if it isn't in it already
          if (!minPQ.contains(neighbor)) minPQ.add(neighbor);
        }
      }
    }

    // If the path doesn't exist return null
    if (!path.containsKey(end)) return;

    // Clear path node list
    pathNodes.clear();

    Node curr = end;
    // Creates the list of nodes in order to create the final path
    while (!curr.equals(start)) {
      pathNodes.add(curr);

      curr = path.get(curr);
    }
    pathNodes.add(start);

    // Flip to correct path direction
    Collections.reverse(pathNodes);
  }

  /**
   * Calculate the Euclidean distance between the current node and goal node So that nodes closer to
   * the end will be given a higher priority
   *
   * @param current The node you are currently at in the path
   * @param goal The next node you are visiting in the path
   * @return int The Euclidean distance between the nodes
   */
  private static int calcHeuristic(Node current, Node goal) {
    double xDist = Math.pow((goal.getX() - current.getX()), 2);
    double yDist = Math.pow((goal.getY() - current.getY()), 2);
    double zDist = Math.pow((current.getFloor() - goal.getFloor()) * 100, 2);

    return (int) Math.sqrt(xDist + yDist + zDist);
  }
}

package edu.wpi.cs3733.d20.teamA.graph;

import com.sun.javafx.scene.traversal.Direction;
import java.util.*;

/** Represents a path along the graph */
public class Path {
  private enum cardinalDirections {
    NORTH,
    SOUTH,
    EAST,
    WEST
  };

  private ArrayList<Node> pathNodes; // List of nodes along path
  private ArrayList<Edge> pathEdges; // List of forward & reverse edges along path
  private Graph graph;

  public Path(Graph graph) {
    this.graph = graph;
    pathNodes = new ArrayList<>();
    pathEdges = new ArrayList<>();
  }

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
        if (neighbor.getFloor() != start.getFloor()) {
          continue;
        }

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

    calculateEdges();
  }

  public ArrayList<Node> getPathNodes() {
    return pathNodes;
  }

  public ArrayList<Edge> getPathEdges() {
    return pathEdges;
  }

  private void calculateEdges() {
    // Clear existing edge list
    pathEdges.clear();

    for (int i = 0; i < (pathNodes.size() - 1); i++) {
      // Locate forward edges
      for (Edge edge : pathNodes.get(i).getEdges().values()) {
        if (edge.getEnd().equals(pathNodes.get(i + 1))) {
          // Forward edge found, add it to the list.
          pathEdges.add(edge);
        }
      }
    }
  }

  // Calculate the "Manhattan distance" between the current node and goal node
  // So that nodes closer to the end will be given a higher priority
  private static int calcHeuristic(Node current, Node goal) {
    double xDist = Math.pow((goal.getX() - current.getX()), 2);
    double yDist = Math.pow((goal.getY() - current.getY()), 2);

    return (int) Math.sqrt(xDist + yDist);
  }

  public String textualDirections() {
    if (pathNodes.get(0).getY() > pathNodes.get(pathNodes.size() - 1).getY())
      return textualDirectionsFromOrientation(Direction.LEFT, Direction.RIGHT);
    else return textualDirectionsFromOrientation(Direction.RIGHT, Direction.LEFT);
  }

  // If the start node is closer to the bottom on the graph than the end node, first = LEFT second =
  // RIGHT
  // else first = RIGHT second = LEFT
  public String textualDirectionsFromOrientation(Direction first, Direction second) {
    double currentAngle = 0;
    String textPath = "";
    ArrayList<Direction> directions = new ArrayList<>();
    for (int i = 0; i < pathNodes.size() - 1; i++) {

      int currX = pathNodes.get(i).getX();
      int currY = pathNodes.get(i).getY();
      int nextX = pathNodes.get(i + 1).getX();
      int nextY = pathNodes.get(i + 1).getY();
      double angle = 0;
      int diffX = nextX - currX;
      int diffY = nextY - currY;
      if (diffX != 0) {

        angle = Math.atan((diffY) / (diffX));
        if (angle == 0 && diffX < 0) angle = Math.PI;

      } else {

        if (diffY < 0) angle = Math.PI / 2;
        else if (diffY < 0) angle = -Math.PI / 2;
        else angle = currentAngle;
      }

      // Set the first angle to the angle between the first two nodes for comparison
      if (i == 0) {
        currentAngle = angle;
      }
      if (angle - currentAngle > Math.PI / 8) {
        // Turn Left if the starting node is lower and turn right if start is higher
        directions.add(first);
      } else if (angle - currentAngle < -Math.PI / 8) {
        // Turn Right
        directions.add(second);
      } else {
        // Go straight
        directions.add(Direction.UP);
      }
      currentAngle = angle;
    } // for

    // Convert the directions to a string
    for (int j = 0; j < directions.size(); j++) {
      if (directions.get(j) == Direction.RIGHT) {
        textPath += " Turn Right at:" + pathNodes.get(j).getNodeID() + "\n";
      } else if (directions.get(j) == Direction.LEFT) {
        textPath += " Turn Left at:" + pathNodes.get(j).getNodeID() + "\n";
      } else {
        // else if it's straight keep track of how many nodes it is straight for
        String end = "";
        int nextNotStraight = j;
        for (int k = j + 1; k < directions.size(); k++) {
          if (directions.get(k) != Direction.UP) {
            end = pathNodes.get(k).getNodeID();
            nextNotStraight = k;
            break;
          }
        }
        j = nextNotStraight - 1;
        // If end is empty then there were no other turns
        if (end.isEmpty()) {
          textPath += "Continue straight until destination." + "\n";
          break;
        } else {
          textPath += " Go straight until:" + end + "\n";
        }
      }
    }
    return textPath;
  }
}

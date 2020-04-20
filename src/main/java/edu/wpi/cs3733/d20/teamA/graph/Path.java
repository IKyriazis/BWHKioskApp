package edu.wpi.cs3733.d20.teamA.graph;

import com.sun.javafx.scene.traversal.Direction;
import java.util.*;

/** Represents a path along the graph */
public class Path {
  private enum cardinalDirections {
    NORTH,
    SOUTH,
    EAST,
    WEST,
    NORTHEAST,
    SOUTHEAST,
    NORTHWEST,
    SOUTHWEST
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
    /*if (pathNodes.get(0).getY() > pathNodes.get(pathNodes.size() - 1).getY())
      return textualDirectionsFromOrientation(Direction.LEFT, Direction.RIGHT);
    else return textualDirectionsFromOrientation(Direction.RIGHT, Direction.LEFT);*/
    return newTextualDirections();
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

  public String newTextualDirections() {
    String textPath = "";
    ArrayList<Direction> directions = new ArrayList<>();
    cardinalDirections currentHeading = cardinalDirections.NORTH;
    cardinalDirections desiredHeading = cardinalDirections.NORTH;

    // For every node in the path
    for (int i = 0; i < pathNodes.size() - 1; i++) {
      int currX = pathNodes.get(i).getX();
      int currY = pathNodes.get(i).getY();
      int nextX = pathNodes.get(i + 1).getX();
      int nextY = pathNodes.get(i + 1).getY();
      int diffX = nextX - currX;
      int diffY = nextY - currY;

      // if either of the differences are zero it is moving in a true cardinal direction
      if (Math.abs(diffX) == 0 || Math.abs(diffY) == 0) {
        if (diffY < 0) desiredHeading = cardinalDirections.NORTH;
        else if (diffY > 0) desiredHeading = cardinalDirections.SOUTH;
        else if (diffX > 0) desiredHeading = cardinalDirections.EAST;
        else if (diffX < 0) desiredHeading = cardinalDirections.WEST;
      } else { // it is moving in an in-between direction
        // +x  +y
        if (diffX > 0 && diffY > 0) desiredHeading = cardinalDirections.SOUTHEAST;

        // +x  -y
        else if (diffX > 0 && diffY < 0) desiredHeading = cardinalDirections.NORTHEAST;

        // -x  +y
        else if (diffX < 0 && diffY > 0) desiredHeading = cardinalDirections.SOUTHWEST;

        // -x  -y
        else desiredHeading = cardinalDirections.NORTHWEST;
      }

      if (i == 0) currentHeading = desiredHeading;

      if (currentHeading == desiredHeading) directions.add(Direction.UP);
      else {

        switch (currentHeading) {
          case NORTH:
            switch (desiredHeading) {
              case NORTH:
                directions.add(Direction.UP);
                break;
              case NORTHEAST:
              case EAST:
              case SOUTHEAST:
              case SOUTH:
                directions.add(Direction.RIGHT);
                break;
              case SOUTHWEST:
              case WEST:
              case NORTHWEST:
                directions.add(Direction.LEFT);
                break;
              default:
                System.out.println("Hi Yash");
            }
            break;
          case SOUTH:
            switch (desiredHeading) {
              case NORTH:
              case NORTHEAST:
              case EAST:
              case SOUTHEAST:
                directions.add(Direction.LEFT);
                break;
              case SOUTH:
                directions.add(Direction.UP);
                break;
              case SOUTHWEST:
              case WEST:
              case NORTHWEST:
                directions.add(Direction.RIGHT);
                break;
              default:
                System.out.println("Hi Yash");
            }
            break;
          case EAST:
            switch (desiredHeading) {
              case NORTH:
              case NORTHEAST:
              case NORTHWEST:
              case WEST:
                directions.add(Direction.LEFT);
                break;
              case EAST:
                directions.add(Direction.UP);
                break;
              case SOUTHEAST:
              case SOUTH:
              case SOUTHWEST:
                directions.add(Direction.RIGHT);
                break;
              default:
                System.out.println("Hi Yash");
            }
            break;
          case WEST:
            switch (desiredHeading) {
              case NORTH:
              case NORTHEAST:
              case NORTHWEST:
              case EAST:
                directions.add(Direction.RIGHT);
                break;
              case WEST:
                directions.add(Direction.UP);
                break;
              case SOUTHEAST:
              case SOUTH:
              case SOUTHWEST:
                directions.add(Direction.LEFT);
                break;
              default:
                System.out.println("Hi Yash");
            }
            break;
          case NORTHWEST:
            switch (desiredHeading) {
              case NORTH:
              case NORTHEAST:
              case EAST:
              case SOUTHEAST:
                directions.add(Direction.RIGHT);
                break;
              case SOUTH:
              case SOUTHWEST:
              case WEST:
                directions.add(Direction.LEFT);
                break;
              case NORTHWEST:
                directions.add(Direction.UP);
                break;
              default:
                System.out.println("Hi Yash");
            }
            break;
          case NORTHEAST:
            switch (desiredHeading) {
              case NORTHEAST:
                directions.add(Direction.UP);
                break;
              case EAST:
              case SOUTHEAST:
              case SOUTH:
              case SOUTHWEST:
                directions.add(Direction.RIGHT);
                break;
              case NORTH:
              case WEST:
              case NORTHWEST:
                directions.add(Direction.LEFT);
                break;
              default:
                System.out.println("Hi Yash");
            }
            break;
          case SOUTHWEST:
            switch (desiredHeading) {
              case SOUTHWEST:
                directions.add(Direction.UP);
                break;
              case EAST:
              case SOUTHEAST:
              case SOUTH:
              case NORTHEAST:
                directions.add(Direction.LEFT);
                break;
              case NORTH:
              case WEST:
              case NORTHWEST:
                directions.add(Direction.RIGHT);
                break;
              default:
                System.out.println("Hi Yash");
            }
            break;
          case SOUTHEAST:
            switch (desiredHeading) {
              case NORTH:
              case NORTHEAST:
              case EAST:
              case NORTHWEST:
                directions.add(Direction.LEFT);
                break;
              case SOUTH:
              case SOUTHWEST:
              case WEST:
                directions.add(Direction.RIGHT);
                break;
              case SOUTHEAST:
                directions.add(Direction.UP);
                break;
              default:
                System.out.println("Hi Yash");
            }
            break;
        }
      }
      currentHeading = desiredHeading;
    }

    // Formulate the string of directions
    for (int j = 0; j < directions.size(); j++) {
      if (directions.get(j) == Direction.RIGHT) {
        textPath +=
            " Turn Right at:" + pathNodes.get(j).getNodeID() + "\n" + "Continue Straight" + "\n";
      } else if (directions.get(j) == Direction.LEFT) {
        textPath +=
            " Turn Left at:" + pathNodes.get(j).getNodeID() + "\n" + "Continue Straight" + "\n";
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

    textPath += "You have reached " + pathNodes.get(pathNodes.size() - 1).getNodeID();

    return textPath;
  }
}

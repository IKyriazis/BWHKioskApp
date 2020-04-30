package edu.wpi.cs3733.d20.teamA.graph;

import java.util.*;
import javafx.scene.control.Label;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

/** Represents a path along the graph */
public class Path implements IStrategyPath {

  /** Represents a list of nodes along path */
  private ArrayList<Node> pathNodes;

  /** Represents a list of forward & reverse edges along path */
  private ArrayList<Edge> pathEdges;

  /** Represents the graph the path is being calculated for */
  private Graph graph;

  /**
   * Creates a new Path based off what graph we are trying to path find on
   *
   * @param graph Graph to that the path will be found on
   */
  public Path(Graph graph) {
    this.graph = graph;
    pathNodes = new ArrayList<>();
    pathEdges = new ArrayList<>();
  }

  /**
   * Uses the A Star algorithm to locate the optimal path between the given start and end node
   *
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

    calculateEdges();
  }

  /**
   * Gets the list of nodes in the path
   *
   * @return pathNodes
   */
  public ArrayList<Node> getPathNodes() {
    return pathNodes;
  }

  /**
   * Gets the list of edges in the path
   *
   * @return pathEdges
   */
  public ArrayList<Edge> getPathEdges() {
    return pathEdges;
  }

  /** Gets only the forward edges for the path */
  private void calculateEdges() {
    // Clear existing edge list
    pathEdges.clear();

    for (int i = 0; i < (pathNodes.size() - 1); i++) {
      // Locate forward edges
      boolean found = false;
      for (Edge edge : pathNodes.get(i).getEdges().values()) {
        if (edge.getEnd().equals(pathNodes.get(i + 1))) {
          // Forward edge found, add it to the list.
          pathEdges.add(edge);
          found = true;
        }
      }
      if (!found) {
        pathEdges.clear();
        pathNodes.clear();
        return;
      }
    }
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

    return (int) Math.sqrt(xDist + yDist);
  }

  /**
   * Creates textual directions of the path based on the perspective of the person following the
   * path by using the angles associated between the nodes
   *
   * @return textPath The array of Labels that write the path out
   */
  public ArrayList<Label> textualDirections() {
    ArrayList<Label> textPath = new ArrayList<>();
    ArrayList<Direction> directions = new ArrayList<>();
    double lastAngle = -100.0;
    // For every node in the path
    for (int i = 0; i < pathNodes.size() - 1; i++) {
      int floorStart = pathNodes.get(i).getFloor();
      int floorEnd = pathNodes.get(i + 1).getFloor();
      if (floorEnd > floorStart) {
        directions.add(Direction.UP);
        lastAngle = -100.0;
        continue;
      } else if (floorEnd < floorStart) {
        directions.add(Direction.DOWN);
        lastAngle = -100.0;
        continue;
      }

      // Account for nodes between floors
      int currX = pathNodes.get(i).getX();
      int currY = pathNodes.get(i).getY();
      int nextX = pathNodes.get(i + 1).getX();
      int nextY = pathNodes.get(i + 1).getY();
      double diffX = Math.abs(nextX - currX);
      double diffY = Math.abs(nextY - currY);

      double angle = Math.atan(diffY / diffX);

      if (nextX > currX && nextY < currY) { // Quadrant 1
        // angle = angle;
      } else if (nextX < currX && nextY < currY) { // Quadrant 2
        angle = Math.PI - angle;
      } else if (nextX < currX && nextY > currY) { // Quadrant 3
        angle = Math.PI + angle;
      } else if (nextX > currX && nextY > currY) { // Quadrant 4
        angle = (2 * Math.PI) - angle;
      } else if (nextX == currX && nextY < currY) { // +Y
        angle = Math.PI / 2;
      } else if (nextX == currX && nextY > currY) { // -Y
        angle = (3 * Math.PI) / 2;
      } else if (nextX > currX && nextY == currY) { // +X
        angle = 0;
      } else if (nextX < currX && nextY == currY) { // -X
        angle = Math.PI;
      }

      if (lastAngle == -100.0) {
        lastAngle = angle;
      }

      double angleDiff = angle - lastAngle;

      if (((angleDiff <= (-5 * Math.PI / 4)) && (angleDiff >= (-7 * Math.PI / 4)))
          || ((angleDiff >= Math.PI / 4) && (angleDiff <= (3 * Math.PI / 4)))) {
        directions.add(Direction.LEFT);
      } else if ((angleDiff <= (-Math.PI / 4) && (angleDiff >= (-3 * Math.PI / 4)))
          || ((angleDiff >= (5 * Math.PI / 4)) && (angleDiff <= (7 * Math.PI / 4)))) {
        directions.add(Direction.RIGHT);
      } else {
        directions.add(Direction.NEXT);
      }

      lastAngle = angle;
    }

    // Formulate the string of directions
    for (int j = 0; j < directions.size(); j++) {
      if (directions.get(j) == Direction.RIGHT) {
        textPath.add(
            new Label(
                "Turn right at " + pathNodes.get(j).getLongName(),
                new FontIcon(FontAwesomeSolid.ARROW_CIRCLE_RIGHT)));
      } else if (directions.get(j) == Direction.LEFT) {
        textPath.add(
            new Label(
                "Turn left at " + pathNodes.get(j).getLongName(),
                new FontIcon(FontAwesomeSolid.ARROW_CIRCLE_LEFT)));
      } else if (directions.get(j) == Direction.UP) {
        int sameLength = getSameLength(directions, j, Direction.UP);

        if (sameLength >= 1) {
          j += sameLength - 1;
          textPath.add(
              new Label(
                  "Go up " + sameLength + " floors",
                  new FontIcon(FontAwesomeSolid.ARROW_CIRCLE_UP)));
        } else {
          textPath.add(
              new Label("Go up until destination", new FontIcon(FontAwesomeSolid.ARROW_CIRCLE_UP)));
          break;
        }
      } else if (directions.get(j) == Direction.DOWN) {
        int sameLength = getSameLength(directions, j, Direction.DOWN);

        if (sameLength >= 1) {
          j += sameLength - 1;
          textPath.add(
              new Label(
                  "Go down " + sameLength + " floors",
                  new FontIcon(FontAwesomeSolid.ARROW_ALT_CIRCLE_DOWN)));
        } else {
          textPath.add(
              new Label(
                  "Go up until destination", new FontIcon(FontAwesomeSolid.ARROW_ALT_CIRCLE_DOWN)));
          break;
        }

      } else {
        // else if it's straight keep track of how many nodes it is straight for
        int sameLength = getSameLength(directions, j, Direction.NEXT);

        if (sameLength >= 1) {
          j += sameLength - 1;
          textPath.add(
              new Label(
                  "Go straight until " + pathNodes.get(j + 1).getLongName(),
                  new FontIcon(FontAwesomeSolid.ARROW_ALT_CIRCLE_UP)));
        } else {
          textPath.add(
              new Label(
                  "Continue straight until destination",
                  new FontIcon(FontAwesomeSolid.ARROW_ALT_CIRCLE_UP)));
          break;
        }
      }
    }

    textPath.add(
        new Label(
            "You have reached " + pathNodes.get(pathNodes.size() - 1).getLongName(),
            new FontIcon(FontAwesomeSolid.DOT_CIRCLE)));

    return textPath;
  }

  private int getSameLength(List<Direction> directions, int index, Direction direction) {
    for (int i = index; i < directions.size(); i++) {
      if (directions.get(i) != direction) {
        return i - index;
      }
    }
    return -1;
  }

  public void update() {
    ArrayList<Node> newNodes = new ArrayList<>();
    pathNodes.forEach(
        node -> {
          Node newNode = Graph.getInstance().getNodeByID(node.getNodeID());
          if (newNode == null) {
            pathNodes.clear();
            pathEdges.clear();
          } else {
            newNodes.add(newNode);
          }
        });

    pathNodes.clear();
    pathNodes.addAll(newNodes);
    calculateEdges();
  }
}

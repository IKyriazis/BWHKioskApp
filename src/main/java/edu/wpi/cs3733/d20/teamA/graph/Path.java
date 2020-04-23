package edu.wpi.cs3733.d20.teamA.graph;

import com.sun.javafx.scene.traversal.Direction;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.util.*;
import javafx.scene.control.Label;

/** Represents a path along the graph */
public class Path {

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

  public ArrayList<Label> textualDirections() {
    ArrayList<Label> textPath = new ArrayList<>();
    ArrayList<Direction> directions = new ArrayList<>();
    double lastAngle = 0.0;
    // For every node in the path
    for (int i = 0; i < pathNodes.size() - 1; i++) {
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

      if (i == 0) {
        lastAngle = angle;
      }

      double angleDiff = angle - lastAngle;

      if ((angleDiff <= (-3 * Math.PI / 2))
          || (angleDiff >= Math.PI / 4) && (angleDiff <= (3 * Math.PI / 4))) {
        directions.add(Direction.LEFT);
      } else if (angleDiff <= (-Math.PI / 4) || (angleDiff >= (3 * Math.PI / 2))) {
        directions.add(Direction.RIGHT);
      } else {
        directions.add(Direction.UP);
      }

      lastAngle = angle;
    }

    // Formulate the string of directions
    for (int j = 0; j < directions.size(); j++) {
      if (directions.get(j) == Direction.RIGHT) {
        textPath.add(
            new Label(
                "Turn right at " + pathNodes.get(j).getLongName(),
                new FontAwesomeIconView(FontAwesomeIcon.ARROW_CIRCLE_RIGHT)));
      } else if (directions.get(j) == Direction.LEFT) {
        textPath.add(
            new Label(
                "Turn left at " + pathNodes.get(j).getLongName(),
                new FontAwesomeIconView(FontAwesomeIcon.ARROW_CIRCLE_LEFT)));
      } else {
        // else if it's straight keep track of how many nodes it is straight for
        String end = "";
        int nextNotStraight = j;
        for (int k = j + 1; k < directions.size(); k++) {
          if (directions.get(k) != Direction.UP) {
            end = pathNodes.get(k).getLongName();
            nextNotStraight = k;
            break;
          }
        }
        j = nextNotStraight - 1;
        // If end is empty then there were no other turns
        if (end.isEmpty()) {
          textPath.add(
              new Label(
                  "Continue straight until destination",
                  new FontAwesomeIconView(FontAwesomeIcon.ARROW_CIRCLE_UP)));
          break;
        } else {
          textPath.add(
              new Label(
                  "Go straight until " + end,
                  new FontAwesomeIconView(FontAwesomeIcon.ARROW_CIRCLE_UP)));
        }
      }
    }

    textPath.add(
        new Label(
            "You have reached " + pathNodes.get(pathNodes.size() - 1).getLongName(),
            new FontAwesomeIconView(FontAwesomeIcon.DOT_CIRCLE_ALT)));

    return textPath;
  }
}

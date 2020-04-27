package edu.wpi.cs3733.d20.teamA.graph;

import com.sun.javafx.scene.traversal.Direction;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.util.*;
import javafx.scene.control.Label;

public class BreadthFirst implements IStrategyPath {
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
  public BreadthFirst(Graph graph) {
    this.graph = graph;
    pathNodes = new ArrayList<>();
    pathEdges = new ArrayList<>();
  }

  @Override
  public void findPath(Node start, Node end) {
    Map<Node, Node> path = new HashMap<>();
    ArrayList<Node> visited = new ArrayList<>();
    Queue<Node> queue = new LinkedList<>();

    // Add start node ot the queue
    queue.add(start);
    visited.add(start);

    while (queue.size() != 0) {
      Node current = queue.poll();
      // visited.add(current);
      // Early exit
      if (current.equals(end) || queue.contains(end)) break;

      // Check the current nodes neighbors
      for (Edge edge : current.getEdges().values()) {
        Node neighbor = edge.getEnd();

        // Only consider nodes on this floor for now.
        if (neighbor.getFloor() != start.getFloor()) {
          continue;
        }

        // if the neighbor hasn't been visited then mark it as visited and enqueue it
        // add it to the path with the neighbor and where it came from
        if (!visited.contains(neighbor)) {
          visited.add(neighbor);
          queue.add(neighbor);
          path.put(neighbor, current);
        }
      }
    }
    // If the path doesn't exist return null
    if (!path.containsKey(end)) return;

    // clear the path nodes in case there was something in it alread
    pathNodes.clear();

    Node curr = end;
    while (!curr.equals(start)) {
      // add the current node to the path
      pathNodes.add(curr);

      // set the current node to the node that we came from
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

  public void setPathNodes(ArrayList<Node> pathNodes) {
    this.pathNodes = pathNodes;
  }

  public ArrayList<Edge> getPathEdges() {
    return pathEdges;
  }

  public void setPathEdges(ArrayList<Edge> pathEdges) {
    this.pathEdges = pathEdges;
  }

  public Graph getGraph() {
    return graph;
  }

  public void setGraph(Graph graph) {
    this.graph = graph;
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

  /** Gets only the forward edges for the path */
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
}

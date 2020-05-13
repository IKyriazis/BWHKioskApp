package edu.wpi.cs3733.d20.teamA.graph;

import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;

public abstract class PathAlgo implements IStrategyPath {

  /** Represents a list of nodes along path */
  protected ArrayList<Node> pathNodes;

  /** Represents a list of forward & reverse edges along path */
  protected ArrayList<Edge> pathEdges;

  /** Represents the graph the path is being calculated for */
  protected Graph graph;

  protected ArrayList<Pair<Node, String>> writtenDirections;

  public abstract void findPath(Node start, Node end);

  public final void pathFind(Node start, Node end) {
    findPath(start, end);

    calculateEdges();

    writtenDirections = textualDirections();
  }

  public PathAlgo(Graph graph) {
    this.graph = graph;
    this.pathNodes = new ArrayList<>();
    this.pathEdges = new ArrayList<>();
    this.writtenDirections = new ArrayList<>();
  }

  public ArrayList<Node> getPathNodes() {
    return this.pathNodes;
  }

  public ArrayList<Edge> getPathEdges() {
    return this.pathEdges;
  }

  public Graph getGraph() {
    return graph;
  }

  public void setGraph(Graph graph) {
    this.graph = graph;
  }

  /** Gets only the forward edges for the path */
  protected void calculateEdges() {
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
   * Converts the distance from pixels to feet
   *
   * @param distance
   * @return the distance in feet
   */
  private double pixelsToFeet(double distance) {
    if (graph.getCampus() == Campus.FAULKNER)
      return distance * 0.238884; // Faulkner Scaling feet per pixel

    return distance * 0.366174; // Main Campus Scaling feet per pixel
  }

  /**
   * Creates textual directions of the path based on the perspective of the person following the
   * path by using the angles associated between the nodes
   *
   * @return textPath The array of Labels that write the path out
   */
  public ArrayList<Pair<Node, String>> textualDirections() {
    ArrayList<Pair<Node, String>> textPath = new ArrayList<>();
    ArrayList<Direction> directions = new ArrayList<>();
    ArrayList<Double> distances = new ArrayList<>();
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
      double straightDistance =
          Math.sqrt((Math.pow(diffX, 2)) + (Math.pow(diffY, 2))); // distance in pixels

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

      // Left
      if (((angleDiff >= (Math.PI / 3)) && (angleDiff <= (2 * Math.PI / 3)))
          || ((angleDiff <= (-4 * Math.PI / 3)) && (angleDiff >= (-5 * Math.PI / 3)))) {
        directions.add(Direction.LEFT);
        directions.add(Direction.NEXT);
      }

      // Right
      else if (((angleDiff <= (-Math.PI / 3)) && (angleDiff >= (-2 * Math.PI / 3)))
          || ((angleDiff >= (4 * Math.PI / 3)) && (angleDiff <= (5 * Math.PI / 3)))) {
        directions.add(Direction.RIGHT);
        directions.add(Direction.NEXT);
      }

      // Slight Left
      else if (((angleDiff >= (Math.PI / 6)) && (angleDiff <= (Math.PI / 3)))
          || ((angleDiff >= (-11 * Math.PI / 6)) && (angleDiff <= (-5 * Math.PI / 3)))
          || ((angleDiff >= (2 * Math.PI / 3)) && (angleDiff <= (5 * Math.PI / 6)))
          || ((angleDiff >= (-4 * Math.PI / 3)) && (angleDiff <= (-7 * Math.PI / 6)))) {

        directions.add(Direction.SLIGHTLEFT);
        directions.add(Direction.NEXT);
      }

      // Slight Right
      else if (((angleDiff <= (-Math.PI / 6)) && (angleDiff >= (-Math.PI / 3)))
          || ((angleDiff <= (11 * Math.PI / 6)) && (angleDiff >= (5 * Math.PI / 3)))
          || ((angleDiff <= (-2 * Math.PI / 3)) && (angleDiff >= (-5 * Math.PI / 6)))
          || ((angleDiff <= (4 * Math.PI / 3)) && (angleDiff >= (7 * Math.PI / 6)))) {

        directions.add(Direction.SLIGHTRIGHT);
        directions.add(Direction.NEXT);
      } else {
        directions.add(Direction.NEXT);
      }

      /*
      if (((angleDiff <= (-5 * Math.PI / 4)) && (angleDiff >= (-7 * Math.PI / 4)))
          || ((angleDiff >= Math.PI / 4) && (angleDiff <= (3 * Math.PI / 4)))) {
        directions.add(Direction.LEFT);
      } else if ((angleDiff <= (-Math.PI / 4) && (angleDiff >= (-3 * Math.PI / 4)))
          || ((angleDiff >= (5 * Math.PI / 4)) && (angleDiff <= (7 * Math.PI / 4)))) {
        directions.add(Direction.RIGHT);
      } else {
        directions.add(Direction.NEXT);
      } */
      distances.add(pixelsToFeet(straightDistance));
      lastAngle = angle;
    }

    // Formulate the string of directions
    for (int k = 0, j = 0; j < directions.size(); j++, k++) {
      if (directions.get(j) == Direction.RIGHT) {
        if (pathNodes.get(k).getType() != NodeType.HALL) {
          textPath.add(
              new Pair<>(pathNodes.get(k), "Turn right at " + pathNodes.get(k).getLongName()));
        } else {
          textPath.add(new Pair<>(pathNodes.get(k), "Turn right"));
        }
        k--;

      } else if (directions.get(j) == Direction.LEFT) {
        if (pathNodes.get(k).getType() != NodeType.HALL) {
          textPath.add(
              new Pair<>(pathNodes.get(k), "Turn left at " + pathNodes.get(k).getLongName()));
        } else {
          textPath.add(new Pair<>(pathNodes.get(k), "Turn left "));
        }
        k--;
      } else if (directions.get(j) == Direction.SLIGHTLEFT) {
        if (pathNodes.get(k).getType() != NodeType.HALL) {
          textPath.add(
              new Pair<>(
                  pathNodes.get(k), "Make a slight left at " + pathNodes.get(k).getLongName()));
        } else {
          textPath.add(new Pair<>(pathNodes.get(k), "Make a slight left"));
        }
        k--;
      } else if (directions.get(j) == Direction.SLIGHTRIGHT) {
        if (pathNodes.get(k).getType() != NodeType.HALL) {
          textPath.add(
              new Pair<>(
                  pathNodes.get(k), "Make a slight right at " + pathNodes.get(k).getLongName()));
        } else {
          textPath.add(new Pair<>(pathNodes.get(k), "Make a slight right"));
        }
        k--;
      } else if (directions.get(j) == Direction.UP) {
        int sameLength = getSameLength(directions, j, Direction.UP);

        if (sameLength >= 1) {
          k += sameLength - 1;
          j += sameLength - 1;
          textPath.add(new Pair<>(pathNodes.get(k), "Go up " + sameLength + " floors"));
        } else {
          textPath.add(new Pair<>(pathNodes.get(k), "Go up until destination"));
          break;
        }
      } else if (directions.get(j) == Direction.DOWN) {
        int sameLength = getSameLength(directions, j, Direction.DOWN);

        if (sameLength >= 1) {
          k += sameLength - 1;
          j += sameLength - 1;
          textPath.add(new Pair<>(pathNodes.get(k), "Go down " + sameLength + " floors"));
        } else {
          textPath.add(new Pair<>(pathNodes.get(k), "Go up until destination"));
          break;
        }

      } else {
        // else if it's straight keep track of how many nodes it is straight for
        int sameLength = getSameLength(directions, j, Direction.NEXT);
        double startX = pathNodes.get(k).getX();
        double startY = pathNodes.get(k).getY();

        if (sameLength >= 1) {
          j += sameLength - 1;
          k += sameLength - 1;
          double endX = pathNodes.get(k + 1).getX();
          double endY = pathNodes.get(k + 1).getY();
          double feet =
              pixelsToFeet(
                  (Math.sqrt((Math.pow(startX - endX, 2)) + (Math.pow(startY - endY, 2)))));
          if (pathNodes.get(k + 1).getType() != NodeType.HALL) {
            textPath.add(
                new Pair<>(
                    pathNodes.get(k),
                    "Go straight for "
                        + (int) feet
                        + " feet until "
                        + pathNodes.get(k + 1).getLongName()));
          } else {
            textPath.add(new Pair<>(pathNodes.get(k), "Go straight for " + (int) feet + " feet"));
          }
        } else {
          textPath.add(new Pair<>(pathNodes.get(k), "Continue straight until destination"));
          break;
        }
      }
    }

    textPath.add(
        new Pair<>(
            pathNodes.get(pathNodes.size() - 1),
            "You have reached " + pathNodes.get(pathNodes.size() - 1).getLongName()));

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
          Node newNode = Graph.getInstance(Campus.FAULKNER).getNodeByID(node.getNodeID());
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

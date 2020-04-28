package edu.wpi.cs3733.d20.teamA.graph;

import java.util.*;

public class DepthFirst implements IStrategyPath {
  /** Represents a list of nodes along path */
  private ArrayList<Node> pathNodes;

  /** Represents a list of forward & reverse edges along path */
  private ArrayList<Edge> pathEdges;

  /** Represents the graph the path is being calculated for */
  private Graph graph;

  public DepthFirst(Graph graph) {
    this.graph = graph;
    pathNodes = new ArrayList<>();
    pathEdges = new ArrayList<>();
  }

  @Override
  public void findPath(Node start, Node end) {
    Map<Node, Node> path = new HashMap<>();
    ArrayList<Node> visited = new ArrayList<>();
    Stack<Node> stack = new Stack<Node>();

    stack.push(start);
    visited.add(start);

    while (!stack.isEmpty()) {
      Node current = stack.pop();

      if (current.equals(end) || stack.contains(end)) break;

      for (Edge edge : current.getEdges().values()) {
        Node neighbor = edge.getEnd();

        if (!visited.contains(neighbor)) {
          visited.add(neighbor);
          stack.push(neighbor);
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
}

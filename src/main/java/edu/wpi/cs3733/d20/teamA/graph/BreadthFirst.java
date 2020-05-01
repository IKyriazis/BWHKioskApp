package edu.wpi.cs3733.d20.teamA.graph;

import java.util.*;

public class BreadthFirst extends PathAlgo implements IStrategyPath {
  /**
   * Creates a new Path based off what graph we are trying to path find on
   *
   * @param graph Graph to that the path will be found on
   */
  public BreadthFirst(Graph graph) {
    super(graph);
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

      // Early exit
      if (current.equals(end) || queue.contains(end)) break;

      // Check the current nodes neighbors
      for (Edge edge : current.getEdges().values()) {
        Node neighbor = edge.getEnd();

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
}

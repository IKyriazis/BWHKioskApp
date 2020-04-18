package edu.wpi.cs3733.d20.teamA.graph;

/** Represents a directed edge (two makes an undirected edge!) */
public class Edge {
  /** Origin of edge */
  private final Node start;
  /** Destination of edge */
  private final Node end;
  /** ID of edge (used for storage) */
  private final String edgeID;
  /** Distance-based weight of the edge */
  private final int weight;

  /**
   * Create an edge
   *
   * @param start Origin node
   * @param end Destination node
   * @param weight Weight of edge
   */
  public Edge(Node start, Node end, int weight) {
    this.start = start;
    this.end = end;
    this.weight = weight;

    this.edgeID = start.getNodeID() + "_" + end.getNodeID();
  }

  /**
   * Get the starting location of the edge
   *
   * @return Origin node
   */
  public Node getStart() {
    return start;
  }

  /**
   * Get the ending location of the edge
   *
   * @return Destination node
   */
  public Node getEnd() {
    return end;
  }

  /**
   * Get the ID of the edge
   *
   * @return ID
   */
  public String getEdgeID() {
    return edgeID;
  }

  /**
   * Get the weight of the edge
   *
   * @return Weight
   */
  public int getWeight() {
    return weight;
  }

  /**
   * Get the edge going from this edge's end to this edge's start
   *
   * @return Reverse Edge
   */
  public Edge getReverseEdge() {
    return end.getEdgeToNode(start);
  }
}

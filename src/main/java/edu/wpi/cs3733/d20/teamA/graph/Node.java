package edu.wpi.cs3733.d20.teamA.graph;

import java.util.HashMap;

/** Represents a mostly immutable node on the graph */
public class Node implements Comparable<Node> {
  /** Map of edges in this node, mapping End Node to Edge */
  private final HashMap<Node, Edge> edges;

  /** ID of this node */
  private final String nodeID;
  /** Name of building containing this node */
  private final String building;
  /** Long name of this node */
  private final String longName;
  /** Short name of this node */
  private final String shortName;
  /** Team that created this node */
  private final String teamAssigned;
  /** X coordinate of this node */
  private final int x;
  /** Y coordinate of this node */
  private final int y;
  /** Floor that this node is on */
  private final int floor;
  /** Type of this node */
  private final NodeType type;

  /** A* cost of this node, used internally for pathfinding */
  private int cost;

  /**
   * Create a new node, which is effectively immutable (Nothing can be changed external of the graph
   * package)
   *
   * @param nodeID ID of node
   * @param x X coordinate
   * @param y Y coordinate
   * @param floor Floor that this node is on
   * @param building Building containing this node
   * @param type Type of node
   * @param longName Long name of node
   * @param shortName Short name of Node
   * @param teamAssigned Team that created this node
   */
  public Node(
      String nodeID,
      int x,
      int y,
      int floor,
      String building,
      NodeType type,
      String longName,
      String shortName,
      String teamAssigned) {
    this.nodeID = nodeID;
    this.x = x;
    this.y = y;
    this.floor = floor;
    this.building = building;
    this.type = type;
    this.longName = longName;
    this.shortName = shortName;
    this.teamAssigned = teamAssigned;

    this.edges = new HashMap<>();
    this.cost = Integer.MAX_VALUE;
  }

  /**
   * Get this node's edge map
   *
   * @return Edges
   */
  public HashMap<Node, Edge> getEdges() {
    return edges;
  }

  /**
   * Add an edge to this node
   *
   * @param edge Edge to add
   */
  protected void addEdge(Edge edge) {
    edges.put(edge.getEnd(), edge);
  }

  /**
   * Delete an edge from this node
   *
   * @param edge Edge to delete
   * @return Success / Failure
   */
  protected boolean deleteEdge(Edge edge) {
    if (edge == null) return false;

    return edges.remove(edge.getEnd(), edge);
  }

  /**
   * Get the edge connecting this node to a given other node.
   *
   * @param node Destination node
   * @return Edge from this to given node
   */
  protected Edge getEdgeToNode(Node node) {
    return edges.get(node);
  }

  /**
   * Check whether this node has an edge connecting it and another given node
   *
   * @param node Destination node
   * @return Edge Exists / Doesn't
   */
  protected boolean hasEdgeTo(Node node) {
    return edges.containsKey(node);
  }

  /**
   * Get the ID of this node
   *
   * @return Node ID
   */
  public String getNodeID() {
    return nodeID;
  }

  /**
   * Get the building this node is in
   *
   * @return Building
   */
  public String getBuilding() {
    return building;
  }

  /**
   * Get the long name of this node
   *
   * @return Long name
   */
  public String getLongName() {
    return longName;
  }

  /**
   * Get the short name of this node
   *
   * @return Short name
   */
  public String getShortName() {
    return shortName;
  }

  /**
   * Get the team that created this node
   *
   * @return Team
   */
  public String getTeamAssigned() {
    return teamAssigned;
  }

  /**
   * Get the x coordinate of this node
   *
   * @return Node x coordinate
   */
  public int getX() {
    return x;
  }

  /**
   * Get the y coordinate of this node
   *
   * @return Node y coordinate
   */
  public int getY() {
    return y;
  }

  /**
   * Get the floor that this node is on
   *
   * @return Floor
   */
  public int getFloor() {
    return floor;
  }

  /**
   * Get the type of this node
   *
   * @return Node type
   */
  public NodeType getType() {
    return type;
  }

  /**
   * Get the cost to reach this node, only used for path finding
   *
   * @return Cost
   */
  protected int getCost() {
    return cost;
  }

  /**
   * Set the cost to reach this node, only used for path finding
   *
   * @param cost Cost
   */
  protected void setCost(int cost) {
    this.cost = cost;
  }

  /**
   * Check equality between this node and another based off of node ID
   *
   * @param obj Other node
   * @return Equal / Non-Equal
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Node) {
      return nodeID.equals(((Node) obj).getNodeID());
    }

    return false;
  }

  /**
   * Compar nodes based on cost
   *
   * @param o Other node
   * @return -1 = Lesser, 0 = Equal, 1 = Greater
   */
  @Override
  public int compareTo(Node o) {
    return Integer.compare(cost, o.getCost());
  }

  /**
   * Convert this node to a string based off of node iD
   *
   * @return Node ID
   */
  @Override
  public String toString() {
    return nodeID;
  }
}

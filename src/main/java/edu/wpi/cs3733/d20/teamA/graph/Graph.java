package edu.wpi.cs3733.d20.teamA.graph;

import edu.wpi.cs3733.d20.teamA.database.GraphDatabase;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/** Represents locations on the map in an 'undirected' Graph */
public class Graph {

  GraphDatabase DB = new GraphDatabase();
  /** The nodes in this graph, mapping ID to Node */
  private HashMap<String, Node> nodes;
  // private GraphDatabase database;

  /** Singleton graph instance */
  private static Graph instance;

  /** Count of bidirectional edges */
  private int edgeCount = 0;

  /** Create a new empty graph, private b/c this is a singleton */
  private Graph() throws SQLException {
    nodes = new HashMap<>();
    DB.makeDatabase();
    DB.dropTables();
    DB.createTables();
  }

  /**
   * Get singleton instance of graph
   *
   * @return instance
   */
  public static Graph getInstance() throws SQLException {
    return (instance == null) ? (instance = new Graph()) : instance;
  }

  /**
   * Get the number of nodes in the graph
   *
   * @return Node count
   */
  public int getNodeCount() {
    return nodes.size();
  }

  /**
   * Get the number of bidirectional edges in the graph
   *
   * @return Edge count
   */
  public int getEdgeCount() {
    return edgeCount;
  }

  /**
   * Add a new node to the graph
   *
   * @param node Node to add to graph
   * @return Success / Failure
   */
  public boolean addNode(Node node) throws SQLException {
    if ((node == null) || (nodes.containsKey(node.getNodeID()))) {
      // Skip if node doesn't exist or already is in the graph.
      return false;
    }

    nodes.put(node.getNodeID(), node);

    DB.addNode(
        node.getNodeID(),
        node.getX(),
        node.getY(),
        node.getFloor(),
        node.getBuilding(),
        node.getStringType(),
        node.getLongName(),
        node.getShortName(),
        node.getTeamAssigned());

    return true;
  }

  /**
   * Add a new edge (in both directions) to the graph
   *
   * @param start First node of edge
   * @param end Second node of edge
   * @param weight Weight of edge
   * @return Success / Failure
   */
  public boolean addEdge(Node start, Node end, int weight) throws SQLException {
    // Skip if either node doesn't exist
    if (start == null || end == null) return false;

    // Skip if either node isn't in graph
    if (!nodes.containsKey(start.getNodeID()) || !nodes.containsKey(end.getNodeID())) return false;

    // Skip if either node has an existing edge to the other
    if (start.hasEdgeTo(end) || end.hasEdgeTo(start)) return false;

    // Create and add edges
    Edge forward = new Edge(start, end, weight);
    start.addEdge(forward);

    Edge reverse = new Edge(end, start, weight);
    end.addEdge(reverse);

    // Update edge count
    edgeCount++;

    DB.addEdge(start.getNodeID() + "_" + end.getNodeID(), start.getNodeID(), end.getNodeID());
    DB.addEdge(end.getNodeID() + "_" + start.getNodeID(), end.getNodeID(), start.getNodeID());
    return true;
  }

  /**
   * Deletes a node from the graph
   *
   * @param node Node to delete
   * @return Success / Failure
   */
  public boolean deleteNode(Node node) throws SQLException {
    // Skip if node is null or is already in graph
    if ((node == null) || (!nodes.containsKey(node.getNodeID()))) return false;

    // Delete all edges involving this node
    ArrayList<Edge> toDelete = new ArrayList<>(node.getEdges().values());
    toDelete.forEach(this::deleteEdge);

    // Delete node
    nodes.remove(node.getNodeID());

    DB.deleteNode(node.getNodeID());

    return true;
  }

  /**
   * Deletes a node from the graph by ID
   *
   * @param nodeID ID of node to delete
   * @return Success / Failure
   */
  public boolean deleteNode(String nodeID) throws SQLException {
    return deleteNode(nodes.get(nodeID));
  }

  /**
   * Deletes an edge (in both directions) from the graph by starting and ending nodes
   *
   * @param start First node of edge
   * @param end Second node of edge
   * @return Success / Failure
   */
  public boolean deleteEdge(Node start, Node end) {
    // Skip if either node doesn't exist.
    if ((start == null) || (end == null)) return false;

    // Skip if either node isn't in graph
    if (!nodes.containsKey(start.getNodeID()) || !nodes.containsKey(end.getNodeID())) return false;

    // Get edges
    Edge forward = start.getEdgeToNode(end);
    if (forward == null) return false;

    Edge reverse = forward.getReverseEdge();
    if (reverse == null) return false;
    try {
      DB.deleteEdge(start.getNodeID() + "_" + end.getNodeID());
      DB.deleteEdge(end.getNodeID() + "_" + start.getNodeID());
    } catch (SQLException e) {

    }
    // Update edge count
    edgeCount--;

    return (start.deleteEdge(forward) && end.deleteEdge(reverse));
  }

  /**
   * Delete an edge (and its reverse) from the graph by reference
   *
   * @param edge Edge to delete
   * @return Success / Failure
   */
  public boolean deleteEdge(Edge edge) {
    return deleteEdge(edge.getStart(), edge.getEnd());
  }

  /**
   * Gets a node by its node ID
   *
   * @param nodeID ID of node
   * @return Node with given ID
   */
  public Node getNodeByID(String nodeID) {
    return nodes.get(nodeID);
  }

  /**
   * Checks whether a node with the given ID exists
   *
   * @param nodeID ID to check
   * @return Exists / Doesn't Exist
   */
  public boolean hasNode(String nodeID) {
    return nodes.containsKey(nodeID);
  }

  /**
   * Gets all the nodes on a given floor
   *
   * @param floor Floor to filter for nodes on
   * @return List of nodes on floor
   */
  public List<Node> getNodesOnFloor(int floor) {
    // Get all nodes on the given floor
    return nodes.values().stream()
        .filter(node -> (node.getFloor() == floor))
        .collect(Collectors.toList());
  }

  /**
   * Updates the graph with any new data in the database
   *
   * @return Success / Failure
   */
  public boolean update() {
    // TODO; Pull any new data in from database

    return true;
  }

  /** Clear all the nodes in this graph */
  public void clearGraph() {
    // Delete every node's edges
    nodes.values().forEach(node -> node.getEdges().clear());

    // Delete nodes
    nodes.clear();

    // Reset edge count
    edgeCount = 0;
  }

  public GraphDatabase getDB() {
    return DB;
  }
}

package edu.wpi.cs3733.d20.teamA.graph;

import edu.wpi.cs3733.d20.teamA.database.DatabaseServiceProvider;
import edu.wpi.cs3733.d20.teamA.database.graph.GraphDatabase;
import edu.wpi.cs3733.d20.teamA.util.CSVLoader;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/** Represents locations on the map in an 'undirected' Graph */
public class Graph {
  DatabaseServiceProvider provider = new DatabaseServiceProvider();
  Connection conn = provider.provideConnection();

  GraphDatabase DB = new GraphDatabase(conn);
  /** The nodes in this graph, mapping ID to Node */
  private HashMap<String, Node> nodes;

  /** Singleton graph instance */
  private static HashMap<Campus, Graph> campusGraphs = new HashMap<>();

  /** Combined observable list of all campuses */
  private static ObservableList<Node> allCampusObservableList = FXCollections.observableArrayList();

  /** Combined observable list of non-navigation only nodes */
  private static ObservableList<Node> allValidDestinationList = FXCollections.observableArrayList();

  static {
    allCampusObservableList.addListener(
        (ListChangeListener<Node>)
            c -> {
              while (c.next()) {
                if (c.wasAdded()) {
                  allValidDestinationList.addAll(
                      c.getAddedSubList().stream()
                          .filter(node -> node.getType() != NodeType.HALL)
                          .collect(Collectors.toList()));
                }

                if (c.wasRemoved()) {
                  allValidDestinationList.removeAll(c.getRemoved());
                }
              }

              allValidDestinationList.sort(Comparator.comparing(o -> o.toString().toLowerCase()));
            });
  }

  /** Combined observable list of non-navigation only nodes */
  private static ObservableList<Node> roomLocationList = FXCollections.observableArrayList();

  static {
    allCampusObservableList.addListener(
        (ListChangeListener<Node>)
            c -> {
              while (c.next()) {
                if (c.wasAdded()) {
                  roomLocationList.addAll(
                      c.getAddedSubList().stream()
                          .filter(
                              node ->
                                  ((node.getType() != NodeType.HALL)
                                      && (node.getType() != NodeType.ELEV)
                                      && (node.getType() != NodeType.STAI)
                                      && (node.getType() != NodeType.EXIT)
                                      && (node.getType() != NodeType.REST)))
                          .collect(Collectors.toList()));
                }

                if (c.wasRemoved()) {
                  roomLocationList.removeAll(c.getRemoved());
                }
              }

              roomLocationList.sort(Comparator.comparing(o -> o.toString().toLowerCase()));
            });
  }

  /** Count of bidirectional edges */
  private int edgeCount = 0;

  /** Observable list of nodes, used for UI stuff */
  private ObservableList<Node> nodeObservableList;

  /** Campus that this graph is for */
  private Campus campus;

  /** Create a new empty graph, private b/c this is a singleton */
  private Graph(Campus campus) {
    nodes = new HashMap<>();
    this.campus = campus;
    // Create observable list of nodes and keep it sorted
    nodeObservableList = FXCollections.observableArrayList();

    if (DB.getSizeNode(campus) == -1 || DB.getSizeEdge(campus) == -1) {
      DB.dropTables();
      DB.createTables();
      CSVLoader.readNodes(this);
      CSVLoader.readEdges(this);
    } else if (DB.getSizeNode(campus) == 0 || DB.getSizeEdge(campus) == 0) {
      DB.removeAll(campus);
      CSVLoader.readNodes(this);
      CSVLoader.readEdges(this);
    } else {
      update();
    }
  }

  /**
   * Get singleton instance of graph
   *
   * @return instance
   */
  public static Graph getInstance(Campus campus) {
    if (!campusGraphs.containsKey(campus)) {
      campusGraphs.put(campus, new Graph(campus));
    }

    return campusGraphs.get(campus);
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
   * Get the nodes in the graph
   *
   * @return Node map
   */
  public HashMap<String, Node> getNodes() {
    return nodes;
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
  public boolean addNode(Node node) {
    if ((node == null) || (nodes.containsKey(node.getNodeID()))) {
      // Skip if node doesn't exist or already is in the graph.
      return false;
    }

    nodes.put(node.getNodeID(), node);
    nodeObservableList.add(node);
    allCampusObservableList.add(node);
    nodeObservableList.sort(Comparator.comparing(o -> o.toString().toLowerCase()));
    allCampusObservableList.sort(Comparator.comparing(o -> o.toString().toLowerCase()));

    DB.addNode(
        node.getNodeID(),
        node.getX(),
        node.getY(),
        node.getFloor(),
        node.getBuilding(),
        node.getStringType(),
        node.getLongName(),
        node.getShortName(),
        node.getTeamAssigned(),
        campus);

    return true;
  }

  /**
   * Changes a node's x/y coordinates on the graph
   *
   * @param x X coordinate
   * @param y Y coordinate
   * @return Success / Failure
   */
  public boolean moveNode(Node node, int x, int y) {
    if ((node == null) || !(nodes.containsKey(node.getNodeID()))) {
      return false;
    }

    boolean success =
        DB.editNode(
            node.getNodeID(),
            x,
            y,
            node.getFloor(),
            node.getBuilding(),
            node.getStringType(),
            node.getLongName(),
            node.getShortName(),
            node.getTeamAssigned(),
            campus);
    update();

    return success;
  }

  /**
   * Add a new edge (in both directions) to the graph and automatically calculate weight
   *
   * @param start First node of the edge
   * @param end Second node of the edge
   * @return Success / Failure
   */
  public boolean addEdge(Node start, Node end) {
    int weight = calcWeight(start, end);
    return addEdge(start, end, weight);
  }

  /**
   * Add a new edge (in both directions) to the graph
   *
   * @param start First node of edge
   * @param end Second node of edge
   * @param weight Weight of edge
   * @return Success / Failure
   */
  public boolean addEdge(Node start, Node end, int weight) {
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

    DB.addEdge(
        start.getNodeID() + "_" + end.getNodeID(), start.getNodeID(), end.getNodeID(), campus);
    DB.addEdge(
        end.getNodeID() + "_" + start.getNodeID(), end.getNodeID(), start.getNodeID(), campus);
    return true;
  }

  /**
   * Deletes a node from the graph
   *
   * @param node Node to delete
   * @return Success / Failure
   */
  public boolean deleteNode(Node node) {
    // Skip if node is null or is already in graph
    if ((node == null) || (!nodes.containsKey(node.getNodeID()))) return false;

    // Delete all edges involving this node
    ArrayList<Edge> toDelete = new ArrayList<>(node.getEdges().values());
    toDelete.forEach(this::deleteEdge);

    // Delete node
    nodes.remove(node.getNodeID());
    nodeObservableList.remove(node);
    allCampusObservableList.remove(node);

    DB.removeEdgeByNode(node.getNodeID(), campus);

    boolean success = DB.deleteNode(node.getNodeID(), campus);
    if (success) {
      return true;
    } else {
      // Add node back to graph map
      nodes.put(node.getNodeID(), node);
      nodeObservableList.add(node);
      allCampusObservableList.add(node);
      nodeObservableList.sort(Comparator.comparing(o -> o.toString().toLowerCase()));
      allCampusObservableList.sort(Comparator.comparing(o -> o.toString().toLowerCase()));

      // Add edges back to table if we failed to remove the node
      toDelete.forEach(
          edge -> {
            addEdge(edge.getStart(), edge.getEnd());
          });
      return false;
    }
  }

  /**
   * Deletes a node from the graph by ID
   *
   * @param nodeID ID of node to delete
   * @return Success / Failure
   */
  public boolean deleteNode(String nodeID) {
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

    DB.deleteEdge(start.getNodeID() + "_" + end.getNodeID(), campus);
    DB.deleteEdge(end.getNodeID() + "_" + start.getNodeID(), campus);

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

  public int getFloorInt(String floor) {
    if (campus.equals(Campus.FAULKNER)) {
      return Integer.parseInt(floor);
    } else {
      switch (floor) {
        case "L2":
          return 1;
        case "L1":
          return 2;
        case "G":
          return 3;
        case "1":
          return 4;
        case "2":
          return 5;
        case "3":
          return 6;
        default:
          return 0;
      }
    }
  }

  public String getFloorString(int floor) {
    if (campus.equals(Campus.FAULKNER)) {
      return Integer.toString(floor);
    } else {
      switch (floor) {
        case 1:
          return "L2";
        case 2:
          return "L1";
        case 3:
          return "G";
        case 4:
          return "1";
        case 5:
          return "2";
        case 6:
          return "3";
        default:
          return "";
      }
    }
  }

  public String getFloorString(int floor, String c) {
    if (c.equals(Campus.FAULKNER.toString())) {
      return Integer.toString(floor);
    } else {
      switch (floor) {
        case 1:
          return "L2";
        case 2:
          return "L1";
        case 3:
          return "G";
        case 4:
          return "1";
        case 5:
          return "2";
        case 6:
          return "3";
        default:
          return "";
      }
    }
  }

  private int calcWeight(Node start, Node end) {
    int side1 = Math.abs(start.getX() - end.getX());
    int side2 = Math.abs(start.getY() - end.getY());
    int side3 = Math.abs(start.getFloor() - end.getFloor()) * 100;

    double weight = Math.sqrt(side1 * side1 + side2 * side2 + side3 * side3);
    return (int) Math.round(weight);
  }

  /**
   * Updates the graph to match the database
   *
   * @return Success / Failure
   */
  public boolean update() {
    HashMap<String, Node> newNodes = new HashMap<>();
    String tblNameNode = "";
    String tblNameEdge = "";
    if (campus == Campus.FAULKNER) {
      tblNameNode = "NodeFaulkner";
      tblNameEdge = "EdgeFaulkner";
    } else if (campus == Campus.MAIN) {
      tblNameNode = "NodeMain";
      tblNameEdge = "EdgeMain";
    }
    try {
      PreparedStatement pstmtNode = conn.prepareStatement("SELECT * FROM " + tblNameNode);
      ResultSet rsetNode = pstmtNode.executeQuery();
      while (rsetNode.next()) {
        String nodeID = rsetNode.getString("nodeID");
        int xcoord = rsetNode.getInt("xcoord");
        int ycoord = rsetNode.getInt("ycoord");
        int floor = rsetNode.getInt("floor");
        String building = rsetNode.getString("building");
        String nodeType = rsetNode.getString("nodeType");
        NodeType type = NodeType.valueOf(nodeType);
        String longName = rsetNode.getString("longName");
        String shortName = rsetNode.getString("shortName");
        String teamAssigned = rsetNode.getString("teamAssigned");
        Node node =
            new Node(
                nodeID, xcoord, ycoord, floor, building, type, longName, shortName, teamAssigned);
        newNodes.put(nodeID, node);
      }
      rsetNode.close();
      pstmtNode.close();
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
    try {
      PreparedStatement pstmtEdge = conn.prepareStatement("SELECT * FROM " + tblNameEdge);
      ResultSet rsetEdge = pstmtEdge.executeQuery();
      while (rsetEdge.next()) {
        String startNode = rsetEdge.getString("startNode");
        String endNode = rsetEdge.getString("endNode");
        Node start = newNodes.get(startNode);
        Node end = newNodes.get(endNode);
        int w = calcWeight(start, end);
        Edge forward = new Edge(start, end, w);
        start.addEdge(forward);
      }
      rsetEdge.close();
      pstmtEdge.close();
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }

    nodes = newNodes;

    // Update observable list
    allCampusObservableList.removeAll(nodeObservableList);
    nodeObservableList.clear();

    nodeObservableList.addAll(nodes.values());
    allCampusObservableList.addAll(nodeObservableList);

    nodeObservableList.sort(Comparator.comparing(o -> o.toString().toLowerCase()));
    allCampusObservableList.sort(Comparator.comparing(o -> o.toString().toLowerCase()));

    return true;
  }

  /** Clear all the nodes in this graph */
  public void clearGraph() {
    // Delete every node's edges
    nodes.values().forEach(node -> node.getEdges().clear());

    // Delete nodes
    nodes.clear();

    // Clear observable list of nodes
    allCampusObservableList.removeAll(nodeObservableList);
    nodeObservableList.clear();

    // Reset edge count
    edgeCount = 0;

    // Clear the database
    DB.removeAll(campus);
  }

  /**
   * Get the nodes in this graph
   *
   * @return An observable list for use in various JavaFX controls
   */
  public ObservableList<Node> getNodeObservableList() {
    return nodeObservableList;
  }

  public static ObservableList<Node> getAllCampusObservableList() {
    // Init both campuses in case they don't already exist
    Graph faulkner = getInstance(Campus.FAULKNER);
    Graph main = getInstance(Campus.MAIN);

    return allCampusObservableList;
  }

  public static ObservableList<Node> getAllValidDestinationList() {
    // Init both campuses in case they don't already exist
    Graph faulkner = getInstance(Campus.FAULKNER);
    Graph main = getInstance(Campus.MAIN);

    return allValidDestinationList;
  }

  public static ObservableList<Node> getRoomLocationList() {
    // Init both campuses in case they don't already exist
    Graph faulkner = getInstance(Campus.FAULKNER);
    Graph main = getInstance(Campus.MAIN);

    return roomLocationList;
  }

  public GraphDatabase getDB() {
    return DB;
  }

  public Campus getCampus() {
    return campus;
  }
}

package edu.wpi.cs3733.d20.teamA.graph;

import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestPath {
  Graph graph = Graph.getInstance();

  // Create nodes
  Node node1 = new Node("Node1", 0, 0, 1, "", NodeType.HALL, "", "", "");
  Node node2 = new Node("Node2", 0, 1, 1, "", NodeType.HALL, "", "", "");
  Node node3 = new Node("Node3", 1, 1, 1, "", NodeType.HALL, "", "", "");
  Node node4 = new Node("Node4", 1, 0, 1, "", NodeType.HALL, "", "", "");

  Node nodeA = new Node("NodeA", 0, 0, 1, "", NodeType.HALL, "", "", "");
  Node nodeB = new Node("NodeB", 3, 0, 1, "", NodeType.HALL, "", "", "");
  Node nodeC = new Node("NodeC", 6, 0, 1, "", NodeType.HALL, "", "", "");
  Node nodeD = new Node("NodeD", 0, 4, 1, "", NodeType.HALL, "", "", "");
  Node nodeE = new Node("NodeE", 3, 4, 1, "", NodeType.HALL, "", "", "");
  Node nodeF = new Node("NodeF", 6, 4, 1, "", NodeType.HALL, "", "", "");
  Node nodeG = new Node("NodeG", 0, 8, 1, "", NodeType.HALL, "", "", "");
  Node nodeH = new Node("NodeH", 3, 8, 1, "", NodeType.HALL, "", "", "");
  Node nodeI = new Node("NodeI", 6, 8, 1, "", NodeType.HALL, "", "", "");

  public TestPath() throws SQLException, IOException, CsvException {}

  public void setupFirstGraph() throws SQLException {
    graph.clearGraph();

    // Add Nodes to Graph
    graph.addNode(node1);
    graph.addNode(node2);
    graph.addNode(node3);
    graph.addNode(node4);

    // Add Edges to the graph
    graph.addEdge(node1, node2, 3);
    graph.addEdge(node1, node3, 5);
    graph.addEdge(node2, node3, 4);
    graph.addEdge(node1, node4, 1);
  }

  public void setupSecondGraph() throws SQLException {
    graph.clearGraph();

    graph.addNode(nodeA);
    graph.addNode(nodeB);
    graph.addNode(nodeC);
    graph.addNode(nodeD);
    graph.addNode(nodeE);
    graph.addNode(nodeF);
    graph.addNode(nodeG);
    graph.addNode(nodeH);
    graph.addNode(nodeI);

    graph.addEdge(nodeA, nodeB, 3);
    graph.addEdge(nodeA, nodeE, 5);
    graph.addEdge(nodeA, nodeD, 4);
    graph.addEdge(nodeB, nodeC, 3);
    graph.addEdge(nodeB, nodeE, 4);
    graph.addEdge(nodeC, nodeE, 5);
    graph.addEdge(nodeC, nodeF, 4);
    graph.addEdge(nodeD, nodeE, 3);
    graph.addEdge(nodeD, nodeH, 10);
    graph.addEdge(nodeD, nodeG, 10);
    graph.addEdge(nodeE, nodeF, 3);
    graph.addEdge(nodeE, nodeH, 10);
    graph.addEdge(nodeF, nodeH, 10);
    graph.addEdge(nodeF, nodeI, 10);
    graph.addEdge(nodeG, nodeH, 3);
    graph.addEdge(nodeH, nodeI, 3);
  }

  @Test
  public void testAStarGraph1() throws SQLException {
    ArrayList<Node> realPath = new ArrayList<>();
    realPath.add(node1);
    realPath.add(node3);

    setupFirstGraph();
    Path path = new Path(graph);
    path.findPath(node1, node3);
    ArrayList<Node> pathNodes = path.getPathNodes();

    Assertions.assertNotNull(pathNodes);

    Assertions.assertEquals(realPath, pathNodes);
  }

  @Test
  public void testAStarGraph2() throws SQLException {
    ArrayList<Node> realPath = new ArrayList<>();
    realPath.add(nodeG);
    realPath.add(nodeH);
    realPath.add(nodeF);
    realPath.add(nodeC);

    setupSecondGraph();
    Path path = new Path(graph);
    path.findPath(nodeG, nodeC);
    ArrayList<Node> pathNodes = path.getPathNodes();

    Assertions.assertNotNull(pathNodes);

    Assertions.assertEquals(realPath, pathNodes);
  }

  @Test
  public void testBreadthFirstGraph1() throws SQLException {
    ArrayList<Node> realPath = new ArrayList<>();
    realPath.add(node1);
    realPath.add(node3);

    setupFirstGraph();
    BreadthFirst path = new BreadthFirst(graph);
    path.findPath(node1, node3);
    ArrayList<Node> pathNodes = path.getPathNodes();

    Assertions.assertNotNull(pathNodes);

    Assertions.assertEquals(realPath, pathNodes);
  }

  @Test
  public void testBreadthFirstGraph2() throws SQLException {
    ArrayList<Node> realPath = new ArrayList<>();
    realPath.add(nodeG);
    realPath.add(nodeD);
    realPath.add(nodeE);
    realPath.add(nodeC);

    setupSecondGraph();
    BreadthFirst path = new BreadthFirst(graph);
    path.findPath(nodeG, nodeC);
    ArrayList<Node> pathNodes = path.getPathNodes();

    Assertions.assertNotNull(pathNodes);

    Assertions.assertEquals(realPath, pathNodes);
  }

  @Test
  public void testDepthFirstGraph1() throws SQLException {
    ArrayList<Node> realPath = new ArrayList<>();
    realPath.add(node1);
    realPath.add(node3);

    setupFirstGraph();
    DepthFirst path = new DepthFirst(graph);
    path.findPath(node1, node3);
    ArrayList<Node> pathNodes = path.getPathNodes();

    Assertions.assertNotNull(pathNodes);

    Assertions.assertEquals(realPath, pathNodes);
  }
}

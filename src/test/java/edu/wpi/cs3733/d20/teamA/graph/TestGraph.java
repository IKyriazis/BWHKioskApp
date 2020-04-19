package edu.wpi.cs3733.d20.teamA.graph;

import java.sql.SQLException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestGraph {
  Graph graph = Graph.getInstance();

  Node node1 = new Node("testNode1", 1, 0, 1, "", NodeType.HALL, "", "", "");
  Node node2 = new Node("testNode2", 0, 1, 1, "", NodeType.HALL, "", "", "");
  Node node3 = new Node("testNode3", 1, 1, 1, "", NodeType.HALL, "", "", "");
  Node node4 = new Node("testNode4", 0, 0, 1, "", NodeType.HALL, "", "", "");
  Node node5 = new Node("testNode5", 2, 1, 1, "", NodeType.HALL, "", "", "");

  public TestGraph() throws SQLException {}

  @Test
  public void testSingleton() throws SQLException {
    Assertions.assertNotNull(Graph.getInstance());
    Assertions.assertNotNull(Graph.getInstance().getDB());
  }

  @Test
  // Adds multiple node and tests to make sure there is the correct number of nodes after
  public void testAddingMultipleNodes() throws SQLException {
    graph.clearGraph();

    graph.addNode(node1);
    graph.addNode(node2);
    graph.addNode(node3);

    Assertions.assertEquals(graph.getNodeCount(), 3);
  }

  @Test
  // Tests multiple cases:
  // 1. Creates nodes and checks to make sure there are the correct number of nodes after
  // 2. Adds edges to the nodes and checks to make sure the correct number of edges are there after
  // 3. Deletes nodes and checks to make sure the correct number of nodes AND edges are there after
  public void testDeletingNodes() throws SQLException {
    graph.clearGraph();
    Assertions.assertEquals(graph.getNodeCount(), 0);

    graph.addNode(node1);
    graph.addNode(node2);
    graph.addNode(node3);

    Assertions.assertEquals(graph.getNodeCount(), 3);
    Assertions.assertTrue(graph.hasNode("testNode1"));
    Assertions.assertTrue(graph.hasNode("testNode2"));
    Assertions.assertTrue(graph.hasNode("testNode3"));

    graph.addEdge(node1, node2, 5);
    graph.addEdge(node2, node3, 5);

    Assertions.assertEquals(graph.getEdgeCount(), 2);

    Assertions.assertTrue(node1.hasEdgeTo(node2));
    Assertions.assertTrue(node2.hasEdgeTo(node1));
    Assertions.assertTrue(node2.hasEdgeTo(node3));
    Assertions.assertTrue(node3.hasEdgeTo(node2));

    Assertions.assertFalse(node1.hasEdgeTo(node3));
    Assertions.assertFalse(node3.hasEdgeTo(node1));

    graph.deleteNode(node1);
    Assertions.assertEquals(graph.getEdgeCount(), 1);
    Assertions.assertEquals(graph.getNodeCount(), 2);
    Assertions.assertFalse(graph.hasNode("testNode1"));
  }

  @Test
  // Tests multiple cases:
  // 1. Creates nodes and checks to make sure there are no edges after creating them
  // 2. Adds edges to the nodes and checks to make sure the correct number of edges is there after
  // 3. Deletes edges from the nodes and checks to make sure the correct number of edges is there
  // after
  public void testNumEdges() throws SQLException {
    graph.clearGraph();

    Assertions.assertEquals(graph.getEdgeCount(), 0);

    graph.addNode(node1);
    graph.addNode(node2);
    graph.addNode(node3);

    Assertions.assertEquals(graph.getEdgeCount(), 0);

    graph.addEdge(node1, node2, 5);
    graph.addEdge(node2, node3, 5);
    graph.addEdge(node3, node1, 5);

    Assertions.assertEquals(graph.getEdgeCount(), 3);

    graph.deleteEdge(node1, node2);

    Assertions.assertEquals(graph.getEdgeCount(), 2);
  }

  @Test
  // Tests all the possibilities with edges
  public void testEdges() throws SQLException {
    graph.clearGraph();

    // Adds the nodes to the graph
    graph.addNode(node2);
    graph.addNode(node3);

    // Adds an edge with a node not in the graph
    graph.addEdge(node1, node2, 5);
    Assertions.assertEquals(0, graph.getEdgeCount());

    // Ensures an edge has been added
    graph.addEdge(node2, node3, 6);
    Assertions.assertEquals(1, graph.getEdgeCount());
  }

  @Test
  public void testUpdate() throws SQLException {

  }
}

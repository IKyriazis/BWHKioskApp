package edu.wpi.cs3733.d20.teamA.util;

import edu.wpi.cs3733.d20.teamA.App;
import edu.wpi.cs3733.d20.teamA.graph.Edge;
import edu.wpi.cs3733.d20.teamA.graph.Graph;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.graph.NodeType;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CSVLoader {
  public static void readNodes(Graph graph) {
    InputStream stream =
        App.class.getResourceAsStream("/edu/wpi/cs3733/d20/teamA/csvfiles/MapAAllNodes.csv");

    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    readNodes(graph, reader.lines().skip(1).collect(Collectors.toList()));
  }

  public static void readNodes(Graph graph, List<String> nodeLines) {
    for (String line : nodeLines) {
      String[] cols = line.split(",");

      String nodeID = cols[0];
      int x = Integer.parseInt(cols[1]);
      int y = Integer.parseInt(cols[2]);
      int floor = Integer.parseInt(cols[3]);
      String building = cols[4];
      String nodeType = cols[5];
      String longName = cols[6];
      String shortName = cols[7];
      String teamAssigned = cols[8];

      graph.addNode(
          new Node(
              nodeID,
              x,
              y,
              floor,
              building,
              NodeType.toNodeType(nodeType),
              longName,
              shortName,
              teamAssigned));
    }
  }

  public static void readEdges(Graph graph) {
    InputStream stream =
        App.class.getResourceAsStream("/edu/wpi/cs3733/d20/teamA/csvfiles/MapAAllEdges.csv");

    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    readEdges(graph, reader.lines().skip(1).collect(Collectors.toList()));
  }

  public static void readEdges(Graph graph, List<String> edgeLines) {
    for (String line : edgeLines) {
      String[] cols = line.split(",");

      String startID = cols[1];
      String endID = cols[2];
      graph.addEdge(graph.getNodeByID(startID), graph.getNodeByID(endID));
    }
  }

  public static void exportEdges(Graph graph, File out) {
    // Accumulate all edges
    ArrayList<Edge> edges = new ArrayList<>();
    graph.getNodes().values().forEach(node -> edges.addAll(node.getEdges().values()));

    // Deduplicate edges
    ArrayList<Edge> duplicates = new ArrayList<>();
    for (Edge edge : edges) {
      if (duplicates.contains(edge)) {
        continue;
      }

      for (Edge other : edges) {
        if ((edge.getStart() == other.getEnd()) && (edge.getEnd() == other.getStart())) {
          duplicates.add(other);
        }
      }
    }
    edges.removeAll(duplicates);

    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(out));
      writer.write("edgeID,startNode,endNode\n");
      for (Edge edge : edges) {
        String startID = edge.getStart().getNodeID();
        String endID = edge.getEnd().getNodeID();
        writer.write(startID + "_" + endID + "," + startID + "," + endID + "\n");
      }
      writer.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void exportNodes(Graph graph, File out) {
    // Accumulate all edges
    ArrayList<Node> nodes = new ArrayList<>(graph.getNodes().values());

    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(out));
      writer.write(
          "nodeID,xcoord,ycoord,floor,building,nodeType,longName,shortName,teamAssigned\n");
      for (Node node : nodes) {
        int xcoordinate = node.getX();
        int ycoordinate = node.getY();
        writer.write(
            node.getNodeID()
                + ","
                + xcoordinate
                + ","
                + ycoordinate
                + ","
                + node.getFloor()
                + ","
                + node.getBuilding()
                + ","
                + node.getType().toString()
                + ","
                + node.getLongName()
                + ","
                + node.getShortName()
                + ","
                + node.getTeamAssigned()
                + "\n");
      }
      writer.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

package edu.wpi.cs3733.d20.teamA.util;

import edu.wpi.cs3733.d20.teamA.graph.Edge;
import edu.wpi.cs3733.d20.teamA.graph.Graph;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CSVLoader {
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
    ArrayList<Node> nodes = new ArrayList<>();
    graph.getNodes().values().forEach(node -> nodes.add(node));

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

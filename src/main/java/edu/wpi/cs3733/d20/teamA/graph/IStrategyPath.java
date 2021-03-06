package edu.wpi.cs3733.d20.teamA.graph;

import java.util.ArrayList;
import javafx.util.Pair;

public interface IStrategyPath {
  public void findPath(Node start, Node end);

  public ArrayList<Node> getPathNodes();

  public ArrayList<Edge> getPathEdges();

  public ArrayList<Pair<Node, String>> textualDirections();

  public void update();

  public void pathFind(Node start, Node end);
}

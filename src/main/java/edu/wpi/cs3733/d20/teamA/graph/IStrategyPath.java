package edu.wpi.cs3733.d20.teamA.graph;

import java.util.ArrayList;

public interface IStrategyPath {
  public void findPath(Node start, Node end);

  public ArrayList<Node> getPathNodes();

  public ArrayList<Edge> getPathEdges();

  public ArrayList<String> textualDirections();

  public void update();

  public void pathFind(Node start, Node end);
}

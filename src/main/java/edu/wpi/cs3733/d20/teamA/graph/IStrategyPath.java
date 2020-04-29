package edu.wpi.cs3733.d20.teamA.graph;

import java.util.ArrayList;
import javafx.scene.control.Label;

public interface IStrategyPath {
  public void findPath(Node start, Node end);

  public ArrayList<Node> getPathNodes();

  public ArrayList<Edge> getPathEdges();

  public ArrayList<Label> textualDirections();

  public void update();
}

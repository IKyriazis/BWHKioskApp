package edu.wpi.cs3733.d20.teamA.graph;

import java.util.ArrayList;

public class ContextPath {

  private IStrategyPath pathFindingAlgo;

  public ContextPath() {}

  public ContextPath(IStrategyPath pathFindingAlgo) {
    this.pathFindingAlgo = pathFindingAlgo;
  }

  public void setPath(IStrategyPath pathFindingAlgo) {
    this.pathFindingAlgo = pathFindingAlgo;
  }

  public void setGraph(Graph graph) {
    if (pathFindingAlgo != null && pathFindingAlgo instanceof PathAlgo) {
      ((PathAlgo) pathFindingAlgo).setGraph(graph);
    }
  }

  public void findPath(Node start, Node end) {
    pathFindingAlgo.pathFind(start, end);
  }

  public IStrategyPath getPathFindingAlgo() {
    return this.pathFindingAlgo;
  }

  public ArrayList<Edge> getPathEdges() {
    return pathFindingAlgo.getPathEdges();
  }

  public ArrayList<Node> getPathNodes() {
    return pathFindingAlgo.getPathNodes();
  }

  public void update() {
    pathFindingAlgo.update();
  };
}

package edu.wpi.cs3733.d20.teamA.graph;

public class ContextPath {

  private IStrategyPath pathFindingAlgo;

  public ContextPath() {}

  public ContextPath(IStrategyPath pathFindingAlgo) {
    this.pathFindingAlgo = pathFindingAlgo;
  }

  public void setPath(IStrategyPath pathFindingAlgo) {
    this.pathFindingAlgo = pathFindingAlgo;
  }

  public void findPath(Node start, Node end) {
    pathFindingAlgo.findPath(start, end);
  }
}

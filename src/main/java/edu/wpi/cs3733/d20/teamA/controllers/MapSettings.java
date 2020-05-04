package edu.wpi.cs3733.d20.teamA.controllers;

import edu.wpi.cs3733.d20.teamA.graph.*;

public class MapSettings {
  private static ContextPath path;
  private static boolean setup = false;

  public MapSettings() {}

  public static ContextPath getPath() {
    return path;
  }

  public static void setPath(IStrategyPath search) {
    path.setPath(search);
  }

  public static void setup() {
    Graph graph = Graph.getInstance(Campus.FAULKER);
    path = new ContextPath();
    path.setPath(new Path(graph));
    setup = true;
  }

  public static boolean isSetup() {
    return setup;
  }
}

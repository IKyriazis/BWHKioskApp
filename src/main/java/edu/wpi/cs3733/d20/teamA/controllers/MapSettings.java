package edu.wpi.cs3733.d20.teamA.controllers;

import edu.wpi.cs3733.d20.teamA.graph.ContextPath;
import edu.wpi.cs3733.d20.teamA.graph.IStrategyPath;

public class MapSettings {
  public static ContextPath path;

  public MapSettings(){}

  public static ContextPath getPath() {
    return path;
  }

  public static void setPath(IStrategyPath search) {
    path.setPath(search);
  }
}

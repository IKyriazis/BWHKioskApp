package edu.wpi.cs3733.d20.teamA.controllers;

import edu.wpi.cs3733.d20.teamA.graph.*;
import javafx.scene.paint.Color;

public class MapSettings {
  private static ContextPath path;
  private static boolean setup = false;
  private static Color darkColor = Color.rgb(249, 194, 44);
  private static Color lightColor = Color.rgb(255, 224, 140);

  public MapSettings() {}

  public static ContextPath getPath() {
    return path;
  }

  public static void setPath(IStrategyPath search) {
    path.setPath(search);
  }

  public static void setup() {
    Graph graph = Graph.getInstance(Campus.FAULKNER);
    path = new ContextPath();
    path.setPath(new Path(graph));
    setup = true;
  }

  public static Color getLightColor() {
    return lightColor;
  }

  public void setLightColor(Color lightColor) {
    this.lightColor = lightColor;
  }

  public static Color getDarkColor() {
    return darkColor;
  }

  public void setDarkColor(Color darkColor) {
    this.darkColor = darkColor;
  }

  public static boolean isSetup() {
    return setup;
  }
}

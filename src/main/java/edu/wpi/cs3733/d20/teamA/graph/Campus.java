package edu.wpi.cs3733.d20.teamA.graph;

public enum Campus {
  FAULKNER("Faulkner"),
  MAIN("Main"),
  INTER("Inbetween");

  private final String name;

  Campus(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}

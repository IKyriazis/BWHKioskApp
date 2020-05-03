package edu.wpi.cs3733.d20.teamA.database.inventory;

public enum ItemType {
  FLOWER("flower"),
  INTERPRETER("interpreter");

  private String name;

  ItemType(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}

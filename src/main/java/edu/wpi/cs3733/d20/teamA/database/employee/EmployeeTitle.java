package edu.wpi.cs3733.d20.teamA.database.employee;

public enum EmployeeTitle {
  ADMIN("admin"),
  DOCTOR("doctor"),
  NURSE("nurse"),
  JANITOR("janitor"),
  INTERPRETER("interpreter"),
  RECEPTIONIST("receptionist"),
  RETAIL("retail");

  private String name;

  EmployeeTitle(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}

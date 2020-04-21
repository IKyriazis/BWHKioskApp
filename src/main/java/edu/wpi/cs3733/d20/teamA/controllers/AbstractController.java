package edu.wpi.cs3733.d20.teamA.controllers;

import edu.wpi.cs3733.d20.teamA.database.*;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractController {

  private DatabaseServiceProvider provider;
  private Connection conn;
  protected FlowerDatabase flDatabase;
  protected GraphDatabase graphDatabase;
  protected JanitorDatabase janitorDatabase;
  protected EmployeesDatabase employeesDatabase;

  public AbstractController() {
    provider = new DatabaseServiceProvider();
    try {
      conn = provider.provideConnection();
      graphDatabase = new GraphDatabase(conn);
      graphDatabase.createTables();
      flDatabase = new FlowerDatabase(conn);
      graphDatabase.deleteNode("ID");
    } catch (SQLException throwables) {
      throwables.printStackTrace(); // All is lost
    }
  }
}

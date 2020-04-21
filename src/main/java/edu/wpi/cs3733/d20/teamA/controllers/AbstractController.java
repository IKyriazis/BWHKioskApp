package edu.wpi.cs3733.d20.teamA.controllers;

import edu.wpi.cs3733.d20.teamA.database.*;
import edu.wpi.cs3733.d20.teamA.database.DatabaseServiceProvider;
import edu.wpi.cs3733.d20.teamA.database.EmployeesDatabase;
import edu.wpi.cs3733.d20.teamA.database.FlowerDatabase;
import edu.wpi.cs3733.d20.teamA.database.GraphDatabase;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractController {

  private DatabaseServiceProvider provider;
  private Connection conn;
  protected FlowerDatabase flDatabase;
  protected GraphDatabase graphDatabase;
  protected EmployeesDatabase eDB;
  protected JanitorDatabase janitorDatabase;

  public AbstractController() {
    provider = new DatabaseServiceProvider();
    try {
      conn = provider.provideConnection();
      graphDatabase = new GraphDatabase(conn);
      graphDatabase.createTables();
      flDatabase = new FlowerDatabase(conn);
      eDB = new EmployeesDatabase(conn);
      eDB.createTables();
      graphDatabase.deleteNode("ID");
      janitorDatabase = new JanitorDatabase(conn);
      janitorDatabase.createTables();
      eDB = new EmployeesDatabase(conn);
      eDB.createTables();
    } catch (SQLException throwables) {
      throwables.printStackTrace(); // All is lost
    }
  }
}

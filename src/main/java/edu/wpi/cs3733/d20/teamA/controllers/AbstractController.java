package edu.wpi.cs3733.d20.teamA.controllers;

import edu.wpi.cs3733.d20.teamA.database.DatabaseServiceProvider;
import edu.wpi.cs3733.d20.teamA.database.FlowerDatabase;
import edu.wpi.cs3733.d20.teamA.database.GraphDatabase;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractController {

  private DatabaseServiceProvider provider;
  private Connection conn;
  protected FlowerDatabase flDatabase;
  protected GraphDatabase graphDatabase;

  public AbstractController() {
    provider = new DatabaseServiceProvider();
    try {
      conn = provider.provideConnection();
      flDatabase = new FlowerDatabase(conn);
      flDatabase.createTables();
      graphDatabase = new GraphDatabase(conn);
      graphDatabase.createTables();
      graphDatabase.deleteNode("ID");
    } catch (SQLException throwables) {
      throwables.printStackTrace(); // All is lost
    }
  }
}

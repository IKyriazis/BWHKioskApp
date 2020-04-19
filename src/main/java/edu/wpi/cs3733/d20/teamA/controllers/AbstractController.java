package edu.wpi.cs3733.d20.teamA.controllers;

import edu.wpi.cs3733.d20.teamA.database.DatabaseServiceProvider;
import edu.wpi.cs3733.d20.teamA.database.FlowerDatabase;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractController {

  private DatabaseServiceProvider provider;
  private Connection conn;
  protected FlowerDatabase flDatabase;

  public AbstractController() throws SQLException {
    provider = new DatabaseServiceProvider();
    conn = provider.provideConnection();
    flDatabase.createTables();
  }
}

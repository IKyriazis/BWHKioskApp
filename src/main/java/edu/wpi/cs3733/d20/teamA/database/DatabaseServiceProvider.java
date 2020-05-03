package edu.wpi.cs3733.d20.teamA.database;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This is a Guice Module config class. This module forces guice to provide an instance of the
 * DatabaseService as a singleton.
 */
public class DatabaseServiceProvider extends AbstractModule {

  private final String remoteDbUrl =
      "jdbc:derby://db.dcole.dev:1527/BWDatabase;user=santa;password=c|@aus";
  private final String realDbUrl = "jdbc:derby:BWDatabase;create=true";

  @Override
  protected void configure() {}

  /** Provide single connection for database access. */
  @Provides
  @Singleton
  public Connection provideConnection() {
    try {
      Connection connRemote = DriverManager.getConnection(remoteDbUrl);
      return connRemote;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try {
      Connection connEmbedded = DriverManager.getConnection(realDbUrl);
      return connEmbedded;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }
}

package edu.wpi.cs3733.d20.teamA.database;

import java.sql.SQLException;

public class JanitorDatabase extends Database {
  /**
   * Drops the graph tables so we can start fresh
   *
   * @return false if the tables don't exist and CONSTRAINT can't be dropped, true if CONSTRAINT and
   *     tables are dropped correctly
   * @throws SQLException
   */
  public boolean dropTables() throws SQLException {

    // if the helper returns false this method should too
    // drop the CONSTRAINT first
    //        if (!(helperPrepared("ALTER TABLE Orders DROP CONSTRAINT FK_fT"))) {
    //
    //            return false;
    //        }
    // Drop the tables
    if (!(helperPrepared("DROP TABLE JanitorRequest"))) {
      return false;
    }

    return true;
  }

  /**
   * Creates graph tables
   *
   * @return False if tables couldn't be created
   * @throws SQLException
   */
  public boolean createTables() throws SQLException {

    // Create the graph tables
    boolean a =
        helperPrepared(
            "CREATE TABLE JanitorRequest (time TIMESTAMP NOT NULL, location Varchar(15) NOT NULL, name Varchar(15), progress Varchar(19) NOT NULL, priority Varchar(6) NOT NULL, CONSTRAINT CHK_PRIO CHECK (priority in ('Low', 'Medium', 'High')), CONSTRAINT CHK_PROG CHECK (progress in ('Reported', 'Dispatched', 'Done')), CONSTRAINT PK_J PRIMARY KEY (time, location))");
    if (a) {
      return true;
    } else {
      return false;
    }
  }
}

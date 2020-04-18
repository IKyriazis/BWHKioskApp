package edu.wpi.cs3733.d20.teamA.database;

import java.sql.*;

public class GraphDatabase extends Database {

  /**
   * Drops the graph tables so we can start fresh
   *
   * @return false if the tables don't exist and constraints can't be dropped, true if constraints
   *     and tables are dropped correctly
   * @throws SQLException
   */
  public boolean dropTables() throws SQLException {

    // if the helper returns false this method should too
    // drop the constraints first
    if (!(helperPrepared("ALTER TABLE Edge DROP CONSTRAINT FK_SN")
        && helperPrepared("ALTER TABLE Edge DROP CONSTRAINT FK_EN"))) {

      return false;
    }
    // Drop the tables
    if (!(helperPrepared("DROP TABLE Edge") && helperPrepared("DROP TABLE Node"))) {
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
    boolean b =
        helperPrepared(
            "CREATE TABLE Node (nodeID Varchar(10) PRIMARY KEY, xcoord INTEGER NOT NULL, ycoord INTEGER NOT NULL, floor INTEGER NOT NULL, building Varchar(50), nodeType Varchar(4) NOT NULL, longName Varchar(200) NOT NULL, shortName Varchar(25), teamAssigned Varchar(10) NOT NULL, CONSTRAINT CHK_Floor CHECK (floor >= 1 AND floor<= 10), CONSTRAINT CHK_Coords CHECK (xcoord > 0 AND ycoord > 0), CONSTRAINT CHK_Type CHECK (nodeType in ('HALL', 'ELEV', 'REST', 'STAI', 'DEPT', 'LABS', 'INFO', 'CONF', 'EXIT', 'RETL', 'SERV')))");

    boolean a =
        helperPrepared(
            "CREATE TABLE Edge (edgeID Varchar(21) PRIMARY KEY, startNode Varchar(10) NOT NULL, endNode Varchar(10) NOT NULL, CONSTRAINT FK_SN FOREIGN KEY (startNode) REFERENCES Node(nodeID), CONSTRAINT FK_EN FOREIGN KEY (endNode) REFERENCES Node(nodeID))");

    if (a && b) {
      return true;
    } else {
      return false;
    }
  }

  public boolean removeAllNodes() throws SQLException {
    return helperPrepared("DELETE From Node");
  }

  public boolean removeAllEdges() throws SQLException {
    return helperPrepared("DELETE From Edge");
  }

  public int getSizeNode() throws SQLException {
    int count = 0;
    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
      PreparedStatement pstmt = conn.prepareStatement("Select * From Node");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        count++;
      }
      rset.close();
      pstmt.close();
      conn.close();
      return count;
    } catch (SQLException e) {
      return -1;
    }
  }

  public boolean addNode(
      String nodeID,
      int xcoord,
      int ycoord,
      int floor,
      String building,
      String nodeType,
      String longName,
      String shortName,
      String teamAssigned)
      throws SQLException {
    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
      PreparedStatement pstmt =
          conn.prepareStatement(
              "INSERT INTO Node (nodeID, xcoord, ycoord, floor, building, nodeType, longName, shortName, teamAssigned) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
      pstmt.setString(1, nodeID);
      pstmt.setInt(2, xcoord);
      pstmt.setInt(3, ycoord);
      pstmt.setInt(4, floor);
      pstmt.setString(5, building);
      pstmt.setString(6, nodeType);
      pstmt.setString(7, longName);
      pstmt.setString(8, shortName);
      pstmt.setString(9, teamAssigned);
      pstmt.executeUpdate();
      pstmt.close();
      conn.close();
      return true;
    } catch (SQLException e) {
      return false;
    }
  }
}

package edu.wpi.cs3733.d20.teamA.database.graph;

import edu.wpi.cs3733.d20.teamA.database.Database;
import edu.wpi.cs3733.d20.teamA.graph.Campus;
import java.io.*;
import java.sql.*;

public class GraphDatabase extends Database {

  public GraphDatabase(Connection connection) {

    super(connection);

    createTables();
  }

  /**
   * Drops the graph tables so we can start fresh
   *
   * @return false if the tables don't exist and constraints can't be dropped, true if constraints
   *     and tables are dropped correctly
   */
  public boolean dropTables() {
    boolean a = false;
    boolean b = false;
    boolean c = false;
    boolean d = false;
    // if the helper returns false this method should too
    // drop the constraints first
    if (!doesTableNotExist("EDGEFAULKNER")) {
      a = helperPrepared("DROP TABLE EdgeFaulkner");
    }
    if (!doesTableNotExist("NODEFAULKNER")) {
      b = helperPrepared("DROP TABLE NodeFaulkner");
    }
    if (!doesTableNotExist("EDGEMAIN")) {
      c = helperPrepared("DROP TABLE EdgeMain");
    }
    if (!doesTableNotExist("NODEMAIN")) {
      d = helperPrepared("DROP TABLE NodeMain");
    }
    return a && b && c && d;
  }

  /**
   * Creates graph tables
   *
   * @return False if tables couldn't be created
   */
  public boolean createTables() {

    boolean a = false;
    boolean b = false;
    boolean c = false;
    boolean d = false;
    // Create the graph tables
    if (doesTableNotExist("NODEFAULKNER")) {
      b =
          helperPrepared(
              "CREATE TABLE NodeFaulkner (nodeID Varchar(10) PRIMARY KEY, xcoord INTEGER NOT NULL, "
                  + "ycoord INTEGER NOT NULL, floor INTEGER NOT NULL, building Varchar(50), "
                  + "nodeType Varchar(4) NOT NULL, longName Varchar(200) UNIQUE NOT NULL, shortName Varchar(25), "
                  + "teamAssigned Varchar(10) NOT NULL, CONSTRAINT CHK_Floor CHECK (floor >= 1 AND floor<= 10), "
                  + "CONSTRAINT CHK_Coords CHECK (xcoord >= 0 AND ycoord >= 0), CONSTRAINT CHK_Type CHECK (nodeType in ('HALL', 'ELEV', 'REST', 'STAI', 'DEPT', 'LABS', 'INFO', 'CONF', 'EXIT', 'RETL', 'SERV')))");
    }
    if (doesTableNotExist("EDGEFAULKNER")) {
      a =
          helperPrepared(
              "CREATE TABLE EdgeFaulkner (edgeID Varchar(21) PRIMARY KEY, startNode Varchar(10) NOT NULL, "
                  + "endNode Varchar(10) NOT NULL, CONSTRAINT FK_SN FOREIGN KEY (startNode) REFERENCES NodeFaulkner(nodeID), "
                  + "CONSTRAINT FK_EN FOREIGN KEY (endNode) REFERENCES NodeFaulkner(nodeID))");
    }
    if (doesTableNotExist("NODEMAIN")) {
      c =
          helperPrepared(
              "CREATE TABLE NodeMain (nodeID Varchar(10) PRIMARY KEY, xcoord INTEGER NOT NULL, "
                  + "ycoord INTEGER NOT NULL, floor INTEGER NOT NULL, building Varchar(50), "
                  + "nodeType Varchar(4) NOT NULL, longName Varchar(200) UNIQUE NOT NULL, shortName Varchar(25), "
                  + "teamAssigned Varchar(10) NOT NULL, CONSTRAINT CHK_FloorMain CHECK (floor >= 1 AND floor<= 10), "
                  + "CONSTRAINT CHK_CoordsMain CHECK (xcoord >= 0 AND ycoord >= 0), CONSTRAINT CHK_TypeMain CHECK (nodeType in ('HALL', 'ELEV', 'REST', 'STAI', 'DEPT', 'LABS', 'INFO', 'CONF', 'EXIT', 'RETL', 'SERV')))");
    }
    if (doesTableNotExist("EDGEMAIN")) {
      d =
          helperPrepared(
              "CREATE TABLE EdgeMain (edgeID Varchar(21) PRIMARY KEY, startNode Varchar(10) NOT NULL, "
                  + "endNode Varchar(10) NOT NULL, CONSTRAINT FK_SNMain FOREIGN KEY (startNode) REFERENCES NodeMain(nodeID), "
                  + "CONSTRAINT FK_ENMain FOREIGN KEY (endNode) REFERENCES NodeMain(nodeID))");
    }
    return a && b && c && d;
  }

  /**
   * Removes all the nodes from the database
   *
   * @return True if this was successful
   */
  public boolean removeAllNodes() {
    return helperPrepared("DELETE From NodeFaulkner") && helperPrepared("DELETE From NodeMain");
  }

  /**
   * Removes all the edges from the database
   *
   * @return True if this was successful
   */
  public boolean removeAllEdges() {
    return helperPrepared("DELETE From EdgeFaulkner") && helperPrepared("DELETE From EdgeMain");
  }

  /**
   * Removes all the edge and node data from the database
   *
   * @return True if this was successful
   */
  public boolean removeAll() {
    return removeAllEdges() && removeAllNodes();
  }

  /**
   * Loops thru the table to find the number of nodes
   *
   * @return The size of the node table
   */
  public int getSizeNode(Campus c) {
    if (c == Campus.FAULKER) {
      return getSize("NodeFaulkner");
    } else if (c == Campus.MAIN) {
      return getSize("NodeMain");
    }
    return -1;
  }

  /**
   * Loops thru the table to find the number of edges
   *
   * @return The size of the edge table
   */
  public int getSizeEdge(Campus c) {
    if (c == Campus.FAULKER) {
      return getSize("EdgeFaulkner");
    } else if (c == Campus.MAIN) {
      return getSize("EdgeMain");
    }
    return -1;
  }

  public String getCampusEdge(Campus c) {
    String tblName = "";
    if (c == Campus.FAULKER) {
      tblName = "EdgeFaulkner";
    } else if (c == Campus.MAIN) {
      tblName = "EdgeMain";
    }
    return tblName;
  }

  public String getCampusNode(Campus c) {
    String tblName = "";
    if (c == Campus.FAULKER) {
      tblName = "NodeFaulkner";
    } else if (c == Campus.MAIN) {
      tblName = "NodeMain";
    }
    return tblName;
  }

  /**
   * Adds a node to the database
   *
   * @param nodeID - The node's ID
   * @param xcoord - The x-coordinate on the map
   * @param ycoord - The y-coordinate on the map
   * @param floor - The floor in the building that the node is on
   * @param building - The building that the node is located in
   * @param nodeType - The type of node (4-letters, i.e. HALL, LABS, SERV...)
   * @param longName - The long name that describes the node
   * @param shortName - Shorthand name for the long name
   * @param teamAssigned - The team that made the node
   * @param c - The campus
   * @return True if the add was successful
   */
  public boolean addNode(
      String nodeID,
      int xcoord,
      int ycoord,
      int floor,
      String building,
      String nodeType,
      String longName,
      String shortName,
      String teamAssigned,
      Campus c) {
    String tblName = getCampusNode(c);
    try {
      if (!checkIfExistsString(tblName, "nodeID", nodeID)) {
        PreparedStatement pstmt =
            getConnection()
                .prepareStatement(
                    "INSERT INTO "
                        + tblName
                        + " (nodeID, xcoord, ycoord, floor, building, nodeType, longName, shortName, teamAssigned) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
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
        return true;
      }
      return false;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Adds an edge to the database
   *
   * @param edgeID - The edge's ID
   * @param startNode - The node that the edge starts on
   * @param endNode - The node that the edge ends on
   * @return True if the add was successful
   */
  public boolean addEdge(String edgeID, String startNode, String endNode, Campus c) {
    String tblName = getCampusEdge(c);
    try {
      if (!checkIfExistsString(tblName, "edgeID", edgeID)) {
        PreparedStatement pstmt =
            getConnection()
                .prepareStatement(
                    "INSERT INTO " + tblName + " (edgeID, startNode, endNode) VALUES (?, ?, ?)");
        pstmt.setString(1, edgeID);
        pstmt.setString(2, startNode);
        pstmt.setString(3, endNode);
        pstmt.executeUpdate();
        pstmt.close();
        return true;
      }
      return false;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Removes an edge from the database
   *
   * @param edgeID - The ID of the edge to delete
   * @return True if the deletion was successful
   */
  public boolean deleteEdge(String edgeID, Campus c) {
    String tblName = getCampusEdge(c);
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("DELETE From " + tblName + " Where edgeID = ?");
      pstmt.setString(1, edgeID);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Removes the node from the database
   *
   * @param nodeID - The ID of the node to be deleted
   * @return True if the deletion was successful
   */
  public boolean deleteNode(String nodeID, Campus c) {
    String tblName = getCampusNode(c);
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("DELETE From " + tblName + " Where nodeID = ?");
      pstmt.setString(1, nodeID);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Changes a node with the given ID to the new values
   *
   * @param nodeID - The ID of the node to be edited
   * @param xcoord - The new or unchanged x coordinate
   * @param ycoord - The new or unchanged y coordinate
   * @param floor - The new or unchanged floor
   * @param building - The new or unchanged building
   * @param nodeType - The new or unchanged node type
   * @param longName - The new or unchanged long name
   * @param shortName - The new or unchanged short name
   * @param teamAssigned - The new or unchanged team that made the node
   * @return True if the edits were successful
   */
  public boolean editNode(
      String nodeID,
      int xcoord,
      int ycoord,
      int floor,
      String building,
      String nodeType,
      String longName,
      String shortName,
      String teamAssigned,
      Campus c) {
    String tblName = getCampusNode(c);
    try {
      if (checkIfExistsString(tblName, "nodeID", nodeID)) {
        PreparedStatement pstmt =
            getConnection()
                .prepareStatement(
                    "UPDATE "
                        + tblName
                        + " SET xcoord = ?, ycoord = ?, floor = ?, building = ?, nodeType = ?, longName = ?, shortName = ?, teamAssigned = ? WHERE nodeID = ?");
        pstmt.setInt(1, xcoord);
        pstmt.setInt(2, ycoord);
        pstmt.setInt(3, floor);
        pstmt.setString(4, building);
        pstmt.setString(5, nodeType);
        pstmt.setString(6, longName);
        pstmt.setString(7, shortName);
        pstmt.setString(8, teamAssigned);
        pstmt.setString(9, nodeID);
        pstmt.executeUpdate();
        pstmt.close();
        return true;
      }
      return false;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Removes all the edges connected to a certain node
   *
   * @param nodeID - The node to delete edges from
   * @return True if the edges were deleted successfully
   */
  public boolean removeEdgeByNode(String nodeID, Campus c) {
    String tblName = getCampusEdge(c);
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement("DELETE FROM " + tblName + " WHERE startNode = ? OR endNode = ?");
      pstmt.setString(1, nodeID);
      pstmt.setString(2, nodeID);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Helps get an integer of the specified column name
   *
   * @param nodeID - The node to get the int from
   * @param col - The name of the column with the int
   * @return The int
   */
  public int helperGetIntG(String nodeID, String col, Campus c) {
    String tblName = getCampusNode(c);
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("SELECT * FROM " + tblName + " WHERE nodeID = ?");
      pstmt.setString(1, nodeID);
      ResultSet rset = pstmt.executeQuery();
      rset.next();
      int i = rset.getInt(col);
      rset.close();
      pstmt.close();
      return i;
    } catch (SQLException e) {
      e.printStackTrace();
      return -1;
    }
  }

  /**
   * Helps get a String from the specified column name
   *
   * @param nodeID - The node to get the String from
   * @param col - The name of the column with the String
   * @return The String
   */
  public String helperGetStringG(String nodeID, String col, Campus c) {
    String tblName = getCampusNode(c);
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("SELECT * FROM " + tblName + " WHERE nodeID = ?");
      pstmt.setString(1, nodeID);
      ResultSet rset = pstmt.executeQuery();
      rset.next();
      String s = rset.getString(col);
      rset.close();
      pstmt.close();
      return s;
    } catch (SQLException e) {
      e.printStackTrace();
      return "";
    }
  }

  /**
   * Helps get a string from the specified column name in the edge table
   *
   * @param edgeID - The edge to get the STring from
   * @param col - The column name with the String
   * @return The String
   */
  public String helperGetStringEdgeG(String edgeID, String col, Campus c) {
    String tblName = getCampusEdge(c);
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("SELECT * FROM " + tblName + " WHERE nodeID = ?");
      pstmt.setString(1, edgeID);
      ResultSet rset = pstmt.executeQuery();
      rset.next();
      String s = rset.getString(col);
      rset.close();
      pstmt.close();
      return s;
    } catch (SQLException e) {
      e.printStackTrace();
      return "";
    }
  }

  public int getX(String nodeID, Campus c) {
    return helperGetIntG(nodeID, "xcoord", c);
  }

  public int getY(String nodeID, Campus c) {
    return helperGetIntG(nodeID, "ycoord", c);
  }

  public int getFloor(String nodeID, Campus c) {
    return helperGetIntG(nodeID, "floor", c);
  }

  public String getBuilding(String nodeID, Campus c) {
    return helperGetStringG(nodeID, "building", c);
  }

  public String getNodeType(String nodeID, Campus c) {
    return helperGetStringG(nodeID, "nodeType", c);
  }

  public String getLongName(String nodeID, Campus c) {
    return helperGetStringG(nodeID, "longName", c);
  }

  public String getShortName(String nodeID, Campus c) {
    return helperGetStringG(nodeID, "shortName", c);
  }

  public String getTeamAssigned(String nodeID, Campus c) {
    return helperGetStringG(nodeID, "teamAssigned", c);
  }

  public String getStartNode(String edgeID, Campus c) {
    return helperGetStringEdgeG(edgeID, "startNode", c);
  }

  public String getEndNode(String edgeID, Campus c) {
    return helperGetStringEdgeG(edgeID, "endNode", c);
  }

  public boolean setX(String nodeID, int i, Campus c) {
    String tblName = getCampusNode(c);
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement("UPDATE " + tblName + " SET xcoord = ? WHERE nodeID = ?");
      pstmt.setInt(1, i);
      pstmt.setString(2, nodeID);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean setY(String nodeID, int i, Campus c) {
    String tblName = getCampusNode(c);
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement("UPDATE " + tblName + " SET ycoord = ? WHERE nodeID = ?");
      pstmt.setInt(1, i);
      pstmt.setString(2, nodeID);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean setFloor(String nodeID, int i, Campus c) {
    String tblName = getCampusNode(c);
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("UPDATE " + tblName + " SET floor = ? WHERE nodeID = ?");
      pstmt.setInt(1, i);
      pstmt.setString(2, nodeID);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean setBuilding(String nodeID, String s, Campus c) {
    String tblName = getCampusNode(c);
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement("UPDATE " + tblName + " SET building = ? WHERE nodeID = ?");
      pstmt.setString(1, s);
      pstmt.setString(2, nodeID);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean setNodeType(String nodeID, String s, Campus c) {
    String tblName = getCampusNode(c);
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement("UPDATE " + tblName + " SET nodeType = ? WHERE nodeID = ?");
      pstmt.setString(1, s);
      pstmt.setString(2, nodeID);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean setLongName(String nodeID, String s, Campus c) {
    String tblName = getCampusNode(c);
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement("UPDATE " + tblName + " SET longName = ? WHERE nodeID = ?");
      pstmt.setString(1, s);
      pstmt.setString(2, nodeID);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean setShortName(String nodeID, String s, Campus c) {
    String tblName = getCampusNode(c);
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement("UPDATE " + tblName + " SET shortName = ? WHERE nodeID = ?");
      pstmt.setString(1, s);
      pstmt.setString(2, nodeID);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean setTeamAssigned(String nodeID, String s, Campus c) {
    String tblName = getCampusNode(c);
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement("UPDATE " + tblName + " SET teamAssigned = ? WHERE nodeID = ?");
      pstmt.setString(1, s);
      pstmt.setString(2, nodeID);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean setStartNode(String edgeID, String s, Campus c) {
    String tblName = getCampusEdge(c);
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement("UPDATE " + tblName + " SET startNode = ? WHERE edgeID = ?");
      pstmt.setString(1, s);
      pstmt.setString(2, edgeID);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean setEndNode(String edgeID, String s, Campus c) {
    String tblName = getCampusNode(c);
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement("UPDATE " + tblName + " SET endNode = ? WHERE edgeID = ?");
      pstmt.setString(1, s);
      pstmt.setString(2, edgeID);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }
}

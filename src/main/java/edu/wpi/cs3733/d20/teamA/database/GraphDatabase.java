package edu.wpi.cs3733.d20.teamA.database;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.List;

public class GraphDatabase extends Database {

  public GraphDatabase(Connection connection) throws SQLException {
    super(connection);
  }

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

  public boolean removeAll() throws SQLException {
    return removeAllEdges() && removeAllNodes();
  }

  public int getSizeNode() throws SQLException {
    int count = 0;
    try {
      PreparedStatement pstmt = getConnection().prepareStatement("Select * From Node");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        count++;
      }
      rset.close();
      pstmt.close();
      return count;
    } catch (SQLException e) {
      return -1;
    }
  }

  public int getSizeEdge() throws SQLException {
    int count = 0;
    try {
      PreparedStatement pstmt = getConnection().prepareStatement("Select * From Edge");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        count++;
      }
      rset.close();
      pstmt.close();
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
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
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
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  public boolean addEdge(String edgeID, String startNode, String endNode) throws SQLException {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement("INSERT INTO Edge (edgeID, startNode, endNode) VALUES (?, ?, ?)");
      pstmt.setString(1, edgeID);
      pstmt.setString(2, startNode);
      pstmt.setString(3, endNode);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  public boolean deleteEdge(String edgeID) throws SQLException {
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("DELETE From Edge Where edgeID = ?");
      pstmt.setString(1, edgeID);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  public boolean deleteNode(String nodeID) throws SQLException {
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("DELETE From Node Where nodeID = ?");
      pstmt.setString(1, nodeID);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  public boolean editNode(
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
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE Node SET xcoord = ?, ycoord = ?, floor = ?, building = ?, nodeType = ?, longName = ?, shortName = ?, teamAssigned = ? WHERE nodeID = ?");
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
    } catch (SQLException e) {
      return false;
    }
  }

  public void readNodeCSV(String fileName) throws IOException, CsvException, SQLException {
    FileReader reads = new FileReader(fileName);
    CSVReader reader = new CSVReader(reads);
    List<String[]> data = reader.readAll();
    for (int i = 1; i < data.size(); i++) {
      String nID, Bu, nodeT, longN, shortN, teamA;
      int xCo, yCo, Fl;
      nID = data.get(i)[0];
      xCo = Integer.parseInt(data.get(i)[1]);
      yCo = Integer.parseInt(data.get(i)[2]);
      Fl = Integer.parseInt(data.get(i)[3]);
      Bu = data.get(i)[4];
      nodeT = data.get(i)[5];
      longN = data.get(i)[6];
      shortN = data.get(i)[7];
      teamA = data.get(i)[8];
      addNode(nID, xCo, yCo, Fl, Bu, nodeT, longN, shortN, teamA);
    }
  }

  public void readEdgeCSV(String fileName) throws IOException, CsvException, SQLException {
    FileReader reads = new FileReader(fileName);
    CSVReader reader = new CSVReader(reads);
    List<String[]> data = reader.readAll();
    for (int i = 1; i < data.size(); i++) {
      String eID, sNode, eNode;
      eID = data.get(i)[0];
      sNode = data.get(i)[1];
      eNode = data.get(i)[2];
      addEdge(eID, sNode, eNode);
    }
  }

  public void writeNodeCSV(String filePath) throws SQLException {
    File file = new File(filePath);
    try {
      FileWriter outputfile = new FileWriter(file);
      CSVWriter writer = new CSVWriter(outputfile);
      PreparedStatement pstmt = getConnection().prepareStatement("SELECT * FROM Node");
      ResultSet rset = pstmt.executeQuery();
      String[] header = {
        "nodeID",
        "xcoord",
        "ycoord",
        "floor",
        "building",
        "nodeType",
        "longName",
        "shortName",
        "teamAssigned"
      };
      writer.writeNext(header);
      while (rset.next()) {
        String ID = rset.getString("nodeID");
        int x = rset.getInt("xcoord");
        String xs = Integer.toString(x);
        int y = rset.getInt("ycoord");
        String ys = Integer.toString(y);
        int f = rset.getInt("floor");
        String fs = Integer.toString(f);
        String build = rset.getString("building");
        String type = rset.getString("nodeType");
        String lName = rset.getString("longName");
        String sName = rset.getString("shortName");
        String teamA = rset.getString("teamAssigned");
        String[] row = {ID, xs, ys, fs, build, type, lName, sName, teamA};
        writer.writeNext(row);
      }
      writer.close();
      rset.close();
      pstmt.close();
    } catch (SQLException e) {
      return;
    } catch (IOException i) {
      return;
    }
  }

  public void writeEdgeCSV(String filePath) throws SQLException {
    File file = new File(filePath);
    try {
      FileWriter outputfile = new FileWriter(file);
      CSVWriter writer = new CSVWriter(outputfile);
      PreparedStatement pstmt = getConnection().prepareStatement("SELECT * FROM Edge");
      ResultSet rset = pstmt.executeQuery();
      String[] header = {"edgeID", "startNode", "endNode"};
      writer.writeNext(header);
      while (rset.next()) {
        String ID = rset.getString("edgeID");
        String sNode = rset.getString("startNode");
        String eNode = rset.getString("endNode");
        String[] row = {ID, sNode, eNode};
        writer.writeNext(row);
      }
      writer.close();
      rset.close();
      pstmt.close();
    } catch (SQLException e) {
      return;
    } catch (IOException i) {
      return;
    }
  }
}

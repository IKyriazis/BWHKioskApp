package edu.wpi.cs3733.d20.teamA.database;

import java.sql.*;
import java.util.ArrayList;

public class InterpreterDatabase extends Database {
  private int requestNum = getSizeRequests() + 1;

  public InterpreterDatabase(Connection connection) {
    super(connection);

    if (doesTableNotExist("INTERPRETERS") && doesTableNotExist("INTERPRETERREQUESTS")) {
      createTables();
    }
  }

  public boolean dropTables() {
    if (!(helperPrepared("DROP TABLE Interpreters")
        && helperPrepared("DROP TABLE InterpreterRequests"))) {
      return false;
    }

    return true;
  }

  public boolean createTables() {
    boolean a =
        helperPrepared(
            "CREATE TABLE Interpreters (name Varchar(64) NOT NULL, language Varchar(64) NOT NULL, PRIMARY KEY (name))");

    // Add dummy interpreters
    addInterpreter("Bob", "Spanish");
    addInterpreter("Bobby", "French");
    addInterpreter("Rob", "German");
    addInterpreter("Robbie", "Italian");
    addInterpreter("Robert", "Portuguese");

    boolean b =
        helperPrepared(
            "CREATE TABLE InterpreterRequests (requestNumber INTEGER PRIMARY KEY, name VARCHAR(64) NOT NULL, language VARCHAR(64) NOT NULL, location VARCHAR(10) NOT NULL, status VARCHAR(64) NOT NULL, CONSTRAINT FK_INID FOREIGN KEY (location) REFERENCES Node(nodeID), CONSTRAINT FK_RN FOREIGN KEY (name) REFERENCES Interpreters(name))");

    // Add dummy request
    addRequest("Bobby", "French", "AINFO00101", "Submitted");

    return a && b;
  }

  public boolean addInterpreter(String name, String language) {
    if (name.isEmpty() || language.isEmpty()) {
      return false;
    }

    name = name.substring(0, Math.min(64, name.length()));
    language = language.substring(0, Math.min(64, language.length()));

    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement("INSERT INTO Interpreters (name, language) VALUES (?, ?)");
      pstmt.setString(1, name);
      pstmt.setString(2, language);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean deleteInterpreter(String name) {
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("DELETE From Interpreters WHERE name = '" + name + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public int addRequest(String name, String language, String location, String status) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "INSERT INTO InterpreterRequests (requestNumber, name, language, location, status) VALUES (?, ?, ?, ?, ?)");
      pstmt.setInt(1, requestNum++);
      pstmt.setString(2, name);
      pstmt.setString(3, language);
      pstmt.setString(4, location);
      pstmt.setString(5, status);
      pstmt.executeUpdate();
      pstmt.close();
      return requestNum - 1;
    } catch (SQLException e) {
      e.printStackTrace();
      return 0;
    }
  }

  public ArrayList<Interpreter> getInterpreters() {
    ArrayList<Interpreter> interpreters = new ArrayList<>();
    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
      PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Interpreters");
      ResultSet set = stmt.executeQuery();
      while (set.next()) {
        String name = set.getString("name");
        String language = set.getString("language");

        interpreters.add(new Interpreter(name, language));
      }
      set.close();
      stmt.close();
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return interpreters;
  }

  public ArrayList<InterpreterRequest> getRequests() {
    ArrayList<InterpreterRequest> requests = new ArrayList<>();
    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
      PreparedStatement stmt = conn.prepareStatement("SELECT * FROM InterpreterRequests");
      ResultSet set = stmt.executeQuery();
      while (set.next()) {
        int num = set.getInt("requestNumber");
        String name = set.getString("name");
        String language = set.getString("language");
        String location = set.getString("location");
        String status = set.getString("status");

        requests.add(new InterpreterRequest(num, name, language, location, status));
      }
      set.close();
      stmt.close();
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return requests;
  }

  public int getSizeRequests() {
    if (doesTableNotExist("INTERPRETERREQUESTS")) {
      return 0;
    } else {
      return getSize("InterpreterRequests");
    }
  }
}

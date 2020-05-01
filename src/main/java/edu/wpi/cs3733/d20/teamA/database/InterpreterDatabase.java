package edu.wpi.cs3733.d20.teamA.database;

import java.sql.*;
import java.util.ArrayList;

public class InterpreterDatabase extends Database {

  public InterpreterDatabase(Connection connection) {
    super(connection);

    if (doesTableNotExist("INTERPRETERS") && doesTableNotExist("INTERPRETERREQUESTS")) {
      createTables();
    }
  }

  public boolean dropTables() {
    if (!(helperPrepared("DROP TABLE InterpreterRequests")
        && helperPrepared("DROP TABLE Interpreters"))) {
      return false;
    }

    return true;
  }

  public boolean createTables() {
    boolean a =
        helperPrepared(
            "CREATE TABLE Interpreters (name Varchar(64) NOT NULL, language Varchar(64) NOT NULL, PRIMARY KEY (name))");

    boolean b =
        helperPrepared(
            "CREATE TABLE InterpreterRequests (requestID VARCHAR(6) PRIMARY KEY, name VARCHAR(64) NOT NULL, language VARCHAR(64) NOT NULL, location VARCHAR(10) NOT NULL, status VARCHAR(64) NOT NULL, CONSTRAINT FK_INID FOREIGN KEY (location) REFERENCES Node(nodeID), CONSTRAINT FK_RN FOREIGN KEY (name) REFERENCES Interpreters(name))");

    return a && b;
  }

  public boolean addDummyInterpreters() {
    // Add dummy interpreters
    boolean a = addInterpreter("Bob", "Spanish");
    boolean b = addInterpreter("Bobby", "French");
    boolean c = addInterpreter("Rob", "German");
    boolean d = addInterpreter("Robbie", "Italian");
    boolean e = addInterpreter("Robert", "Portuguese");

    return a && b && c && d && e;
  }

  public boolean addDummyRequests() {
    // Add dummy request
    return (addRequest("Bobby", "French", "AINFO00101", "Submitted") != "");
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

  public String addRequest(String name, String language, String location, String status) {
    String requestID = getRandomString();
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "INSERT INTO InterpreterRequests (requestNumber, name, language, location, status) VALUES (?, ?, ?, ?, ?)");
      pstmt.setString(1, requestID);
      pstmt.setString(2, name);
      pstmt.setString(3, language);
      pstmt.setString(4, location);
      pstmt.setString(5, status);
      pstmt.executeUpdate();
      pstmt.close();
      return requestID;
    } catch (SQLException e) {
      e.printStackTrace();
      return "";
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

        // requests.add(new InterpreterRequest(num, name, language, location, status));
      }
      set.close();
      stmt.close();
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return requests;
  }

  public boolean updateRequestStatus(int id, String status) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE InterpreterRequests SET status = '"
                      + status
                      + "' WHERE requestNumber = "
                      + id);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public int getSizeRequests() {
    if (doesTableNotExist("INTERPRETERREQUESTS")) {
      return 0;
    } else {
      return getSize("InterpreterRequests");
    }
  }

  public int getSizeInterpreters() {
    if (doesTableNotExist("INTERPRETERS")) {
      return 0;
    } else {
      return getSize("Interpreters");
    }
  }
}

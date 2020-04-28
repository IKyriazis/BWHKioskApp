package edu.wpi.cs3733.d20.teamA.database;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class EmployeesDatabase extends Database {
  int employeeID;
  private final int numIterations = 14; // 2 ^ 16 = 16384 iterations

  public EmployeesDatabase(Connection connection) {

    super(connection);

    if (doesTableNotExist("EMPLOYEES")
        && doesTableNotExist("EquipReq")
        && doesTableNotExist("LoggedIn")) {
      createTables();
    }

    employeeID = getSizeEmployees() + 1;
  }

  /**
   * Drop the 'Employees' table
   *
   * @return Success / Failure
   */
  public synchronized boolean dropTables() {

    // Drop the tables
    if (!(helperPrepared("ALTER TABLE EquipReq DROP CONSTRAINT FK_EID")
        && helperPrepared("ALTER TABLE LoggedIn DROP CONSTRAINT FK_USE"))) {

      return false;
    }
    // Drop the tables
    if (!(helperPrepared("DROP TABLE Employees")
        && helperPrepared("DROP TABLE EquipReq")
        && helperPrepared("DROP TABLE LoggedIn"))) {
      return false;
    }

    return true;
  }

  /**
   * Creates graph tables
   *
   * @return False if tables couldn't be created
   */
  public synchronized boolean createTables() {

    // Create the graph tables

    boolean a =
        helperPrepared(
            "CREATE TABLE Employees (employeeID INTEGER PRIMARY KEY, nameFirst Varchar(25), nameLast Varchar(25), username Varchar(25) UNIQUE NOT NULL, password Varchar(60) NOT NULL, title Varchar(50))");
    boolean b =
        helperPrepared(
            "CREATE TABLE EquipReq (username Varchar(25), timeOf TIMESTAMP, item Varchar(75) NOT NULL, qty INTEGER, location Varchar(10) NOT NULL, priority Varchar(7) NOT NULL, CONSTRAINT CK_Q CHECK (qty >= 0), CONSTRAINT FK_EID FOREIGN KEY (username) REFERENCES Employees(username), CONSTRAINT PK_ET PRIMARY KEY(username, timeOf), CONSTRAINT CHK_PRI CHECK (priority in ('High', 'Medium', 'Low')), CONSTRAINT FK_NLOC FOREIGN KEY (location) REFERENCES NODE(nodeID))");
    boolean c =
        helperPrepared(
            "CREATE TABLE LoggedIn (username Varchar(25), timeLogged TIMESTAMP, flag BOOLEAN, CONSTRAINT FK_USE FOREIGN KEY (username) REFERENCES Employees (username), CONSTRAINT PK_UST PRIMARY KEY (username, timeLogged))");
    return a && b && c;
  }

  /**
   * @param nameFirst nameFirst
   * @param nameLast last name
   * @return returns true if the employee is added
   */
  public synchronized boolean addEmployee(
      int employeeID,
      String nameFirst,
      String nameLast,
      String username,
      String password,
      String title) {
    String storedPassword =
        BCrypt.withDefaults().hashToString(numIterations, password.toCharArray());

    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "INSERT INTO Employees (employeeID, nameFirst, nameLast, username, password, title) VALUES (?, ?, ?, ?, ?, ?)");
      pstmt.setInt(1, employeeID);
      pstmt.setString(2, nameFirst);
      pstmt.setString(3, nameLast);
      pstmt.setString(4, username);
      pstmt.setString(5, storedPassword);
      pstmt.setString(6, title);
      pstmt.executeUpdate();
      pstmt.close();
      this.employeeID++;
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // returns true if the username isn't in the database
  public synchronized boolean uNameExists(String uName) {
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("SELECT * FROM Employees WHERE username = ?");
      pstmt.setString(1, uName);
      ResultSet rset = pstmt.executeQuery();
      rset.next();
      String uNameFromTable;
      uNameFromTable = rset.getString("username");
      pstmt.close();
      return uName.equals(uNameFromTable);
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public synchronized boolean addEmployeeNoChecks(
      String nameFirst, String nameLast, String username, String password, String title) {
    return addEmployee(getSizeEmployees() + 1, nameFirst, nameLast, username, password, title);
  }

  public synchronized boolean addEmployee(
      String nameFirst, String nameLast, String username, String password, String title) {
    return checkSecurePass(password)
        && addEmployee(employeeID, nameFirst, nameLast, username, password, title);
  }

  /**
   * Removes a janitor of empID from the Janitor's table
   *
   * @return true if the Janitor was successfully deleted
   */
  public synchronized boolean deleteEmployee(String username) {
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("DELETE From Employees Where username = ?");
      pstmt.setString(1, username);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /** @return returns the size of the table */
  public synchronized int getSizeEmployees() {
    return getSize("Employees");
  }

  /**
   * @param newTitle newTitle
   * @return true if the title is changed
   */
  public synchronized boolean editTitle(String username, String newTitle) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE Employees SET title = '"
                      + newTitle
                      + "' WHERE username = '"
                      + username
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public synchronized boolean editNameFirst(String username, String newFirst) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE Employees SET title = '"
                      + newFirst
                      + "' WHERE username = '"
                      + username
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public synchronized boolean editNameLast(String username, String newLast) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE Employees SET title = '"
                      + newLast
                      + "' WHERE username = '"
                      + username
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public synchronized boolean logIn(String username, String enteredPass) {
    String pass = null;
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "Select password From Employees Where username = '" + username + "'");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        pass = rset.getString("password");
      }
      rset.close();
      pstmt.close();
      if (pass == null) {
        return false;
      }
      BCrypt.Result result = BCrypt.verifyer().verify(enteredPass.toCharArray(), pass);
      return result.verified;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public synchronized String getName(int id) {
    String pass = null;
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("Select * From Employees Where employeeID = " + id);
      ResultSet rset = pstmt.executeQuery();
      String name = "Not found";
      if (rset.next()) {
        String fName = rset.getString("nameFirst");
        String lName = rset.getString("nameLast");
        name = fName + " " + lName;
      }
      rset.close();
      pstmt.close();
      return name;
    } catch (SQLException e) {
      e.printStackTrace();
      return "Not found";
    }
  }

  public synchronized boolean changePassword(String username, String oldPass, String newPass) {

    if (logIn(username, oldPass) && checkSecurePass(newPass)) {
      String storedPass = BCrypt.withDefaults().hashToString(numIterations, newPass.toCharArray());
      try {
        PreparedStatement pstmt =
            getConnection()
                .prepareStatement(
                    "UPDATE Employees SET password = '"
                        + storedPass
                        + "' WHERE username = '"
                        + username
                        + "'");
        pstmt.executeUpdate();
        pstmt.close();
        return true;
      } catch (SQLException e) {
        e.printStackTrace();
        return false;
      }
    }

    return false;
  }

  /**
   * @param password password
   * @return true if the there is a scure pass
   */
  public synchronized boolean checkSecurePass(String password) {
    char ch;
    boolean capital = false;
    boolean lowercase = false;
    boolean number = false;
    if (password.length() > 72) {
      return false;
    }
    for (int i = 0; i < password.length(); i++) {
      ch = password.charAt(i);
      if (Character.isDigit(ch)) {
        number = true;
      } else if (Character.isUpperCase(ch)) {
        capital = true;
      } else if (Character.isLowerCase(ch)) {
        lowercase = true;
      }

      if (number && capital && lowercase) return true;
    }
    return false;
  }

  /**
   * Gets all employees in the table
   *
   * @return an observable list containing all employees in the table
   */
  public synchronized ObservableList<Employee> employeeOl() {
    ObservableList<Employee> eList = FXCollections.observableArrayList();
    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
      // CREATE TABLE Employees (employeeID INTEGER PRIMARY KEY, nameFirst Varchar(25), nameLast
      // Varchar(25), username Varchar(25) UNIQUE NOT NULL, password Varchar(25) NOT NULL, title
      // Varchar(50))"
      PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Employees");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        int id = rset.getInt("employeeID");
        String fName = rset.getString("nameFirst");
        String lName = rset.getString("nameLast");
        String title = rset.getString("title");
        Employee e = new Employee(id, fName, lName, title);
        eList.add(e);
      }
      rset.close();
      pstmt.close();
      conn.close();
      return eList;
    } catch (SQLException e) {
      e.printStackTrace();
      return eList;
    }
  }

  /** @return true if all all employee are removed */
  public synchronized boolean removeAllEmployees() {
    return helperPrepared("DELETE From Employees");
  }

  public synchronized void readEmployeeCSV() {
    try {
      InputStream stream =
          getClass().getResourceAsStream("/edu/wpi/cs3733/d20/teamA/csvfiles/Employees.csv");
      CSVReader reader = new CSVReader(new InputStreamReader(stream));
      List<String[]> data = reader.readAll();
      for (int i = 1; i < data.size(); i++) {
        String nameFirst, nameLast, username, password, title;
        int employeeID;
        employeeID = Integer.parseInt(data.get(i)[0]);
        nameFirst = data.get(i)[1];
        nameLast = data.get(i)[2];
        username = data.get(i)[3];
        password = data.get(i)[4];
        title = data.get(i)[5];
        addEmployee(employeeID, nameFirst, nameLast, username, password, title);
      }
    } catch (IOException | CsvException e) {
      e.printStackTrace();
    }
  }

  public String getLoggedIn() {
    String username = null;
    try {
      PreparedStatement pstm =
          getConnection().prepareStatement("Select username From LoggedIn Where flag = true");
      ResultSet rset = pstm.executeQuery();
      while (rset.next()) {
        username = rset.getString("username");
      }
      rset.close();
      pstm.close();
      if (username != null) {
        return username;
      }
      return null;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public boolean addReq(String item, int qty, String location, String priority) {
    String username = getLoggedIn();
    Timestamp timeOf = new Timestamp(System.currentTimeMillis());
    if (username == null) {
      return false;
    }
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "INSERT INTO EquipReq (username, timeOf, item, qty, location, priority) VALUES (?, ?, ?, ?, ?, ?)");
      pstmt.setString(1, username);
      pstmt.setTimestamp(2, timeOf);
      pstmt.setString(3, item);
      pstmt.setInt(4, qty);
      pstmt.setString(5, location);
      pstmt.setString(6, priority);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean deleteReq(String username, String timeOf) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "DELETE From EquipReq Where username = '"
                      + username
                      + "' AND timeOf = '"
                      + timeOf
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean addLog(String username) {
    Timestamp timeOf = new Timestamp(System.currentTimeMillis());

    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "INSERT INTO LoggedIn (username, timeLogged, flag) VALUES (?, ?, ?)");
      pstmt.setString(1, username);
      pstmt.setTimestamp(2, timeOf);
      pstmt.setBoolean(3, true);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean changeFlag() {

    String username = getLoggedIn();

    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE LoggedIn set flag = false WHERE username = '" + username + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public int getSizeReq() {
    return getSize("EquipReq");
  }

  public int getSizeLog() {
    return getSize("LoggedIn");
  }

  public boolean removeAllReqs() {
    return helperPrepared("DELETE From EquipReq");
  }

  public boolean removeAllLogs() {
    return helperPrepared("DELETE From LoggedIn");
  }

  public String getNamefromUser(String username) {
    String last;
    try {
      Statement priceStmt = getConnection().createStatement();
      ResultSet rst =
          priceStmt.executeQuery("SELECT * FROM Employees WHERE username = '" + username + "'");
      ;
      rst.next();
      last = rst.getString("nameLast");
      return last;
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    return null;
  }

  public String getTime(String username) {
    Timestamp timeOf;
    try {
      Statement priceStmt = getConnection().createStatement();
      ResultSet rst =
          priceStmt.executeQuery("SELECT * FROM EquipReq WHERE username = '" + username + "'");
      ;
      rst.next();
      timeOf = rst.getTimestamp("timeOf");
      return timeOf.toString();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    return null;
  }

  public boolean isOnline(String username) {
    boolean isOnline = false;
    try {
      Statement priceStmt = getConnection().createStatement();
      ResultSet rst =
          priceStmt.executeQuery("SELECT * FROM LoggedIn WHERE username = '" + username + "'");
      ;
      rst.next();
      if (rst.getBoolean("flag")) return true;
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    return false;
  }

  public ObservableList<EquipRequest> ReqOl() {
    ObservableList<EquipRequest> rList = FXCollections.observableArrayList();
    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
      PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM EquipReq");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        String name = getNamefromUser(rset.getString("username"));
        Timestamp timeOf = rset.getTimestamp("timeOF");
        String item = rset.getString("item");
        int qty = rset.getInt("qty");
        String location = rset.getString("location");
        String priority = rset.getString("priority");
        String username = rset.getString("username");

        EquipRequest node = new EquipRequest(name, item, qty, location, priority, timeOf, username);

        rList.add(node);
      }
      rset.close();
      pstmt.close();
      conn.close();
      return rList;
    } catch (SQLException e) {
      e.printStackTrace();
      return rList;
    }
  }
}

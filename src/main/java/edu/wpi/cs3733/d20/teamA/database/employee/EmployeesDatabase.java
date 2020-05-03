package edu.wpi.cs3733.d20.teamA.database.employee;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import edu.wpi.cs3733.d20.teamA.database.Database;
import edu.wpi.cs3733.d20.teamA.database.IDatabase;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.*;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.codec.binary.Base32;

public class EmployeesDatabase extends Database implements IDatabase<Employee> {
  private final int numIterations = 14; // 2 ^ 16 = 16384 iterations

  public EmployeesDatabase(Connection connection) {

    super(connection);

    createTables();
  }

  /**
   * Drop the 'Employees' table
   *
   * @return Success / Failure
   */
  public synchronized boolean dropTables() {

    // Drop the tables
    if (!(doesTableNotExist("EMPLOYEES"))) {
      return helperPrepared("DROP TABLE Employees");
    }
    return false;
  }

  /**
   * Creates graph tables
   *
   * @return False if tables couldn't be created
   */
  public synchronized boolean createTables() {

    if (doesTableNotExist("EMPLOYEES")) {
      return helperPrepared(
          "CREATE TABLE Employees (employeeID VARCHAR(6) PRIMARY KEY,"
              + " nameFirst Varchar(25), nameLast Varchar(25),"
              + " username Varchar(25) UNIQUE NOT NULL,"
              + " password Varchar(60) NOT NULL, title Varchar(50), secretKey Varchar(32), pagerNum BigInt NOT NULL, "
              + "CONSTRAINT Check_Title CHECK (title in ('admin', 'doctor', 'nurse', 'janitor', 'interpreter', 'receptionist', 'retail')), CONSTRAINT CK_PG CHECK (pagerNum BETWEEN 1000000000 and 9999999999))");
    }
    return false;
  }

  /**
   * @param nameFirst nameFirst
   * @param nameLast last name
   * @return returns true if the employee is added
   */
  public synchronized String addEmployee(
          String employeeID,
          String nameFirst,
          String nameLast,
          String username,
          String password,
          EmployeeTitle title, Long pagerNum) {
    String storedPassword =
        BCrypt.withDefaults().hashToString(numIterations, password.toCharArray());

    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "INSERT INTO Employees (employeeID, nameFirst, nameLast," +
                          " username, password, title, pagerNum)" +
                          " VALUES (?, ?, ?, ?, ?, ?, ?)");
      pstmt.setString(1, employeeID);
      pstmt.setString(2, nameFirst);
      pstmt.setString(3, nameLast);
      pstmt.setString(4, username);
      pstmt.setString(5, storedPassword);
      pstmt.setObject(6, title);
      pstmt.setObject(7, pagerNum);
      pstmt.executeUpdate();
      pstmt.close();
      return employeeID;
    } catch (SQLException e) {
      e.printStackTrace();
      return "";
    }
  }

  // copied from medium to create google authenticator secret key
  public static String generateSecretKey() {
    SecureRandom random = new SecureRandom();
    byte[] bytes = new byte[20];
    random.nextBytes(bytes);
    Base32 base32 = new Base32();
    return base32.encodeToString(bytes);
  }

  /**
   * @param nameFirst nameFirst
   * @param nameLast last name
   * @return returns true if the employee is added
   */
  public synchronized String addEmployeeGA(
      String nameFirst, String nameLast, String username, String password, EmployeeTitle title) {
    String storedPassword =
        BCrypt.withDefaults().hashToString(numIterations, password.toCharArray());
    String secretKey = generateSecretKey();

    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "INSERT INTO Employees (employeeID, nameFirst, nameLast, username, password, title, secretKey) VALUES (?, ?, ?, ?, ?, ?, ?)");
      pstmt.setString(1, getRandomString());
      pstmt.setString(2, nameFirst);
      pstmt.setString(3, nameLast);
      pstmt.setString(4, username);
      pstmt.setString(5, storedPassword);
      pstmt.setString(6, title.toString());
      pstmt.setString(7, secretKey);
      pstmt.executeUpdate();
      pstmt.close();
      return secretKey;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  // get the secret key (used for google authenticator) of the specified user
  public synchronized String getSecretKey(String uname) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement("Select secretKey From Employees Where username = '" + uname + "'");
      ResultSet rset = pstmt.executeQuery();
      String secretKey = "";
      if (rset.next()) {
        secretKey = rset.getString("secretKey");
      }
      rset.close();
      pstmt.close();
      return secretKey;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public synchronized String getEmployeeID(String username) {
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("SELECT * FROM Employees WHERE username = ?");
      pstmt.setString(1, username);
      ResultSet rset = pstmt.executeQuery();
      rset.next();
      String id = rset.getString("employeeID");
      rset.close();
      pstmt.close();
      return id;

    } catch (SQLException e) {
      e.printStackTrace();
      return "";
    }
  }

  // returns true if the username isn't in the database
  public synchronized boolean uNameExists(String uName) {
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("SELECT * FROM Employees WHERE username = ?");
      pstmt.setString(1, uName);
      ResultSet rset = pstmt.executeQuery();
      if (rset.next()) {
        return true;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return false;
  }


  public synchronized String addEmployeeNoChecks(
      String nameFirst, String nameLast, String username, String password, EmployeeTitle title, Long pagerNum) {
    return addEmployee(getRandomString(), nameFirst, nameLast, username, password, title, pagerNum);
  }

  public synchronized String addEmployee(
      String nameFirst, String nameLast, String username, String password, EmployeeTitle title, Long pagerNum) {
    if (checkSecurePass(password)) {
      return addEmployee(getRandomString(), nameFirst, nameLast, username, password, title, pagerNum);
    } else {
      return "";
    }
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
  public synchronized int getSize() {
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
      loggedIn = username;
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
  public synchronized ObservableList<Employee> getObservableList() {
    ObservableList<Employee> eList = FXCollections.observableArrayList();
    try {
      PreparedStatement pstmt = getConnection().prepareStatement("SELECT * FROM Employees");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        String id = rset.getString("employeeID");
        String fName = rset.getString("nameFirst");
        String lName = rset.getString("nameLast");
        String title = rset.getString("title");
        String username = rset.getString("username");
        Employee e =
            new Employee(id, fName, lName, EmployeeTitle.valueOf(title.toUpperCase()), username);
        eList.add(e);
      }
      rset.close();
      pstmt.close();
      return eList;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }


  /** @return true if all all employee are removed */
  public synchronized boolean removeAllEmployees() {
    return helperPrepared("DELETE From Employees");
  }

  /*
  public synchronized void readEmployeeCSV() {
    try {
      InputStream stream =
          getClass().getResourceAsStream("/edu/wpi/cs3733/d20/teamA/csvfiles/Employees.csv");
      CSVReader reader = new CSVReader(new InputStreamReader(stream));
      List<String[]> data = reader.readAll();
      for (int i = 1; i < data.size(); i++) {
        String nameFirst, nameLast, username, password, title;
        String employeeID;
        employeeID = data.get(i)[0];
        nameFirst = data.get(i)[1];
        nameLast = data.get(i)[2];
        username = data.get(i)[3];
        password = data.get(i)[4];
        title = data.get(i)[5];
        addEmployee(
            employeeID,
            nameFirst,
            nameLast,
            username,
            password,
            EmployeeTitle.valueOf(title.toUpperCase()));
      }
    } catch (IOException | CsvException e) {
      e.printStackTrace();
    }
  }

   */

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

    loggedIn = "";
    return true;
  }

  /*public boolean isOnline(String username) {
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
  }*/

  public boolean removeAll() {
    return helperPrepared("DELETE From Employees");
  }

  public String getUsername(int ID) {
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("SELECT * FROM Employees WHERE employeeID = ?");
      pstmt.setInt(1, ID);
      ResultSet rset = pstmt.executeQuery();
      rset.next();
      String un = rset.getString("username");
      rset.close();
      pstmt.close();
      return un;
    } catch (SQLException e) {
      e.printStackTrace();
      return "";
    }
  }

  public Employee findFromUsername(String un) {
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("SELECT * FROM Employees WHERE username = ?");
      pstmt.setString(1, un);
      ResultSet rset = pstmt.executeQuery();
      rset.next();
      String id = rset.getString("employeeID");
      String fName = rset.getString("nameFirst");
      String lName = rset.getString("nameLast");
      String title = rset.getString("title");
      String username = rset.getString("username");
      Employee e =
          new Employee(id, fName, lName, EmployeeTitle.valueOf(title.toUpperCase()), username);
      rset.close();
      pstmt.close();
      return e;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public ObservableList<Employee> getObservableListType(EmployeeTitle title) {
    ObservableList<Employee> eList = FXCollections.observableArrayList();
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("SELECT * FROM Employees WHERE title = ?");
      pstmt.setString(1, title.toString());
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        String id = rset.getString("employeeID");
        String fName = rset.getString("nameFirst");
        String lName = rset.getString("nameLast");
        String type = rset.getString("title");
        String username = rset.getString("username");
        Employee e =
            new Employee(id, fName, lName, EmployeeTitle.valueOf(type.toUpperCase()), username);
        eList.add(e);
      }
      rset.close();
      pstmt.close();
      return eList;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }
}

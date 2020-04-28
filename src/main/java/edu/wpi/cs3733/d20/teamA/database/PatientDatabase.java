package edu.wpi.cs3733.d20.teamA.database;

import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PatientDatabase extends Database {

  private int patientID;

  public PatientDatabase(Connection connection) {
    super(connection);

    if (doesTableNotExist("PATIENTS")) {
      createTables();
    }

    patientID = getRandomNumber();
  }

  /**
   * Drops the tables so we can start fresh
   *
   * @return false if the tables don't exist and CONSTRAINT can't be dropped, true if CONSTRAINT and
   *     tables are dropped correctly
   */
  public boolean dropTables() {

    // Drop the tables
    if (!(helperPrepared("DROP TABLE Patients"))) {
      return false;
    }

    return true;
  }

  /**
   * Creates table
   *
   * @return False if tables couldn't be created
   */
  public boolean createTables() {

    // Create the graph tables
    boolean a =
        helperPrepared(
            "CREATE TABLE Patients (patientID INTEGER PRIMARY KEY, firstName Varchar(15), lastName Varchar(15), healthInsurance Varchar(30), dateOfBirth Varchar(15), CONSTRAINT IDNotNegative CHECK (patientID >= 0))");
    return a;
  }

  /**
   * Gets how many entries are in the patients table
   *
   * @return the number of entries in the patients table
   */
  public int getSizePatients() {
    return getSize("Patients");
  }

  /**
   * removes all patients from the table
   *
   * @return true if all the patients were removed, false otherwise
   */
  public boolean removeAllPatients() {
    return helperPrepared("DELETE From Patients");
  }

  /**
   * Gets all patients in the flower table in an observable list
   *
   * @return an observable list containing all the flowers in the table
   */
  public ObservableList<Patient> patientOl() {
    ObservableList<Patient> oList = FXCollections.observableArrayList();
    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
      PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Patients");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        int patientID = rset.getInt("patientID");
        String firstName = rset.getString("firstName");
        String lastName = rset.getString("lastName");
        String healthInsurance = rset.getString("healthInsurance");
        String dateOfBirth = rset.getString("dateOfBirth");

        Patient node = new Patient(patientID, firstName, lastName, healthInsurance, dateOfBirth);
        oList.add(node);
      }
      rset.close();
      pstmt.close();
      conn.close();
      return oList;
    } catch (SQLException e) {
      e.printStackTrace();
      return oList;
    }
  }

  public int addPatient(
      int patientID,
      String firstName,
      String lastName,
      String healthInsurance,
      String dateOfBirth) {

    try {
      int num = patientID;
      while (idInUse(num)) {
        num++;
      }

      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "INSERT INTO Patients (patientID, firstName, lastName, healthInsurance, dateOfBirth) VALUES (?, ?, ?, ?, ?)");
      pstmt.setInt(1, num);
      pstmt.setString(2, firstName);
      pstmt.setString(3, lastName);
      pstmt.setString(4, healthInsurance);
      pstmt.setString(5, dateOfBirth);
      pstmt.executeUpdate();
      pstmt.close();

      return patientID;
    } catch (SQLException e) {
      e.printStackTrace();
      return -1;
    }
  }

  /**
   * Check to ensure that no duplicate IDs are present in the database
   *
   * @param num
   * @return
   */
  private boolean idInUse(int num) {
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("Select * From Patients Where patientID = " + num);

      ResultSet rset = pstmt.executeQuery();
      if (rset.next()) return true;

      pstmt.close();
      return false;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public int addPatient(
      String firstName, String lastName, String healthInsurance, String dateOfBirth) {
    patientID = getRandomNumber();

    return addPatient(this.patientID, firstName, lastName, healthInsurance, dateOfBirth);
  }

  public boolean updatePatient(
      int patientID, String firstName, String lastName, String newHealthIns, String dateOfBirth) {

    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE Patients SET healthInsurance = '"
                      + newHealthIns
                      + "', firstName = '"
                      + firstName
                      + "', lastName = '"
                      + lastName
                      + "', dateOfBirth = '"
                      + dateOfBirth
                      + "' WHERE patientID = "
                      + patientID);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean deletePatient(int id) {
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("DELETE From Patients WHERE patientID = " + id);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }
}

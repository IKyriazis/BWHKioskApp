package edu.wpi.cs3733.d20.teamA.database;

import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PatientDatabase extends Database implements IDatabase<Patient> {

  public PatientDatabase(Connection connection) {
    super(connection);

    if (doesTableNotExist("PATIENTS")) {
      createTables();
    }
  }

  /**
   * Drops the tables so we can start fresh
   *
   * @return false if the tables don't exist and CONSTRAINT can't be dropped, true if CONSTRAINT and
   *     tables are dropped correctly
   */
  public boolean dropTables() {

    // Drop the tables
    return helperPrepared("DROP TABLE Patients");
  }

  /**
   * Creates table
   *
   * @return False if tables couldn't be created
   */
  public boolean createTables() {

    // Create the graph tables
    return helperPrepared(
        "CREATE TABLE Patients (patientID VARCHAR(6) PRIMARY KEY, firstName Varchar(15), lastName Varchar(15), healthInsurance Varchar(30), dateOfBirth Varchar(15))");
  }

  /**
   * Gets how many entries are in the patients table
   *
   * @return the number of entries in the patients table
   */
  public int getSize() {
    return getSize("Patients");
  }

  /**
   * removes all patients from the table
   *
   * @return true if all the patients were removed, false otherwise
   */
  public boolean removeAll() {
    return helperPrepared("DELETE From Patients");
  }

  /**
   * Gets all patients in the flower table in an observable list
   *
   * @return an observable list containing all the flowers in the table
   */
  public ObservableList<Patient> getObservableList() {
    ObservableList<Patient> oList = FXCollections.observableArrayList();
    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
      PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Patients");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        String patientID = rset.getString("patientID");
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

  public String addPatient(
      String patientID,
      String firstName,
      String lastName,
      String healthInsurance,
      String dateOfBirth) {

    try {

      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "INSERT INTO Patients (patientID, firstName, lastName, healthInsurance, dateOfBirth) VALUES (?, ?, ?, ?, ?)");
      pstmt.setString(1, patientID);
      pstmt.setString(2, firstName);
      pstmt.setString(3, lastName);
      pstmt.setString(4, healthInsurance);
      pstmt.setString(5, dateOfBirth);
      pstmt.executeUpdate();
      pstmt.close();

      return patientID;
    } catch (SQLException e) {
      e.printStackTrace();
      return "";
    }
  }

  /**
   * Check to ensure that no duplicate IDs are present in the database
   *
   * @return
   */
  private boolean idInUse(String id) {
    return checkIfExistsString("PATIENTS", "patientID", id);
  }

  public String addPatient(
      String firstName, String lastName, String healthInsurance, String dateOfBirth) {

    return addPatient(getRandomString(), firstName, lastName, healthInsurance, dateOfBirth);
  }

  public boolean updatePatient(
      String patientID,
      String firstName,
      String lastName,
      String newHealthIns,
      String dateOfBirth) {

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
                      + "' WHERE patientID = '"
                      + patientID
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean deletePatient(String id) {
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("DELETE From Patients WHERE patientID = '" + id + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }
}

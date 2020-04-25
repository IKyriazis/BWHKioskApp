package edu.wpi.cs3733.d20.teamA.database;

import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PatientDatabase extends Database {
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

    // if the helper returns false this method should too
    // drop the CONSTRAINT first
    if (!(helperPrepared("ALTER TABLE Patients DROP CONSTRAINT FK_fT"))) {
      return false;
    }
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
            "CREATE TABLE Patients (firstName Varchar(15), lastName Varchar(15), healthInsurance Varchar(15), dateOfBirth Varchar(15), heightFeet INTEGER NOT NULL, heightInches INTEGER NOT NULL, weight DOUBLE NOT NULL, symptoms Varchar(20), allergies Varchar(20), currentMeds Varchar(20), CONSTRAINT PK_pat PRIMARY KEY (lastName, dateOfBirth))");
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
        String firstName = rset.getString("firstName");
        String lastName = rset.getString("lastName");
        String healthInsurance = rset.getString("healthInsurance");
        String dateOfBirth = rset.getString("dateOfBirth");
        int heightFeet = rset.getInt("heightFeet");
        int heightInches = rset.getInt("heightInches");
        double weight = rset.getDouble("weight");
        String symptoms = rset.getString("symptoms");
        String allergies = rset.getString("allergies");
        String currentMeds = rset.getString("currentMeds");

        Patient node =
            new Patient(
                firstName,
                lastName,
                healthInsurance,
                dateOfBirth,
                heightFeet,
                heightInches,
                weight,
                symptoms,
                allergies,
                currentMeds);
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
}

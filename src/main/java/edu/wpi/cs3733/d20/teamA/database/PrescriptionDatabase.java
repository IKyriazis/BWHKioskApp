package edu.wpi.cs3733.d20.teamA.database;

import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PrescriptionDatabase extends Database {
  private int prescriptionNum;

  /**
   * Constructor
   *
   * @param connection
   */
  public PrescriptionDatabase(Connection connection) {
    super(connection);

    // Creates the table if it doesn't exist
    if (doesTableNotExist("PRESCRIPTION")) {
      createTables();
    }
    prescriptionNum = getRandomNumber();
  }

  /**
   * Creates the table in the database
   *
   * @return true if completed
   */
  public boolean createTables() {
    return helperPrepared(
        "CREATE TABLE PRESCRIPTION(prescriptionID INTEGER PRIMARY KEY, patientName VARCHAR(50) NOT NULL, prescription VARCHAR(50) NOT NULL, pharmacy VARCHAR(50), dosage VARCHAR(25), numberOfRefills INTEGER NOT NULL, doctorUsername VARCHAR(25) NOT NULL, notes VARCHAR(100), CONSTRAINT CH_NUMREFILL CHECK(numberOfRefills >= 0))");
  }

  /**
   * Deletes the table from the database
   *
   * @return true if completed
   */
  public boolean dropTables() {
    return helperPrepared("DROP TABLE PRESCRIPTION");
  }

  /**
   * Removes all info in the database
   *
   * @return
   */
  public boolean removeAllPrescriptions() {
    return helperPrepared("DELETE From PRESCRIPTION");
  }

  /**
   * Returns the size of the table
   *
   * @return int size
   */
  public int getSizePrescription() {
    return getSize("PRESCRIPTION");
  }

  /*
  Adds a prescription to the table
   */
  public int addPrescription(
      int prescriptionNum,
      String patient,
      String prescription,
      String pharmacy,
      String dosage,
      int numRefills,
      String notes) {

    String username = getLoggedIn();

    String name = getNamefromUser(username);

    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "INSERT INTO PRESCRIPTION (prescriptionID, patientName, prescription, pharmacy, dosage, numberOfRefills, doctorUsername, notes) VALUES (?, ?, ?, ?,?,?,?,?)");
      pstmt.setInt(1, prescriptionNum);
      pstmt.setString(2, patient);
      pstmt.setString(3, prescription);
      pstmt.setString(4, pharmacy);
      pstmt.setString(5, dosage);
      pstmt.setInt(6, numRefills);
      pstmt.setString(7, name);
      pstmt.setString(8, notes);
      pstmt.executeUpdate();
      pstmt.close();
      return prescriptionNum;
    } catch (SQLException e) {
      e.printStackTrace();
      return -1;
    }
  }

  /** Adds a prescription without a prescription number */
  public int addPrescription(
      String patient,
      String prescription,
      String pharmacy,
      String dosage,
      int numRefills,
      String notes) {
    prescriptionNum = getRandomNumber();
    return addPrescription(
        this.prescriptionNum, patient, prescription, pharmacy, dosage, numRefills, notes);
  }

  public String getPrescription(String patientName) {
    String prescription;
    try {
      Statement stmt = getConnection().createStatement();
      ResultSet rst =
          stmt.executeQuery("SELECT * FROM PRESCRIPTION WHERE patientName = '" + patientName + "'");
      rst.next();
      prescription = rst.getString("prescription");
      return prescription;
    } catch (SQLException e) {
      e.printStackTrace();
      return "";
    }
  }

  public String getPharmacy(String patient) {
    String pharmacy;
    try {
      Statement stmt = getConnection().createStatement();
      ResultSet rst =
          stmt.executeQuery("SELECT * FROM PRESCRIPTION WHERE patientName = '" + patient + "'");
      rst.next();
      pharmacy = rst.getString("pharmacy");
      return pharmacy;
    } catch (SQLException e) {
      e.printStackTrace();
      return "";
    }
  }

  public String getDosage(String patient) {
    String dosage;
    try {
      Statement stmt = getConnection().createStatement();
      ResultSet rst =
          stmt.executeQuery("SELECT * FROM PRESCRIPTION WHERE patientName = '" + patient + "'");
      rst.next();
      dosage = rst.getString("dosage");
      return dosage;
    } catch (SQLException e) {
      e.printStackTrace();
      return "";
    }
  }

  public int getNumRefills(String patient) {
    int numRefills;
    try {
      Statement stmt = getConnection().createStatement();
      ResultSet rst =
          stmt.executeQuery("SELECT * FROM PRESCRIPTION WHERE patientName = '" + patient + "'");
      rst.next();
      numRefills = rst.getInt("numberOfRefills");
      return numRefills;
    } catch (SQLException e) {
      e.printStackTrace();
      return -1;
    }
  }

  public String getNotes(String patient) {
    String notes;
    try {
      Statement stmt = getConnection().createStatement();
      ResultSet rst =
          stmt.executeQuery("SELECT * FROM PRESCRIPTION WHERE patientName = '" + patient + "'");
      rst.next();
      notes = rst.getString("notes");
      return notes;
    } catch (SQLException e) {
      e.printStackTrace();
      return "error";
    }
  }

  public String getDoctor(String patient) {
    String doctor;
    try {
      Statement stmt = getConnection().createStatement();
      ResultSet rst =
          stmt.executeQuery("SELECT * FROM PRESCRIPTION WHERE patientName = '" + patient + "'");
      rst.next();
      doctor = rst.getString("doctorUsername");

      Statement doctorStatement = getConnection().createStatement();
      ResultSet rstD =
          doctorStatement.executeQuery("SELECT * FROM EMPLOYEES WHERE username = '" + doctor + "'");
      rstD.next();
      String doctorName = rstD.getString("nameFirst") + " " + rstD.getString("nameLast");
      return doctorName;
    } catch (SQLException e) {
      e.printStackTrace();
      return "error";
    }
  }

  public String getPatient(int prescriptionID) {
    String patient;
    try {
      Statement stmt = getConnection().createStatement();
      ResultSet rst =
          stmt.executeQuery(
              "SELECT * FROM PRESCRIPTION WHERE prescriptionID = " + prescriptionID + "");
      rst.next();
      patient = rst.getString("patientName");
      return patient;

    } catch (SQLException e) {
      e.printStackTrace();
      return "";
    }
  }

  public boolean setPatient(int prescriptionID, String newPatient) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE PRESCRIPTION SET patientName = '"
                      + newPatient
                      + "' WHERE prescriptionID = "
                      + prescriptionID
                      + "");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean setPrescription(int prescriptionID, String newPrescription) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE PRESCRIPTION SET prescription = '"
                      + newPrescription
                      + "' WHERE prescriptionID = "
                      + prescriptionID
                      + "");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean setPharmacy(int prescriptionID, String newPharmacy) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE PRESCRIPTION SET pharmacy = '"
                      + newPharmacy
                      + "' WHERE prescriptionID = "
                      + prescriptionID
                      + "");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean setDosage(int prescriptionID, String newDosage) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE PRESCRIPTION SET dosage = '"
                      + newDosage
                      + "' WHERE prescriptionID = "
                      + prescriptionID
                      + "");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean setNumberOfRefills(int prescriptionID, int newRefills) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE PRESCRIPTION SET numberOfRefills = "
                      + newRefills
                      + " WHERE prescriptionID = "
                      + prescriptionID
                      + "");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean setDoctorUsername(int prescriptionID, String newDoctorUsername) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE PRESCRIPTION SET doctorUsername = '"
                      + newDoctorUsername
                      + "' WHERE prescriptionID = "
                      + prescriptionID
                      + "");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean setNotes(int prescriptionID, String newNote) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE PRESCRIPTION SET notes = '"
                      + newNote
                      + "' WHERE prescriptionID = "
                      + prescriptionID
                      + "");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean deletePrescription(int prescriptionID) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "DELETE From PRESCRIPTION WHERE prescriptionID = " + prescriptionID);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public ObservableList<Prescription> prescriptionObservableList() {
    ObservableList<Prescription> prescriptionObservableList = FXCollections.observableArrayList();
    try {
      PreparedStatement pstmt = getConnection().prepareStatement("SELECT * FROM PRESCRIPTION");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        int prescriptionID = rset.getInt("prescriptionID");
        String patientName = rset.getString("patientName");
        String prescription = rset.getString("prescription");
        String pharmacy = rset.getString("pharmacy");
        String dosage = rset.getString("dosage");
        int numberOfRefills = rset.getInt("numberOfRefills");
        String doctorUsername = rset.getString("doctorUsername");
        String notes = rset.getString("notes");

        Prescription node =
            new Prescription(
                prescriptionID,
                patientName,
                prescription,
                pharmacy,
                dosage,
                numberOfRefills,
                doctorUsername,
                notes);
        prescriptionObservableList.add(node);
      }
      rset.close();
      pstmt.close();
      return prescriptionObservableList;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }
}

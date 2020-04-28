package edu.wpi.cs3733.d20.teamA.database;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.List;
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
        "CREATE TABLE PRESCRIPTION(prescriptionID INTEGER PRIMARY KEY, patientName VARCHAR(50) NOT NULL, prescription VARCHAR(50) NOT NULL, pharmacy VARCHAR(50), dosage VARCHAR(25), numberOfRefills INTEGER NOT NULL, doctorUsername VARCHAR(25) NOT NULL, notes VARCHAR(100), CONSTRAINT FK_DOCTOR FOREIGN KEY (doctorUsername) REFERENCES Employees(username), CONSTRAINT CH_NUMREFILL CHECK(numberOfRefills >= 0))");
  }

  /**
   * Deletes the table from the database
   *
   * @return true if completed
   */
  public boolean dropTables() {
    if (!(helperPrepared("ALTER TABLE PRESCRIPTION DROP CONSTRAINT FK_DOCTOR"))) {
      return false;
    }

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
  public boolean addPrescription(
      int prescriptionNum,
      String patient,
      String prescription,
      String pharmacy,
      String dosage,
      int numRefills,
      String doctorName,
      String notes) {

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
      pstmt.setString(7, doctorName);
      pstmt.setString(8, notes);
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /** Adds a prescription without a prescription number */
  public boolean addPrescription(
      String patient,
      String prescription,
      String pharmacy,
      String dosage,
      int numRefills,
      String doctorName,
      String notes) {
    prescriptionNum = getRandomNumber();
    return addPrescription(
        this.prescriptionNum,
        patient,
        prescription,
        pharmacy,
        dosage,
        numRefills,
        doctorName,
        notes);
  }

  /** Reads the flower csv file into the database */
  public boolean readPrescriptionCSV() {
    try {
      InputStream stream =
          getClass().getResourceAsStream("/edu/wpi/cs3733/d20/teamA/csvfiles/Prescription.csv");
      CSVReader reader = new CSVReader(new InputStreamReader(stream));
      List<String[]> data = reader.readAll();
      for (int i = 1; i < data.size(); i++) {
        String patient, prescription, pharmacy, dosage, refillPer, doctorName, notes;
        int prescriptionNum, numRefills;
        prescriptionNum = Integer.parseInt(data.get(i)[0]);
        patient = data.get(i)[1];
        prescription = data.get(i)[2];
        pharmacy = data.get(i)[3];
        dosage = data.get(i)[4];
        numRefills = Integer.parseInt(data.get(i)[5]);
        doctorName = data.get(i)[6];
        notes = data.get(i)[7];
        addPrescription(
            prescriptionNum,
            patient,
            prescription,
            pharmacy,
            dosage,
            numRefills,
            doctorName,
            notes);
      }
      return true;
    } catch (IOException | CsvException e) {
      e.printStackTrace();
      return false;
    }
  }

  protected String getPrescription(String patientName) {
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

  protected String getPharmacy(String patient) {
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

  protected String getDosage(String patient) {
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

  protected int getNumRefills(String patient) {
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

  protected String getNotes(String patient) {
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

  protected String getDoctor(String patient) {
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

  protected String getPatient(int prescriptionID) {
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

  protected boolean setPatient(int prescriptionID, String newPatient) {
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

  protected boolean setPrescription(String patient, String newPrescription) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE PRESCRIPTION SET prescription = '"
                      + newPrescription
                      + "' WHERE patientName = '"
                      + patient
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  protected boolean setPharmacy(String patient, String newPharmacy) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE PRESCRIPTION SET pharmacy = '"
                      + newPharmacy
                      + "' WHERE patientName = '"
                      + patient
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  protected boolean setDosage(String patient, String newDosage) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE PRESCRIPTION SET dosage = '"
                      + newDosage
                      + "' WHERE patientName = '"
                      + patient
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  protected boolean setNumberOfRefills(String patient, int newRefills) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE PRESCRIPTION SET numberOfRefills = "
                      + newRefills
                      + " WHERE patientName = '"
                      + patient
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  protected boolean setDoctorUsername(String patient, String newDoctorUsername) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE PRESCRIPTION SET doctorUsername = '"
                      + newDoctorUsername
                      + "' WHERE patientName = '"
                      + patient
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  protected boolean setNotes(String patient, String newNote) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE PRESCRIPTION SET notes = '"
                      + newNote
                      + "' WHERE patientName = '"
                      + patient
                      + "'");
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

package edu.wpi.cs3733.d20.teamA.database;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.List;

public class PrescriptionDatabase extends Database {
  private int prescriptionNum;

  public PrescriptionDatabase(Connection connection) {
    super(connection);

    if (doesTableNotExist("PRESCRIPTION")) {
      createTables();
    }
    prescriptionNum = getSizePrescription() + 1;
  }

  protected boolean createTables() {
    return helperPrepared(
        "CREATE TABLE PRESCRIPTION(prescriptionID INTEGER PRIMARY KEY, patientName VARCHAR(50) NOT NULL, prescription VARCHAR(50) NOT NULL, dosage VARCHAR(25), numberOfRefills INTEGER NOT NULL, refillPer VARCHAR(20), doctorLastName VARCHAR(25) NOT NULL, notes VARCHAR(100), CONSTRAINT FK_DOCTOR FOREIGN KEY (doctorLastName) REFERENCES Employees(nameLast), CONSTRAINT CH_PER CHECK( refillPer in ('DAY', 'WEEK', 'MONTH','YEAR')), CONSTRAINT CH_NUMREFILL CHECK(numberOfRefills > 0))");
  }

  protected boolean dropTables() {
    if (!(helperPrepared("ALTER TABLE PRESCRIPTION DROP CONSTRAINT FK_DOCTOR"))) {
      return false;
    }

    return helperPrepared("DROP TABLE PRESCRIPTION");
  }

  public boolean removeAllPrescriptions() {
    return helperPrepared("DELETE From PRESCRIPTION");
  }

  protected int getSizePrescription() {
    return getSize("PRESCRIPTION");
  }

  protected boolean addPrescription(
      int prescriptionNum,
      String patient,
      String dosage,
      int numRefills,
      String refillPer,
      String doctorName,
      String notes) {
    return false;
  }

  /** Reads the flower csv file into the database */
  protected void readPrescriptionCSV() {
    try {
      InputStream stream =
          getClass().getResourceAsStream("/edu/wpi/cs3733/d20/teamA/csvfiles/Prescription.csv");
      CSVReader reader = new CSVReader(new InputStreamReader(stream));
      List<String[]> data = reader.readAll();
      for (int i = 1; i < data.size(); i++) {
        String patient, prescription, dosage, refillPer, doctorName, notes;
        int prescriptionNum, numRefills;
        prescriptionNum = Integer.parseInt(data.get(i)[0]);
        patient = data.get(i)[1];
        dosage = data.get(i)[2];
        numRefills = Integer.parseInt(data.get(i)[3]);
        refillPer = data.get(i)[4];
        doctorName = data.get(i)[5];
        notes = data.get(i)[6];
        addPrescription(prescriptionNum, patient, dosage, numRefills, refillPer, doctorName, notes);
      }
    } catch (IOException | CsvException e) {
      e.printStackTrace();
    }
  }
}

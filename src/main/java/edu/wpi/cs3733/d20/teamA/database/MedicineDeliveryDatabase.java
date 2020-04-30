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

public class MedicineDeliveryDatabase extends Database {
  private int requestCount;

  public MedicineDeliveryDatabase(Connection connection) {
    super(connection);

    if (doesTableNotExist("MEDICINEREQUEST")) {
      createTables();
    }

    requestCount = getRequestSize();
  }

  /**
   * Creates Medicine Delivery tables
   *
   * @return False if tables couldn't be created
   */
  public boolean createTables() {

    // Create the medicinerequest table
    return helperPrepared(
        "CREATE TABLE MedicineRequest (requestNumber varchar(50) PRIMARY KEY, firstName Varchar(15) NOT NULL, lastName Varchar(15) NOT NULL, doctor Varchar(15) NOT NULL, medicine Varchar(25), roomNumber INTEGER NOT NULL, progress Varchar(19), ho INTEGER DEFAULT -1, mins INTEGER DEFAULT -1, fulfilledBy varchar(15), CONSTRAINT CHK_PROG2 CHECK (progress in ('Prescribed', 'Dispatched', 'Done')))");
  }

  /**
   * Drops the medicinerequest tables so we can start fresh
   *
   * @return false if the tables don't exist, true if table is dropped correctly
   */
  public boolean dropTables() {
    // if the helper returns false this method should too
    return helperPrepared("DROP TABLE MedicineRequest");
  }

  /**
   * Sets the requestCount to 0
   *
   * @return true if everything could be deleted
   */
  public boolean removeAll() {
    requestCount = 0;
    return helperPrepared("DELETE From MedicineRequest");
  }

  public boolean deleteRequest(String requestNum) {
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "DELETE From MedicineRequest WHERE requestNumber = '" + requestNum + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean updateMedicine(String id, String newMed) {

    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE MedicineRequest SET medicine = '"
                      + newMed
                      + "' WHERE requestNumber = '"
                      + id
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean updateDoctor(String id, String newDoc) {

    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE MedicineRequest SET doctor = '"
                      + newDoc
                      + "' WHERE requestNumber = '"
                      + id
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean updateHo(String id, int newHo) {

    if (newHo < 0 || newHo > 23) {
      return false;
    }

    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE MedicineRequest SET ho = "
                      + newHo
                      + " WHERE requestNumber = '"
                      + id
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean updateMins(String id, int newMins) {

    if (newMins < 0 || newMins > 59) {
      return false;
    }

    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE MedicineRequest SET mins = "
                      + newMins
                      + " WHERE requestNumber = '"
                      + id
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean updateFulfilledBy(String id, String fulfilledBy) {

    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE MedicineRequest SET fulfilledBy = '"
                      + fulfilledBy
                      + "' WHERE requestNumber = '"
                      + id
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean updateProgress(String id, String newProg) {

    if (!newProg.equals("Prescribed") && !newProg.equals("Dispatched") && !newProg.equals("Done")) {
      return false;
    }
    try {
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "UPDATE MedicineRequest SET progress = '"
                      + newProg
                      + "' WHERE requestNumber = '"
                      + id
                      + "'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Adds medicine delivery service request given first name, last name, doctor, medicine, and room
   * number
   *
   * @return False if request couldn't be added
   */
  public boolean addRequest(
      String firstName, String lastName, String doctor, String medicine, int roomNumber) {
    // default status is reported
    String progress = "Prescribed";

    try {
      // creates the prepared statement that will be sent to the database
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "INSERT INTO MedicineRequest (requestNumber, firstName, lastName, doctor, medicine, roomNumber, progress) VALUES (?, ?, ?, ?, ?, ?, ?)");
      // sets all the parameters of the prepared statement string
      // requestCount++;
      pstmt.setString(1, firstName + lastName + medicine + roomNumber);
      pstmt.setString(2, firstName);
      pstmt.setString(3, lastName);
      pstmt.setString(4, doctor);
      pstmt.setString(5, medicine);
      pstmt.setInt(6, roomNumber);
      pstmt.setString(7, progress);

      pstmt.executeUpdate();
      pstmt.close();
      // return true if the request is added
      return true;

    } catch (SQLException esq) {
      esq.printStackTrace();
      return false;
    }
  }

  public boolean addRequest(
      String firstName,
      String lastName,
      String doctor,
      String medicine,
      int roomNumber,
      int hour,
      int minute) {
    // default status is reported
    String progress = "Prescribed";

    if (hour < 0 || hour > 23) {
      return false;
    }

    if (minute < 0 || minute > 59) {
      return false;
    }

    try {
      // creates the prepared statement that will be sent to the database
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "INSERT INTO MedicineRequest (requestNumber, firstName, lastName, doctor, medicine, roomNumber, progress, ho, mins) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
      // sets all the parameters of the prepared statement string
      // requestCount++;
      pstmt.setString(1, firstName + lastName + medicine + roomNumber);
      pstmt.setString(2, firstName);
      pstmt.setString(3, lastName);
      pstmt.setString(4, doctor);
      pstmt.setString(5, medicine);
      pstmt.setInt(6, roomNumber);
      pstmt.setString(7, progress);
      pstmt.setInt(8, hour);
      pstmt.setInt(9, minute);

      pstmt.executeUpdate();
      pstmt.close();
      // return true if the request is added
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /** @return False if request couldn't be added */
  public boolean addRequest(
      String firstName,
      String lastName,
      String doctor,
      String medicine,
      int roomNumber,
      int hour,
      int minute,
      String fulfilledBy) {
    // default status is reported
    String progress = "Prescribed";

    if (hour < 0 || hour > 23) {
      return false;
    }

    if (minute < 0 || minute > 59) {
      return false;
    }

    try {
      // creates the prepared statement that will be sent to the database
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "INSERT INTO MedicineRequest (requestNumber, firstName, lastName, doctor, medicine, roomNumber, progress, ho, mins, fulfilledBy) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
      // sets all the parameters of the prepared statement string
      pstmt.setString(1, firstName + lastName + medicine + roomNumber);
      pstmt.setString(2, firstName);
      pstmt.setString(3, lastName);
      pstmt.setString(4, doctor);
      pstmt.setString(5, medicine);
      pstmt.setInt(6, roomNumber);
      pstmt.setString(7, progress);
      pstmt.setInt(8, hour);
      pstmt.setInt(9, minute);
      pstmt.setString(10, fulfilledBy);

      pstmt.executeUpdate();
      pstmt.close();
      // return true if the request is added
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Deletes all the progress that have been completed
   *
   * @return true if requests were deleted
   */
  public boolean deleteDoneRequests() {
    try {
      PreparedStatement pstmt =
          getConnection().prepareStatement("DELETE From MedicineRequest Where progress = 'Done'");
      pstmt.executeUpdate();
      pstmt.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public int getRequestSize() {
    int count = 0;
    try {
      PreparedStatement pstmt = getConnection().prepareStatement("Select * From MedicineRequest");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        count++;
      }
      rset.close();
      pstmt.close();
      return count;
    } catch (SQLException e) {
      e.printStackTrace();
      return -1;
    }
  }

  public void readFromCSV() {
    try {
      InputStream stream =
          getClass().getResourceAsStream("/edu/wpi/cs3733/d20/teamA/csvfiles/MedicineRequests.csv");
      CSVReader reader = new CSVReader(new InputStreamReader(stream));
      List<String[]> data = reader.readAll();
      for (int i = 1; i < data.size(); i++) {
        String num;
        String firstName;
        String lastName;
        String doctor;
        String medicine;
        int roomNumber;
        num = data.get(i)[0];
        firstName = data.get(i)[1];
        lastName = data.get(i)[2];
        doctor = data.get(i)[3];
        medicine = data.get(i)[4];
        roomNumber = Integer.parseInt(data.get(i)[5]);

        addRequest(firstName, lastName, doctor, medicine, roomNumber);
      }
    } catch (IOException | CsvException e) {
      e.printStackTrace();
    }
  }

  public ObservableList<MedRequest> requests() {
    ObservableList<MedRequest> oList = FXCollections.observableArrayList();
    try {
      Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
      PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM MedicineRequest");
      ResultSet rset = pstmt.executeQuery();
      while (rset.next()) {
        String reqNum = rset.getString("requestNumber");
        String fName = rset.getString("firstName");
        String lName = rset.getString("lastName");
        String doctor = rset.getString("doctor");
        String medicine = rset.getString("medicine");
        int room = rset.getInt("roomNumber");
        String progress = rset.getString("progress");
        int ho = rset.getInt("ho");
        int mins = rset.getInt("mins");

        String fulfilledBy = rset.getString("fulfilledBy");

        /*MedRequest node =
        new MedRequest(
            reqNum, fName, lName, doctor, medicine, room, progress, ho, mins, fulfilledBy);*/
        // oList.add(node);
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

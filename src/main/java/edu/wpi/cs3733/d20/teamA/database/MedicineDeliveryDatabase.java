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
  private int requestCount = 0;

  public MedicineDeliveryDatabase(Connection connection) {
    super(connection);

    if (doesTableNotExist("MEDICINEREQUEST")) {
      createTables();
    }
  }

  /**
   * Creates Medicine Delivery tables
   *
   * @return False if tables couldn't be created
   */
  public boolean createTables() {

    // Create the medicinerequest table
    return helperPrepared(
        "CREATE TABLE MedicineRequest (requestNumber INTEGER PRIMARY KEY, firstName Varchar(15) NOT NULL, lastName Varchar(15) NOT NULL, doctor Varchar(15) NOT NULL, medicine Varchar(25), roomNumber INTEGER NOT NULL, progress Varchar(19), timeAdminister TIME, fulfilledBy varchar(15), CONSTRAINT CHK_PROG2 CHECK (progress in ('Prescribed', 'Dispatched', 'Done')))");
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

  /**
   * Adds janitor service request given location, and priority
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
      pstmt.setInt(1, ++requestCount);
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
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Adds janitor service request given location, and priority
   *
   * @return False if request couldn't be added
   */
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
    try {
      // creates the prepared statement that will be sent to the database
      PreparedStatement pstmt =
          getConnection()
              .prepareStatement(
                  "INSERT INTO MedicineRequest (requestNumber, firstName, lastName, doctor, medicine, roomNumber, progress, timeAdminister, fulfilledBy) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
      // sets all the parameters of the prepared statement string
      pstmt.setInt(1, ++requestCount);
      pstmt.setString(2, firstName);
      pstmt.setString(3, lastName);
      pstmt.setString(4, doctor);
      pstmt.setString(5, medicine);
      pstmt.setInt(6, roomNumber);
      pstmt.setString(7, progress);
      pstmt.setString(8, hour + ":" + minute + ":00");
      pstmt.setString(9, fulfilledBy);

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
      PreparedStatement pstmt = getConnection().prepareStatement("Select * From MedicineRequest ");
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
        int num;
        String firstName;
        String lastName;
        String doctor;
        String medicine;
        int roomNumber;
        num = Integer.parseInt(data.get(i)[0]);
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
        int reqNum = rset.getInt("requestNumber");
        String fName = rset.getString("firstName");
        String lName = rset.getString("lastName");
        String doctor = rset.getString("doctor");
        String medicine = rset.getString("medicine");
        int room = rset.getInt("roomNumber");
        String progress = rset.getString("progress");
        String time = rset.getString("timeAdminister");
        String fulfilledBy = rset.getString("fulfilledBy");

        MedRequest node =
            new MedRequest(
                reqNum, fName, lName, doctor, medicine, room, progress, time, fulfilledBy);
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

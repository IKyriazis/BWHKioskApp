package edu.wpi.cs3733.d20.teamA.controllers;

import com.fazecast.jSerialComm.SerialPort;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733.d20.teamA.database.DatabaseServiceProvider;
import edu.wpi.cs3733.d20.teamA.database.OnCallDatabase;
import edu.wpi.cs3733.d20.teamA.database.announcement.AnnouncementDatabase;
import edu.wpi.cs3733.d20.teamA.database.employee.EmployeeTitle;
import edu.wpi.cs3733.d20.teamA.database.employee.EmployeesDatabase;
import edu.wpi.cs3733.d20.teamA.database.graph.GraphDatabase;
import edu.wpi.cs3733.d20.teamA.database.inventory.InventoryDatabase;
import edu.wpi.cs3733.d20.teamA.database.patient.PatientDatabase;
import edu.wpi.cs3733.d20.teamA.database.reservation.ReservationDatabase;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceDatabase;
import edu.wpi.cs3733.d20.teamA.graph.Graph;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.util.NodeAutoCompleteHandler;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.Optional;

public abstract class AbstractController {
  private SerialPort comPort = null;
  public static String companyName = "Amethyst Asgardians";

  private DatabaseServiceProvider provider;
  private Connection conn;

  protected ServiceDatabase serviceDatabase;
  protected InventoryDatabase inventoryDatabase;
  // protected FlowerDatabase flDatabase;
  protected GraphDatabase graphDatabase;
  protected EmployeesDatabase eDB;
  protected OnCallDatabase ocDB;

  protected PatientDatabase patientDatabase; // Not usable as service request table line
  protected AnnouncementDatabase announcementDatabase;
  protected ReservationDatabase reservationDatabase;

  public AbstractController() {
    provider = new DatabaseServiceProvider();
    conn = provider.provideConnection();

    graphDatabase = new GraphDatabase(conn);
    eDB = new EmployeesDatabase(conn);

    inventoryDatabase = new InventoryDatabase(conn);

    patientDatabase = new PatientDatabase(conn);
    announcementDatabase = new AnnouncementDatabase(conn);
    serviceDatabase = new ServiceDatabase(conn);
    ocDB = new OnCallDatabase(conn);
    reservationDatabase = new ReservationDatabase(conn);

    // Uncomment this line to delete the database of food stuff
    // APIController.clearDatabase();

    if (comPort == null) {
      SerialPort[] comPorts = SerialPort.getCommPorts();
      if (comPorts.length > 0) {
        comPort = SerialPort.getCommPorts()[0];
      }
    }

    // Create the employee table if it doesn't exist
    if (eDB.getSize() == -1 || eDB.getSize() == 0) {
      eDB.createTables();
      eDB.addEmployee(
          "111111", "Amethyst", "Asguardians", "admin", "admin", EmployeeTitle.ADMIN, "1122112211");
      eDB.addEmployee(
          "222222", "Yash", "Patel", "staff", "staff", EmployeeTitle.NURSE, "2233223322");
      eDB.addEmployee(
          "Brennan", "Aubuchaun", "baub", "baUb578", EmployeeTitle.INTERPRETER, "1234567890");
      eDB.addEmployee(
          "Cory", "Helmuth", "CLHelmuth77", "!Lov3MyPiano2", EmployeeTitle.NURSE, "3344334433");
      eDB.addEmployee(
          "Eva", "Labbe", "ELLabbe", "CluBP3nGuin3", EmployeeTitle.JANITOR, "4455445544");
      eDB.addEmployee(
          "Dean", "Winchester", "WinDean", "catNipRox2", EmployeeTitle.DOCTOR, "5566556655");
      eDB.addEmployee(
          "Stella", "Simmons", "Ssimmons", "gaLaxY6", EmployeeTitle.RECEPTIONIST, "6677667766");
      eDB.addEmployee(
          "Yolanda", "Daniels", "YDaniels", "SpoodRman3", EmployeeTitle.RETAIL, "7788778877");
      // create account with rfid
      eDB.addEmployeeGA(
          "Ioannis", "Kyriazis", "ioannisky", "Ioannisky1", EmployeeTitle.ADMIN, "7100250198");
    }
  }

  public String scanRFID() {
    try {
      comPort.openPort();
      // throws out stuff that was there before we were ready to read
      byte[] trash = new byte[comPort.bytesAvailable()];
      comPort.readBytes(trash, trash.length);
      while (true) {
        while (comPort.bytesAvailable() != 14) Thread.sleep(20);

        byte[] readBuffer = new byte[comPort.bytesAvailable()];
        int numRead = comPort.readBytes(readBuffer, readBuffer.length);
        String scannedString = new String(readBuffer, "UTF-8");
        String[] scannedArray = scannedString.split(" ");
        if (scannedArray[0].length() != 10) {
          continue;
        }
        if (scannedArray[1].contains("p")) {
          comPort.closePort();
          return scannedArray[0];
        } else {
          comPort.closePort();
          return null;
        }
      }
    } catch (Exception e) {
      comPort.closePort();
      e.printStackTrace();
      return null;
    }
  }

  public void stopRFID() {
    if (comPort != null) {
      comPort.closePort();
    }
  }

  public String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer) {
    try {
      return "otpauth://totp/"
          + URLEncoder.encode(issuer + ":" + account, "UTF-8").replace("+", "%20")
          + "?secret="
          + URLEncoder.encode(secretKey, "UTF-8").replace("+", "%20")
          + "&issuer="
          + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return null;
    }
  }

  protected void setupNodeBox(JFXComboBox<Node> box, javafx.scene.Node toFocus) {
    box.setItems(Graph.getAllValidDestinationList());
    box.getEditor()
        .setOnKeyTyped(
            new NodeAutoCompleteHandler(box, toFocus, Graph.getAllValidDestinationList()));
  }

  protected Node getSelectedNode(JFXComboBox<Node> nodeBox) {
    Optional<Node> selected =
        nodeBox.getItems().stream()
            .filter(node -> node.toString().equals(nodeBox.getEditor().getText()))
            .findFirst();

    return selected.orElse(null);
  }
}

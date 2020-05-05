package edu.wpi.cs3733.d20.teamA.controllers;

import com.fazecast.jSerialComm.SerialPort;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733.d20.teamA.database.DatabaseServiceProvider;
import edu.wpi.cs3733.d20.teamA.database.announcement.AnnouncementDatabase;
import edu.wpi.cs3733.d20.teamA.database.employee.EmployeesDatabase;
import edu.wpi.cs3733.d20.teamA.database.graph.GraphDatabase;
import edu.wpi.cs3733.d20.teamA.database.inventory.InventoryDatabase;
import edu.wpi.cs3733.d20.teamA.database.patient.PatientDatabase;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceDatabase;
import edu.wpi.cs3733.d20.teamA.graph.Graph;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.util.NodeAutoCompleteHandler;
import java.sql.Connection;
import java.util.Optional;

public abstract class AbstractController {

  private SerialPort comPort = null;

  private DatabaseServiceProvider provider;
  private Connection conn;

  protected ServiceDatabase serviceDatabase;
  protected InventoryDatabase inventoryDatabase;
  // protected FlowerDatabase flDatabase;
  protected GraphDatabase graphDatabase;
  protected EmployeesDatabase eDB;

  protected PatientDatabase patientDatabase; // Not usable as service request table line
  protected AnnouncementDatabase announcementDatabase;

  public AbstractController() {
    provider = new DatabaseServiceProvider();
    conn = provider.provideConnection();

    graphDatabase = new GraphDatabase(conn);
    eDB = new EmployeesDatabase(conn);
    // flDatabase = new FlowerDatabase(conn);
    // iDB = new InterpreterDatabase(conn);
    inventoryDatabase = new InventoryDatabase(conn);

    patientDatabase = new PatientDatabase(conn);
    announcementDatabase = new AnnouncementDatabase(conn);
    serviceDatabase = new ServiceDatabase(conn);

    if (comPort == null) {
      SerialPort[] comPorts = SerialPort.getCommPorts();
      if (comPorts.length > 0) {
        comPort = SerialPort.getCommPorts()[0];
      }
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

package edu.wpi.cs3733.d20.teamA.controllers;

import com.fazecast.jSerialComm.SerialPort;
import edu.wpi.cs3733.d20.teamA.database.DatabaseServiceProvider;
import edu.wpi.cs3733.d20.teamA.database.OnCallDatabase;
import edu.wpi.cs3733.d20.teamA.database.announcement.AnnouncementDatabase;
import edu.wpi.cs3733.d20.teamA.database.employee.EmployeesDatabase;
import edu.wpi.cs3733.d20.teamA.database.flower.FlowerDatabase;
import edu.wpi.cs3733.d20.teamA.database.graph.GraphDatabase;
import edu.wpi.cs3733.d20.teamA.database.inventory.InventoryDatabase;
import edu.wpi.cs3733.d20.teamA.database.patient.PatientDatabase;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceDatabase;
import java.sql.Connection;

public abstract class AbstractController {

  private SerialPort comPort = null;


  private DatabaseServiceProvider provider;
  private Connection conn;

  protected ServiceDatabase serviceDatabase;
  protected InventoryDatabase inventoryDatabase;
  protected FlowerDatabase flDatabase;
  protected GraphDatabase graphDatabase;
  protected EmployeesDatabase eDB;
  protected OnCallDatabase ocDB;

  protected PatientDatabase patientDatabase; // Not usable as service request table line
  protected AnnouncementDatabase announcementDatabase;

  public AbstractController() {
    provider = new DatabaseServiceProvider();
    conn = provider.provideConnection();

    graphDatabase = new GraphDatabase(conn);
    eDB = new EmployeesDatabase(conn);
    flDatabase = new FlowerDatabase(conn);
    // iDB = new InterpreterDatabase(conn);
    inventoryDatabase = new InventoryDatabase(conn);

    patientDatabase = new PatientDatabase(conn);
    announcementDatabase = new AnnouncementDatabase(conn);
    serviceDatabase = new ServiceDatabase(conn);
    ocDB = new OnCallDatabase(conn);

    if (comPort != null) {
      comPort = SerialPort.getCommPorts()[0];
    }
  }

  public String scanRFID() {
    try {
      comPort.openPort();
      while (true) {
        while (comPort.bytesAvailable() != 14) Thread.sleep(20);

        byte[] readBuffer = new byte[comPort.bytesAvailable()];
        int numRead = comPort.readBytes(readBuffer, readBuffer.length);
        String scannedString = new String(readBuffer, "UTF-8");
        String[] scannedArray = scannedString.split(" ");
        System.out.println(scannedString);
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
}

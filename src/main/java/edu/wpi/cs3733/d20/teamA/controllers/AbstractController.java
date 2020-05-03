package edu.wpi.cs3733.d20.teamA.controllers;

import edu.wpi.cs3733.d20.teamA.database.DatabaseServiceProvider;
import edu.wpi.cs3733.d20.teamA.database.announcement.AnnouncementDatabase;
import edu.wpi.cs3733.d20.teamA.database.employee.EmployeesDatabase;
import edu.wpi.cs3733.d20.teamA.database.flower.FlowerDatabase;
import edu.wpi.cs3733.d20.teamA.database.graph.GraphDatabase;
import edu.wpi.cs3733.d20.teamA.database.inventory.InventoryDatabase;
import edu.wpi.cs3733.d20.teamA.database.patient.PatientDatabase;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceDatabase;
import java.sql.Connection;

public abstract class AbstractController {

  private DatabaseServiceProvider provider;
  private Connection conn;

  protected ServiceDatabase serviceDatabase;
  protected InventoryDatabase inventoryDatabase;
  protected FlowerDatabase flDatabase;
  protected GraphDatabase graphDatabase;
  protected EmployeesDatabase eDB;

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
  }
}

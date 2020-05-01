package edu.wpi.cs3733.d20.teamA.database;

import java.sql.Connection;

public class DatabaseMaker {

  private DatabaseServiceProvider provider;
  private Connection conn;

  ServiceDatabase serviceDatabase;
  InventoryDatabase inventoryDatabase;
  EmployeesDatabase employeeDatabase;
  GraphDatabase graphDatabase;
  PatientDatabase patientDatabase;
  FlowerDatabase flowerDatabase;
  AnnouncementDatabase announcementDatabase;
  InterpreterDatabase interpreterDatabase;

  public DatabaseMaker() {
    provider = new DatabaseServiceProvider();
    conn = provider.provideConnection();

    graphDatabase = new GraphDatabase(conn);
    employeeDatabase = new EmployeesDatabase(conn);
    flowerDatabase = new FlowerDatabase(conn);
    interpreterDatabase = new InterpreterDatabase(conn);

    patientDatabase = new PatientDatabase(conn);
    announcementDatabase = new AnnouncementDatabase(conn);
    serviceDatabase = new ServiceDatabase(conn);
  }

}

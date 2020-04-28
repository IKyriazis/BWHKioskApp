package edu.wpi.cs3733.d20.teamA.controllers;

import edu.wpi.cs3733.d20.teamA.database.*;
import edu.wpi.cs3733.d20.teamA.database.DatabaseServiceProvider;
import edu.wpi.cs3733.d20.teamA.database.EmployeesDatabase;
import edu.wpi.cs3733.d20.teamA.database.FlowerDatabase;
import edu.wpi.cs3733.d20.teamA.database.GraphDatabase;
import java.sql.Connection;

public abstract class AbstractController {

  private DatabaseServiceProvider provider;
  private Connection conn;
  protected FlowerDatabase flDatabase;
  protected GraphDatabase graphDatabase;
  protected EmployeesDatabase eDB;
  protected EquipReqDatabase erDB;
  protected JanitorDatabase janitorDatabase;
  protected LaundryDatabase lDB;
  protected ITTicketDatabase itTicketDatabase;
  protected PatientDatabase patientDatabase;
  protected InternalTransportDatabase itDatabase;
  protected InterpreterDatabase iDB;
  protected PrescriptionDatabase prescriptionDatabase;

  public AbstractController() {
    provider = new DatabaseServiceProvider();
    conn = provider.provideConnection();
    graphDatabase = new GraphDatabase(conn);
    flDatabase = new FlowerDatabase(conn);
    eDB = new EmployeesDatabase(conn);
    erDB = new EquipReqDatabase(conn);

    // Uncomment if employee database is messed up
    // eDB.removeAllEmployees();
    // eDB.readEmployeeCSV();

    // Run these two after I merge the new version of flower
    // flDatabase.removeAllOrders();
    // flDatabase.removeAllFlowers();
    iDB = new InterpreterDatabase(conn);
    // Uncomment if employee database is messed up
    // eDB.removeAllEmployees();
    // eDB.readEmployeeCSV();
    janitorDatabase = new JanitorDatabase(conn);
    lDB = new LaundryDatabase(conn);
    itTicketDatabase = new ITTicketDatabase(conn);
    patientDatabase = new PatientDatabase(conn);
    itDatabase = new InternalTransportDatabase(conn);
    prescriptionDatabase = new PrescriptionDatabase(conn);
  }
}

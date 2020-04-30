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
  protected ServiceDatabase primaryDB;

  protected FlowerDatabase flDatabase;
  protected GraphDatabase graphDatabase;
  protected EmployeesDatabase eDB;
  // protected EquipReqDatabase erDB;
  protected ITTicketDatabase itTicketDatabase;
  protected PatientDatabase patientDatabase;
  protected InternalTransportDatabase itDatabase;
  protected InterpreterDatabase iDB;
  protected PrescriptionDatabase prescriptionDatabase;
  protected AnnouncementDatabase announcementDatabase;

  public AbstractController() {
    provider = new DatabaseServiceProvider();
    conn = provider.provideConnection();

    graphDatabase = new GraphDatabase(conn);
    eDB = new EmployeesDatabase(conn);
    flDatabase = new FlowerDatabase(conn);
    // erDB = new EquipReqDatabase(conn);
    iDB = new InterpreterDatabase(conn);
    itTicketDatabase = new ITTicketDatabase(conn);
    patientDatabase = new PatientDatabase(conn);
    itDatabase = new InternalTransportDatabase(conn);
    prescriptionDatabase = new PrescriptionDatabase(conn);
    announcementDatabase = new AnnouncementDatabase(conn);
    primaryDB = new ServiceDatabase(conn);
  }
}

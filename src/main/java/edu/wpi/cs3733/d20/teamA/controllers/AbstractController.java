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
  protected JanitorDatabase janitorDatabase;
  protected PatientDatabase patientDatabase;
  protected PrescriptionDatabase prescriptionDatabase;

  public AbstractController() {
    provider = new DatabaseServiceProvider();
    conn = provider.provideConnection();
    graphDatabase = new GraphDatabase(conn);
    flDatabase = new FlowerDatabase(conn);
    eDB = new EmployeesDatabase(conn);
    janitorDatabase = new JanitorDatabase(conn);
    patientDatabase = new PatientDatabase(conn);
    prescriptionDatabase = new PrescriptionDatabase(conn);
  }
}

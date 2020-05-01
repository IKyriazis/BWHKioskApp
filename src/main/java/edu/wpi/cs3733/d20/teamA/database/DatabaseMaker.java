package edu.wpi.cs3733.d20.teamA.database;

import java.sql.Connection;

public class DatabaseMaker {

  private DatabaseServiceProvider provider;
  private Connection conn;

  private ServiceDatabase serviceDatabase;
  private InventoryDatabase inventoryDatabase;
  private EmployeesDatabase employeeDatabase;
  private GraphDatabase graphDatabase;
  private PatientDatabase patientDatabase;
  private FlowerDatabase flowerDatabase;
  private AnnouncementDatabase announcementDatabase;
  private InterpreterDatabase interpreterDatabase;

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

  //Creates tables
  public boolean createGraphTable(){
    return graphDatabase.createTables();
  }

  public boolean createEmployeeTable(){
    return employeeDatabase.createTables();
  }

  public boolean createPatientTable(){
    return patientDatabase.createTables();
  }

  public boolean createAnnouncementTable(){
    return announcementDatabase.createTables();
  }

  public boolean createFlowerTable(){
    return flowerDatabase.createTables();
  }

  public boolean createServiceTable(){
    return serviceDatabase.createTables();
  }

  //Drops Tables
  public boolean deleteGraphTable(){
    return graphDatabase.dropTables();
  }

  public boolean deleteEmployeeTable(){
    return employeeDatabase.dropTables();
  }

  public boolean deletePatientTable(){
    return patientDatabase.dropTables();
  }

  public boolean deleteAnnouncementTable(){
    return announcementDatabase.dropTables();
  }

  public boolean deleteFlowerTable(){
    return flowerDatabase.dropTables();
  }

  public boolean deleteServiceTable(){
    return serviceDatabase.dropTables();
  }

  //GetSize
  public int getSizeNode(){
    return graphDatabase.getSizeNode();
  }

  public int getSizeEdge(){
    return graphDatabase.getSizeEdge();
  }

  public int getSizeEmployee(){
    return employeeDatabase.getSize();
  }

  public int getSizePatient(){
    return patientDatabase.getSize();
  }

  public int getSizeAnnouncement(){
    return announcementDatabase.getSize();
  }

  public int getSizeFlower(){
    return flowerDatabase.getSizeFlowers();
  }

  public int getSizeOrders(){
    return flowerDatabase.getSizeOrders();
  }

  public int getServiceSize(){
    return serviceDatabase.getSize();
  }

  public int getServiceSize(String service){
    return serviceDatabase.getSize(service);
  }
}

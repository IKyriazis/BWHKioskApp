package edu.wpi.cs3733.d20.teamA.database;

import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import java.sql.Connection;
import javafx.collections.ObservableList;

public class DatabaseMaker {

  private DatabaseServiceProvider provider;
  private Connection conn;

  private ServiceDatabase serviceDatabase;
  private InventoryDatabase inventoryDatabase;
  private EmployeesDatabase employeeDatabase;
  public GraphDatabase graphDatabase;
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

  // Creates tables
  public boolean createGraphTable() {
    return graphDatabase.createTables();
  }

  public boolean createEmployeeTable() {
    return employeeDatabase.createTables();
  }

  public boolean createPatientTable() {
    return patientDatabase.createTables();
  }

  public boolean createAnnouncementTable() {
    return announcementDatabase.createTables();
  }

  public boolean createFlowerTable() {
    return flowerDatabase.createTables();
  }

  public boolean createServiceTable() {
    return serviceDatabase.createTables();
  }

  // Drops Tables
  public boolean deleteGraphTable() {
    return graphDatabase.dropTables();
  }

  public boolean deleteEmployeeTable() {
    return employeeDatabase.dropTables();
  }

  public boolean deletePatientTable() {
    return patientDatabase.dropTables();
  }

  public boolean deleteAnnouncementTable() {
    return announcementDatabase.dropTables();
  }

  public boolean deleteFlowerTable() {
    return flowerDatabase.dropTables();
  }

  public boolean deleteServiceTable() {
    return serviceDatabase.dropTables();
  }

  // GetSize
  public int getSizeNode() {
    return graphDatabase.getSizeNode();
  }

  public int getSizeEdge() {
    return graphDatabase.getSizeEdge();
  }

  public int getSizeEmployee() {
    return employeeDatabase.getSize();
  }

  public int getSizePatient() {
    return patientDatabase.getSize();
  }

  public int getSizeAnnouncement() {
    return announcementDatabase.getSize();
  }

  public int getSizeFlower() {
    return flowerDatabase.getSizeFlowers();
  }

  public int getSizeOrders() {
    return flowerDatabase.getSizeOrders();
  }

  public int getServiceSize() {
    return serviceDatabase.getSize();
  }

  public int getServiceSize(ServiceType service) {
    return serviceDatabase.getSize(service);
  }

  // Remove All Functions
  public boolean removeAllGraph() {
    return graphDatabase.removeAll();
  }

  public boolean removeAllNode() {
    return graphDatabase.removeAllNodes();
  }

  public boolean removeAllEdge() {
    return graphDatabase.removeAllEdges();
  }

  public boolean removeAllEmployees() {
    return employeeDatabase.removeAll();
  }

  public boolean removeAllPatients() {
    return patientDatabase.removeAll();
  }

  public boolean removeAllAnnouncements() {
    return announcementDatabase.removeAll();
  }

  public boolean removeAllFlowers() {
    return flowerDatabase.removeAllFlowers();
  }

  public boolean removeAllOrders() {
    return flowerDatabase.removeAllOrders();
  }

  public boolean removeAllService() {
    return serviceDatabase.removeAll();
  }

  public boolean removeAllService(ServiceType service) {
    return serviceDatabase.removeAll(service);
  }

  // Get Observable Lists
  public ObservableList<Employee> getEmployeeObservableList() {
    return employeeDatabase.getObservableList();
  }

  public ObservableList<Announcement> getAnnouncementObservableList() {
    return announcementDatabase.getObservableList();
  }

  public ObservableList<Patient> getPatientObservableList() {
    return patientDatabase.getObservableList();
  }

  public ObservableList<ITableable> getServiceObservableList() {
    return serviceDatabase.getObservableList();
  }

  public ObservableList<ITableable> getServiceObservableList(ServiceType service) {
    return serviceDatabase.getObservableListService(service);
  }

  // Remove a row
  public boolean removeEmployee(String username) {
    return employeeDatabase.deleteEmployee(username);
  }

  public boolean removePatient(String id) {
    return patientDatabase.deletePatient(id);
  }

  public boolean removeAnnouncement(String id) {
    return announcementDatabase.removeAnnouncement(id);
  }

  public boolean removeFlower(String type, String color) {
    return flowerDatabase.deleteFlower(type, color);
  }

  public boolean removeOrder(int orderNumber) {
    return flowerDatabase.deleteOrder(orderNumber);
  }

  public boolean removeServiceRequest(String id) {
    return serviceDatabase.deleteServReq(id);
  }

  public String addEmployee(
      String nameFirst, String nameLast, String username, String password, String title) {
    return employeeDatabase.addEmployee(nameFirst, nameLast, username, password, title);
  }
}

package edu.wpi.cs3733.d20.teamA.database;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

// Add more methods to this class as needed
public class Employee {
  private SimpleIntegerProperty id;
  private SimpleStringProperty fName;
  private SimpleStringProperty lName;
  private SimpleStringProperty title;
  private SimpleStringProperty username;

  public Employee(int id, String fName, String lName, String title, String username) {
    this.id = new SimpleIntegerProperty(id);
    this.fName = new SimpleStringProperty(fName);
    this.lName = new SimpleStringProperty(lName);
    this.title = new SimpleStringProperty(title);
    this.username = new SimpleStringProperty(username);
  }

  public String getUsername() {
    return username.get();
  }

  public String toString() {
    return fName.get() + " " + lName.get();
  }

  public int getId() {
    return id.get();
  }
}

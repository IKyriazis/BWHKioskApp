package edu.wpi.cs3733.d20.teamA.database.employee;

import javafx.beans.property.SimpleStringProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestEmployee {

  @Test
  public void createEmployee() {
    Employee employee = new Employee("EEEEEEE", "Yash", "Patel", "ADMIN", "yppatel");
    Assertions.assertNotNull(employee);
  }

  @Test
  public void testGetters() {
    Employee employee = new Employee("EEEEEEE", "Yash", "Patel", "ADMIN", "yppatel");
    Assertions.assertEquals("EEEEEE", employee.getId());
    Assertions.assertEquals("Yash Patel", employee.toString());
    Assertions.assertEquals("yppatel", employee.getUsername());

    Assertions.assertEquals(new SimpleStringProperty("EEEEEE"), employee.getIDProperty());
    Assertions.assertEquals(new SimpleStringProperty("Yash"), employee.getfNameProperty());
    Assertions.assertEquals(new SimpleStringProperty("Patel"), employee.getlNameProperty());
    Assertions.assertEquals(new SimpleStringProperty("ADMIN"), employee.getTitleProperty());
    Assertions.assertEquals(new SimpleStringProperty("yppatel"), employee.getUsernameProperty());
  }
}

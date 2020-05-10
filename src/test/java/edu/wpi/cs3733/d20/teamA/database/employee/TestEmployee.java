package edu.wpi.cs3733.d20.teamA.database.employee;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestEmployee {

  @Test
  public void createEmployee() {
    Employee employee =
        new Employee("EEEEEEE", "Yash", "Patel", EmployeeTitle.ADMIN, "yppatel", "9900990094l");
    Assertions.assertNotNull(employee);
  }

  @Test
  public void testGetters() {
    Employee employee =
        new Employee("EEEEEEE", "Yash", "Patel", EmployeeTitle.ADMIN, "yppatel", "9900990094l");
    Assertions.assertEquals("EEEEEEE", employee.getId());
    Assertions.assertEquals("Yash Patel", employee.toString());
    Assertions.assertEquals("yppatel", employee.getUsername());

    Assertions.assertEquals("EEEEEEE", employee.getIDProperty().get());
    Assertions.assertEquals("Yash", employee.getfNameProperty().get());
    Assertions.assertEquals("Patel", employee.getlNameProperty().get());
    Assertions.assertEquals("admin", employee.getTitleProperty().get());
    Assertions.assertEquals("yppatel", employee.getUsernameProperty().get());
  }

  @Test
  public void testEquals() {
    Employee employee =
        new Employee("EEEEEEE", "Yash", "Patel", EmployeeTitle.ADMIN, "yppatel", "3300220033l");
    Employee employee1 =
        new Employee("EEEEEEE", "Yash", "Patel", EmployeeTitle.ADMIN, "yppatel", "3300220033l");
    Assertions.assertTrue(employee.equals(employee1));
  }
}

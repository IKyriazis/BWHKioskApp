package edu.wpi.cs3733.d20.teamA.database.OnCall;

import edu.wpi.cs3733.d20.teamA.database.PublicEmployee;
import edu.wpi.cs3733.d20.teamA.database.employee.EmployeeTitle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestPublicEmp {

  @Test
  public void createPublicEmployee() {
    PublicEmployee emp =
        new PublicEmployee("Available", "Yash", "Patel", EmployeeTitle.ADMIN, "9948832456l", "abc");
    Assertions.assertNotNull(emp);
  }

  @Test
  public void testGetters() {
    PublicEmployee emp =
        new PublicEmployee("Available", "Yash", "Patel", EmployeeTitle.ADMIN, "9948832456l", "abc");
    Assertions.assertEquals("Available", emp.getStatus());
    Assertions.assertEquals("abc", emp.getUsername());

    Assertions.assertEquals("Available", emp.statusProperty().get());
    Assertions.assertEquals("Yash", emp.fNameProperty().get());
    Assertions.assertEquals("Patel", emp.lNameProperty().get());
    Assertions.assertEquals("admin", emp.titleProperty().get());
    Assertions.assertEquals("abc", emp.usernameProperty().get());
  }
}

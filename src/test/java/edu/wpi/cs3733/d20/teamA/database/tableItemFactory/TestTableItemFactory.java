package edu.wpi.cs3733.d20.teamA.database.tableItemFactory;

import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import edu.wpi.cs3733.d20.teamA.database.TableItemFactory;
import java.sql.Timestamp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestTableItemFactory {

  @Test
  public void testService() {
    ITableable janitorService =
        TableItemFactory.getService(
            "janitor",
            "TURTLE",
            "Clean up",
            "Yash Patel",
            new Timestamp(System.currentTimeMillis()),
            "In Progress",
            "IKEA",
            "HIGH",
            "");
    Assertions.assertNotNull(janitorService);
    ITableable medicine =
        TableItemFactory.getService(
            "medicine",
            "TURTLE",
            "Yash Patel",
            "Someone",
            new Timestamp(System.currentTimeMillis()),
            "In Progress",
            "IKEA",
            "670",
            "Patient|Doctor|NyQuil");
    Assertions.assertNotNull(medicine);
  }
}

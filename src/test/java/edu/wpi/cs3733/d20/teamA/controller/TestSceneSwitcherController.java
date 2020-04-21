package edu.wpi.cs3733.d20.teamA.controller;

import edu.wpi.cs3733.d20.teamA.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

@ExtendWith(ApplicationExtension.class)
public class TestSceneSwitcherController extends TestAbstractController {
  @Start
  private void start(Stage stage) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(App.class.getResource("views/SceneSwitcher.fxml"));

      Node rootNode = loader.load();
      Assertions.assertNotNull(rootNode);

      Scene scene = new Scene((Parent) rootNode);
      stage.setScene(scene);
      stage.show();
      stage.setAlwaysOnTop(true);

    } catch (Exception e) {
      e.printStackTrace();

      Assertions.fail();
    }
  }

  @Test
  public void testClickDeliveryServices() {
    clickOn("#serviceTab");
    // If we got to here without crashing then we probably did something right.
    Assertions.assertTrue(true);
  }

  @Test
  public void testClickEmployeeTab() {
    clickOn("#employeeLoginTab");
    // If we got to here without crashing then we probably did something right.
    Assertions.assertTrue(true);
  }

  @Test
  public void testClickMapTab() {
    clickOn("#mapTab");
    // If we got to here without crashing then we probably did something right.
    Assertions.assertTrue(true);
  }

  @Test
  public void testClickMultiTabs() {
    clickOn("#serviceTab");
    clickOn("#mapTab");
    clickOn("#serviceTab");
    // robot.
    // Node node = lookup("#serviceTab").query();
    // If we got to here without crashing then we probably did something right.
    Assertions.assertTrue(true);
  }

  @Test
  public void testClickInfo() {
    clickOn("#informationButton");
    // If we got to here without crashing then we probably did something right.
    Assertions.assertTrue(true);
  }
}

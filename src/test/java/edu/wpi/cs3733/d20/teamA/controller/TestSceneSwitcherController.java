package edu.wpi.cs3733.d20.teamA.controller;

import edu.wpi.cs3733.d20.teamA.App;
import edu.wpi.cs3733.d20.teamA.controllers.LoginController;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

@ExtendWith(ApplicationExtension.class)
public class TestSceneSwitcherController extends TestAbstractController {

  @InjectMocks LoginController controller;

  private final FXMLLoader loader = new FXMLLoader();
  private Parent sceneRoot;

  @Start
  private void start(Stage stage) {
    try {
      loader.setControllerFactory((i) -> controller);
      sceneRoot = loader.load(App.class.getResource("views/SceneSwitcher.fxml"));
      var primaryScene = new Scene(sceneRoot);
      // We are forced to inject this object afterward, since it must be created on the JavaFX
      // thread
      // controller.setAppPrimaryScene(primaryScene);

      stage.setScene(primaryScene);
      stage.setAlwaysOnTop(true);
      stage.show();
    } catch (IOException e) {
      e.printStackTrace();
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

  // @Test
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

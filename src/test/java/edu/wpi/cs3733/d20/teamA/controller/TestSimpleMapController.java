package edu.wpi.cs3733.d20.teamA.controller;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.testfx.util.WaitForAsyncUtils.sleep;

import edu.wpi.cs3733.d20.teamA.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

@ExtendWith(ApplicationExtension.class)
public class TestSimpleMapController extends TestAbstractController {

  @Start
  private void start(Stage stage) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(App.class.getResource("views/SimpleMap.fxml"));

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
  public void checkGetDirections() {
    clickOn("#drawAllNodesButton");
    clickOn("#drawPathButton");

    clickOn("#directionsButton");
    sleep(1, SECONDS);

    clickOn("#startingLocationBox");
    type(KeyCode.A);
    type(KeyCode.ENTER);
    type(KeyCode.H);
    type(KeyCode.ENTER);
    clickOn("#goButton");

    clickOn("#swapBtn");
    clickOn("#goButton");

    // If we got to here without crashing then we probably did something right.
    Assertions.assertTrue(true);
  }
}

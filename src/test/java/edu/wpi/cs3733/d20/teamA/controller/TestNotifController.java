package edu.wpi.cs3733.d20.teamA.controller;

import edu.wpi.cs3733.d20.teamA.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

@ExtendWith(ApplicationExtension.class)
public class TestNotifController {

  @Start
  private void start(Stage stage) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(App.class.getResource("views/NotificationWall.fxml"));

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
}

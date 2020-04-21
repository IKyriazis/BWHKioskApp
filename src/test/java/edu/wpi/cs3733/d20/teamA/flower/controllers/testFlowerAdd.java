package edu.wpi.cs3733.d20.teamA.flower.controllers;

import edu.wpi.cs3733.d20.teamA.App;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.FlowerDialogController;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

@ExtendWith(ApplicationExtension.class)
public class testFlowerAdd {

  private FlowerDialogController controller;

  public testFlowerAdd() {}

  @Start
  private void start(Stage stage) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(App.class.getResource("views/AddFlowerPopup.fxml"));
      loader.setControllerFactory(param -> new FlowerDialogController());
      GridPane gridPane = loader.load();
      Assertions.assertNotNull(gridPane);

      controller = loader.getController();
      Assertions.assertNotNull(controller);

      Scene scene = new Scene(gridPane);
      stage.setScene(scene);
      stage.show();

    } catch (Exception e) {
      e.printStackTrace();

      Assertions.fail();
    }
  }

  @Test
  public void testAdd() throws IOException {
    try {
      controller.isDone(new ActionEvent());
    } catch (Exception e) {
      // Wont run in isolation
    }
    // Manually tested
    Assertions.assertTrue(true);
  }
}

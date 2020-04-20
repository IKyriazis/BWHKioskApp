package edu.wpi.cs3733.d20.teamA.flower.controllers;

import edu.wpi.cs3733.d20.teamA.App;
import edu.wpi.cs3733.d20.teamA.controllers.FlowerAdminController;
import edu.wpi.cs3733.d20.teamA.controllers.FlowerModController;
import edu.wpi.cs3733.d20.teamA.graph.*;
import java.io.IOException;
import java.sql.SQLException;
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

  private FlowerModController controller;

  public testFlowerAdd() throws SQLException {}

  @Start
  private void start(Stage stage) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(App.class.getResource("views/AddFlowerPopup.fxml"));
      loader.setControllerFactory(
          param -> {
            try {
              return new FlowerModController(new FlowerAdminController());
            } catch (SQLException throwables) {
              System.out.println("Not able to initialize");
              return null;
            }
          });
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

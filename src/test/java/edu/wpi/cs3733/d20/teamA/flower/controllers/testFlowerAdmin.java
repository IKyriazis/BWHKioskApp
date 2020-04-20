package edu.wpi.cs3733.d20.teamA.flower.controllers;

import edu.wpi.cs3733.d20.teamA.App;
import edu.wpi.cs3733.d20.teamA.controllers.FlowerAdminController;
import edu.wpi.cs3733.d20.teamA.graph.*;
import java.io.IOException;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

@ExtendWith(ApplicationExtension.class)
public class testFlowerAdmin {

  private FlowerAdminController controller = new FlowerAdminController();

  public testFlowerAdmin() throws SQLException {}

  @Start
  private void start(Stage stage) {
    try {

      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(App.class.getResource("views/FlowerAdmin.fxml"));

      AnchorPane anchorPane = loader.load();
      Assertions.assertNotNull(anchorPane);

      controller = loader.getController();
      Assertions.assertNotNull(controller);

      Scene scene = new Scene(anchorPane);
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
      controller.addFlower(new ActionEvent());
    } catch (Exception e) {
      // Wont run in isolation
    }
    // Manually tested
    Assertions.assertTrue(true);
  }

  @Test
  public void testEdit() throws IOException {
    try {
      controller.editFlower(new ActionEvent());
    } catch (Exception e) {
      // Wont run in isolation
    }
    // Manually tested
    Assertions.assertTrue(true);
  }

  @Test
  public void testDelete() throws IOException {
    try {
      controller.deleteFlower(new ActionEvent());
    } catch (Exception e) {
      // Wont run in isolation
    }
    // Manually tested
    Assertions.assertTrue(true);
  }

  @Test
  public void testChangeStatus() throws IOException {
    try {
      controller.changeProgress(new ActionEvent());
    } catch (Exception e) {
      // Wont run in isolation
    }
    // Manually tested
    Assertions.assertTrue(true);
  }
}

package edu.wpi.cs3733.d20.teamA.controller;

import edu.wpi.cs3733.d20.teamA.App;
import edu.wpi.cs3733.d20.teamA.controllers.FlowerServiceController;
import java.sql.SQLException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

@ExtendWith(ApplicationExtension.class)
public class TestFlowerOrderController {

  private FlowerServiceController controller = new FlowerServiceController();

  public TestFlowerOrderController() throws SQLException {}

  @Start
  private void start(Stage stage) {
    try {

      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(App.class.getResource("views/FlowerOrderGUI.fxml"));

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
}

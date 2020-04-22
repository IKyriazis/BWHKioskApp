package edu.wpi.cs3733.d20.teamA.controller;

import edu.wpi.cs3733.d20.teamA.App;
import edu.wpi.cs3733.d20.teamA.controllers.FlowerAdminController;
import java.sql.SQLException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

@ExtendWith(ApplicationExtension.class)
public class TestFlowerAdmin extends TestAbstractController {

  @InjectMocks FlowerAdminController controller;

  private final FXMLLoader loader = new FXMLLoader();
  private Parent sceneRoot;

  public TestFlowerAdmin() throws SQLException {}

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
  public void testAdd() {
    clickOn("#addFlowerButton");
    sleep(1000);
    clickOn("#txtName");
    writeString("Poppy");
    clickOn("#txtColor");
    writeString("Red");
    clickOn("#txtQty");
    writeInt("60");
    clickOn("#txtCost");
    writeInt("5");
  }

  @Test
  public void testEdit() {}

  @Test
  public void testDelete() {}

  @Test
  public void testChangeStatus() {}
}

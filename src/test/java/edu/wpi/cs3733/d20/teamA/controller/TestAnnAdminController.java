package edu.wpi.cs3733.d20.teamA.controller;

import edu.wpi.cs3733.d20.teamA.App;
import edu.wpi.cs3733.d20.teamA.database.AnnouncementList;
import java.awt.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

@ExtendWith(ApplicationExtension.class)
public class TestAnnAdminController {

  @Start
  private void start(Stage stage) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(App.class.getResource("views/AnnouncementAdmin.fxml"));

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
  public void testAddAnn(FxRobot robot) {

    robot.clickOn("#textAnn");
    robot.type(KeyCode.H);
    robot.type(KeyCode.I);
    robot.clickOn("#addA");
    // If we got to here without crashing then we probably did something right.
    Assertions.assertTrue(true);
  }

  @Test
  public void testDeleteAnn(FxRobot robot) {

    robot.clickOn("#textAnn");
    robot.type(KeyCode.H);
    robot.type(KeyCode.I);
    robot.clickOn("#addA");
    robot.clickOn("#editAnn");
    robot.clickOn("#deleteA");
    // If we got to here without crashing then we probably did something right.
    Assertions.assertTrue(true);

    int i = AnnouncementList.getList().size();
    Assertions.assertEquals(0, i);
  }
}

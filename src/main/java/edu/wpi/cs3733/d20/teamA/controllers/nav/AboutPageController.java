package edu.wpi.cs3733.d20.teamA.controllers.nav;

import edu.wpi.cs3733.d20.teamA.controllers.SceneSwitcherController;
import javafx.event.ActionEvent;

public class AboutPageController {

  public void exitAbout(ActionEvent actionEvent) {
    SceneSwitcherController.popScene();
  }
}

package edu.wpi.cs3733.d20.teamA.controllers.nav;

import static edu.wpi.cs3733.d20.teamA.controllers.SceneSwitcherController.pushScene;

import edu.wpi.cs3733.d20.teamA.controllers.SceneSwitcherController;
import edu.wpi.cs3733.d20.teamA.controls.TransitionType;
import javafx.event.ActionEvent;

public class AboutPageController {

  public void exitAbout(ActionEvent actionEvent) {
    SceneSwitcherController.popScene();
  }

  public void openCredits(ActionEvent actionEvent) {
    pushScene("views/Credits.fxml", TransitionType.ZOOM);
  }
}

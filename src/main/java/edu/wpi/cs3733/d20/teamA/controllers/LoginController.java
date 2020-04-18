package edu.wpi.cs3733.d20.teamA.controllers;

import edu.wpi.cs3733.d20.teamA.App;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class LoginController {

  // Do whatever validation
  @FXML
  public void login(ActionEvent event) throws IOException {
    Stage stage;
    Parent root;
    // putting the event's source in src var so it doesn't have to check it every time
    Object src = event.getSource();

    stage =
        (Stage)
            ((Button) (src)).getScene().getWindow(); // use existing stage to close current window

    root = FXMLLoader.load(App.class.getResource("views/AdminFeatures.fxml"));
    Scene scene = new Scene(root);

    stage.setScene(scene);
    stage.show();
  }
}

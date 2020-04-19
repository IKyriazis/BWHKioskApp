package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.App;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javax.swing.*;

public class FlowerTrackerController {

  @FXML private JFXTextField txtNumber;
  @FXML private JFXProgressBar progress;
  @FXML private Label lblOutput;

  public void initialize() {
    progress.setProgress(0);
    lblOutput.setText("Input an order number");
  }

  @FXML
  public void checkNum(Event e) {
    // Send text to database and get back the status
    String s = txtNumber.getText();
    if (s.equals("1")) {
      progress.setProgress(.1);
      lblOutput.setText("Order sent");
    } else if (s.equals("2")) {
      progress.setProgress(.35);
      lblOutput.setText("Order received");
    } else if (s.equals("3")) {
      progress.setProgress(.7);
      lblOutput.setText("Flower sent");
    } else if (s.equals("4")) {
      progress.setProgress(1);
      lblOutput.setText("Flower delivered");
    } else {
      progress.setProgress(0);
      lblOutput.setText("Input an order number");
    }
  }

  @FXML
  public void cancel(ActionEvent event) throws IOException {
    Stage stage;
    Parent root;
    // putting the event's source in src var so it doesn't have to check it every time
    Object src = event.getSource();

    stage =
        (Stage)
            ((Button) (src)).getScene().getWindow(); // use existing stage to close current window

    root = FXMLLoader.load(App.class.getResource("views/FlowerDeliveryServiceHome.fxml"));
    Scene scene = new Scene(root);

    stage.setScene(scene);
    stage.show();
  }
}

package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javax.swing.*;

public class FlowerTrackerController extends AbstractController {

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
    String s = super.flDatabase.getOrderStatus(Integer.parseInt(txtNumber.getText()));
    if (s == null) {
      progress.setProgress(0);
      lblOutput.setText("Input an order number");
      return;
    }

    if (s.equals("Order Sent")) {
      progress.setProgress(.1);
      lblOutput.setText("Order Sent");
    } else if (s.equals("Order Received")) {
      progress.setProgress(.35);
      lblOutput.setText("Order Received");
    } else if (s.equals("Flower Sent")) {
      progress.setProgress(.7);
      lblOutput.setText("Flower Sent");
    } else if (s.equals("Flower Delivered")) {
      progress.setProgress(1);
      lblOutput.setText("Flower Delivered");
    } else {
      progress.setProgress(0);
      lblOutput.setText("Input an order number");
    }
  }
}

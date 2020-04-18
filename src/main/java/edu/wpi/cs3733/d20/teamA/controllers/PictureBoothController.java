package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

public class PictureBoothController {
  ObservableList dropdownMenuItems = FXCollections.observableArrayList();

  @FXML private JFXComboBox<String> dropdownMenu;
  @FXML private JFXButton takePictureButton;

  public void initialize() {
    dropdownMenuItems.removeAll(dropdownMenuItems);
    String a = "Image 1";
    String b = "Image 2";
    String c = "Image 3";
    String d = "Image 4";
    String e = "Image 5";
    String f = "Image 6";
    String g = "Image 7";
    dropdownMenuItems.addAll(a, b, c, d, e, f, g);
    dropdownMenu.getItems().addAll(dropdownMenuItems);
  }
}

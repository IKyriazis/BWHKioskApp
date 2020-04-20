package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXListView;
import edu.wpi.cs3733.d20.teamA.database.AnnouncementList;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

public class NotificationController extends AbstractController {

  @FXML JFXListView viewAnn;

  public void initialize() {
    viewAnn.getItems().clear();
    ObservableList<String> listAnn = AnnouncementList.getList();
    for (int i = 0; i < listAnn.size(); i++) {
      viewAnn.getItems().add(listAnn.get(i));
    }
  }
}

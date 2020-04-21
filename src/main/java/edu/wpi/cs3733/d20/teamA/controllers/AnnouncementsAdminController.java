package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.database.AnnouncementList;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class AnnouncementsAdminController {

  @FXML private JFXListView editAnn;
  @FXML private JFXTextField textAnn;

  public void addAnn(ActionEvent actionEvent) {
    if (textAnn.getText().isEmpty()) {
      return;
    }

    AnnouncementList.addToList(textAnn.getText());
    textAnn.setText("");
    update();
  }

  public void deleteAnn(ActionEvent actionEvent) {
    if (editAnn.getSelectionModel().getSelectedIndex() == -1) {
      return;
    }

    AnnouncementList.deleteFromList(editAnn.getSelectionModel().getSelectedIndex());
    update();
  }

  public void initialize() {
    this.update();
  }

  public void update() {
    editAnn.getItems().clear();
    ObservableList<String> listAnn = AnnouncementList.getList();
    for (int i = 0; i < listAnn.size(); i++) {
      editAnn.getItems().add(listAnn.get(i));
    }
  }
}

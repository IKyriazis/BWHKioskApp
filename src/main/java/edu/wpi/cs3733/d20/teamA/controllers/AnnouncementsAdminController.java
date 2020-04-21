package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.database.AnnouncementList;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class AnnouncementsAdminController {
  @FXML private JFXListView editAnn;
  @FXML private JFXTextField textAnn;

  @FXML private JFXButton addButton;
  @FXML private JFXButton deleteButton;

  @FXML
  public void initialize() {
    addButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS_SQUARE_ALT));
    deleteButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.MINUS_SQUARE_ALT));

    this.update();
  }

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

  public void update() {
    editAnn.getItems().clear();
    ObservableList<String> listAnn = AnnouncementList.getList();
    for (int i = 0; i < listAnn.size(); i++) {
      editAnn.getItems().add(listAnn.get(i));
    }
  }
}

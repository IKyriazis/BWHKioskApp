package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class AnnouncementsAdminController {
  @FXML private Label lblTitle;

  @FXML private JFXListView editAnn;
  @FXML private JFXTextField textAnn;

  @FXML private JFXButton addButton;
  @FXML private JFXButton deleteButton;

  @FXML private StackPane dialogPane;

  @FXML
  public void initialize() {
    lblTitle.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.BULLHORN));
    addButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS_SQUARE_ALT));
    deleteButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.MINUS_SQUARE_ALT));

    this.update();
  }

  public void addAnn(ActionEvent actionEvent) {
    if (textAnn.getText().isEmpty()) {
      DialogUtil.simpleInfoDialog(
          dialogPane, "No Text", "Please enter in the text of the announcement to add");
      return;
    }

    // AnnouncementDatabase.addToList(textAnn.getText());
    textAnn.setText("");
    update();
  }

  public void deleteAnn(ActionEvent actionEvent) {
    if (editAnn.getSelectionModel().getSelectedIndex() == -1) {
      DialogUtil.simpleInfoDialog(
          dialogPane, "No Selection", "Please select an announcement to delete");
      return;
    }

    // AnnouncementDatabase.deleteFromList(editAnn.getSelectionModel().getSelectedIndex());
    update();
  }

  public void update() {
    editAnn.getItems().clear();
    // ObservableList<String> listAnn = AnnouncementDatabase.getList();
    // for (int i = 0; i < listAnn.size(); i++) {
    //  editAnn.getItems().add(listAnn.get(i));
    // }
  }
}

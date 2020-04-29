package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.Announcement;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class AnnouncementsAdminController extends AbstractController {
  @FXML private Label lblTitle;

  @FXML private JFXTextField textAnn;

  @FXML private JFXButton addButton;
  @FXML private JFXButton deleteButton;

  @FXML private StackPane dialogPane;

  @FXML private GridPane tableGridPane;

  private SimpleTableView<Announcement> tblAnnouncement;

  private Announcement selected;

  @FXML
  public void initialize() {
    lblTitle.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.BULLHORN));
    addButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS_SQUARE_ALT));
    deleteButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.MINUS_SQUARE_ALT));

    // Set up table
    tblAnnouncement = new SimpleTableView<>(new Announcement(0, ""), 80);
    tableGridPane.getChildren().add(tblAnnouncement);

    // Track when the mouse has clicked on the table
    tblAnnouncement.setOnMouseClicked(
        event -> {
          selected = tblAnnouncement.getSelected();
          if (selected != null) {
            deleteButton.setDisable(false);
          }
        });

    deleteButton.setDisable(true);
    this.update();
  }

  public void addAnn(ActionEvent actionEvent) {
    if (textAnn.getText().isEmpty()) {
      DialogUtil.simpleInfoDialog(
          dialogPane, "No Text", "Please enter in the text of the announcement to add");
      return;
    }

    String announcement = textAnn.getText();
    int id = announcementDatabase.addAnnouncement(announcement);
    textAnn.setText("");
    update();
  }

  public void deleteAnn(ActionEvent actionEvent) {
    selected = tblAnnouncement.getSelected();
    if (selected != null) {
      announcementDatabase.removeAnnouncement(selected.getAnnouncementID());
    }
    deleteButton.setDisable(true);
    update();
  }

  public void update() {
    try {
      tblAnnouncement.clear();
      tblAnnouncement.add(announcementDatabase.announcementObservableList());
    } catch (Exception e) {
      e.printStackTrace();
      DialogUtil.simpleErrorDialog(dialogPane, "Error", "Failed to update Announcement Table");
    }
  }
}

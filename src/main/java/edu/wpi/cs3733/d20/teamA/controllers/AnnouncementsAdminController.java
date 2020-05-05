package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.announcement.Announcement;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class AnnouncementsAdminController extends AbstractController {
  @FXML private Label lblTitle;

  @FXML private JFXTextArea textAnn;

  @FXML private JFXButton addButton;
  @FXML private JFXButton deleteButton;

  @FXML private StackPane dialogPane;

  @FXML private GridPane tableGridPane;

  private SimpleTableView<Announcement> tblAnnouncement;

  private Announcement selected;

  @FXML
  public void initialize() {
    lblTitle.setGraphic(new FontIcon(FontAwesomeSolid.BULLHORN));
    addButton.setGraphic(new FontIcon(FontAwesomeSolid.PLUS_CIRCLE));
    deleteButton.setGraphic(new FontIcon(FontAwesomeSolid.MINUS_CIRCLE));

    // Set up table
    tblAnnouncement = new SimpleTableView<>(new Announcement("", ""), 80);
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
    announcementDatabase.addAnnouncement(announcement);
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
      tblAnnouncement.add(announcementDatabase.getObservableList());
    } catch (Exception e) {
      e.printStackTrace();
      DialogUtil.simpleErrorDialog(dialogPane, "Error", "Failed to update Announcement Table");
    }
  }
}

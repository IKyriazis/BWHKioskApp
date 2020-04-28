package edu.wpi.cs3733.d20.teamA.controllers.dialog;

import com.jfoenix.controls.JFXListView;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import javafx.fxml.FXML;

public class NotificationController extends AbstractController {
  @FXML JFXListView<String> viewAnn;

  @FXML
  public void initialize() {
    // Turn off selection on listview
    viewAnn.setMouseTransparent(true);
    viewAnn.setFocusTraversable(false);

    // Clear existing announcements, if there somehow magically are any.
    viewAnn.getItems().clear();

    // Add new announcements
    // AnnouncementDatabase.getList().forEach(s -> viewAnn.getItems().add(s));
  }
}

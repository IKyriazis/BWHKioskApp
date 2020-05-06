package edu.wpi.cs3733.d20.teamA.controllers.dialog;

import com.jfoenix.controls.JFXDialog;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.announcement.Announcement;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

public class NotificationController extends AbstractController implements IDialogController {

  @FXML private GridPane tblPaneAnnouncement;
  private JFXDialog dialog;

  private SimpleTableView<Announcement> tblAnnouncement;

  @FXML
  public void initialize() {

    tblAnnouncement = new SimpleTableView<>(new Announcement("", ""), 20);
    tblPaneAnnouncement.getChildren().addAll(tblAnnouncement);
    update();
  }

  public void update() {

    try {
      tblAnnouncement.clear();

      tblAnnouncement.add(announcementDatabase.getObservableList());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void setDialog(JFXDialog dialog) {
    this.dialog = dialog;
  }
}

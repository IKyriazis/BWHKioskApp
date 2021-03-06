package edu.wpi.cs3733.d20.teamA.controllers;

import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.announcement.Announcement;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class AnnouncementsController extends AbstractController {

  @FXML private GridPane announcementGrid;
  private SimpleTableView<Announcement> announcementTbl;
  @FXML private StackPane dialogPane;
  @FXML private AnchorPane rootPane;
  @FXML private Label headerLabel;

  public void initialize() {
    // Set header label icon
    headerLabel.setGraphic(new FontIcon(FontAwesomeSolid.BULLHORN));

    announcementTbl = new SimpleTableView<>(new Announcement("", ""), 80);
    announcementGrid.getChildren().add(announcementTbl);
    update();
    rootPane.addEventHandler(
        TabSwitchEvent.TAB_SWITCH,
        event -> {
          update();
        });
  }

  public void update() {
    try {
      announcementTbl.clear();
      announcementTbl.add(announcementDatabase.getObservableList());
    } catch (Exception e) {
      e.printStackTrace();
      DialogUtil.simpleErrorDialog(dialogPane, "Error", "Failed to update Announcement Table");
    }
  }
}

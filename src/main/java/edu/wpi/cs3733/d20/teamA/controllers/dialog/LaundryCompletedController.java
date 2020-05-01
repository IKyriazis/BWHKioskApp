package edu.wpi.cs3733.d20.teamA.controllers.dialog;

import com.jfoenix.controls.JFXDialog;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.Laundry;
import edu.wpi.cs3733.d20.teamA.database.ServiceType;
import java.sql.Timestamp;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

public class LaundryCompletedController extends AbstractController implements IDialogController {
  @FXML private GridPane completedTablePane;

  private JFXDialog dialog;

  private SimpleTableView tblLaundryView;

  @FXML
  public void initialize() {

    tblLaundryView =
        new SimpleTableView<>(
            new Laundry("", "", "", "", "", new Timestamp(System.currentTimeMillis())), 80.0);
    completedTablePane.getChildren().add(tblLaundryView);

    update();
  }

  public void update() {
    try {
      tblLaundryView.clear();

      tblLaundryView.add(serviceDatabase.getObservableListService(ServiceType.LAUNDRY));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void setDialog(JFXDialog dialog) {
    this.dialog = dialog;
  }
}

package edu.wpi.cs3733.d20.teamA.controllers.nav;

import edu.wpi.cs3733.d20.teamA.App;
import edu.wpi.cs3733.d20.teamA.util.FXMLCache;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import edu.wpi.cs3733.d20.teamC.InterpreterRequest;
import java.io.IOException;
import java.util.function.BooleanSupplier;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material.Material;

public class ServiceHomeController extends AbstractNavPaneController {
  @FXML private AnchorPane rootPane;
  @FXML private GridPane buttonPane;

  public void initialize() {
    // Rebuild button pane when switching to this
    rootPane.addEventHandler(
        TabSwitchEvent.TAB_SWITCH,
        event -> {
          buildButtonPane();
        });

    buttonPane.setAlignment(Pos.TOP_CENTER);

    // Build initial button pane
    buildButtonPane();

    FXMLCache.preLoadFXML("views/reservation/Reservation.fxml");
  }

  private void buildButtonPane() {
    buttonPane.setAlignment(Pos.CENTER);
    buttonPane.getChildren().clear();

    // Services available to the public
    addButton(
        buttonPane,
        new FontIcon(Material.LOCAL_FLORIST),
        "views/flower/FlowerService.fxml",
        "Flowers");
    addButton(
        buttonPane,
        new FontIcon(FontAwesomeSolid.WHEELCHAIR),
        "views/service/InternalTransportRequest.fxml",
        "Internal\nTransport");

    addButton(
        buttonPane,
        new FontIcon(FontAwesomeSolid.GLOBE),
        "Interpreter",
        new BooleanSupplier() {
          @Override
          public boolean getAsBoolean() {
            try {
              Rectangle2D bounds = Screen.getPrimary().getBounds();
              InterpreterRequest.run(
                  (int) ((bounds.getWidth() - 1000) / 2),
                  (int) ((bounds.getHeight() - 1000) / 3),
                  1000,
                  1000,
                  App.class.getResource("stylesheet.css").toExternalForm(),
                  "NodeTarg",
                  "NodeEnd");
              return true;
            } catch (IOException e) {
              e.printStackTrace();
              return false;
            }
          }
        });

    addButton(buttonPane, new FontIcon(FontAwesomeSolid.HAMBURGER), "views/food/Food.fxml", "Food");

    addButton(
        buttonPane, new FontIcon(FontAwesomeSolid.GIFT), "views/service/GiftRequest.fxml", "Gifts");

    // Services available to employees
    if (eDB.getLoggedIn() != null) {

      String employeeTitle = eDB.getLoggedIn().getTitle();

      if (employeeTitle.equals("admin")
          || employeeTitle.equals("doctor")
          || employeeTitle.equals("nurse")
          || employeeTitle.equals("janitor")) {
        addButton(
            buttonPane,
            new FontIcon(Material.LOCAL_LAUNDRY_SERVICE),
            "views/service/LaundryRequest.fxml",
            "Laundry");
      }

      // services available to admins, doctors, and nurses
      if (employeeTitle.equals("admin")
          || employeeTitle.equals("doctor")
          || employeeTitle.equals("nurse")) {

        addButton(
            buttonPane,
            new FontIcon(FontAwesomeSolid.USER),
            "views/PatientsInfoService.fxml",
            "Patient\nInfo");

        addButton(
            buttonPane,
            new FontIcon(FontAwesomeSolid.MEDKIT),
            "views/service/MedicineRequest.fxml",
            "Medicine\nDelivery");

        addButton(
            buttonPane,
            new FontIcon(FontAwesomeSolid.PILLS),
            "views/service/PrescriptionRequest.fxml",
            "Prescriptions");

        addButton(
            buttonPane,
            new FontIcon(FontAwesomeSolid.CALENDAR_DAY),
            "views/reservation/Reservation.fxml",
            "Room\nScheduler");
        addButton(
            buttonPane,
            new FontIcon(FontAwesomeSolid.ID_BADGE),
            "Security Report",
            () -> {
              Rectangle2D primScreenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
              edu.wpi.cs3733.d20.teamB.api.IncidentReportApplication.run(
                  (int) ((primScreenBounds.getWidth() - 750) / 2),
                  (int) ((primScreenBounds.getHeight() - 500) / 3),
                  0,
                  0,
                  null,
                  "",
                  "");
              return true;
            });
      }

      // services excluded to retail employees
      if (!employeeTitle.equals("retail")) {
        addButton(
            buttonPane,
            new FontIcon(FontAwesomeSolid.STETHOSCOPE),
            "views/service/EquipRequest.fxml",
            "Equipment\nRequest");

        addButton(
            buttonPane,
            new FontIcon(FontAwesomeSolid.LAPTOP),
            "views/service/ITRequest.fxml",
            "Tech\nSupport");

        addButton(
            buttonPane,
            new FontIcon(FontAwesomeSolid.PAGER),
            "views/OnCallList.fxml",
            "Employees\nOn Call");
      }

      addButton(
          buttonPane,
          new FontIcon(FontAwesomeSolid.BROOM),
          "views/service/JanitorRequest.fxml",
          "Janitorial");

      addButton(
          buttonPane,
          new FontIcon(FontAwesomeSolid.LIST),
          "views/service/RequestViewer.fxml",
          "Service\nRequests");
    }
    equalizeButtonGrid(buttonPane);
  }
}

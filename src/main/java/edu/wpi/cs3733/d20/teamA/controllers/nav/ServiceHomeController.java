package edu.wpi.cs3733.d20.teamA.controllers.nav;

import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
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
  }

  private void buildButtonPane() {
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
        "views/service/InterpreterRequest.fxml",
        "Interpreters");

    // Services available to employees
    if (eDB.getLoggedIn() != null) {

      String employeeTitle = eDB.getLoggedIn().getTitle();
      System.out.println(employeeTitle);

      // services available to admins, doctors, and nurses
      if (employeeTitle.equals("admin")
          || employeeTitle.equals("doctor")
          || employeeTitle.equals("nurse")) {
        addButton(
            buttonPane,
            new FontIcon(FontAwesomeSolid.MEDKIT),
            "views/service/MedicineRequest.fxml",
            "Medicine\nDelivery");

        addButton(
            buttonPane,
            new FontIcon(FontAwesomeSolid.USER),
            "views/PatientsInfoService.fxml",
            "Patient\nInfo");

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
      }

      addButton(
          buttonPane,
          new FontIcon(FontAwesomeSolid.BROOM),
          "views/service/JanitorRequest.fxml",
          "Janitorial");

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

      addButton(
          buttonPane,
          new FontIcon(FontAwesomeSolid.BAND_AID),
          "APIService",
          "Appointment\nRequest");

      addButton(
          buttonPane,
          new FontIcon(FontAwesomeSolid.LIST),
          "views/service/RequestViewer.fxml",
          "Service\nRequests");
      addButton(
          buttonPane,
          new FontIcon(FontAwesomeSolid.PAGER),
          "views/OnCallList.fxml",
          "Employees\nOn Call");

      addButton(
          buttonPane,
          new FontIcon(FontAwesomeSolid.CALENDAR_DAY),
          "views/reservation/Reservation.fxml",
          "Room\nScheduler");
      addButton(
          buttonPane,
          new FontIcon(FontAwesomeSolid.BAND_AID),
          "APIService",
          "Appointment\nRequests");
    }
    equalizeButtonGrid(buttonPane);
  }
}

package edu.wpi.cs3733.d20.teamA.controllers.nav;

import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import javafx.fxml.FXML;
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
        "views/InternalTransportService.fxml",
        "Internal\nTransport");

    // Services available to employees
    // if (eDB.getLoggedIn() != null) {
    addButton(
        buttonPane, new FontIcon(FontAwesomeSolid.BROOM), "views/JanitorialGUI.fxml", "Janitorial");
    addButton(
        buttonPane,
        new FontIcon(FontAwesomeSolid.BULLHORN),
        "views/AnnouncementAdmin.fxml",
        "Announcements");
    addButton(
        buttonPane,
        new FontIcon(FontAwesomeSolid.MEDKIT),
        "views/MedicineRequest.fxml",
        "Medicine\nDelivery");
    addButton(
        buttonPane,
        new FontIcon(FontAwesomeSolid.STETHOSCOPE),
        "views/EquipReq.fxml",
        "Equipment\nRequest");
    addButton(
        buttonPane,
        new FontIcon(Material.LOCAL_LAUNDRY_SERVICE),
        "views/service/LaundryRequest.fxml",
        "Laundry");
    addButton(
        buttonPane,
        new FontIcon(FontAwesomeSolid.LAPTOP),
        "views/ITServices.fxml",
        "Tech\nSupport");
    addButton(
        buttonPane,
        new FontIcon(FontAwesomeSolid.USER),
        "views/PatientsInfoService.fxml",
        "Patient\nInfo");
    addButton(
        buttonPane,
        new FontIcon(FontAwesomeSolid.GLOBE),
        "views/InterpreterService.fxml",
        "Interpreters");
    addButton(
        buttonPane,
        new FontIcon(FontAwesomeSolid.PILLS),
        "views/PrescriptionService.fxml",
        "Prescriptions");
    // }

    equalizeButtonGrid(buttonPane);
  }
}

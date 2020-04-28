package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import edu.wpi.cs3733.d20.teamA.controls.VSwitcherBox;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import edu.wpi.cs3733.d20.teamA.util.ThreadPool;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class LoginController extends AbstractController {
  @FXML private VBox loginBox;
  @FXML private VBox buttonBox;
  @FXML private JFXSpinner spinner;
  @FXML private JFXButton loginButton;
  @FXML private JFXTextField usernameBox;
  @FXML private JFXPasswordField passwordBox;
  @FXML private StackPane dialogPane;
  @FXML private JFXButton logoutButton;

  @FXML private Pane rootPane;
  @FXML private Pane switcherPane;
  @FXML private Pane destPane;
  @FXML private Pane blockerPane;

  private boolean loggedIn = false;
  private boolean transitioning = false;

  @FXML
  public void initialize() {

    loginButton.getStyleClass().add("submitButton");

    // Creates the table if it doesn't exit
    if (eDB.getSizeEmployees() == -1) {
      eDB.dropTables();
      eDB.createTables();
      eDB.readEmployeeCSV();
    } else if (eDB.getSizeEmployees() == 0) {
      eDB.removeAllEmployees();
      eDB.readEmployeeCSV();
    }
    // Setup switcher box
    VSwitcherBox vSwitcherBox =
        new VSwitcherBox(destPane, new FontAwesomeIconView(FontAwesomeIcon.COGS));
    vSwitcherBox.addEntry(
        "Map Editor", new FontAwesomeIconView(FontAwesomeIcon.MAP_ALT), "views/MapEditor.fxml");
    vSwitcherBox.addEntry(
        "Flowers - William Engdahl",
        new MaterialIconView(MaterialIcon.LOCAL_FLORIST),
        "views/flower/FlowerAdmin.fxml");
    vSwitcherBox.addEntry(
        "Janitorial - Cory H",
        new FontAwesomeIconView(FontAwesomeIcon.CAR),
        "views/JanitorialGUI.fxml");
    vSwitcherBox.addEntry(
        "Announcements",
        new FontAwesomeIconView(FontAwesomeIcon.BULLHORN),
        "views/AnnouncementAdmin.fxml");
    vSwitcherBox.addEntry(
        "Equipment Request Eva L",
        new FontAwesomeIconView(FontAwesomeIcon.STETHOSCOPE),
        "views/EquipReq.fxml");
    vSwitcherBox.addEntry(
        "Laundry - Brennan",
        new MaterialIconView(MaterialIcon.LOCAL_LAUNDRY_SERVICE),
        "views/LaundryGUI.fxml");
    vSwitcherBox.addEntry(
        "IT Ticket Gabriel Dudlicek",
        new FontAwesomeIconView(FontAwesomeIcon.LAPTOP),
        "views/ITServices.fxml");
    vSwitcherBox.addEntry(
        "Patient Info - Tyler Looney",
        new MaterialIconView(MaterialIcon.PERSON_ADD),
        "views/PatientsInfoService.fxml");
    vSwitcherBox.addEntry(
        "Internal Transport - Ioannis K",
        new FontAwesomeIconView(FontAwesomeIcon.WHEELCHAIR),
        "views/InternalTransportAdmin.fxml");
    vSwitcherBox.addEntry(
        "Interpreters - Dyllan Cole",
        new FontAwesomeIconView(FontAwesomeIcon.GLOBE),
        "views/InterpreterService.fxml");
    vSwitcherBox.addEntry(
        "Prescriptions - Yash Patel",
        new MaterialIconView(MaterialIcon.LOCAL_PHARMACY),
        "views/PrescriptionService.fxml");
    vSwitcherBox.addEntry(
        "Manage Accounts", new FontAwesomeIconView(FontAwesomeIcon.USER), "views/CreateAcct.fxml");
    vSwitcherBox.setTransitionMillis(500);

    // Add switcher box to anchor pane and constrain it
    switcherPane.getChildren().add(vSwitcherBox);
    AnchorPane.setBottomAnchor(vSwitcherBox, 0.0);
    AnchorPane.setTopAnchor(vSwitcherBox, 0.0);

    // Add drop shadow to login box.
    DropShadow dropShadow = new DropShadow();
    dropShadow.setRadius(12.0);
    dropShadow.setOffsetX(5.0);
    dropShadow.setOffsetY(5.0);
    dropShadow.setColor(Color.GREY);
    loginBox.setEffect(dropShadow);

    // Setup button icons
    loginButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.LOCK));
    logoutButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SIGN_OUT));

    // Setup enter key to go from Username -> Password -> Login
    usernameBox.setOnAction(event -> passwordBox.requestFocus());
    passwordBox.setOnAction(event -> loginButton.requestFocus());

    // Pass events through to root pane
    rootPane.addEventFilter(
        TabSwitchEvent.TAB_SWITCH,
        event -> {
          if (event.getTarget().equals(rootPane)) {
            destPane.getChildren().forEach(node -> node.fireEvent(new TabSwitchEvent()));
          }
        });
  }

  @FXML
  public void loginButtonPressed() {
    if (transitioning || loggedIn) {
      return;
    }

    if (usernameBox.getText().isEmpty() || passwordBox.getText().isEmpty()) {
      DialogUtil.simpleErrorDialog(
          dialogPane, "No Credentials", "Please enter your credentials and try again");
      return;
    }

    // Disable login button, hide text fields, and show spinner.
    loginButton.setDisable(true);

    // Fade out buttons
    buttonBox.setDisable(true);
    FadeTransition boxOutFade = new FadeTransition(Duration.millis(250), buttonBox);
    boxOutFade.setFromValue(1.0);
    boxOutFade.setToValue(0.0);
    boxOutFade.play();

    // Fade in spinner
    FadeTransition spinnerInFade = new FadeTransition(Duration.millis(250), spinner);
    spinnerInFade.setFromValue(0.0);
    spinnerInFade.setToValue(1.0);
    spinnerInFade.play();

    ThreadPool.runBackgroundTask(
        () -> {
          if (!eDB.logIn(usernameBox.getText(), passwordBox.getText())) {
            Platform.runLater(
                () -> {
                  DialogUtil.simpleErrorDialog(
                      dialogPane,
                      "Incorrect Login",
                      "Please reenter your credentials and try again");
                  usernameBox.setText("");
                  passwordBox.setText("");

                  // Fade in buttons
                  buttonBox.setDisable(false);
                  FadeTransition boxInFade = new FadeTransition(Duration.millis(250), buttonBox);
                  boxInFade.setFromValue(0.0);
                  boxInFade.setToValue(1.0);
                  boxInFade.play();

                  // Fade out spinner
                  FadeTransition spinnerOutFade = new FadeTransition(Duration.millis(250), spinner);
                  spinnerOutFade.setFromValue(1.0);
                  spinnerOutFade.setToValue(0.0);
                  spinnerOutFade.play();

                  // Re-enable
                  loginButton.setDisable(false);
                });
          } else {
            eDB.addLog(usernameBox.getText());
            Platform.runLater(
                () -> {
                  // Chuck the login box way off screen
                  transitioning = true;
                  TranslateTransition translate =
                      new TranslateTransition(Duration.millis(1000), loginBox);
                  translate.setByY(-2000f);
                  translate.setOnFinished(
                      event -> {
                        // Clear username / password once they're off screen
                        usernameBox.setText("");
                        passwordBox.setText("");
                        transitioning = false;

                        // Reset visibility of stuff in box
                        buttonBox.setDisable(false);
                        buttonBox.setOpacity(1.0);
                        spinner.setOpacity(0.0);
                        loginButton.setDisable(false);
                      });
                  translate.play();

                  // Fade out the background
                  FadeTransition fade = new FadeTransition(Duration.millis(500), blockerPane);
                  fade.setFromValue(1.0);
                  fade.setToValue(0.0);
                  fade.setOnFinished(event -> blockerPane.setMouseTransparent(true));
                  fade.play();

                  loggedIn = true;
                });
          }
        });
  }

  @FXML
  public void logoutButtonPressed() {
    if (!loggedIn || transitioning) {
      return;
    }
    eDB.changeFlag();
    transitioning = true;
    TranslateTransition translate = new TranslateTransition(Duration.millis(1000), loginBox);
    translate.setByY(2000f);
    translate.setOnFinished(event -> transitioning = false);
    translate.play();

    // Fade in the background
    FadeTransition fade = new FadeTransition(Duration.millis(500), blockerPane);
    fade.setFromValue(0.0);
    fade.setToValue(1.0);
    fade.setOnFinished(event -> blockerPane.setMouseTransparent(false));
    fade.play();

    loggedIn = false;
  }
}

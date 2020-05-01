package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.*;
import de.taimos.totp.TOTP;
import edu.wpi.cs3733.d20.teamA.controls.VSwitcherBox;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import edu.wpi.cs3733.d20.teamA.util.ThreadPool;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class LoginController extends AbstractController {
  @FXML private VBox loginBox;
  @FXML private VBox buttonBox;
  @FXML private VBox gauth;
  @FXML private JFXSpinner spinner;
  @FXML private JFXButton loginButton;
  @FXML private JFXButton authenticateButton;
  @FXML private JFXTextField usernameBox;
  @FXML private JFXTextField gauthCode;
  @FXML private JFXPasswordField passwordBox;
  @FXML private StackPane dialogPane;
  @FXML private JFXButton logoutButton;

  @FXML private Pane rootPane;
  @FXML private Pane switcherPane;
  @FXML private Pane destPane;
  @FXML private Pane blockerPane;

  private boolean loggedIn = false;
  private boolean transitioning = false;
  private String username;

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
    VSwitcherBox vSwitcherBox = new VSwitcherBox(destPane, new FontIcon(FontAwesomeSolid.COGS));
    vSwitcherBox.addEntry("Map Editor", new FontIcon(FontAwesomeSolid.MAP), "views/MapEditor.fxml");
    vSwitcherBox.addEntry(
        "Flowers - Will",
        new FontIcon(FontAwesomeRegular.PAPER_PLANE),
        "views/flower/FlowerAdmin.fxml");
    vSwitcherBox.addEntry(
        "Janitorial - Cory", new FontIcon(FontAwesomeSolid.CAR), "views/JanitorialGUI.fxml");
    vSwitcherBox.addEntry(
        "Announcements", new FontIcon(FontAwesomeSolid.BULLHORN), "views/AnnouncementAdmin.fxml");
    vSwitcherBox.addEntry(
        "Medicine Delivery - Maddie",
        new FontIcon(FontAwesomeSolid.MEDKIT),
        "views/MedicineRequest.fxml");
    vSwitcherBox.addEntry(
        "Equipment Request - Eva",
        new FontIcon(FontAwesomeSolid.STETHOSCOPE),
        "views/EquipReq.fxml");
    vSwitcherBox.addEntry(
        "Laundry - Brennan", new FontIcon(FontAwesomeSolid.TINT), "views/LaundryGUI.fxml");
    vSwitcherBox.addEntry(
        "IT Ticket - Gabriel", new FontIcon(FontAwesomeSolid.LAPTOP), "views/ITServices.fxml");
    vSwitcherBox.addEntry(
        "Patient Info - Tyler",
        new FontIcon(FontAwesomeSolid.USER_PLUS),
        "views/PatientsInfoService.fxml");
    vSwitcherBox.addEntry(
        "Internal Transport - Ioannis",
        new FontIcon(FontAwesomeSolid.WHEELCHAIR),
        "views/InternalTransportAdmin.fxml");
    vSwitcherBox.addEntry(
        "Interpreters - Dyllan",
        new FontIcon(FontAwesomeSolid.GLOBE),
        "views/InterpreterService.fxml");
    vSwitcherBox.addEntry(
        "Prescriptions - Yash",
        new FontIcon(FontAwesomeSolid.PILLS),
        "views/PrescriptionService.fxml");
    vSwitcherBox.addEntry(
        "Manage Accounts", new FontIcon(FontAwesomeSolid.USER), "views/CreateAcct.fxml");
    vSwitcherBox.addEntry("Settings", new FontIcon(FontAwesomeSolid.COG), "views/Settings.fxml");
    vSwitcherBox.setTransitionMillis(500);

    // Add switcher box to anchor pane and constrain it
    switcherPane.getChildren().add(vSwitcherBox);

    // Add drop shadow to login box.
    DropShadow dropShadow = new DropShadow();
    dropShadow.setRadius(12.0);
    dropShadow.setOffsetX(5.0);
    dropShadow.setOffsetY(5.0);
    dropShadow.setColor(Color.GREY);
    loginBox.setEffect(dropShadow);

    // Setup button icons
    loginButton.setGraphic(new FontIcon(FontAwesomeSolid.LOCK));
    logoutButton.setGraphic(new FontIcon(FontAwesomeSolid.SIGN_OUT_ALT));

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
            if (eDB.getSecretKey(usernameBox.getText()) == null) {
              System.out.println("Secret key is null");
              Platform.runLater(
                  () -> {
                    eDB.addLog(usernameBox.getText());
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
            Platform.runLater(
                () -> {
                  username = usernameBox.getText();
                  // Fade out spinner
                  FadeTransition spinnerOutFade = new FadeTransition(Duration.millis(250), spinner);
                  spinnerOutFade.setFromValue(1.0);
                  spinnerOutFade.setToValue(0.0);
                  spinnerOutFade.play();

                  buttonBox.setVisible(false);
                  gauth.setVisible(true);
                  loginButton.setVisible(false);
                  authenticateButton.setVisible(true);
                });
          }
        });
  }

  public void authenticateButtonPressed() {
    String secretKey = eDB.getSecretKey(username);
    if (gauthCode.getText().equals(getTOTPCode(secretKey))) {
      eDB.addLog(usernameBox.getText());
      // Chuck the login box way off screen
      transitioning = true;
      TranslateTransition translate = new TranslateTransition(Duration.millis(1000), loginBox);
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
    } else {
      DialogUtil.simpleErrorDialog(
          dialogPane, "Incorrect code", "You have entered an incorrect Google Authenticator code.");
    }
  }

  public static String getTOTPCode(String secretKey) {
    Base32 base32 = new Base32();
    byte[] bytes = base32.decode(secretKey);
    String hexKey = Hex.encodeHexString(bytes);
    return TOTP.getOTP(hexKey);
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

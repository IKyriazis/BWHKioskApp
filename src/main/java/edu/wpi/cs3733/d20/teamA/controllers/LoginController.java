package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.controls.VSwitcherBox;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import java.sql.SQLException;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class LoginController extends AbstractController {
  @FXML private VBox loginBox;
  @FXML private JFXButton loginButton;
  @FXML private JFXTextField usernameBox;
  @FXML private JFXPasswordField passwordBox;
  @FXML private StackPane dialogPane;
  @FXML private JFXButton logoutButton;

  @FXML private Pane rootPane;
  @FXML private Pane switcherPane;
  @FXML private Pane destPane;
  @FXML private Pane blockerPane;

  private GaussianBlur currentBlur;
  private Scene appPrimaryScene;
  private boolean loggedIn = false;
  private boolean transitioning = false;

  @FXML
  public void initialize() throws SQLException {
    eDB.addEmployee("Admin", "Eva", "Labbe", "Janitor");

    // Setup switcher box
    VSwitcherBox vSwitcherBox =
        new VSwitcherBox(destPane, new FontAwesomeIconView(FontAwesomeIcon.COGS));
    vSwitcherBox.addEntry(
        "Map Editor", new FontAwesomeIconView(FontAwesomeIcon.MAP_ALT), "views/MapEditor.fxml");
    vSwitcherBox.addEntry(
        "Janitor GUI", new FontAwesomeIconView(FontAwesomeIcon.CAR), "views/JanitorialGUI.fxml");
    vSwitcherBox.addEntry(
        "Announcements",
        new FontAwesomeIconView(FontAwesomeIcon.BULLHORN),
        "views/AnnouncementAdmin.fxml");

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
  }

  @FXML
  public void loginButtonPressed() throws SQLException {
    if (transitioning || loggedIn) {
      return;
    }

    if (usernameBox.getText().isEmpty() || passwordBox.getText().isEmpty()) {
      DialogUtil.simpleErrorDialog(
          dialogPane, "No Credentials", "Please enter your credentials and try again");
      return;
    }

    if (!eDB.logIn(usernameBox.getText(), passwordBox.getText())) {
      DialogUtil.simpleErrorDialog(
          dialogPane, "Incorrect Login", "Please reenter your credentials and try again");
      usernameBox.setText("");
      passwordBox.setText("");
      return;
    }

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
        });
    translate.play();

    // Fade out the background
    FadeTransition fade = new FadeTransition(Duration.millis(500), blockerPane);
    fade.setFromValue(1.0);
    fade.setToValue(0.0);
    fade.setOnFinished(
        event -> {
          blockerPane.setMouseTransparent(true);
        });
    fade.play();

    loggedIn = true;
  }

  @FXML
  public void logoutButtonPressed() {
    if (!loggedIn || transitioning) {
      return;
    }

    transitioning = true;
    TranslateTransition translate = new TranslateTransition(Duration.millis(1000), loginBox);
    translate.setByY(2000f);
    translate.setOnFinished(
        event -> {
          transitioning = false;
        });
    translate.play();

    // Fade in the background
    FadeTransition fade = new FadeTransition(Duration.millis(500), blockerPane);
    fade.setFromValue(0.0);
    fade.setToValue(1.0);
    fade.setOnFinished(
        event -> {
          blockerPane.setMouseTransparent(false);
        });
    fade.play();

    loggedIn = false;
  }
}

package edu.wpi.cs3733.d20.teamA.controllers;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import java.sql.SQLException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class LoginController extends AbstractController {
  @FXML private VBox loginBox;
  @FXML private JFXButton loginButton;
  @FXML private JFXTabPane tabPane;
  @FXML private JFXTextField usernameBox;
  @FXML private JFXPasswordField passwordBox;
  @FXML private StackPane dialogPane;
  @FXML private JFXButton logoutButton;

  private GaussianBlur currentBlur;
  private Scene appPrimaryScene;
  private boolean loggedIn = false;
  private boolean transitioning = false;

  /**
   * This method allows the tests to inject the scene at a later time, since it must be done on the
   * JavaFX thread
   *
   * @param appPrimaryScene Primary scene of the app whose root will be changed
   */
  @Inject
  public void setAppPrimaryScene(Scene appPrimaryScene) {
    this.appPrimaryScene = appPrimaryScene;
  }

  @FXML
  public void initialize() throws SQLException {
    eDB.addEmployee("Admin", "Eva", "Labbe", "Janitor");
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

    // Blur everything in background
    currentBlur = new GaussianBlur();
    currentBlur.setRadius(15.0);

    tabPane.setEffect(currentBlur);

    // Setup enter key to go from Username -> Password -> Login
    usernameBox.setOnAction(event -> passwordBox.requestFocus());
    passwordBox.setOnAction(event -> loginButton.requestFocus());
  }

  @FXML
  public void loginButtonPressed() throws SQLException {
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

    // Fade away background blur
    Timeline blurFader = new Timeline();
    blurFader.setCycleCount(5);
    blurFader.setAutoReverse(false);
    blurFader
        .getKeyFrames()
        .add(
            new KeyFrame(
                Duration.millis(100),
                event -> {
                  currentBlur.setRadius(currentBlur.getRadius() - 3);
                  if (currentBlur.getRadius() == 0) {
                    tabPane.setEffect(null);
                    tabPane.setMouseTransparent(false);
                  }
                }));
    blurFader.play();
    loggedIn = true;
  }

  @FXML
  public void logoutButtonPressed() {
    if (!loggedIn || transitioning) {
      return;
    }

    TranslateTransition translate = new TranslateTransition(Duration.millis(1000), loginBox);
    translate.setByY(2000f);
    translate.play();

    // Put blur back
    tabPane.setEffect(currentBlur);

    // Fade in
    Timeline blurFader = new Timeline();
    blurFader.setCycleCount(5);
    blurFader.setAutoReverse(false);
    blurFader
        .getKeyFrames()
        .add(
            new KeyFrame(
                Duration.millis(100),
                event -> {
                  currentBlur.setRadius(currentBlur.getRadius() + 3);
                  tabPane.setMouseTransparent(true);
                }));
    blurFader.play();

    loggedIn = false;
  }
}

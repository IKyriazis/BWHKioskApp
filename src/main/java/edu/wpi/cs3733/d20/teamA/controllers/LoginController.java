package edu.wpi.cs3733.d20.teamA.controllers;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.database.FlowerDatabase;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.sql.SQLException;

public class LoginController extends AbstractController{
  @FXML private VBox loginBox;
  @FXML private Button loginButton;
  @FXML private JFXTabPane tabPane;
  @FXML private JFXTextField usernameBox;
  @FXML private JFXTextField passwordBox;

  private GaussianBlur currentBlur;

  private Scene appPrimaryScene;

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
  public void initialize() {
    eDB.addEmployee("Admin", "Eva", "Labbe", "Janitor");
    // Add drop shadow to login box.
    DropShadow dropShadow = new DropShadow();
    dropShadow.setRadius(12.0);
    dropShadow.setOffsetX(5.0);
    dropShadow.setOffsetY(5.0);
    dropShadow.setColor(Color.GREY);
    loginBox.setEffect(dropShadow);

    // Setup login button icon
    loginButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.LOCK));

    // Blur everything in background
    currentBlur = new GaussianBlur();
    currentBlur.setRadius(15.0);

    tabPane.setEffect(currentBlur);
  }

  @FXML
  public void loginButtonPressed() throws SQLException {
    // TODO; Real login

    if(!eDB.logIn(usernameBox.getText(), passwordBox.getText())){

      //Doesn't pass

    }


    // Chuck the login box way off screen
    TranslateTransition translate = new TranslateTransition(Duration.millis(1000), loginBox);
    translate.setByY(-2000f);
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
  }


}

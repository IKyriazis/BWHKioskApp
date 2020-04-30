package edu.wpi.cs3733.d20.teamA.controllers;

import animatefx.animation.*;
import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamA.util.FXMLCache;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import java.util.Stack;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class SceneSwitcherController {
  @FXML private ImageView backgroundImage;
  @FXML private StackPane rootPane;
  @FXML private JFXButton backButton;
  @FXML private JFXButton loginButton;
  @FXML private GridPane contentPane;

  private static SceneSwitcherController instance;

  private Stack<Node> sceneStack;

  private boolean transitioning;

  private FontIcon homeIcon;
  private FontIcon backIcon;

  @FXML
  public void initialize() {
    // Bind background image to just outside the bounds of the window for proper formatting
    rootPane
        .widthProperty()
        .addListener(
            observable -> {
              backgroundImage.setFitWidth(rootPane.getWidth() + 50.0);
            });

    rootPane
        .heightProperty()
        .addListener(
            observable -> {
              backgroundImage.setFitHeight(rootPane.getHeight() + 50.0);
            });

    // Setup home button
    homeIcon = new FontIcon(FontAwesomeSolid.HOME);
    backIcon = new FontIcon(FontAwesomeSolid.LONG_ARROW_ALT_LEFT);

    // Setup login button icon
    loginButton.setGraphic(new FontIcon(FontAwesomeSolid.SIGN_IN_ALT));

    // Set this equal to instance
    instance = this;

    // Setup scene stack
    sceneStack = new Stack<>();

    pushScene("views/nav/MainMenu.fxml");

    // Sometimes buttons start selected for some reason
    rootPane.requestFocus();
  }

  @FXML
  public void pressedBack() {
    if (!transitioning) {
      popScene();
    }
  }

  @FXML
  public void pressedLogin() {}

  private void transition(boolean right) {
    if (transitioning) {
      return;
    }
    boolean first = contentPane.getChildren().isEmpty();

    Node top = sceneStack.peek();

    // Fire off tab switch event to new scene
    top.fireEvent(new TabSwitchEvent());

    contentPane.getChildren().add(top);
    if (!first) {
      transitioning = true;

      AnimationFX transOut =
          right
              ? new FadeOutLeft(contentPane.getChildren().get(0))
              : new FadeOutRight(contentPane.getChildren().get(0));
      transOut.setOnFinished(
          event -> {
            contentPane.getChildren().remove(transOut.getNode());
          });
      transOut.play();

      AnimationFX transIn = right ? new FadeInRight(top) : new FadeInLeft(top);
      transIn.setOnFinished(
          event -> {
            transitioning = false;
          });
      transIn.play();
    }

    if (sceneStack.size() > 2) {
      backButton.setGraphic(backIcon);
      backButton.setVisible(true);
    } else if (sceneStack.size() == 2) {
      backButton.setGraphic(homeIcon);
      backButton.setVisible(true);
    } else {
      backButton.setVisible(false);
    }

    rootPane.requestFocus();
  }

  private static void pushScene(Node newNode) {
    if (instance == null) {
      return;
    }

    instance.sceneStack.push(newNode);
    instance.transition(true);
  }

  private static Node popScene() {
    if (instance == null || instance.sceneStack.size() <= 1) {
      return null;
    }

    Node old = instance.sceneStack.pop();
    instance.transition(false);
    return old;
  }

  public static void pushScene(String fxmlPath) {
    Node node = FXMLCache.loadFXML(fxmlPath);
    pushScene(node);
  }
}

package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamA.util.FXMLCache;
import java.util.Stack;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class SceneSwitcherController {
  @FXML private ImageView backgroundImage;
  @FXML private StackPane rootPane;
  @FXML private JFXButton backButton;
  @FXML private GridPane contentPane;

  private static SceneSwitcherController instance;

  private Stack<Node> sceneStack;

  private boolean transitioning;

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

    // Set this equal to instance
    instance = this;

    // Setup scene stack
    sceneStack = new Stack<>();

    pushScene("views/nav/MainMenu.fxml");
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
    contentPane.getChildren().add(top);
    if (!first) {
      top.setTranslateX(right ? contentPane.getWidth() : -contentPane.getWidth());

      transitioning = true;
      TranslateTransition transOut =
          new TranslateTransition(Duration.millis(500), contentPane.getChildren().get(0));
      transOut.setByX(right ? -contentPane.getWidth() : contentPane.getWidth());
      transOut.setOnFinished(
          event -> {
            contentPane.getChildren().remove(transOut.getNode());
          });
      transOut.play();

      TranslateTransition transIn = new TranslateTransition(Duration.millis(500), top);
      transIn.setByX(right ? -contentPane.getWidth() : contentPane.getWidth());
      transIn.setOnFinished(
          event -> {
            transitioning = false;
          });
      transIn.play();
    }

    if (sceneStack.size() > 2) {
      backButton.setText("Back");
      backButton.setVisible(true);
    } else if (sceneStack.size() == 2) {
      backButton.setText("Home");
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

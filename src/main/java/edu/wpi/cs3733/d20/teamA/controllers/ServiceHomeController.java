package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.util.FXMLCache;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class ServiceHomeController {
  @FXML private Label iconLabel;
  @FXML private VBox buttonBox;

  @FXML private JFXButton flowerButton;
  @FXML private JFXButton service1Button;
  @FXML private JFXButton service2Button;
  @FXML private JFXButton service3Button;

  @FXML private Pane viewPane;
  @FXML private Pane rootPane;

  private JFXButton selected;
  private boolean transitioning = false;

  public void initialize() {
    // Set icons
    iconLabel.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.LIST_ALT));
    flowerButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.LEAF));
    service1Button.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CIRCLE));
    service2Button.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CIRCLE));
    service3Button.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CIRCLE));

    // Add drop shadow to VBox
    DropShadow dropShadow = new DropShadow();
    dropShadow.setRadius(8.0);
    buttonBox.setEffect(dropShadow);

    // Set selection state
    flowerButton.setStyle("-fx-background-color: #BDBDBD");
    selected = flowerButton;
    viewPane.getChildren().add(FXMLCache.loadFXML("views/FlowerService.fxml"));

    // Pass tab switch events through to current view
    rootPane.addEventFilter(
        TabSwitchEvent.TAB_SWITCH,
        event -> {
          if (event.getTarget().equals(rootPane)) {
            viewPane.getChildren().forEach(node -> node.fireEvent(new TabSwitchEvent()));
          }
        });
  }

  @FXML
  public void pressedServiceButton(ActionEvent event) {
    if (event.getSource() == selected || transitioning) {
      return;
    }

    // Calculate direction of transition
    boolean translateUp = false;
    if (((JFXButton) event.getSource()).getLayoutY() > selected.getLayoutY()) {
      translateUp = true;
    }

    // Update selection info
    selected.setStyle("-fx-background-color: white");
    ((JFXButton) event.getSource()).setStyle("-fx-background-color: #BDBDBD");
    selected = (JFXButton) event.getSource();

    // Transition out current view
    TranslateTransition translateOut = null;
    if (!viewPane.getChildren().isEmpty()) {
      Node oldNode = viewPane.getChildren().get(0);
      translateOut = new TranslateTransition(Duration.millis(1000), oldNode);
      translateOut.setByY(translateUp ? -viewPane.getHeight() : viewPane.getHeight());
      translateOut.setOnFinished(
          e -> {
            viewPane.getChildren().remove(oldNode);
          });
    }

    // Slot in new fxml file
    Node newNode = null;
    if (selected == flowerButton) {
      newNode = FXMLCache.loadFXML("views/FlowerService.fxml");
    } else {
      newNode = FXMLCache.loadFXML("views/JanitorialGUI.fxml");
    }

    if (viewPane.getChildren().contains(newNode) && translateOut != null) {
      newNode.setTranslateX(0);
      newNode.setTranslateY(0);
    } else {
      viewPane.getChildren().add(newNode);

      newNode.setTranslateY(translateUp ? viewPane.getHeight() : -viewPane.getHeight());
      TranslateTransition translateIn = new TranslateTransition(Duration.millis(1000), newNode);
      translateIn.setByY(translateUp ? -viewPane.getHeight() : viewPane.getHeight());
      translateIn.setOnFinished(
          e -> {
            transitioning = false;
          });
      translateIn.play();

      if (translateOut != null) {
        translateOut.play();
      }

      transitioning = true;
    }
  }
}

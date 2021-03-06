package edu.wpi.cs3733.d20.teamA.controls;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamA.util.FXMLCache;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import java.util.HashMap;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

public class VSwitcherBox extends VBox {
  private final Pane destPane;

  private final HashMap<JFXButton, String> map;
  private final Label iconLabel;
  private JFXButton selected;

  private boolean minimized = true;
  private double minimizedWidth = Double.MAX_VALUE;
  private Animation widthTransition;

  private boolean transitioning = false;
  private int transitionTime = 1000;

  private static final String iconStyle = "-fx-font-size: 36pt;";
  private static final String buttonStyle =
      "-fx-font-size: 22pt;"
          + "-fx-background-radius: 0px;"
          + "-fx-text-fill: -secondary-color-text;";

  public VSwitcherBox(Pane destPane, Node topIcon) {
    this.destPane = destPane;
    this.map = new HashMap<>();

    // Set background to white
    setStyle("-fx-background-color: -secondary-color-light");

    // Center all children
    setAlignment(Pos.TOP_CENTER);

    // Add icon label
    iconLabel = new Label("", topIcon);
    iconLabel.setPrefWidth(getWidth());
    iconLabel.setStyle(iconStyle);
    iconLabel.setAlignment(Pos.CENTER);
    iconLabel.setPadding(new Insets(0, 2, 0, 2));
    getChildren().add(iconLabel);

    // Add mouseover listener
    setOnMouseEntered(
        event -> {
          if (widthTransition != null) {
            widthTransition.stop();
          }
          (widthTransition = widthTransition(getMaxContentWidth())).play();
          widthTransition.setOnFinished(
              e -> {
                widthTransition = null;
              });
        });
    setOnMouseExited(
        event -> {
          if (widthTransition != null) {
            widthTransition.stop();
          }
          (widthTransition = widthTransition(getMaxIconWidth())).play();
          widthTransition.setOnFinished(
              e -> {
                widthTransition = null;
              });
        });

    // Add drop shadow to self
    DropShadow dropShadow = new DropShadow();
    dropShadow.setRadius(8.0);
    setEffect(dropShadow);
  }

  private void setSelected(JFXButton newSelection) {
    if (selected != null) {
      selected.setStyle(buttonStyle + "-fx-background-color: white");
    }
    newSelection.setStyle(buttonStyle + "-fx-background-color: -primary-color-light");

    selected = newSelection;
  }

  public void addEntry(String label, FontIcon graphic, String fxmlPath) {
    JFXButton button = new JFXButton(label, graphic);
    button.getStyleClass().add("sidebar-button");
    button.setEllipsisString("");
    button.setAlignment(Pos.CENTER_LEFT);
    button.setButtonType(JFXButton.ButtonType.FLAT);
    button.setStyle(buttonStyle + "-fx-background-color: -secondary-color-light");
    button.setOnAction(this::switchView);

    // Load initial scene if this is the first button added
    if (map.size() == 0) {
      setSelected(button);
      destPane.getChildren().add(FXMLCache.loadFXML(fxmlPath));
    }

    // Add button to this VBox
    getChildren().add(button);

    // Add button to fxml mapping
    map.put(button, fxmlPath);

    // Resize to fit button
    setPrefWidth(getMaxContentWidth());
  }

  private void switchView(ActionEvent event) {
    JFXButton source = (JFXButton) event.getSource();
    if (source == selected || transitioning) {
      return;
    }

    // Calculate direction of transition
    boolean translateUp = false;
    if (source.getLayoutY() > selected.getLayoutY()) {
      translateUp = true;
    }

    // Update selection info
    setSelected(source);

    // Transition out current view
    TranslateTransition translateOut = null;
    if (!destPane.getChildren().isEmpty()) {
      Node oldNode = destPane.getChildren().get(0);
      translateOut = new TranslateTransition(Duration.millis(transitionTime), oldNode);
      translateOut.setByY(translateUp ? -destPane.getHeight() : destPane.getHeight());
      translateOut.setOnFinished(e -> destPane.getChildren().remove(oldNode));
    }

    // Slot in new fxml file
    Node newNode = FXMLCache.loadFXML(map.get(source));
    if (newNode != null) {
      if (destPane.getChildren().contains(newNode) && translateOut != null) {
        newNode.setTranslateX(0);
        newNode.setTranslateY(0);
      } else {
        destPane.getChildren().add(newNode);

        newNode.setTranslateY(translateUp ? destPane.getHeight() : -destPane.getHeight());
        TranslateTransition translateIn =
            new TranslateTransition(Duration.millis(transitionTime), newNode);
        translateIn.setByY(translateUp ? -destPane.getHeight() : destPane.getHeight());
        translateIn.setOnFinished(e -> transitioning = false);
        translateIn.play();

        if (translateOut != null) {
          translateOut.play();
        }

        transitioning = true;
      }

      // Fire off tab switch event to notify new tab that it was swapped in
      newNode.fireEvent(new TabSwitchEvent());
    }
  }

  private Animation widthTransition(double goal) {
    final double startWidth = getWidth();
    return new Transition() {
      {
        setCycleDuration(Duration.millis(250));
      }

      @Override
      protected void interpolate(double frac) {
        setPrefWidth(startWidth + frac * (goal - startWidth));
      }
    };
  }

  private double getMaxContentWidth() {
    double maxWidth = 0.0;
    for (Node node : getChildren()) {
      maxWidth = Math.max(maxWidth, node.prefWidth(getHeight()));
    }
    return maxWidth;
  }

  private double getMaxIconWidth() {
    double maxGraphicWidth = 0.0;
    for (JFXButton button : map.keySet()) {
      maxGraphicWidth =
          Math.max(maxGraphicWidth, button.getGraphic().prefWidth(button.getHeight()));
    }
    return Math.max(maxGraphicWidth, iconLabel.getGraphic().prefWidth(iconLabel.getHeight()));
  }

  @Override
  public void resize(double width, double height) {
    super.resize(width, height);

    if (getWidth() < minimizedWidth) {
      minimizedWidth = getWidth();
    }

    // Resize contents to max
    iconLabel.setMaxWidth(getWidth());
    map.keySet().forEach(button -> button.setMaxWidth(getWidth()));

    // Fit padding on destpane for overlap only when expanded
    double labelIconWidth = iconLabel.getGraphic().prefWidth(iconLabel.getHeight());
    destPane.setPadding(new Insets(0, 0, 0, minimizedWidth));

    // Offset labels by deviation from max icon width
    double maxIconWidth = getMaxIconWidth();
    map.keySet()
        .forEach(
            button -> {
              double graphicWidth = button.getGraphic().prefWidth(button.getHeight());
              double gap = (maxIconWidth - graphicWidth);
              button.setPadding(new Insets(0, 0, 0, gap / 2 + 4));
              button.setGraphicTextGap(gap / 2 + 4);
            });
  }

  public void setTransitionMillis(int transitionTime) {
    this.transitionTime = transitionTime;
  }
}

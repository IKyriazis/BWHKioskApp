package edu.wpi.cs3733.d20.teamA.controllers;

import animatefx.animation.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.FXMLCache;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import edu.wpi.cs3733.d20.teamA.util.ThreadPool;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Stack;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class SceneSwitcherController extends AbstractController {
  @FXML private ImageView backgroundImage;
  @FXML private StackPane rootPane;

  @FXML private JFXButton backButton;
  @FXML private JFXButton signInButton;
  @FXML private JFXButton loginButton;

  @FXML private JFXTextField usernameBox;
  @FXML private JFXPasswordField passwordBox;
  @FXML private JFXSpinner spinner;

  @FXML private AnchorPane signInPane;
  @FXML private GridPane blockerPane;
  @FXML private GridPane contentPane;
  @FXML private VBox loginBox;
  @FXML private VBox buttonBox;

  @FXML private Label timeLabel;
  @FXML private Label dateLabel;

  private static SceneSwitcherController instance;

  private Stack<Node> sceneStack;

  private boolean transitioning = false;
  private boolean loginTransitioning = false;
  private boolean loggedIn = false;

  private FontIcon homeIcon;
  private FontIcon backIcon;

  private Label usernameLabel;
  private Date date;

  @FXML
  public void initialize() {
    // Create the employee table if it doesn't exist
    if (eDB.getSizeEmployees() == -1) {
      eDB.dropTables();
      eDB.createTables();
      eDB.readEmployeeCSV();
    } else if (eDB.getSizeEmployees() == 0) {
      eDB.removeAllEmployees();
      eDB.readEmployeeCSV();
    }

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

    // Setup sign in button icon
    signInButton.setGraphic(new FontIcon(FontAwesomeSolid.SIGN_IN_ALT));

    // Setup login button icon
    loginButton.setGraphic(new FontIcon(FontAwesomeSolid.LOCK));

    // Set this equal to instance
    instance = this;

    // Setup scene stack
    sceneStack = new Stack<>();
    pushScene("views/nav/MainMenu.fxml");

    // Creat username label
    usernameLabel = new Label();

    usernameLabel.getStyleClass().add("button-tag-label");
    usernameLabel.getStyleClass().add("heading-text");

    usernameLabel.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
    usernameLabel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    usernameLabel
        .widthProperty()
        .addListener(
            observable -> {
              AnchorPane.setTopAnchor(
                  usernameLabel, (signInButton.getHeight() - usernameLabel.getHeight()) / 2);
              AnchorPane.setRightAnchor(usernameLabel, signInButton.getWidth() * 2 / 3);
            });

    usernameLabel.setVisible(false);
    signInPane.getChildren().add(0, usernameLabel);

    // Sometimes buttons start selected for some reason
    rootPane.requestFocus();

    this.date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");

    this.dateLabel.setText(dateFormat.format(this.date));
    bindToTime();
  }

  private void bindToTime() {
    Timeline timeline =
        new Timeline(
            new KeyFrame(
                Duration.seconds(0),
                new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent actionEvent) {
                    Calendar time = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm aa");
                    timeLabel.setText(simpleDateFormat.format(time.getTime()));
                  }
                }),
            new KeyFrame(Duration.seconds(1)));
    timeline.setCycleCount(Animation.INDEFINITE);
    timeline.play();
  }

  @FXML
  public void pressedBack() {
    if (!transitioning) {
      popScene();
    }
  }

  @FXML
  public void pressedSignIn() {
    if (loginTransitioning) {
      return;
    }

    if (loggedIn) {
      eDB.changeFlag();

      FadeOutRight lblTrans = new FadeOutRight(usernameLabel);
      lblTrans.play();

      signInButton.setGraphic(new FontIcon(FontAwesomeSolid.SIGN_IN_ALT));
      loggedIn = false;
    } else {
      loginTransitioning = true;

      if (!loginBox.isVisible()) {
        loginBox.setVisible(true);
      }
      blockerPane.setMouseTransparent(false);

      ZoomIn trans = new ZoomIn(loginBox);
      trans.setOnFinished(
          event -> {
            loginTransitioning = false;
          });
      trans.play();
    }
  }

  @FXML
  public void clickedBlockerPane() {
    if (loginTransitioning) {
      return;
    }

    loginTransitioning = true;

    ZoomOut trans = new ZoomOut(loginBox);
    trans.setOnFinished(
        event -> {
          loginTransitioning = false;
          blockerPane.setMouseTransparent(true);
        });
    trans.play();
  }

  @FXML
  public void pressedLogin() {
    if (loginTransitioning) {
      return;
    }

    if (usernameBox.getText().isEmpty() || passwordBox.getText().isEmpty()) {
      DialogUtil.simpleErrorDialog(
          rootPane, "No Credentials", "Please enter your credentials and try again");
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
                      rootPane, "Incorrect Login", "Please reenter your credentials and try again");
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
                  // Zoom out login box
                  loginTransitioning = true;
                  ZoomOut trans = new ZoomOut(loginBox);
                  trans.setOnFinished(
                      event -> {
                        // Clear username / password once they're off screen
                        usernameBox.setText("");
                        passwordBox.setText("");

                        // Reset visibility of stuff in box
                        buttonBox.setDisable(false);
                        buttonBox.setOpacity(1.0);
                        spinner.setOpacity(0.0);
                        loginButton.setDisable(false);

                        // Pass clicks through blocker pane again
                        blockerPane.setMouseTransparent(true);

                        // Toggle off transition flag
                        loginTransitioning = false;
                      });
                  trans.play();

                  // Update sign in button to serve as log out button
                  signInButton.setGraphic(new FontIcon(FontAwesomeSolid.SIGN_OUT_ALT));

                  // Update username label
                  usernameLabel.setText(eDB.getLoggedIn());
                  if (!usernameLabel.isVisible()) {
                    usernameLabel.setVisible(true);
                  }

                  // Slide in username label
                  FadeInRight lblTrans = new FadeInRight(usernameLabel);
                  lblTrans.play();

                  loggedIn = true;
                });
          }
        });
  }

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
              ? new FadeOutLeftBig(contentPane.getChildren().get(0))
              : new FadeOutRightBig(contentPane.getChildren().get(0));
      transOut.setOnFinished(
          event -> {
            contentPane.getChildren().remove(transOut.getNode());
          });
      transOut.play();

      AnimationFX transIn = right ? new FadeInRightBig(top) : new FadeInLeftBig(top);
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
    if (instance == null || instance.transitioning) {
      return;
    }

    instance.sceneStack.push(newNode);
    instance.transition(true);
  }

  private static void popScene() {
    if (instance == null || instance.sceneStack.size() <= 1) {
      return;
    }

    Node old = instance.sceneStack.pop();
    instance.transition(false);
  }

  public static void pushScene(String fxmlPath) {
    Node node = FXMLCache.loadFXML(fxmlPath);
    pushScene(node);
  }
}

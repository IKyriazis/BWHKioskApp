package edu.wpi.cs3733.d20.teamA.controllers;

import animatefx.animation.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import de.taimos.totp.TOTP;
import edu.wpi.cs3733.d20.teamA.controls.TransitionType;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.FXMLCache;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import edu.wpi.cs3733.d20.teamA.util.ThreadPool;
import java.text.SimpleDateFormat;
import java.util.*;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;
import net.aksingh.owmjapis.core.OWM;
import net.aksingh.owmjapis.model.CurrentWeather;
import net.aksingh.owmjapis.model.param.Weather;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.weathericons.WeatherIcons;

public class SceneSwitcherController extends AbstractController {
  @FXML private ImageView backgroundImage;
  @FXML private StackPane rootPane;

  @FXML private JFXButton backButton;
  @FXML private JFXButton homeButton;
  @FXML private JFXButton signInButton;
  @FXML private JFXButton loginButton;
  @FXML private JFXButton authenticateButton;
  @FXML private JFXButton settingsButton;
  @FXML private JFXButton aboutBtn;

  @FXML private JFXTextField usernameBox;
  @FXML private JFXTextField gauthCode;
  @FXML private JFXPasswordField passwordBox;
  @FXML private JFXSpinner spinner;

  @FXML private AnchorPane signInPane;
  @FXML private GridPane blockerPane;
  @FXML private GridPane contentPane;
  @FXML private GridPane funPane;

  @FXML private VBox loginBox;
  @FXML private VBox buttonBox;
  @FXML private VBox gauth;

  @FXML private Label timeLabel;
  @FXML private Label dateLabel;
  @FXML private Label tempLabel;
  @FXML private Label weatherIconLabel;

  private static SceneSwitcherController instance;

  private Stack<Node> sceneStack;
  private HashMap<Node, TransitionType> sceneTrans;

  private boolean transitioning = false;
  private boolean loginTransitioning = false;
  private boolean loggedIn = false;

  private Label usernameLabel;
  private String username;
  private Date date;

  private static double logoutTime = 15;
  private boolean inFocus = true;

  private static boolean settingsScene = false;

  @FXML
  public void initialize() {
    // Setup instance
    instance = this;

    // Make it so top bar is actually on top of everything.
    funPane.toFront();

    // Set default dialog pane
    DialogUtil.setDefaultStackPane(rootPane);

    rootPane
        .focusedProperty()
        .addListener(
            new ChangeListener<Boolean>() {
              @Override
              public void changed(
                  ObservableValue<? extends Boolean> observable, Boolean onHidden, Boolean onShow) {
                timer.stop();
                inFocus = onShow;
              }
            });

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
    homeButton.setGraphic(new FontIcon(FontAwesomeSolid.HOME));
    backButton.setGraphic(new FontIcon(FontAwesomeSolid.LONG_ARROW_ALT_LEFT));

    // Setup sign in button icon
    signInButton.setGraphic(new FontIcon(FontAwesomeSolid.SIGN_IN_ALT));

    // Setup login button icon
    loginButton.setGraphic(new FontIcon(FontAwesomeSolid.LOCK));

    // Setup settings button icon
    settingsButton.setGraphic(new FontIcon(FontAwesomeSolid.COG));

    aboutBtn.setGraphic(new FontIcon(FontAwesomeSolid.INFO_CIRCLE));

    // Setup scene stack
    sceneStack = new Stack<>();
    sceneTrans = new HashMap<>();
    pushScene("views/nav/MainMenu.fxml", TransitionType.FADE);

    // Create username label
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

    // Setup date and time
    // Create new date
    this.date = new Date();
    // Get the current date and format it
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
    this.dateLabel.setText(dateFormat.format(this.date));

    // Call bind to time functions to update weather and time
    bindToTime();
    bindToTime2();
  }

  private void bindToTime() {
    // Create a new timeline object
    Timeline timeline =
        new Timeline(
            new KeyFrame(
                Duration.seconds(0),
                new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent actionEvent) {
                    // Get the current date and time
                    Calendar time = Calendar.getInstance();
                    // Create a format for the time so it is pretty
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm aa");
                    // Set the time to the label
                    timeLabel.setText(simpleDateFormat.format(time.getTime()));
                  }
                }),
            // Make it update every second so the time is accurate
            new KeyFrame(Duration.seconds(1)));
    timeline.setCycleCount(Animation.INDEFINITE);
    timeline.play();
  }

  private void bindToTime2() {
    // Create a new timeline
    Timeline timeline =
        new Timeline(
            new KeyFrame(
                Duration.seconds(0),
                new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent actionEvent) {
                    // Create a new openweathermaps object
                    // Note this uses Maddie's API Key pls don't steal my identity
                    OWM owm = new OWM("75fc9ba2793ec8f828c04ab93cc3437c");
                    try {
                      // Get the current weather by coordinates of Boston weather station
                      CurrentWeather cwd = owm.currentWeatherByCoords(42.3584, -71.0598);
                      // Make a list of current weather: format Weather[conditionId, mainInfo,
                      // moreInfo, iconCode]
                      List<Weather> w = cwd.getWeatherList();
                      // Get the current weather's condition code
                      int condCode = w.get(0).getConditionId();
                      // Get the icon code to display the icon in the corner
                      String iconCode = w.get(0).getIconCode();

                      if (iconCode != null) {
                        switch (iconCode) {
                          case "01d":
                            weatherIconLabel.setGraphic(new FontIcon(WeatherIcons.DAY_SUNNY));
                            break;
                          case "01n":
                            weatherIconLabel.setGraphic(new FontIcon(WeatherIcons.NIGHT_CLEAR));
                          case "02d":
                          case "03d":
                          case "04d":
                            weatherIconLabel.setGraphic(new FontIcon(WeatherIcons.DAY_CLOUDY));
                            break;
                          case "02n":
                          case "03n":
                          case "04n":
                            weatherIconLabel.setGraphic(new FontIcon(WeatherIcons.NIGHT_CLOUDY));
                            break;
                          case "09d":
                            weatherIconLabel.setGraphic(new FontIcon(WeatherIcons.DAY_SHOWERS));
                            break;
                          case "09n":
                            weatherIconLabel.setGraphic(new FontIcon(WeatherIcons.NIGHT_SHOWERS));
                          case "10d":
                          case "10n":
                            weatherIconLabel.setGraphic(new FontIcon(WeatherIcons.RAIN));
                            break;
                          case "11d":
                          case "11n":
                            weatherIconLabel.setGraphic(new FontIcon(WeatherIcons.THUNDERSTORM));
                            break;
                          case "13d":
                          case "13n":
                            weatherIconLabel.setGraphic(new FontIcon(WeatherIcons.SNOW));
                            break;
                          case "50d":
                            weatherIconLabel.setGraphic(new FontIcon(WeatherIcons.DAY_FOG));
                            break;
                          case "50n":
                            weatherIconLabel.setGraphic(new FontIcon(WeatherIcons.NIGHT_FOG));
                            break;
                          default:
                            weatherIconLabel.setGraphic(new FontIcon(WeatherIcons.METEOR));
                        }
                      }
                      // Get the current temperature
                      Double d = cwd.getMainData().getTemp();
                      // Convert the temperature from kelvin to fahrenheit
                      double f = ((d.doubleValue() - 273.15) * (9.0 / 5.0)) + 32.0;
                      int t = (int) Math.rint(f);
                      String tem = t + "";
                      // Set the text of the label to the temperature and add degree symbol to make
                      // it pretty
                      tempLabel.setText(tem + (char) 0x00B0 + " F");
                    } catch (Exception e) {
                      e.printStackTrace();
                    }
                  }
                }),
            // Have the temperature and icon update every 15 mins
            new KeyFrame(Duration.seconds(900)));
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
  public void pressedHome() {
    if (!transitioning) {
      while (sceneStack.size() > 1) {
        sceneStack.pop();
      }

      transition(TransitionType.FADE, false);
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

      // Turn sign in button graphic back
      signInButton.setGraphic(new FontIcon(FontAwesomeSolid.SIGN_IN_ALT));

      // Hide settings button
      ZoomOut settingsTrans = new ZoomOut(settingsButton);
      settingsTrans.play();

      loggedIn = false;
      username = "";

      // Kick user back to home screen.
      while (sceneStack.size() > 1) {
        sceneStack.pop();
      }
      transition(TransitionType.FADE, false);
    } else {
      loginTransitioning = true;

      if (!loginBox.isVisible()) {
        loginBox.setVisible(true);
      }
      blockerPane.setMouseTransparent(false);

      ZoomInDown trans = new ZoomInDown(loginBox);
      trans.setSpeed(2);
      trans.setOnFinished(
          event -> {
            loginTransitioning = false;
          });
      trans.play();

      ThreadPool.runBackgroundTask(
          () -> {
            String scannedCode = scanRFID();
            if (scannedCode != null) {
              String localUsername = eDB.getUsername(scannedCode);
              if (!localUsername.isEmpty()) {
                username = localUsername;
                eDB.rfidLogin(localUsername);
                Platform.runLater(this::login);
              } else {
                // popup that rfid is not in the database
                Platform.runLater(
                    () -> {
                      clickedBlockerPane();
                      DialogUtil.simpleErrorDialog(
                          rootPane, "Invalid Card", "The card you used doesn't belong to anyone");
                    });
              }
            } else {
              // popup that rfid scan went wrong
              Platform.runLater(
                  () -> {
                    clickedBlockerPane();
                    DialogUtil.simpleErrorDialog(
                        rootPane, "Failed Read", "Something went wrong while scanning the card");
                  });
            }
          });
    }
  }

  @FXML
  public void clickedBlockerPane() {
    if (loginTransitioning) {
      return;
    }

    stopRFID();

    loginTransitioning = true;

    // make sure google authenticator stuff is now the login stuff
    // Reset login box
    gauth.setVisible(false);

    // Clear username / password once they're off screen
    usernameBox.setText("");
    passwordBox.setText("");

    // Reset visibility of stuff in box
    buttonBox.setDisable(false);
    buttonBox.setOpacity(1.0);

    buttonBox.setVisible(true);
    usernameBox.setVisible(true);
    passwordBox.setVisible(true);

    loginButton.setVisible(true);

    loginButton.setDisable(false);
    authenticateButton.setVisible(false);

    ZoomOutDown trans = new ZoomOutDown(loginBox);
    trans.setSpeed(2);
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
            if (eDB.getSecretKey(usernameBox.getText()) == null) {
              username = usernameBox.getText();
              Platform.runLater(this::login);
            } else {
              Platform.runLater(
                  () -> {
                    // W need to know the username to find the user's secret key
                    username = usernameBox.getText();

                    // Fade out spinner
                    FadeTransition spinnerOutFade =
                        new FadeTransition(Duration.millis(250), spinner);
                    spinnerOutFade.setFromValue(1.0);
                    spinnerOutFade.setToValue(0.0);
                    spinnerOutFade.play();

                    // Reset login box
                    buttonBox.setVisible(false);
                    gauth.setVisible(true);
                    loginButton.setVisible(false);
                    authenticateButton.setVisible(true);
                  });
            }
          }
        });
  }

  @FXML
  public void pressedAuthenticate() {
    // gets the secret key of the user that just logged in
    String secretKey = eDB.getSecretKey(username);

    // check if the authenticator code given is correct, log in if it is
    // show an error message if it isn't
    if (gauthCode.getText().equals(getTOTPCode(secretKey))) {
      login();
    } else {
      DialogUtil.simpleErrorDialog(
          rootPane, "Incorrect Code", "You have entered an incorrect Google Authenticator code.");
    }
  }

  @FXML
  public void pressedSettings() {
    pushScene("views/nav/Settings.fxml", TransitionType.FADE);
    settingsScene = true;
    // settingsButton.setVisible(false);
  }

  private void login() {
    // Zoom out login box
    loginTransitioning = true;
    ZoomOutDown trans = new ZoomOutDown(loginBox);
    trans.setSpeed(2);
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

    // Enable settings button & zoom it in
    if (!settingsButton.isVisible()
        && "admin".equals(serviceDatabase.getLoggedIn().getTitle())
        && !settingsScene) {
      settingsButton.setVisible(true);
    } else if (!"admin".equals(serviceDatabase.getLoggedIn().getTitle())) {
      settingsButton.setVisible(false);
    }

    ZoomIn settingsTrans = new ZoomIn(settingsButton);
    settingsTrans.play();

    // Update username label
    usernameLabel.setText(username);
    if (!usernameLabel.isVisible()) {
      usernameLabel.setVisible(true);
    }

    // Slide in username label
    FadeInRight lblTrans = new FadeInRight(usernameLabel);
    lblTrans.play();

    loggedIn = true;

    // Fire tab switch event off to top scene to get it to update
    sceneStack.peek().fireEvent(new TabSwitchEvent());

    // Undo changes to login box done for auth purposes
    gauthCode.setText("");
    buttonBox.setVisible(true);
    gauth.setVisible(false);
    loginButton.setVisible(true);
    authenticateButton.setVisible(false);
  }

  private void transition(TransitionType trans, boolean additive) {
    if (transitioning || contentPane.getChildren().contains(sceneStack.peek())) {
      return;
    }
    boolean first = contentPane.getChildren().isEmpty();

    Node top = sceneStack.peek();

    // Fire off tab switch event to new scene
    top.fireEvent(new TabSwitchEvent());

    contentPane.getChildren().add(top);
    if (!first) {
      transitioning = true;

      AnimationFX transOut = trans.getTransitionOut(contentPane.getChildren().get(0), additive);
      transOut.setResetOnFinished(true);
      transOut.setOnFinished(
          event -> {
            contentPane.getChildren().remove(transOut.getNode());
          });
      transOut.play();

      AnimationFX transIn = trans.getTransitionIn(top, additive);
      transIn.setResetOnFinished(true);
      transIn.setOnFinished(
          event -> {
            transitioning = false;
          });
      transIn.play();
    }

    if (sceneStack.size() > 1) {
      backButton.setVisible(true);
      homeButton.setVisible(true);
    } else {
      backButton.setVisible(false);
      homeButton.setVisible(false);
    }

    rootPane.requestFocus();
  }

  private static void pushScene(Node newNode, TransitionType trans) {
    if (instance == null || instance.transitioning) {
      return;
    }

    instance.sceneStack.push(newNode);
    instance.sceneTrans.put(newNode, trans);

    instance.transition(trans, true);
  }

  public static void popScene() {
    if (instance == null || instance.sceneStack.size() <= 1) {
      return;
    }

    Node old = instance.sceneStack.pop();
    instance.transition(instance.sceneTrans.get(old), false);
  }

  public static void pushScene(String fxmlPath, TransitionType trans) {
    Node node = FXMLCache.loadFXML(fxmlPath);

    // Disallow duplicate scenes in the stack
    if (instance != null && !instance.sceneStack.isEmpty()) {
      Stack<Node> sceneStack = instance.sceneStack;
      for (Node scene : sceneStack) {
        if (scene == node) {
          return;
        }
      }
    }
    // Tell settings scene not to show
    settingsScene = false;

    pushScene(node, trans);
  }

  private static String getTOTPCode(String secretKey) {
    Base32 base32 = new Base32();
    byte[] bytes = base32.decode(secretKey);
    String hexKey = Hex.encodeHexString(bytes);
    return TOTP.getOTP(hexKey);
  }

  public void openAbout(ActionEvent actionEvent) {
    pushScene("views/About.fxml", TransitionType.ZOOM);
  }

  Timeline timer =
      new Timeline(
          new KeyFrame(
              Duration.seconds(logoutTime),
              (v) -> {
                if (inFocus) {
                  pressedHome();
                  if (loggedIn == true) {
                    pressedSignIn();
                  }
                }
                DialogUtil.killDialogs();
              }));

  @FXML
  public void logoutTimer() {
    timer.stop();
    timer.play();
  }

  @FXML
  public void logoutTimerClick() {
    timer.stop();
    inFocus = true;
    timer.play();
  }

  public void setLogoutTime(double d) {
    logoutTime = d;
    timer.setDelay(Duration.seconds(logoutTime - 15));
  }

  public static SceneSwitcherController getInstanceOf() {
    return instance;
  }
}

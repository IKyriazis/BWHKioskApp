package edu.wpi.cs3733.d20.teamA.controllers.nav;

import com.fazecast.jSerialComm.SerialPort;
import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamA.App;
import edu.wpi.cs3733.d20.teamA.controllers.SceneSwitcherController;
import edu.wpi.cs3733.d20.teamA.controls.TransitionType;
import edu.wpi.cs3733.d20.teamA.util.FXMLCache;
import edu.wpi.cs3733.d20.teamA.util.ThreadPool;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class MainMenuController {
  @FXML private JFXButton mapButton;
  @FXML private JFXButton transitButton;
  @FXML private JFXButton serviceButton;

  public void initialize() {
    mapButton.setGraphic(new FontIcon(FontAwesomeSolid.MAP));
    transitButton.setGraphic(new FontIcon(FontAwesomeSolid.TRAIN));
    serviceButton.setGraphic(new FontIcon(FontAwesomeSolid.HAND_HOLDING_HEART));

    // Set up listeners to resize buttons to be equiradial circles
    JFXButton[] buttons = {mapButton, transitButton, serviceButton};
    for (JFXButton button : buttons) {
      button.widthProperty().addListener(observable -> resizeButtons());
      button.heightProperty().addListener(observable -> resizeButtons());
    }

    // Set up scene switching callbacks
    mapButton.setOnAction(
        event -> {
          SceneSwitcherController.pushScene("views/SimpleMap.fxml", TransitionType.FADE);
        });

    transitButton.setOnAction(
        event -> {
          // SceneSwitcherController.pushScene("views/nav/ServiceHome.fxml");
          try {
            Media media = new Media(App.class.getResource("sounds/no.mp3").toURI().toString());
            AudioClip audioClip = new AudioClip(media.getSource());
            audioClip.play();
          } catch (Exception e) {
            e.printStackTrace();
          }
        });

    serviceButton.setOnAction(
        event -> {
          SceneSwitcherController.pushScene("views/nav/ServiceHome.fxml", TransitionType.FADE);
        });

    // Preload scenes into FXML cache
    FXMLCache.preLoadFXML("views/SimpleMap.fxml");
    // FXMLCache.preLoadFXML();
    FXMLCache.preLoadFXML("views/nav/ServiceHome.fxml");
    // put rfid scanner on background thread
    SerialPort comPort = SerialPort.getCommPorts()[0];
    comPort.openPort();
    ThreadPool.runBackgroundTask(
        () -> {
          try {
            while (true) {
              while (comPort.bytesAvailable() != 14) Thread.sleep(20);

              byte[] readBuffer = new byte[comPort.bytesAvailable()];
              int numRead = comPort.readBytes(readBuffer, readBuffer.length);
              System.out.println(new String(readBuffer, "UTF-8"));
              System.out.println("Read " + numRead + " bytes.");
              System.out.println("HI");
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
    // Fix for weird button layout issues
    Platform.runLater(
        () -> {
          GridPane buttonPane = (GridPane) mapButton.getParent();
          buttonPane.requestLayout();
        });
  }

  private void resizeButtons() {
    JFXButton[] buttons = {mapButton, transitButton, serviceButton};

    // Find maximum button dimension
    double maxWidth = 0.0;
    double maxHeight = 0.0;
    for (JFXButton button : buttons) {
      maxWidth = Math.max(maxWidth, button.getWidth());
      maxHeight = Math.max(maxHeight, button.getHeight());
    }

    // Turn square
    maxWidth = Math.max(maxWidth, maxHeight);
    maxHeight = Math.max(maxWidth, maxHeight);

    // Set new button sizes
    for (JFXButton button : buttons) {
      button.setMinWidth(maxWidth);
      button.setPrefWidth(maxWidth);
      button.setMaxWidth(maxWidth);

      button.setMinHeight(maxHeight);
      button.setPrefHeight(maxHeight);
      button.setMaxHeight(maxHeight);
    }
  }
}

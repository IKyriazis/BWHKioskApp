package edu.wpi.cs3733.d20.teamA;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App extends Application {

  @Override
  public void init() {
    log.info("Starting Up");
  }

  @Override
  public void start(Stage primaryStage){
    // Load FXML file
    FXMLLoader loader = new FXMLLoader();

    loader.setLocation(App.class.getResource("views/SceneSwitcher.fxml"));
    Node rootPane = loader.load();
    rootPane.setCache(true);
    rootPane.setCacheHint(CacheHint.SPEED);

    // Set up the stage
    JFXDecorator decorator = new JFXDecorator(primaryStage, rootPane);
    decorator.setCustomMaximize(true);

    Scene scene = new Scene(decorator);
    scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());
    ((HBox) decorator.getChildren().get(0))
        .getChildren()
        .forEach(
            node -> {
              if (node instanceof JFXButton) {
                node.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-fill: white");
              }
            });

    primaryStage.setScene(scene);
    primaryStage.setTitle("Hospital App");
    primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("images/icon.png")));

    primaryStage.setMinWidth(1440);
    primaryStage.setWidth(1440);
    primaryStage.setMinHeight(960);
    primaryStage.setHeight(960);

    // Display the stage
    primaryStage.show();
  }

  @Override
  public void stop() {
    log.info("Shutting Down");
  }
}

package edu.wpi.cs3733.d20.teamA;

import com.jfoenix.controls.JFXDecorator;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App extends Application {

  @Override
  public void init() {
    log.info("Starting Up");
  }

  @Override
  public void start(Stage primaryStage) throws IOException {
    // Load FXML file
    FXMLLoader loader = new FXMLLoader();

    loader.setLocation(App.class.getResource("views/SceneSwitcher.fxml"));
    Node rootPane = loader.load();

    // Set up the stage
    JFXDecorator decorator = new JFXDecorator(primaryStage, rootPane);
    decorator.setCustomMaximize(true);

    Scene scene = new Scene(decorator);
    scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());

    primaryStage.setScene(scene);
    primaryStage.setTitle("Hospital App");
    primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("images/wonga_lisa.png")));

    primaryStage.setMinWidth(1000);
    primaryStage.setWidth(1000);
    primaryStage.setMinHeight(780);
    primaryStage.setHeight(780);
    // primaryStage.setFullScreen(true);

    // Display the stage
    primaryStage.show();
  }

  @Override
  public void stop() {
    log.info("Shutting Down");
  }
}

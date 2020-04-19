package edu.wpi.cs3733.d20.teamA;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
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

    loader.setLocation(App.class.getResource("views/MainMockup.fxml"));
    BorderPane borderPane = loader.load();

    // Set up the stage
    Scene scene = new Scene(borderPane);

    primaryStage.setScene(scene);
    primaryStage.setTitle("Hospital GUI");
    primaryStage.setMinWidth(1000);
    primaryStage.setMinHeight(755);
    // primaryStage.setFullScreen(true);

    // Display the stage
    primaryStage.show();
  }

  @Override
  public void stop() {
    log.info("Shutting Down");
  }
}

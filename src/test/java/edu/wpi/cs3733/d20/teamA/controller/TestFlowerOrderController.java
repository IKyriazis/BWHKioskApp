package edu.wpi.cs3733.d20.teamA.controller;

import static org.testfx.api.FxAssert.verifyThat;

import edu.wpi.cs3733.d20.teamA.App;
import edu.wpi.cs3733.d20.teamA.controllers.FlowerOrderController;
import edu.wpi.cs3733.d20.teamA.controllers.LoginController;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

@ExtendWith({MockitoExtension.class, ApplicationExtension.class})
public class TestFlowerOrderController {

    @InjectMocks
    FlowerOrderController controller;

    private final FXMLLoader loader = new FXMLLoader();
    private Parent sceneRoot;

    @Start
    private void start(Stage stage) throws IOException {
        loader.setControllerFactory((i) -> controller);
        sceneRoot = loader.load(App.class.getResource("views/EmployeeArea.fxml"));
        var primaryScene = new Scene(sceneRoot);
        // We are forced to inject this object afterward, since it must be created on the JavaFX thread
        controller.setAppPrimaryScene(primaryScene);

        stage.setScene(primaryScene);
        stage.setAlwaysOnTop(true);
        stage.show();
    }


}

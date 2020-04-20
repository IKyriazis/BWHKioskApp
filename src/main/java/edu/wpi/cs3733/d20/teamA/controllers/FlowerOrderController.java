package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.App;
import java.io.IOException;
import java.sql.SQLException;

import edu.wpi.cs3733.d20.teamA.database.Flower;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javax.swing.*;

public class FlowerOrderController extends AbstractController {

  @FXML private JFXDrawer trackerDrawer;
  @FXML private JFXDrawer orderDrawer;
  @FXML private VBox trackerBox;
  @FXML private JFXButton orderButton;
  @FXML private JFXButton trackButton;
  @FXML private GridPane centerPane;
  private AnchorPane flowerOrderPane;

  public FlowerOrderController() throws SQLException {}
  private Scene appPrimaryScene;


  public void initialize() throws SQLException, IOException {
    // Set up drop shadow on flower order pane
    DropShadow dropShadow = new DropShadow();
    dropShadow.setRadius(5.0);
    dropShadow.setOffsetX(2.0);
    dropShadow.setOffsetY(2.0);
    dropShadow.setColor(Color.GREY);
    centerPane.setEffect(dropShadow);
    flowerOrderPane = FXMLLoader.load(App.class.getResource("views/FlowerOrder.fxml"));
    flowerOrderPane.setEffect(dropShadow);

    // Set up tracker drawer
    trackerDrawer.setSidePane(trackerBox);
    trackerDrawer.close();

    // Set up drawer opening button
    trackButton.setOnAction(event -> trackerDrawer.toggle());

    // Place drawer directly under order gridpane
    centerPane
            .heightProperty()
            .addListener(
                    observable -> {
                      trackerDrawer.setTranslateY(centerPane.getHeight() - 50);
                    });

    // Set up order drawer
    orderDrawer.setSidePane(flowerOrderPane);
    orderDrawer.close();
    orderDrawer.setDefaultDrawerSize(175);

    // Set up drawer opening button
    orderButton.setOnAction(event -> orderDrawer.toggle());

    // Place drawer directly under order gridpane
    centerPane
            .heightProperty()
            .addListener(
                    observable -> {
                      orderDrawer.setTranslateY(centerPane.getHeight());
                    });

    // Setup button icons
    orderButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ADDRESS_CARD));
    trackButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.TRUCK));

  }

  /**
   * This method allows the tests to inject the scene at a later time, since it must be done on the
   * JavaFX thread
   *
   * @param appPrimaryScene Primary scene of the app whose root will be changed
   */
  @Inject
  public void setAppPrimaryScene(Scene appPrimaryScene) {
    this.appPrimaryScene = appPrimaryScene;
  }


  @FXML
  public void cancel(ActionEvent event) throws IOException {
    Stage stage;
    Parent root;
    // putting the event's source in src var so it doesn't have to check it every time
    Object src = event.getSource();

    stage =
        (Stage)
            ((Button) (src)).getScene().getWindow(); // use existing stage to close current window

    root = FXMLLoader.load(App.class.getResource("views/ServiceHome.fxml"));
    Scene scene = new Scene(root);

    stage.setScene(scene);
    stage.show();
  }
}

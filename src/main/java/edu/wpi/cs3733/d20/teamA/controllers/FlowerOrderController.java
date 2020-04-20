package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.App;
import edu.wpi.cs3733.d20.teamA.database.Flower;
import java.io.IOException;
import java.sql.SQLException;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javax.swing.*;

public class FlowerOrderController extends AbstractController {

  @FXML private JFXComboBox<String> choiceFlower;
  @FXML private JFXTextField txtNumber;
  @FXML private JFXDrawer trackerDrawer;
  @FXML private VBox trackerBox;
  @FXML private JFXButton orderButton;
  @FXML private JFXButton trackButton;
  @FXML private GridPane flowerOrderPane;

  public FlowerOrderController() throws SQLException {}

  public void initialize() throws SQLException {
    // Set up drop shadow on flower order pane
    DropShadow dropShadow = new DropShadow();
    dropShadow.setRadius(5.0);
    dropShadow.setOffsetX(2.0);
    dropShadow.setOffsetY(2.0);
    dropShadow.setColor(Color.GREY);
    flowerOrderPane.setEffect(dropShadow);

    // Set up tracker drawer
    trackerDrawer.setSidePane(trackerBox);
    trackerDrawer.close();

    // Set up drawer opening button
    trackButton.setOnAction(event -> trackerDrawer.toggle());

    // Place drawer directly under order gridpane
    flowerOrderPane
        .heightProperty()
        .addListener(
            observable -> {
              trackerDrawer.setTranslateY(flowerOrderPane.getHeight() - 50);
            });

    // Setup button icons
    orderButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ADDRESS_CARD));
    trackButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.TRUCK));

    ObservableList<Flower> list = super.flDatabase.flowerOl(); // Get from FlowerDatabase @TODO
    for (Flower f : list) {
      choiceFlower.getItems().add(f.getTypeFlower() + ", " + f.getColor());
    }
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

  public void placeOrder(ActionEvent actionEvent) throws SQLException, IOException {
    if (choiceFlower.getSelectionModel().getSelectedItem() != null) {
      String s = choiceFlower.getSelectionModel().getSelectedItem();
      String type = s.substring(0, s.indexOf(','));
      String color = s.substring(s.indexOf(' ') + 1);

      int num = Integer.parseInt(txtNumber.getText());

      int i = super.flDatabase.addOrder(num, type, color, "ID");

      if (i == 0) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Unable to place order");
        alert.setContentText("Order not placed successfully, please try again");
        alert.showAndWait();
      } else {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order placed");
        alert.setContentText("Your order has been placed. Your order number is: " + i);
        alert.showAndWait();
        choiceFlower.getSelectionModel().clearSelection();
        txtNumber.setText("");
      }
    }
  }

  public void setMaxNumber(ActionEvent actionEvent) {}
}

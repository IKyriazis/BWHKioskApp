package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.App;
import java.io.IOException;
import java.sql.SQLException;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javax.swing.*;

public class FlowerOrderController extends AbstractController {
  @FXML private JFXTextField txtNumber;
  @FXML private JFXProgressBar progress;
  @FXML private Label lblOutput;

  @FXML private JFXDrawer trackerDrawer;
  @FXML private JFXDrawer orderDrawer;
  @FXML private VBox trackerBox;
  @FXML private JFXButton orderButton;
  @FXML private JFXButton trackButton;
  @FXML private GridPane centerPane;
  private AnchorPane flowerOrderPane;

  public FlowerOrderController() throws SQLException {}

  public void initialize() throws SQLException, IOException {
    // Set up drop shadow on flower order pane
    DropShadow dropShadow = new DropShadow();
    dropShadow.setRadius(5.0);
    dropShadow.setOffsetX(2.0);
    dropShadow.setOffsetY(2.0);
    dropShadow.setColor(Color.GREY);
    centerPane.setEffect(dropShadow);
    flowerOrderPane = FXMLLoader.load(App.class.getResource("views/FlowerOrder.fxml"));

    // Set up tracker drawer
    trackerDrawer.setSidePane(trackerBox);
    trackerDrawer.close();

    // Set up drawer opening button
    trackButton.setOnAction(
        event -> {
          trackerDrawer.toggle();
          if (orderDrawer.isOpened()) orderDrawer.close();
          if (orderDrawer.isVisible())
            orderDrawer.setVisible(false); // Necessary because weird reasons
        });

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
    orderButton.setOnAction(
        event -> {
          orderDrawer.toggle();
          if (trackerDrawer.isOpened()) trackerDrawer.close();
          if (!orderDrawer.isVisible())
            orderDrawer.setVisible(true); // Necessary because weird reasons
        });

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

  @FXML
  public void checkNum(Event e) {
    // Send text to database and get back the status
    String s = super.flDatabase.getOrderStatus(Integer.parseInt(txtNumber.getText()));
    if (s == null) {
      progress.setProgress(0);
      lblOutput.setText("Input an order number");
      return;
    }

    if (s.equals("Order Sent")) {
      progress.setProgress(.1);
      lblOutput.setText("Order Sent");
    } else if (s.equals("Order Received")) {
      progress.setProgress(.35);
      lblOutput.setText("Order Received");
    } else if (s.equals("Flowers Sent")) {
      progress.setProgress(.7);
      lblOutput.setText("Flower Sent");
    } else if (s.equals("Flowers Delivered")) {
      progress.setProgress(1);
      lblOutput.setText("Flowers Delivered");
    } else {
      progress.setProgress(0);
      lblOutput.setText("Input an order number");
    }
  }
}

package edu.wpi.cs3733.d20.teamA.controllers.InternalTransport;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.Flower;
import edu.wpi.cs3733.d20.teamA.database.Order;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class InternalTransportAdminController extends AbstractController {

    @FXML private GridPane orderTablePane;

    @FXML private JFXTextField txtPrev;
    @FXML private JFXComboBox<String> txtNext;

    @FXML private StackPane dialogStackPane;

    @FXML private Label orderTblLbl;
    @FXML private JFXButton changeProgressButton;

    @FXML private AnchorPane flowerPane;

    private Order lastOrder;

    private SimpleTableView<Flower> tblFlowerView;
    private SimpleTableView<Order> tblOrderView;

    public void initialize() {
        if (flDatabase.getSizeFlowers() == -1 || flDatabase.getSizeFlowers() == -1) {
            flDatabase.dropTables();
            flDatabase.createTables();
            flDatabase.readFlowersCSV();
            flDatabase.readFlowerOrderCSV();
        } else if (flDatabase.getSizeFlowers() == 0 || flDatabase.getSizeOrders() == 0) {
            flDatabase.removeAllOrders();
            flDatabase.removeAllFlowers();
            flDatabase.readFlowersCSV();
            flDatabase.readFlowerOrderCSV();
        }

        // Setup label icons
        orderTblLbl.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.BARCODE));

        // Setup button icons
        changeProgressButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.EXCHANGE));

        // Add tab switch update listener
        flowerPane.addEventHandler(
                TabSwitchEvent.TAB_SWITCH,
                event -> {
                    event.consume();
                    update();
                });

        // Set up tables

        tblOrderView = new SimpleTableView<>(new Order(0, 0, "", "", 0, "", ""), 40.0);
        orderTablePane.getChildren().addAll(tblOrderView);

        // Populate tables
        update();

        // Hook up txtPrev to show status of selected order
        tblOrderView.setOnMouseClicked(this::updateStatus);

        // Setup status change stuff
        txtNext.getItems().addAll("Order Sent", "Order Received", "Flowers Sent", "Flowers Delivered");
        txtNext.getSelectionModel().select(0);
    }

    public void update() {
        try {
            tblOrderView.clear();

            tblOrderView.add(itDatabase.requestOl());
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtil.simpleErrorDialog(
                    dialogStackPane, "Error", "Failed to update flower and/or order tables");
        }
    }


}

package edu.wpi.cs3733.d20.teamA.controllers.flower;

import com.jfoenix.controls.*;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.database.flowerTableItems.Flower;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class FlowerServiceController extends AbstractController {
  @FXML private Label headerLabel;

  @FXML private JFXTextField txtTotal;

  @FXML private JFXButton trackButton;
  @FXML private JFXButton orderButton;
  @FXML private StackPane dialogPane;

  @FXML private Pane rootPane;

  @FXML private JFXTreeTableView<Flower> flowerTable;

  public FlowerServiceController() {}

  public void initialize() {
    if (flDatabase.getSizeFlowers() == -1 || flDatabase.getSizeOrders() == -1) {
      flDatabase.dropTables();
      flDatabase.createTables();
      flDatabase.readFlowersCSV();
      flDatabase.readFlowerOrderCSV();
    } else if (flDatabase.getSizeFlowers() == 0 || flDatabase.getSizeOrders() == 0) {
      flDatabase.removeAllFlowers();
      flDatabase.removeAllOrders();
      flDatabase.readFlowersCSV();
      flDatabase.readFlowerOrderCSV();
    }

    // Set up icons
    headerLabel.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FILE));
    trackButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.TRUCK));
    orderButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ARROW_CIRCLE_RIGHT));

    // Add columns to table - please for the love of god dont use a simple table view here unless
    // you are really sure
    // Nevermind that.It isnt that hard. Im dummmb. Someone please kill me - will
    JFXTreeTableColumn<Flower, String> column1 = new JFXTreeTableColumn<>("Type");
    column1.setCellValueFactory(param -> param.getValue().getValue().typeFlowerProperty());

    JFXTreeTableColumn<Flower, String> column2 = new JFXTreeTableColumn<>("Color");
    column2.setCellValueFactory(param -> param.getValue().getValue().colorProperty());

    JFXTreeTableColumn<Flower, Integer> column3 = new JFXTreeTableColumn<>("Quantity");
    column3.setCellValueFactory(param -> param.getValue().getValue().qtyProperty().asObject());

    JFXTreeTableColumn<Flower, Double> column4 = new JFXTreeTableColumn<>("Unit Price");
    column4.setCellValueFactory(param -> param.getValue().getValue().pricePerProperty().asObject());

    JFXTreeTableColumn<Flower, String> column5 = new JFXTreeTableColumn<>("Number");
    column5.setCellValueFactory(
        param -> param.getValue().getValue().getQuantitySelectedProp().asObject().asString());

    column5.setCellFactory(
        (TreeTableColumn<Flower, String> param) -> {
          GenericEditableTreeTableCell cell =
              new GenericEditableTreeTableCell<Flower, String>(new TextFieldEditorBuilder());
          return cell;
        });

    column5.setOnEditCommit(
        new EventHandler<TreeTableColumn.CellEditEvent<Flower, String>>() {
          @Override
          public void handle(TreeTableColumn.CellEditEvent<Flower, String> t) {
            Flower f =
                ((Flower)
                    t.getTreeTableView().getTreeItem(t.getTreeTablePosition().getRow()).getValue());
            try {
              int i = Integer.parseInt(t.getNewValue());
              if (i <= f.getQty() && i >= 0) {
                f.setQuantitySelected(i);
              } else {
                f.setQuantitySelected(0);
                flowerTable.refresh();
                DialogUtil.simpleErrorDialog(
                    dialogPane,
                    "Invalid number",
                    "Please enter a number between 0 and the number of flowers in stock");
              }
            } catch (NumberFormatException e) {
              f.setQuantitySelected(0);
              flowerTable.refresh();
              DialogUtil.simpleErrorDialog(
                  dialogPane, "Invalid number", "Please enter a numeric value");
            }
            updateTotal();
          }
        });

    //noinspection unchecked
    flowerTable.getColumns().addAll(column1, column2, column3, column4, column5);
    flowerTable.setEditable(true);

    // Setup column sizing
    flowerTable.setColumnResizePolicy(JFXTreeTableView.CONSTRAINED_RESIZE_POLICY);
    flowerTable
        .getColumns()
        .forEach(
            column -> {
              column.setMinWidth(80.0);
              column.setReorderable(false);
            });

    // Null root flower node
    TreeItem<Flower> rootFlower = new TreeItem(new Flower("", "", 0, 0.0, 0));

    // Add other tree items below root item
    flDatabase.flowerOl().forEach(flower -> rootFlower.getChildren().add(new TreeItem(flower)));

    // Set root table item
    flowerTable.setRoot(rootFlower);
    flowerTable.setShowRoot(false);

    // Listen for tab switch events to update flower list
    rootPane.addEventHandler(
        TabSwitchEvent.TAB_SWITCH,
        event -> {
          event.consume();
          updateTable();
        });
  }

  private void updateTotal() {
    double cost = 0;

    ObservableList<TreeItem<Flower>> f = flowerTable.getRoot().getChildren();
    for (TreeItem<Flower> item : f) {
      Flower myFlower = item.getValue();
      cost += myFlower.getQuantitySelected() * myFlower.getPricePer();
    }
    txtTotal.setText(String.format("$%.2f", cost));
  }

  @FXML
  public void updateTable() {
    try {
      flowerTable.getRoot().getChildren().clear();
      flDatabase
          .flowerOl()
          .forEach(flower -> flowerTable.getRoot().getChildren().add(new TreeItem(flower)));
      updateTotal();
    } catch (Exception e) {
      e.printStackTrace();
      DialogUtil.simpleInfoDialog(
          dialogPane,
          "Database Error",
          "Failed to pull flower list from database. Please try again later.");
    }
  }

  @FXML
  public void placeOrder() {
    List<Flower> myList = getOrderList();
    if (myList.size() != 0) {
      FlowerOrderController cont = new FlowerOrderController();
      cont.setList(myList);
      DialogUtil.complexDialog(
          dialogPane,
          "Place Order",
          "views/flower/FlowerOrderDialog.fxml",
          false,
          event -> updateTable(),
          cont);
    } else {
      DialogUtil.simpleErrorDialog(
          dialogPane, "Cannot add order", "Please select flowers before placing your order");
    }
  }
  // Generate a list of flowers to pass to the order controller
  private List<Flower> getOrderList() {
    List<Flower> myList = new ArrayList<>();
    ObservableList<TreeItem<Flower>> f = flowerTable.getRoot().getChildren();
    for (TreeItem<Flower> item : f) {
      Flower myFlower = item.getValue();
      if (myFlower.getQuantitySelected() != 0) myList.add(myFlower);
    }
    return myList;
  }

  @FXML
  public void trackOrder() {
    DialogUtil.complexDialog(
        dialogPane,
        "Track Order",
        "views/flower/FlowerTrackerDialog.fxml",
        false,
        null,
        new FlowerTrackerController());
  }
}

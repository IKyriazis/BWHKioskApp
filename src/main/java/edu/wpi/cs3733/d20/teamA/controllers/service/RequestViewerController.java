package edu.wpi.cs3733.d20.teamA.controllers.service;

import animatefx.animation.FadeInDown;
import animatefx.animation.FadeOutDown;
import com.jfoenix.controls.JFXTreeTableView;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceRequest;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceType;
import edu.wpi.cs3733.d20.teamA.util.FXMLCache;
import edu.wpi.cs3733.d20.teamA.util.TabSwitchEvent;
import java.util.HashMap;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.Pane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class RequestViewerController extends AbstractController {
  @FXML private Label headerLabel;
  @FXML private Pane rootPane;
  @FXML private JFXTreeTableView reqTable;
  @FXML private Label promptLabel;
  @FXML private Pane editPane;

  private HashMap<ServiceType, TreeItem<ServiceRequest>> categoryItems;

  public void initialize() {
    // Set up header icon
    headerLabel.setGraphic(new FontIcon(FontAwesomeSolid.LIST));

    // Set up table
    TreeItem<ServiceRequest> rootItem =
        new TreeItem<>(new ServiceRequest(ServiceType.JANITOR, "", "", "", "", "", "", "", ""));
    reqTable.setRoot(rootItem);
    reqTable.setShowRoot(false);

    reqTable.setMaxWidth(Double.MAX_VALUE);
    reqTable.setMaxHeight(Double.MAX_VALUE);

    reqTable.setColumnResizePolicy(TreeTableView.UNCONSTRAINED_RESIZE_POLICY);
    reqTable.getColumns().addAll(rootItem.getValue().getColumns());

    reqTable
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            observable -> {
              selectService();
            });

    // Setup category items
    categoryItems = new HashMap<>();
    for (ServiceType type : ServiceType.values()) {
      TreeItem<ServiceRequest> categoryItem =
          new TreeItem<>(new ServiceRequest(type, "", "", "", "", "", "", "", ""));
      categoryItems.put(type, categoryItem);
      reqTable.getRoot().getChildren().add(categoryItem);
    }
    rootPane.addEventHandler(
        TabSwitchEvent.TAB_SWITCH,
        event -> {
          updateTable();
        });

    updateTable();
  }

  public void updateTable() {
    // Clear all categories
    categoryItems.forEach(
        (serviceType, serviceRequestTreeItem) -> {
          serviceRequestTreeItem.getChildren().clear();
        });

    // Add all service requests in under category
    ObservableList<ServiceRequest> reqs = serviceDatabase.getGenericObservableList();
    reqs.forEach(
        serviceRequest -> {
          TreeItem<ServiceRequest> category = categoryItems.get(serviceRequest.getServiceType());
          category.getChildren().add(new TreeItem<>(serviceRequest));
        });
  }

  @FXML
  public void selectService() {
    ServiceRequest req =
        ((TreeItem<ServiceRequest>) reqTable.getSelectionModel().getSelectedItem()).getValue();

    Node newNode = promptLabel;
    if (req != null) {
      switch (req.getServiceType()) {
        case JANITOR:
          newNode = FXMLCache.loadFXML("views/AddJanitorPopup.fxml");
          break;
        default:
          break;
      }
    }

    // TODO; Fix transitions when you click rapidly
    if (!editPane.getChildren().contains(newNode)) {
      Node oldNode = editPane.getChildren().get(0);
      FadeOutDown transOut = new FadeOutDown(oldNode);
      transOut.setOnFinished(
          event -> {
            editPane.getChildren().remove(oldNode);
          });
      transOut.play();

      editPane.getChildren().add(newNode);
      newNode.setVisible(false);
      Node finalNewNode = newNode;
      Platform.runLater(
          () -> {
            finalNewNode.setVisible(true);
            FadeInDown transIn = new FadeInDown(finalNewNode);
            transIn.play();
          });

      // Update width so that the thingy never shrinks
      if (newNode.prefWidth(editPane.getHeight()) > editPane.getPrefWidth()) {
        editPane.setPrefWidth(newNode.prefWidth(editPane.getHeight()));
      }
    }
  }
}

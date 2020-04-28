package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamA.controls.SimpleTableView;
import edu.wpi.cs3733.d20.teamA.database.ITTicket;
import edu.wpi.cs3733.d20.teamA.graph.Graph;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class ITServicesController extends AbstractController {

  @FXML private JFXTextField ITTicketName;
  @FXML private JFXComboBox<Node> ITTicketLocation;
  @FXML private JFXComboBox<String> ITTicketCategory;
  @FXML private JFXTextArea ITTicketDescription;
  @FXML private GridPane ticketTablePane;
  @FXML private JFXComboBox<String> statusChangeStatus;
  @FXML private JFXTextField statusChangeName;
  @FXML private StackPane ITStackPane;
  @FXML private JFXButton changeStatusBtn;
  @FXML private JFXButton descriptionBtn;
  @FXML private JFXButton submitBtn;
  @FXML private JFXButton deleteBtn;

  private ITTicket selected;

  private SimpleTableView<ITTicket> tblViewITTicket;

  /**
   * Sets up the ITTicket database, initializes the choices for comboBoxes, sets up the table, and
   * sets up an event handler to track when the mouse is selecting the table.
   */
  public void initialize() {
    // Set up the ITTicket database
    if (itTicketDatabase.getSizeITTickets() == -1) {
      itTicketDatabase.dropTables();
      itTicketDatabase.createTables();
      itTicketDatabase.readITTicketsCSV();
    } else if (itTicketDatabase.getSizeITTickets() == 0) {
      itTicketDatabase.removeAllITTickets();
      itTicketDatabase.readITTicketsCSV();
    }

    // Initialize choices for comboBoxes
    ITTicketCategory.getItems().addAll("Wifi", "Email", "Login", "Kiosk", "Pager", "Other");
    statusChangeStatus.getItems().addAll("Ticket Sent", "In Progress", "Complete");
    ObservableList<Node> allNodeList =
        FXCollections.observableArrayList(
            Graph.getInstance().getNodes().values().stream()
                .filter(node -> node.getFloor() == 1)
                .collect(Collectors.toList()));
    allNodeList.sort(Comparator.comparing(Node::getLongName));
    ITTicketLocation.setItems(allNodeList);

    // Set up table
    tblViewITTicket =
        new SimpleTableView<>(
            new ITTicket(new Timestamp(System.currentTimeMillis()), "", "", "", "", "", ""), 80.0);
    ticketTablePane.getChildren().add(tblViewITTicket);

    // Track when the mouse has clicked on the table
    tblViewITTicket.setOnMouseClicked(
        event -> {
          selected = tblViewITTicket.getSelected();
          if (selected != null) {
            descriptionBtn.disableProperty().setValue(false);
            deleteBtn.disableProperty().setValue(false);
            statusChangeStatus.disableProperty().setValue(false);
            statusChangeName.disableProperty().setValue(false);
          }
        });

    // Set max character values.
    ITTicketName.textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue.length() > 25) {
                ITTicketName.setText(newValue.substring(0, 25));
              }
            });
    statusChangeName
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue.length() > 25) {
                statusChangeName.setText(newValue.substring(0, 25));
              }
            });
    ITTicketDescription.textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue.length() > 200) {
                ITTicketDescription.setText(newValue.substring(0, 200));
              }
            });

    updateTable();
  }

  /** Updates the table with the items in the ITTicket database. */
  public void updateTable() {
    try {
      tblViewITTicket.clear();

      tblViewITTicket.add(itTicketDatabase.ITTicketObservableList());
    } catch (Exception e) {
      e.printStackTrace();
      DialogUtil.simpleErrorDialog(ITStackPane, "Error", "Failed to update IT Ticket Table");
    }
  }

  /**
   * Called when the mouse clicks on anything that isn't the table, or buttons it is disabling.
   *
   * @param mouseEvent mouseClicked
   */
  public void disableBtns(MouseEvent mouseEvent) {
    descriptionBtn.disableProperty().setValue(true);
    deleteBtn.disableProperty().setValue(true);
    statusChangeStatus.disableProperty().setValue(true);
    statusChangeName.disableProperty().setValue(true);
  }

  /**
   * Adds a ticket to the database from values in the fxml.
   *
   * @param actionEvent Submit button
   */
  public void submitTicket(ActionEvent actionEvent) {
    Timestamp ticketTime = new Timestamp(System.currentTimeMillis());
    String status = "Ticket Sent";
    String category = ITTicketCategory.getSelectionModel().getSelectedItem();
    Node location = ITTicketLocation.getSelectionModel().getSelectedItem();
    String requesterName = ITTicketName.getText();
    String completedBy = " ";
    String description = ITTicketDescription.getText();
    boolean ticket =
        itTicketDatabase.addTicket(
            ticketTime,
            status,
            category,
            location.getNodeID(),
            requesterName,
            completedBy,
            description);
    ITTicketCategory.getSelectionModel().clearSelection();
    ITTicketLocation.getSelectionModel().clearSelection();
    ITTicketDescription.clear();
    ITTicketName.clear();
    updateTable();
  }

  /**
   * Removes a ticket from the table and database.
   *
   * @param actionEvent delete button
   */
  public void deleteRequest(ActionEvent actionEvent) {
    selected = tblViewITTicket.getSelected();
    if (selected != null) {
      boolean i = itTicketDatabase.deleteTicket(Timestamp.valueOf(selected.getTicketTime()));
      updateTable();
    }
  }

  /**
   * Changes the status of the node selected from values in the fxml.
   *
   * @param actionEvent Update status button
   */
  public void changeStatus(ActionEvent actionEvent) {
    selected = tblViewITTicket.getSelected();
    boolean i =
        itTicketDatabase.changeStatus(
            Timestamp.valueOf(selected.getTicketTime()),
            statusChangeStatus.getSelectionModel().getSelectedItem(),
            statusChangeName.getText());
    System.out.println(Timestamp.valueOf(selected.getTicketTime()));
    statusChangeName.clear();
    statusChangeStatus.getSelectionModel().clearSelection();
    updateTable();
  }

  /**
   * Opens a window with the selected ticket's description displayed
   *
   * @param actionEvent description button
   */
  public void getDescription(ActionEvent actionEvent) {
    selected = tblViewITTicket.getSelected();
    if (selected != null) {
      DialogUtil.simpleInfoDialog(ITStackPane, "Description", selected.getDescription());
    }
  }

  /**
   * Calls checkUpdateStatus
   *
   * @param keyEvent typing
   */
  public void checkUpdateStatusName(KeyEvent keyEvent) {
    checkUpdateStatus();
  }

  /**
   * Calls checkUpdateStatus
   *
   * @param actionEvent comboBox selected
   */
  public void checkUpdateStatusBox(ActionEvent actionEvent) {
    checkUpdateStatus();
  }

  /**
   * Checks if the update status button can be enabled by checking for improper or unfinished values
   * in fxml
   */
  public void checkUpdateStatus() {
    selected = tblViewITTicket.getSelected();
    if (selected != null
        && statusChangeStatus.getSelectionModel().getSelectedItem() != null
        && !statusChangeName.getText().isEmpty()) {
      changeStatusBtn.disableProperty().setValue(false);
    } else {
      changeStatusBtn.disableProperty().setValue(true);
    }
  }

  /**
   * Calls checkSubmit
   *
   * @param keyEvent description and name fields
   */
  public void descriptionCheckSubmit(KeyEvent keyEvent) {
    checkSubmit();
  }

  /**
   * Calls checkSubmit
   *
   * @param actionEvent comboBox is updated
   */
  public void actionCheckSubmit(ActionEvent actionEvent) {
    checkSubmit();
  }

  /**
   * Checks if the submit ticket button can be enabled by checking for improper or unfinished values
   * in fxml
   */
  public void checkSubmit() {
    if (ITTicketLocation.getSelectionModel().getSelectedItem() != null
        && ITTicketCategory.getSelectionModel().getSelectedItem() != null
        && !ITTicketName.getText().isEmpty()
        && !ITTicketDescription.getText().isEmpty()) {
      submitBtn.disableProperty().setValue(false);
    } else {
      submitBtn.disableProperty().setValue(true);
    }
  }
}

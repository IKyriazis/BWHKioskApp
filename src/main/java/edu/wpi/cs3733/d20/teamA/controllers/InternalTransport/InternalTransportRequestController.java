package edu.wpi.cs3733.d20.teamA.controllers.InternalTransport;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.IDialogController;
import edu.wpi.cs3733.d20.teamA.database.Flower;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.util.DialogUtil;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.util.Optional;

public class InternalTransportRequestController extends AbstractController implements IDialogController {
    @FXML private JFXComboBox<Node> startList;
    @FXML private JFXComboBox<Node> destinationList;
    @FXML private JFXButton confirmButton;
    private JFXDialog dialog;

    @FXML
    public void placeOrder(ActionEvent actionEvent) {
        if (choiceFlower.getSelectionModel().getSelectedItem() != null) {
            String s = choiceFlower.getSelectionModel().getSelectedItem();
            String type = s.substring(0, s.indexOf(','));
            String color = s.substring(s.indexOf(' ') + 1);
            int count = Integer.parseInt(txtNumber.getText());

            try {
                if ((count <= 0) || (count > flDatabase.getFlowerQuantity(type, color))) {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            Optional<Node> found =
                    roomList.getItems().stream()
                            .filter(node -> node.toString().contains(roomList.getEditor().getText()))
                            .findFirst();
            if (found.isPresent()) {
                Node node = found.get();
                try {
                    int i = flDatabase.addOrder(count, type, color, node.getNodeID());
                    dialog.close();
                    DialogUtil.simpleInfoDialog(
                            dialog.getDialogContainer(), "Order Placed", "Order #" + i + " placed successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    public void updateFlowerList() {
        try {
            ObservableList<Flower> list = flDatabase.flowerOl();
            list.forEach(
                    flower -> choiceFlower.getItems().add(flower.getTypeFlower() + ", " + flower.getColor()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setDialog(JFXDialog dialog) {
        this.dialog = dialog;
    }
}

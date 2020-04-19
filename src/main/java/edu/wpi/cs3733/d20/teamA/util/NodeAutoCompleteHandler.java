package edu.wpi.cs3733.d20.teamA.util;

import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class NodeAutoCompleteHandler implements EventHandler<KeyEvent> {
  private final JFXComboBox<Node> box;
  private final javafx.scene.Node toFocus;
  private final ArrayList<Node> startingList;

  public NodeAutoCompleteHandler(
      JFXComboBox<Node> box, javafx.scene.Node toFocus, ArrayList<Node> startingList) {
    super();

    this.box = box;
    this.toFocus = toFocus;
    this.startingList = startingList;
  }

  @Override
  public void handle(KeyEvent event) {
    if (event.getCode() == KeyCode.ENTER
        || event.getCharacter().equals("\r")
        || event.getCharacter().equals("\n")) {
      if (!box.getItems().isEmpty()) {
        box.setValue(box.getItems().get(0));
        box.setItems(FXCollections.observableArrayList(box.getItems().get(0)));
      }
      toFocus.requestFocus();
    } else {
      List<Node> matches =
          startingList.stream()
              .filter(node -> node.getShortName().toLowerCase().contains(box.getEditor().getText()))
              .collect(Collectors.toList());
      box.setItems(FXCollections.observableArrayList(matches));
      box.show();
    }
  }
}

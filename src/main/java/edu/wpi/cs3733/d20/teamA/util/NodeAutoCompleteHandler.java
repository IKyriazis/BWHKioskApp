package edu.wpi.cs3733.d20.teamA.util;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.skins.JFXComboBoxListViewSkin;
import edu.wpi.cs3733.d20.teamA.graph.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.BoundExtractedResult;

public class NodeAutoCompleteHandler implements EventHandler<KeyEvent> {
  private final JFXComboBox<Node> box;
  private final javafx.scene.Node toFocus;
  private final ObservableList<Node> startingList;

  public NodeAutoCompleteHandler(
      JFXComboBox<Node> box, javafx.scene.Node toFocus, ObservableList<Node> startingList) {
    super();

    this.box = box;
    this.toFocus = toFocus;
    this.startingList = startingList;

    // Prevent the list from being hidden when you hit the space bar. Don't ask me why the skin does
    // that. I don't have
    // the faintest idea.
    JFXComboBoxListViewSkin<Node> jfxComboBoxListViewSkin = new JFXComboBoxListViewSkin<>(box);
    jfxComboBoxListViewSkin
        .getPopupContent()
        .addEventFilter(
            KeyEvent.ANY,
            event -> {
              if (event.getCode() == KeyCode.SPACE) {
                event.consume();
              }
            });
    box.setSkin(jfxComboBoxListViewSkin);
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
      List<Node> nodesList = startingList.stream().collect(Collectors.toList());
      List<BoundExtractedResult<Node>> fuzzyMatch =
          FuzzySearch.extractTop(box.getEditor().getText(), nodesList, x -> x.toString(), 12, 50);
      List<Node> matchess = new ArrayList<>();
      for (BoundExtractedResult<Node> node : fuzzyMatch) {
        Node aMatch = node.getReferent();
        matchess.add(aMatch);
      }

      List<Node> listOfNodes =
          startingList.stream()
              .filter(
                  node ->
                      fuzzyMatch
                          .stream()
                          .anyMatch(
                              match ->
                                  node.toString()
                                      .toLowerCase()
                                      .contains(match.getReferent().toString().toLowerCase())))
              .collect(Collectors.toList());

      List<Node> matches =
          startingList.stream()
              .filter(
                  node ->
                      node.toString()
                          .toLowerCase()
                          .contains(box.getEditor().getText().toLowerCase()))
              .collect(Collectors.toList());
      box.setItems(FXCollections.observableArrayList(matches)); // used to be matches
      box.setVisibleRowCount(Math.min(12, matches.size())); // used to be matches.size()
      if (box.getVisibleRowCount() > 0) {
        box.show();
      }
    }
  }
}

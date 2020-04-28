package edu.wpi.cs3733.d20.teamA.controls;

import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;

@SuppressWarnings({"rawtypes", "unchecked"}) // Suppressed b/c JFoenix is poorly written
public class SimpleTableView<T extends ITableable<T>> extends JFXTreeTableView {
  private final TreeItem<T> rootItem;

  private boolean columnsBuilt;
  private double minColumnWidth;

  public SimpleTableView(T dummy, double minColumnWidth) {
    this.rootItem = new TreeItem<>(dummy);
    this.minColumnWidth = minColumnWidth;

    // Set column resize policy to constrain them
    setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);

    // Setup root item in table
    setRoot(rootItem);
    setShowRoot(false);
  }

  private void buildColumns() {
    if (rootItem.getChildren().isEmpty()) {
      return;
    }

    // Add columns
    ArrayList<JFXTreeTableColumn<T, ?>> columns =
        rootItem.getChildren().get(0).getValue().getColumns();
    columns.forEach(
        col -> {
          col.setMinWidth(minColumnWidth);
          col.setReorderable(false);
        });

    getColumns().addAll(columns);

    columnsBuilt = true;
  }

  public void clear() {
    rootItem.getChildren().clear();
  }

  public void add(T t) {
    rootItem.getChildren().add(new TreeItem<>(t));
    if (!columnsBuilt) buildColumns();
  }

  public void add(List<T> list) {
    list.forEach(t -> rootItem.getChildren().add(new TreeItem<>(t)));
    if (!columnsBuilt) buildColumns();
  }

  public void setMinColumnWidth(double minColumnWidth) {
    this.minColumnWidth = minColumnWidth;

    for (Object col : getColumns()) {
      if (col instanceof TreeTableColumn) {
        ((TreeTableColumn) col).setMinWidth(minColumnWidth);
      }
    }
  }

  public T getSelected() {
    TreeItem<T> selected = (TreeItem<T>) getSelectionModel().getSelectedItem();

    if (selected != null) {
      return selected.getValue();
    }

    return null;
  }

  public void setGraphic(FontAwesomeIconView fontAwesomeIconView) {}
}

package edu.wpi.cs3733.d20.teamA.controls;

import com.jfoenix.controls.JFXTreeTableColumn;
import java.util.ArrayList;

public interface ITableable<T> {
  public ArrayList<JFXTreeTableColumn<T, ?>> getColumns();
}

package edu.wpi.cs3733.d20.teamA.database;

import javafx.collections.ObservableList;

public interface IDatabase<T> {

  boolean createTables();

  boolean dropTables();

  int getSize();

  boolean removeAll();

  ObservableList<T> getObservableList();
}

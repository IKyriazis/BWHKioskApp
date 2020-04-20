package edu.wpi.cs3733.d20.teamA.database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AnnouncementList {

  private static ObservableList<String> annList = FXCollections.observableArrayList();

  public static void addToList(String announcement) {
    annList.add(announcement);
  }

  public static void deleteFromList(int index) {
    annList.remove(index);
  }

  public static ObservableList<String> getList() {
    return annList;
  }
}

package edu.wpi.cs3733.d20.teamA.database;

import com.jfoenix.controls.JFXTreeTableColumn;
import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;

public class Interpreter implements ITableable<Interpreter> {
  private SimpleStringProperty name;
  private SimpleStringProperty secondLanguage;

  public Interpreter(String name, String secondLanguage) {
    this.name = new SimpleStringProperty(name);
    this.secondLanguage = new SimpleStringProperty(secondLanguage);
  }

  public String getName() {
    return name.get();
  }

  public SimpleStringProperty nameProperty() {
    return name;
  }

  public void setName(String name) {
    this.name.set(name);
  }

  public String getSecondLanguage() {
    return secondLanguage.get();
  }

  public SimpleStringProperty secondLanguageProperty() {
    return secondLanguage;
  }

  public void setSecondLanguage(String secondLanguage) {
    this.secondLanguage.set(secondLanguage);
  }

  @Override
  public ArrayList<JFXTreeTableColumn<Interpreter, ?>> getColumns() {
    JFXTreeTableColumn<Interpreter, String> col1 = new JFXTreeTableColumn<>("Name");
    col1.setCellValueFactory(param -> param.getValue().getValue().nameProperty());

    JFXTreeTableColumn<Interpreter, String> col2 = new JFXTreeTableColumn<>("Second Language");
    col2.setCellValueFactory(param -> param.getValue().getValue().secondLanguageProperty());

    return new ArrayList<>(List.of(col1, col2));
  }
}

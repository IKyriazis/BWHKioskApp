package edu.wpi.cs3733.d20.teamA.database;

import com.jfoenix.controls.JFXTreeTableColumn;
import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import edu.wpi.cs3733.d20.teamA.database.employee.EmployeeTitle;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class PublicEmployee implements ITableable<PublicEmployee> {

  private SimpleStringProperty status;
  private SimpleStringProperty fName;
  private SimpleStringProperty lName;
  private SimpleStringProperty title;
  private SimpleStringProperty pagerNum;
  private SimpleStringProperty username;

  public PublicEmployee(
      String status,
      String fName,
      String lName,
      EmployeeTitle title,
      String pagerNum,
      String username) {
    this.status = new SimpleStringProperty(status);
    this.fName = new SimpleStringProperty(fName);
    this.lName = new SimpleStringProperty(lName);
    this.title = new SimpleStringProperty(title.toString());
    this.pagerNum = new SimpleStringProperty(pagerNum);
    this.username = new SimpleStringProperty(username);
  }

  public String getStatus() {
    return status.get();
  }

  public SimpleStringProperty statusProperty() {
    return status;
  }

  public String getfName() {
    return fName.get();
  }

  public SimpleStringProperty fNameProperty() {
    return fName;
  }

  public String getlName() {
    return lName.get();
  }

  public SimpleStringProperty lNameProperty() {
    return lName;
  }

  public String getTitle() {
    return title.get();
  }

  public SimpleStringProperty titleProperty() {
    return title;
  }

  public String getPagerNum() {
    return pagerNum.get();
  }

  public SimpleStringProperty pagerNumProperty() {
    return pagerNum;
  }

  public String getUsername() {
    return username.get();
  }

  public SimpleStringProperty usernameProperty() {
    return username;
  }

  @Override
  public ArrayList<JFXTreeTableColumn<PublicEmployee, ?>> getColumns() {
    JFXTreeTableColumn<PublicEmployee, String> column1 = new JFXTreeTableColumn<>("Status");
    column1.setCellValueFactory(param -> param.getValue().getValue().statusProperty());

    JFXTreeTableColumn<PublicEmployee, String> column2 = new JFXTreeTableColumn<>("First Name");
    column2.setCellValueFactory(param -> param.getValue().getValue().fNameProperty());

    JFXTreeTableColumn<PublicEmployee, String> column3 = new JFXTreeTableColumn<>("Last Name");
    column3.setCellValueFactory(param -> param.getValue().getValue().lNameProperty());

    JFXTreeTableColumn<PublicEmployee, String> column4 = new JFXTreeTableColumn<>("Pager #");
    column4.setCellValueFactory(param -> param.getValue().getValue().pagerNumProperty());

    JFXTreeTableColumn<PublicEmployee, String> column5 = new JFXTreeTableColumn<>("Title");
    column5.setCellValueFactory(param -> param.getValue().getValue().titleProperty());

    return new ArrayList<>(List.of(column1, column2, column3, column4, column5));
  }
}

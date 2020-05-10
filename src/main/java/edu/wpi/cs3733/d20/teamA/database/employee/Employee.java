package edu.wpi.cs3733.d20.teamA.database.employee;

import com.jfoenix.controls.JFXTreeTableColumn;
import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

// Add more methods to this class as needed
public class Employee implements ITableable<Employee> {
  private SimpleStringProperty id;
  private SimpleStringProperty fName;
  private SimpleStringProperty lName;
  private SimpleStringProperty title;
  private SimpleStringProperty username;
  private SimpleStringProperty pagerNum;
  private Image img;

  public Employee(
      String id,
      String fName,
      String lName,
      EmployeeTitle title,
      String username,
      String pagerNum,
      Image pic) {
    this.id = new SimpleStringProperty(id);
    this.fName = new SimpleStringProperty(fName);
    this.lName = new SimpleStringProperty(lName);
    this.title = new SimpleStringProperty(title.toString());
    this.username = new SimpleStringProperty(username);
    this.pagerNum = new SimpleStringProperty(pagerNum);
    this.img = pic;
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

  public String toString() {
    return fName.get() + " " + lName.get();
  }

  public String getId() {
    return id.get();
  }

  public String getTitle() {
    return title.get();
  }

  public SimpleStringProperty getIDProperty() {
    return this.id;
  };

  public SimpleStringProperty getfNameProperty() {
    return this.fName;
  };

  public SimpleStringProperty getlNameProperty() {
    return this.lName;
  };

  public SimpleStringProperty getTitleProperty() {
    return this.title;
  };

  public SimpleStringProperty getUsernameProperty() {
    return this.username;
  };

  public ObservableValue<ImageView> observableImage(int size) {
    return new ObservableValue<ImageView>() {
      @Override
      public void addListener(ChangeListener<? super ImageView> listener) {}

      @Override
      public void removeListener(ChangeListener<? super ImageView> listener) {}

      @Override
      public ImageView getValue() {
        ImageView view = new ImageView(img);
        view.setPreserveRatio(true);
        view.setFitWidth(size);
        view.setFitHeight(size);
        return view;
      }

      @Override
      public void addListener(InvalidationListener listener) {}

      @Override
      public void removeListener(InvalidationListener listener) {}
    };
  }

  @Override
  public ArrayList<JFXTreeTableColumn<Employee, ?>> getColumns() {

    JFXTreeTableColumn<Employee, String> column1 = new JFXTreeTableColumn<>("Employee ID");
    column1.setCellValueFactory(param -> param.getValue().getValue().getIDProperty());

    JFXTreeTableColumn<Employee, String> column2 = new JFXTreeTableColumn<>("First Name");
    column2.setCellValueFactory(param -> param.getValue().getValue().getfNameProperty());

    JFXTreeTableColumn<Employee, String> column3 = new JFXTreeTableColumn<>("Last Name");
    column3.setCellValueFactory(param -> param.getValue().getValue().getlNameProperty());

    JFXTreeTableColumn<Employee, String> column4 = new JFXTreeTableColumn<>("Title");
    column4.setCellValueFactory(param -> param.getValue().getValue().getTitleProperty());

    JFXTreeTableColumn<Employee, String> column5 = new JFXTreeTableColumn<>("Username");
    column5.setCellValueFactory(param -> param.getValue().getValue().getUsernameProperty());

    JFXTreeTableColumn<Employee, String> column6 = new JFXTreeTableColumn<>("Pager #");
    column6.setCellValueFactory(param -> param.getValue().getValue().pagerNumProperty());

    JFXTreeTableColumn<Employee, ImageView> column7 = new JFXTreeTableColumn<>("Profile Picture");
    column7.setCellValueFactory(param -> param.getValue().getValue().observableImage(60));

    return new ArrayList<>(List.of(column1, column2, column3, column4, column5, column6, column7));
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Employee) {
      return username.getValue().equals(((Employee) obj).getUsername());
    }
    return false;
  }
}

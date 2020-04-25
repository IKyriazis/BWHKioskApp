package edu.wpi.cs3733.d20.teamA.database;

import com.jfoenix.controls.JFXTreeTableColumn;
import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class ITTicket implements ITableable<ITTicket> {

    @Setter
    private SimpleStringProperty name;
    @Setter
    private SimpleStringProperty location;
    @Setter
    private SimpleStringProperty category;
    @Setter
    private SimpleStringProperty description;
    @Setter
    private SimpleStringProperty status;
    @Setter
    private SimpleStringProperty completedBy;

    public ITTicket(SimpleStringProperty name, SimpleStringProperty location, SimpleStringProperty category, SimpleStringProperty description, SimpleStringProperty status, SimpleStringProperty completedBy) {
        this.name = name;
        this.location = location;
        this.category = category;
        this.description = description;
        this.status = status;
        this.completedBy = completedBy;
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public String getLocation() {
        return location.get();
    }

    public SimpleStringProperty locationProperty() {
        return location;
    }

    public String getCategory() {
        return category.get();
    }

    public SimpleStringProperty categoryProperty() {
        return category;
    }

    public String getDescription() {
        return description.get();
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public String getStatus() {
        return status.get();
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }

    public String getCompletedBy() {
        return completedBy.get();
    }

    public SimpleStringProperty completedByProperty() {
        return completedBy;
    }

    @Override
    public ArrayList<JFXTreeTableColumn<ITTicket, ?>> getColumns() {
        JFXTreeTableColumn<ITTicket, String> column1 = new JFXTreeTableColumn<>("Status");
        column1.setCellValueFactory(param -> param.getValue().getValue().statusProperty());
        JFXTreeTableColumn<ITTicket, String> column2 = new JFXTreeTableColumn<>("Category");
        column2.setCellValueFactory(param -> param.getValue().getValue().categoryProperty());
        JFXTreeTableColumn<ITTicket, String> column3 = new JFXTreeTableColumn<>("Location");
        column3.setCellValueFactory(param -> param.getValue().getValue().locationProperty());
        JFXTreeTableColumn<ITTicket, String> column4 = new JFXTreeTableColumn<>("Requester Name");
        column4.setCellValueFactory(param -> param.getValue().getValue().nameProperty());
        JFXTreeTableColumn<ITTicket, String> column5 = new JFXTreeTableColumn<>("Employee Name");
        column5.setCellValueFactory(param -> param.getValue().getValue().completedByProperty());

        return new ArrayList<>(List.of(column1, column2, column3, column4, column5));
    }
}

package edu.wpi.cs3733.d20.teamA.database;

import com.jfoenix.controls.JFXTreeTableColumn;
import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Laundry implements ITableable<Laundry> {
    @Setter
    private SimpleIntegerProperty requestNum;
    @Setter
    private SimpleStringProperty employeeEntered;
    @Setter
    private SimpleStringProperty location;
    @Setter
    private SimpleStringProperty progress;
    @Setter
    private SimpleStringProperty employeeWash;
    @Setter
    private SimpleStringProperty timeRequested;

    public Laundry(int requestNum, String employeeEntered, String location, String progress, String employeeWash, Timestamp timeRequested) {
        this.requestNum = new SimpleIntegerProperty(requestNum);
        this.employeeEntered = new SimpleStringProperty(employeeEntered);
        this.location = new SimpleStringProperty(location);
        this.progress = new SimpleStringProperty(progress);
        this.employeeWash = new SimpleStringProperty(employeeWash);
        this.timeRequested = new SimpleStringProperty(timeRequested.toString());
    }

    public SimpleIntegerProperty requestNumProperty() {return requestNum;}

    public SimpleStringProperty employeeEnteredProperty() {return employeeEntered;}

    public SimpleStringProperty locationProperty() {return location;}

    public SimpleStringProperty progressProperty() {return progress;}

    public SimpleStringProperty employeeWashProperty() {return employeeWash;}

    public SimpleStringProperty timeRequestedProperty() {return timeRequested;}

    public int getRequestNum() {return requestNum.get();}

    public String getEmployeeEntered() {return employeeEntered.get();}

    public String getLocation() {return location.get();}

    public String getProgress() {return progress.get();}

    public String getEmployeeWash() {return employeeWash.get();}

    public String getTimeRequested() {return timeRequested.get();}

    @Override
    public ArrayList<JFXTreeTableColumn<Laundry, ?>> getColumns() {
        JFXTreeTableColumn<Laundry, String> column1 = new JFXTreeTableColumn<>("Request #");
        column1.setCellValueFactory(param -> param.getValue().getValue().requestNumProperty().asObject());
    }
}

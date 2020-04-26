package edu.wpi.cs3733.d20.teamA.database;

import com.jfoenix.controls.JFXTreeTableColumn;
import edu.wpi.cs3733.d20.teamA.controls.ITableable;

import java.util.ArrayList;

public class Employee implements ITableable<Employee> {


    @Override
    public ArrayList<JFXTreeTableColumn<Employee, ?>> getColumns() {
        return null;
    }
}

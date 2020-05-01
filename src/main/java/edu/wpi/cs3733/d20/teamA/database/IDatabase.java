package edu.wpi.cs3733.d20.teamA.database;

import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import javafx.collections.ObservableList;

public interface IDatabase {

    boolean createTables();
    boolean dropTables();
    int getSize();
    boolean removeAll();
    ObservableList<ITableable> getObservableList();

}

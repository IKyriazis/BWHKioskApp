package edu.wpi.cs3733.d20.teamA.database;

import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class OnCallDatabase extends Database implements IDatabase{

    public OnCallDatabase(Connection connection) {

        super(connection);

        if (doesTableNotExist("OnCall")) {
            createTables();
        }
    }

    /**
     * Drop the 'EquipReq' table
     *
     * @return Success / Failure
     */
    public synchronized boolean dropTables() {

        // Drop the tables
        if (!(helperPrepared("ALTER TABLE OnCall DROP CONSTRAINT FK_OCID"))) {

            return false;
        }

        // Drop the tables
        if (!(helperPrepared("DROP TABLE OnCall"))) {
            return false;
        }

        return true;
    }

    /**
     * Creates graph tables
     *
     * @return False if tables couldn't be created
     */
    public synchronized boolean createTables() {

        // Create the graph tables

        return helperPrepared(
                "CREATE TABLE OnCall (username Varchar(25), status Varchar(9), CONSTRAINT FK_OCID FOREIGN KEY (username) REFERENCES Employees(username), CONSTRAINT CK_ST CHECK (status in('Available', 'Busy', 'Out')))");
    }

    @Override
    public int getSize() {
        return getSize("OnCall");
    }

    @Override
    public boolean removeAll() {
        return helperPrepared("DELETE From OnCall");
    }

    @Override
    public ObservableList<ITableable> getObservableList() {
        ObservableList<ITableable> rList = FXCollections.observableArrayList();
        try {
            Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM EquipReq");
            ResultSet rset = pstmt.executeQuery();
            while (rset.next()) {
                String name = getNamefromUser(rset.getString("username"));
                Timestamp timeOf = rset.getTimestamp("timeOF");
                String item = rset.getString("item");
                int qty = rset.getInt("qty");
                String location = rset.getString("location");
                String priority = rset.getString("priority");
                String username = rset.getString("username");

                EquipRequest node = new EquipRequest(name, item, null, null, "");

                rList.add(node);
            }
            rset.close();
            pstmt.close();
            conn.close();
            return rList;
        } catch (SQLException e) {
            e.printStackTrace();
            return rList;
        }
    }

}

package edu.wpi.cs3733.d20.teamA.database;

import at.favre.lib.crypto.bcrypt.BCrypt;
import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import edu.wpi.cs3733.d20.teamA.database.employee.EmployeeTitle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigInteger;
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
                "CREATE TABLE OnCall (username Varchar(25) PRIMARY KEY, status Varchar(9), CONSTRAINT FK_OCID FOREIGN KEY (username) REFERENCES Employees(username), CONSTRAINT CK_ST CHECK (status in('Available', 'Busy', 'Out')))");
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
    public ObservableList<PublicEmployee> getObservableList() {
        ObservableList<PublicEmployee> eList = FXCollections.observableArrayList();
        try {
            Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM OnCall");
            ResultSet rset = pstmt.executeQuery();
            while (rset.next()) {
                String username = rset.getString("username");
                String nameF = getNamefromUserF(username);
                String nameL = getNamefromUser(username);
                String status = rset.getString("status");
                String title = getTitle(username);
                Long pageNum = getPager(username);

                PublicEmployee node = new PublicEmployee(status, nameF, nameL, title, pageNum, username);

                eList.add(node);
            }
            rset.close();
            pstmt.close();
            conn.close();
            return eList;
        } catch (SQLException e) {
            e.printStackTrace();
            return eList;
        }
    }

    public synchronized boolean updateStatus(String username, String newStatus) {
        try {
            PreparedStatement pstmt =
                    getConnection()
                            .prepareStatement(
                                    "UPDATE OnCall SET status = '"
                                            + newStatus
                                            + "' WHERE username = '"
                                            + username
                                            + "'");
            pstmt.executeUpdate();
            pstmt.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public synchronized boolean signOntoShift(String username) {

        try {
            PreparedStatement pstmt =
                    getConnection()
                            .prepareStatement(
                                    "INSERT INTO OnCall (username, status)" +
                                            " VALUES (?, ?)");
            pstmt.setString(1, username);
            pstmt.setString(2, "Available");
            pstmt.executeUpdate();
            pstmt.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public synchronized boolean signOffShift(String username) {
        try {
            PreparedStatement pstmt =
                    getConnection().prepareStatement("DELETE From OnCall Where username = ?");
            pstmt.setString(1, username);
            pstmt.executeUpdate();
            pstmt.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



}

package edu.wpi.cs3733.d20.teamA.database;

import at.favre.lib.crypto.bcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ServiceDatabase extends Database{


    public ServiceDatabase(Connection connection) {
        super(connection);
    }

    public synchronized boolean dropTables() {

        // Drop the tables
        if (!(helperPrepared("ALTER TABLE SERVICEREQ DROP CONSTRAINT FK_REQEMP") && helperPrepared("ALTER TABLE SERVICEREQ DROP CONSTRAINT FK_EMPUSE"))) {

            return false;
        }
        // Drop the tables
        if (!(helperPrepared("DROP TABLE SERVICEREQ"))) {
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

        if (doesTableNotExist("SERVICEREQ")) {
            return helperPrepared(
                    "CREATE TABLE SERVICEREQ (servType Varchar(10), reqID Varchar(6) PRIMARY KEY, didReqName Varchar(25), madeReqName Varchar(25), timeOfReq Timestamp, status Varchar(20), location Varchar(10), description Varchar(100), additional Varchar(2000), CONSTRAINT CK_TYPE CHECK (servType in('janitor', 'medicine', 'equipreq', 'laundry', 'ittix', 'intrntrans', 'interpret', 'rxreq')), CONSTRAINTS FK_REQEMP (didReqName) REFERENCES EMPLOYEES(username), CONSTRAINTS FK_EmpUse (madeReqName) REFERENCES EMPLOYEES(username), CONSTRAINT CK_STAT CHECK (status in ('Request Made', 'In Progress', 'Completed')))");

        }
        return false;
    }

    public synchronized boolean addServiceReq(String servType, String location, String description, String additional) {

        String reqID = null;
        String madeReqName = getLoggedIn();
        String didReqName = null;
        Timestamp timeOf = new Timestamp(System.currentTimeMillis());
        String status = "Request Made";

        try {
            PreparedStatement pstmt =
                    getConnection()
                            .prepareStatement(
                                    "INSERT INTO SERVICEREQ (servType, reqID, didReqName, madeReqName, timeOfReq, status, location, description, additional) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
            pstmt.setString(1, servType);
            pstmt.setString(2, reqID);
            pstmt.setString(3, didReqName);
            pstmt.setString(4, madeReqName);
            pstmt.setTimestamp(5, timeOf);
            pstmt.setString(6, status);
            pstmt.setString(7, location);
            pstmt.setString(8, description);
            pstmt.setString(9, additional);
            pstmt.executeUpdate();
            pstmt.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteServReq(String reqID) {
        try {
            PreparedStatement pstmt =
                    getConnection()
                            .prepareStatement(
                                    "DELETE From SERVICEREQ Where reqID = '"
                                            + reqID
                                            + "'");
            pstmt.executeUpdate();
            pstmt.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getSizeReq() {
        return getSize("SERVICEREQ");
    }

    public boolean removeAllReqs() {
        return helperPrepared("DELETE From SERVICEREQ");
    }

    public synchronized boolean editStatus(String reqID, String newStatus) {
        try {
            PreparedStatement pstmt =
                    getConnection()
                            .prepareStatement(
                                    "UPDATE SERVICEREQ SET status = '"
                                            + newStatus
                                            + "' WHERE reqID = '"
                                            + reqID
                                            + "'");
            pstmt.executeUpdate();
            pstmt.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}

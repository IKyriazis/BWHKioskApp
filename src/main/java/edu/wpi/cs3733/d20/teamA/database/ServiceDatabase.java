package edu.wpi.cs3733.d20.teamA.database;

import at.favre.lib.crypto.bcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
        String didReqName;
        /*
        try {
            PreparedStatement pstmt =
                    getConnection()
                            .prepareStatement(
                                    "INSERT INTO Employees (employeeID, nameFirst, nameLast, username, password, title) VALUES (?, ?, ?, ?, ?, ?)");
            pstmt.setInt(1, employeeID);
            pstmt.setString(2, nameFirst);
            pstmt.setString(3, nameLast);
            pstmt.setString(4, username);
            pstmt.setString(5, storedPassword);
            pstmt.setString(6, title);
            pstmt.executeUpdate();
            pstmt.close();
            this.employeeID++;
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

         */
        return true;
    }



}

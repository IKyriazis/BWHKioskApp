package edu.wpi.cs3733.d20.teamA.database.reservation;

import java.sql.*;
import java.util.GregorianCalendar;

import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import edu.wpi.cs3733.d20.teamA.database.Database;
import edu.wpi.cs3733.d20.teamA.database.IDatabase;
import javafx.collections.ObservableList;

public class ReservationDatabase extends Database implements IDatabase<ITableable> {

    public ReservationDatabase(Connection conn) {
        super(conn);
        createTables();
    }

    public boolean dropTables() {
        if(!doesTableNotExist("RESERVATION")) {
            return helperPrepared("DROP TABLE Reservation");
        }
        return false;
    }

    public boolean createTables() {
        if(doesTableNotExist("RESERVATION")) {
            return helperPrepared("CREATE TABLE Reservation (requestedBy Varchar(25), startTime TIMESTAMP, endTime TIMESTAMP, areaReserved Varchar(50), CONSTRAINT FK_ResEmp FOREIGN KEY (requestedBy) REFERENCES Employees(username), CONSTRAINT PK_Res PRIMARY KEY (startTime, areaReserved))");
        }
        return false;
    }

    public int getSize() {
        return getSize("Reservation");
    }

    public boolean removeAll() {
        return helperPrepared("DELETE FROM Reservation");
    }

    public boolean addRes(Timestamp start, Timestamp end, String loc) {
        boolean a = checkIfExistsTSandString("Reservation", "startTime", start, "areaReserved", loc);
        try{
            if(!a) {
                PreparedStatement pstmt = getConnection().prepareStatement("INSERT INTO Reservation (requestedBy, startTime, endTime, areaReserved) VALUES (?, ?, ?, ?)");
                pstmt.setString(1, getLoggedIn().getUsername());
                pstmt.setTimestamp(2, start);
                pstmt.setTimestamp(3, end);
                pstmt.setString(4, loc);
                pstmt.executeUpdate();
                pstmt.close();
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRes(Timestamp start, String loc) {
        boolean a = checkIfExistsTSandString("Reservation", "startTime", start, "areaReserved", loc);
        try {
            if(a) {
                PreparedStatement pstmt = getConnection().prepareStatement("DELETE FROM Reservation WHERE startTime = ? AND areaReserved = ?");
                pstmt.setTimestamp(1, start);
                pstmt.setString(2, loc);
                pstmt.executeUpdate();
                pstmt.close();
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getRequestedBy(Timestamp start, String loc) {
        try {
            PreparedStatement pstmt = getConnection().prepareStatement("SELECT * FROM Reservation WHERE startTime = ? AND areaReserved = ?");
            pstmt.setTimestamp(1, start);
            pstmt.setString(2, loc);
            ResultSet rset = pstmt.executeQuery();
            if(rset.next()) {
                return rset.getString("requestedBy");
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Timestamp getEndTime(Timestamp start, String loc) {
        try {
            PreparedStatement pstmt = getConnection().prepareStatement("SELECT * FROM Reservation WHERE startTime = ? AND areaReserved = ?");
            pstmt.setTimestamp(1, start);
            pstmt.setString(2, loc);
            ResultSet rset = pstmt.executeQuery();
            if(rset.next()) {
                return rset.getTimestamp("endTime");
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean setRequestedBy(Timestamp start, String loc, String req) {
        boolean a = checkIfExistsTSandString("Reservation", "startTime", start, "areaReserved", loc);
        try {
            if(a) {
                PreparedStatement pstmt = getConnection().prepareStatement("UPDATE Reservation SET requestedBy = ? WHERE startTime = ? AND areaReserved = ?");
                pstmt.setString(1, req);
                pstmt.setTimestamp(2, start);
                pstmt.setString(3, loc);
                pstmt.executeUpdate();
                pstmt.close();
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ObservableList<ITableable> getObservableList() {
        return null;
    }
}

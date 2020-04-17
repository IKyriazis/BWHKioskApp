package edu.wpi.cs3733.d20.teamA.database;

import java.sql.*;

public class GraphDatabase {


    /**
     * makes connection
     *
     * @param str the sql statement in a string
     * @return false if anything goes wrong
     * @throws SQLException
     */
    public boolean helperPrepared(String str) throws SQLException {

        try {
            Connection conn = DriverManager.getConnection("jdbc:derby:BWDatabase");

            PreparedStatement stmt = conn.prepareStatement(str);

            stmt.executeUpdate();
            stmt.close();
            conn.close();
            return true;
        } catch(SQLException e){

            return false;

        }

    }

    /**
     * Drops the graph tables so we can start fresh
     *
     * @return false if the tables don't exist and constraints can't be dropped, true if constraints and tables are dropped correctly
     * @throws SQLException
     */
    public boolean dropTables() throws SQLException{


        //if the helper returns false this method should too
        //drop the constraints first
        if(helperPrepared("ALTER TABLE Edge DROP CONSTRAINT FK_SN") || helperPrepared("ALTER TABLE Edge DROP CONSTRAINT FK_EN")){

            return false;

        }
        //Drop the tables
        helperPrepared("DROP TABLE Edge");
        helperPrepared("DROP TABLE Node");

        return true;

    }

    /**
     * Creates graph tables
     *
     * @throws SQLException
     */
    public void createTables() throws SQLException {

        //Create the graph tables
        helperPrepared("CREATE TABLE Edge (edgeID Varchar(21) PRIMARY KEY, startNode Varchar(10), endNode Node Varchar(10), CONSTRAINT FK_SN FOREIGN KEY (startNode) REFERENCES Node(nodeID), CONSTRAINT FK_EN FOREIGN KEY (endNode) REFERENCES Node(nodeID))");

        helperPrepared("CREATE TABLE Node (nodeID Varchar(10) PRIMARY KEY, xcoord INTEGER, ycoord INTEGER, floor INTEGER, building Varchar(50), nodeType Varchar(4), longName Varchar(200), shortName Varchar(25), teamAssigned Varchar(10), CONSTRAINT CHK_Floor CHECK (floor >= 1 AND floor<= 10), CONSTRAINT CHK_Coords CHECK (xcoord > 0 AND ycoord > 0), CONSTRAINT CHK_Type CHECK nodeType=('HALL', 'ELEV', 'REST', 'STAI', 'DEPT', 'LABS', 'INFO', 'CONF', 'EXIT', 'RETL', 'SERV'))");

    }

}

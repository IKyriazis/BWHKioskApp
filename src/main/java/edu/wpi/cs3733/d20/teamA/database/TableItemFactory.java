package edu.wpi.cs3733.d20.teamA.database;

import edu.wpi.cs3733.d20.teamA.controls.ITableable;
import java.sql.Timestamp;

public class TableItemFactory {

  public static ITableable get(
      String servType,
      String reqID,
      String didReqName,
      String madeReqName,
      Timestamp timeOfReq,
      String status,
      String location,
      String description,
      String additional) {
    // 'janitor', 'medicine', 'equipreq', 'laundry', 'ittix', 'intrntrans', 'interpret', 'rxreq'
    switch (servType) {
      case "janitor":
        return new JanitorService(location, description, status, madeReqName, reqID, didReqName);
        // what is index
      case "medicine":
        return new MedRequest(
            reqID,
            status,
            additional,
            description,
            timeOfReq,
            didReqName); // todo figure out how to pass in med request, probably need to change
        // parameters
      case "equipreq":
        return new EquipRequest(reqID, location, madeReqName, timeOfReq, additional);
      case "laundry":
        return new Laundry(
            reqID,
            madeReqName,
            location,
            status,
            didReqName,
            timeOfReq); // Change requestID to a string
      case "ittix":
        return new ITTicket(
            reqID, timeOfReq, status, additional, location, madeReqName, didReqName, description);
      case "intrntrans":
        return new InternalTransportRequest(
            reqID,
            description,
            location,
            timeOfReq,
            status,
            additional); // Additional hold name because it doenst check table
      case "interpret":
        return new InterpreterRequest(9, "Name", "Language", location, status);
      case "rxreq":
        return new Prescription(reqID, description, additional);
      default:
        return null;
    }
  }
}

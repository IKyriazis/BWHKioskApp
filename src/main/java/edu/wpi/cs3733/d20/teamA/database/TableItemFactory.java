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
        return new JanitorService(
            location,
            "Priority",
            status,
            madeReqName,
            0,
            "If you are seeing this change long name in service factory"); // todo parse priority,
        // what is index
      case "medicine":
        return new MedRequest(
            reqID,
            "First name",
            "Last Name",
            "doctor",
            "Medicine",
            11,
            status,
            12,
            12,
            didReqName); // todo figure out how to pass in med request, probably need to change
        // parameters
      case "equipreq":
        return new EquipRequest("Name", "item", 12, location, "priority", timeOfReq, didReqName);
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
            timeOfReq, status, "CATEGORY", location, "Name", didReqName, description);
      case "intrntrans":
        return new InternalTransportRequest(12, "Start", didReqName, "Time", status, "Name");
      case "interpret":
        return new InterpreterRequest(9, "Name", "Language", location, status);
      case "rxreq":
        return new Prescription(
            2, "PatientName", "Prescription", "Pharmacy", "Dosage", 4, "Doc name", "Notes");
      default:
        return null;
    }
  }
}

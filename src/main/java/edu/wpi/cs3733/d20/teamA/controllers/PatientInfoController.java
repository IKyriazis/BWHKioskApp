package edu.wpi.cs3733.d20.teamA.controllers;


public class PatientInfoController extends AbstractController {

    public PatientInfoController(){}

    public void initialize(){
        if (flDatabase.getSizeFlowers() == -1) {
            flDatabase.dropTables();
            flDatabase.createTables();
            flDatabase.readFlowersCSV();
            flDatabase.readFlowerOrderCSV();
        } else if (flDatabase.getSizeFlowers() == 0 || flDatabase.getSizeOrders() == 0) {
            flDatabase.removeAllFlowers();
            flDatabase.removeAllOrders();
            flDatabase.readFlowersCSV();
            flDatabase.readFlowerOrderCSV();
        }
    }
}

package edu.wpi.cs3733.d20.teamA.controllers.dialog;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

public class WebCamPopupController implements IDialogController {
    @FXML private BorderPane bPane;
    @FXML private JFXButton lButton;
    @FXML private JFXButton rButton;
    private JFXDialog dialog;

    public void initialize() {

    }

    @Override
    public void setDialog(JFXDialog dialog) {
        this.dialog = dialog;
    }
}

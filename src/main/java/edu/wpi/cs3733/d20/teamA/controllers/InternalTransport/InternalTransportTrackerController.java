package edu.wpi.cs3733.d20.teamA.controllers.InternalTransport;

import com.jfoenix.controls.JFXDialog;
import edu.wpi.cs3733.d20.teamA.controllers.AbstractController;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.IDialogController;

public class InternalTransportTrackerController extends AbstractController
    implements IDialogController {
  private JFXDialog dialog;

  @Override
  public void setDialog(JFXDialog dialog) {
    this.dialog = dialog;
  }
}

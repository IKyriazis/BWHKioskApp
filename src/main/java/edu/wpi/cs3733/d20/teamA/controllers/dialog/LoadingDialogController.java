package edu.wpi.cs3733.d20.teamA.controllers.dialog;

import com.jfoenix.controls.JFXDialog;

public class LoadingDialogController implements IDialogController {
  private JFXDialog dialog;

  @Override
  public void setDialog(JFXDialog dialog) {
    this.dialog = dialog;
    dialog.setOverlayClose(false);
  }
}

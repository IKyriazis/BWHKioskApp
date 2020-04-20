package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXDialog;

public abstract class DialogController {
  private JFXDialog dialog;

  public JFXDialog getDialog() {
    return dialog;
  }

  public void setDialog(JFXDialog dialog) {
    this.dialog = dialog;
  }
}

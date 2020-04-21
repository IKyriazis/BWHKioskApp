package edu.wpi.cs3733.d20.teamA.controller;

import javafx.scene.input.KeyCode;
import org.testfx.api.FxRobot;

public abstract class TestAbstractController extends FxRobot {

  void writeString(String s) {
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (Character.isUpperCase(c)) {
        press(KeyCode.SHIFT);
      }
      press(KeyCode.valueOf(String.valueOf(Character.toUpperCase(c))));
      release(KeyCode.valueOf(String.valueOf(Character.toUpperCase(c))));

      if (Character.isUpperCase(c)) {
        release(KeyCode.SHIFT);
      }
    }
  }

  void writeInt(String s) {
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      String keyCodeString = "DIGIT";
      keyCodeString += String.valueOf(c);
      press(KeyCode.valueOf(keyCodeString));
      release(KeyCode.valueOf(keyCodeString));
    }
  }
}

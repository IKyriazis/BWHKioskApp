package edu.wpi.cs3733.d20.teamA.controls;

import animatefx.animation.*;
import javafx.scene.Node;

public enum TransitionType {
  FADE,
  ZOOM,
  BOUNCE,
  FLIP,
  LIGHTSPEED,
  ROTATE,
  ROLL;

  public AnimationFX getTransitionIn(Node node, boolean dir) {
    if (this == FADE) {
      return dir ? new FadeInRightBig(node) : new FadeInLeftBig(node);
    } else if (this == ZOOM) {
      return new ZoomIn(node);
    } else if (this == BOUNCE) {
      return new BounceIn(node);
    } else if (this == FLIP) {
      return new FlipInY(node);
    } else if (this == LIGHTSPEED) {
      return new LightSpeedIn(node);
    } else if (this == ROTATE) {
      return new RotateIn(node);
    } else if (this == ROLL) {
      return new RollIn(node);
    } else {
      return new ZoomIn(node);
    }
  }

  public AnimationFX getTransitionOut(Node node, boolean dir) {
    if (this == FADE) {
      return dir ? new FadeOutLeftBig(node) : new FadeOutRightBig(node);
    } else if (this == ZOOM) {
      return new ZoomOut(node);
    } else if (this == BOUNCE) {
      return new BounceOut(node);
    } else if (this == FLIP) {
      return new FlipOutY(node);
    } else if (this == LIGHTSPEED) {
      return new LightSpeedOut(node);
    } else if (this == ROTATE) {
      return new RotateOut(node);
    } else if (this == ROLL) {
      return new RollOut(node);
    } else {
      return new ZoomOut(node);
    }
  }
}

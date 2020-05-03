package edu.wpi.cs3733.d20.teamA.controls;

import animatefx.animation.*;
import javafx.scene.Node;

public enum TransitionType {
  FADE,
  ZOOM,
  BOUNCE,
  FLIP;

  public AnimationFX getTransitionIn(Node node, boolean dir) {
    if (this == FADE) {
      return dir ? new FadeInRightBig(node) : new FadeInLeftBig(node);
    } else if (this == ZOOM) {
      return new ZoomIn(node);
    } else {
      return new ZoomIn(node);
    }
  }

  public AnimationFX getTransitionOut(Node node, boolean dir) {
    if (this == FADE) {
      return dir ? new FadeOutLeftBig(node) : new FadeOutRightBig(node);
    } else if (this == ZOOM) {
      return new ZoomOut(node);
    } else {
      return new ZoomOut(node);
    }
  }
}

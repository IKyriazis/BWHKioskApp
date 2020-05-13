package edu.wpi.cs3733.d20.teamA.memento;

import edu.wpi.cs3733.d20.teamA.App;

public class Originator {
  private App state;

  public Originator() {}

  public void setState(App state) {
    this.state = state;
  }

  public App getStateFromMemento() {
    return state;
  }
}

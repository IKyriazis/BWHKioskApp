package edu.wpi.cs3733.d20.teamA.memento;

import edu.wpi.cs3733.d20.teamA.App;

public class Memento {

  private App program = new App();

  public Memento(String[] args) {
    Caretaker care = new Caretaker();
    Originator origin = new Originator();
    program.launch(App.class, args);

    origin.setState(program);
    care.add(this);
  }

  public App getState() {
    return program;
  }
}

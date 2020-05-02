package edu.wpi.cs3733.d20.teamA.interpreter;

import edu.wpi.cs3733.d20.teamA.database.service.interpreter.Interpreter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestInterpreter {
  @Test
  public void testInterpreter() {
    Interpreter interpreter = new Interpreter("Yash", "Latin");

    Assertions.assertEquals(interpreter.getName(), "Yash");
    Assertions.assertEquals(interpreter.getSecondLanguage(), "Latin");

    Assertions.assertEquals(interpreter.nameProperty().get(), "Yash");
    Assertions.assertEquals(interpreter.secondLanguageProperty().get(), "Latin");
  }
}

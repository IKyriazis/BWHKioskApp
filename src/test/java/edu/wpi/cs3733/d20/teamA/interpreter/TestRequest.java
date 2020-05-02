package edu.wpi.cs3733.d20.teamA.interpreter;

import edu.wpi.cs3733.d20.teamA.database.service.interpreter.InterpreterRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestRequest {
  @Test
  public void testRequest() {
    InterpreterRequest req = new InterpreterRequest(0, "Yash", "Latin", "Worcester", "Submitted");

    Assertions.assertEquals(req.getRequestNumber(), 0);
    Assertions.assertEquals(req.getName(), "Yash");
    Assertions.assertEquals(req.getLanguage(), "Latin");
    Assertions.assertEquals(req.getLocation(), "Worcester");
    Assertions.assertEquals(req.getStatus(), "Submitted");
  }
}

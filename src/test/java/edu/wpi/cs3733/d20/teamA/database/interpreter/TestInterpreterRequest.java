package edu.wpi.cs3733.d20.teamA.database.interpreter;

import edu.wpi.cs3733.d20.teamA.database.service.interpreter.InterpreterRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestInterpreterRequest {
  @Test
  public void testRequest() {
    InterpreterRequest req = new InterpreterRequest("0", "Yash", "Latin", "Worcester", "Submitted");

    Assertions.assertEquals(req.getRequestID(), "");
    Assertions.assertEquals(req.getName(), "Yash");
    Assertions.assertEquals(req.getLanguage(), "Latin");
    Assertions.assertEquals(req.getLocation(), "Worcester");
    Assertions.assertEquals(req.getStatus(), "Submitted");
  }
}

package edu.wpi.cs3733.d20.teamA.util;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

// Returns a formatter that limits a text field to double or integer input
//Just set text formatter to InputFormatUtil.get(Double/Int)Filter()
public class InputFormatUtil {
  private static final Pattern doubleState = Pattern.compile("-?(([1-9][0-9]*)|0)?(\\.[0-9]*)?");
  private static final Pattern integerState = Pattern.compile("^\\d+$");

  private static final UnaryOperator<TextFormatter.Change> filterDouble =
      c -> {
        String text = c.getControlNewText();
        if (doubleState.matcher(text).matches()) {
          return c;
        } else {
          return null;
        }
      };
  private static final UnaryOperator<TextFormatter.Change> filterInt =
      c -> {
        String text = c.getControlNewText();
        if (integerState.matcher(text).matches()) {
          return c;
        } else {
          return null;
        }
      };

  private static final StringConverter<Double> convertD =
      new StringConverter<Double>() {

        @Override
        public Double fromString(String s) {
          if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s)) {
            return 0.0;
          } else {
            return Double.valueOf(s);
          }
        }

        @Override
        public String toString(Double d) {
          return d.toString();
        }
      };

  private static final StringConverter<Integer> convertI =
      new StringConverter<Integer>() {

        @Override
        public Integer fromString(String s) {
          if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s)) {
            return 0;
          } else {
            return Integer.valueOf(s);
          }
        }

        @Override
        public String toString(Integer i) {
          return i.toString();
        }
      };

  public static TextFormatter<Integer> getIntFilter() {
    return new TextFormatter<Integer>(convertI, 0, filterInt);
  }

  public static TextFormatter<Double> getDoubleFilter() {
    return new TextFormatter<Double>(convertD, 0.0, filterDouble);
  }
}

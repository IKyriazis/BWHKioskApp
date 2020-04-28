package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXColorPicker;
import edu.wpi.cs3733.d20.teamA.App;
import java.io.*;
import java.net.URI;
import java.nio.file.Paths;
import javafx.css.CssParser;
import javafx.css.Rule;
import javafx.css.Stylesheet;
import javafx.css.converter.ColorConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.paint.Color;

public class SettingsController {
  @FXML private JFXColorPicker primaryPicker;
  @FXML private JFXColorPicker primaryLightPicker;
  @FXML private JFXColorPicker primaryDarkPicker;

  private static final String themePath = "theme.css";

  @FXML
  public void initialize() {
    File file = new File(themePath);
    primaryPicker.setValue(getColor("-primary-color"));
    primaryLightPicker.setValue(getColor("-primary-color-light"));
    primaryDarkPicker.setValue(getColor("-primary-color-dark"));
  }

  private Color getColor(String property) {
    CssParser parser = new CssParser();
    try {
      Stylesheet stylesheet = parser.parse(App.class.getResource("stylesheet.css").toURI().toURL());
      Rule rootRule = stylesheet.getRules().get(0);
      return rootRule.getDeclarations().stream()
          .filter(declaration -> declaration.getProperty().equals(property))
          .findFirst()
          .map(
              declaration ->
                  ColorConverter.getInstance().convert(declaration.getParsedValue(), null))
          .orElse(Color.WHITESMOKE);
    } catch (Exception e) {
      e.printStackTrace();
      return Color.WHITESMOKE;
    }
  }

  private String colorToHex(Color color) {
    String hexColor = "#";

    String redHex = Integer.toHexString((int) (color.getRed() * 255));
    if (redHex.length() == 1) {
      redHex = "0" + redHex;
    }
    hexColor += redHex;

    String greenHex = Integer.toHexString((int) (color.getGreen() * 255));
    if (greenHex.length() == 1) {
      greenHex = "0" + greenHex;
    }
    hexColor += greenHex;

    String blueHex = Integer.toHexString((int) (color.getBlue() * 255));
    if (blueHex.length() == 1) {
      blueHex = "0" + greenHex;
    }
    hexColor += blueHex;

    return hexColor;
  }

  @FXML
  private void updateTheme(ActionEvent event) {
    // Write theme to temporary file
    File themeFile = new File(themePath);
    try {
      if (themeFile.createNewFile()) {
        BufferedWriter writer = new BufferedWriter(new FileWriter(themeFile));
        writer.write(".root {\n");

        writer.write("-primary-color: " + colorToHex(primaryPicker.getValue()) + ";\n");
        writer.write("-primary-color-light: " + colorToHex(primaryLightPicker.getValue()) + ";\n");
        writer.write("-primary-color-dark: " + colorToHex(primaryDarkPicker.getValue()) + ";\n");

        writer.write("}\n");
        writer.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    // Remove existing theme if loaded
    Scene scene = primaryPicker.getScene();
    scene
        .getStylesheets()
        .removeIf(
            s -> {
              try {
                return Paths.get(new URI(s)).endsWith(themePath);
              } catch (Exception e) {
                return false;
              }
            });

    // Read in theme and add
    try {
      scene.getStylesheets().add(Paths.get(themePath).toUri().toURL().toExternalForm());
    } catch (Exception e) {
      e.printStackTrace();
    }

    // Delete temporary file
    themeFile.delete();
  }
}

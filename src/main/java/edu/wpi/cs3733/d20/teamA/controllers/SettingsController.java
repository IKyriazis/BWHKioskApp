package edu.wpi.cs3733.d20.teamA.controllers;

import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXRadioButton;
import edu.wpi.cs3733.d20.teamA.App;
import edu.wpi.cs3733.d20.teamA.graph.*;
import edu.wpi.cs3733.d20.teamA.util.ImageCache;
import java.io.*;
import javafx.css.CssParser;
import javafx.css.Rule;
import javafx.css.Stylesheet;
import javafx.css.converter.ColorConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;

public class SettingsController {
  @FXML private JFXColorPicker primaryPicker;
  @FXML private JFXColorPicker primaryLightPicker;
  @FXML private JFXColorPicker primaryDarkPicker;
  @FXML private JFXColorPicker mapDarkPicker;
  @FXML private JFXColorPicker mapLightPicker;

  @FXML private JFXRadioButton aStarButton;
  @FXML private JFXRadioButton bfsButton;
  @FXML private JFXRadioButton dfsButton;
  @FXML private JFXRadioButton djikstraButton;

  private ToggleGroup toggleGroup;

  private static final String themePath = "theme.css";

  @FXML
  public void initialize() {
    // Set up color pickers to show default color scheme
    File file = new File(themePath);
    primaryPicker.setValue(getColor("-primary-color"));
    primaryLightPicker.setValue(getColor("-primary-color-light"));
    primaryDarkPicker.setValue(getColor("-primary-color-dark"));
    mapDarkPicker.setValue(Color.rgb(198, 177, 150));
    mapLightPicker.setValue(Color.rgb(220, 189, 156));

    // Set radio buttons and toggle group
    toggleGroup = new ToggleGroup();
    aStarButton.setToggleGroup(toggleGroup);
    bfsButton.setToggleGroup(toggleGroup);
    dfsButton.setToggleGroup(toggleGroup);
    djikstraButton.setToggleGroup(toggleGroup);

    aStarButton.setOnAction(
        event -> {
          if (aStarButton.isSelected()) {
            MapSettings.setPath(new Path(Graph.getInstance(Campus.FAULKNER)));
          }
        });

    bfsButton.setOnAction(
        event -> {
          if (bfsButton.isSelected()) {
            MapSettings.setPath(new BreadthFirst(Graph.getInstance(Campus.FAULKNER)));
          }
        });

    dfsButton.setOnAction(
        event -> {
          if (dfsButton.isSelected()) {
            MapSettings.setPath(new DepthFirst(Graph.getInstance(Campus.FAULKNER)));
          }
        });

    djikstraButton.setOnAction(
        event -> {
          if (djikstraButton.isSelected()) {
            MapSettings.setPath(new Djikstras(Graph.getInstance(Campus.FAULKNER)));
          }
        });
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
      blueHex = "0" + blueHex;
    }
    hexColor += blueHex;

    return hexColor;
  }

  @FXML
  private void updateTheme(ActionEvent event) {

    File themeFile = new File(themePath);
    try {
      boolean go = true;
      if (themeFile.exists()) {
        go = themeFile.delete();
      }

      if (go && themeFile.createNewFile()) {
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
                return s.equals("file:" + themePath);
              } catch (Exception e) {
                return false;
              }
            });

    // Read in theme and add
    try {
      scene.getStylesheets().add("file:" + themePath);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void updateMapColor(ActionEvent actionEvent) {
    MapSettings.setLightColor(mapLightPicker.getValue());
    MapSettings.setDarkColor(mapDarkPicker.getValue());

    ImageCache.recolorFloorImages(mapDarkPicker.getValue(), mapLightPicker.getValue());
  }
}

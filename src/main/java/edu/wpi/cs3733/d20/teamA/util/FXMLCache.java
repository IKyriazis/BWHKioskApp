package edu.wpi.cs3733.d20.teamA.util;

import edu.wpi.cs3733.d20.teamA.App;
import java.util.HashMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

public class FXMLCache {
  private static final HashMap<String, Node> map = new HashMap<>();

  // Load FXML files from a cache if possible. Please note that this will not reset controllers in
  // any way shape or form.
  // Do not use it if new controllers are required.
  public static Node loadFXML(String file) {
    if (map.containsKey(file)) {
      return map.get(file);
    }

    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(App.class.getResource(file));
      Node node = loader.load();
      map.put(file, node);
      return node;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static void preLoadFXML(String file) {
    loadFXML(file);
  }
}

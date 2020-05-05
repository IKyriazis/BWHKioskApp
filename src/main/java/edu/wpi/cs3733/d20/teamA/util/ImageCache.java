package edu.wpi.cs3733.d20.teamA.util;

import java.util.HashMap;
import javafx.scene.image.Image;

public class ImageCache {
  private static final HashMap<String, Image> cacheMap = new HashMap<>();

  public static Image loadImage(String path) {
    if (cacheMap.containsKey(path)) {
      return cacheMap.get(path);
    } else {
      Image image = new Image("/edu/wpi/cs3733/d20/teamA/" + path);
      cacheMap.put(path, image);
      return image;
    }
  }
}

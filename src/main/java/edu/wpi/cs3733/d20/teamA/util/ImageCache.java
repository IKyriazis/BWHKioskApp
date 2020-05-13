package edu.wpi.cs3733.d20.teamA.util;

import com.jfoenix.controls.JFXDialog;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.LoadingDialogController;
import edu.wpi.cs3733.d20.teamA.graph.Campus;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class ImageCache {
  private static final ConcurrentHashMap<String, Image> cacheMap = new ConcurrentHashMap<>();
  private static boolean loadedDefaultImages = false;
  private static Color darkColor = Color.BLUE;
  private static Color lightColor = Color.RED;

  public static Image loadImage(String path) {
    if (cacheMap.containsKey(path)) {
      return cacheMap.get(path);
    } else {
      Image image = new Image("/edu/wpi/cs3733/d20/teamA/" + path);
      cacheMap.put(path, image);
      return image;
    }
  }

  private static List<String> getFloorImagePaths() {
    ArrayList<String> imageLocations = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      String imgName = "Faulkner" + (i + 1);
      imageLocations.add("images/" + imgName + ".png");
    }

    for (int i = 0; i < 6; i++) {
      String imgName = "Main" + (i + 1);
      imageLocations.add("images/" + imgName + ".png");
    }

    return imageLocations;
  }

  public static Image getFloorImage(Campus campus, int floor) {
    if (!loadedDefaultImages) {
      // Load images
      getFloorImagePaths().parallelStream().forEach(ImageCache::loadImage);
      loadedDefaultImages = true;

      recolorFloorImages(Color.rgb(198, 177, 150), Color.rgb(220, 189, 156));
    }

    String path = "images/" + (campus == Campus.MAIN ? "Main" : "Faulkner") + floor + ".png";

    return cacheMap.get(path);
  }

  public static synchronized void recolorFloorImages(Color dark, Color light) {
    JFXDialog loadingDialog =
        DialogUtil.complexDialog(
            "Loading",
            "views/dialog/LoadingDialog.fxml",
            false,
            null,
            new LoadingDialogController());
    ThreadPool.runBackgroundTask(
        () -> {
          getFloorImagePaths()
              .parallelStream()
              .forEach(
                  s -> {
                    Image image = loadImage(s);
                    PixelReader pixelReader = image.getPixelReader();
                    WritableImage wImage =
                        new WritableImage((int) image.getWidth(), (int) image.getHeight());
                    PixelWriter pixelWriter = wImage.getPixelWriter();

                    // Determine the color of each pixel in a specified row
                    for (int readY = 0; readY < image.getHeight(); readY++) {
                      for (int readX = 0; readX < image.getWidth(); readX++) {
                        Color color = pixelReader.getColor(readX, readY);
                        if (color.equals(darkColor)) {
                          color = dark;
                        } else if (color.equals(lightColor)) {
                          color = light;
                        }
                        pixelWriter.setColor(readX, readY, color);
                      }
                    }

                    // Replace image
                    cacheMap.put(s, wImage);
                  });

          lightColor = light;
          darkColor = dark;

          Platform.runLater(loadingDialog::close);
        });
  }
}

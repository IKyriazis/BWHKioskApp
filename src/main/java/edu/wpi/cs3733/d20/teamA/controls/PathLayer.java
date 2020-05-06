package edu.wpi.cs3733.d20.teamA.controls;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import edu.wpi.cs3733.d20.teamA.App;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Pair;

public class PathLayer extends MapLayer {
  private final ObservableList<Pair<MapPoint, Node>> points = FXCollections.observableArrayList();

  public PathLayer() {}

  public void importPointsFromCSV() {
    InputStream stream =
        App.class.getResourceAsStream("/edu/wpi/cs3733/d20/teamA/csvfiles/WorldPath.csv");

    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    reader
        .lines()
        .forEach(
            s -> {
              String[] data = s.split(",");

              MapPoint point =
                  new MapPoint(Double.parseDouble(data[1]), Double.parseDouble(data[0]));
              Node icon = new Circle(5, Color.BLACK);

              points.add(new Pair<>(point, icon));
            });

    ArrayList<Line> lines = new ArrayList<>();
    for (int i = 0; i < points.size() - 1; i++) {
      // Start / end nodes
      Circle start = (Circle) points.get(i).getValue();
      Circle end = (Circle) points.get(i + 1).getValue();

      // Circle circle
      Line line = new Line();

      line.startXProperty().bind(start.translateXProperty());
      line.startYProperty().bind(start.translateYProperty());
      line.endXProperty().bind(end.translateXProperty());
      line.endYProperty().bind(end.translateYProperty());
      line.setStroke(Color.BLUE);
      line.setStrokeWidth(5);

      this.getChildren().add(line);
    }
    this.markDirty();
  }

  @Override
  protected void layoutLayer() {
    for (Pair<MapPoint, Node> candidate : points) {
      MapPoint point = candidate.getKey();
      Node icon = candidate.getValue();
      Point2D mapPoint = getMapPoint(point.getLatitude(), point.getLongitude());
      icon.setVisible(true);
      icon.setTranslateX(mapPoint.getX());
      icon.setTranslateY(mapPoint.getY());
    }
  }
}

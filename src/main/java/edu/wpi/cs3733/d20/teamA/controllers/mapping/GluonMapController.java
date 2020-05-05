package edu.wpi.cs3733.d20.teamA.controllers.mapping;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class GluonMapController {
  @FXML private Pane rootPane;

  private MapPoint mapPoint;

  public void initialize() {
    MapView mapView = new MapView();
    // mapView.addLayer(new MapLayer());
    mapView.setCenter(new MapPoint(42.3016445, -71.1281649));
    mapView.setZoom(18);
    rootPane.getChildren().add(mapView);
  }

  private MapLayer positionLayer() {
    /*return Services.get(PositionService.class)
    .map(positionService -> {
      positionService.start();

      ReadOnlyObjectProperty<Position> positionProperty = positionService.positionProperty();
      Position position = positionProperty.get();
      if (position == null) {
        position = new Position(50., 4.);
      }
      mapPoint = new MapPoint(position.getLatitude(), position.getLongitude());

      PoiLayer answer = new PoiLayer();
      answer.addPoint(mapPoint, new Circle(7, Color.RED));

      positionProperty.addListener(e -> {
        Position pos = positionProperty.get();
        LOGGER.log(Level.INFO, "New Position: " + pos.getLatitude() + ", " + pos.getLongitude());
        mapPoint.update(pos.getLatitude(), pos.getLongitude());
      });
      return answer;
    })
    .orElseGet(() -> {
      LOGGER.log(Level.WARNING, "Position Service not available");
      PoiLayer answer = new PoiLayer();
      mapPoint = new MapPoint(50., 4.);
      answer.addPoint(mapPoint, new Circle(7, Color.RED));
      return answer;
    });*/
    return new MapLayer();
  }
}

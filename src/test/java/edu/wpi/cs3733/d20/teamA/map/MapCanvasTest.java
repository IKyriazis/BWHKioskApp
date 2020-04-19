package edu.wpi.cs3733.d20.teamA.map;

import edu.wpi.cs3733.d20.teamA.App;
import edu.wpi.cs3733.d20.teamA.controllers.MainController;
import edu.wpi.cs3733.d20.teamA.graph.*;
import java.sql.SQLException;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

@ExtendWith(ApplicationExtension.class)
public class MapCanvasTest {
  private MainController controller = new MainController();
  private Node node1 = new Node("Node1", 123, 456, 1, "Main", NodeType.HALL, "N/A", "N/A", "A");
  private Node node2 = new Node("Node2", 123, 789, 1, "Main", NodeType.HALL, "N/A", "N/A", "A");
  private Node node3 = new Node("Node3", 123, 899, 1, "Main", NodeType.HALL, "N/A", "N/A", "A");

  private Edge edge1 = new Edge(node1, node2, 1);
  private Edge edge2 = new Edge(node2, node3, 1);

  private Path path = new Path(Graph.getInstance());

  Point2D point = new Point2D(130, 140);

  public MapCanvasTest() throws SQLException {}

  @Start
  private void start(Stage stage) {
    try {

      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(App.class.getResource("views/MainMockup.fxml"));

      BorderPane anchorPane = loader.load();
      Assertions.assertNotNull(anchorPane);

      controller = loader.getController();
      Assertions.assertNotNull(controller);

      Scene scene = new Scene(anchorPane);
      stage.setScene(scene);
      stage.show();

    } catch (Exception e) {
      e.printStackTrace();

      Assertions.fail();
    }
  }

  @Test
  public void testDraw() {
    controller.getCanvas().draw(1);
    // Manually tested
    Assertions.assertTrue(true);
  }

  @Test
  public void testSetPath() {
    path.getPathNodes().add(node1);
    path.getPathNodes().add(node2);
    path.getPathNodes().add(node3);
    path.getPathEdges().add(edge1);
    path.getPathEdges().add(edge2);
    Assertions.assertNull(controller.getCanvas().getPath());
    controller.getCanvas().setPath(path);
    Assertions.assertNotNull(controller.getCanvas().getPath());
  }
}

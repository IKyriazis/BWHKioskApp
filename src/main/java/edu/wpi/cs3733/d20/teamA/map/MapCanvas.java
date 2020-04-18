package edu.wpi.cs3733.d20.teamA.map;

import edu.wpi.cs3733.d20.teamA.graph.Edge;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class MapCanvas extends Canvas {
  private Bounds viewSpace;
  private Image[] floorImages;
  private Canvas canvas;

  public MapCanvas() {
    super();

    // Load floor images
    floorImages = new Image[5];
    for (int i = 0; i < 5; i++) {
      floorImages[i] = new Image("/edu/wpi/cs3733/d20/teamA/images/Floor" + (i + 1) + ".jpg");
    }
  }

  // graphToCanvas() projects a point from graph coordinates onto canvas coordinates
  private Point2D graphToCanvas(Point2D point) {
    double xRatio = getWidth() / viewSpace.getWidth();
    double yRatio = getHeight() / viewSpace.getHeight();

    return new Point2D(
        (point.getX() - viewSpace.getMinX()) * xRatio,
        (point.getY() - viewSpace.getMinY()) * yRatio);
  }

  // canvasToGraph() projects a point from canvas coordinates to graph coordinates
  private Point2D canvasToGraph(Point2D point) {
    // Aspect ratio adjustments
    double xRatio = viewSpace.getWidth() / getWidth();
    double yRatio = viewSpace.getHeight() / getHeight();

    return new Point2D(
        (point.getX() * xRatio) + viewSpace.getMinX(),
        (point.getY() * yRatio) + viewSpace.getMinY());
  }

  public void drawFloorBackground(int floor) {
    // Clamp floor to between 1 and 5
    floor = Math.max(1, Math.min(floor, 5));

    Image img = floorImages[floor - 1];
    viewSpace = new BoundingBox(0, 0, img.getWidth(), img.getHeight());
    Point2D imgStart = graphToCanvas(new Point2D(0, 0));
    Point2D imgEnd = graphToCanvas(new Point2D(img.getWidth(), img.getHeight()));
    getGraphicsContext2D()
        .drawImage(
            img,
            imgStart.getX(),
            imgStart.getY(),
            imgEnd.getX() - imgStart.getX(),
            imgEnd.getY() - imgStart.getY());
  }

  // Draws an edge
  public void drawEdge(Edge edge){
    GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

    // Clear canvas
    graphicsContext.clearRect(0,0, canvas.getWidth(), canvas.getHeight());

    Point2D start = graphToCanvas(new Point2D(edge.getStart().getX(), edge.getStart().getY()));
    Point2D end = graphToCanvas(new Point2D(edge.getEnd().getX(), edge.getEnd().getY()));

    // Set the color to blue for the edge
    graphicsContext.setLineWidth(5);
    graphicsContext.setStroke(Color.BLUE);

    // Draw the line in between the points
    graphicsContext.strokeLine(start.getX(), start.getY(), end.getX(), end.getY());
  }

  // Draws a node on the map
  public void drawNode(Node node){
    GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

    graphicsContext.clearRect(0,0, canvas.getWidth(), canvas.getHeight());

    graphicsContext.setFill(Color.DARKTURQUOISE);

    Point2D nodePoint = graphToCanvas(new Point2D(node.getX(), node.getY()));
    graphicsContext.fillOval(nodePoint.getX() - 4, nodePoint.getY() - 4,8, 8);
  }

}

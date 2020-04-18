package edu.wpi.cs3733.d20.teamA.map;

import edu.wpi.cs3733.d20.teamA.graph.Edge;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.graph.Path;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class MapCanvas extends Canvas {
  private Bounds viewSpace;
  private int lastDrawnFloor = 0;
  private Image[] floorImages;
  private Canvas canvas;

  public MapCanvas() {
    super();

    // Load floor images
    floorImages = new Image[5];
    for (int i = 0; i < 5; i++) {
      floorImages[i] = new Image("/edu/wpi/cs3733/d20/teamA/images/Floor" + (i + 1) + ".jpg");
    }

    viewSpace = new BoundingBox(0, 0, 100, 100);
    setManaged(false);

    widthProperty()
        .addListener(
            (observable, oldValue, newValue) -> resize(newValue.doubleValue(), getHeight()));
    heightProperty()
        .addListener(
            (observable, oldValue, newValue) -> resize(getWidth(), newValue.doubleValue()));
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

  private void fitToFloor(int floor, boolean forceRefit) {
    if ((lastDrawnFloor == floor) && (!forceRefit)) {
      return;
    }

    double imageWidth = floorImages[floor - 1].getWidth();
    double imageHeight = floorImages[floor - 1].getHeight();

    double wScalar = imageWidth / getWidth();
    double hScalar = imageHeight / getHeight();

    if (wScalar > hScalar) {
      double displayedWidth = (imageWidth * hScalar) / wScalar;
      double excess = imageWidth - displayedWidth;
      viewSpace = new BoundingBox(excess / 2, 0, displayedWidth, imageHeight);
    } else {
      double displayedHeight = (imageHeight * wScalar) / hScalar;
      double excess = imageHeight - displayedHeight;
      viewSpace = new BoundingBox(0, excess / 2, imageWidth, displayedHeight);
    }
  }

  public void drawFloorBackground(int floor) {
    fitToFloor(floor, false);

    // Clamp floor to between 1 and 5
    floor = Math.max(1, Math.min(floor, 5));

    Image img = floorImages[floor - 1];
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
  public void drawEdge(Edge edge) {
    GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

    Point2D start = graphToCanvas(new Point2D(edge.getStart().getX(), edge.getStart().getY()));
    Point2D end = graphToCanvas(new Point2D(edge.getEnd().getX(), edge.getEnd().getY()));

    // Set the color to blue for the edge
    graphicsContext.setLineWidth(5);
    graphicsContext.setStroke(Color.BLUE);

    // Draw the line in between the points
    graphicsContext.strokeLine(start.getX(), start.getY(), end.getX(), end.getY());
  }

  // Draws a node on the map
  public void drawNode(Node node) {
    GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

    graphicsContext.setFill(Color.DARKTURQUOISE);

    Point2D nodePoint = graphToCanvas(new Point2D(node.getX(), node.getY()));
    graphicsContext.fillOval(nodePoint.getX() - 4, nodePoint.getY() - 4, 8, 8);
  }

  // Draws the path found
  public void drawPath(Path path) {
    for (Node node : path.getPathNodes()) {
      drawNode(node);
    }

    for (Edge edge : path.getPathEdges()) {
      drawEdge(edge);
    }
  }

  @Override
  public void resize(double width, double height) {
    super.resize(width, height);

    lastDrawnFloor = Math.max(lastDrawnFloor, 1);
    fitToFloor(lastDrawnFloor, true);
    drawFloorBackground(lastDrawnFloor);
  }
}

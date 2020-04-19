package edu.wpi.cs3733.d20.teamA.map;

import edu.wpi.cs3733.d20.teamA.graph.Edge;
import edu.wpi.cs3733.d20.teamA.graph.Graph;
import edu.wpi.cs3733.d20.teamA.graph.Node;
import edu.wpi.cs3733.d20.teamA.graph.Path;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class MapCanvas extends Canvas {
  private final Image[] floorImages;

  private int lastDrawnFloor = 1;
  private boolean drawAllNodes;
  private SimpleDoubleProperty zoom;

  private BoundingBox viewSpace;
  private Point2D center;
  private Point2D dragLast;

  private Path path;

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

    // Drag to change center
    setOnMousePressed(event -> dragLast = new Point2D(event.getX(), event.getY()));
    setOnMouseDragged(
        event -> {
          Point2D startGraphPos = canvasToGraph(dragLast);
          Point2D endGraphPos = canvasToGraph(new Point2D(event.getX(), event.getY()));

          double xDiff = startGraphPos.getX() - endGraphPos.getX();
          double yDiff = startGraphPos.getY() - endGraphPos.getY();

          center = new Point2D(xDiff + center.getX(), yDiff + center.getY());

          draw(lastDrawnFloor);

          dragLast = new Point2D(event.getX(), event.getY());
        });
    setOnScroll(
        event -> {
          double diff = event.getDeltaY() / 10.0;
          zoom.set(Math.min(100, Math.max(0, zoom.getValue() + diff)));
        });

    // Set cursor to indicate panning possibility
    setCursor(Cursor.MOVE);

    // Setup zoom property
    zoom = new SimpleDoubleProperty(0.0);
    zoom.addListener(observable -> draw(lastDrawnFloor));
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

  private void calcViewSpace(int floor) {
    double imageWidth = floorImages[floor - 1].getWidth();
    double imageHeight = floorImages[floor - 1].getHeight();

    // Set center if not yet set
    if ((center == null) || (floor != lastDrawnFloor)) {
      center = new Point2D(0, 0).midpoint(new Point2D(imageWidth, imageHeight));
    }

    double aspectRatio = (imageWidth / getWidth()) / (imageHeight / getHeight());

    double zoomFactor = (1.0 + (zoom.getValue() / 100));
    double width = ((aspectRatio < 1.0) ? imageWidth : (imageWidth / aspectRatio)) / zoomFactor;
    double height = ((aspectRatio > 1.0) ? imageHeight : (imageHeight * aspectRatio)) / zoomFactor;

    double startX = center.getX() - (width / 2);
    double startY = center.getY() - (height / 2);

    // Correct center if view is off screen
    if (startX < 0) {
      center = center.add(-startX, 0);
      startX = 0;
    }

    if (startY < 0) {
      center = center.add(0, -startY);
      startY = 0;
    }

    if ((startX + width) > imageWidth) {
      double xDiff = (startX + width) - imageWidth;
      center = center.subtract(xDiff, 0);
      startX -= xDiff;
    }

    if ((startY + height) > imageHeight) {
      double yDiff = (startY + height) - imageHeight;
      center = center.subtract(0, yDiff);
      startY -= yDiff;
    }

    viewSpace = new BoundingBox(startX, startY, width, height);
  }

  public void draw(int floor) {
    // Clear background
    getGraphicsContext2D().clearRect(0, 0, getWidth(), getHeight());

    // Clamp floor to between 1 and 5
    floor = Math.max(1, Math.min(floor, 5));

    // Update view space
    calcViewSpace(floor);

    // Draw background
    drawFloorBackground(floor);

    // All nodes if flag is set.
    if (drawAllNodes) {
      try {
        Graph.getInstance()
            .getNodes()
            .values()
            .forEach(
                node -> {
                  drawNode(node);

                  node.getEdges().values().forEach(this::drawEdge);
                });
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    // Draw path if it exists
    if (path != null) {
      drawPath(path);
    }

    lastDrawnFloor = floor;
  }

  private void drawFloorBackground(int floor) {
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
  private void drawEdge(Edge edge) {
    GraphicsContext graphicsContext = getGraphicsContext2D();

    Point2D start = graphToCanvas(new Point2D(edge.getStart().getX(), edge.getStart().getY()));
    Point2D end = graphToCanvas(new Point2D(edge.getEnd().getX(), edge.getEnd().getY()));

    // Set the color to blue for the edge
    graphicsContext.setLineWidth(5);
    graphicsContext.setStroke(Color.RED);

    // Draw the line in between the points
    graphicsContext.strokeLine(start.getX(), start.getY(), end.getX(), end.getY());
  }

  // Draws a node on the map
  private void drawNode(Node node) {
    GraphicsContext graphicsContext = getGraphicsContext2D();

    graphicsContext.setFill(Color.BLACK);

    Point2D nodePoint = graphToCanvas(new Point2D(node.getX(), node.getY()));
    graphicsContext.fillOval(nodePoint.getX() - 5, nodePoint.getY() - 5, 10, 10);
  }

  // Draws the path found
  private void drawPath(Path path) {
    for (Node node : path.getPathNodes()) {
      drawNode(node);
    }

    for (Edge edge : path.getPathEdges()) {
      drawEdge(edge);
    }
  }

  public void setPath(Path path) {
    this.path = path;
  }

  public void clearPath(Path path) {
    this.path = null;
  }

  @Override
  public void resize(double width, double height) {
    super.resize(width, height);

    draw(lastDrawnFloor);
  }

  public SimpleDoubleProperty getZoomProperty() {
    return zoom;
  }

  public void setDrawAllNodes(boolean drawAllNodes) {
    this.drawAllNodes = drawAllNodes;
  }
}

package edu.wpi.cs3733.d20.teamA.map;

import edu.wpi.cs3733.d20.teamA.graph.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class MapCanvas extends Canvas {
  private final Image[] floorImages;

  private int lastDrawnFloor = 1;
  private boolean drawAllNodes;
  private SimpleDoubleProperty zoom;

  private BoundingBox viewSpace;
  private Point2D center;
  private Point2D dragLast;

  private boolean dragEnabled;
  private MouseButton dragMapButton = MouseButton.PRIMARY;
  private EventHandler<MouseEvent> dragStartHandler;
  private EventHandler<MouseEvent> dragHandler;
  private EventHandler<MouseEvent> dragEndHandler;

  private ArrayList<Node> highlights;
  private Color highlightColor = Color.DEEPSKYBLUE;
  private Point2D highlightOffset;

  private Point2D selectionStart;
  private Point2D selectionEnd;

  private ContextPath path;

  public MapCanvas(boolean dragEnabled) {
    super();

    this.dragEnabled = dragEnabled;

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

    // Create drag event handlers
    dragStartHandler =
        event -> {
          if (event.getButton() == dragMapButton) {
            dragLast = new Point2D(event.getX(), event.getY());
            setCursor(Cursor.MOVE);
          }
        };
    dragHandler =
        event -> {
          if (event.getButton() == dragMapButton) {
            Point2D startGraphPos = canvasToGraph(dragLast);
            Point2D endGraphPos = canvasToGraph(new Point2D(event.getX(), event.getY()));

            double xDiff = startGraphPos.getX() - endGraphPos.getX();
            double yDiff = startGraphPos.getY() - endGraphPos.getY();

            center = new Point2D(xDiff + center.getX(), yDiff + center.getY());

            draw(lastDrawnFloor);

            dragLast = new Point2D(event.getX(), event.getY());
          }
        };
    dragEndHandler =
        event -> {
          if (event.getButton() == dragMapButton) {
            setCursor(Cursor.DEFAULT);
          }
        };

    // Register drag handlers if enabled
    if (dragEnabled) {
      setOnMousePressed(dragStartHandler);
      setOnMouseDragged(dragHandler);
      setOnMouseReleased(dragEndHandler);
    }

    setOnScroll(
        event -> {
          double diff = event.getDeltaY() / 10.0;
          zoom.set(Math.min(100, Math.max(0, zoom.getValue() + diff)));
        });

    // Setup zoom property
    zoom = new SimpleDoubleProperty(0.0);
    zoom.addListener(observable -> draw(lastDrawnFloor));

    // Create list of nodes to highlight
    highlights = new ArrayList<>();
  }

  // graphToCanvas() projects a point from graph coordinates onto canvas coordinates
  public Point2D graphToCanvas(Point2D point) {
    double xRatio = getWidth() / viewSpace.getWidth();
    double yRatio = getHeight() / viewSpace.getHeight();

    return new Point2D(
        (point.getX() - viewSpace.getMinX()) * xRatio,
        (point.getY() - viewSpace.getMinY()) * yRatio);
  }

  // canvasToGraph() projects a point from canvas coordinates to graph coordinates
  public Point2D canvasToGraph(Point2D point) {
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
        int finalFloor = floor;
        List<Node> floorNodes =
            Graph.getInstance().getNodes().values().stream()
                .filter(node -> node.getFloor() == finalFloor)
                .collect(Collectors.toList());

        // Draw all edges
        floorNodes.forEach(
            node -> {
              node.getEdges()
                  .values()
                  .forEach(
                      edge -> {
                        // Skip edges that cross floors for now
                        if (edge.getEnd().getFloor() == finalFloor) {
                          drawEdge(edge);
                        }
                      });
            });

        // Draw nodes
        floorNodes.forEach(
            node -> {
              if (highlights.contains(node)) {
                drawNode(node, highlightColor);
              } else {
                drawNode(node, Color.BLACK);
              }
            });
      } catch (Exception e) {
        e.printStackTrace();
      }

      // Draw selection box
      if (selectionStart != null && selectionEnd != null) {
        drawSelectionBox(selectionStart, selectionEnd);
      }
    }

    // Draw path if it exists
    if (path != null) {
      drawPath(path, floor);
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
    if (highlightOffset != null && highlights.contains(edge.getStart())) {
      start = start.add(highlightOffset);
    }

    Point2D end = graphToCanvas(new Point2D(edge.getEnd().getX(), edge.getEnd().getY()));
    if (highlightOffset != null && highlights.contains(edge.getEnd())) {
      end = end.add(highlightOffset);
    }

    // Set the color to black for the edge
    graphicsContext.setLineWidth(5);
    graphicsContext.setStroke(Color.BLACK);

    // Draw the line in between the points
    graphicsContext.strokeLine(start.getX(), start.getY(), end.getX(), end.getY());
  }

  // Draws a node on the map
  private void drawNode(Node node, Color color) {
    GraphicsContext graphicsContext = getGraphicsContext2D();

    graphicsContext.setFill(color);

    Point2D nodePoint = graphToCanvas(new Point2D(node.getX(), node.getY()));
    if (highlights.contains(node) && highlightOffset != null) {
      nodePoint = nodePoint.add(highlightOffset);
    }
    graphicsContext.fillOval(nodePoint.getX() - 5, nodePoint.getY() - 5, 10, 10);
  }

  // Draws the path found
  private void drawPath(ContextPath path, int floor) {

    for (Edge edge : path.getPathEdges()) {
      if (edge.getEnd().getFloor() == floor) drawEdge(edge);
    }

    for (Node node : path.getPathNodes()) {

      if (path.getPathNodes().indexOf(node) == 0 && node.getFloor() == floor) {
        drawNode(node, Color.SPRINGGREEN);
      } else if (path.getPathNodes().size() - 1 == path.getPathNodes().lastIndexOf(node)
          && node.getFloor() == floor) {
        drawNode(node, Color.TOMATO);
      } else if (node.getFloor() == floor) {
        drawNode(node, Color.BLACK);
      }
    }
  }

  // Draws a rubber-band selection box
  public void drawSelectionBox(Point2D start, Point2D end) {
    double startX = Math.min(start.getX(), end.getX());
    double startY = Math.min(start.getY(), end.getY());

    double width = Math.max(start.getX(), end.getX()) - startX;
    double height = Math.max(start.getY(), end.getY()) - startY;

    getGraphicsContext2D().setFill(Color.rgb(63, 159, 191, 0.40));
    getGraphicsContext2D().fillRect(startX, startY, width, height);
  }

  public ContextPath getPath() {
    return this.path;
  }

  // Get distance between two points
  private double getDistance(Point2D p0, Point2D p1) {
    double xDiff = p1.getX() - p0.getX();
    double yDiff = p1.getY() - p0.getY();

    return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
  }

  // Get the closest node to a position on the canvas (within a given radius in canvas space)
  public Optional<Node> getClosestNode(int floor, Point2D point, double maxDistance) {
    try {
      Stream<Node> nodeStream = Graph.getInstance().getNodes().values().stream();

      return nodeStream
          .filter(node -> node.getFloor() == floor)
          .filter(
              node -> {
                Point2D nodeCanvasPoint = graphToCanvas(new Point2D(node.getX(), node.getY()));

                return getDistance(point, nodeCanvasPoint) < maxDistance;
              })
          .min(
              Comparator.comparingDouble(
                  o -> getDistance(graphToCanvas(new Point2D(o.getX(), o.getY())), point)));
    } catch (Exception e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }

  public void setPath(ContextPath path) {
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

  public void setDragEnabled(boolean dragEnabled) {
    if (dragEnabled == this.dragEnabled) return;

    if (dragEnabled) {
      setOnMousePressed(dragStartHandler);
      setOnMouseDragged(dragHandler);
      setOnMouseReleased(dragEndHandler);
    } else {
      setOnMousePressed(null);
      setOnMouseDragged(null);
      setOnMouseReleased(null);
    }

    this.dragEnabled = dragEnabled;
  }

  public void setHighlights(ArrayList<Node> highlights) {
    this.highlights = highlights;
  }

  public void setHighlightColor(Color color) {
    this.highlightColor = color;
  }

  public void setDragMapButton(MouseButton dragMapButton) {
    this.dragMapButton = dragMapButton;
  }

  public EventHandler<MouseEvent> getDragStartHandler() {
    return dragStartHandler;
  }

  public EventHandler<MouseEvent> getDragHandler() {
    return dragHandler;
  }

  public EventHandler<MouseEvent> getDragEndHandler() {
    return dragEndHandler;
  }

  public void setSelectionBox(Point2D start, Point2D end) {
    selectionStart = start;
    selectionEnd = end;
  }

  public void setHighlightOffset(Point2D highlightOffset) {
    this.highlightOffset = highlightOffset;
  }
}

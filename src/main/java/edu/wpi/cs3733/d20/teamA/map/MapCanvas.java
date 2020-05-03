package edu.wpi.cs3733.d20.teamA.map;

import edu.wpi.cs3733.d20.teamA.graph.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.animation.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.util.Duration;

public class MapCanvas extends Canvas {
  private final Image[] floorImages;
  private final Image upImage;
  private final Image downImage;

  private int lastDrawnFloor = 1;
  private boolean drawAllNodes = false;
  private boolean drawBelow = false;
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
  Group group = new Group();
  PathTransition transition = new PathTransition();

  public MapCanvas(boolean dragEnabled) {
    super();

    this.dragEnabled = dragEnabled;

    // Load floor images
    floorImages = new Image[5];
    for (int i = 0; i < 5; i++) {
      floorImages[i] = new Image("/edu/wpi/cs3733/d20/teamA/images/Floor" + (i + 1) + ".jpg");
    }

    // Load up/down arrow images
    upImage = new Image("/edu/wpi/cs3733/d20/teamA/images/up.png");
    downImage = new Image("/edu/wpi/cs3733/d20/teamA/images/down.png");

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
            transition.stop();
            group.getChildren().clear();
            dragLast = new Point2D(event.getX(), event.getY());
            setCursor(Cursor.MOVE);
          }
        };
    dragHandler =
        event -> {
          if (event.getButton() == dragMapButton) {
            transition.stop();
            group.getChildren().clear();
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
          transition.stop();
          group.getChildren().clear();
          draw(lastDrawnFloor);
        });

    // Setup zoom property
    zoom = new SimpleDoubleProperty(0.0);
    zoom.addListener(
        observable -> {
          transition.stop();
          group.getChildren().clear();
          draw(lastDrawnFloor);
        });

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

    // Correct center if view is too far away from image
    if (startX < -(imageWidth / 4)) {
      center = center.add((-(imageWidth / 4) - startX), 0);
      startX = -(imageWidth / 4);
    }

    if (startY < (-imageHeight / 4)) {
      center = center.add(0, (-(imageHeight / 4) - startY));
      startY = -(imageHeight / 4);
    }

    if ((startX + width) > (5 * imageWidth / 4)) {
      double xDiff = (startX + width) - (5 * imageWidth / 4);
      center = center.subtract(xDiff, 0);
      startX -= xDiff;
    }

    if ((startY + height) > (5 * imageHeight / 4)) {
      double yDiff = (startY + height) - (5 * imageHeight / 4);
      center = center.subtract(0, yDiff);
      startY -= yDiff;
    }

    viewSpace = new BoundingBox(startX, startY, width, height);
  }

  public void draw(int floor) {
    // Clear background
    getGraphicsContext2D().setFill(Color.web("F4F4F4"));
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
        if (drawBelow) {
          // Get all the nodes on the floor below
          List<Node> belowNodes =
              Graph.getInstance().getNodes().values().stream()
                  .filter(node -> node.getFloor() == finalFloor - 1)
                  .collect(Collectors.toList());
          Color belowColor = Color.rgb(0, 0, 0, 0.25);
          Color belowHighlight =
              Color.rgb(
                  (int) highlightColor.getRed() * 255,
                  (int) highlightColor.getGreen() * 255,
                  (int) highlightColor.getBlue() * 255,
                  0.5);

          // Draw edges
          belowNodes.forEach(
              node ->
                  node.getEdges()
                      .values()
                      .forEach(edge -> drawEdge(edge, belowColor, false, true)));

          belowNodes.forEach(
              node -> {
                if (highlights.contains(node)) {
                  drawNode(node, belowHighlight);
                } else {
                  drawNode(node, belowColor);
                }
              });
        }

        // Get all the nodes on this floor
        List<Node> floorNodes =
            Graph.getInstance().getNodes().values().stream()
                .filter(node -> node.getFloor() == finalFloor)
                .collect(Collectors.toList());

        // Draw all edges
        floorNodes.forEach(
            node -> {
              node.getEdges().values().forEach(edge -> drawEdge(edge, Color.BLACK, true, true));
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
      animatePath(path, floor);
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
  private void drawEdge(Edge edge, Color color, boolean showArrows, boolean drawMultifloorEdge) {
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
    graphicsContext.setLineWidth(4);
    graphicsContext.setStroke(color);

    // Draw the line in between the points
    if ((edge.getStart().getFloor() == edge.getEnd().getFloor()) || drawMultifloorEdge) {

      graphicsContext.beginPath();
      graphicsContext.moveTo(start.getX(), start.getY());
      graphicsContext.lineTo(end.getX(), end.getY());
      graphicsContext.fill();
      graphicsContext.closePath();
      graphicsContext.stroke();
      // graphicsContext.strokeLine(start.getX(), start.getY(), end.getX(), end.getY());
    }

    if (showArrows) {
      Point2D pos = (drawMultifloorEdge) ? end : start;
      // Draw up / down arrow if floor changed
      if (edge.getEnd().getFloor() < edge.getStart().getFloor()) {
        graphicsContext.drawImage(downImage, pos.getX() - 16, pos.getY() - 16, 32, 32);
      } else if (edge.getEnd().getFloor() > edge.getStart().getFloor()) {
        graphicsContext.drawImage(upImage, pos.getX() - 16, pos.getY() - 16, 32, 32);
      }
    }
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
      if (edge.getStart().getFloor() == floor) drawEdge(edge, Color.BLACK, true, false);
    }

    for (Node node : path.getPathNodes()) {

      if (path.getPathNodes().indexOf(node) == 0 && node.getFloor() == floor) {
        drawNode(node, Color.SPRINGGREEN);
      } else if (path.getPathNodes().size() - 1 == path.getPathNodes().lastIndexOf(node)
          && node.getFloor() == floor) {
        drawNode(node, Color.TOMATO);
      } else if (node.getFloor() == floor) {
        // drawNode(node, Color.BLACK);
      }
    }

    animatePath(path, floor);
  }

  public Group animatePath(ContextPath path, int floor) {

    Circle circ = new Circle(10);
    circ.setFill(Color.AQUA);
    double xcoord = path.getPathNodes().get(0).getX();
    double ycoord = path.getPathNodes().get(0).getY();
    double length = 0;
    group.getChildren().add(circ);

    javafx.scene.shape.Path animatedPath = new javafx.scene.shape.Path();
    boolean firstTime = true;

    for (Node node : path.getPathNodes()) {
      Point2D point = graphToCanvas(new Point2D(xcoord, ycoord));
      double canvasPointX = point.getX();
      double canvasPointY = point.getY();
      if (firstTime && node.getFloor() == floor) {
        xcoord = node.getX();
        ycoord = node.getY();
        point = graphToCanvas(new Point2D(xcoord, ycoord));
        canvasPointX = point.getX();
        canvasPointY = point.getY();
        animatedPath.getElements().add(new MoveTo(canvasPointX, canvasPointY));
        firstTime = false;
      }
      // if the node is on the same floor then animate the path on the floor
      if (node.getFloor() == floor) {
        animatedPath.getElements().add(new LineTo(canvasPointX, canvasPointY));
        length += getDistance(xcoord, node.getX(), ycoord, node.getY());
        xcoord = node.getX();
        ycoord = node.getY();
      }
    }

    transition.setDuration(Duration.seconds(length / 250.0));
    transition.setPath(animatedPath);
    transition.setNode(circ);
    transition.setCycleCount(Timeline.INDEFINITE);
    transition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
    transition.play();
    return group;
  }

  public Group getGroup() {
    return group;
  }

  public void setGroup(Group group) {
    this.group = group;
  }

  public PathTransition getTransition() {
    return transition;
  }

  public void setTransition(PathTransition transition) {
    this.transition = transition;
  }

  public double getDistance(double x, double newX, double y, double newY) {
    return Math.sqrt((Math.pow((newX - x), 2)) + (Math.pow((newY - y), 2)));
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

  public void setDrawBelow(boolean drawBelow) {
    this.drawBelow = drawBelow;
  }
}

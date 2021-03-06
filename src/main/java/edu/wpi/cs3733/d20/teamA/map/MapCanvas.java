package edu.wpi.cs3733.d20.teamA.map;

import edu.wpi.cs3733.d20.teamA.graph.*;
import edu.wpi.cs3733.d20.teamA.util.ImageCache;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.animation.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

public class MapCanvas extends Canvas {
  private Graph graph;

  private final int maxFloor;

  private int lastDrawnFloor = 1;
  private boolean drawAllNodes = false;
  private boolean drawBelow = false;
  private SimpleDoubleProperty zoom;
  private final double zoomScalar = 2.0;

  private BoundingBox viewSpace;
  private Point2D center;
  private Point2D dragLast;

  private boolean dragEnabled;
  private MouseButton dragMapButton = MouseButton.PRIMARY;
  private final EventHandler<MouseEvent> dragStartHandler;
  private final EventHandler<MouseEvent> dragHandler;
  private final EventHandler<MouseEvent> dragEndHandler;
  private ArrayList<Node> highlights;
  private Color highlightColor = Color.rgb(255, 112, 67);
  private Point2D highlightOffset;

  private Point2D selectionStart;
  private Point2D selectionEnd;

  private ArrayList<Node> pathNodes;
  private ArrayList<Edge> pathEdges;
  private ImageView pathArrow;
  private PathTransition transition = new PathTransition();
  private boolean animatingPath = false;

  private boolean doingJankyMathPleaseDontRedraw = false;

  public MapCanvas(boolean dragEnabled, Campus campus) {
    super();
    graph = Graph.getInstance(campus);
    maxFloor = campus == Campus.FAULKNER ? 5 : 6;

    this.dragEnabled = dragEnabled;

    // Load path arrow
    pathArrow = new ImageView(ImageCache.loadImage("images/blue_right.png"));
    pathArrow.setFitWidth(32.0);
    pathArrow.setFitHeight(32.0);

    viewSpace = new BoundingBox(0, 0, 100, 100);
    setManaged(false);
    widthProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              resize(newValue.doubleValue(), getHeight());
            });
    heightProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              resize(getWidth(), newValue.doubleValue());
            });

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
          draw(lastDrawnFloor);
        });

    // Setup zoom property
    zoom = new SimpleDoubleProperty(0.0);
    zoom.addListener(
        observable -> {
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

  private void calcViewSpace(int floor, double zoomVal) {
    double imageWidth = ImageCache.getFloorImage(graph.getCampus(), floor).getWidth();
    double imageHeight = ImageCache.getFloorImage(graph.getCampus(), floor).getHeight();

    // Set center if not yet set
    if ((center == null)) {
      center = new Point2D(0, 0).midpoint(new Point2D(imageWidth, imageHeight));
    }

    double aspectRatio = (imageWidth / getWidth()) / (imageHeight / getHeight());

    double zoomFactor = (1.0 + ((zoomVal / 100) * zoomScalar));
    double width = ((aspectRatio < 1.0) ? imageWidth : (imageWidth / aspectRatio)) / zoomFactor;
    double height = ((aspectRatio > 1.0) ? imageHeight : (imageHeight * aspectRatio)) / zoomFactor;

    double startX = center.getX() - (width / 2);
    double startY = center.getY() - (height / 2);

    // Correct center if view is too far away from image
    if (startX < 0) {
      center = center.add(-startX, 0);
      startX = 0;
    }

    if (startY < 0) {
      center = center.add(0, (-startY));
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
    if (doingJankyMathPleaseDontRedraw) {
      return;
    }

    // Clear background
    getGraphicsContext2D().setFill(Color.web("F4F4F4"));
    getGraphicsContext2D().clearRect(0, 0, getWidth(), getHeight());

    // Clamp floor to between 1 and [maxFloor]
    floor = Math.max(1, Math.min(floor, maxFloor));

    // Update view space
    calcViewSpace(floor, zoom.getValue());

    // Draw background
    drawFloorBackground(floor);

    // All nodes if flag is set.
    if (drawAllNodes) {
      try {
        int finalFloor = floor;
        if (drawBelow) {
          // Get all the nodes on the floor below
          List<Node> belowNodes =
              graph.getNodes().values().stream()
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
              node -> node.getEdges().values().forEach(edge -> drawEdge(edge, belowColor, true)));

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
            graph.getNodes().values().stream()
                .filter(node -> node.getFloor() == finalFloor)
                .collect(Collectors.toList());

        // Draw all edges
        floorNodes.forEach(
            node ->
                node.getEdges()
                    .values()
                    .forEach(edge -> drawEdge(edge, Color.rgb(92, 107, 192), true)));

        // Draw nodes 5C 6B C0, 42 A5 F5
        floorNodes.forEach(
            node -> {
              if (highlights.contains(node)) {
                drawNode(node, highlightColor);
              } else {
                drawNode(node, Color.rgb(66, 165, 245));
              }
            });
      } catch (Exception e) {
        e.printStackTrace();
      }

      // Draw selection box
      if (selectionStart != null && selectionEnd != null) {
        drawSelectionBox(selectionStart, selectionEnd);
      }
    } else if (highlights != null) {
      highlights.forEach(
          node -> {
            if (node != null && node.getCampus() == graph.getCampus()) {
              drawNode(node, highlightColor);
            }
          });
    }

    // Draw path if it exists
    if (pathNodes != null) {
      drawPath(floor);
      animatePath(floor);
    }

    lastDrawnFloor = floor;
  }

  private void drawFloorBackground(int floor) {
    Image image = ImageCache.getFloorImage(graph.getCampus(), floor);

    Point2D imgStart = graphToCanvas(new Point2D(0, 0));
    Point2D imgEnd = graphToCanvas(new Point2D(image.getWidth(), image.getHeight()));

    getGraphicsContext2D()
        .drawImage(
            image,
            imgStart.getX(),
            imgStart.getY(),
            imgEnd.getX() - imgStart.getX(),
            imgEnd.getY() - imgStart.getY());
  }

  // Draws an edge
  private void drawEdge(Edge edge, Color color, boolean drawMultifloorEdge) {
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
  private void drawPath(int floor) {

    for (Edge edge : pathEdges) {
      if ((edge.getStart().getFloor() == floor)
          && (edge.getStart().getFloor() == edge.getEnd().getFloor())) {
        drawEdge(edge, Color.rgb(92, 107, 192), false);
      } else if ((edge.getStart().getFloor() == floor)
          && (edge.getStart().getFloor() != edge.getEnd().getFloor())) {

      }
    }

    for (Node node : pathNodes) {

      if (pathNodes.indexOf(node) == 0 && node.getFloor() == floor) {
        drawNode(node, Color.SPRINGGREEN);
      } else if (pathNodes.size() - 1 == pathNodes.lastIndexOf(node) && node.getFloor() == floor) {
        drawNode(node, Color.TOMATO);
      } else if ((node.getType() == NodeType.STAI) || (node.getType() == NodeType.ELEV)) {

      }
    }
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

  // Get distance between two points
  private double getDistance(Point2D p0, Point2D p1) {
    double xDiff = p1.getX() - p0.getX();
    double yDiff = p1.getY() - p0.getY();

    return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
  }

  // Get the closest node to a position on the canvas (within a given radius in canvas space)
  public Optional<Node> getClosestNode(int floor, Point2D point, double maxDistance) {
    try {
      Stream<Node> nodeStream = graph.getNodes().values().stream();

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
    this.pathNodes = new ArrayList<>();
    this.pathEdges = new ArrayList<>();

    this.pathNodes.addAll(path.getPathNodes());
    this.pathEdges.addAll(path.getPathEdges());
  }

  public void animatePath(int floor) {
    if (pathNodes == null || pathEdges == null || !animatingPath) {
      return;
    }

    pathArrow.setVisible(true);

    Path path = new Path();
    pathNodes.stream()
        .filter(node -> node.getFloor() == floor)
        .forEach(
            node -> {
              Point2D dest = graphToCanvas(new Point2D(node.getX(), node.getY()));
              if (path.getElements().isEmpty()) {
                path.getElements().add(new MoveTo(dest.getX(), dest.getY()));
              } else {
                path.getElements().add(new LineTo(dest.getX(), dest.getY()));
              }
            });

    // Ignore empty paths
    if (path.getElements().size() <= 1) {
      pathArrow.setVisible(false);
      transition.stop();
      return;
    }

    double length =
        pathEdges.stream()
            .filter(edge -> edge.getEnd().getFloor() == floor)
            .mapToDouble(Edge::getWeight)
            .sum();

    Pane parent = (Pane) getParent();
    if (!parent.getChildren().contains(pathArrow)) {
      parent.getChildren().add(parent.getChildren().indexOf(this) + 1, pathArrow);
    }

    transition.stop();
    transition.setDuration(Duration.seconds(length / 250.0));
    transition.setPath(path);
    transition.setNode(pathArrow);
    transition.setCycleCount(Timeline.INDEFINITE);
    transition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);

    transition.play();
  }

  public void disablePathAnimation() {
    transition.stop();
    pathArrow.setVisible(false);
    animatingPath = false;
  }

  public void enablePathAnimation() {
    animatingPath = true;
  }

  public void clearPath() {
    this.pathNodes = null;
    this.pathEdges = null;
    disablePathAnimation();
  }

  public void centerPathOnFloor(int floor) {
    List<Node> floorNodes =
        pathNodes.stream().filter(node -> (node.getFloor() == floor)).collect(Collectors.toList());

    int centerX =
        (int)
            floorNodes.stream()
                .mapToInt(Node::getX)
                .average()
                .orElse(ImageCache.getFloorImage(graph.getCampus(), floor).getWidth() / 2);
    int centerY =
        (int)
            floorNodes.stream()
                .mapToInt(Node::getY)
                .average()
                .orElse(ImageCache.getFloorImage(graph.getCampus(), floor).getHeight() / 2);
    Point2D idealCenter = new Point2D(centerX, centerY);

    // Find closest fitting zoom level
    doingJankyMathPleaseDontRedraw = true;
    for (int newZoom = 100; newZoom > 0; newZoom -= 10) {
      // Calculate new view space
      center = idealCenter;
      calcViewSpace(floor, newZoom);

      // Calculate bounding box that takes directions into account
      Point2D viewStart = new Point2D(viewSpace.getMinX(), viewSpace.getMaxX());

      Point2D dirBoxStart = canvasToGraph(new Point2D(0, 0));
      Point2D dirBoxEnd = canvasToGraph(new Point2D(300, 0));

      int excess = (int) (dirBoxEnd.getX() - dirBoxStart.getX());
      BoundingBox newBox =
          new BoundingBox(
              viewSpace.getMinX() + excess,
              viewSpace.getMinY(),
              viewSpace.getWidth() - excess,
              viewSpace.getHeight());

      // Check if all nodes are visible
      boolean oob = false;
      for (Node node : floorNodes) {
        if (!newBox.contains(new Point2D(node.getX(), node.getY()))) {
          oob = true;
          break;
        }
      }
      if (!oob) {
        zoom.setValue(newZoom);
        doingJankyMathPleaseDontRedraw = false;
        return;
      }
    }
    doingJankyMathPleaseDontRedraw = false;
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

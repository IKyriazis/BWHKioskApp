package edu.wpi.cs3733.d20.teamA.graph;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Label;
import javafx.util.Pair;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class PathSegment {
  private Campus campus;
  private ArrayList<Label> directions;
  private int floor;

  private PathSegment(Campus campus, int floor) {
    this.campus = campus;
    this.floor = floor;
    this.directions = new ArrayList<>();
  }

  private void addDirections(Label label) {
    directions.add(label);
  }

  public ArrayList<Label> getDirections() {
    return directions;
  }

  public int getFloor() {
    return floor;
  }

  public Campus getCampus() {
    return campus;
  }

  public void removeLast() {
    directions.remove(directions.size() - 1);
  }

  public static ArrayList<PathSegment> calcPathSegments(ArrayList<Pair<Node, Label>> pairs) {
    ArrayList<PathSegment> segments = new ArrayList<>();
    if (pairs.isEmpty()) {
      return segments;
    }

    PathSegment currSegment =
        new PathSegment(pairs.get(0).getKey().getCampus(), pairs.get(0).getKey().getFloor());
    currSegment.addDirections(pairs.get(0).getValue());
    Node lastNode = pairs.get(0).getKey();
    for (int i = 1; i < pairs.size(); i++) {
      Node node = pairs.get(i).getKey();

      if ((node.getCampus() != lastNode.getCampus()) || (node.getFloor() != lastNode.getFloor())) {
        segments.add(currSegment);
        currSegment = new PathSegment(node.getCampus(), node.getFloor());
      }

      currSegment.addDirections(pairs.get(i).getValue());
      lastNode = node;
    }
    segments.add(currSegment);

    // Merge down segments with only one label
    ArrayList<PathSegment> merged = new ArrayList<>();
    for (int i = 0; i < segments.size() - 1; i++) {
      if (segments.get(i + 1).getDirections().size() == 1) {
        PathSegment seg = segments.get(i);
        seg.addDirections(segments.get(i + 1).getDirections().get(0));
        i++;
        merged.add(seg);
      } else {
        merged.add(segments.get(i));
      }
    }
    merged.add(segments.get(segments.size() - 1));

    return merged;
  }

  public static PathSegment calcInterSegment(Campus dest) {
    PathSegment seg = new PathSegment(Campus.INTER, 0);
    if (dest == Campus.MAIN) {
      Label l0 = new Label("Turn right onto Whitcomb Ave");
      l0.setGraphic(new FontIcon(FontAwesomeSolid.ARROW_CIRCLE_RIGHT));

      Label l1 = new Label("Continue straight until Centre St");
      l1.setGraphic(new FontIcon(FontAwesomeSolid.ARROW_CIRCLE_UP));

      Label l2 = new Label("Turn left onto Centre St");
      l2.setGraphic(new FontIcon(FontAwesomeSolid.ARROW_CIRCLE_LEFT));

      Label l3 = new Label("Continue straight until Arborway");
      l3.setGraphic(new FontIcon(FontAwesomeSolid.ARROW_CIRCLE_UP));

      Label l4 = new Label("Follow Arborway onto Pond St");
      l4.setGraphic(new FontIcon(FontAwesomeSolid.ARROW_CIRCLE_UP));

      Label l5 = new Label("Follow Pond St onto Jamaicaway");
      l5.setGraphic(new FontIcon(FontAwesomeSolid.ARROW_CIRCLE_UP));

      Label l6 = new Label("Follow Jamaicaway onto Riverway");
      l6.setGraphic(new FontIcon(FontAwesomeSolid.ARROW_CIRCLE_UP));

      Label l7 = new Label("Turn right onto Vining St");
      l7.setGraphic(new FontIcon(FontAwesomeSolid.ARROW_CIRCLE_RIGHT));

      Label l8 = new Label("Continue to Francis St");
      l8.setGraphic(new FontIcon(FontAwesomeSolid.ARROW_CIRCLE_UP));

      Label l9 = new Label("Your destination is ahead");
      l9.setGraphic(new FontIcon(FontAwesomeSolid.DOT_CIRCLE));

      seg.directions.addAll(List.of(l0, l1, l2, l3, l4, l5, l6, l7, l8, l9));
    } else if (dest == Campus.FAULKNER) {
      Label l0 = new Label("Turn right onto Francis St");
      l0.setGraphic(new FontIcon(FontAwesomeSolid.ARROW_CIRCLE_RIGHT));

      Label l1 = new Label("Turn left onto Brookline Ave");
      l1.setGraphic(new FontIcon(FontAwesomeSolid.ARROW_CIRCLE_LEFT));

      Label l2 = new Label("Turn left onto Riverway");
      l2.setGraphic(new FontIcon(FontAwesomeSolid.ARROW_CIRCLE_LEFT));

      Label l3 = new Label("Follow Riverway onto Jamaicaway");
      l3.setGraphic(new FontIcon(FontAwesomeSolid.ARROW_CIRCLE_UP));

      Label l4 = new Label("Follow Jamaicaway onto Pond St");
      l4.setGraphic(new FontIcon(FontAwesomeSolid.ARROW_CIRCLE_UP));

      Label l5 = new Label("Follow Pond St onto Arborway");
      l5.setGraphic(new FontIcon(FontAwesomeSolid.ARROW_CIRCLE_UP));

      Label l6 = new Label("Follow Arborway onto Pond St");
      l6.setGraphic(new FontIcon(FontAwesomeSolid.ARROW_CIRCLE_UP));

      Label l7 = new Label("Turn right onto Whitcomb Ave");
      l7.setGraphic(new FontIcon(FontAwesomeSolid.ARROW_CIRCLE_RIGHT));

      Label l8 = new Label("Your destination is on the left");
      l8.setGraphic(new FontIcon(FontAwesomeSolid.DOT_CIRCLE));

      seg.directions.addAll(List.of(l0, l1, l2, l3, l4, l5, l6, l7, l8));
    }

    return seg;
  }

  public String getFloorString(int floor) {
    if (campus.equals(Campus.FAULKNER)) {
      return Integer.toString(floor);
    } else {
      switch (floor) {
        case 1:
          return "L2";
        case 2:
          return "L1";
        case 3:
          return "G";
        case 4:
          return "1";
        case 5:
          return "2";
        case 6:
          return "3";
        default:
          return "";
      }
    }
  }

  @Override
  public String toString() {
    if (getCampus().equals(Campus.INTER)) {
      return getCampus().toString();
    }
    return getCampus().toString() + " - " + getFloorString(getFloor());
  }
}

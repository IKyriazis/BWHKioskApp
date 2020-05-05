package edu.wpi.cs3733.d20.teamA.graph;

import java.util.ArrayList;
import javafx.scene.control.Label;
import javafx.util.Pair;

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

  public static ArrayList<PathSegment> calcPathSegments(ArrayList<Pair<Node, Label>> pairs) {
    ArrayList<PathSegment> segments = new ArrayList<>();
    if (pairs.isEmpty()) {
      return segments;
    }

    PathSegment currSegment =
        new PathSegment(getNodeCampus(pairs.get(0).getKey()), pairs.get(0).getKey().getFloor());
    currSegment.addDirections(pairs.get(0).getValue());
    Node lastNode = pairs.get(0).getKey();
    for (int i = 1; i < pairs.size(); i++) {
      Node node = pairs.get(i).getKey();

      if ((getNodeCampus(node) != getNodeCampus(lastNode))
          || (node.getFloor() != lastNode.getFloor())) {
        segments.add(currSegment);
        currSegment = new PathSegment(getNodeCampus(node), node.getFloor());
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

  public static PathSegment calcInterSegment() {
    return new PathSegment(Campus.INTER, 0);
  }

  private static Campus getNodeCampus(Node node) {
    return node.getBuilding().equals("Faulkner") ? Campus.FAULKNER : Campus.MAIN;
  }
}

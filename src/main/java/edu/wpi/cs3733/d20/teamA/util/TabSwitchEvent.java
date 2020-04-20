package edu.wpi.cs3733.d20.teamA.util;

import javafx.event.Event;
import javafx.event.EventType;

public class TabSwitchEvent extends Event {
  public static final EventType<TabSwitchEvent> TAB_SWITCH =
      new EventType<>(Event.ANY, "TAB_SWITCH");

  public TabSwitchEvent() {
    super(TAB_SWITCH);
  }

  private TabSwitchEvent(EventType<? extends Event> eventType) {
    super(eventType);
  }
}

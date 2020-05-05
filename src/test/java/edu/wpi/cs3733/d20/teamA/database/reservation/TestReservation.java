package edu.wpi.cs3733.d20.teamA.database.reservation;

import java.util.Calendar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestReservation {

  public TestReservation() {}

  @Test
  public void testGetters() {
    Calendar calendar1 = Calendar.getInstance();
    Calendar calendar2 = Calendar.getInstance();
    Reservation reservation = new Reservation("John", calendar1, calendar2, "Bed1");
    Assertions.assertEquals("John", reservation.getRequestedBy());
    Assertions.assertNotNull(reservation.getStartTime());
    Assertions.assertNotNull(reservation.getEndTime());
    Assertions.assertEquals("Bed1", reservation.getAreaReserved());
  }
}

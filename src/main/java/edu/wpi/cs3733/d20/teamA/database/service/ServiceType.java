package edu.wpi.cs3733.d20.teamA.database.service;

public enum ServiceType {
  LAUNDRY("laundry"),
  INTERPRETER_REQ("interpret"),
  JANITOR("janitor"),
  MEDICINE("medicine"),
  EQUIPMENT("equipreq"),
  IT_TICKET("ittix"),
  INTERNAL_TRANSPORT("intrntrans"),
  PRESCRIPTION("rxreq"),
  GIFT("gift");

  private String name;

  ServiceType(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }

  public static ServiceType getServiceType(String str) {
    for (ServiceType type : values()) {
      if (type.name.equals(str) || type.toString().equals(str)) {
        return type;
      }
    }
    return JANITOR;
  }
}

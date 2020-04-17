package edu.wpi.cs3733.d20.teamA.graph;

/** Represents the location type of a node */
public enum NodeType {
  /** Hallway */
  HALL,
  /** Elevator */
  ELEV,
  /** Restroom */
  REST,
  /** Staircase */
  STAI,
  /** Medical departments, clinics, and waiting room areas */
  DEPT,
  /** Labs, imaging centers, and medical testing areas */
  LABS,
  /** Information desks, security desks, lost and found */
  INFO,
  /** Conference room */
  CONF,
  /** Exit / Entrance */
  EXIT,
  /** Shops, food, pay phone, areas that provide non-medical services for immediate payment */
  RETL,
  /**
   * Hospital non-medical services, interpreters, shuttles, spiritual, library, patient financial,
   * etc.
   */
  SERV
}

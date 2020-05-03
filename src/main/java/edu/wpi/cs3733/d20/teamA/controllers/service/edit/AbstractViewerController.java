package edu.wpi.cs3733.d20.teamA.controllers.service.edit;

import edu.wpi.cs3733.d20.teamA.controllers.service.request.AbstractRequestController;
import edu.wpi.cs3733.d20.teamA.database.service.ServiceRequest;

public abstract class AbstractViewerController extends AbstractRequestController {
  protected ServiceRequest req;

  public AbstractViewerController(ServiceRequest req) {
    this.req = req;
  }

  public void reset(ServiceRequest req) {
    this.req = req;
  };

  public abstract void initialize();
}

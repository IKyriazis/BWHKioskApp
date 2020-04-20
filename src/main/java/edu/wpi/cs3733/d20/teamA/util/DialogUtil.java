package edu.wpi.cs3733.d20.teamA.util;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.events.JFXDialogEvent;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733.d20.teamA.App;
import edu.wpi.cs3733.d20.teamA.controllers.DialogController;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class DialogUtil {
  private static JFXButton createCloseButton() {
    JFXButton closeButton = new JFXButton("Close");
    closeButton.setButtonType(JFXButton.ButtonType.RAISED);
    closeButton.setStyle("-fx-background-color: #78909C");
    closeButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CLOSE));

    return closeButton;
  }

  public static void simpleDialog(StackPane dialogPane, String heading, String body) {
    try {
      JFXDialogLayout layout = new JFXDialogLayout();
      layout.setHeading(new Text(heading));
      layout.setBody(new Text(body));

      JFXDialog dialog = new JFXDialog(dialogPane, layout, JFXDialog.DialogTransition.TOP);

      JFXButton closeButton = createCloseButton();
      closeButton.setOnAction(
          event -> {
            dialog.close();
          });

      layout.setActions(closeButton);
      dialog.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void complexDialog(
      StackPane dialogPane,
      String heading,
      String path,
      boolean includeCloseButton,
      EventHandler<? super JFXDialogEvent> closeHandler,
      DialogController controller) {
    FXMLLoader loader = new FXMLLoader();

    try {
      loader.setLocation(App.class.getResource(path));

      JFXDialogLayout layout = new JFXDialogLayout();
      layout.setHeading(new Text(heading));

      JFXDialog dialog = new JFXDialog(dialogPane, layout, JFXDialog.DialogTransition.BOTTOM);
      dialog.setOnDialogClosed(closeHandler);
      if (controller != null) {
        controller.setDialog(dialog);
        loader.setController(controller);
      }
      javafx.scene.Node rootPane = loader.load();
      layout.setBody(rootPane);

      if (includeCloseButton) {
        JFXButton closeButton = createCloseButton();
        closeButton.setOnAction(
            event -> {
              dialog.close();
            });

        layout.setActions(closeButton);
      }

      dialog.show();
    } catch (Exception e) {
      simpleDialog(dialogPane, "Error", "Unable to load complex dialog for: " + path);
    }
  }
}

package edu.wpi.cs3733.d20.teamA.util;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.events.JFXDialogEvent;
import edu.wpi.cs3733.d20.teamA.App;
import edu.wpi.cs3733.d20.teamA.controllers.dialog.IDialogController;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class DialogUtil {
  private static StackPane defaultStackPane;

  private static JFXButton createCloseButton() {
    JFXButton closeButton = new JFXButton("Close");
    closeButton.setButtonType(JFXButton.ButtonType.RAISED);
    closeButton.setStyle("-fx-background-color: -primary-color");
    closeButton.setGraphic(new FontIcon(FontAwesomeSolid.TIMES));

    return closeButton;
  }

  private static void simpleDialog(
      StackPane dialogPane, String heading, String body, FontIcon headingIcon) {
    try {
      Label headingLabel = new Label(heading);
      headingLabel.setGraphic(headingIcon);

      JFXDialogLayout layout = new JFXDialogLayout();
      layout.setHeading(headingLabel);
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

  public static void simpleInfoDialog(StackPane dialogPane, String heading, String body) {
    simpleDialog(dialogPane, heading, body, new FontIcon(FontAwesomeSolid.INFO));
  }

  public static void simpleErrorDialog(StackPane dialogPane, String heading, String body) {
    simpleDialog(dialogPane, heading, body, new FontIcon(FontAwesomeSolid.EXCLAMATION_TRIANGLE));
  }

  public static void simpleInfoDialog(String heading, String body) {
    if (defaultStackPane == null) {
      return;
    }

    simpleInfoDialog(defaultStackPane, heading, body);
  }

  public static void simpleErrorDialog(String heading, String body) {
    if (defaultStackPane == null) {
      return;
    }

    simpleErrorDialog(defaultStackPane, heading, body);
  }

  public static void complexDialog(
      StackPane dialogPane,
      String heading,
      String path,
      boolean includeCloseButton,
      EventHandler<? super JFXDialogEvent> closeHandler,
      IDialogController controller) {
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
      e.printStackTrace();
      simpleErrorDialog(dialogPane, "Error", "Unable to load complex dialog for: " + path);
    }
  }

  public static void setDefaultStackPane(StackPane defaultStackPane) {
    DialogUtil.defaultStackPane = defaultStackPane;
  }
}

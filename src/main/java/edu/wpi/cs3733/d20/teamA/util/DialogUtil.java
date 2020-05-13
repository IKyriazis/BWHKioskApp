package edu.wpi.cs3733.d20.teamA.util;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.events.JFXDialogEvent;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
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
  private static final String ACCOUNT_SID = "AC809fda5395cc3a459bc3555e6477ba72";
  private static final String AUTH_TOKEN = "215f4ff302e84eccd3e7838c51d86506";

  private static JFXButton createCloseButton() {
    JFXButton closeButton = new JFXButton("Close");
    closeButton.setButtonType(JFXButton.ButtonType.RAISED);
    closeButton.setStyle("-fx-background-color: -primary-color");
    closeButton.setGraphic(new FontIcon(FontAwesomeSolid.TIMES));

    return closeButton;
  }

  private static JFXButton createSendButton() {
    JFXButton sendButton = new JFXButton("Send");
    sendButton.setButtonType(JFXButton.ButtonType.RAISED);
    sendButton.setStyle("-fx-background-color: -primary-color");
    sendButton.setGraphic(new FontIcon(FontAwesomeSolid.MAIL_BULK));

    return sendButton;
  }

  private static JFXDialog simpleDialog(
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
      return dialog;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static JFXDialog simpleInfoDialog(StackPane dialogPane, String heading, String body) {
    return simpleDialog(dialogPane, heading, body, new FontIcon(FontAwesomeSolid.INFO));
  }

  public static JFXDialog simpleErrorDialog(StackPane dialogPane, String heading, String body) {
    return simpleDialog(
        dialogPane, heading, body, new FontIcon(FontAwesomeSolid.EXCLAMATION_TRIANGLE));
  }

  public static JFXDialog simpleInfoDialog(String heading, String body) {
    if (defaultStackPane == null) {
      return null;
    }

    return simpleInfoDialog(defaultStackPane, heading, body);
  }

  public static JFXDialog simpleErrorDialog(String heading, String body) {
    if (defaultStackPane == null) {
      return null;
    }

    return simpleErrorDialog(defaultStackPane, heading, body);
  }

  public static JFXDialog complexDialog(
      String heading,
      String path,
      boolean includeCloseButton,
      EventHandler<? super JFXDialogEvent> closeHandler,
      IDialogController controller) {
    return complexDialog(
        defaultStackPane, heading, path, includeCloseButton, closeHandler, controller);
  }

  public static JFXDialog complexDialog(
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
      return dialog;
    } catch (Exception e) {
      e.printStackTrace();
      return simpleErrorDialog(dialogPane, "Error", "Unable to load complex dialog for: " + path);
    }
  }

  public static JFXDialog textingDialog(String orderNum) {
    if (defaultStackPane == null) {
      return null;
    }
    try {
      String heading = "(Optional) Send To Your Phone!";
      String body =
          "Your Request Number is: "
              + orderNum
              + "\nPlease enter your phone number if you would like the request number to be sent to your phone.";
      Label headingLabel = new Label(heading);
      headingLabel.setGraphic(new FontIcon(FontAwesomeSolid.PHONE));

      JFXDialogLayout layout = new JFXDialogLayout();
      layout.setHeading(headingLabel);
      layout.setBody(new Text(body));

      JFXDialog dialog = new JFXDialog(defaultStackPane, layout, JFXDialog.DialogTransition.TOP);

      JFXButton closeButton = createCloseButton();
      closeButton.setOnAction(
          event -> {
            dialog.close();
          });

      JFXTextField phoneNumberField = new JFXTextField();

      JFXButton sendButton = createSendButton();

      sendButton.setOnAction(
          event -> {
            String phoneNumber = phoneNumberField.getText();
            if (!phoneNumberField.getText().isEmpty() && phoneNumberField.getText() != null) {
              if (phoneNumber.contains(" ")
                  || phoneNumber.contains("(")
                  || phoneNumber.contains(")")
                  || phoneNumber.contains("-")) {
                phoneNumber.replace(" ", "");
                phoneNumber.replace("(", "");
                phoneNumber.replace(")", "");
                phoneNumber.replace("-", "");
              }
              if (!phoneNumber.substring(0, 2).equals("+1")) {
                if (phoneNumber.length() == 10) {
                  phoneNumber = "+1" + phoneNumber;
                }
              }
              final String number = phoneNumber;

              Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
              Message message =
                  Message.creator(
                          new com.twilio.type.PhoneNumber(number),
                          new com.twilio.type.PhoneNumber("+12029310539"),
                          orderNum)
                      .create();
              System.out.println(message.getSid());
              dialog.close();
            } else {
              dialog.close();
            }
          });

      layout.setActions(phoneNumberField, sendButton, closeButton);
      dialog.show();
      return dialog;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static void setDefaultStackPane(StackPane defaultStackPane) {
    DialogUtil.defaultStackPane = defaultStackPane;
  }
}

package edu.wpi.cs3733.d20.teamA.controllers.dialog;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.jfoenix.controls.JFXDialog;
import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class QRDialogController implements IDialogController {
  @FXML private ImageView qrImage;

  private final String text;
  private JFXDialog dialog;

  public QRDialogController(String text) {
    this.text = text;
  }

  @FXML
  public void initialize() {
    QRCodeWriter writer = new QRCodeWriter();
    try {
      BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 200, 200);
      BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
      Image javafxImage = SwingFXUtils.toFXImage(image, null);
      qrImage.setImage(javafxImage);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void setDialog(JFXDialog dialog) {
    this.dialog = dialog;
  }
}

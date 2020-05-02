package edu.wpi.cs3733.d20.teamA;

import com.fazecast.jSerialComm.*;

public class SerialTest {

  public static void main(String[] args) throws Exception {
    SerialPort comPort = SerialPort.getCommPorts()[0];
    comPort.openPort();
    try {
      while (true) {
        while (comPort.bytesAvailable() != 14) Thread.sleep(20);

        byte[] readBuffer = new byte[comPort.bytesAvailable()];
        int numRead = comPort.readBytes(readBuffer, readBuffer.length);
        System.out.println(new String(readBuffer, "UTF-8"));
        System.out.println("Read " + numRead + " bytes.");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    comPort.closePort();
  }
}

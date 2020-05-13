package edu.wpi.cs3733.d20.teamA.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;

public class ThreadPool {
  private static ExecutorService executor =
      Executors.newFixedThreadPool(
          500,
          r -> {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
          });

  public static void runBackgroundTask(Runnable runnable) {
    executor.submit(runnable);
  }

  public static void singleShotMainTask(long millis, Runnable runnable) {
    executor.submit(
        () -> {
          try {
            Thread.sleep(millis);
          } catch (InterruptedException ignored) {
          }
          Platform.runLater(runnable);
        });
  }
}

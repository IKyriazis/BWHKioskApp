package edu.wpi.cs3733.d20.teamA.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
  private static ExecutorService executor = Executors.newFixedThreadPool(5);

  public static void runBackgroundTask(Runnable runnable) {
    executor.submit(runnable);
  }
}

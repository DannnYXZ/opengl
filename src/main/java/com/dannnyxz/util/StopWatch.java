package com.dannnyxz.util;

public class StopWatch {

  private long lastNanoTime = System.nanoTime();
  private long deltaNanoTime;

  /**
   * Measured delta time from previous measurement and stores it.
   */
  public void measureDeltaTime() {
    long currentTime = System.nanoTime();
    deltaNanoTime = currentTime - lastNanoTime;
    lastNanoTime = currentTime;
  }

  /**
   * Retrieves stored delta time.
   */
  public float getDeltaSeconds() {
    return deltaNanoTime / 1_000_000_000f;
  }
}

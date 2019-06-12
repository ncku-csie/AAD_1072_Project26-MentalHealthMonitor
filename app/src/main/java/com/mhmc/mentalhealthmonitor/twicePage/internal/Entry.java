package com.mhmc.mentalhealthmonitor.twicePage.internal;


public class Entry {
  public final float high;
  public final float low;
  public final float open;
  public final float close;

  public final int volume;

  public String xValue;

  public Entry(float high, float low, float open, float close, int volume, String xValue) {
    this.high = high;
    this.low = low;
    this.open = open;
    this.close = close;
    this.volume = volume;
    this.xValue = xValue;
  }
}

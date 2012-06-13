package com.ratethisfest.server.logic;


public class CoachellaSetDataLoader extends SetDataLoader {

  // Private constructor prevents instantiation from other classes
  private CoachellaSetDataLoader() {
    super(CoachellaRatingManager.getInstance());
  }

  /**
   * SingletonHolder is loaded on the first execution of Singleton.getInstance()
   * or the first access to SingletonHolder.INSTANCE, not before.
   */
  private static class SingletonHolder {
    public static final CoachellaSetDataLoader instance = new CoachellaSetDataLoader();
  }

  public static CoachellaSetDataLoader getInstance() {
    return SingletonHolder.instance;
  }

}

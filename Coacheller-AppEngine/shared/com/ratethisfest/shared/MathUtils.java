package com.ratethisfest.shared;

import java.text.DecimalFormat;

public class MathUtils {

  public static double roundTwoDecimals(double d) {
    DecimalFormat twoDForm = new DecimalFormat("#.##");
    return Double.valueOf(twoDForm.format(d));
  }

}

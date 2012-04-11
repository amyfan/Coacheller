package com.coacheller.shared;

/**
 * <p>
 * FieldVerifier validates that the name the user enters is valid.
 * </p>
 * <p>
 * This class is in the <code>shared</code> package because we use it in both
 * the client code and on the server. On the client, we verify that the name is
 * valid before sending an RPC request so the user doesn't have to wait for a
 * network round trip to get feedback. On the server, we verify that the name is
 * correct to ensure that the input is correct regardless of where the RPC
 * originates.
 * </p>
 * <p>
 * When creating a class that is used on both the client and the server, be sure
 * that all code is translatable and does not use native JavaScript. Code that
 * is not translatable (such as code that interacts with a database or the file
 * system) cannot be compiled into client side JavaScript. Code that uses native
 * JavaScript (such as Widgets) cannot be run on the server.
 * </p>
 */
public class FieldVerifier {
  public static final String EMAIL_ERROR = "Please enter valid email address";
  public static final String YEAR_ERROR = "Please enter valid year";
  public static final String DAY_ERROR = "Please enter valid day";
  public static final String WEEKEND_ERROR = "Please enter valid weekend number";
  public static final String SCORE_ERROR = "Please enter valid score";

  /**
   * Verifies that the specified name is valid for our service.
   * 
   * In this example, we only require that the name is at least four characters.
   * In your application, you can use more complex checks to ensure that
   * usernames, passwords, email addresses, URLs, and other fields have the
   * proper syntax.
   * 
   * @param name
   *          the name to validate
   * @return true if valid, false if invalid
   */
  public static boolean isValidName(String name) {
    if (name == null) {
      return false;
    }
    return name.length() > 3;
  }

  public static boolean isValidEmail(String name) {
    if (name == null) {
      return false;
    } else if (!name.contains("@")) {
      return false;
    } else if (!name.contains(".")) {
      return false;
    }
    return name.length() > 10;
  }

  public static boolean isValidYear(String year) {
    if (year == null) {
      return false;
    } else if (year.length() != 4) {
      return false;
    } else {
      try {
        Integer.parseInt(year);
      } catch (Exception e) {
        return false;
      }
      return true;
    }
  }

  public static boolean isValidDay(String day) {
    if (DayEnum.FRIDAY.getValue().equals(day) || DayEnum.SATURDAY.getValue().equals(day)
        || DayEnum.SUNDAY.getValue().equals(day)) {
      return true;
    }
    return false;
  }

  public static boolean isValidWeekend(String weekend) {
    if ("1".equals(weekend) || "2".equals(weekend)) {
      return true;
    }
    return false;
  }

  public static boolean isValidScore(String score) {
    if (score == null) {
      return false;
    } else {
      try {
        Integer scoreInt = Integer.parseInt(score);
        if (scoreInt > 0 && scoreInt <= 5) {
          return true;
        } else {
          return false;
        }
      } catch (Exception e) {
        return false;
      }
    }
  }

}

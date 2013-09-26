package com.ratethisfest.shared;

public class Helper {

  public static String readXmlProperty(String propertyName, String xmlData) {
    String xmlPropertyOpen = "<" + propertyName + ">";
    String xmlPropertyClose = "</" + propertyName + ">";
    int propertyOpenIndex = xmlData.indexOf(xmlPropertyOpen);
    int propertyCloseIndex = xmlData.indexOf(xmlPropertyClose);
    if (propertyOpenIndex == -1) {
      System.out.println("Property not found in XML data");
      return null;
    } else if (propertyCloseIndex == -1) {
      System.out.println("Property close tag not found in XML data");
      return null;
    }
    return xmlData.substring(propertyOpenIndex + xmlPropertyOpen.length(), propertyCloseIndex);
  }

  public static String readSimpleJsonProperty(String propertyName, String jsonData) {
    String jsonPropertyDeclare = "\"" + propertyName + "\":";
    int propertyOpenIndex = jsonData.indexOf(jsonPropertyDeclare) + jsonPropertyDeclare.length();
    boolean inQuotes = false;
    boolean inBraces = false;
    char firstChar = jsonData.charAt(propertyOpenIndex);
    if (firstChar == '"') {
      inQuotes = true;
    }
    if (firstChar == '{') {
      inBraces = true;
    }

    if (inBraces) {
      System.out.println("Can't parse a complex JSON property in braces");
      return "{}";
    }

    int propertyCloseIndex = 0;
    if (inQuotes) {
      propertyOpenIndex++;
      propertyCloseIndex = jsonData.indexOf('"', propertyOpenIndex);
    } else {
      propertyCloseIndex = jsonData.indexOf(",", propertyOpenIndex);
    }

    String returnString = jsonData.substring(propertyOpenIndex, propertyCloseIndex);
    return returnString;
  }
}

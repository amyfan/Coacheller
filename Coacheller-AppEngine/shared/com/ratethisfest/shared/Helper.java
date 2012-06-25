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
}

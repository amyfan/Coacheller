package com.ratethisfest.server.logic;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtils {

  public static <E> List<E> iterableToList(Iterable<E> iterable) {
    ArrayList<E> list = new ArrayList<E>();
    if (iterable != null) {
      for (E e : iterable) {
        list.add(e);
      }
    }
    return list;
  }
}

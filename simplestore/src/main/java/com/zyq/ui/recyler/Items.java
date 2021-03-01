

package com.zyq.ui.recyler;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A convenient class for creating a {@code ArrayList<Object>}.
 *
 * @author drakeet
 */
public class Items extends ArrayList<Object> {

  /**
   * Constructs an empty Items with an initial capacity of ten.
   */
  public Items() {
    super();
  }


  /**
   * Constructs an empty Items with the specified initial capacity.
   *
   * @param initialCapacity the initial capacity of the Items
   * @throws IllegalArgumentException if the specified initial capacity
   * is negative
   */
  public Items(int initialCapacity) {
    super(initialCapacity);
  }


  /**
   * Constructs a Items containing the elements of the specified
   * collection, in the order they are returned by the collection's
   * iterator.
   *
   * @param c the collection whose elements are to be placed into this Items
   * @throws NullPointerException if the specified collection is null
   */
  public Items( Collection<?> c) {
    super(c);
  }
}



package com.zyq.ui.recyler;


import androidx.annotation.IntRange;

/**
 * An interface to link the items and binders by array integer index.
 *
 * @author drakeet
 */
public interface Linker<T> {

  /**
   * Returns the index of your registered binders for your item. The result should be in range of
   * {@code [0, one-to-multiple-binders.length)}.
   *
   * <p>Note: The argument of {@link OneToManyFlow#to(ItemViewBinder[])} is the
   * one-to-multiple-binders.</p>
   *
   * @param position The position in items
   * @param t Your item data
   * @return The index of your registered binders
   * @see OneToManyFlow#to(ItemViewBinder[])
   * @see OneToManyEndpoint#withLinker(Linker)
   */
  @IntRange(from = 0) int index(int position, T t);
}

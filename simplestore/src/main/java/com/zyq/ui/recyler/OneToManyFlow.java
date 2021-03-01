

package com.zyq.ui.recyler;


/**
 * Process and flow operators for one-to-many.
 *
 * @author drakeet
 */
public interface OneToManyFlow<T> {

  /**
   * Sets some item view binders to the item type.
   *
   * @param binders the item view binders
   * @return end flow operator
   */

   OneToManyEndpoint<T> to( ItemViewBinder<T, ?>... binders);
}

package com.zyq.ui.recyler;

import java.util.Arrays;

/**
 * @author drakeet
 */
final class ClassLinkerWrapper<T> implements Linker<T> {

  private final  ClassLinker<T> classLinker;
  private final  ItemViewBinder<T, ?>[] binders;


  private ClassLinkerWrapper(
       ClassLinker<T> classLinker,
       ItemViewBinder<T, ?>[] binders) {
    this.classLinker = classLinker;
    this.binders = binders;
  }


  static  <T> ClassLinkerWrapper<T> wrap(
       ClassLinker<T> classLinker,
       ItemViewBinder<T, ?>[] binders) {
    return new ClassLinkerWrapper<T>(classLinker, binders);
  }


  @Override
  public int index(int position,  T t) {
    Class<?> userIndexClass = classLinker.index(position, t);
    for (int i = 0; i < binders.length; i++) {
      if (binders[i].getClass().equals(userIndexClass)) {
        return i;
      }
    }
    throw new IndexOutOfBoundsException(
        String.format("%s is out of your registered binders'(%s) bounds.",
            userIndexClass.getName(), Arrays.toString(binders))
    );
  }
}



package com.zyq.ui.recyler;
import java.util.ArrayList;
import java.util.List;

import static com.zyq.ui.recyler.Preconditions.checkNotNull;


/**
 * An List implementation of TypePool.
 *
 * @author drakeet
 */
public class MultiTypePool implements TypePool {

  private final  List<Class<?>> classes;
  private final  List<ItemViewBinder<?, ?>> binders;
  private final  List<Linker<?>> linkers;


  /**
   * Constructs a MultiTypePool with default lists.
   */
  public MultiTypePool() {
    this.classes = new ArrayList<>();
    this.binders = new ArrayList<>();
    this.linkers = new ArrayList<>();
  }


  /**
   * Constructs a MultiTypePool with default lists and a specified initial capacity.
   *
   * @param initialCapacity the initial capacity of the list
   */
  public MultiTypePool(int initialCapacity) {
    this.classes = new ArrayList<>(initialCapacity);
    this.binders = new ArrayList<>(initialCapacity);
    this.linkers = new ArrayList<>(initialCapacity);
  }


  /**
   * Constructs a MultiTypePool with specified lists.
   *
   * @param classes the list for classes
   * @param binders the list for binders
   * @param linkers the list for linkers
   */
  public MultiTypePool(
       List<Class<?>> classes,
       List<ItemViewBinder<?, ?>> binders,
       List<Linker<?>> linkers) {
    checkNotNull(classes);
    checkNotNull(binders);
    checkNotNull(linkers);
    this.classes = classes;
    this.binders = binders;
    this.linkers = linkers;
  }


  @Override
  public <T> void register(
       Class<? extends T> clazz,
       ItemViewBinder<T, ?> binder,
       Linker<T> linker) {
    checkNotNull(clazz);
    checkNotNull(binder);
    checkNotNull(linker);
    classes.add(clazz);
    binders.add(binder);
    linkers.add(linker);
  }


  @Override
  public boolean unregister( Class<?> clazz) {
    checkNotNull(clazz);
    boolean removed = false;
    while (true) {
      int index = classes.indexOf(clazz);
      if (index != -1) {
        classes.remove(index);
        binders.remove(index);
        linkers.remove(index);
        removed = true;
      } else {
        break;
      }
    }
    return removed;
  }


  @Override
  public int size() {
    return classes.size();
  }


  @Override
  public int firstIndexOf( final Class<?> clazz) {
    checkNotNull(clazz);
    int index = classes.indexOf(clazz);
    if (index != -1) {
      return index;
    }
    for (int i = 0; i < classes.size(); i++) {
      if (classes.get(i).isAssignableFrom(clazz)) {
        return i;
      }
    }
    return -1;
  }


  @Override
  public  Class<?> getClass(int index) {
    return classes.get(index);
  }


  @Override
  public  ItemViewBinder<?, ?> getItemViewBinder(int index) {
    return binders.get(index);
  }


  @Override
  public  Linker<?> getLinker(int index) {
    return linkers.get(index);
  }
}

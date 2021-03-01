

package com.zyq.ui.recyler;

import static com.zyq.ui.recyler.Preconditions.checkNotNull;

/**
 * @author drakeet
 */
class OneToManyBuilder<T> implements OneToManyFlow<T>, OneToManyEndpoint<T> {

  private final  MultiTypeAdapter adapter;
  private final  Class<? extends T> clazz;
  private ItemViewBinder<T, ?>[] binders;


  OneToManyBuilder( MultiTypeAdapter adapter,  Class<? extends T> clazz) {
    this.clazz = clazz;
    this.adapter = adapter;
  }


  @Override
  public final  OneToManyEndpoint<T> to( ItemViewBinder<T, ?>... binders) {
    checkNotNull(binders);
    this.binders = binders;
    return this;
  }


  @Override
  public void withLinker( Linker<T> linker) {
    checkNotNull(linker);
    doRegister(linker);
  }


  @Override
  public void withClassLinker( ClassLinker<T> classLinker) {
    checkNotNull(classLinker);
    doRegister(ClassLinkerWrapper.wrap(classLinker, binders));
  }


  private void doRegister( Linker<T> linker) {
    for (ItemViewBinder<T, ?> binder : binders) {
      adapter.register(clazz, binder, linker);
    }
  }
}

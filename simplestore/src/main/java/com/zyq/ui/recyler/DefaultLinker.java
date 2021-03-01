package com.zyq.ui.recyler;


final class DefaultLinker<T> implements Linker<T> {

  @Override
  public int index(int position, T t) {
    return 0;
  }
}

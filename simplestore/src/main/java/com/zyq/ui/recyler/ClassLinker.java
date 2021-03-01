package com.zyq.ui.recyler;

public interface ClassLinker<T> {

    /**
     * Returns the class of your registered binders for your item.
     *
     * @param position The position in items
     * @param t        The item
     * @return The index of your registered binders
     * @see OneToManyEndpoint#withClassLinker(ClassLinker)
     */
    Class<? extends ItemViewBinder<T, ?>> index(int position, T t);
}

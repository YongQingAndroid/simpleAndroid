

package com.zyq.ui.recyler;


/**
 * End-operators for one-to-many.
 *
 * @author drakeet
 */
public interface OneToManyEndpoint<T> {

    /**
     * Sets a linker to link the items and binders by array index.
     *
     * @param linker the row linker
     * @see Linker
     */
    void withLinker(Linker<T> linker);

    /**
     * Sets a class linker to link the items and binders by the class instance of binders.
     *
     * @param classLinker the class linker
     * @see ClassLinker
     */
    void withClassLinker(ClassLinker<T> classLinker);
}

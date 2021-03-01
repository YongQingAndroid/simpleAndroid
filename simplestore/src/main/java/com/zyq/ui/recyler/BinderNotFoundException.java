
package com.zyq.ui.recyler;

class BinderNotFoundException extends RuntimeException {

    BinderNotFoundException(Class<?> clazz) {
        super("Do you have registered {className}.class to the binder in the adapter/pool?"
                .replace("{className}", clazz.getSimpleName()));
    }
}

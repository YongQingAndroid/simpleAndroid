package com.zyq.ui.recyler;



@SuppressWarnings("WeakerAccess")
public final class Preconditions {

    @SuppressWarnings("ConstantConditions")
    public static <T> T checkNotNull(final T object) {
        if (object == null) {
            throw new NullPointerException();
        }
        return object;
    }


    private Preconditions() {
    }
}

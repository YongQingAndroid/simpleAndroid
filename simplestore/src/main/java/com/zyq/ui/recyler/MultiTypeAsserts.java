

package com.zyq.ui.recyler;


import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import static com.zyq.ui.recyler.Preconditions.checkNotNull;



public final class MultiTypeAsserts {

    private MultiTypeAsserts() {
        throw new AssertionError();
    }


    /**
     * 使类在调试和索引中发生异常。
     *
     * @throws IllegalArgumentException 如果您的项目 列表为空
     * @param适配器MultiTypeAdapter
     * @param项目项目列表 如果检查失败，则抛出BinderNotFoundException
     */
    @SuppressWarnings("unchecked")
    public static void assertAllRegistered(MultiTypeAdapter adapter, List<?> items)
            throws BinderNotFoundException, IllegalArgumentException, IllegalAccessError {
        checkNotNull(adapter);
        checkNotNull(items);
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Your Items/List is empty.");
        }
        for (int i = 0; i < items.size(); i++) {
            adapter.indexInTypesOf(i, items.get(0));
        }
        /* All passed. */
    }


    /**
     * @param recyclerView RecyclerView
     * @throws IllegalAccessError       assertHasTheSameAdapter（）方法必须放在后面
     *                                  recyclerView.setAdapter（）。
     * @throws IllegalArgumentException 如果您的recyclerView的适配器。
     *                                  不是带有参数适配器的示例。
     * @param适配器MultiTypeAdapter
     */
    public static void assertHasTheSameAdapter(RecyclerView recyclerView, MultiTypeAdapter adapter)
            throws IllegalArgumentException, IllegalAccessError {
        checkNotNull(recyclerView);
        checkNotNull(adapter);
        if (recyclerView.getAdapter() == null) {
            throw new IllegalAccessError("The assertHasTheSameAdapter() method must " +
                    "be placed after recyclerView.setAdapter()");
        }
        if (recyclerView.getAdapter() != adapter) {
            throw new IllegalArgumentException(
                    "Your recyclerView's adapter is not the sample with the argument adapter.");
        }
    }
}

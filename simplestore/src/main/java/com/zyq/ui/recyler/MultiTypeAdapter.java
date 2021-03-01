package com.zyq.ui.recyler;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import androidx.recyclerview.widget.RecyclerView;

import static com.zyq.ui.recyler.Preconditions.checkNotNull;


/**
 * @author drakeet
 */
public class MultiTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MultiTypeAdapter";

    private
    List<?> items;
    private TypePool typePool;


    public MultiTypeAdapter() {
        this(Collections.emptyList());
    }


    public MultiTypeAdapter(List<?> items) {
        this(items, new MultiTypePool());
    }


    public MultiTypeAdapter(List<?> items, int initialCapacity) {
        this(items, new MultiTypePool(initialCapacity));
    }

    public MultiTypeAdapter(List<?> items, TypePool pool) {
        checkNotNull(items);
        checkNotNull(pool);
        this.items = items;
        this.typePool = pool;
    }


    /**
     * 注册类型类及其项目视图联编程序。如果您已经注册课程，
     * 它将覆盖原始活页夹。请注意，该方法是非线程安全的
     * ，因此您不应在并发操作中使用它。
     * <p>
     * 请注意，该方法不应在之后调用
     * {@link RecyclerView＃setAdapter（RecyclerView.Adapter）}，或者您必须调用setAdapter
     * 再次。
     * </ p>
     *
     * @param clazz     项目的类别
     * @param <T>项目数据类型
     * @param活页夹项目视图活页夹
     */
    public <T> void register(Class<? extends T> clazz, ItemViewBinder<T, ?> binder) {
        checkNotNull(clazz);
        checkNotNull(binder);
        checkAndRemoveAllTypesIfNeeded(clazz);
        register(clazz, binder, new DefaultLinker<T>());
    }


    <T> void register(
            Class<? extends T> clazz,
            ItemViewBinder<T, ?> binder,
            Linker<T> linker) {
        typePool.register(clazz, binder, linker);
        binder.adapter = this;
    }


    /**
     * 将类型类注册到多个项目视图联编程序。如果您已经注册了
     * 类，它将覆盖原始活页夹。请注意，该方法是非线程安全的
     * ，因此您不应在并发操作中使用它。
     * <p>
     * 请注意，该方法不应在之后调用
     * {@link RecyclerView＃setAdapter（RecyclerView.Adapter）}，或者您必须调用setAdapter
     * 再次。
     * </ p>
     *
     * @param clazz     项目的类别
     * @param <T>项目数据类型
     * @return {@link OneToManyFlow}用于设置活页夹
     * @see #register（Class，ItemViewBinder）
     */

    public <T> OneToManyFlow<T> register(Class<? extends T> clazz) {
        checkNotNull(clazz);
        checkAndRemoveAllTypesIfNeeded(clazz);
        return new OneToManyBuilder<>(this, clazz);
    }


    /**
     * 在指定的类型池中注册所有内容。如果您已经注册了
     * 类，它将覆盖原始活页夹。请注意，该方法是非线程安全的
     * ，因此您不应在并发操作中使用它。
     * <p>
     * 请注意，该方法不应在之后调用
     * {@link RecyclerView＃setAdapter（RecyclerView.Adapter）}，或者您必须调用setAdapter
     * 再次。
     * </ p>
     *
     * @param池类型池，包含要添加到此适配器内部池中的内容
     * @请参阅#register（Class）
     * @see #register（Class，ItemViewBinder）
     */
    public void registerAll(final TypePool pool) {
        checkNotNull(pool);
        final int size = pool.size();
        for (int i = 0; i < size; i++) {
            registerWithoutChecking(
                    pool.getClass(i),
                    pool.getItemViewBinder(i),
                    pool.getLinker(i)
            );
        }
    }


    /**
     * 自动设置和更新项目。建议使用这种方法
     * 用新的包装器列表更新项目，或考虑使用{@link CopyOnWriteArrayList}。
     *
     * <p>注意：如果要在设置项目后刷新列表视图，则应
     * 自己致电{@link RecyclerView.Adapter＃notifyDataSetChanged（）}。</ p>
     *
     * @param项目新项目列表
     * @自v2.4.1起
     */
    public void setItems(List<?> items) {
        checkNotNull(items);
        this.items = items;
    }


    public List<?> getItems() {
        return items;
    }


    /**
     * 设置TypePool以容纳类型并查看活页夹。
     *
     * @param typePool TypePool实现
     */
    public void setTypePool(TypePool typePool) {
        checkNotNull(typePool);
        this.typePool = typePool;
    }


    public TypePool getTypePool() {
        return typePool;
    }


    @Override
    public final int getItemViewType(int position) {
        Object item = items.get(position);
        return indexInTypesOf(position, item);
    }


    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int indexViewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemViewBinder<?, ?> binder = typePool.getItemViewBinder(indexViewType);
        return binder.onCreateViewHolder(inflater, parent);
    }


    @Override
    @Deprecated
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        onBindViewHolder(holder, position, Collections.emptyList());
    }


    @Override
    @SuppressWarnings("unchecked")
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        Object item = items.get(position);
        ItemViewBinder binder = typePool.getItemViewBinder(holder.getItemViewType());
        binder.onBindViewHolder(holder, item, payloads);
    }


    @Override
    public final int getItemCount() {
        return items.size();
    }

    /**
     * 被调用以返回该项目的稳定ID，并将事件传递到其关联的活页夹。
     *
     * @param position 适配器位置查询
     * @返回项目在位置的稳定ID
     * @请参阅ItemViewBinder＃getItemId（Object）
     * @自v3.2.0起
     * @see RecyclerView.Adapter＃setHasStableIds（boolean）
     */
    @Override
    @SuppressWarnings("unchecked")
    public final long getItemId(int position) {
        Object item = items.get(position);
        int itemViewType = getItemViewType(position);
        ItemViewBinder binder = typePool.getItemViewBinder(itemViewType);
        return binder.getItemId(item);
    }


    @Override
    @SuppressWarnings("unchecked")
    public final void onViewRecycled(RecyclerView.ViewHolder holder) {
        getRawBinderByViewHolder(holder).onViewRecycled(holder);
    }


    @Override
    @SuppressWarnings("unchecked")
    public final boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
        return getRawBinderByViewHolder(holder).onFailedToRecycleView(holder);
    }


    @Override
    @SuppressWarnings("unchecked")
    public final void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        getRawBinderByViewHolder(holder).onViewAttachedToWindow(holder);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        getRawBinderByViewHolder(holder).onViewDetachedFromWindow(holder);
    }


    private ItemViewBinder getRawBinderByViewHolder(RecyclerView.ViewHolder holder) {
        return typePool.getItemViewBinder(holder.getItemViewType());
    }


    int indexInTypesOf(int position, Object item) throws BinderNotFoundException {
        int index = typePool.firstIndexOf(item.getClass());
        if (index != -1) {
            @SuppressWarnings("unchecked")
            Linker<Object> linker = (Linker<Object>) typePool.getLinker(index);
            return index + linker.index(position, item);
        }
        throw new BinderNotFoundException(item.getClass());
    }


    private void checkAndRemoveAllTypesIfNeeded(Class<?> clazz) {
        if (typePool.unregister(clazz)) {
            Log.w(TAG, "You have registered the " + clazz.getSimpleName() + " type. " +
                    "It will override the original binder(s).");
        }
    }


    /**
     * A safe register method base on the TypePool's safety for TypePool.
     */
    @SuppressWarnings("unchecked")
    private void registerWithoutChecking(Class clazz, ItemViewBinder binder, Linker linker) {
        checkAndRemoveAllTypesIfNeeded(clazz);
        register(clazz, binder, linker);
    }
}

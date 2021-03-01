

package com.zyq.ui.recyler;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/***
 * @author drakeet
 */
public abstract class ItemViewBinder<T, VH extends RecyclerView.ViewHolder> {

    /* internal */ MultiTypeAdapter adapter;


    protected abstract VH onCreateViewHolder(LayoutInflater inflater, ViewGroup parent);

    protected abstract void onBindViewHolder(VH holder, T item);


    protected void onBindViewHolder(VH holder, T item, List<Object> payloads) {
        onBindViewHolder(holder, item);
    }


    protected final int getPosition(final RecyclerView.ViewHolder holder) {
        return holder.getAdapterPosition();
    }

    protected final MultiTypeAdapter getAdapter() {
        if (adapter == null) {
            throw new IllegalStateException("ItemViewBinder " + this + " not attached to MultiTypeAdapter. " +
                    "You should not call the method before registering the binder.");
        }
        return adapter;
    }

    protected long getItemId(T item) {
        return RecyclerView.NO_ID;
    }

    protected void onViewRecycled(VH holder) {
    }

    protected boolean onFailedToRecycleView(VH holder) {
        return false;
    }


    protected void onViewAttachedToWindow(VH holder) {
    }


    protected void onViewDetachedFromWindow(VH holder) {
    }
}

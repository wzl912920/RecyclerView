package com.lynn.simplerecyclerview.base;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Lynn.
 */

public abstract class BaseViewHolder<T extends Object> extends RecyclerView.ViewHolder {
    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bind(T data);
}

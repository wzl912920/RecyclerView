package com.lynn.library.recycler;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Lynn.
 */

public class BaseRecycledView extends RecyclerView {
    private BaseRecycledAdapter adapter;

    public BaseRecycledView(Context context) {
        this(context, null, 0);
    }

    public BaseRecycledView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseRecycledView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        adapter = new BaseRecycledAdapter();
        setAdapter(adapter);
        setLayoutManager(new LinearLayoutManager(getContext()));
    }
}

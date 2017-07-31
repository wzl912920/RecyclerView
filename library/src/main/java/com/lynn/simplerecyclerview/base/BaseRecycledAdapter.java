package com.lynn.simplerecyclerview.base;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lynn.
 */

public class BaseRecycledAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    private List<T> mItems = new ArrayList();
    private BinderTools tools;
    private Map<Integer, Integer> type2Layout = new HashMap<>();

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (null == type2Layout.get(viewType) || null == tools) {
            return new BaseViewHolder(parent) {
                @Override
                public void bind(Object data) {
                }
            };
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(type2Layout.get(viewType), parent, false);
        return tools.getHolder(view, viewType);
    }

    public void setBinder(BinderTools tools) {
        this.tools = tools;
    }

    public void register(int type, @LayoutRes int layoutId) {
        type2Layout.put(type, layoutId);
    }

    public void add(T data) {
        if (null != mItems) {
            mItems.add(data);
        }
    }

    public void addAll(@NonNull List<T> list) {
        if (null != mItems) {
            mItems.addAll(list);
        }
    }

    public void remove(int position) {
        if (null != mItems && position >= 0 && position < mItems.size()) {
            mItems.remove(position);
        }
    }

    public void remove(T data) {
        if (null != mItems && mItems.size() > 0) {
            mItems.remove(data);
        }
    }

    public void clear() {
        if (null != mItems) {
            mItems.clear();
        }
    }

    public List getData() {
        return mItems;
    }

    @Override
    public int getItemViewType(int position) {
        if (null != mItems && position >= 0 && position < mItems.size()) {
            Object obj = mItems.get(position);
            if (null != tools) {
                return tools.getType(obj);
            }
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.bind(null == mItems ? null : mItems.get(position));
    }

    @Override
    public int getItemCount() {
        if (null == mItems) {
            return 0;
        }
        return mItems.size();
    }
}

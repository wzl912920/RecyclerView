package com.lynn.simplerecyclerview.base;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by Lynn.
 */

public interface BinderTools {
    BaseViewHolder getHolder(@NonNull View view, int type);

    <T> int getType(T t);
}

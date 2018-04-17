package com.lynn.recyclerdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.lynn.library.recycler.BaseRecyclerAdapter;

/**
 * Created by Lynn.
 */
public class TestActivity extends Activity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseRecyclerAdapter adapter = new BaseRecyclerAdapter();
    }
}

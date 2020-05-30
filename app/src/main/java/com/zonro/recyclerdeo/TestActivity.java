package com.zonro.recyclerdeo;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.zonro.library.recycler.BaseRecyclerAdapter;

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

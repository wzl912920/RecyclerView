package com.lynn.library.db;

import android.os.Looper;
import android.util.Log;

/**
 * Created by lynn on 16/9/22.
 */
public abstract class BaseDBRequest<T> implements Runnable {
    String error;
    T t;
    private boolean errorState;
    private boolean successState;

    public BaseDBRequest() {
    }

    @Override
    public final void run() {
        Log.e("db", "isMainThread=" + (Thread.currentThread() == Looper.getMainLooper().getThread()));
        try {
            T t = init();
            if (errorState || successState) {
                return;
            }
            postSuccess(t);
        } catch (Exception e) {
            postError(e.getMessage());
        }
    }

    protected abstract T init();

    protected abstract void onSuccess(T t);

    protected abstract void onError(String error);

    protected final void postError(String error) {
        errorState = true;
        this.error = error;
        DBHelper.getInstance().post(this);
    }

    protected final void postSuccess(T t) {
        successState = true;
        this.t = t;
        DBHelper.getInstance().post(this);
    }
}

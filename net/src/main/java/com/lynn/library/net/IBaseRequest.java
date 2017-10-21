package com.lynn.library.net;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by lynn on 16/8/4.
 */
abstract class IBaseRequest<T, K extends Serializable> implements Callback {

    private final WeakReference<T> weakReference;
    private final Handler handler;
    private final Map<String, Object> params;
    private final Map<String, String> headers;
    private String responseString;
    private static final String TAG = "Response";

    public IBaseRequest(T t) {
        weakReference = new WeakReference(t);
        handler = new Handler(Looper.getMainLooper());
        params = new LinkedHashMap<>();
        headers = new HashMap<>();
    }

    protected final T getMainObject() {
        return null == weakReference ? null : weakReference.get();
    }

    @Override
    public final void onFailure(Call call, final IOException e) {
        if (checkNull()) {
            handler.removeCallbacksAndMessages(null);
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                onError(null, e.getMessage(), 0);
            }
        });
    }

    @Override
    public final void onResponse(Call call, final Response response) throws IOException {
        final int responseCode = response.code();
        try {
            if (checkNull()) {
                handler.removeCallbacksAndMessages(null);
                return;
            }
            responseString = response.body().string();
            log(getUrl() + " === " + responseString);
            if (TextUtils.isEmpty(responseString)) {
                if (checkNull()) {
                    handler.removeCallbacks(null);
                    return;
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onSuccess(null, responseString, responseCode);
                    }
                });
                return;
            }
            Type type = this.getClass().getGenericSuperclass();
            Class<K> clazz = null;
            if (type != null && type instanceof ParameterizedType) {
                Type[] p = ((ParameterizedType) type).getActualTypeArguments();
                if (null != p && p.length > 1) {
                    clazz = (Class<K>) p[1];
                }
            }
            final K k = Config.GSON.fromJson(responseString, clazz);
            if (checkNull()) {
                handler.removeCallbacksAndMessages(null);
                return;
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onSuccess(k, responseString, response.code());
                }
            });
        } catch (final Exception e) {
            e.printStackTrace();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onError(null, e.getMessage(), responseCode);
                }
            });
        } finally {
            response.close();
        }
    }

    protected final void addHeader(String key, String value) {
        headers.put(key, value);
    }

    protected final void addHeaders(Map<String, String> map) {
        headers.putAll(map);
    }

    protected final void addParam(String k, Object value) {
        params.put(k, value);
    }

    protected final void addParams(Map<String, String> map) {
        params.putAll(map);
    }

    protected final Map<String, String> getHeaders() {
        return headers;
    }

    protected final Map<String, Object> getParams() {
        return params;
    }

    private boolean checkNull() {
        boolean flag = null == weakReference || null == getMainObject();
        if (getMainObject() instanceof Activity) {
            flag |= ((Activity) weakReference.get()).isFinishing();
        } else if (getMainObject() instanceof Fragment) {
            Fragment fragment = (Fragment) weakReference.get();
            flag |= null == fragment.getActivity() || fragment.getActivity().isFinishing();
        }
        return flag;
    }

    protected abstract void onSuccess(K k, String response, int httpCode);

    protected abstract void onError(K k, String msg, int httpCode);

    protected abstract String getUrl();

    protected final void cancel() {
        HttpUtils.getInstance().cancel(this);
    }

    private static final void log(String log) {
        if (Config.DEBUG) {
            Log.e(TAG, log);
        }
    }
}

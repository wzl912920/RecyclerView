package com.lynn.library.net;

import java.io.Serializable;

/**
 * Created by lynn on 16/8/4.
 */
public abstract class BaseRequest<T, K extends Serializable> extends IBaseRequest<T, K> {
    public BaseRequest(T t) {
        super(t);
    }

    @Override
    protected abstract void onSuccess(K k, String response, int httpCode);

    @Override
    protected abstract void onError(K k, String msg, int httpCode);

    @Override
    protected abstract String getUrl();
}

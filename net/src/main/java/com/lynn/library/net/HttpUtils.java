package com.lynn.library.net;

import android.app.Application;
import android.graphics.Bitmap;
import android.text.TextUtils;

import java.util.List;

import okhttp3.Call;

/**
 * Created by lynn on 16/8/4.
 */
public class HttpUtils {
    private static HttpUtils httpUtils;
    private HttpApi httpApi;

    private HttpUtils() {
        httpApi = HttpApi.getInstance();
    }

    private static boolean isInited = false;

    public static HttpUtils init(Application context, String domain, String testDomain) {
        if (TextUtils.isEmpty(domain)) {
            domain = "www.baidu.com";
        }
        if (!domain.startsWith("http")) {
            domain = "http://" + domain;
        }
        Config.DOMAIN = domain;
        if (!TextUtils.isEmpty(testDomain)) {
            if (!testDomain.startsWith("http")) {
                testDomain = "http://" + testDomain;
            }
            Config.TEST_DOMAIN = testDomain;
        } else {
            Config.TEST_DOMAIN = Config.DOMAIN;
        }
        isInited = true;
        HttpUtils utils = getInstance();
        utils.httpApi.init(context);
        return utils;
    }

    public void setDebugMode(boolean debug) {
        if (debug && TextUtils.isEmpty(Config.TEST_DOMAIN)) {
            throw new NullPointerException("test domain isnot initialized");
        }
        Config.DEBUG = debug;
    }

    public static HttpUtils getInstance() {
        if (!isInited) {
            throw new IllegalStateException("httpUtils isnot initialized");
        }
        if (null == httpUtils) {
            synchronized (HttpUtils.class) {
                if (null == httpUtils) {
                    httpUtils = new HttpUtils();
                }
            }
        }
        return httpUtils;
    }

    public synchronized void cancel(IBaseRequest object) {
        String url = object.getUrl();
        url = Config.toMd5(url);
        List<Call> calls = httpApi.getClient().dispatcher().queuedCalls();
        for (Call call : calls) {
            if (call.request().tag().equals(url))
                call.cancel();
        }
        calls = httpApi.getClient().dispatcher().runningCalls();
        for (Call call : calls) {
            if (call.request().tag().equals(url))
                call.cancel();
        }
    }

    public Call post(BaseRequest call) {
        return httpApi.post(call);
    }

    public Call get(BaseRequest call) {
        return httpApi.get(call);
    }
}

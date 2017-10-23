package com.lynn.library.net;

import android.app.Application;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by lynn on 16/8/4.
 */
class HttpApi {
    private static OkHttpClient okHttpClient;
    private static HttpApi instance;
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String MULTIPART = "multipart/form-data";
    private static final MediaType JSON = MediaType.parse(APPLICATION_JSON + ";charset=utf-8");
    private static final String TAG = "Request";

    private HttpApi() {
    }

    static HttpApi getInstance() {
        if (instance == null) {
            synchronized (HttpApi.class) {
                if (instance == null) {
                    instance = new HttpApi();
                }
            }
        }
        return instance;
    }

    protected void init(Application context) {
        int cacheSize = 10 * 1024 * 1024;
        String path = context.getFilesDir().getAbsolutePath();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File external = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            if (external != null) {
                path = external.getAbsolutePath();
            }
        }
        File httpCacheDirectory = new File(path);
        Cache cache = new Cache(httpCacheDirectory, cacheSize);
        okHttpClient = new OkHttpClient().newBuilder().connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS)
                .followRedirects(true).retryOnConnectionFailure(true).cache(cache)
                .build();
    }

    OkHttpClient getClient() {
        return okHttpClient;
    }

    Call post(IBaseRequest callBack) {
        if (TextUtils.isEmpty(callBack.getUrl())) {
            throw new NullPointerException("url is null");
        }
        Request.Builder builder = new Request.Builder();
        if (null != callBack.getHeaders()) {
            Map<String, String> headers = callBack.getHeaders();
            for (Map.Entry<String, String> set : headers.entrySet()) {
                builder.addHeader(set.getKey(), set.getValue());
            }
        }
        List<FileParams> files = callBack.getFiles();
        Map<String, Object> params = callBack.getParams();
        String json = Config.GSON.toJson(params);
        log(Config.URL + callBack.getUrl() + " === " + json);
        RequestBody body;
        if (null == files || files.size() == 0) {
            builder.addHeader(CONTENT_TYPE, APPLICATION_JSON);
            body = RequestBody.create(JSON, json);
        } else {
            builder.addHeader(CONTENT_TYPE, MULTIPART);
            MultipartBody.Builder mpb = new MultipartBody.Builder();
            mpb.setType(MultipartBody.FORM);
            for (Map.Entry<String, Object> item : params.entrySet()) {
                mpb.addFormDataPart(item.getKey(), String.valueOf(item.getValue()));
            }
            for (int i = 0; i < files.size(); i++) {
                FileParams item = files.get(i);
                mpb.addFormDataPart(item.key, item.fileName, RequestBody.create(MediaType.parse(item.mediaType), item.bytes));
            }
            body = mpb.build();
        }
        builder.url(callBack.getUrl().startsWith("http") ? callBack.getUrl() : Config.URL + callBack.getUrl()).post(body);
        builder.tag(Config.toMd5(callBack.getUrl()));
        Request request = builder.build();
        Call call = null;
        try {
            call = okHttpClient.newCall(request);
            call.enqueue(callBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return call;
    }

    Call get(IBaseRequest callBack) {
        String url = callBack.getUrl();
        if (TextUtils.isEmpty(callBack.getUrl())) {
            throw new NullPointerException("url is null");
        }
        Map<String, Object> params = callBack.getParams();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> item : params.entrySet()) {
            String key = item.getKey();
            String value = item.getValue().toString();
            sb = (sb.length() == 0 ? sb.append("?") : sb.append("&")).append(key).append("=").append(value);
        }
        if (url.contains("?")) {
            sb = sb.replace(0, 1, "&");
        }
        url += sb.toString();
        Request.Builder builder = new Request.Builder();
        if (null != callBack.getHeaders()) {
            Map<String, String> headers = callBack.getHeaders();
            for (Map.Entry<String, String> set : headers.entrySet()) {
                builder.addHeader(set.getKey(), set.getValue());
            }
        }

        Request req = builder.url(url).get().build();
        Call call = okHttpClient.newCall(req);
        call.enqueue(callBack);
        return call;
    }

    private static void log(String log) {
        if (Config.DEBUG) {
            Log.e(TAG, log);
        }
    }
}

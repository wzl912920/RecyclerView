package com.lynn.library.db;


import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import org.greenrobot.greendao.database.Database;

/**
 * Created by lynn on 16/9/22.
 * cannot be called outside
 */
class DBHelper {
    private static DBHelper dbUtils;
    private Handler handler;

    private DBHelper() {
        handler = new Handler(Looper.getMainLooper());
    }

    public static DBHelper getInstance() {
        if (null == dbUtils) {
            synchronized (DBHelper.class) {
                if (null == dbUtils) {
                    dbUtils = new DBHelper();
                }
            }
        }
        return dbUtils;
    }

    public synchronized void post(BaseDBRequest request) {
        handler.post(new DbExecutor(request));
    }

    private static class DbExecutor implements Runnable {
        private static final String TAG = "DB_ERROR";
        private BaseDBRequest request;

        DbExecutor(BaseDBRequest request) {
            this.request = request;
        }

        @Override
        public void run() {
            if (null == request) return;
            if (!TextUtils.isEmpty(request.error)) {
                request.onError(request.error);
            } else {
                request.onSuccess(request.t);
            }
        }
    }
}

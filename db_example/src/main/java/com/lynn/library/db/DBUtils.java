package com.lynn.library.db;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.lynn.library.db.entity.DaoMaster;
import com.lynn.library.db.entity.DaoSession;

/**
 * Created by Lynn.
 */

public class DBUtils {
    private DBUtils() {
    }

    private static DBUtils instance;
    private DaoSession daoSession;

    public void init(Application application) {
        init(application, "cache.db");
    }

    public void init(Application application, String dbName) {
        DaoMaster.DevOpenHelper helper = new DbOpenHelper(application, dbName, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public static DBUtils getInstance() {
        if (instance == null) {
            synchronized (DBUtils.class) {
                if (null == instance) {
                    instance = new DBUtils();
                }
            }
        }
        return instance;
    }

    public void post(final BaseDBRequest request) {
        daoSession.startAsyncSession().runInTx(request);
    }
}

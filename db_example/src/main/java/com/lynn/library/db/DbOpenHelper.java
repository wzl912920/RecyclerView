package com.lynn.library.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.lynn.library.db.entity.DaoMaster.DevOpenHelper;

import org.greenrobot.greendao.database.Database;

/**
 * Created by Lynn.
 */

public class DbOpenHelper extends DevOpenHelper {
    public DbOpenHelper(Context context, String name) {
        super(context, name);
    }

    public DbOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        DBUpgradeExample.upgrade(db, oldVersion, newVersion);
    }
}

package com.lynn.library.db;

import com.lynn.library.db.entity.DaoMaster;

import org.greenrobot.greendao.database.Database;

/**
 * Created by Lynn.
 */

public class DBUpgradeExample {

    public static void upgrade(Database db, int oldVersion, int currentVersion) {
        switch (oldVersion) {
            case 0:
                DaoMaster.createAllTables(db, true);
                return;
            case 1:
                updateDBVersionTo2(db);
                break;
            default:
                break;
        }
        oldVersion += 1;
        if (oldVersion < currentVersion) {
            upgrade(db, oldVersion, currentVersion);
        }
    }

    private static void updateDBVersionTo2(Database db) {
        db.beginTransaction();
        try {
            db.execSQL("ALTER TABLE ARTICLE ADD COLUMN TAG_NAMES_RESERVED TEXT;");
        } catch (Exception e) {
        } finally {
            try {
                db.execSQL("ALTER TABLE ARTICLE ADD COLUMN IS_FAVORITE_USER INTEGER;");
            } catch (Exception e) {
            } finally {
                try {
                    db.execSQL("ALTER TABLE USER ADD COLUMN WE_CHAT TEXT;");
                } catch (Exception e) {
                } finally {
                    try {
                        db.execSQL("ALTER TABLE USER ADD COLUMN WEIBO TEXT;");
                    } catch (Exception e) {
                    } finally {
                        db.setTransactionSuccessful();
                        db.endTransaction();
                    }
                }
            }
        }
    }
}

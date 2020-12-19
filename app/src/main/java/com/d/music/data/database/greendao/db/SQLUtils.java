package com.d.music.data.database.greendao.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class SQLUtils {
    public static synchronized Cursor findBySQL(SQLiteDatabase db, String... sql) {
        checkConditionsCorrect(sql);
        if (db == null || sql == null) {
            return null;
        } else if (sql.length <= 0) {
            return null;
        } else {
            String[] selectionArgs;
            if (sql.length == 1) {
                selectionArgs = null;
            } else {
                selectionArgs = new String[sql.length - 1];
                System.arraycopy(sql, 1, selectionArgs, 0, sql.length - 1);
            }
            return db.rawQuery(sql[0], selectionArgs);
        }
    }

    private static void checkConditionsCorrect(String... conditions) {
        if (conditions != null) {
            int conditionsSize = conditions.length;
            if (conditionsSize > 0) {
                String whereClause = conditions[0];
                int placeHolderSize = count(whereClause, "?");
                if (conditionsSize != placeHolderSize + 1) {
                    throw new RuntimeException("The parameters in conditions are incorrect.");
                }
            }
        }
    }

    private static int count(String string, String mark) {
        if (!TextUtils.isEmpty(string) && !TextUtils.isEmpty(mark)) {
            int count = 0;
            for (int index = string.indexOf(mark); index != -1; index = string.indexOf(mark)) {
                ++count;
                string = string.substring(index + mark.length());
            }
            return count;
        } else {
            return 0;
        }
    }
}

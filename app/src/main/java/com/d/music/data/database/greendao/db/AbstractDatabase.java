package com.d.music.data.database.greendao.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.d.music.data.database.greendao.operation.AbstractOp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.AbstractDaoSession;

public abstract class AbstractDatabase<M extends AbstractDaoMaster, S extends AbstractDaoSession>
        extends AbstractOp {
    protected SQLiteDatabase mDatabase;
    protected M mDaoMaster;
    protected S mDaoSession;
    protected Gson mGson;

    protected AbstractDatabase(Context context) {
        mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    }
}

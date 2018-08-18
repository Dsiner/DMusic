package com.d.music.module.greendao.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.d.music.module.greendao.operation.AbstractOp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.AbstractDaoSession;

public abstract class AbstractDB<M extends AbstractDaoMaster, S extends AbstractDaoSession> extends AbstractOp {
    protected SQLiteDatabase db;
    protected M daoMaster;
    protected S daoSession;
    protected Gson gson;

    protected AbstractDB(Context context) {
        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    }
}

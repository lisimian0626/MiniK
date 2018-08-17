package com.beidousat.minidoor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.beidousat.libbns.util.Logger;

/**
 * Created by J Wong on 2017/7/10.
 */

public class BnsDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ad_play.db";//数据库名称
    private static final int SCHEMA_VERSION = 1;//版本号,则是升级之后的,升级方法请看onUpgrade方法里面的判断

    public BnsDbHelper(Context context) {//构造函数,接收上下文作为参数,直接调用的父类的构造函数
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {//创建的是一个午餐订餐的列表,id,菜名,地址等等
        db.execSQL("CREATE TABLE tb_ad_statistics (_id INTEGER PRIMARY KEY AUTOINCREMENT, file_path TEXT, times INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == 2) {//升级判断,如果再升级就要再加两个判断,从1到3,从2到3
            //  db.execSQL("ALTER TABLE tb_ad_statistics ADD phone TEXT;");
        }
    }

    public Cursor getAllTimeAsc() {//返回表中的数据,where是调用时候传进来的搜索内容,orderby是设置中传进来的列表排序类型
//        StringBuilder buf = new StringBuilder("SELECT _id, file_path, times FROM tb_ad_statistics");
     /*   if (where != null) {
            buf.append(" WHERE ");
            buf.append(where);
        }
        if (orderBy != null) {
            buf.append(" ORDER BY ");
            buf.append(orderBy);
        }
        */
//        return (getReadableDatabase().query("tb_ad_statistics", null, null, null,null));
        return getReadableDatabase().query("tb_ad_statistics", new String[]{"_id", "file_path", "times"}, null, null, null, null, "times desc");

    }

    public Cursor getById(String id) {//根据点击事件获取id,查询数据库
        String[] args = {id};
        return (getReadableDatabase().rawQuery("SELECT _id, file_path, times FROM tb_ad_statistics WHERE _ID=?", args));
    }

    public Cursor getByFilePath(String path) {//根据点击事件获取id,查询数据库
        String[] args = {path};
        return (getReadableDatabase().rawQuery("SELECT _id, file_path, times FROM tb_ad_statistics WHERE file_path=?", args));
    }


    public void insert(String file_path, int times) {
        ContentValues cv = new ContentValues();
        cv.put("file_path", file_path);
        cv.put("times", times);
        Logger.d("BnsDbHelper", "insert  file_path:" + file_path + "  times:" + times);
        getWritableDatabase().insert("tb_ad_statistics", "file_path", cv);
    }

    public void update(String id, String file_path, int times) {
        ContentValues cv = new ContentValues();
        String[] args = {id};
        cv.put("file_path", file_path);
        cv.put("times", times);
        Logger.d("BnsDbHelper", "update id:" + id + " file_path:" + file_path + "  times:" + times);
        getWritableDatabase().update("tb_ad_statistics", cv, "_ID=?", args);
    }

    public void delete(String filePath) {
        int count = getWritableDatabase().delete("tb_ad_statistics", " file_path=?", new String[]{filePath});
        Logger.d("BnsDbHelper", "delete  file_path:" + filePath + "  count:" + count);
    }


//    public String getName(Cursor c) {
//        return (c.getString(1));
//    }
//
//    public String gettimes(Cursor c) {
//        return (c.getString(2));
//    }
//
//    public String getType(Cursor c) {
//        return (c.getString(3));
//    }
//
//    public String getNotes(Cursor c) {
//        return (c.getString(4));
//    }
//
//    public String getPhone(Cursor c) {
//        return (c.getString(5));
//    }
}
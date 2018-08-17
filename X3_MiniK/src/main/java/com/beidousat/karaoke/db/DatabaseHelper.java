package com.beidousat.karaoke.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.beidousat.karaoke.db.service.SongDao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "SongCache.db"; //数据库名称
	private static final int DB_VERSION = 1; //数据库版本

	private final SQLiteDatabase writableDatabase;
	private final SQLiteDatabase readableDatabase;
	private final SongDao mSongDao;

	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		writableDatabase = getWritableDatabase();
		readableDatabase = getReadableDatabase();
		this.mSongDao = new SongDao(writableDatabase, readableDatabase);
		copyDatabaseFile(context);
	}

	public void init() {
	}

	private void copyDatabaseFile(Context context) {
		File dbFile = context.getDatabasePath(DB_NAME);
		Log.e("test","dbFile:"+dbFile.getAbsolutePath());
		if (dbFile.exists()&&mSongDao.isDBfileExist()>0) {
			return;
		}
		Log.e("test","复制数据库");
		InputStream in = null;
		FileOutputStream out = null;
		try {
			in = context.getAssets().open(DB_NAME);
			out = new FileOutputStream(dbFile);

			byte buf[] = new byte[1024*1024];
			int len = 0;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.flush();
		} catch (Exception e) {
//			dbFile.delete();
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
//		createAllTables(db, false);
	}

	private void createAllTables(SQLiteDatabase db, boolean ifNotExists) {
//		SongDao.createTable(db, ifNotExists);
	}

	public static void dropAllTables(SQLiteDatabase db, boolean ifExists) {
//		SongDao.dropTable(db, ifExists);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public SongDao getSongDao() {
		return mSongDao;
	}
}

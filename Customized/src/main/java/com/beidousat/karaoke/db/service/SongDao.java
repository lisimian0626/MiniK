package com.beidousat.karaoke.db.service;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.beidousat.karaoke.model.Song;
import com.beidousat.libbns.util.Logger;

import java.util.ArrayList;
import java.util.List;

public class SongDao extends BaseDao<Song> {
    private static final String TAG = "SongDao";
	public static final String TABLE_NAME = "TB_Song_1w";

	public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
		String constraint = ifNotExists ? "IF NOT EXISTS " : "";
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("CREATE TABLE ");
		sqlBuffer.append(constraint);
		sqlBuffer.append(TABLE_NAME);
		sqlBuffer.append(" (");
		sqlBuffer.append("'SongFilePath' TEXT NOT NULL ,");
		sqlBuffer.append("'Hot' INT(11) NOT NULL DEFAULT '0'");
		sqlBuffer.append(")");

		String sql = sqlBuffer.toString();
		Logger.i("execSQL sql = " + sql, TAG);
		db.execSQL(sql);
	}

	public static void dropTable(SQLiteDatabase db, boolean ifExists) {
		String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'" + TABLE_NAME + "'";
		Logger.i("execSQL sql = " + sql, TAG);
		db.execSQL(sql);
	}

	public SongDao(SQLiteDatabase writableDatabase, SQLiteDatabase readableDatabase) {
		super(writableDatabase, readableDatabase);
	}

	@Override
	public Song queryEntity(long id) {
		return null;
	}

	@Override
	public List<Song> queryEntityBuilder(String sql) {
		List<Song> searchList = new ArrayList<Song>();
		Song entity = null;
		Cursor cursor = null;
		try {
			cursor = mReadableDatabase.rawQuery("select * from " + TABLE_NAME + " T " + sql, null);
			if (cursor.moveToFirst()) {
				do {
					entity = new Song();
					readEntity(cursor, entity);
					searchList.add(entity);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return searchList;
	}

	private void readEntity(Cursor cursor, Song entity) throws Exception {
		try {
			entity.SongFilePath = cursor.getString(cursor.getColumnIndex("SongFilePath"));
			entity.Hot = cursor.getInt(cursor.getColumnIndex("Hot"));
		} catch (Exception e) {
			throw new Exception("列不存在", e);
		}
	}

	@Override
	public List<Song> queryAllEntityBuilder() {
		return null;
	}

	@Override
	public boolean insertEntity(Song entity) {
		return insertOrReplaceEntity(entity);
	}

	@NonNull
	private ContentValues getContentValues(Song entity) {
		ContentValues cv = new ContentValues();
		cv.put("SongFilePath", entity.SongFilePath);
		cv.put("Hot", entity.Hot);
		return cv;
	}

	@Override
	public boolean insertOrReplaceEntity(Song entity) {
		ContentValues cv = getContentValues(entity);
		long rowID = mWritableDatabase.replace(TABLE_NAME, null, cv);
		return rowID != -1;
	}

	@Override
	public boolean updateEntity(Song entity) {
		ContentValues cv = getContentValues(entity);
		mWritableDatabase.update(TABLE_NAME, cv, "SongFilePath=?", new String[] {entity.SongFilePath});
		return false;
	}

	@Override
	public boolean deleteEntity(String songFiltPath) {
		String where = "SongFilePath=?";
		return mWritableDatabase.delete(TABLE_NAME, where, new String[]{songFiltPath}) != 0;
	}

	public List<String> deleteLessHotSongs() {
		//DELETE from "_TB_Song_1w_old_20170510" where SongFilePath IN
		// (SELECT SongFilePath from "_TB_Song_1w_old_20170510" ORDER BY Hot asc LIMIT 10);
		List<String> deletePaths = null;
		try {
			mWritableDatabase.beginTransaction();
			deletePaths = queryDeletePaths();

			if (deletePaths != null && deletePaths.size() > 0) {
//				String[] whereArgs = new String[deletePaths.size()];
//				whereArgs = deletePaths.toArray(whereArgs);
				mWritableDatabase.delete(TABLE_NAME, "Hot < ?", new String[]{"10"});
			}
			mWritableDatabase.setTransactionSuccessful();

		} catch (Exception e) {
			Logger.e(TAG, e.toString());
			deletePaths = null;
		} finally {
			mWritableDatabase.endTransaction();
		}

		return deletePaths;
	}

	private List<String> queryDeletePaths() {
		List<String> deletePaths = new ArrayList<>();
		try {
			Cursor cursor = mWritableDatabase.query(TABLE_NAME,
					new String[] {"SongFilePath"}, "Hot<?", new String[]{"10"}, null, null, null);
			if (cursor.moveToFirst()) {
				do {
					deletePaths.add(cursor.getString(cursor.getColumnIndex("SongFilePath")));
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Logger.e(TAG, e.toString());
			deletePaths.clear();
		}

		return deletePaths;
	}

	public void increaseSongHot(String songPath) {
		try {
			mWritableDatabase.beginTransaction();
			List<Song> songs = queryEntityBuilder("where songFilePaht = " + songPath);
			for (Song s : songs) {
				s.Hot += 1;
				updateEntity(s);
			}
			mWritableDatabase.setTransactionSuccessful();
		} catch (Exception e) {
			Logger.e(TAG, e.toString());
		} finally {
			mWritableDatabase.endTransaction();
		}
	}

	public boolean insertSong(String songPath, int hot) {
		Song song = new Song();
		song.SongFilePath = songPath;
		song.Hot = hot;

		return insertOrReplaceEntity(song);
	}

	@Override
	public boolean deleteAllEntity() {
		return false;
	}
}

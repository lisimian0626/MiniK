package com.beidousat.karaoke.db.service;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

public abstract class BaseDao<E> {

	protected final SQLiteDatabase mWritableDatabase;
	protected final SQLiteDatabase mReadableDatabase;


	public BaseDao(SQLiteDatabase writableDatabase, SQLiteDatabase readableDatabase) {
		this.mWritableDatabase = writableDatabase;
		this.mReadableDatabase = readableDatabase;
	}

	public abstract E queryEntity(long id);

	public abstract List<E> queryEntityBuilder(String sql);

	public abstract List<E> queryAllEntityBuilder();

	public abstract boolean insertEntity(E entity);

	public abstract boolean insertOrReplaceEntity(E entity);

	public abstract boolean updateEntity(E entity);

	public abstract boolean deleteEntity(String songFilePath);

	public abstract boolean deleteAllEntity();
}

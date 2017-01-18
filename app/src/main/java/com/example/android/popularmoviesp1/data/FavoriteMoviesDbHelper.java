package com.example.android.popularmoviesp1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by vinsce on 18/01/17 at 21.03.
 *
 * @author vinsce
 */

public class FavoriteMoviesDbHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "favorite_movies.db";

	private static final int DATABASE_VERSION = 1;

	public FavoriteMoviesDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {

		final String SQL_CREATE_TABLE = "CREATE TABLE " + MoviesContract.FavoriteMoviesEntry.TABLE_NAME + " (" +
				MoviesContract.FavoriteMoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				MoviesContract.FavoriteMoviesEntry.COLUMN_TITLE + " STRING NOT NULL, " +
				MoviesContract.FavoriteMoviesEntry.COLUMN_POSTER_URL + " STRING NOT NULL," +
				MoviesContract.FavoriteMoviesEntry.COLUMN_SYNOPSIS + " STRING NOT NULL, " +
				MoviesContract.FavoriteMoviesEntry.COLUMN_RATING + " REAL NOT NULL, " +
				MoviesContract.FavoriteMoviesEntry.COLUMN_RELEASE_DATE + " INTEGER NOT NULL, " +
				" UNIQUE (" + MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

		sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.FavoriteMoviesEntry.TABLE_NAME);
		onCreate(sqLiteDatabase);
	}
}
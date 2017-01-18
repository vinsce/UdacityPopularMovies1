package com.example.android.popularmoviesp1.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Created by vinsce on 18/01/17 at 21.10.
 *
 * @author vinsce
 */

public class FavoriteMoviesProvider extends ContentProvider {

	public static final int CODE_FAVORITE_MOVIES = 100;
	public static final int CODE_FAVORITE_MOVIES_WITH_MOVIE_ID = 101;

	private static final UriMatcher sUriMatcher = buildUriMatcher();
	private FavoriteMoviesDbHelper mOpenHelper;


	public static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = MoviesContract.CONTENT_AUTHORITY;

		matcher.addURI(authority, MoviesContract.PATH_FAVORITE_MOVIES, CODE_FAVORITE_MOVIES);
		matcher.addURI(authority, MoviesContract.PATH_FAVORITE_MOVIES + "/*", CODE_FAVORITE_MOVIES_WITH_MOVIE_ID);

		return matcher;
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new FavoriteMoviesDbHelper(getContext());
		return true;
	}

	@Override
	public Uri insert(@NonNull Uri uri, ContentValues values) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		switch (sUriMatcher.match(uri)) {
			case CODE_FAVORITE_MOVIES:
				long _id = db.insert(MoviesContract.FavoriteMoviesEntry.TABLE_NAME, null, values);

				if (_id != -1) {
					getContext().getContentResolver().notifyChange(uri, null);
				}

				return uri.buildUpon().appendPath(values.getAsString(MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID)).build();
			default:
				throw new RuntimeException("Invalid Uri for insertion");
		}
	}

	@Override
	public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		Cursor cursor;

		switch (sUriMatcher.match(uri)) {
			case CODE_FAVORITE_MOVIES_WITH_MOVIE_ID: {

				String movieId = uri.getLastPathSegment();

				String[] selectionArguments = new String[]{movieId};

				cursor = mOpenHelper.getReadableDatabase().query(
						MoviesContract.FavoriteMoviesEntry.TABLE_NAME,
						projection,
						MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID + " = ? ",
						selectionArguments,
						null,
						null,
						sortOrder);

				break;
			}
			case CODE_FAVORITE_MOVIES: {
				cursor = mOpenHelper.getReadableDatabase().query(
						MoviesContract.FavoriteMoviesEntry.TABLE_NAME,
						projection,
						selection,
						selectionArgs,
						null,
						null,
						sortOrder);

				break;
			}

			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}

		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
		int numRowsDeleted;

		switch (sUriMatcher.match(uri)) {
			case CODE_FAVORITE_MOVIES_WITH_MOVIE_ID:
				numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
						MoviesContract.FavoriteMoviesEntry.TABLE_NAME,
						selection,
						selectionArgs);
				break;

			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}

		if (numRowsDeleted != 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}

		return numRowsDeleted;
	}

	@Override
	public String getType(@NonNull Uri uri) {
		throw new RuntimeException("Operation not supported");
	}

	@Override
	public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		throw new RuntimeException("Operation not supported");
	}

	@Override
	@TargetApi(11)
	public void shutdown() {
		mOpenHelper.close();
		super.shutdown();
	}
}
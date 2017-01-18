package com.example.android.popularmoviesp1.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by vinsce on 18/01/17 at 20.53.
 *
 * @author vinsce
 */

public class MoviesContract {

	public static final String CONTENT_AUTHORITY = "com.example.android.popularmoviesp1";

	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	public static final String PATH_FAVORITE_MOVIES = "favoriteMovies";

	public static final class FavoriteMoviesEntry implements BaseColumns {

		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE_MOVIES).build();

		public static final String TABLE_NAME = "favoriteMovies";

		public static final String COLUMN_MOVIE_ID = "movieId";
		public static final String COLUMN_TITLE = "title";
		public static final String COLUMN_POSTER_URL = "posterUrl";
		public static final String COLUMN_SYNOPSIS = "synopsis";
		public static final String COLUMN_RATING = "rating";
		public static final String COLUMN_RELEASE_DATE = "releaseDate";

		public static Uri buildMovieUriWithMovieId(String movieId) {
			return CONTENT_URI.buildUpon().appendPath(movieId).build();
		}
	}
}
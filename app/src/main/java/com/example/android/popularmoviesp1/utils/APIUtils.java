package com.example.android.popularmoviesp1.utils;

import android.net.Uri;

import com.example.android.popularmoviesp1.model.Movie;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by vinsce on 13/01/17 at 1.14.
 *
 * @author vinsce
 */

public class APIUtils {
	private static String BASE_MOVIES_API_URL = "http://api.themoviedb.org/3/movie/";
	private static String MOST_POPULAR_MOVIES_PATH = "popular";
	private static String TOP_RATED_MOVIES_PATH = "top_rated";
	private static String API_KEY_PARAM = "api_key";

	// Images
	private static String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
	private static String IMAGE_SIZE_CODE = "w185";


	public static Uri getURLForImage(String relativeImagePath) {
		return Uri.parse(BASE_IMAGE_URL).buildUpon().appendPath(IMAGE_SIZE_CODE).appendEncodedPath(relativeImagePath).build();
	}

	public static URL getMoviesURL(String apiKey, SortOption sortOption) {
		String sortingPath;
		if (sortOption == SortOption.MOST_POPULAR)
			sortingPath = MOST_POPULAR_MOVIES_PATH;
		else
			sortingPath = TOP_RATED_MOVIES_PATH;

		Uri builtUri = Uri.parse(BASE_MOVIES_API_URL).buildUpon().appendPath(sortingPath).appendQueryParameter(API_KEY_PARAM, apiKey).build();

		URL url = null;
		try {
			url = new URL(builtUri.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}

	public static Movie[] getMovieArrayFromNetworkResponse(String response) {
		try {
			JSONObject mainJson = new JSONObject(response);
			JSONArray moviesArray = mainJson.getJSONArray("results");

			Movie[] resultArray = new Movie[moviesArray.length()];

			Movie tmpMovie;
			Gson gson = new Gson();
			for (int i = 0; i < moviesArray.length(); i++) {
				tmpMovie = gson.fromJson(moviesArray.getString(i), Movie.class);
				resultArray[i] = tmpMovie;
			}

			return resultArray;

		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

	}

	public enum SortOption {MOST_POPULAR, TOP_RATED}
}

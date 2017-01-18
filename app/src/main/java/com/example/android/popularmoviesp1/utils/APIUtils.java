package com.example.android.popularmoviesp1.utils;

import android.net.Uri;
import android.util.Log;

import com.example.android.popularmoviesp1.model.Movie;
import com.example.android.popularmoviesp1.model.Trailer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

	private static String TRAILERS_PATH = "videos";

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
			Log.d("JSON", mainJson.toString());
			JSONArray moviesArray = mainJson.getJSONArray("results");

			Movie[] resultArray = new Movie[moviesArray.length()];

			Movie tmpMovie;
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

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

	public static Trailer[] getTrailerArrayFromNetworkResponse(String response) {
		try {
			JSONObject mainJson = new JSONObject(response);
			JSONArray trailersArray = mainJson.getJSONArray("results");

			Trailer[] resultArray = new Trailer[trailersArray.length()];

			Trailer tmpTrailer;

			Gson gson = new Gson();

			for (int i = 0; i < trailersArray.length(); i++) {
				tmpTrailer = gson.fromJson(trailersArray.getString(i), Trailer.class);
				resultArray[i] = tmpTrailer;
			}

			return resultArray;

		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

	}

	public enum SortOption {MOST_POPULAR, TOP_RATED}

	public static URL getTrailersURL(String apiKey, String movieId) {
		Uri builtUri = Uri.parse(BASE_MOVIES_API_URL).buildUpon().appendPath(movieId).appendPath(TRAILERS_PATH).appendQueryParameter(API_KEY_PARAM, apiKey).build();

		URL url = null;
		try {
			url = new URL(builtUri.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}
}

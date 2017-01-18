package com.example.android.popularmoviesp1;

import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmoviesp1.adapters.MovieAdapterOnClickHandler;
import com.example.android.popularmoviesp1.adapters.MoviesCursorGridAdapter;
import com.example.android.popularmoviesp1.adapters.MoviesGridAdapter;
import com.example.android.popularmoviesp1.data.MoviesContract;
import com.example.android.popularmoviesp1.model.Movie;
import com.example.android.popularmoviesp1.utils.APIUtils;
import com.example.android.popularmoviesp1.utils.Networking;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieAdapterOnClickHandler {
	private static final String SORT_OPTION_STATE_KEY = "SORTING";
	private static final String MOVIES_STATE_KEY = "MOVIES";

	private RecyclerView mRecyclerView;

	private MoviesGridAdapter mMoviesAdapter;
	private MoviesCursorGridAdapter mFavoriteMoviesAdapter;

	private TextView mErrorMessageDisplay;
	private ProgressBar mLoadingIndicator;

	private APIUtils.SortOption mSortOption = APIUtils.SortOption.MOST_POPULAR;

	private Movie[] mMovies;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(SORT_OPTION_STATE_KEY))
				mSortOption = APIUtils.SortOption.values()[savedInstanceState.getInt(SORT_OPTION_STATE_KEY)];
			if (savedInstanceState.containsKey(MOVIES_STATE_KEY))
				mMovies = (Movie[]) savedInstanceState.getParcelableArray(MOVIES_STATE_KEY);
		}

		setContentView(R.layout.activity_main);
		//init views
		mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);
		mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error);
		GridLayoutManager layoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.main_grid_columns), GridLayoutManager.VERTICAL, false);

		mMoviesAdapter = new MoviesGridAdapter(this);
		mFavoriteMoviesAdapter = new MoviesCursorGridAdapter(this);

		//Listening for favorites updates
		Uri uriToQuery = MoviesContract.BASE_CONTENT_URI.buildUpon().appendPath(MoviesContract.PATH_FAVORITE_MOVIES).build();
		getContentResolver().registerContentObserver(uriToQuery, true, new ContentObserver(new Handler()) {
			@Override
			public void onChange(boolean selfChange) {
				super.onChange(selfChange);
				if (mSortOption == APIUtils.SortOption.FAVORITES && mFavoriteMoviesAdapter != null)
					loadMovies();
			}
		});

		mRecyclerView.setLayoutManager(layoutManager);
		mRecyclerView.setHasFixedSize(true);

		if (mSortOption == APIUtils.SortOption.FAVORITES)
			mRecyclerView.setAdapter(mFavoriteMoviesAdapter);
		else
			mRecyclerView.setAdapter(mMoviesAdapter);

		mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

		if (mSortOption == APIUtils.SortOption.FAVORITES) {
			loadMovies();
		} else {
			if (mMovies == null)
				loadMovies();
			else mMoviesAdapter.setMoviesList(mMovies);
		}
	}

	private void loadMovies() {
		if (mSortOption == APIUtils.SortOption.FAVORITES) {
			loadFavoritesMovies();
		} else if (Networking.isNetworkAvailable(this))
			new FetchMovies().execute(getString(R.string.api_key));
		else {
			showErrorMessage();
			Toast.makeText(this, R.string.network_unavailable, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);

		MenuItem mostPopularItem = menu.findItem(R.id.action_sort_popular);
		MenuItem topRatedItem = menu.findItem(R.id.action_sort_rating);
		MenuItem favoritesItem = menu.findItem(R.id.action_favorites);

		switch (mSortOption) {
			case MOST_POPULAR:
				mostPopularItem.setChecked(true);
				break;
			case TOP_RATED:
				topRatedItem.setChecked(true);
				break;
			case FAVORITES:
				favoritesItem.setChecked(true);
				break;
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_sort_popular:
				item.setChecked(true);
				if (mSortOption != APIUtils.SortOption.MOST_POPULAR) {
					mSortOption = APIUtils.SortOption.MOST_POPULAR;
					loadMovies();
				}
				return true;
			case R.id.action_sort_rating:
				if (mSortOption != APIUtils.SortOption.TOP_RATED) {
					mSortOption = APIUtils.SortOption.TOP_RATED;
					loadMovies();
				}
				item.setChecked(true);
				return true;
			case R.id.action_favorites:
				if (mSortOption != APIUtils.SortOption.FAVORITES) {
					mSortOption = APIUtils.SortOption.FAVORITES;
					loadMovies();
				}
				item.setChecked(true);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(Movie movie) {
		Intent intentToStartDetailActivity = new Intent(this, DetailsActivity.class);
		intentToStartDetailActivity.putExtra(DetailsActivity.MOVIE_ARG_KEY, movie);
		startActivity(intentToStartDetailActivity);
	}

	private void loadFavoritesMovies() {
		Uri uriToQuery = MoviesContract.BASE_CONTENT_URI.buildUpon().appendPath(MoviesContract.PATH_FAVORITE_MOVIES).build();
		Cursor cursor = getContentResolver().query(uriToQuery, null, null, null, null);
		if (cursor != null) {
			mFavoriteMoviesAdapter.swapCursor(cursor);
		}
		mRecyclerView.setAdapter(mFavoriteMoviesAdapter);
		showData();
	}

	public class FetchMovies extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressBar();
		}

		@Override
		protected String doInBackground(String... params) {

			/* the API KEY is passed here **/
			if (params.length == 0) {
				return null;
			}
			String apiKey = params[0];

			URL moviesUrl = APIUtils.getMoviesURL(apiKey, mSortOption);

			try {
				return Networking.getResponseFromHttpUrl(moviesUrl);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(String data) {
			if (data != null) {
				Log.d(MainActivity.class.getSimpleName(), data);
				mMovies = APIUtils.getMovieArrayFromNetworkResponse(data);
				if (mMovies == null) showErrorMessage();
				else {
					mMoviesAdapter.setMoviesList(mMovies);
					mRecyclerView.setAdapter(mMoviesAdapter);
					showData();
				}
			} else showErrorMessage();
		}
	}

	private void showProgressBar() {
		mLoadingIndicator.setVisibility(View.VISIBLE);
		mErrorMessageDisplay.setVisibility(View.INVISIBLE);
		mRecyclerView.setVisibility(View.INVISIBLE);
	}

	private void showErrorMessage() {
		mRecyclerView.setVisibility(View.INVISIBLE);
		mLoadingIndicator.setVisibility(View.INVISIBLE);
		mErrorMessageDisplay.setVisibility(View.VISIBLE);
	}

	private void showData() {
		mLoadingIndicator.setVisibility(View.INVISIBLE);
		mErrorMessageDisplay.setVisibility(View.INVISIBLE);
		mRecyclerView.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt(SORT_OPTION_STATE_KEY, mSortOption.ordinal());
		if (mMovies != null)
			outState.putParcelableArray(MOVIES_STATE_KEY, mMovies);
	}
}

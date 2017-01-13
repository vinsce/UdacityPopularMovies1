package com.example.android.popularmoviesp1;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.example.android.popularmoviesp1.model.Movie;
import com.example.android.popularmoviesp1.utils.APIUtils;
import com.example.android.popularmoviesp1.utils.Networking;
import com.google.gson.Gson;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements MoviesGridAdapter.MovieAdapterOnClickHandler {

	private RecyclerView mRecyclerView;

	private MoviesGridAdapter mAdapter;

	private TextView mErrorMessageDisplay;
	private ProgressBar mLoadingIndicator;

	private APIUtils.SortOption mSortOption = APIUtils.SortOption.MOST_POPULAR;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//init views
		mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);
		mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error);
		GridLayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
		mAdapter = new MoviesGridAdapter(this);

		mRecyclerView.setLayoutManager(layoutManager);
		mRecyclerView.setAdapter(mAdapter);

		mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
		loadMovies();
	}

	private void loadMovies() {
		if (Networking.isNetworkAvailable(this))
			new FetchMovies().execute(getString(R.string.api_key));
		else
			Toast.makeText(this, R.string.network_unavailable, Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
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
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(Movie movie) {
		Intent intentToStartDetailActivity = new Intent(this, DetailsActivity.class);
		intentToStartDetailActivity.putExtra(DetailsActivity.MOVIE_JSON_ARG_KEY, new Gson().toJson(movie));
		startActivity(intentToStartDetailActivity);
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
				Movie[] movies = APIUtils.getMovieArrayFromNetworkResponse(data);
				if (movies == null) showErrorMessage();
				else {
					mAdapter.setMoviesList(movies);
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


}

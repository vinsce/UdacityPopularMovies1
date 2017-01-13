package com.example.android.popularmoviesp1;

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

import com.example.android.popularmoviesp1.utils.APIUtils;
import com.example.android.popularmoviesp1.utils.Networking;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

	private RecyclerView mRecyclerView;
	private TextView mErrorMessageDisplay;
	private ProgressBar mLoadingIndicator;

	private APIUtils.SortOption mSortOption;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//init views
		mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);
		mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error);
		GridLayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);

		mRecyclerView.setLayoutManager(layoutManager);
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
				showData();
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

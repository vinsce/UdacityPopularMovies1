package com.example.android.popularmoviesp1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.popularmoviesp1.utils.APIUtils;
import com.example.android.popularmoviesp1.utils.Networking;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

	private APIUtils.SortOption mSortOption;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		loadMovies();
	}

	private void loadMovies() {
		new FetchMovies().execute(getString(R.string.api_key));
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
			Log.d(MainActivity.class.getSimpleName(), data);
		}
	}
}

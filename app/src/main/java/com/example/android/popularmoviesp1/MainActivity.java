package com.example.android.popularmoviesp1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.android.popularmoviesp1.utils.APIUtils;
import com.example.android.popularmoviesp1.utils.Networking;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		new FetchMovies().execute(getString(R.string.api_key));
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

			URL moviesUrl = APIUtils.getMostPopularMoviesURL(apiKey);

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

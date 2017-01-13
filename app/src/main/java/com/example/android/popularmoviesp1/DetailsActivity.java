package com.example.android.popularmoviesp1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.android.popularmoviesp1.model.Movie;
import com.google.gson.Gson;

public class DetailsActivity extends AppCompatActivity {
	public static final String MOVIE_JSON_ARG_KEY = "MOVIE_JSON";

	private Movie mMovie;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getIntent() != null && getIntent().hasExtra(MOVIE_JSON_ARG_KEY)) {
			String movieString = getIntent().getStringExtra(MOVIE_JSON_ARG_KEY);
			mMovie = new Gson().fromJson(movieString, Movie.class);
		} else {
			Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		setContentView(R.layout.activity_details);

		Log.d(getClass().getSimpleName(), mMovie.toString());
	}
}

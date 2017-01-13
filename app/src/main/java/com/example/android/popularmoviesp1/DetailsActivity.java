package com.example.android.popularmoviesp1;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmoviesp1.model.Movie;
import com.example.android.popularmoviesp1.utils.APIUtils;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class DetailsActivity extends AppCompatActivity {
	public static final String MOVIE_JSON_ARG_KEY = "MOVIE_JSON";

	private Movie mMovie;

	private TextView mTitleTextView, mPlotTextView, mDateTextView, mRatingTextView;
	private ImageView mImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null && savedInstanceState.containsKey(MOVIE_JSON_ARG_KEY)) {
			String movieString = savedInstanceState.getString(MOVIE_JSON_ARG_KEY);
			mMovie = new Gson().fromJson(movieString, Movie.class);
		}
		if (getIntent() != null && getIntent().hasExtra(MOVIE_JSON_ARG_KEY)) {
			String movieString = getIntent().getStringExtra(MOVIE_JSON_ARG_KEY);
			mMovie = new Gson().fromJson(movieString, Movie.class);
		} else {
			Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		setContentView(R.layout.activity_details);

		mTitleTextView = (TextView) findViewById(R.id.tv_title);
		mPlotTextView = (TextView) findViewById(R.id.tv_plot);
		mDateTextView = (TextView) findViewById(R.id.tv_date);
		mRatingTextView = (TextView) findViewById(R.id.tv_rating);
		mImageView = (ImageView) findViewById(R.id.image);

		ViewCompat.setElevation(mTitleTextView, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));

		populateView();
	}

	void populateView() {
		mTitleTextView.setText(mMovie.getTitle());
		mPlotTextView.setText(mMovie.getPlotSynopsis());

		String ratingText = String.valueOf(mMovie.getUserRating()) + "/10";
		mRatingTextView.setText(ratingText);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(mMovie.getReleaseDate().getTime());
		mDateTextView.setText(String.valueOf(calendar.get(Calendar.YEAR)));

		Picasso.with(this).load(APIUtils.getURLForImage(mMovie.getImageUrl())).into(mImageView);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString(MOVIE_JSON_ARG_KEY, new Gson().toJson(mMovie));
	}
}

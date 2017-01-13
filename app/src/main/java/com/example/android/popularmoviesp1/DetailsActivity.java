package com.example.android.popularmoviesp1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmoviesp1.model.Movie;
import com.example.android.popularmoviesp1.utils.APIUtils;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {
	public static final String MOVIE_JSON_ARG_KEY = "MOVIE_JSON";

	private Movie mMovie;

	private TextView mTitleTextView, mPlotTextView, mDateTextView, mRatingTextView;
	private ImageView mImageView;

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

		mTitleTextView = (TextView) findViewById(R.id.tv_title);
		mPlotTextView = (TextView) findViewById(R.id.tv_plot);
		mDateTextView = (TextView) findViewById(R.id.tv_date);
		mRatingTextView = (TextView) findViewById(R.id.tv_rating);
		mImageView = (ImageView) findViewById(R.id.image);

		populateView();
	}

	void populateView() {
		mTitleTextView.setText(mMovie.getTitle());
		mPlotTextView.setText(mMovie.getPlotSynopsis());
		mDateTextView.setText(String.valueOf(mMovie.getReleaseDate().getYear()));
		mRatingTextView.setText(String.valueOf(mMovie.getUserRating()));

		Picasso.with(this).load(APIUtils.getURLForImage(mMovie.getImageUrl())).into(mImageView);
	}
}

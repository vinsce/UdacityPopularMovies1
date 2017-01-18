package com.example.android.popularmoviesp1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmoviesp1.model.Movie;
import com.example.android.popularmoviesp1.model.Trailer;
import com.example.android.popularmoviesp1.utils.APIUtils;
import com.example.android.popularmoviesp1.utils.Networking;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.Calendar;

public class DetailsActivity extends AppCompatActivity implements TrailersListAdapter.TrailerAdapterOnClickHandler {
	public static final String MOVIE_JSON_ARG_KEY = "MOVIE_JSON";

	private Movie mMovie;
	private Trailer[] mTrailers;

	private RecyclerView mTrailersRecyclerView;
	private TrailersListAdapter mAdapter;

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

		mTrailersRecyclerView = (RecyclerView) findViewById(R.id.rv_trailers);
		mTrailersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mTrailersRecyclerView.setNestedScrollingEnabled(false);
		mTrailersRecyclerView.setHasFixedSize(true);

		mAdapter = new TrailersListAdapter(this);
		mTrailersRecyclerView.setAdapter(mAdapter);

		ViewCompat.setElevation(mTitleTextView, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));
		new FetchTrailers().execute(getString(R.string.api_key));
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

	@Override
	public void onClick(Trailer trailer) {

	}


	public class FetchTrailers extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			/* the API KEY is passed here **/
			if (params.length == 0) {
				return null;
			}
			String apiKey = params[0];

			URL moviesUrl = APIUtils.getTrailersURL(apiKey, mMovie.getId());

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

			if (data != null) {
				mTrailers = APIUtils.getTrailerArrayFromNetworkResponse(data);
				mAdapter.setTrailersList(mTrailers);
			}
		}
	}
}

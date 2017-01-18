package com.example.android.popularmoviesp1;

import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import com.example.android.popularmoviesp1.databinding.ActivityDetailsBinding;
import com.example.android.popularmoviesp1.model.Movie;
import com.example.android.popularmoviesp1.model.Trailer;
import com.example.android.popularmoviesp1.utils.APIUtils;
import com.example.android.popularmoviesp1.utils.Networking;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.Calendar;

public class DetailsActivity extends AppCompatActivity implements TrailersListAdapter.TrailerAdapterOnClickHandler {
	public static final String MOVIE_ARG_KEY = "MOVIE";
	private static final String TRAILERS_ARG_KEY = "TRAILERS";

	private Movie mMovie;
	private Trailer[] mTrailers;

	private TrailersListAdapter mAdapter;

	ActivityDetailsBinding mBinding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null && savedInstanceState.containsKey(MOVIE_ARG_KEY)) {
			mMovie = savedInstanceState.getParcelable(MOVIE_ARG_KEY);
			if (savedInstanceState.containsKey(TRAILERS_ARG_KEY))
				mTrailers = (Trailer[]) savedInstanceState.getParcelableArray(TRAILERS_ARG_KEY);
		} else if (getIntent() != null && getIntent().hasExtra(MOVIE_ARG_KEY)) {
			mMovie = getIntent().getParcelableExtra(MOVIE_ARG_KEY);
		} else {
			Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_details);
		mBinding.setMovie(mMovie);

		mBinding.rvTrailers.setLayoutManager(new LinearLayoutManager(this));
		mBinding.rvTrailers.setNestedScrollingEnabled(false);
		mBinding.rvTrailers.setHasFixedSize(true);

		mAdapter = new TrailersListAdapter(this);
		mBinding.rvTrailers.setAdapter(mAdapter);

		ViewCompat.setElevation(mBinding.tvTitle, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));
		new FetchTrailers().execute(getString(R.string.api_key));
		populateView();
	}

	void populateView() {
		String ratingText = String.valueOf(mMovie.getUserRating()) + "/10";
		mBinding.tvRating.setText(ratingText);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(mMovie.getReleaseDate().getTime());
		mBinding.tvDate.setText(String.valueOf(calendar.get(Calendar.YEAR)));

		Picasso.with(this).load(APIUtils.getURLForImage(mMovie.getImageUrl())).into(mBinding.image);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(MOVIE_ARG_KEY, mMovie);
		if (mTrailers != null)
			outState.putParcelableArray(TRAILERS_ARG_KEY, mTrailers);
	}

	@Override
	public void onClick(Trailer trailer) {
		startActivity(APIUtils.getYoutubeTrailerIntent(this, trailer.getProviderKey()));
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

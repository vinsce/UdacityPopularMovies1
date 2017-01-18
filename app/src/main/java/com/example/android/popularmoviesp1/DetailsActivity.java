package com.example.android.popularmoviesp1;

import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.example.android.popularmoviesp1.databinding.ActivityDetailsBinding;
import com.example.android.popularmoviesp1.model.Movie;
import com.example.android.popularmoviesp1.model.Review;
import com.example.android.popularmoviesp1.model.Trailer;
import com.example.android.popularmoviesp1.utils.APIUtils;
import com.example.android.popularmoviesp1.utils.Networking;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.Calendar;

public class DetailsActivity extends AppCompatActivity implements TrailersListAdapter.TrailerAdapterOnClickHandler {
	public static final String MOVIE_ARG_KEY = "MOVIE";
	private static final String TRAILERS_ARG_KEY = "TRAILERS";
	private static final String REVIEWS_ARG_KEY = "REVIEWS";

	private Movie mMovie;
	private Trailer[] mTrailers;
	private Review[] mReviews;

	private TrailersListAdapter mTrailersAdapter;
	private ReviewsListAdapter mReviewsAdapter;

	ActivityDetailsBinding mBinding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null && savedInstanceState.containsKey(MOVIE_ARG_KEY)) {
			mMovie = savedInstanceState.getParcelable(MOVIE_ARG_KEY);
			if (savedInstanceState.containsKey(TRAILERS_ARG_KEY))
				mTrailers = (Trailer[]) savedInstanceState.getParcelableArray(TRAILERS_ARG_KEY);
			if (savedInstanceState.containsKey(REVIEWS_ARG_KEY))
				mReviews = (Review[]) savedInstanceState.getParcelableArray(REVIEWS_ARG_KEY);
		} else if (getIntent() != null && getIntent().hasExtra(MOVIE_ARG_KEY)) {
			mMovie = getIntent().getParcelableExtra(MOVIE_ARG_KEY);
		} else {
			Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_details);

		setSupportActionBar(mBinding.toolbar);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null)
			actionBar.setDisplayHomeAsUpEnabled(true);

		mBinding.setMovie(mMovie);

		mBinding.rvTrailers.setLayoutManager(new LinearLayoutManager(this));
		mBinding.rvTrailers.setNestedScrollingEnabled(false);
		mBinding.rvTrailers.setHasFixedSize(true);
		mTrailersAdapter = new TrailersListAdapter(this);
		mBinding.rvTrailers.setAdapter(mTrailersAdapter);


		mBinding.rvReviews.setLayoutManager(new LinearLayoutManager(this));
		mBinding.rvReviews.setNestedScrollingEnabled(false);
		mBinding.rvReviews.setHasFixedSize(true);
		mReviewsAdapter = new ReviewsListAdapter();
		mBinding.rvReviews.setAdapter(mReviewsAdapter);
		DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
		mBinding.rvReviews.addItemDecoration(dividerItemDecoration);

		populateView();
	}

	void populateView() {
		setTitle(mMovie.getTitle());
		String ratingText = String.valueOf(mMovie.getUserRating()) + "/10";
		mBinding.tvRating.setText(ratingText);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(mMovie.getReleaseDate().getTime());
		mBinding.tvDate.setText(String.valueOf(calendar.get(Calendar.YEAR)));

		Picasso.with(this).load(APIUtils.getURLForImage(mMovie.getImageUrl())).into(mBinding.image);

		new FetchTrailers().execute(getString(R.string.api_key));
		new FetchReviews().execute(getString(R.string.api_key));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(MOVIE_ARG_KEY, mMovie);
		if (mTrailers != null)
			outState.putParcelableArray(TRAILERS_ARG_KEY, mTrailers);
		if (mReviews != null)
			outState.putParcelableArray(REVIEWS_ARG_KEY, mReviews);
	}

	@Override
	public void onClick(Trailer trailer) {
		startActivity(APIUtils.getYoutubeTrailerIntent(this, trailer.getProviderKey()));
	}


	public class FetchTrailers extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showLoadingTrailersIndicator();
		}

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
			if (data != null) {
				mTrailers = APIUtils.getTrailerArrayFromNetworkResponse(data);
				if (mTrailers != null && mTrailers.length > 0) {
					mTrailersAdapter.setTrailersList(mTrailers);
					showTrailers();
				} else showTrailersLoadingError(R.string.no_trailers_available);
			} else {
				showTrailersLoadingError(R.string.error_loading_trailers);
			}
		}
	}

	private void showLoadingTrailersIndicator() {
		mBinding.pbLoadingIndicatorTrailers.setVisibility(View.VISIBLE);
		mBinding.rvTrailers.setVisibility(View.INVISIBLE);
		mBinding.tvTrailersMessage.setVisibility(View.INVISIBLE);
	}

	private void showTrailers() {
		mBinding.pbLoadingIndicatorTrailers.setVisibility(View.INVISIBLE);
		mBinding.rvTrailers.setAdapter(mTrailersAdapter);
		mBinding.rvTrailers.setVisibility(View.VISIBLE);
		mBinding.tvTrailersMessage.setVisibility(View.INVISIBLE);
	}

	private void showTrailersLoadingError(@StringRes int errorResourceId) {
		mBinding.pbLoadingIndicatorTrailers.setVisibility(View.INVISIBLE);
		mBinding.rvTrailers.setAdapter(null);
		mBinding.rvTrailers.setVisibility(View.INVISIBLE);
		mBinding.tvTrailersMessage.setText(errorResourceId);
		mBinding.tvTrailersMessage.setVisibility(View.VISIBLE);
	}

	public class FetchReviews extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showLoadingReviewsIndicator();
		}

		@Override
		protected String doInBackground(String... params) {

			/* the API KEY is passed here **/
			if (params.length == 0) {
				return null;
			}
			String apiKey = params[0];

			URL moviesUrl = APIUtils.getReviewsURL(apiKey, mMovie.getId());

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
				mReviews = APIUtils.getReviewArrayFromNetworkResponse(data);
				if (mReviews != null && mReviews.length > 0) {
					mReviewsAdapter.setReviewsList(mReviews);
					showReviews();
				} else showReviewsLoadingError(R.string.no_reviews_available);
			} else {
				showReviewsLoadingError(R.string.error_loading_reviews);
			}
		}
	}

	private void showLoadingReviewsIndicator() {
		mBinding.pbLoadingIndicatorReviews.setVisibility(View.VISIBLE);
		mBinding.rvReviews.setVisibility(View.INVISIBLE);
		mBinding.tvReviewsMessage.setVisibility(View.INVISIBLE);
	}

	private void showReviews() {
		mBinding.pbLoadingIndicatorReviews.setVisibility(View.INVISIBLE);
		mBinding.rvReviews.setAdapter(mReviewsAdapter);
		mBinding.rvReviews.setVisibility(View.VISIBLE);
		mBinding.tvReviewsMessage.setVisibility(View.INVISIBLE);
	}

	private void showReviewsLoadingError(@StringRes int errorResourceId) {
		mBinding.pbLoadingIndicatorReviews.setVisibility(View.INVISIBLE);
		mBinding.rvReviews.setAdapter(null);
		mBinding.rvReviews.setVisibility(View.INVISIBLE);
		mBinding.tvReviewsMessage.setText(errorResourceId);
		mBinding.tvReviewsMessage.setVisibility(View.VISIBLE);
	}
}

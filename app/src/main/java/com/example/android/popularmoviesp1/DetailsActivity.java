package com.example.android.popularmoviesp1;

import android.content.ContentValues;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.example.android.popularmoviesp1.adapters.ReviewsListAdapter;
import com.example.android.popularmoviesp1.adapters.TrailersListAdapter;
import com.example.android.popularmoviesp1.data.MoviesContract;
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

	private boolean mIsFavorite = false;

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
			Toast.makeText(this, R.string.error_loading_movies, Toast.LENGTH_SHORT).show();
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

		initFavoriteButton();
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

	private void initFavoriteButton() {
		// Checking if movie already liked. If so, the icon in the FAB button is changed to a full star instead of empty star
		Uri uriToQuery = MoviesContract.BASE_CONTENT_URI.buildUpon().appendPath(MoviesContract.PATH_FAVORITE_MOVIES).appendPath(mMovie.getId()).build();
		Cursor cursor = getContentResolver().query(uriToQuery, null, null, null, null);
		if (cursor != null && cursor.getCount() != 0) {
			mIsFavorite = true;
			mBinding.fabStar.setImageResource(R.drawable.ic_star_on);
		} else {
			mIsFavorite = false;
			mBinding.fabStar.setImageResource(R.drawable.ic_star_off);
		}
		if (cursor != null)
			cursor.close();

		// Handling on click on the favorite FAB. If the movie is already in favorites, it will be removed. Otherwise it will be added to favorites
		mBinding.fabStar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mIsFavorite) {
					boolean inserted = insertMovieInFavorites();
					if (inserted) {
						mIsFavorite = true;
						mBinding.fabStar.setImageResource(R.drawable.ic_star_on);
					}
				} else {
					boolean deleted = deleteMovieFromFavorites();
					if (deleted) {
						mIsFavorite = false;
						mBinding.fabStar.setImageResource(R.drawable.ic_star_off);
					}
				}
			}
		});
	}

	private boolean insertMovieInFavorites() {
		try {
			ContentValues movieValues = new ContentValues();
			movieValues.put(MoviesContract.FavoriteMoviesEntry.COLUMN_TITLE, mMovie.getTitle());
			movieValues.put(MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID, mMovie.getId());
			movieValues.put(MoviesContract.FavoriteMoviesEntry.COLUMN_RELEASE_DATE, mMovie.getReleaseDate().getTime());
			movieValues.put(MoviesContract.FavoriteMoviesEntry.COLUMN_POSTER_URL, mMovie.getImageUrl());
			movieValues.put(MoviesContract.FavoriteMoviesEntry.COLUMN_RATING, mMovie.getUserRating());
			movieValues.put(MoviesContract.FavoriteMoviesEntry.COLUMN_SYNOPSIS, mMovie.getPlotSynopsis());

			Uri uriToInsert = MoviesContract.BASE_CONTENT_URI.buildUpon().appendPath(MoviesContract.PATH_FAVORITE_MOVIES).build();
			return getContentResolver().insert(uriToInsert, movieValues) != null;
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, R.string.general_error_message, Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	private boolean deleteMovieFromFavorites() {
		try {
			Uri uriToDelete = MoviesContract.BASE_CONTENT_URI.buildUpon().appendPath(MoviesContract.PATH_FAVORITE_MOVIES).appendPath(mMovie.getId()).build();
			return getContentResolver().delete(uriToDelete, null, null) > 0;
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, R.string.general_error_message, Toast.LENGTH_SHORT).show();
			return false;
		}
	}
}

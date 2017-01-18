package com.example.android.popularmoviesp1.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmoviesp1.R;
import com.example.android.popularmoviesp1.data.MoviesContract;
import com.example.android.popularmoviesp1.model.Movie;
import com.example.android.popularmoviesp1.utils.APIUtils;
import com.squareup.picasso.Picasso;

import java.util.Date;

/**
 * Created by vinsce on 18/01/17 at 22.08.
 *
 * @author vinsce
 */

public class MoviesCursorGridAdapter extends RecyclerView.Adapter<MoviesCursorGridAdapter.MovieViewHolder> {

	private final Context mContext;

	final private MovieAdapterOnClickHandler mClickHandler;

	private Cursor mCursor;

	public MoviesCursorGridAdapter(@NonNull Context context, MovieAdapterOnClickHandler clickHandler) {
		mContext = context;
		mClickHandler = clickHandler;
	}

	@Override
	public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		View view = LayoutInflater.from(mContext).inflate(R.layout.movie_list_item, viewGroup, false);
		return new MovieViewHolder(view);
	}

	@Override
	public void onBindViewHolder(MovieViewHolder viewHolder, int position) {
		mCursor.moveToPosition(position);

		String movieId = mCursor.getString(mCursor.getColumnIndex(MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID));
		String title = mCursor.getString(mCursor.getColumnIndex(MoviesContract.FavoriteMoviesEntry.COLUMN_TITLE));
		String synopsis = mCursor.getString(mCursor.getColumnIndex(MoviesContract.FavoriteMoviesEntry.COLUMN_SYNOPSIS));
		String posterUrl = mCursor.getString(mCursor.getColumnIndex(MoviesContract.FavoriteMoviesEntry.COLUMN_POSTER_URL));
		float rating = mCursor.getFloat(mCursor.getColumnIndex(MoviesContract.FavoriteMoviesEntry.COLUMN_RATING));
		long dateMillis = mCursor.getLong(mCursor.getColumnIndex(MoviesContract.FavoriteMoviesEntry.COLUMN_RELEASE_DATE));
		Date date = new Date(dateMillis);

		Movie movie = new Movie(movieId, title, posterUrl, synopsis, rating, date);
		viewHolder.bind(movie);
	}

	@Override
	public int getItemCount() {
		if (null == mCursor) return 0;
		return mCursor.getCount();
	}

	void swapCursor(Cursor newCursor) {
		mCursor = newCursor;
		notifyDataSetChanged();
	}

	class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		private ImageView mImageView;
		private Movie mMovie;

		MovieViewHolder(View itemView) {
			super(itemView);
			mImageView = (ImageView) itemView.findViewById(R.id.image);
			itemView.setOnClickListener(this);
		}

		void bind(Movie movie) {
			mMovie = movie;
			Picasso.with(itemView.getContext()).load(APIUtils.getURLForImage(movie.getImageUrl())).into(mImageView);
		}

		@Override
		public void onClick(View v) {
			mClickHandler.onClick(mMovie);
		}
	}

}

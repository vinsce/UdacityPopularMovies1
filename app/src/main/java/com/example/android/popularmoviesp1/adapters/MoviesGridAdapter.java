package com.example.android.popularmoviesp1.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmoviesp1.R;
import com.example.android.popularmoviesp1.model.Movie;
import com.example.android.popularmoviesp1.utils.APIUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by vinsce on 13/01/17 at 13.57.
 *
 * @author vinsce
 */

public class MoviesGridAdapter extends RecyclerView.Adapter<MoviesGridAdapter.MovieViewHolder> {
	private Movie[] mMoviesList;

	private final MovieAdapterOnClickHandler mClickHandler;


	public MoviesGridAdapter(MovieAdapterOnClickHandler clickHandler) {
		mClickHandler = clickHandler;
	}

	@Override
	public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item, parent, false);
		return new MovieViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(MovieViewHolder holder, int position) {
		Movie item = mMoviesList[position];
		holder.bind(item);
	}

	@Override
	public int getItemCount() {
		if (mMoviesList == null)
			return 0;
		else return mMoviesList.length;
	}

	public void setMoviesList(Movie[] movies) {
		this.mMoviesList = movies;
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

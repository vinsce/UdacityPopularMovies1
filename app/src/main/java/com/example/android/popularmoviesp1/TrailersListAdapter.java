package com.example.android.popularmoviesp1;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmoviesp1.model.Trailer;

/**
 * Created by vinsce on 18/01/17 at 14.23.
 *
 * @author vinsce
 */

public class TrailersListAdapter extends RecyclerView.Adapter<TrailersListAdapter.TrailerViewHolder> {
	private Trailer[] mTrailersList;

	private final TrailerAdapterOnClickHandler mClickHandler;


	public TrailersListAdapter(TrailerAdapterOnClickHandler clickHandler) {
		mClickHandler = clickHandler;
	}

	@Override
	public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_list_item, parent, false);
		return new TrailerViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(TrailerViewHolder holder, int position) {
		Trailer item = mTrailersList[position];
		holder.bind(item);
	}

	@Override
	public int getItemCount() {
		if (mTrailersList == null)
			return 0;
		else return mTrailersList.length;
	}

	public void setTrailersList(Trailer[] trailers) {
		this.mTrailersList = trailers;
		notifyDataSetChanged();
	}

	public interface TrailerAdapterOnClickHandler {
		void onClick(Trailer trailer);
	}

	class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		private TextView mTrailerTextView;
		private Trailer mTrailer;

		TrailerViewHolder(View itemView) {
			super(itemView);
			mTrailerTextView = (TextView) itemView.findViewById(R.id.tv_trailer_title);
			itemView.setOnClickListener(this);
		}

		void bind(Trailer trailer) {
			mTrailer = trailer;
			mTrailerTextView.setText(trailer.getName());
		}

		@Override
		public void onClick(View v) {
			mClickHandler.onClick(mTrailer);
		}
	}
}

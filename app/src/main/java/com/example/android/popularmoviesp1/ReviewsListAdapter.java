package com.example.android.popularmoviesp1;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmoviesp1.model.Review;

/**
 * Created by vinsce on 18/01/17 at 19.54.
 *
 * @author vinsce
 */

public class ReviewsListAdapter extends RecyclerView.Adapter<ReviewsListAdapter.ReviewViewHolder> {
	private Review[] mReviewsList;

	@Override
	public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list_item, parent, false);
		return new ReviewViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(ReviewViewHolder holder, int position) {
		Review item = mReviewsList[position];
		holder.bind(item);
	}

	@Override
	public int getItemCount() {
		if (mReviewsList == null)
			return 0;
		else return mReviewsList.length;
	}

	public void setReviewsList(Review[] reviews) {
		this.mReviewsList = reviews;
		notifyDataSetChanged();
	}

	class ReviewViewHolder extends RecyclerView.ViewHolder {
		private TextView mContentTextView, mAuthorTextView;
		private Review mReview;

		ReviewViewHolder(View itemView) {
			super(itemView);
			mContentTextView = (TextView) itemView.findViewById(R.id.tv_review_content);
			mAuthorTextView = (TextView) itemView.findViewById(R.id.tv_review_author);
		}

		void bind(Review review) {
			mReview = review;
			mContentTextView.setText(review.getContent());
			mAuthorTextView.setText(review.getAuthorName());
		}
	}
}

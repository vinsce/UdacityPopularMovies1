package com.example.android.popularmoviesp1.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by vinsce on 13/01/17 at 13.50.
 *
 * @author vinsce
 */

public class Movie {
	@SerializedName("original_title")
	private String mTitle;

	@SerializedName("poster_path")
	private String mImageUrl;

	@SerializedName("overview")
	private String mPlotSynopsis;

	@SerializedName("vote_average")
	private float mUserRating;

	@SerializedName("release_date")
	private Date mReleaseDate;

	public Movie(String title, String imageUrl, String plotSynopsis, float userRating, Date releaseDate) {
		mTitle = title;
		mImageUrl = imageUrl;
		mPlotSynopsis = plotSynopsis;
		mUserRating = userRating;
		mReleaseDate = releaseDate;
	}

	public String getTitle() {
		return mTitle;
	}

	public String getImageUrl() {
		return mImageUrl;
	}

	public String getPlotSynopsis() {
		return mPlotSynopsis;
	}

	public float getUserRating() {
		return mUserRating;
	}

	public Date getReleaseDate() {
		return mReleaseDate;
	}

	@Override
	public String toString() {
		return mTitle;
	}
}

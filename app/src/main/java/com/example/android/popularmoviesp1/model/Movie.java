package com.example.android.popularmoviesp1.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by vinsce on 13/01/17 at 13.50.
 *
 * @author vinsce
 */

public class Movie implements Parcelable {
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

	private Movie(Parcel in) {
		mTitle = in.readString();
		mImageUrl = in.readString();
		mPlotSynopsis = in.readString();
		mUserRating = in.readFloat();
	}

	public static final Creator<Movie> CREATOR = new Creator<Movie>() {
		@Override
		public Movie createFromParcel(Parcel in) {
			return new Movie(in);
		}

		@Override
		public Movie[] newArray(int size) {
			return new Movie[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mTitle);
		dest.writeString(mImageUrl);
		dest.writeString(mPlotSynopsis);
		dest.writeFloat(mUserRating);
	}
}

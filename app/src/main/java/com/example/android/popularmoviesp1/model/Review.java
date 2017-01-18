package com.example.android.popularmoviesp1.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vinsce on 18/01/17 at 19.41.
 *
 * @author vinsce
 */

public class Review implements Parcelable {
	@SerializedName("author")
	private String mAuthorName;

	@SerializedName("content")
	private String mContent;

	@SerializedName("url")
	private String mUrl;

	public Review(String authorName, String content, String url) {
		mAuthorName = authorName;
		mContent = content;
		mUrl = url;
	}

	public String getAuthorName() {
		return mAuthorName;
	}

	public String getContent() {
		return mContent;
	}

	public String getUrl() {
		return mUrl;
	}


	private Review(Parcel in) {
		mAuthorName = in.readString();
		mContent = in.readString();
		mUrl = in.readString();
	}

	public static final Creator<Review> CREATOR = new Creator<Review>() {
		@Override
		public Review createFromParcel(Parcel in) {
			return new Review(in);
		}

		@Override
		public Review[] newArray(int size) {
			return new Review[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mAuthorName);
		dest.writeString(mContent);
		dest.writeString(mUrl);
	}
}

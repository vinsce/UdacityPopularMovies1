package com.example.android.popularmoviesp1.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vinsce on 18/01/17 at 14.39.
 *
 * @author vinsce
 */

public class Trailer implements Parcelable {
	@SerializedName("id")
	private String mId;

	@SerializedName("name")
	private String mName;

	@SerializedName("key")
	private String mProviderKey;

	public Trailer(String id, String name, String providerKey) {
		mId = id;
		mName = name;
		mProviderKey = providerKey;
	}

	public String getId() {
		return mId;
	}

	public String getName() {
		return mName;
	}

	public String getProviderKey() {
		return mProviderKey;
	}


	private Trailer(Parcel in) {
		mId = in.readString();
		mName = in.readString();
		mProviderKey = in.readString();
	}

	public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
		@Override
		public Trailer createFromParcel(Parcel in) {
			return new Trailer(in);
		}

		@Override
		public Trailer[] newArray(int size) {
			return new Trailer[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mId);
		dest.writeString(mName);
		dest.writeString(mProviderKey);
	}
}

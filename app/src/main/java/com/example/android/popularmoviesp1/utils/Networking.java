package com.example.android.popularmoviesp1.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by vinsce on 13/01/17 at 1.12.
 *
 * @author vinsce
 */

public class Networking {

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}


	public static String getResponseFromHttpUrl(URL url) throws IOException {
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		try {
			InputStream in = urlConnection.getInputStream();

			Scanner scanner = new Scanner(in);
			scanner.useDelimiter("\\A");

			boolean hasInput = scanner.hasNext();
			if (hasInput) {
				return scanner.next();
			} else {
				return null;
			}
		} finally {
			urlConnection.disconnect();
		}
	}
}

package com.mbronshteyn.android.calendar.hebrew.util;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mbronshteyn.android.calendar.hebrew.util.geocode.GeoCodeResult;
import com.mbronshteyn.android.calendar.hebrew.util.geocode.Location;
import com.mbronshteyn.android.calendar.hebrew.util.timezone.TimeZoneResult;
import com.mbronshteyn.calendar.hebrew.data.GeoInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

public class GeoCoderUtils {

	private final static String MAOQUEST_KEY = "hzhQtWtqvoZc5klzAxvLsKjp3Qxb4lig";
	private final static String TIMEZONEDB_KEY = "4JLH760ZZXYC";

	/**
	 * The callback used to indicate when request is complete.
	 */
	public interface OnRequestListener {
		void onGeoInfoReceived(GeoInfo result);
	}

	private OnRequestListener callBack;

	public GeoCoderUtils(OnRequestListener callBack) {
		this.callBack = callBack;
	}

	public void getLocationInfo(String address) {

		RetreiveGeoInfoTask task = new RetreiveGeoInfoTask();
		task.execute(address.trim());
	}

	public void getReverseLocationInfo(Double lon, Double lat) {

		RetreiveReverseInfoTask task = new RetreiveReverseInfoTask();
		task.execute(lon, lat);
	}

	private class RetreiveGeoInfoTask extends AsyncTask<String, Void, GeoInfo> {

		protected GeoInfo doInBackground(String... address) {

			Double lon = new Double(0);
			Double lat = new Double(0);
			String timeZone = null;
			String addressName = "";
			int dstOffset = 0;
			int rawOffset = 0;

			GeoInfo result = null;

			try {

				HttpURLConnection urlConnection = null;
				Gson gson = null;
				String response = null;

				// get geo coordinates
				URL url = new URL("https://www.mapquestapi.com/geocoding/v1/address?key=" + MAOQUEST_KEY + "&inFormat=kvp&outFormat=json&location="
						+ URLEncoder.encode(address[0]));
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.connect();

				response = getServiceResponse(urlConnection.getInputStream());
				gson = new GsonBuilder().create();
				GeoCodeResult geoResult = gson.fromJson(response, GeoCodeResult.class);

				Location location = geoResult.getResults().get(0).getLocations().get(0);
				lon = location.getLatLng().getLng();
				lat = location.getLatLng().getLat();
				addressName = location.getAdminArea5() + " " + location.getAdminArea3() + " " + location.getPostalCode() + ", "
						+ location.getAdminArea1();

				// get time zone info
				long timeStamp = new Date().getTime() / 1000;
				url = new URL("https://api.timezonedb.com/v2.1/get-time-zone?key=" + TIMEZONEDB_KEY + "&format=json&by=position&lat=" + lat + "&lng=" + lon
						+ "&time=" + timeStamp);
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.connect();

				response = getServiceResponse(urlConnection.getInputStream());
				gson = new GsonBuilder().create();
				TimeZoneResult timeZoneResult = gson.fromJson(response, TimeZoneResult.class);

				timeZone = timeZoneResult.getZoneName();
				dstOffset = timeZoneResult.getGmtOffset();

			} catch (IOException e) {
				e.getMessage();
			}

			result = new GeoInfo(lat, lon, timeZone, addressName, dstOffset, rawOffset);

			return result;
		}

		protected void onPostExecute(GeoInfo geoInfo) {
			callBack.onGeoInfoReceived(geoInfo);
		}
	}

	private class RetreiveReverseInfoTask extends AsyncTask<Double, Void, GeoInfo> {

		protected GeoInfo doInBackground(Double... coordinates) {

			String timeZone = null;
			String addressName = "";
			int dstOffset = 0;
			int rawOffset = 0;

			GeoInfo result = null;

			try {

				// get latitude and longitude and addressName

				HttpURLConnection urlConnection = null;
				Gson gson = null;
				String response = null;

				// get geo coordinates
				URL url = new URL("https://www.mapquestapi.com/geocoding/v1/reverse?key=" + MAOQUEST_KEY + "&location=" + coordinates[0] + "," + coordinates[1]);
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.connect();

				response = getServiceResponse(urlConnection.getInputStream());
				gson = new GsonBuilder().create();
				GeoCodeResult geoResult = gson.fromJson(response, GeoCodeResult.class);

				Location location = geoResult.getResults().get(0).getLocations().get(0);
				addressName = location.getAdminArea5() + " " + location.getAdminArea3() + " " + location.getPostalCode() + ", "
						+ location.getAdminArea1();

				// get time zone info
				long timeStamp = new Date().getTime() / 1000;
				url = new URL("https://api.timezonedb.com/v2.1/get-time-zone?key=" + TIMEZONEDB_KEY + "&format=json&by=position&lat=" + coordinates[0] + "&lng="
						+ coordinates[1] + "&time=" + timeStamp);
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.connect();

				response = getServiceResponse(urlConnection.getInputStream());
				gson = new GsonBuilder().create();
				TimeZoneResult timeZoneResult = gson.fromJson(response, TimeZoneResult.class);

				timeZone = timeZoneResult.getZoneName();
				dstOffset = timeZoneResult.getGmtOffset();

			} catch (IOException e) {
			}

			result = new GeoInfo(coordinates[0], coordinates[1], timeZone, addressName, dstOffset, rawOffset);

			return result;
		}

		protected void onPostExecute(GeoInfo geoInfo) {
			callBack.onGeoInfoReceived(geoInfo);
		}
	}

	public String getServiceResponse(InputStream inputStream) throws IOException {

		String result = null;

		// Read the input stream into a String
		StringBuffer buffer = new StringBuffer();
		if (inputStream != null) {

			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

			String line;
			while ((line = reader.readLine()) != null) {
				buffer.append(line + "\n");
			}

			if (buffer.length() != 0) {
				// Stream was empty. No point in parsing.
				result = buffer.toString();
			}
		}
		return result;
	}

}

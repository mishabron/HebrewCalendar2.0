package com.mbronshteyn.android.calendar.hebrew.activity.preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mbronshteyn.android.calendar.hebrew.R;
import com.mbronshteyn.android.calendar.hebrew.util.GeoCoderUtils;
import com.mbronshteyn.android.calendar.hebrew.util.GeoCoderUtils.OnRequestListener;
import com.mbronshteyn.calendar.hebrew.data.GeoInfo;

import java.util.List;

@SuppressLint("NewApi")
public class GeoPreferences extends DialogPreference {

	private String latitudeKey;
	private String latitudeValue;

	private String longitudeKey;
	private String longitudeValue;

	private SharedPreferences preferences;
	private String location;
	private EditText latitude;
	private EditText longitude;
	private Context context;
	private LocationManager locationManager;
	private String timeZone;
	private String addressName;
	private int dstOffset;
	private int rawOffset;

	private String addressNameKey;
	private TextView addressNameLable;
	private TextView gmtUCT;

	public GeoPreferences(Context context, AttributeSet attrs) {

		super(context, attrs);

		this.context = context;

		preferences = PreferenceManager.getDefaultSharedPreferences(context);

		setDialogLayoutResource(R.layout.geocoordinates_setup);

		setPersistent(false);
		setDialogIcon(null);
	}

	@Override
	public void onBindDialogView(View view) {

		super.onBindDialogView(view);

		location = preferences.getString("address_location", "");

		latitude = (EditText) view.findViewById(R.id.latitude);
		longitude = (EditText) view.findViewById(R.id.longitude);
		addressNameLable = (TextView) view.findViewById(R.id.addressName);
		gmtUCT = (TextView) view.findViewById(R.id.gmtValue);

		Resources res = view.getResources(); // get resources
		if (res != null) {
			latitudeKey = res.getResourceEntryName(latitude.getId());
			longitudeKey = res.getResourceEntryName(longitude.getId());
			addressNameKey = res.getResourceEntryName(addressNameLable.getId());
		}

		// get saved values
		latitudeValue = preferences.getString(latitudeKey, "0");
		longitudeValue = preferences.getString(longitudeKey, "0");
		addressNameLable.setText(preferences.getString(addressNameKey, ""));
		dstOffset = preferences.getInt("dstOffset", 0);
		rawOffset = preferences.getInt("rawOffset", 0);

		gmtUCT.setText(Integer.toString((dstOffset + rawOffset) / 3600));

		// display saved values
		latitude.setText(latitudeValue.toString());
		longitude.setText(longitudeValue.toString());

		Button locationButton = (Button) view.findViewById(R.id.locationButton);

		if (!"".equals(location) && checkNetworkConnection()) {
			locationButton.setText(locationButton.getText() + ": " + location);
		} else {
			locationButton.setEnabled(false);
		}

		locationButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				findByLocation(location);
			}
		});

		Button gpsButton = (Button) view.findViewById(R.id.gpsButton);

		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			gpsButton.setEnabled(true);
		} else {
			gpsButton.setEnabled(false);
		}

		gpsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				findByGps(latitude, longitude);
			}

		});
	}


	protected void findByGps(EditText latitude, EditText longitude) {

		String locationProvider = LocationManager.GPS_PROVIDER;
		// Or use LocationManager.GPS_PROVIDER

		Location lastKnownLocation = getLastKnownLocation();

		if (lastKnownLocation != null) {
			lastKnownLocation.getLatitude();

			latitude.setText(Double.toString(lastKnownLocation.getLatitude()));
			longitude.setText(Double.toString(lastKnownLocation.getLongitude()));

			GeoCoderUtils utils = new GeoCoderUtils(callBack);
			utils.getReverseLocationInfo(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
		}

	}
	protected void findByLocation(String location) {

		GeoCoderUtils utils = new GeoCoderUtils(callBack);

		utils.getLocationInfo(location);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {

		super.onDialogClosed(positiveResult);

		if (positiveResult) {

			latitudeValue = latitude.getText().toString();
			longitudeValue = longitude.getText().toString();

			Editor editor = getEditor();
			editor.putString(latitudeKey, latitudeValue);
			editor.putString(longitudeKey, longitudeValue);
			editor.putString("timeZone", timeZone);
			editor.putString(addressNameKey, addressName);
			editor.putInt("dstOffset", dstOffset);
			editor.putInt("rawOffset", rawOffset);

			editor.commit();
		}
	}

	private boolean checkNetworkConnection() {

		boolean networkOk = true;

		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = null;
		try {
			networkInfo = connMgr.getActiveNetworkInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (networkInfo == null || !networkInfo.isConnected()) {
			networkOk = false;
		}

		return networkOk;
	}

	private OnRequestListener callBack = new OnRequestListener() {

		@Override
		public void onGeoInfoReceived(GeoInfo geo) {

			latitude.setText(geo.getLatitude().toString());
			longitude.setText(geo.getLongitude().toString());
			timeZone = geo.getTimeZone();
			addressName = geo.getAddressName();
			dstOffset = geo.getDstOffset();
			rawOffset = geo.getRawOffset();

			addressNameLable.setText(addressName);
			gmtUCT.setText(Integer.toString((dstOffset + rawOffset) / 3600));
		}

	};

	private Location getLastKnownLocation() {

		List<String> providers = locationManager.getProviders(true);
		Location bestLocation = null;
		for (String provider : providers) {
			Location l = locationManager.getLastKnownLocation(provider);
			if (l == null) {
				continue;
			}
			if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
				// Found best last known location: %s", l);
				bestLocation = l;
			}
		}
		return bestLocation;
	}
}

package com.mbronshteyn.android.calendar.hebrew.activity.drive;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.services.drive.DriveScopes;
import com.mbronshteyn.android.calendar.hebrew.dialog.CustomProgressDialog;
import com.mbronshteyn.android.calendar.hebrew.util.drive.DriveServiceHelper;

/**
 * An abstract activity that handles authorization and connection to the Drive
 * services.
 */
public abstract class BaseDriveActivity extends Activity {

	private static final int REQUEST_CODE_SIGN_IN = 100;
	private GoogleSignInClient mGoogleSignInClient;
	public DriveServiceHelper mDriveServiceHelper;
	private static final String TAG = "BaseDriveActivity";

	public CustomProgressDialog mProgressDialog;
	/**
	 * Extra for account name.
	 */
	protected static final String EXTRA_ACCOUNT_NAME = "account_name";

	/**
	 * Request code for auto Google Play Services error resolution.
	 */
	protected static final int REQUEST_CODE_RESOLUTION = 1;

	/**
	 * Next available request code.
	 */
	protected static final int NEXT_AVAILABLE_REQUEST_CODE = 2;

	@Override
	protected void onStart() {
		super.onStart();
		signIn();
	}

	private void signIn() {

		Log.d(TAG, "Requesting sign-in");

		GoogleSignInOptions signInOptions =
				new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
						//.requestIdToken("353823649850-tt4o4o33a9qe5p9jjofiulq6s903s35j.apps.googleusercontent.com")
						.requestEmail()
						.requestScopes(new Scope(DriveScopes.DRIVE_FILE))
						.build();
		GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);

		// The result of the sign-in Intent is handled in onActivityResult.
		startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

		switch (requestCode) {
			case REQUEST_CODE_SIGN_IN:
				if (resultCode == Activity.RESULT_OK && resultData != null) {
					handleSignInResult(resultData);
				}
				else{
					showMessage("Google Drive Sign In Failed");
				}
				break;


		}

		super.onActivityResult(requestCode, resultCode, resultData);
	}

	private void handleSignInResult(Intent result) {
		GoogleSignIn.getSignedInAccountFromIntent(result)
				.addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
					@Override
					public void onSuccess(GoogleSignInAccount googleSignInAccount) {
						Log.d(TAG, "Signed in as " + googleSignInAccount.getEmail());

						mDriveServiceHelper = new DriveServiceHelper(mDriveServiceHelper.getGoogleDriveService(getApplicationContext(), googleSignInAccount, "appName"));
						processFile();
						Log.d(TAG, "handleSignInResult: " + mDriveServiceHelper);
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						Log.e(TAG, "Unable to sign in.", e);
					}
				});
	}

	/**
	 * Shows a toast message.
	 */
	public void showMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	public abstract void processFile();
}

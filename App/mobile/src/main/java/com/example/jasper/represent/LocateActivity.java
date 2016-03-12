package com.example.jasper.represent;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LocateActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "iEwFa735kekJ2hpwyiO8yOYay";
    private static final String TWITTER_SECRET = "mnnBO2ZqnZsH1aybfElwYRlfxyCQsnlgiZqVYDCvKXDMLw5bvM";


    // UI references.
    private AutoCompleteTextView zipView;
    private Button continueButton;
    private CheckBox useLocCB;
    private TextView locateError;

    LocationManager mlocManager;
    LocationListener mlocListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_locate);

        //location stuff
        mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener(this);

        // Set up the login form.
        zipView = (AutoCompleteTextView) findViewById(R.id.zip);
        // populateAutoComplete();
        useLocCB = (CheckBox) findViewById(R.id.use_locCB);
        locateError = (TextView) findViewById(R.id.locateErrorText);
        continueButton = (Button) findViewById(R.id.contButton);
        continueButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLocate();
            }
        });

        //mProgressView = findViewById(R.id.login_progress);
    }

    private void attemptLocate(){
        //do nothing for now besides go to next screen
        if(!useLocCB.isChecked() && !isZipValid(zipView.getText().toString())){
            locateError.setText("Please enter valid 5 digit zipcode.");
        }else if(!zipView.getText().toString().equals("") && useLocCB.isChecked()){
            locateError.setText("Please only use one location option, not both.");
        }else if(!useLocCB.isChecked()){
            locateError.setText("");
            String zip = zipView.getText().toString();
            Intent intent = new Intent(this, CongressActivity.class);
            intent.putExtra("zip", zip);
            intent.putExtra("useZip", true);

            Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
            if(zip.equals("21042")){
                sendIntent.putExtra("zip", "21042");
            }else {
                sendIntent.putExtra("zip", "94704");
            }
            startService(sendIntent);
            startActivity(intent);
        }else{
            /* Use the LocationManager class to obtain GPS locations */
            mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
        }

    }

    private boolean isZipValid(String zip) {
        //TODO: Replace this with your own logic
        return zip.length() == 5;
    }


    /*---------- Listener class to get coordinates ------------- */
    private class MyLocationListener implements LocationListener {
        Activity parent;

        public MyLocationListener(Activity a){
            Log.d("T","Hi do the loacate thing");
            parent = a;
        }
        @Override
        public void onLocationChanged(Location loc)

        {
            Log.d("T","starting to do the loacate thing");
            Intent intent = new Intent(parent, CongressActivity.class);
            intent.putExtra("lat", loc.getLatitude());
            intent.putExtra("longi", loc.getLongitude());
            intent.putExtra("useZip", false);
            Log.d("T","yoyooyoyoyoyoyoyo trying to do the loacate thing"+loc.getLatitude()+loc.getLongitude());
            mlocManager.removeUpdates(mlocListener);
            startActivity(intent);
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }
}


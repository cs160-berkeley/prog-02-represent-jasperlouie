package com.example.jasper.represent;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
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

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LocateActivity extends AppCompatActivity {

    // UI references.
    private AutoCompleteTextView zipView;
    private Button continueButton;
    private CheckBox useLocCB;
    private TextView locateError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate);
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
        }else{
            locateError.setText("");
            String zip = zipView.getText().toString();
            Intent intent = new Intent(this, CongressActivity.class);
            intent.putExtra("zip", zip);

            Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
            if(zip.equals("21042")){
                sendIntent.putExtra("zip", "21042");
            }else {
                sendIntent.putExtra("zip", "94704");
            }
            startService(sendIntent);
            startActivity(intent);
        }

    }

    private boolean isZipValid(String zip) {
        //TODO: Replace this with your own logic
        return zip.length() == 5;
    }


}


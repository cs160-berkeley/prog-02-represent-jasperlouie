package com.example.jasper.represent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.GridViewPager;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by Jasper on 3/2/2016.
 */
public class InfoActivity extends Activity{

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "tET1MZZfM4APqjExQaHj9RyFF";
    private static final String TWITTER_SECRET = "tVZDaUdzmhxdDCkEEOpcLXkxTekrtvbjiAHcwV3SU264E73ppH";

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.info_activity);
        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        String zip;
        try{
            zip = getIntent().getExtras().getString("zip");
        }catch(Exception e){
            zip = "94704";
        }
        JSONObject obj;
        JSONArray repArray = null;
        try{
            InputStream is = this.getResources().openRawResource(R.raw.data);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            obj = new JSONObject(json);


            if(!zip.equals("21042")){
                zip = "94704";
            }
            repArray = obj.getJSONArray(zip);
        }catch(Exception e){

        }

        pager.setAdapter(new GridPagerAdapter(zip, repArray, this, getFragmentManager()));

         /* do this in onCreate */
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }

    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity

    private final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent se) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta; // perform low-cut filter

            if (mAccel > 12) {
                int a = randomZip();
                Toast toast = Toast.makeText(getApplicationContext(), "Going to random Zip Code: "+a+".", Toast.LENGTH_LONG);
                toast.show();
                startRandom(a);
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    protected int randomZip(){
        //TODO:return random zip
        return 21042;
    }

    protected void startRandom(int a) {
        Intent intent = new Intent(this, InfoActivity.class);
        intent.putExtra("cmd", "zip");
        intent.putExtra("zip", a+"");

        Intent sendIntent = new Intent(this, WatchToPhoneService.class);
        sendIntent.putExtra("cmd", "zip");
        sendIntent.putExtra("zip", a+"");
        startService(sendIntent);

        startActivity(intent);


    }
}


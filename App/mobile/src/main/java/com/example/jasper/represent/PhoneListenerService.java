package com.example.jasper.represent;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 */
public class PhoneListenerService extends WearableListenerService {

//   WearableListenerServices don't need an iBinder or an onStartCommand: they just need an onMessageReceieved.
private static final String ZIP = "/zip";
private static final String DETAILS = "/details";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());
        if (messageEvent.getPath().equalsIgnoreCase(ZIP)) {

            // Value contains the String we sent over in WatchToPhoneService, "good job"
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Log.d("T", "in zip, got: " + value);
            // Make a toast with the String
//            Context context = getApplicationContext();
//            int duration = Toast.LENGTH_SHORT;

//            Toast toast = Toast.makeText(context, value, duration);
//            toast.show();
            Intent intent = new Intent(this, CongressActivity.class);
            intent.putExtra("lat", getRandomLatitude());
            intent.putExtra("longi", getRandomLongitude());
            intent.putExtra("useZip", false);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            Log.d("T", "im here" + getRandomLatitude() + "," + getRandomLongitude());
            startActivity(intent);
            Log.d("T", "now here");
            // so you may notice this crashes the phone because it's
            //''sending message to a Handler on a dead thread''... that's okay. but don't do this.
            // replace sending a toast with, like, starting a new activity or something.
            // who said skeleton code is untouchable? #breakCSconceptions

        }else if(messageEvent.getPath().equalsIgnoreCase(DETAILS)){
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Log.d("T", "in Details, got: " + value);

//            String[] sArr =  value.split("!");
//            String zip = sArr[0];
//            String name = sArr[1];
//            JSONObject obj = null;
//            try {
//                InputStream is = this.getResources().openRawResource(R.raw.data);
//                int size = is.available();
//                byte[] buffer = new byte[size];
//                is.read(buffer);
//                is.close();
//                String json = new String(buffer, "UTF-8");
//                obj = new JSONObject(json);
//
//                JSONArray repArray = null;
//                if(zip.equals("21042")){
//                    repArray = obj.getJSONArray(zip);
//                }else{
//                    repArray = obj.getJSONArray("94704");
//                }
//
//                for(int i = 0; i < 3; i++){
//                    JSONObject jo = (JSONObject) repArray.get(i);
//                    if(jo.getString("name").equals(name)){
//                        Intent intent = new Intent(this, DetailsActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent.putExtra("json", jo.toString());
//                        startActivity(intent);
//                    }
//
//                }
//
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("json", value);
            startActivity(intent);
        }else{
            super.onMessageReceived( messageEvent );
        }

    }

    private double getRandomLatitude(){
        float minX = 33.0f;
        float maxX = 42.0f;

        Random rand = new Random();

        return (double)(rand.nextFloat() * (maxX - minX) + minX);
    }

    private double getRandomLongitude(){
        float minX = 83.0f;
        float maxX = 117.0f;

        Random rand = new Random();

        return -1.0*(double)(rand.nextFloat() * (maxX - minX) + minX);
    }
}

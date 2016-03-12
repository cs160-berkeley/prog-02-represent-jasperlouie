package com.example.jasper.represent;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Jasper on 3/2/2016.
 */
public class DetailsActivity extends AppCompatActivity {
    String SUNLIGHT_API_KEY = "aaa24083b491487aa9edd05b83bb32ee";
    //UI references
    private Button seeMore1, seeMore2;
    private TextView tv1, tv2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent intent = this.getIntent();
        JSONObject jo = new JSONObject();
        try{
            jo = new JSONObject(intent.getExtras().getString("json"));
            Log.d("T", "Making detail view using this info: ");
            Log.d("T", jo.toString());
            ((TextView) findViewById(R.id.title1)).setText(jo.getString("title")+" "+jo.getString("first_name") + " " + jo.getString("last_name"));
            String p = jo.getString("party");
            if(p.equals("D")){
                p = "Democrat";
            }else if(p.equals("R")){
                p = "Republican";
            }else if(p.equals("I")){
                p = "Independent";
            }
            ((TextView) findViewById(R.id.partyText1)).setText(p);
//            ((TextView) findViewById(R.id.positionText1)).setText(jo.getString("title"));
            ((TextView) findViewById(R.id.positionText1)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.termText1)).setText("Term ends " + jo.getString("term_end"));
            Log.d("T", jo.getString("term_end"));
            Log.d("T", ((TextView) findViewById(R.id.termText1)).getText().toString());
            ((TextView) findViewById(R.id.termText1)).setVisibility(View.VISIBLE);
//            int imageID = this.getResources().getIdentifier("drawable/" + img_name, null, this.getPackageName());
            int imageID = this.getResources().getIdentifier("drawable/" + jo.getString("party").toLowerCase(), null, this.getPackageName());
            ((ImageView) findViewById(R.id.partyIcon1)).setImageResource(imageID);

            byte[] bmp = intent.getExtras().getByteArray("portrait");
            Bitmap pic = BitmapFactory.decodeByteArray(bmp, 0, bmp.length);
            ((ImageView) findViewById(R.id.portrait1)).setImageBitmap(pic);
        }catch (Exception e){}


        seeMore1 = (Button) findViewById(R.id.button);
        seeMore2 = (Button) findViewById(R.id.button2);
        tv1 = (TextView) findViewById(R.id.committeeText);
        tv2 = (TextView) findViewById(R.id.billsText);

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            try{
                String stringUrl = "https://congress.api.sunlightfoundation.com/bills?apikey="+SUNLIGHT_API_KEY+"&sponsor_id="+jo.getString("bioguide_id");
                new DownloadWebpageTask(this, true).execute(stringUrl);
                stringUrl = "https://congress.api.sunlightfoundation.com/committees?apikey="+SUNLIGHT_API_KEY+"&member_ids="+jo.getString("bioguide_id");
                new DownloadWebpageTask(this, false).execute(stringUrl);
            }catch(Exception e){

            }



        } else {
            Log.d("T", "NETWORK FAILURE in CongressActivity");
        }

        do_thing();
    }

    private void do_thing(){
        tv1.post(new Runnable() {
            @Override
            public void run() {
                if(tv1.getLineCount() > 10){
                    tv1.setMaxLines(5);
                    linkButtonToTextField(seeMore1, tv1);
                    seeMore1.setVisibility(View.VISIBLE);
                }else{
                    seeMore1.setVisibility(View.INVISIBLE);
                }
            }
        });

        tv2.post(new Runnable() {
            @Override
            public void run() {
                if(tv2.getLineCount() > 10){
                    tv2.setMaxLines(5);
                    linkButtonToTextField(seeMore2, tv2);
                    seeMore2.setVisibility(View.VISIBLE);
                }else{
                    seeMore2.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void linkButtonToTextField(final Button b, final TextView tv){
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleTextView(b, tv);
            }
        });
    }

    private void toggleTextView(Button b, TextView tv){
        int collapsedMaxLines = 5;
        ObjectAnimator animation = ObjectAnimator.ofInt(tv, "maxLines",
                tv.getMaxLines() == collapsedMaxLines? tv.getLineCount() : collapsedMaxLines);
        if(tv.getMaxLines() == 5){
            b.setText("Collapse");
        }else{
            b.setText("See More");
        }
        animation.setDuration(200).start();
    }

    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        Activity myActivity;
        boolean isBills;

        private  DownloadWebpageTask(Activity a, boolean ib){
            super();
            myActivity = a;
            isBills = ib;
        }

        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("T", "Success! data retrieved: ");
            Log.d("T", result);
//            dicts = new ArrayList<String>();
            JSONObject obj = null;
            try {
//                InputStream is = myActivity.getResources().openRawResource(R.raw.data);
//                int size = is.available();
//                byte[] buffer = new byte[size];
//                is.read(buffer);
//                is.close();
//                String json = new String(buffer, "UTF-8");
                obj = new JSONObject(result);
                JSONArray arr = obj.getJSONArray("results");
//                if(zip.equals("21042")){
//                    repArray = obj.getJSONArray(zip);
//                }else{
//                    repArray = obj.getJSONArray("94704");
//                }
                String s = "";
                for(int i = 0; i < arr.length(); i++){
                    JSONObject jo = (JSONObject) arr.get(i);
                    if(isBills){
                        String temp = jo.getString("short_title");
                        if(temp.equals("null")){
                            temp = "Bill: " + jo.getString("official_title");
                        }
                        s += temp+"\n\n";
                    }else{
                        s += jo.getString("name")+"\n\n";
                    }


                }
                if(isBills){
                    tv2.setText(s);
                }else{
                    tv1.setText(s);
                }
                do_thing();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    // Given a URL, establishes an HttpUrlConnection and retrieves
// the web page content as a InputStream, which it returns as
// a string.
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("T", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
        Scanner s = new Scanner(stream).useDelimiter("\\A");
        String result = s.hasNext() ? s.next() : "";
        return new String(result);
    }

    public final static Bitmap stringToBitmap(String in){
        byte[] bytes = Base64.decode(in, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


}

package com.example.jasper.represent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.SortedList;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import io.fabric.sdk.android.Fabric;
import retrofit.http.GET;
import retrofit.http.Query;

//import com.twitter.sdk.android.tweetui.TweetUtils;

/**
 * Created by Jasper on 3/2/2016.
 */
public class CongressActivity extends AppCompatActivity {
    String SUNLIGHT_API_KEY = "aaa24083b491487aa9edd05b83bb32ee";
    //UI references
    private ArrayList<RepCard> rCards;
    private RepAdapter rAdapter;
    private ArrayList<CardView> cards;
    private ArrayList<TextView> names, websites, emails, tweets;
    private ArrayList<ImageView> portraits, partyIcons;
    private TextView location;
    String currentCounty;
    String s;
    JSONArray repArray;

    private ArrayList<String> dicts;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congress);

        TwitterAuthConfig authConfig = new TwitterAuthConfig("consumerKey", "consumerSecret");
        Fabric.with(this, new Twitter(authConfig));
        TwitterCore.getInstance().logInGuest(null);

//        Grab zip from intent
        BitmapKeeper.bitmaps = new HashMap<String, Bitmap>();
        String zip = "";
        String latitude = "";
        String longitude = "";
        Bundle extras = getIntent().getExtras();
        boolean useZip = extras.getBoolean("useZip");
        if (useZip) {
            Log.d("T", "using zip");
            zip = extras.getString("zip");
        } else {
            Log.d("T", "using lat/long");
            latitude = extras.getDouble("lat") + "";
            longitude = extras.getDouble("longi") + "";
            Log.d("T", latitude + "/" + longitude);
        }


//        Todo: Use this zip to fetch candidate info using sunlight api
//        request formats:
//        https://congress.api.sunlightfoundation.com/legislators/locate?latitude=...&longitude=...&apikey=SUNLIGHT_API_KEY
//        https://congress.api.sunlightfoundation.com/legislators/locate?zip=...&apikey=SUNLIGHT_API_KEY
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String stringUrl = "https://congress.api.sunlightfoundation.com/legislators/locate?apikey=" + SUNLIGHT_API_KEY;
            if (useZip) {
                stringUrl += "&zip=" + zip;
            } else {
                stringUrl += "&latitude=" + latitude + "&longitude=" + longitude;
            }
            new DownloadWebpageTask(this).execute(stringUrl);


        } else {
            Log.d("T", "NETWORK FAILURE in CongressActivity");
        }

//        // TODO: 3/10/2016 Add twitter handling


//        Todo: make repcard layout that is constructed by taking json string and interpretting

        location = (TextView) findViewById(R.id.location);

        if (zip.equals("")) {
//            Log.d("T", "zip: "+zip);
            new CountyGetter().execute("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&key=AIzaSyCiQ0pcOW_wQ_lIR-EzoEYatyrRZOYl4aU");
        } else {
//            Log.d()
            new CountyGetter().execute("https://maps.googleapis.com/maps/api/geocode/json?address=" + zip + "&key=AIzaSyCiQ0pcOW_wQ_lIR-EzoEYatyrRZOYl4aU");

        }


        rCards = new ArrayList<RepCard>();
        rAdapter = new RepAdapter(this, 0, rCards);
        ((ListView) this.findViewById(R.id.listView)).setAdapter(rAdapter);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Congress Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.jasper.represent/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Congress Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.jasper.represent/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        Activity myActivity;

        private DownloadWebpageTask(Activity a) {
            super();
            myActivity = a;
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
            dicts = new ArrayList<String>();
            JSONObject obj = null;
            try {
//                InputStream is = myActivity.getResources().openRawResource(R.raw.data);
//                int size = is.available();
//                byte[] buffer = new byte[size];
//                is.read(buffer);
//                is.close();
//                String json = new String(buffer, "UTF-8");
                obj = new JSONObject(result);
                repArray = obj.getJSONArray("results");
//                if(zip.equals("21042")){
//                    repArray = obj.getJSONArray(zip);
//                }else{
//                    repArray = obj.getJSONArray("94704");
//                }
                s = "";
                for (int i = 0; i < repArray.length(); i++) {
//                    bmps.add(null);
                    JSONObject jo = (JSONObject) repArray.get(i);
                    if (jo.getString("title").equals("Sen")) {
                        s += "Senator!";
                    } else {
                        s += "Representative!";
                    }

                    s += jo.getString("first_name") + " " + jo.getString("last_name") + "!";
                    s += jo.getString("party") + "!";
                    rCards.add(new RepCard(jo, null, null));
                    rAdapter.notifyDataSetChanged();

//                    TwitterSession session =
//                            Twitter.getSessionManager().getActiveSession();
//                    TwitterAuthToken authToken = session.getAuthToken();
                    String token = "cyuDq9b27fEKQWU6xtoTk1ZTZ";
                    String secret = "9pd4duKsNzlF4MGNlV3wafCkyNJLKqjoc5BhH0YuDQ73cFxrlg";
                    TwitterApiClient twitterApiClient = Twitter.getApiClient();
                    StatusesService twapiclient = twitterApiClient.getStatusesService();
                    final int finalI = i;
                    twapiclient.userTimeline(null, jo.getString("twitter_id"), null, (long) 1, null, null, null, null, null, new Callback<List<Tweet>>() {
                        @Override
                        public void success(Result<List<Tweet>> listResult) {

                            System.out.println("listResult" + listResult.data.size());
                            System.out.println("listResult" + listResult.data.get(0).user);
                            System.out.println("listResult" + listResult.data.get(0).user.profileImageUrl);
                            String picUrl = listResult.data.get(0).user.profileImageUrl;
                            rCards.get(finalI).tweet = listResult.data.get(0).text;
                            rAdapter.notifyDataSetChanged();
//                            ((TextView)tweets.get(finalI)).setText(listResult.data.get(0).text);
//                            userInfo.imageurl = listResult.data.get(0).user.profileImageUrl;
                            new ImageDownloader(finalI)
                                    .execute(picUrl.replace("_normal", ""));

                        }

                        @Override
                        public void failure(TwitterException e) {

                        }
                    });


                }


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

    class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        int i;
        ImageView bmImage;

        public ImageDownloader(int in) {
            this.bmImage = bmImage;
            i = in;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream in = new URL(url).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
//            bmps.set(i, result);
//            bmImage.setImageBitmap(result);
            rCards.get(i).b = result;
            try{
                BitmapKeeper.bitmaps.put(rCards.get(i).jo.getString("bioguide_id"), result);

            }catch(Exception e){

            }
            rAdapter.notifyDataSetChanged();

        }
    }

    class CountyGetter extends AsyncTask<String, Void, String> {
        String url;

        protected String doInBackground(String... urls) {
            String url = urls[0];
            String out = "";
            try {
                InputStream in = new URL(url).openStream();
                out = readIt(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }

            return out;
        }

        protected void onPostExecute(String result) {
//            bmps.set(i, result);
//            bmImage.setImageBitmap(result);
            Log.d("T", "wahhhhhhh" + result);
            String c = "";
            try {
                JSONObject jo = new JSONObject(result);
                JSONArray arr = jo.getJSONArray("results");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jo2 = arr.getJSONObject(i);
                    JSONArray address_comps = jo2.getJSONArray("address_components");
                    for (int j = 0; j < address_comps.length(); j++) {
                        JSONArray t = address_comps.getJSONObject(j).getJSONArray("types");
                        for (int k = 0; k < t.length(); k++) {
                            if (t.getString(k).equals("administrative_area_level_2")) {
                                c = address_comps.getJSONObject(j).getString("short_name");
                                break;
                            }
                        }

                    }

                }
            } catch (Exception e) {

            }

            Log.d("T", "Successfully got county:" + c);
            currentCounty = c;
            Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
            repArray.put(c);
            sendIntent.putExtra("zip", repArray.toString());
            Log.d("T", "message length: " + repArray.toString().length());
            Log.d("T", "message "+ repArray.toString());

            startService(sendIntent);

        }
    }

    public final static byte[] bitmapToBA(Bitmap in) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        in.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        return bytes.toByteArray();
    }

    private class RepAdapter extends ArrayAdapter<RepCard> {

        private ArrayList<RepCard> items;
        Context context;

        public RepAdapter(Context context, int textViewResourceId, ArrayList<RepCard> items) {
            super(context, textViewResourceId, items);
            this.items = items;
            this.context = context;

        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.card, null);
            }
            final JSONObject jo = items.get(position).jo;
            if (jo != null) {
                try {
                    ((TextView) v.findViewById(R.id.title1)).setText(jo.getString("first_name") + " " + jo.getString("last_name"));
                    ((TextView) v.findViewById(R.id.websiteText1)).setText(jo.getString("website"));
                    ((TextView) v.findViewById(R.id.websiteText1)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            try {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(jo.getString("website")));
                                startActivity(browserIntent);

                            } catch (Exception e) {

                            }

                        }
                    });
                    ((TextView) v.findViewById(R.id.emailText1)).setText(jo.getString("oc_email"));

                    ((TextView) v.findViewById(R.id.emailText1)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            /* Create the Intent */
                            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

                            /* Fill it with Data */
                            emailIntent.setType("plain/text");
                            try{
                                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{jo.getString("oc_email")});

                            }catch(Exception e){

                            }
                            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Hi");
                            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Text");

                            /* Send it off to the Activity-Chooser */
                            context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                        }
                    });
                    int imageID = v.getResources().getIdentifier("drawable/" + jo.getString("party").toLowerCase(), null, context.getPackageName());
                    ((ImageView) v.findViewById(R.id.partyIcon1)).setImageResource(imageID);
                    ((ImageView) v.findViewById(R.id.partyIcon1)).setVisibility(View.VISIBLE);

                    ((CardView) v.findViewById(R.id.card_view1)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, DetailsActivity.class);
                            intent.putExtra("json", items.get(position).jo.toString());
                            byte[] s = bitmapToBA(items.get(position).b);
                            intent.putExtra("portrait", s);
                            startActivity(intent);
                        }
                    });
                } catch (Exception e) {

                }
            }
            Bitmap b = items.get(position).b;
            if (b != null) {
                ((ImageView) v.findViewById(R.id.portrait1)).setImageBitmap(b);
                ((ImageView) v.findViewById(R.id.portrait1)).setVisibility(View.VISIBLE);
            }

            String t = items.get(position).tweet;
            if (t != null) {
                ((TextView) v.findViewById(R.id.twitterText1)).setText(t);
            }
            return v;
        }
    }

    private class RepCard {
        JSONObject jo;
        Bitmap b;
        String tweet;

        RepCard(JSONObject j, Bitmap m, String t) {
            jo = j;
            b = m;
            tweet = t;
        }
    }
}



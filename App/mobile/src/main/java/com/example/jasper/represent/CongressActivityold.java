package com.example.jasper.represent;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import io.fabric.sdk.android.Fabric;

//import com.twitter.sdk.android.tweetui.TweetUtils;

/**
 * Created by Jasper on 3/2/2016.
 */
public class CongressActivityold extends AppCompatActivity {
    String SUNLIGHT_API_KEY = "aaa24083b491487aa9edd05b83bb32ee";
    //UI references
    private ArrayList<CardView> cards;
    private ArrayList<TextView> names, websites, emails, tweets;
    private ArrayList<ImageView> portraits, partyIcons;
    private TextView location;
    private ArrayList<Bitmap> bmps;

    private ArrayList<String> dicts;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congress);

        TwitterAuthConfig authConfig =  new TwitterAuthConfig("consumerKey", "consumerSecret");
        Fabric.with(this, new Twitter(authConfig));
        TwitterCore.getInstance().logInGuest(null);

//        Grab zip from intent
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
            location.setText("Alameda County");
        } else {
            location.setText("ZIP: " + zip);
        }

        bmps = new ArrayList<Bitmap>();
        cards = new ArrayList<CardView>();
        cards.add((CardView) findViewById(R.id.card_view1));
        cards.add((CardView) findViewById(R.id.card_view2));
        cards.add((CardView) findViewById(R.id.card_view3));

        names = new ArrayList<TextView>();
        names.add((TextView) findViewById(R.id.title1));
        names.add((TextView) findViewById(R.id.title2));
        names.add((TextView) findViewById(R.id.title3));

        websites = new ArrayList<TextView>();
        websites.add((TextView) findViewById(R.id.websiteText1));
        websites.add((TextView) findViewById(R.id.websiteText2));
        websites.add((TextView) findViewById(R.id.websiteText3));

        emails = new ArrayList<TextView>();
        emails.add((TextView) findViewById(R.id.emailText1));
        emails.add((TextView) findViewById(R.id.emailText2));
        emails.add((TextView) findViewById(R.id.emailText3));

        tweets = new ArrayList<TextView>();
        tweets.add((TextView) findViewById(R.id.twitterText1));
        tweets.add((TextView) findViewById(R.id.twitterText2));
        tweets.add((TextView) findViewById(R.id.twitterText3));

        portraits = new ArrayList<ImageView>();
        portraits.add((ImageView) findViewById(R.id.portrait1));
        portraits.add((ImageView) findViewById(R.id.portrait2));
        portraits.add((ImageView) findViewById(R.id.portrait3));

        partyIcons = new ArrayList<ImageView>();
        partyIcons.add((ImageView) findViewById(R.id.partyIcon1));
        partyIcons.add((ImageView) findViewById(R.id.partyIcon2));
        partyIcons.add((ImageView) findViewById(R.id.partyIcon3));

//        dicts = new ArrayList<String>();
//        JSONObject obj = null;
//        try {
//            InputStream is = this.getResources().openRawResource(R.raw.data);
//            int size = is.available();
//            byte[] buffer = new byte[size];
//            is.read(buffer);
//            is.close();
//            String json = new String(buffer, "UTF-8");
//            obj = new JSONObject(json);
//
//            JSONArray repArray = null;
//            if(zip.equals("21042")){
//                repArray = obj.getJSONArray(zip);
//            }else{
//                repArray = obj.getJSONArray("94704");
//            }
//
//            for(int i = 0; i < 3; i++){
//                JSONObject jo = (JSONObject) repArray.get(i);
//                dicts.add(jo.toString());
//                names.get(i).setText(jo.getString("name"));
//                websites.get(i).setText(jo.getString("website"));
//                emails.get(i).setText(jo.getString("email"));
//                tweets.get(i).setText(jo.getString("tweet"));
//                String img_name = jo.getString("name").replaceAll("[^A-Za-z]+", "").toLowerCase();
//                int imageID = this.getResources().getIdentifier("drawable/" + img_name, null, this.getPackageName());
//                portraits.get(i).setImageResource(imageID);
//                imageID = this.getResources().getIdentifier("drawable/"+jo.getString("party")+"icon", null, this.getPackageName());
//                partyIcons.get(i).setImageResource(imageID);
//
//            }
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }


        for (int i = 0; i < 3; i++) {
            final int finalI = i;
            cards.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToDetails(finalI);
                }
            });
        }
    }

    private void goToDetails(int i) {
        //do nothing for now besides go to next screen
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("json", dicts.get(i));
        byte[] s = bitmapToBA(bmps.get(i));
        intent.putExtra("portrait", s);
        startActivity(intent);
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
                JSONArray repArray = obj.getJSONArray("results");
//                if(zip.equals("21042")){
//                    repArray = obj.getJSONArray(zip);
//                }else{
//                    repArray = obj.getJSONArray("94704");
//                }

                for (int i = 0; i < 3; i++) {
                    bmps.add(null);
                    JSONObject jo = (JSONObject) repArray.get(i);
                    dicts.add(jo.toString());
                    names.get(i).setText(jo.getString("first_name") + " " + jo.getString("last_name"));
                    websites.get(i).setText(jo.getString("website"));
                    emails.get(i).setText(jo.getString("oc_email"));
//                    tweets.get(i).setText("Placeholder");
                    String img_name = "barbaraboxer".replaceAll("[^A-Za-z]+", "").toLowerCase();
                    int imageID = myActivity.getResources().getIdentifier("drawable/" + img_name, null, myActivity.getPackageName());
                    portraits.get(i).setImageResource(imageID);
                    imageID = myActivity.getResources().getIdentifier("drawable/" + jo.getString("party").toLowerCase(), null, myActivity.getPackageName());
                    partyIcons.get(i).setImageResource(imageID);

//                    TwitterSession session =
//                            Twitter.getSessionManager().getActiveSession();
//                    TwitterAuthToken authToken = session.getAuthToken();
                    String token = "cyuDq9b27fEKQWU6xtoTk1ZTZ";
                    String secret = "9pd4duKsNzlF4MGNlV3wafCkyNJLKqjoc5BhH0YuDQ73cFxrlg";
                    TwitterApiClient twitterApiClient =  Twitter.getApiClient();
                    StatusesService twapiclient = twitterApiClient.getStatusesService();
                    final int finalI = i;
                    twapiclient.userTimeline(null, jo.getString("twitter_id"), null, (long)1, null, null, null, null, null, new Callback<List<Tweet>>() {
                        @Override
                        public void success(Result<List<Tweet>> listResult) {

                            System.out.println("listResult" + listResult.data.size());
                            System.out.println("listResult" + listResult.data.get(0).user);
                            System.out.println("listResult" + listResult.data.get(0).user.profileImageUrl);
                            String picUrl = listResult.data.get(0).user.profileImageUrl;
                            ((TextView)tweets.get(finalI)).setText(listResult.data.get(0).text);
//                            userInfo.imageurl = listResult.data.get(0).user.profileImageUrl;
                            new ImageDownloader(portraits.get(finalI), finalI)
                                    .execute(picUrl.replace("_normal", ""));

                        }

                        @Override
                        public void failure(TwitterException e) {

                        }
                    });


//                    // TODO: Use a more specific parent
////                    final ViewGroup parentView = (ViewGroup) getWindow().getDecorView().getRootView();
//                    // TODO: Base this Tweet ID on some data from elsewhere in your app
//                    long tweetId = 631879971628183552L;
//                    final int finalI = i;
//                    TweetUtils.loadTweet(tweetId, new Callback<Tweet>() {
//                        @Override
//                        public void success(Result<Tweet> result) {
//                            CompactTweetView tweetView = new CompactTweetView(CongressActivity.this, result.data);
//                            final int x = finalI;
//                            tweets.get(x).addView(tweetView);
//
//                        }
//
//                        @Override
//                        public void failure(TwitterException exception) {
//                            Log.d("TwitterKit", "Load Tweet failure", exception);
//                        }
//                    });


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

        public ImageDownloader(ImageView bmImage, int in) {
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
            bmps.set(i, result);
            bmImage.setImageBitmap(result);
        }
    }

    public final static byte[] bitmapToBA(Bitmap in){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        in.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        return bytes.toByteArray();
    }

    private class OrderAdapter extends ArrayAdapter<JSONObject> {

        private ArrayList<JSONObject> items;

        public OrderAdapter(Context context, int textViewResourceId, ArrayList<JSONObject> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                v = vi.inflate(R.layout.row, null);
            }
            JSONObject o = items.get(position);
            if (o != null) {
//                TextView tt = (TextView) v.findViewById(R.id.toptext);
//                TextView bt = (TextView) v.findViewById(R.id.bottomtext);
//                if (tt != null) {
//                    tt.setText("Name: "+o.getOrderName());                            }
//                if(bt != null){
//                    bt.setText("Status: "+ o.getOrderStatus());
//                }
            }
            return v;
        }
    }
}



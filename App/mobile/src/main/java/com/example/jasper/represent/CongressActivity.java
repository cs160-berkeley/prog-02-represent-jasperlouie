package com.example.jasper.represent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jasper on 3/2/2016.
 */
public class CongressActivity extends AppCompatActivity {
    //UI references
    private ArrayList<CardView> cards;
    private ArrayList<TextView> names, websites, emails, tweets;
    private ArrayList<ImageView> portraits, partyIcons;
    private TextView location;

    private ArrayList<String> dicts;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congress2);
        String zip;
        zip =  getIntent().getExtras().getString("zip");
        location = (TextView) findViewById(R.id.location);
        if(zip.equals("")){
            location.setText("Alameda County");
        }else{
            location.setText("ZIP: " +zip);
        }

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

        dicts = new ArrayList<String>();
        JSONObject obj = null;
        try {
            InputStream is = this.getResources().openRawResource(R.raw.data);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            obj = new JSONObject(json);

            JSONArray repArray = null;
            if(zip.equals("21042")){
                repArray = obj.getJSONArray(zip);
            }else{
                repArray = obj.getJSONArray("94704");
            }

            for(int i = 0; i < 3; i++){
                JSONObject jo = (JSONObject) repArray.get(i);
                dicts.add(jo.toString());
                names.get(i).setText(jo.getString("name"));
                websites.get(i).setText(jo.getString("website"));
                emails.get(i).setText(jo.getString("email"));
                tweets.get(i).setText(jo.getString("tweet"));
                String img_name = jo.getString("name").replaceAll("[^A-Za-z]+", "").toLowerCase();
                int imageID = this.getResources().getIdentifier("drawable/" + img_name, null, this.getPackageName());
                portraits.get(i).setImageResource(imageID);
                imageID = this.getResources().getIdentifier("drawable/"+jo.getString("party")+"icon", null, this.getPackageName());
                partyIcons.get(i).setImageResource(imageID);

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }




        for(int i = 0; i < 3; i++){
            final int finalI = i;
            cards.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToDetails(finalI);
                }
            });
        }
    }


    private void goToDetails(int i){
        //do nothing for now besides go to next screen
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("json", dicts.get(i));
//        intent.putExtra("ZIP", zip);
        startActivity(intent);
    }
}

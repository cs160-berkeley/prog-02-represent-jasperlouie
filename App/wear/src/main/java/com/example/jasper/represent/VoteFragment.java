package com.example.jasper.represent;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by Jasper on 3/2/2016.
 */
public class VoteFragment extends Fragment {
    //UI References
    TextView titleV, subtitleV;
    ImageView portraitV;
    CharSequence title, subtitle;
    boolean isCandidate;
    String state;

    public static VoteFragment create(String location){
        VoteFragment f = new VoteFragment();
        Bundle b = new Bundle();
        b.putCharSequence("location", location);
//        b.putCharSequence("subtitle", s);
//        b.putBoolean("isblue", isBlue);
//        b.putCharSequence("portrait", p);
        f.setArguments(b);
        return f;
    }



    public View onCreateView(LayoutInflater inflater, ViewGroup containter, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.vote_layout, null);
        String l = (String)getArguments().get("location");

        titleV = (TextView)v.findViewById(R.id.location);

        double p1 = 0;
        double p2 = 0;

        String location = (String)getArguments().get("location");

        JSONObject obj = null;
        try {
            InputStream is = this.getResources().openRawResource(R.raw.electioncounty2012);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");


            JSONArray repArray = new JSONArray(json);

            for(int i = 0; i < repArray.length(); i++){
                JSONObject jo = (JSONObject) repArray.get(i);
                String c = jo.getString("county-name");
                if(location.replace(" County", "").equals(c)){
                    p1 = jo.getDouble("obama-percentage");
                    p2 = jo.getDouble("romney-percentage");
                    state = ", "+jo.get("state-postal");
                }

            }


        } catch (Exception ex) {
            subtitleV = (TextView)v.findViewById(R.id.textView4);
            subtitleV.setText("No voting data available here for");
            ex.printStackTrace();
        }
        titleV.setText((String)getArguments().get("location")+state);

        subtitleV = (TextView)v.findViewById(R.id.percentages);
        subtitleV.setText(p1+"%              "+p2+"%");
        if(p1 < p2){
            v.findViewById(R.id.back_color).setBackgroundColor(Color.parseColor("#FF2C2C"));
        }

        return v;
    }
}

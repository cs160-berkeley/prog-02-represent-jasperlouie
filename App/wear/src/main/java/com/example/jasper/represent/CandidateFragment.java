package com.example.jasper.represent;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

/**
 * Created by Jasper on 3/2/2016.
 */
public class CandidateFragment extends Fragment {
    //UI References
    TextView titleV, subtitleV;
    ImageView portraitV;
    CharSequence title, subtitle;
    boolean isCandidate;
    String zip;


    public static CandidateFragment create(boolean isBlue, String title, CharSequence t, String z, JSONObject jo){
        CandidateFragment f = new CandidateFragment();
        Bundle b = new Bundle();
        b.putCharSequence("title", title);
        b.putCharSequence("subtitle", t);
        b.putBoolean("isblue", isBlue);
        b.putString("zip", z);
        b.putString("json", jo.toString());
        Log.d("T", "making cand page with " + jo.toString());
        f.setArguments(b);
        return f;
    }



    public View onCreateView(LayoutInflater inflater, ViewGroup containter, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.candidate_layout, null);
        titleV = (TextView)v.findViewById(R.id.title);
        titleV.setText((String)getArguments().get("title"));

        titleV = (TextView)v.findViewById(R.id.name);
        titleV.setText((String)getArguments().get("subtitle"));

        if(!getArguments().getBoolean("isblue")) {
            v.findViewById(R.id.back_color).setBackgroundColor(Color.parseColor("#FF2C2C"));
            int imageID = v.getResources().getIdentifier("drawable/r", null, getActivity().getPackageName());
            ((ImageView) v.findViewById(R.id.portrait)).setImageResource(imageID);
        }else{
            int imageID = v.getResources().getIdentifier("drawable/d", null, getActivity().getPackageName());
            ((ImageView) v.findViewById(R.id.portrait)).setImageResource(imageID);
        }

        try{
//                    String j =
//                    Log.d("T", "FAILLLLL");
            JSONObject jo = new JSONObject(getArguments().getString("json"));
            if(jo.getString("party").equals("I")){
                v.findViewById(R.id.back_color).setBackgroundColor(Color.parseColor("#7A7A7A"));
                ((ImageView) v.findViewById(R.id.portrait)).setVisibility(View.INVISIBLE);
            }

        }catch(Exception e){
            Log.d("T", "FAILLLLL");
        }

        ((LinearLayout)v.findViewById(R.id.whole_layout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(getActivity().getBaseContext(), WatchToPhoneService.class);
                sendIntent.putExtra("cmd", "details");
                JSONObject jo = null;
                try{
//                    String j =
//                    Log.d("T", "FAILLLLL");
                    jo = new JSONObject(getArguments().getString("json"));

                }catch(Exception e){
                    Log.d("T", "FAILLLLL");
                }
                sendIntent.putExtra("json", jo.toString());
                getActivity().startService(sendIntent);
            }
        });
        return v;

    }
}

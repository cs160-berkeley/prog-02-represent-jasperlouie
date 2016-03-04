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

/**
 * Created by Jasper on 3/2/2016.
 */
public class VoteFragment extends Fragment {
    //UI References
    TextView titleV, subtitleV;
    ImageView portraitV;
    CharSequence title, subtitle;
    boolean isCandidate;

    public static VoteFragment create(boolean isBlue, CharSequence t, CharSequence s){
        VoteFragment f = new VoteFragment();
        Bundle b = new Bundle();
        b.putCharSequence("title", t);
        b.putCharSequence("subtitle", s);
        b.putBoolean("isblue", isBlue);
//        b.putCharSequence("portrait", p);
        f.setArguments(b);
        return f;
    }



    public View onCreateView(LayoutInflater inflater, ViewGroup containter, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.vote_layout, null);
        titleV = (TextView)v.findViewById(R.id.location);
        titleV.setText((String)getArguments().get("title"));
        subtitleV = (TextView)v.findViewById(R.id.percentages);
        subtitleV.setText((String)getArguments().get("subtitle"));
        if(!getArguments().getBoolean("isblue")){
            v.findViewById(R.id.back_color).setBackgroundColor(Color.parseColor("#FF2C2C"));
        }

        return v;
    }
}

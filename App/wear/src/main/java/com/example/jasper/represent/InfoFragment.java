package com.example.jasper.represent;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Jasper on 3/2/2016.
 */
public class InfoFragment extends Fragment {
    //UI References
    TextView titleV, subtitleV;
    ImageView portraitV;
    CharSequence title, subtitle;
    boolean isCandidate;

    public static InfoFragment create(CharSequence t, CharSequence s, boolean ic){
        InfoFragment f = new InfoFragment();
        Bundle b = new Bundle();
        b.putCharSequence("title", t);
        b.putCharSequence("subtitle", s);
//        b.putCharSequence("portrait", p);
        b.putBoolean("isCandidate", ic);
        f.setArguments(b);
        return f;
    }



    public View onCreateView(LayoutInflater inflater, ViewGroup containter, Bundle savedInstanceState){
        if ((boolean) getArguments().get("isCandidate")){
            View v = inflater.inflate(R.layout.candidate_layout, null);
            titleV = (TextView)v.findViewById(R.id.title);
            titleV.setText((String)getArguments().get("title"));
            subtitleV = (TextView)v.findViewById(R.id.name);
            subtitleV.setText((String)getArguments().get("subtitle"));
//            portraitV = (ImageView)v.findViewById(R.id.portrait);
            return v;
        }else{
            View v = inflater.inflate(R.layout.vote_layout, null);
            titleV = (TextView)v.findViewById(R.id.location);
            titleV.setText((String)getArguments().get("title"));
            subtitleV = (TextView)v.findViewById(R.id.percentages);
            subtitleV.setText((String)getArguments().get("subtitle"));
            return v;
        }

    }
}

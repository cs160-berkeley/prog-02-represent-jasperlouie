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
public class CandidateFragment extends Fragment {
    //UI References
    TextView titleV, subtitleV;
    ImageView portraitV;
    CharSequence title, subtitle;
    boolean isCandidate;
    String zip;

    public static CandidateFragment create(boolean isBlue, CharSequence t, CharSequence s, String z){
        CandidateFragment f = new CandidateFragment();
        Bundle b = new Bundle();
        b.putCharSequence("title", t);
        b.putCharSequence("subtitle", s);
        b.putBoolean("isblue", isBlue);
        b.putString("zip", z);
        f.setArguments(b);
        return f;
    }



    public View onCreateView(LayoutInflater inflater, ViewGroup containter, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.candidate_layout, null);
        titleV = (TextView)v.findViewById(R.id.title);
        titleV.setText((String)getArguments().get("title"));
        subtitleV = (TextView)v.findViewById(R.id.name);
        subtitleV.setText((String)getArguments().get("subtitle"));
        if(!getArguments().getBoolean("isblue")){
            v.findViewById(R.id.back_color).setBackgroundColor(Color.parseColor("#FF2C2C"));
        }
        String img_name = ((String)getArguments().get("subtitle")).replaceAll("[^A-Za-z]+", "").toLowerCase();
        int imageID = this.getResources().getIdentifier("drawable/" + img_name, null, getActivity().getPackageName());
        ((ImageView) v.findViewById(R.id.portrait)).setImageResource(imageID);
        ((LinearLayout)v.findViewById(R.id.whole_layout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(getActivity().getBaseContext(), WatchToPhoneService.class);
                sendIntent.putExtra("cmd", "details");
                sendIntent.putExtra("zip", getArguments().getString("zip"));
                sendIntent.putExtra("name", ((String)getArguments().get("subtitle")));
                getActivity().startService(sendIntent);
            }
        });
        return v;

    }
}

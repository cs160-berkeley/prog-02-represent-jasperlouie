package com.example.jasper.represent;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Jasper on 3/2/2016.
 */
public class DetailsActivity extends AppCompatActivity {
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
            ((TextView) findViewById(R.id.title1)).setText(jo.getString("name"));
            ((TextView) findViewById(R.id.partyText1)).setText(jo.getString("party"));
            ((TextView) findViewById(R.id.positionText1)).setText(jo.getString("title"));
            ((TextView) findViewById(R.id.positionText1)).setText(jo.getString("title"));
            String img_name = jo.getString("name").replaceAll("[^A-Za-z]+", "").toLowerCase();
            int imageID = this.getResources().getIdentifier("drawable/" + img_name, null, this.getPackageName());
            ((ImageView) findViewById(R.id.portrait1)).setImageResource(imageID);
            imageID = this.getResources().getIdentifier("drawable/"+jo.getString("party")+"icon", null, this.getPackageName());
            ((ImageView) findViewById(R.id.partyIcon1)).setImageResource(imageID);
        }catch (Exception e){}


        seeMore1 = (Button) findViewById(R.id.button);
        seeMore2 = (Button) findViewById(R.id.button2);
        tv1 = (TextView) findViewById(R.id.committeeText);
        tv2 = (TextView) findViewById(R.id.billsText);
        tv1.post(new Runnable() {
            @Override
            public void run() {
                if(tv1.getLineCount() > 10){
                    tv1.setMaxLines(5);
                    linkButtonToTextField(seeMore1, tv1);
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
//
//    private void collapseTextView(TextView tv, int numLines){
//        ObjectAnimator animation = ObjectAnimator.ofInt(tv, "maxLines", numLines);
//        animation.setDuration(200).start();
//    }


}

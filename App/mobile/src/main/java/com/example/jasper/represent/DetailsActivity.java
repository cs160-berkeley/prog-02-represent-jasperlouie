package com.example.jasper.represent;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

        seeMore1 = (Button) findViewById(R.id.button);
        seeMore2 = (Button) findViewById(R.id.button2);
        tv1 = (TextView) findViewById(R.id.committeeText);
        tv2 = (TextView) findViewById(R.id.billsText);
        tv1.setMaxLines(3);
        tv2.setMaxLines(3);
        linkButtonToTextField(seeMore1, tv1);
        linkButtonToTextField(seeMore2, tv2);
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
        int collapsedMaxLines = 3;
        ObjectAnimator animation = ObjectAnimator.ofInt(tv, "maxLines",
                tv.getMaxLines() == collapsedMaxLines? tv.getLineCount() : collapsedMaxLines);
        if(tv.getMaxLines() == 3){
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

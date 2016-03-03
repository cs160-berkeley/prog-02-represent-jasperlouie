package com.example.jasper.represent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;


import java.util.ArrayList;

/**
 * Created by Jasper on 3/2/2016.
 */
public class CongressActivity extends AppCompatActivity {
    //UI references
    private ArrayList<CardView> cards;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congress2);
        cards = new ArrayList<CardView>();
        cards.add((CardView) findViewById(R.id.card_view1));
        cards.add((CardView) findViewById(R.id.card_view2));
        cards.add((CardView) findViewById(R.id.card_view3));
        for(CardView c :cards){
            c.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToDetails();
                }
            });
        }
    }


    private void goToDetails(){
        //do nothing for now besides go to next screen
        Intent intent = new Intent(this, DetailsActivity.class);
//        intent.putExtra("ZIP", zip);
        startActivity(intent);
    }
}

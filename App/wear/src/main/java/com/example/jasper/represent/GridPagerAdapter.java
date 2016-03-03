package com.example.jasper.represent;

import android.app.Fragment;
import android.content.Context;
import android.app.FragmentManager;
import android.graphics.drawable.Drawable;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jasper on 3/2/2016.
 */
public class GridPagerAdapter extends FragmentGridPagerAdapter {

    private final Context mContext;
    private List mRows;
    private String[] names = new String[3];
    private String[] titles = new String[3];
    private boolean[] isBlues = new boolean[3];
    float p1,p2;
    String location;

    public GridPagerAdapter(JSONArray ja, Context ctx, FragmentManager fm) {
        super(fm);
        mContext = ctx;
        try{

            for(int i = 0; i < 3; i++){
                JSONObject jo = ja.getJSONObject(i);
                names[i] = jo.getString("name");
                titles[i] = jo.getString("shorttitle");
                isBlues[i] = jo.getString("party").equals("democrat");

            }

            p1 = Float.parseFloat(ja.getJSONObject(3).getString("per1"));
            p2 = Float.parseFloat(ja.getJSONObject(3).getString("per2"));
            location = ja.getJSONObject(3).getString("location");
        }catch (Exception e){

        }
    }
    static final int[] BG_IMAGES = new int[] {
    //        R.drawable.debug_background_1, ...
    //R.drawable.debug_background_5
    };



// A simple container for static data in each page
private class Page {
    // static resources
    boolean isCandidate;
    boolean isBlue;
}

private class CandidatePage extends Page{
    String title;
    String name;
    //    int portraitRes;
    private CandidatePage(boolean ic, boolean ib, String t, String n){
        title = t;
        isBlue = ib;
        name = n;
        isCandidate = ic;
    }
}

private class VotePage extends Page{
    // static resources
    boolean isCandidate;
    boolean isBlue;
    String location, percentages;
    //    int portraitRes;
    private VotePage(boolean ic, boolean ib, String l, String per1, String per2){
        location = l;
        isBlue = ib;
        percentages = per1 + "          " + per2;
        isCandidate = ic;
    }
}
// Create a static set of pages in a 2D array
//private final Page[] PAGES = {new CandidatePage(true, isBlues[0], titles[0], names[0]),
//        new CandidatePage(true, isBlues[1], titles[1], names[1]),
//        new CandidatePage(true, isBlues[2], titles[2], names[2]),
//        new VotePage()};

    // Obtain the UI fragment at the specified position
    @Override
    public Fragment getFragment(int row, int col) {
        Page page;
        if(col < 3){
            page = new CandidatePage(true, isBlues[col], titles[col], names[col]);
        }else{
            page = new VotePage(false, p1 > p2, location, p1+"%",p2+"%");
        }

//        String title = page.titleRes != 0 ? mContext.getString(page.titleRes) : null;
//        String text = page.textRes != 0 ? mContext.getString(page.textRes) : null;
        if(page.isCandidate){
            CandidatePage cp = (CandidatePage) page;
            return CandidateFragment.create(page.isBlue, cp.title, cp.name);
        }else{
            VotePage vp = (VotePage) page;
            return VoteFragment.create(vp.isBlue,vp.location, vp.percentages);
        }
    }

    // Obtain the background image for the row
//    @Override
//    public Drawable getBackgroundForRow(int row) {
//        return mContext.getResources().getDrawable(
//                (BG_IMAGES[row % BG_IMAGES.length]), null);
//    }
    // Obtain the number of pages (vertical)
    public int getRowCount() {
        return 1;
    }

    // Obtain the number of pages (horizontal)
    @Override
    public int getColumnCount(int rowNum) {
        return 4;
    }
}
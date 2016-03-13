package com.example.jasper.represent;

import android.app.Fragment;
import android.content.Context;
import android.app.FragmentManager;
import android.graphics.drawable.Drawable;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.util.Log;
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
    private String[] names = new String[6];
    private String[] titles = new String[6];
    private boolean[] isBlues = new boolean[6];
    private JSONObject[] jos = new JSONObject[6];
    float p1,p2;
    String location;
    String zip;
    int numCand;

    public GridPagerAdapter(String z, Context ctx, FragmentManager fm) {
        super(fm);
        mContext = ctx;
        JSONArray ja = new JSONArray();
        try{
            ja = new JSONArray(z);
        }catch (Exception e){
            Log.d("T", "Corrupted message recieved: "+z);
        }
//        Log.d("T", "Making grid page adapter, "+arr.toString());
        try{
            numCand = ja.length()-1;
            for(int i = 0; i < ja.length() - 1; i++){
//                JSONObject jo = ja.getJSONObject(i);
                jos[i] = ja.getJSONObject(i);
                JSONObject repObj = ja.getJSONObject(i);
                names[i] = repObj.getString("first_name")+" "+repObj.getString("last_name");
                titles[i] = repObj.getString("title");
                isBlues[i] = repObj.getString("party").equals("D");
            }
            location = ja.getString(ja.length() - 1);
//            p1 = Float.parseFloat(ja.getJSONObject(3).getString("per1"));
//            p2 = Float.parseFloat(ja.getJSONObject(3).getString("per2"));
//            location = ja.getJSONObject(3).getString("locatio/n");
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
    private CandidatePage(boolean ic, String t, boolean ib, String n){
        isBlue = ib;
        name = n;
        isCandidate = ic;
        title = t;
    }
}

private class VotePage extends Page{
    // static resources
    //    int portraitRes;
    private VotePage(String county){
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
        if(col < numCand){
            page = new CandidatePage(true, titles[col], isBlues[col], names[col]);
        }else{
            page = new VotePage(location);
        }

//        String title = page.titleRes != 0 ? mContext.getString(page.titleRes) : null;
//        String text = page.textRes != 0 ? mContext.getString(page.textRes) : null;
        if(page.isCandidate){
            CandidatePage cp = (CandidatePage) page;
            Log.d("T", "making cand page with "+jos[col].toString());
            return CandidateFragment.create(page.isBlue, cp.title, cp.name, zip, jos[col]);
        }else{
            VotePage vp = (VotePage) page;
            return VoteFragment.create(location);
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
        return numCand+1;
    }
}
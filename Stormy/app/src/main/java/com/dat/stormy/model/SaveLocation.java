package com.dat.stormy.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by dat on 08/07/2015.
 */
public class SaveLocation {
    private String mKeyStore = "Location";
    Context mContext;

    public SaveLocation(Context context){
        this.mContext = context;
    }

    public void saveLocation(String place){
        SharedPreferences location = mContext.getApplicationContext().getSharedPreferences("KEYSTORED",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = location.edit();

        Set<String> set = location.getStringSet(mKeyStore,null);
        if(set==null){
            set = new HashSet<>();
            List<String> places = new ArrayList<String>();
            places.add(place);
            set.addAll(places);
        }
        else {
            set.add(place);
        }
        editor.putStringSet(mKeyStore,set);
        editor.commit();
    }

    public  String[] getLocation(){
        SharedPreferences location = mContext.getSharedPreferences("KEYSTORED",Context.MODE_PRIVATE);
        Set<String> set = location.getStringSet(mKeyStore,null);
        if(set==null) return null;
        else return (String[]) set.toArray();
    }
}

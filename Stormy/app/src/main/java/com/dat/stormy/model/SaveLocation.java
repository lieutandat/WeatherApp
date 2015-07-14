package com.dat.stormy.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

/**
 * Created by dat on 08/07/2015.
 */
public class SaveLocation {
    final private String mKeyStore = "DEFAULT_LOCATION";
    Context mContext;
    SharedPreferences mSharedPreferences;

    public SaveLocation(Context context){
        this.mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public void saveLocation(Locate locate){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
/*
        Set<String> set = mLocation.getStringSet(mKeyStore,null);
        if(set==null){
            set = new HashSet<>();
            List<String> places = new ArrayList<String>();
            places.add(place);
            set.addAll(places);
        }
        else {
            set.add(place);
        }
        editor.putStringSet(mKeyStore,set);*/
        Gson gson = new Gson();
        String json =  gson.toJson(locate);
        editor.putString(mKeyStore,json);
        editor.commit();
    }

    public  Locate getLocation(){
    /*
        Set<String> set = mLocation.getStringSet(mKeyStore,null);
        if(set==null) return null;
        else return (String[]) set.toArray();*/
        Gson gson = new Gson();
        String json = mSharedPreferences.getString(mKeyStore,"");
        Locate locate = gson.fromJson(json,Locate.class);
        return  locate;
    }
}

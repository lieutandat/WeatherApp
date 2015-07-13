package com.dat.stormy.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dat on 13/07/2015.
 */
public class Locates implements Serializable {
    private List<Locate> mLocates;

    public Locates(){
        mLocates = new ArrayList<Locate>();
    }

    public void addLocate(Locate locate){
        mLocates.add(locate);
    }

    public String[] getLocateName(){
        String[] locateName = new String[mLocates.size()];
        for(int i=0;i<mLocates.size();i++){
            locateName[i] = mLocates.get(i).getAddress();
        }
        return locateName;
    }

    public List<Locate> getLocateList(){
        return mLocates;
    }
}

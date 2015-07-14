package com.dat.stormy.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dat on 13/07/2015.
 */
public class Locates implements Serializable {
    private List<Locate> mLocates;

    public List<String> getLocateName(){
        List<String> locateName = new ArrayList<String>();
        for(int i=0;i<mLocates.size();i++){
            locateName.add(mLocates.get(i).getAddress());
        }
        return locateName;
    }

    public List<Locate> getLocates(){
        return mLocates;
    }

    public void setLocates(List<Locate> locates){
        mLocates = locates;
    }
}

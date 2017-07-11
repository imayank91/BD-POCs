package com.RMC.BDCloud.RealmDB.Model;

import io.realm.RealmObject;

/**
 * Created by amresh on 5/3/17.
 */

public class RMCImageNames extends RealmObject{
    public String imageNames;

    public String getImageName() {
        return imageNames;
    }

    public void setImageName(String imageName) {
        this.imageNames = imageName;
    }
}

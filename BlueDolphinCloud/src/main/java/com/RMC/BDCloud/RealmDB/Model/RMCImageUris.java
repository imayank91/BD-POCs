package com.RMC.BDCloud.RealmDB.Model;

import io.realm.RealmObject;

/**
 * Created by amresh on 5/3/17.
 */

public class RMCImageUris extends RealmObject {

    public String imageUri;

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}

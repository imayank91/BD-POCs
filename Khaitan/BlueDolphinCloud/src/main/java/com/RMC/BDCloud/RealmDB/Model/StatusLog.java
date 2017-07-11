package com.RMC.BDCloud.RealmDB.Model;

import io.realm.RealmObject;

/**
 * Created by mayanksaini on 22/02/17.
 */

public class StatusLog extends RealmObject {

    public String type;
    public String timestamp;
    public String photoUri;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }
}

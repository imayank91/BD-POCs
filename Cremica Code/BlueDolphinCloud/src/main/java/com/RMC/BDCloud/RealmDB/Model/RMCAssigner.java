package com.RMC.BDCloud.RealmDB.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mayanksaini on 18/11/16.
 */

public class RMCAssigner extends RealmObject {

    @PrimaryKey
    private String userId;
    private String organisationId;
    private String privelege;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(String organisationId) {
        this.organisationId = organisationId;
    }

    public String getPrivelege() {
        return privelege;
    }

    public void setPrivelege(String privelege) {
        this.privelege = privelege;
    }
}

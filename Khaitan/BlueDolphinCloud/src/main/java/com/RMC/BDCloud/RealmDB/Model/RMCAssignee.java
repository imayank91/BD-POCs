package com.RMC.BDCloud.RealmDB.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mayanksaini on 18/11/16.
 */

public class RMCAssignee extends RealmObject{

    @PrimaryKey
    private String userId;
    private String organisationId;

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
}

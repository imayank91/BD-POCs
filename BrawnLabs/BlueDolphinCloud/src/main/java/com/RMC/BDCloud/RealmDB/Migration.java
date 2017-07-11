package com.RMC.BDCloud.RealmDB;

import com.RMC.BDCloud.RealmDB.Model.StatusLog;

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmList;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by mayanksaini on 09/02/17.
 */

public class Migration implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

        RealmSchema schema = realm.getSchema();

        if (oldVersion == 3) {
            schema.get("VicinityBeacons")
                    .addField("beaconDetails", String.class);
            oldVersion++;
        }

        if (oldVersion == 4) {
            schema.get("RMCAssignment")
                    .addField("localStatus", String.class);
            oldVersion++;
        }

        if (oldVersion == 5) {
            schema.get("RMCUser")
                    .addField("isActive", boolean.class, FieldAttribute.INDEXED);

            schema.create("StatusLog")
                    .addField("type", String.class)
                    .addField("timestamp", String.class)
                    .addField("photoUri", String.class);

            schema.get("RMCAssignment").removeField("statusLog")
                    .addRealmListField("statusLog", schema.get("StatusLog"));
            oldVersion++;
        }

        if (oldVersion == 6) {
            schema.get("RMCAssignment")
                    .addField("isOpened", boolean.class);
            oldVersion++;
        }

        if (oldVersion == 7) {
            schema.get("RMCAssignment")
                    .addField("placeId", String.class);
            oldVersion++;
        }

        if (oldVersion == 8) {
            schema.get("RMCAssignment")
                    .addField("imageUrl", String.class);
            oldVersion++;
        }

        if (oldVersion == 9) {


            schema.get("RMCAssignment")
                    .removeField("lastseen")
                    .addField("imageUrl", String.class)
                    .addField("status", String.class)
                    .addField("isMarked", boolean.class)
                    .addField("lastseen", Date.class);


            oldVersion++;
        }
    }

    @Override
    public int hashCode() {
        return Migration.class.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Migration;
    }
}

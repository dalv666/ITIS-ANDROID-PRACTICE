package com.googlemaps.template.myapplication.database;


import android.support.annotation.NonNull;

import com.googlemaps.template.myapplication.model.GeoObject;

import java.util.List;

import io.realm.Realm;

public class GeoObjectHelper {

    @NonNull
    public static List<GeoObject> findAll(@NonNull Realm realm) {
        return realm.allObjects(GeoObject.class);
    }

    public static void save(@NonNull Realm realm, List<GeoObject> airports) {
        realm.beginTransaction();
        realm.clear(GeoObject.class);
        realm.copyToRealm(airports);
        realm.commitTransaction();
    }

    public static void delete(@NonNull Realm realm,GeoObject geoObject) {
        GeoObject toEdit = realm.where(GeoObject.class).equalTo("pos", geoObject.getPos()).findFirst();
        realm.beginTransaction();
        toEdit.removeFromRealm();
        realm.commitTransaction();
    }

    public static void update(@NonNull Realm realm,GeoObject geoObject) {
        GeoObject toEdit = realm.where(GeoObject.class).equalTo("pos", geoObject.getPos()).findFirst();
        realm.beginTransaction();
        toEdit.setName(geoObject.getName());
        realm.commitTransaction();
    }

    public static GeoObject findByPosition(@NonNull Realm realm,String position) {
        return realm.where(GeoObject.class).equalTo("pos", position).findFirst();
    }


    public static boolean isTableEmpty(@NonNull Realm realm){
        return findAll(realm).isEmpty();
    }

    public static void deleteAll(@NonNull Realm realm){
        realm.beginTransaction();
        realm.allObjects(GeoObject.class).clear();
        realm.commitTransaction();
    }
}

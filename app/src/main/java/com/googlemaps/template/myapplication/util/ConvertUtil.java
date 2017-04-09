package com.googlemaps.template.myapplication.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.googlemaps.template.myapplication.model.GeoObject;
import com.googlemaps.template.myapplication.model.GeoObjectVO;

import java.util.ArrayList;
import java.util.List;


public class ConvertUtil {

    public static List<GeoObject> jsonElementToGeoObjectsList(JsonElement jsonElement){
        JsonArray asJsonArray = jsonElement.getAsJsonObject().getAsJsonObject("response").getAsJsonObject("GeoObjectCollection").getAsJsonArray("featureMember");
        //Convert to modal
        List<GeoObjectVO> arrayGeoObjectVOs = new ArrayList<>();
        for (int i = 0; i < asJsonArray.size(); i++) {
            JsonObject GeoObjectVO = (JsonObject) ((JsonObject) asJsonArray.get(i)).get("GeoObject");
            JsonObject point = GeoObjectVO.getAsJsonObject("Point");
            JsonPrimitive name = GeoObjectVO.getAsJsonPrimitive("name");
            JsonPrimitive posPrimitive = point.getAsJsonPrimitive("pos");
            arrayGeoObjectVOs.add(new GeoObjectVO(name.getAsString(),posPrimitive.getAsString()));
        }
        return toGeoObjects(arrayGeoObjectVOs);
    }


    public static GeoObject toGeoObject(GeoObjectVO geoObjectVO){
        return new GeoObject(geoObjectVO.getName(),geoObjectVO.getPos());
    }

    public static GeoObjectVO toGeoObject(GeoObject geoObjectVO){
        return new GeoObjectVO(geoObjectVO.getName(),geoObjectVO.getPos());
    }

    public static List<GeoObjectVO> toVoGeoObjects(List<GeoObject> geoObjectsVO){
        ArrayList<GeoObjectVO> geoObjectVOs = new ArrayList<GeoObjectVO>();
        for (GeoObject geoObject : geoObjectsVO) {
            geoObjectVOs.add(toGeoObject(geoObject));
        }
        return geoObjectVOs;
    }

    public static List<GeoObject> toGeoObjects(List<GeoObjectVO> geoObjectsVO){
        ArrayList<GeoObject> geoObjects = new ArrayList<GeoObject>();
        for (GeoObjectVO geoObject : geoObjectsVO) {
            geoObjects.add(toGeoObject(geoObject));
        }
        return geoObjects;
    }
}

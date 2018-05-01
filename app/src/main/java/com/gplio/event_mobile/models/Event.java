package com.gplio.event_mobile.models;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.gplio.event_mobile.Utils;

import java.io.Serializable;

/**
 * Created by goncalopalaio on 29/04/18.
 */

public class Event implements Serializable {
    private static String TAG = "Event";
    public String description;
    public String location;
    public String author;
    public int category = 1;


    @SuppressLint("HardwareIds")
    public Event(String description, String location) {
        this.description = description;
        this.location = location;
        this.author = Build.SERIAL; // @note temporary value
    }

    public Event(String description, double lon, double lat) {
        this(description, Utils.lonLatToPoint(lon, lat));
    }

    public LatLng getLocationAsLatLng() {
        // @note this is not ideal
        String prefix = "POINT (";
        if (location != null) {

            int startIndex = location.indexOf(prefix);
            if (startIndex < 0) {
                return null;
            }

            int endIndex = location.indexOf(")", startIndex);
            if (endIndex < 0) {
                return null;
            }
            String latLon = location.substring(startIndex + prefix.length(), endIndex);
            String[] split = latLon.split(" ");

            if (split.length == 2) {
                String slat = split[1];
                String slon = split[0];
                try {
                    double lon = Double.parseDouble(slon);
                    double lat = Double.parseDouble(slat);


                    LatLng result = new LatLng(lat, lon);
                    Log.d(TAG, "getLocationAsLatLng: " + location + " lat:: " + lat + " lon:: " + lon + " result:: " + result);
                    return result;
                } catch (Exception e) {
                    Log.e(TAG, "Parsing location: " + e.getLocalizedMessage());
                }
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "Event{" +
                "description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", author='" + author + '\'' +
                ", category=" + category +
                '}';
    }
}

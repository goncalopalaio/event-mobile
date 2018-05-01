package com.gplio.event_mobile;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by goncalopalaio on 29/04/18.
 */

public class LocationProvider {
    private static String TAG = "LocationProvider";

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location lastLocation;

    public void subscribe(Context context, final LocationInterface listener) {
        // Acquire a reference to the system Location Manager

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           return;
        }

        if (locationManager == null) {
            Log.e(TAG, "Could not get location service");
            return;
        }

        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Location lastKnownLocation = locationManager
                .getLastKnownLocation(bestProvider);

        if (lastKnownLocation != null) {
            Log.d(TAG, "subscribe: lastKnownLocation: " + lastKnownLocation);
            listener.onLocationChanged(lastKnownLocation);
            lastLocation = lastKnownLocation;
        }


        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                listener.onLocationChanged(location);

                lastLocation = location;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 60, 0, locationListener);
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void unsubscribe() {
        locationManager.removeUpdates(locationListener);
    }

    public interface LocationInterface {
        public void onLocationChanged(Location location);
    }
}

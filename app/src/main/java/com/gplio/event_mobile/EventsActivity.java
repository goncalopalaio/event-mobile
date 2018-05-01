package com.gplio.event_mobile;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gplio.event_mobile.models.Event;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static String TAG = "EventsActivity";
    private ReportingApi.EventService eventService;
    private LocationProvider locationProvider;
    private GoogleMap map;
    private boolean hasPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        // Request permissions
        Permissions.requestPermissions(this);

        // Request location updates
        locationProvider = new LocationProvider();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_map:
                            case R.id.action_list:
                        }
                        return true;
                    }
                });

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intents.openDetails(EventsActivity.this, new Event("TEST EVENT"));
            }
        });

        eventService = ReportingApi.getEventInstance(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        locationProvider.subscribe(this, new LocationProvider.LocationInterface() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "Location changed: " + location + " lat: " + location.getLatitude() + " lng: " + location.getLongitude());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        locationProvider.unsubscribe();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        new Thread(new Runnable() {
            @Override
            public void run() {
                eventService.listAllEvents().enqueue(new Callback<List<Event>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Event>> call, @NonNull Response<List<Event>> response) {
                        List<Event> body = response.body();

                        if (body == null) {
                            Log.e(TAG, "Empty response body");
                            return;
                        }

                        int i = 0;
                        for (Event event : body) {
                            Log.d(TAG, "event: " + event.toString());
                            LatLng sydney = new LatLng(-34 + i, 151 + i);
                            map.addMarker(new MarkerOptions().position(sydney).title(event.description));
                            i+=2;

                        }

                        // Add a marker in Sydney and move the camera
                        LatLng sydney = new LatLng(-34, 151);
                        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Event>> call, @NonNull Throwable t) {
                        Log.e(TAG, "Failure: " + t.getLocalizedMessage());
                        Toast.makeText(EventsActivity.this, "Could not fetch the latest events", Toast.LENGTH_SHORT).show(); // @todo move string to the correct place
                    }
                });
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        hasPermissions = Permissions.resultingPermissions(requestCode, permissions, grantResults);
    }
}

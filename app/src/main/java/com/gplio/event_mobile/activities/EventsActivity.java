package com.gplio.event_mobile.activities;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gplio.event_mobile.Intents;
import com.gplio.event_mobile.LocationProvider;
import com.gplio.event_mobile.Permissions;
import com.gplio.event_mobile.R;
import com.gplio.event_mobile.ReportingApi;
import com.gplio.event_mobile.fragments.CategoryListDialogFragment;
import com.gplio.event_mobile.models.Event;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsActivity extends AppCompatActivity implements OnMapReadyCallback, CategoryListDialogFragment.Listener {
    private static String TAG = "EventsActivity";
    private ReportingApi.EventService eventService;
    private LocationProvider locationProvider;
    private GoogleMap map;
    private boolean hasPermissions;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        bindViews();

        // Request permissions
        Permissions.requestPermissions(this);

        // Request location updates
        locationProvider = new LocationProvider();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intents.openDetails(EventsActivity.this, new Event("TEST EVENT"));
            }
        });

        eventService = ReportingApi.getEventInstance(this);
    }

    private void bindViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.floatingActionButton);

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
                            i += 2;

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                CategoryListDialogFragment.newInstance().show(getSupportFragmentManager(), "CategoryListDialogFragment");
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public void onCategoryClicked(int position) {

    }
}

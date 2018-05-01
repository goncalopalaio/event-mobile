package com.gplio.event_mobile.activities;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
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
                Location lastLocation = locationProvider.getLastLocation();
                if (lastLocation == null) {
                    Intents.openNew(EventsActivity.this);
                } else {
                    Intents.openNew(EventsActivity.this, lastLocation.getLatitude(), lastLocation.getLongitude());
                }

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
                if (map != null) {
                    LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
                    map.moveCamera(CameraUpdateFactory.newLatLng(position));
                }
            }
        });

        fetchEvents();
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
        map.animateCamera(CameraUpdateFactory.zoomTo(10.0f));
        fetchEvents();
    }

    private void fetchEvents() {
        if (map == null) {
            Log.e(TAG, "fetchEvents: Map not ready");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                eventService.listAllEvents().enqueue(listEventsCallback);
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
            case R.id.action_all:
                fetchEvents();
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
    public void onCategoryClicked(final int position) {
        if (map == null) {
            Log.e(TAG, "onCategoryClicked: Map not ready");
            return;
        }

        map.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                eventService.filterByCategory(position + 1).enqueue(listEventsCallback); // @note using the position might not be the greatest idea in the long run
            }
        }).start();
    }

    Callback<List<Event>> listEventsCallback = new Callback<List<Event>>() {
        @Override
        public void onResponse(@NonNull Call<List<Event>> call, @NonNull Response<List<Event>> response) {
            List<Event> body = response.body();

            if (body == null) {
                Log.e(TAG, "Empty response body");
                return;
            }

            for (Event event : body) {
                Log.d(TAG, "event: " + event.toString());

                LatLng latLon = event.getLocationAsLatLng();
                if (latLon != null) {
                    MarkerOptions position = new MarkerOptions().position(latLon).title(event.description);
                    position = Event.addMarkerColor(event, position);

                    map.addMarker(position);
                }
            }
        }

        @Override
        public void onFailure(@NonNull Call<List<Event>> call, @NonNull Throwable t) {
            Log.e(TAG, "Failure: " + t.getLocalizedMessage());
            Toast.makeText(EventsActivity.this, "Could not fetch the latest events", Toast.LENGTH_SHORT).show(); // @todo move string to the correct place
        }
    };
}

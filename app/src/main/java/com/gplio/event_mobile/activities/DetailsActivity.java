package com.gplio.event_mobile.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gplio.event_mobile.Intents;
import com.gplio.event_mobile.R;
import com.gplio.event_mobile.ReportingApi;
import com.gplio.event_mobile.Utils;
import com.gplio.event_mobile.models.Event;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static String TAG = "DetailsActivity";

    private GoogleMap map;
    private EditText descriptionEditText;
    private TextView locationTextView;
    private Button saveButton;
    private LatLng location;
    private LatLng initialLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        bindViews();
        bindData();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCurrentEvent();
            }
        });

    }

    private void bindViews() {
        descriptionEditText = findViewById(R.id.descriptionTextView);
        locationTextView = findViewById(R.id.localizationTextView);
        saveButton = findViewById(R.id.saveButton);
    }

    private void bindData() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }

        if (intent.hasExtra(Intents.EXTRA_DETAILS_LAST_LAT) && intent.hasExtra(Intents.EXTRA_DETAILS_LAST_LON)) {
            double lat = intent.getDoubleExtra(Intents.EXTRA_DETAILS_LAST_LAT, 0);
            double lon = intent.getDoubleExtra(Intents.EXTRA_DETAILS_LAST_LON, 0);
            initialLocation = new LatLng(lat, lon);
            location = initialLocation;
            locationTextView.setText(Utils.lonLatToPoint(lon, lat));
        }

        Event event = (Event) intent.getSerializableExtra(Intents.EXTRA_DETAILS);
        if (event == null) {
            return;
        }

        descriptionEditText.setText(event.description);
    }

    private void saveCurrentEvent() {
        String description = Utils.safeGetText(descriptionEditText);

        if (location == null) {
            Log.e(TAG, "saveCurrentEvent: attempt to save without location");
            return;
        }

        Event event = new Event(description, location.longitude, location.latitude);
        saveEvent(event);
    }

    private void saveEvent(final Event event) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ReportingApi.getEventInstance(getApplicationContext()).createEvent(event).enqueue(new Callback<Event>() {
                    @Override
                    public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                        Log.d(TAG, "Response: " + response);
                    }

                    @Override
                    public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                        Log.e(TAG, "Failure: " + t);
                    }
                });
            }
        }).start();
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

        if (initialLocation != null) {
            map.addMarker(new MarkerOptions().position(initialLocation));
            map.moveCamera(CameraUpdateFactory.newLatLng(initialLocation));
        }
        map.animateCamera(CameraUpdateFactory.zoomTo(10.0f));

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                map.clear();

                location = latLng;
                locationTextView.setText(Utils.lonLatToPoint(latLng.longitude, latLng.latitude));

                MarkerOptions markerOptions = new MarkerOptions().position(latLng);

                String description = String.valueOf(descriptionEditText.getText());
                if (!TextUtils.isEmpty(description)) {
                    markerOptions.title(description);
                }

                map.addMarker(markerOptions);
                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });
    }
}

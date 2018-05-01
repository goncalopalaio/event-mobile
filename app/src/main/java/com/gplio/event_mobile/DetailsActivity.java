package com.gplio.event_mobile;

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

        Event event = (Event) intent.getSerializableExtra(Intents.EXTRA_DETAILS);
        if (event == null) {
            return;
        }

        descriptionEditText.setText(event.description);
    }
    private void saveCurrentEvent() {
        String description = Utils.safeGetText(descriptionEditText);
        Event event = new Event(description);
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
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                map.clear();

                location = latLng;
                locationTextView.setText(latLng.toString());

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

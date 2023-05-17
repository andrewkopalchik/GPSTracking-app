package com.example.gpstracking;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.os.Handler;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String HISTORY_LIST = "historyList";

    private TextView markerTimestamp;
    private GoogleMap mMap;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private final long MIN_TIME = 1000;
    private final long MIN_DIST = 5;

    private Handler coordinatesHandler;
    private Runnable coordinatesRunnable;


    private String deviceName;
    private EditText editTextLatitude;
    private EditText editTextLongitude;
    private Button showMyLocationButton;
    private Button historyButton;

    private Button startButton;
    private Button stopButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        markerTimestamp = new TextView(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deviceName = Build.MODEL;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        editTextLatitude = findViewById(R.id.editTextTextPersonName);
        editTextLongitude = findViewById(R.id.editTextTextPersonName2);
        startButton = findViewById(R.id.startButton);
        showMyLocationButton = findViewById(R.id.showMyLocationButton);
        showMyLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCurrentLocation();
                Toast.makeText(MainActivity.this, "SAVED", Toast.LENGTH_SHORT).show();
            }
        });

        historyButton = findViewById(R.id.historyButton);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent historyIntent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(historyIntent);
            }
        });



        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSavingCoordinates();
            }
        });

        /*

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSavingCoordinates();
            }
        });

         */

    }



    private void startSavingCoordinates() {
        coordinatesHandler = new Handler();
        coordinatesRunnable = new Runnable() {
            @Override
            public void run() {
                showCurrentLocation();
                Toast.makeText(MainActivity.this, "SAVED", Toast.LENGTH_SHORT).show();
                coordinatesHandler.postDelayed(this, 60000); // 1 хвилина
            }
        };
        coordinatesHandler.post(coordinatesRunnable);
    }

    private void stopSavingCoordinates() {
        if (coordinatesHandler != null) {
            coordinatesHandler.removeCallbacks(coordinatesRunnable);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                try {
                    editTextLatitude.setText(Double.toString(location.getLatitude()));
                    editTextLongitude.setText(Double.toString(location.getLongitude()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            try {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null) {
            LatLng currentLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(currentLocation).title(deviceName));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String currentTime = sdf.format(new Date());

            String historyEntry = deviceName + " - " + currentTime + " - " + lastKnownLocation.getLatitude() + ", " + lastKnownLocation.getLongitude();
            saveHistoryEntry(historyEntry);
        }
    }

    private void saveHistoryEntry(String historyEntry) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> historySet = sharedPreferences.getStringSet(HISTORY_LIST, new HashSet<String>());
        historySet.add(historyEntry);

        editor.putStringSet(HISTORY_LIST, historySet);
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }
}

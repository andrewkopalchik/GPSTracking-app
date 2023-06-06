package com.example.gpstracking;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    // Оголошення потрібних змінних
    private GoogleMap mMap;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private DatabaseReference databaseReference;

    private TextView distanceView;
    private float totalDistance = 0;
    private Location lastKnownLocation;

    private Button showMyLocationButton;
    private Button historyButton;
    private Button startButton;
    private Button endButton;
    private Button zoomInButton;
    private Button zoomOutButton;

    private Handler handler;
    private Runnable runnable;

    private String deviceName;

    private final long MIN_TIME = 1000;
    private final long MIN_DIST = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ініціалізація змінних
        deviceName = Build.MODEL;
        distanceView = findViewById(R.id.distanceView);

        zoomInButton = findViewById(R.id.zoomInButton);
        zoomOutButton = findViewById(R.id.zoomOutButton);

        databaseReference = FirebaseDatabase.getInstance().getReference("Locations");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Ініціалізація кнопок і їх слухачів
        initButtons();

        handler = new Handler();
    }

    // Ініціалізація кнопок і їх слухачів
    private void initButtons() {
        showMyLocationButton = findViewById(R.id.showMyLocationButton);
        showMyLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCurrentLocation();
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

        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSavingCoordinates();
            }
        });

        endButton = findViewById(R.id.endButton);
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSavingCoordinates();
            }
        });

        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });

        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });
    }

    // Коли карта готова
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                showCurrentLocation();
            }
        };

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        checkPermissionAndUpdateLocation();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
    }

    // Показ поточного місцезнаходження
    private void showCurrentLocation() {
        if (!checkPermissionAndUpdateLocation()) {
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        LatLng latLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title(deviceName));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        LocationData locationData = new LocationData(deviceName, lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), System.currentTimeMillis());
        saveLocationData(locationData);

        updateTotalDistance(lastKnownLocation);
    }

    // Оновлення загальної відстані
    private void updateTotalDistance(Location newLocation) {
        if (lastKnownLocation != null) {
            float[] distance = new float[1];
            Location.distanceBetween(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), newLocation.getLatitude(), newLocation.getLongitude(), distance);
            totalDistance += distance[0] / 1000; // Конвертування відстані до кілометрів
        }

        lastKnownLocation = newLocation; // Оновлення останнього відомого місцезнаходження
        distanceView.setText(String.format("Відстань: %.2f км", totalDistance));
    }

    // Перевірка дозволу та оновлення місцезнаходження
    private boolean checkPermissionAndUpdateLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
            return false;
        }
        return true;
    }

    // Збереження даних про місцезнаходження
    private void saveLocationData(LocationData locationData) {
        databaseReference.push().setValue(locationData);
        Toast.makeText(this,"SAVED TO BASE", Toast.LENGTH_LONG).show();
    }

    // Початок зберігання координат
    private void startSavingCoordinates() {
        runnable = new Runnable() {
            @Override
            public void run() {
                showCurrentLocation();
                handler.postDelayed(this, 10000);
            }
        };
        handler.post(runnable);
    }

    // Зупинка зберігання координат
    private void stopSavingCoordinates() {
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }
}

package com.example.gpstracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap myMap;
    private LocationManager locationManager;
    private LatLng currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {



        myMap = googleMap;
        LatLng uzhgorod = new LatLng(48.6208, 22.2879);
        myMap.addMarker(new MarkerOptions().position(uzhgorod).title("Ужгород"));
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uzhgorod, 15));




    }
}
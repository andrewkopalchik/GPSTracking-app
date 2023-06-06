package com.example.gpstracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private ListView listView;
    private Button backButton;
    private DatabaseReference databaseReference;
    private ArrayList<LocationData> historyList = new ArrayList<>();
    private ArrayList<String> displayList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listView = findViewById(R.id.listView);
        backButton = findViewById(R.id.backButton);

        databaseReference = FirebaseDatabase.getInstance().getReference("Locations");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        listView.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                historyList.clear();
                displayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    LocationData locationData = snapshot.getValue(LocationData.class);
                    historyList.add(locationData);
                    displayList.add("Device Name: " + locationData.getDeviceName() + ", Latitude: " + locationData.getLatitude() + ", Longitude: " + locationData.getLongitude() + ", Timestamp: " + locationData.getTimestamp());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HistoryActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocationData locationData = historyList.get(position);
                Intent intent = new Intent(HistoryActivity.this, MapActivity.class);
                intent.putExtra("deviceName", locationData.getDeviceName());
                intent.putExtra("latitude", locationData.getLatitude());
                intent.putExtra("longitude", locationData.getLongitude());
                intent.putExtra("timestamp", locationData.getTimestamp());
                startActivity(intent);
            }
        });
    }
}

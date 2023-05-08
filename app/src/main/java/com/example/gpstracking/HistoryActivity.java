package com.example.gpstracking;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private ListView listView;
    private Button backButton;
    private List<LocationData> historyList;
    private ArrayAdapter<LocationData> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listView = findViewById(R.id.listView);
        backButton = findViewById(R.id.backButton);
        historyList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Location");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyList);
        listView.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                historyList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    LocationData historyItem = snapshot.getValue(LocationData.class);
                    historyList.add(historyItem);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocationData selectedLocation = historyList.get(position);
                Intent intent = new Intent(HistoryActivity.this, MapActivity.class);
                intent.putExtra("latitude", Double.parseDouble(selectedLocation.getLatitude()));
                intent.putExtra("longitude", Double.parseDouble(selectedLocation.getLongitude()));
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}

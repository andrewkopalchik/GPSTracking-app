package com.example.gpstracking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HistoryActivity extends AppCompatActivity {
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String HISTORY_LIST = "historyList";

    private ListView listView;
    private Button backButton;
    private List<String> historyList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listView = findViewById(R.id.listView);
        backButton = findViewById(R.id.backButton);
        historyList = new ArrayList<>();



        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Set<String> historySet = sharedPreferences.getStringSet(HISTORY_LIST, null);

        if (historySet != null) {
            historyList.addAll(historySet);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = historyList.get(position);
                String[] locationData = selectedItem.split(", ");

                if (locationData.length >= 4) {
                    try {
                        double latitude = Double.parseDouble(locationData[1].split("=")[1]);
                        double longitude = Double.parseDouble(locationData[2].split("=")[1]);
                        long timestamp = Long.parseLong(locationData[3].split("=")[1]);

                        Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                        intent.putExtra("latitude", latitude);
                        intent.putExtra("longitude", longitude);
                        intent.putExtra("timestamp", timestamp);
                        startActivity(intent);
                        finish();
                    } catch (NumberFormatException e) {
                        Log.e("HistoryActivity", "Error parsing location data", e);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        Log.e("HistoryActivity", "Error splitting location data", e);
                    }
                }
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
package com.example.rollcall;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;


import androidx.activity.EdgeToEdge;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.net.Uri;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import android.content.SharedPreferences;
import com.google.gson.reflect.TypeToken;


public class MainActivity extends AppCompatActivity {
    private static final String SHARED_PREFS = "persist data";
    private static final String SHARED_PREFS_KEY = "list";
    private String name = "";
    private String scannedUrl = "";

    Button saveButton;
    EditText textInput;
    ListView listView;
    ArrayList<String> items = new ArrayList<>();
    ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        saveButton = findViewById(R.id.saveButton);
        textInput = findViewById(R.id.textInput);
        listView = findViewById(R.id.listView);
        listAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, items);

        listView.setAdapter(listAdapter);
        // Load data from SharedPreferences
        loadData();

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize the Intent Integrator
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setOrientationLocked(true); // Allow both portrait and landscape orientation
                integrator.setPrompt("Scan a QR code"); // Set a custom prompt message
                integrator.setBeepEnabled(false); // Disable beep sound
                integrator.initiateScan(); // Start QR code scanning
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = textInput.getText().toString();
                if (text == null || text.trim().equals("")) {
                    Toast.makeText(MainActivity.this, "Item is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (items.contains(text)) {
                    Toast.makeText(MainActivity.this, "Item has already been added", Toast.LENGTH_SHORT).show();
                    return;
                }
                items.add(text);
                listAdapter.notifyDataSetChanged();
                textInput.setText("");

                // Save the data to SharedPreferences
                saveData();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan canceled", Toast.LENGTH_SHORT).show();
            } else {
                scannedUrl = result.getContents(); // Assign scanned URL to scannedUrl
                Toast.makeText(this, "Scanned URL: " + scannedUrl, Toast.LENGTH_SHORT).show();
                // Handle the scanned QR code data (e.g., open a link)
                try {
                    name = extractNameFromWebsite(scannedUrl); // Extract website name
                    System.out.println("Name of the website: " + name);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    protected String extractNameFromWebsite(String scannedUrl) throws IOException {
        return "hello";
    }

    private void saveData() {
        SharedPreferences sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String jsonString = gson.toJson(items);
        editor.putString(SHARED_PREFS_KEY, jsonString);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp.getString(SHARED_PREFS_KEY, "");
        if (!json.isEmpty()) {
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            items.addAll(gson.fromJson(json, type));
        }
    }
}

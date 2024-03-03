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

    Button saveButton;
    EditText textInput;
    ListView listView;
    ArrayList<String> items = new ArrayList<>();
    ArrayAdapter<String> listAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        saveButton = (Button) findViewById(R.id.saveButton);
        textInput = (EditText) findViewById(R.id.textInput);
        listView = (ListView) findViewById(R.id.listView);
        listAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, items);

        listView.setAdapter(listAdapter);
        //load data from sp
        loadData();


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = textInput.getText().toString();
                if (text == null || text.trim().equals("")) {
                    Toast.makeText( MainActivity.this, "Item is empty", Toast.LENGTH_SHORT).
                            show();
                    return;
                }
                if (items.contains(text)) {
                    Toast.makeText(MainActivity.this, "Item has already been added", Toast.LENGTH_SHORT).show();
                    return;
                }
                items.add(text);
                listAdapter.notifyDataSetChanged();
                textInput.setText("");

                //save the data to SP
                saveData();
            }
        });
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
        if (json.isEmpty()) {
            return;
        }
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        items.addAll((gson.fromJson(json, type)));
    }
}

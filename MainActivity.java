package com.example.rollcall;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rollcall.databinding.ActivityMainBinding;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.net.Uri;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {
    private String scannedUrl = "";
    private String name = "";

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Initialize and set OnClickListener for scanButton
        Button scanButton = findViewById(R.id.button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize the Intent Integrator
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setOrientationLocked(false); // Allow both portrait and landscape orientation
                integrator.setPrompt("Scan a QR code"); // Set a custom prompt message
                integrator.setBeepEnabled(false); // Disable beep sound
                integrator.initiateScan(); // Start QR code scanning
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan canceled", Toast.LENGTH_SHORT).show();
            } else {
                String scannedText = result.getContents();
                Toast.makeText(this, "Scanned URL: " + scannedUrl, Toast.LENGTH_SHORT).show();
                // Handle the scanned QR code data (e.g., open a link)
                try {
                    name = extractNameFromWebsite(scannedUrl); // Extract website name
                    System.out.println("Name of the website: " + name);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private String extractNameFromWebsite(String websiteUrl) throws IOException {
        URL url = new URL(websiteUrl);
        URLConnection connection = url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String firstLine = reader.readLine(); // Read the first line of the website
        reader.close();

        // Assuming the name is in the format "name xyz"
        String[] parts = firstLine.split("\\s+");
        if (parts.length > 1 && parts[0].equalsIgnoreCase("name")) {
            return parts[1]; // Return the second part after "name"
        } else {
            return "Name not found";
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}

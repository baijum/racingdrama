package com.racingdrama;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SettingsActivity extends Activity {
    
    private RadioGroup bikeStyleGroup;
    private RadioButton styleClassic;
    private RadioButton styleSport;
    private RadioButton styleRetro;
    private Button saveButton;
    private Button backButton;
    
    // Shared preferences for storing settings
    public static final String PREFS_NAME = "BikeStylePrefs";
    public static final String PREF_BIKE_STYLE = "bikeStyle";
    public static final String STYLE_CLASSIC = "classic";
    public static final String STYLE_SPORT = "sport";
    public static final String STYLE_RETRO = "retro";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        // Initialize UI elements
        bikeStyleGroup = findViewById(R.id.bike_style_group);
        styleClassic = findViewById(R.id.style_classic);
        styleSport = findViewById(R.id.style_sport);
        styleRetro = findViewById(R.id.style_retro);
        saveButton = findViewById(R.id.save_button);
        backButton = findViewById(R.id.back_button);
        
        // Load current settings
        loadSettings();
        
        // Set up save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
                Toast.makeText(SettingsActivity.this, getString(R.string.settings_saved), Toast.LENGTH_SHORT).show();
            }
        });
        
        // Set up back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Return to previous activity
            }
        });
    }
    
    private void loadSettings() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String currentStyle = settings.getString(PREF_BIKE_STYLE, STYLE_CLASSIC);
        
        // Set the appropriate radio button
        if (currentStyle.equals(STYLE_CLASSIC)) {
            styleClassic.setChecked(true);
        } else if (currentStyle.equals(STYLE_SPORT)) {
            styleSport.setChecked(true);
        } else if (currentStyle.equals(STYLE_RETRO)) {
            styleRetro.setChecked(true);
        }
    }
    
    private void saveSettings() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        
        // Determine which style is selected
        String selectedStyle = STYLE_CLASSIC; // Default
        
        int selectedId = bikeStyleGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.style_classic) {
            selectedStyle = STYLE_CLASSIC;
        } else if (selectedId == R.id.style_sport) {
            selectedStyle = STYLE_SPORT;
        } else if (selectedId == R.id.style_retro) {
            selectedStyle = STYLE_RETRO;
        }
        
        // Save the selected style
        editor.putString(PREF_BIKE_STYLE, selectedStyle);
        editor.apply();
    }
} 
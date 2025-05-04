package com.racingdrama;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends Activity {
    
    // Game view
    private GameView gameView;
    private Button settingsButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // Set landscape orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        // Set content view to main layout
        setContentView(R.layout.activity_main);
        
        // Get screen dimensions
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        
        // Create game view
        gameView = new GameView(this, screenWidth, screenHeight);
        
        // Add game view to frame layout
        FrameLayout gameContainer = findViewById(R.id.game_container);
        gameContainer.addView(gameView);
        
        // Set up settings button
        settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pause the game
                gameView.surfaceDestroyed(gameView.getHolder());
                
                // Launch settings activity
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // Pause game when activity is paused
        if (gameView != null) {
            gameView.surfaceDestroyed(gameView.getHolder());
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Resume game when activity is resumed
        if (gameView != null && gameView.getHolder().getSurface().isValid()) {
            gameView.surfaceCreated(gameView.getHolder());
        }
    }
}
package com.racingdrama;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

/**
 * Manages different bike styles and provides methods to load bike images
 * based on the selected style from settings.
 */
public class BikeStyleManager {
    
    // Style constants
    public static final String STYLE_CLASSIC = "classic";
    public static final String STYLE_SPORT = "sport";
    public static final String STYLE_RETRO = "retro";
    
    // Preference settings
    private static final String PREFS_NAME = "BikeStylePrefs";
    private static final String PREF_BIKE_STYLE = "bikeStyle";
    
    // Context for loading resources
    private final Context context;
    
    // Current style
    private String currentStyle;
    
    // Bike images
    private Bitmap bikeNormalImg;
    private Bitmap bikeWheelieImg;
    private Bitmap bikeJumpImg;
    
    // Base bike vector drawables (unmodified)
    private VectorDrawableCompat baseBikeNormalVector;
    private VectorDrawableCompat baseBikeWheelieVector;
    private VectorDrawableCompat baseBikeJumpVector;
    
    // Base bike images (bitmap versions of vectors)
    private Bitmap baseBikeNormal;
    private Bitmap baseBikeWheelie;
    private Bitmap baseBikeJump;
    
    // Color modifications for sport style
    private static final float[] SPORT_COLOR_MATRIX = {
        1.1f, 0, 0, 0, 10,    // Red
        0, 0.9f, 0, 0, -10,   // Green
        0, 0, 1.2f, 0, 40,    // Blue
        0, 0, 0, 1, 0         // Alpha
    };
    
    // Color modifications for retro style
    private static final float[] RETRO_COLOR_MATRIX = {
        1.2f, 0.2f, 0.2f, 0, 20,  // Red
        0.2f, 0.9f, 0.1f, 0, 10,  // Green
        0.1f, 0.1f, 0.6f, 0, 0,   // Blue
        0, 0, 0, 1, 0              // Alpha
    };
    
    /**
     * Constructor
     * @param context Application context
     */
    public BikeStyleManager(Context context) {
        this.context = context;
        
        // Load the base images
        loadBaseImages();
        
        // Load the current style from preferences
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        this.currentStyle = settings.getString(PREF_BIKE_STYLE, STYLE_CLASSIC);
        
        // Apply style to bike images
        applyStyleToBikeImages();
    }
    
    /**
     * Loads the base bike images from vector drawable resources
     */
    private void loadBaseImages() {
        // Load vector drawables
        baseBikeNormalVector = VectorDrawableCompat.create(
                context.getResources(), 
                context.getResources().getIdentifier("bike_normal", "drawable", context.getPackageName()),
                null);
        baseBikeWheelieVector = VectorDrawableCompat.create(
                context.getResources(), 
                context.getResources().getIdentifier("bike_wheelie", "drawable", context.getPackageName()),
                null);
        baseBikeJumpVector = VectorDrawableCompat.create(
                context.getResources(), 
                context.getResources().getIdentifier("bike_jump", "drawable", context.getPackageName()),
                null);
        
        // Convert vector drawables to bitmaps
        baseBikeNormal = vectorToBitmap(baseBikeNormalVector);
        baseBikeWheelie = vectorToBitmap(baseBikeWheelieVector);
        baseBikeJump = vectorToBitmap(baseBikeJumpVector);
    }
    
    /**
     * Converts a vector drawable to bitmap
     * @param vectorDrawable The vector drawable to convert
     * @return Bitmap representation of the vector drawable
     */
    private Bitmap vectorToBitmap(VectorDrawableCompat vectorDrawable) {
        if (vectorDrawable == null) {
            return null;
        }
        
        Bitmap bitmap = Bitmap.createBitmap(
                vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        
        return bitmap;
    }
    
    /**
     * Applies the current style to the bike images
     */
    private void applyStyleToBikeImages() {
        // Apply style modifications
        if (STYLE_CLASSIC.equals(currentStyle)) {
            // Use the original images for classic style
            bikeNormalImg = baseBikeNormal.copy(Bitmap.Config.ARGB_8888, true);
            bikeWheelieImg = baseBikeWheelie.copy(Bitmap.Config.ARGB_8888, true);
            bikeJumpImg = baseBikeJump.copy(Bitmap.Config.ARGB_8888, true);
        } else if (STYLE_SPORT.equals(currentStyle)) {
            // Apply Sport style colors
            Paint sportPaint = new Paint();
            ColorMatrix sportColorMatrix = new ColorMatrix(SPORT_COLOR_MATRIX);
            sportPaint.setColorFilter(new ColorMatrixColorFilter(sportColorMatrix));
            
            bikeNormalImg = applyColorFilter(baseBikeNormal, sportPaint);
            bikeWheelieImg = applyColorFilter(baseBikeWheelie, sportPaint);
            bikeJumpImg = applyColorFilter(baseBikeJump, sportPaint);
        } else if (STYLE_RETRO.equals(currentStyle)) {
            // Apply Retro style colors
            Paint retroPaint = new Paint();
            ColorMatrix retroColorMatrix = new ColorMatrix(RETRO_COLOR_MATRIX);
            retroPaint.setColorFilter(new ColorMatrixColorFilter(retroColorMatrix));
            
            bikeNormalImg = applyColorFilter(baseBikeNormal, retroPaint);
            bikeWheelieImg = applyColorFilter(baseBikeWheelie, retroPaint);
            bikeJumpImg = applyColorFilter(baseBikeJump, retroPaint);
        }
    }
    
    /**
     * Apply color filter to a bitmap
     * @param source Source bitmap
     * @param paint Paint with color filter
     * @return Filtered bitmap
     */
    private Bitmap applyColorFilter(Bitmap source, Paint paint) {
        Bitmap result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(source, 0, 0, paint);
        return result;
    }
    
    /**
     * Reload bike images if the style has changed
     */
    public void reloadIfStyleChanged() {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        String savedStyle = settings.getString(PREF_BIKE_STYLE, STYLE_CLASSIC);
        
        if (!currentStyle.equals(savedStyle)) {
            currentStyle = savedStyle;
            applyStyleToBikeImages();
        }
    }
    
    /**
     * Get the normal bike image
     * @return Normal bike bitmap
     */
    public Bitmap getBikeNormalImg() {
        return bikeNormalImg;
    }
    
    /**
     * Get the wheelie bike image
     * @return Wheelie bike bitmap
     */
    public Bitmap getBikeWheelieImg() {
        return bikeWheelieImg;
    }
    
    /**
     * Get the jump bike image
     * @return Jump bike bitmap
     */
    public Bitmap getBikeJumpImg() {
        return bikeJumpImg;
    }
    
    /**
     * Get the current style
     * @return Current bike style
     */
    public String getCurrentStyle() {
        return currentStyle;
    }
} 
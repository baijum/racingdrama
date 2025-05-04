package com.racingdrama;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

public class TouchButton {
    private int x;
    private int y;
    private int width;
    private int height;
    private String text;
    private int color;
    private int textColor;
    private boolean pressed;
    private Rect rect;
    
    public TouchButton(int x, int y, int width, int height, String text, int color, int textColor) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.color = color;
        this.textColor = textColor;
        this.pressed = false;
        this.rect = new Rect(x, y, x + width, y + height);
    }
    
    public void draw(Canvas canvas) {
        // Create paint objects
        Paint buttonPaint = new Paint();
        Paint borderPaint = new Paint();
        Paint textPaint = new Paint();
        
        // Set button paint properties
        buttonPaint.setColor(color);
        if (pressed) {
            // Make button darker when pressed
            buttonPaint.setAlpha(200);
        }
        
        // Set border paint properties
        borderPaint.setColor(Color.WHITE);
        borderPaint.setAlpha(100);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(2);
        
        // Set text paint properties
        textPaint.setColor(textColor);
        
        // Adjust text size based on button size for better proportions
        // Make arrows larger and more visible
        if (text.equals("←") || text.equals("→") || text.equals("↑") || text.equals("↓")) {
            // For arrow buttons, use a size proportional to the button dimensions
            textPaint.setTextSize(Math.min(width, height) * 0.6f);
        } else {
            // For other buttons (like "W", "J", "Reset", etc.)
            textPaint.setTextSize(Math.min(width, height) * 0.4f);
        }
        
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setTextAlign(Paint.Align.CENTER);
        
        // Draw button background
        canvas.drawRect(rect, buttonPaint);
        
        // Draw button border
        canvas.drawRect(rect, borderPaint);
        
        // Draw button text
        float textX = x + width / 2f;
        float textY = y + height / 2f - ((textPaint.descent() + textPaint.ascent()) / 2);
        canvas.drawText(text, textX, textY, textPaint);
    }
    
    public boolean isPressed(float touchX, float touchY) {
        return rect.contains((int) touchX, (int) touchY);
    }
    
    public boolean isPressed() {
        return pressed;
    }
    
    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }
    
    // Getters
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public String getText() {
        return text;
    }
}
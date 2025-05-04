package com.racingdrama;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class VirtualJoystick {
    // Base position (center of the joystick base)
    private int baseX;
    private int baseY;
    private int baseRadius;
    
    // Stick position (center of the movable stick)
    private float stickX;
    private float stickY;
    private int stickRadius;
    
    // Colors
    private int baseColor;
    private int stickColor;
    
    // Joystick state
    private boolean isActive;
    private float deltaX;
    private float deltaY;
    private float distance;
    private float angle;
    
    // Movement threshold (minimum distance to register movement)
    private float movementThreshold;
    
    // Multi-touch support
    private int activePointerId = -1;
    
    public VirtualJoystick(int baseX, int baseY, int baseRadius) {
        this.baseX = baseX;
        this.baseY = baseY;
        this.baseRadius = baseRadius;
        
        // Initialize stick at the center of the base
        this.stickX = baseX;
        this.stickY = baseY;
        this.stickRadius = baseRadius / 2;
        
        // Set default colors
        this.baseColor = Color.argb(100, 100, 100, 100);
        this.stickColor = Color.argb(180, 200, 200, 200);
        
        // Initialize state
        this.isActive = false;
        this.deltaX = 0;
        this.deltaY = 0;
        this.distance = 0;
        this.angle = 0;
        
        // Set movement threshold (10% of base radius)
        this.movementThreshold = baseRadius * 0.1f;
    }
    
    public void draw(Canvas canvas) {
        // Create paint objects
        Paint basePaint = new Paint();
        Paint stickPaint = new Paint();
        Paint borderPaint = new Paint();
        
        // Set paint properties
        basePaint.setColor(baseColor);
        basePaint.setStyle(Paint.Style.FILL);
        basePaint.setAntiAlias(true);
        
        stickPaint.setColor(stickColor);
        stickPaint.setStyle(Paint.Style.FILL);
        stickPaint.setAntiAlias(true);
        
        borderPaint.setColor(Color.WHITE);
        borderPaint.setAlpha(100);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(2);
        borderPaint.setAntiAlias(true);
        
        // Draw joystick base
        canvas.drawCircle(baseX, baseY, baseRadius, basePaint);
        canvas.drawCircle(baseX, baseY, baseRadius, borderPaint);
        
        // Draw joystick stick
        canvas.drawCircle(stickX, stickY, stickRadius, stickPaint);
        canvas.drawCircle(stickX, stickY, stickRadius, borderPaint);
    }
    
    /**
     * Legacy method for backward compatibility
     */
    public boolean onTouchEvent(float touchX, float touchY, int action) {
        return onTouchEvent(touchX, touchY, action, 0);
    }
    
    /**
     * Handle touch events with pointer ID tracking for multi-touch support
     */
    public boolean onTouchEvent(float touchX, float touchY, int action, int pointerId) {
        switch (action) {
            case android.view.MotionEvent.ACTION_DOWN:
            case android.view.MotionEvent.ACTION_POINTER_DOWN:
                // Check if touch is within the base circle
                float touchDistance = distance(baseX, baseY, touchX, touchY);
                if (touchDistance <= baseRadius) {
                    isActive = true;
                    activePointerId = pointerId;
                    updateStickPosition(touchX, touchY);
                    return true;
                }
                return false;
                
            case android.view.MotionEvent.ACTION_MOVE:
                if (isActive && (activePointerId == pointerId || activePointerId == -1)) {
                    updateStickPosition(touchX, touchY);
                    return true;
                }
                return false;
                
            case android.view.MotionEvent.ACTION_UP:
            case android.view.MotionEvent.ACTION_POINTER_UP:
            case android.view.MotionEvent.ACTION_CANCEL:
                if (isActive && (activePointerId == pointerId || activePointerId == -1)) {
                    isActive = false;
                    activePointerId = -1;
                    resetStick();
                    return true;
                }
                return false;
                
            default:
                return false;
        }
    }
    
    private void updateStickPosition(float touchX, float touchY) {
        // Calculate distance from base center to touch point
        distance = distance(baseX, baseY, touchX, touchY);
        
        // Calculate angle
        angle = angle(baseX, baseY, touchX, touchY);
        
        // If touch is outside the base radius, limit the stick position to the base perimeter
        if (distance > baseRadius) {
            // Calculate stick position on the perimeter
            stickX = baseX + (float) (Math.cos(angle) * baseRadius);
            stickY = baseY + (float) (Math.sin(angle) * baseRadius);
        } else {
            // Set stick position to touch position
            stickX = touchX;
            stickY = touchY;
        }
        
        // Calculate delta values (normalized from -1 to 1)
        deltaX = (stickX - baseX) / baseRadius;
        deltaY = (stickY - baseY) / baseRadius;
    }
    
    private void resetStick() {
        // Reset stick to center
        stickX = baseX;
        stickY = baseY;
        
        // Reset state
        deltaX = 0;
        deltaY = 0;
        distance = 0;
        angle = 0;
    }
    
    private float distance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
    
    private float angle(float x1, float y1, float x2, float y2) {
        return (float) Math.atan2(y2 - y1, x2 - x1);
    }
    
    // Returns true if joystick is active and moved beyond threshold
    public boolean isMoving() {
        return isActive && distance > movementThreshold;
    }
    
    // Get horizontal movement (-1 to 1)
    public float getHorizontalMovement() {
        return deltaX;
    }
    
    // Get vertical movement (-1 to 1)
    public float getVerticalMovement() {
        return deltaY;
    }
    
    // Get the primary direction as a string (for compatibility with existing code)
    public String getDirection() {
        if (!isMoving()) {
            return null;
        }
        
        // Determine the primary direction based on the angle
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            // Horizontal movement is stronger
            return deltaX > 0 ? "right" : "left";
        } else {
            // Vertical movement is stronger
            return deltaY > 0 ? "down" : "up";
        }
    }
    
    // Check if the joystick is currently active
    public boolean isActive() {
        return isActive;
    }
    
    // Get the base position
    public int getBaseX() {
        return baseX;
    }
    
    public int getBaseY() {
        return baseY;
    }
    
    // Get the base radius
    public int getBaseRadius() {
        return baseRadius;
    }
    
    // Set colors
    public void setBaseColor(int baseColor) {
        this.baseColor = baseColor;
    }
    
    public void setStickColor(int stickColor) {
        this.stickColor = stickColor;
    }
}
package com.racingdrama;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.HashMap;
import java.util.Map;

public class Player {
    // Bike images for different states
    private Bitmap normalImage;
    private Bitmap wheelieImage;
    private Bitmap jumpImage;
    
    // Current image and dimensions
    private Bitmap currentImage;
    private int width;
    private int height;
    
    // Position and movement
    private int x;
    private int y;
    private int speed;
    private Rect collisionRect;
    
    // Screen and road dimensions
    private int screenWidth;
    private int screenHeight;
    private int roadTopBoundary;
    private int roadBottomBoundary;
    private int roadLeftBoundary;
    private int roadRightBoundary;
    
    // Stunt properties
    private boolean performingStunt;
    private String stuntType;
    private String lastStuntType;
    private int stuntTimer;
    private int stuntDuration;
    private int stuntCooldown;
    private int stuntCooldownDuration;
    
    // Stunt score bonuses
    private Map<String, Integer> stuntPoints;
    
    // Particle effects
    private boolean showSpeedLines;
    private boolean showDust;
    private boolean showStars;
    private int effectTimer;
    
    // Effect images
    private Bitmap speedLinesImg;
    private Bitmap dustImg;
    private Bitmap stuntStarsImg;
    
    public Player(Bitmap normalImage, Bitmap wheelieImage, Bitmap jumpImage, int screenWidth, int screenHeight) {
        this.normalImage = normalImage;
        this.wheelieImage = wheelieImage;
        this.jumpImage = jumpImage;
        
        // Set screen dimensions
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        
        // Set road boundaries
        this.roadLeftBoundary = 150; // Left edge of the road
        this.roadRightBoundary = screenWidth - 50; // Right edge of the road
        this.roadTopBoundary = 50; // Top edge of the road
        this.roadBottomBoundary = screenHeight - 50; // Bottom edge of the road
        
        // Set current image and dimensions
        this.currentImage = normalImage;
        this.width = normalImage.getWidth();
        this.height = normalImage.getHeight();
        
        // Set initial position - safely within the road
        this.x = 200; // Start at a safe position on the left side of the road
        this.y = screenHeight - 150;
        this.speed = 5;
        
        // Initialize collision rectangle
        this.collisionRect = new Rect(x, y, x + width, y + height);
        
        // Initialize stunt properties
        this.performingStunt = false;
        this.stuntType = null;
        this.lastStuntType = null;
        this.stuntTimer = 0;
        this.stuntDuration = 60;  // frames (1 second at 60 FPS)
        this.stuntCooldown = 0;
        this.stuntCooldownDuration = 90;  // frames (1.5 seconds at 60 FPS)
        
        // Initialize stunt points
        this.stuntPoints = new HashMap<>();
        this.stuntPoints.put("wheelie", 100);
        this.stuntPoints.put("jump", 200);
        
        // Initialize particle effects
        this.showSpeedLines = false;
        this.showDust = false;
        this.showStars = false;
        this.effectTimer = 0;
    }
    
    public void setEffectImages(Bitmap speedLinesImg, Bitmap dustImg, Bitmap stuntStarsImg) {
        this.speedLinesImg = speedLinesImg;
        this.dustImg = dustImg;
        this.stuntStarsImg = stuntStarsImg;
    }
    
    public void draw(Canvas canvas) {
        // Draw particle effects behind the bike
        if (showSpeedLines && speedLinesImg != null) {
            canvas.drawBitmap(speedLinesImg, x - 80, y + 20, null);
        }
        
        if (showDust && dustImg != null) {
            canvas.drawBitmap(dustImg, x - 10, y + height - 20, null);
        }
        
        // Draw the bike image
        canvas.drawBitmap(currentImage, x, y, null);
        
        // Draw stunt stars above the bike if performing a stunt
        if (showStars && stuntStarsImg != null) {
            canvas.drawBitmap(stuntStarsImg, x - 25, y - 60, null);
        }
    }
    
    public boolean startStunt(String stuntType) {
        if (stuntCooldown <= 0 && !performingStunt) {
            performingStunt = true;
            this.stuntType = stuntType;
            lastStuntType = stuntType;
            stuntTimer = stuntDuration;
            
            // Set the appropriate image
            if ("wheelie".equals(stuntType)) {
                currentImage = wheelieImage;
                showSpeedLines = true;
            } else if ("jump".equals(stuntType)) {
                currentImage = jumpImage;
                showStars = true;
            }
            
            // Show dust effect
            showDust = true;
            effectTimer = 20;  // Show effects for 20 frames
            
            return true;
        }
        return false;
    }
    
    public void endStunt() {
        performingStunt = false;
        stuntType = null;
        currentImage = normalImage;
        stuntCooldown = stuntCooldownDuration;
    }
    
    public void update() {
        // Update stunt timer
        if (performingStunt) {
            stuntTimer--;
            if (stuntTimer <= 0) {
                endStunt();
            }
        }
        
        // Update stunt cooldown
        if (stuntCooldown > 0) {
            stuntCooldown--;
        }
        
        // Update effect timer
        if (effectTimer > 0) {
            effectTimer--;
        } else {
            showSpeedLines = false;
            showDust = false;
            showStars = false;
        }
        
        // Update collision rectangle
        collisionRect.set(x, y, x + width, y + height);
    }
    
    public void move(String direction, String stunt) {
        // Handle player movement
        if (direction != null) {
            if ("left".equals(direction)) {
                // Move left, but not beyond the left road boundary
                x = Math.max(roadLeftBoundary, x - speed);
            } else if ("right".equals(direction)) {
                // Move right, but not beyond the right road boundary
                x = Math.min(roadRightBoundary - width, x + speed);
            } else if ("up".equals(direction)) {
                // Move up, but not beyond the top road boundary
                y = Math.max(roadTopBoundary, y - speed);
            } else if ("down".equals(direction)) {
                // Move down, but not beyond the bottom road boundary
                y = Math.min(roadBottomBoundary - height, y + speed);
            }
            
            // Ensure the bike stays within road boundaries (additional safety check)
            x = Math.max(roadLeftBoundary, Math.min(roadRightBoundary - width, x));
            y = Math.max(roadTopBoundary, Math.min(roadBottomBoundary - height, y));
        }
        
        // Handle stunts
        if (stunt != null) {
            if ("wheelie".equals(stunt)) {
                startStunt("wheelie");
            } else if ("jump".equals(stunt)) {
                startStunt("jump");
            }
        }
        
        // Update collision rectangle
        collisionRect.set(x, y, x + width, y + height);
    }
    
    // Getters and setters
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
    
    public int getSpeed() {
        return speed;
    }
    
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    
    public Rect getCollisionRect() {
        return collisionRect;
    }
    
    public boolean isPerformingStunt() {
        return performingStunt;
    }
    
    public String getStuntType() {
        return stuntType;
    }
    
    public String getLastStuntType() {
        return lastStuntType;
    }
    
    public int getStuntCooldown() {
        return stuntCooldown;
    }
    
    public int getStuntCooldownDuration() {
        return stuntCooldownDuration;
    }
    
    public int getStuntPoints(String stuntType) {
        return stuntPoints.getOrDefault(stuntType, 0);
    }
    
    // Road boundary getters
    public int getRoadLeftBoundary() {
        return roadLeftBoundary;
    }
    
    public int getRoadRightBoundary() {
        return roadRightBoundary;
    }
    
    public int getRoadTopBoundary() {
        return roadTopBoundary;
    }
    
    public int getRoadBottomBoundary() {
        return roadBottomBoundary;
    }
    
    // Method for continuous joystick movement
    public void moveWithJoystick(float horizontalInput, float verticalInput) {
        // Scale the input by speed
        int deltaX = (int)(horizontalInput * speed);
        int deltaY = (int)(verticalInput * speed);
        
        // Apply movement
        x += deltaX;
        y += deltaY;
        
        // Ensure the bike stays within road boundaries
        x = Math.max(roadLeftBoundary, Math.min(roadRightBoundary - width, x));
        y = Math.max(roadTopBoundary, Math.min(roadBottomBoundary - height, y));
        
        // Update collision rectangle
        collisionRect.set(x, y, x + width, y + height);
    }
    
    // Method to reset position if bike gets stuck
    public void resetPosition() {
        // Reset to a safe position within the road boundaries
        this.x = roadLeftBoundary + 50; // 50 pixels from the left road boundary
        this.y = roadBottomBoundary - height - 50; // 50 pixels from the bottom road boundary
        
        // Update collision rectangle
        collisionRect.set(x, y, x + width, y + height);
    }
}
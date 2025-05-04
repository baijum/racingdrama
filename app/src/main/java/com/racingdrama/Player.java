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
    
    // Screen dimensions
    private int screenWidth;
    private int screenHeight;
    
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
        
        // Set current image and dimensions
        this.currentImage = normalImage;
        this.width = normalImage.getWidth();
        this.height = normalImage.getHeight();
        
        // Set screen dimensions
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        
        // Set initial position
        this.x = screenWidth / 4;
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
            if ("left".equals(direction) && x > 150) {  // Left road boundary
                x -= speed;
            } else if ("right".equals(direction) && x < 650 - width) {  // Right road boundary
                x += speed;
            } else if ("up".equals(direction) && y > 0) {
                y -= speed;
            } else if ("down".equals(direction) && y < screenHeight - height) {
                y += speed;
            }
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
}
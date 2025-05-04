package com.racingdrama;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
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
    
    // Physics and animation properties
    private float leanAngle = 0; // Angle for bike leaning (in degrees)
    private float maxLeanAngle = 20; // Maximum lean angle
    private float leanSpeed = 2.0f; // How quickly the bike leans
    private float targetLeanAngle = 0; // Target angle when turning
    
    // Suspension properties
    private float suspensionOffset = 0; // Vertical offset for suspension effect
    private float maxSuspensionCompress = 10; // Maximum suspension compression
    private float suspensionSpeed = 0.8f; // How quickly suspension compresses/extends
    private boolean isLanding = false; // Flag for when bike is landing from a jump
    
    // Movement history for smoother turning
    private float lastHorizontalInput = 0;
    private float horizontalInputSmoothing = 0.2f; // Smoothing factor for turning
    
    // Paint for drawing with transformations
    private Paint bikePaint;
    private Matrix transformMatrix;
    
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
        
        // Initialize physics and animation properties
        this.bikePaint = new Paint();
        this.transformMatrix = new Matrix();
        
        // Enable filtering for smoother rotation
        this.bikePaint.setFilterBitmap(true);
        this.bikePaint.setAntiAlias(true);
    }
    
    public void setEffectImages(Bitmap speedLinesImg, Bitmap dustImg, Bitmap stuntStarsImg) {
        this.speedLinesImg = speedLinesImg;
        this.dustImg = dustImg;
        this.stuntStarsImg = stuntStarsImg;
    }
    
    public void draw(Canvas canvas) {
        // Save the current canvas state
        canvas.save();
        
        // Draw particle effects behind the bike
        if (showSpeedLines && speedLinesImg != null) {
            canvas.drawBitmap(speedLinesImg, x - 80, y + 20, null);
        }
        
        if (showDust && dustImg != null) {
            // Draw dust with more intensity when landing
            float dustScale = isLanding ? 1.5f : 1.0f;
            
            // Create a matrix for dust transformation
            Matrix dustMatrix = new Matrix();
            dustMatrix.postScale(dustScale, dustScale, dustImg.getWidth()/2, 0);
            dustMatrix.postTranslate(x - 10, y + height - 20 + suspensionOffset);
            
            canvas.drawBitmap(dustImg, dustMatrix, bikePaint);
        }
        
        // Set up the transformation matrix for the bike
        transformMatrix.reset();
        
        // Calculate the center point of the bike for rotation
        float pivotX = width / 2.0f;
        float pivotY = height / 2.0f;
        
        // Apply rotation for leaning effect
        transformMatrix.postRotate(leanAngle, pivotX, pivotY);
        
        // Apply translation for position and suspension effect
        transformMatrix.postTranslate(x, y + suspensionOffset);
        
        // Draw the bike image with transformations
        canvas.drawBitmap(currentImage, transformMatrix, bikePaint);
        
        // Draw stunt stars above the bike if performing a stunt
        if (showStars && stuntStarsImg != null) {
            canvas.drawBitmap(stuntStarsImg, x - 25, y - 60 + suspensionOffset, null);
        }
        
        // Restore the canvas state
        canvas.restore();
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
        
        // Update bike lean angle - gradually move toward target angle
        if (Math.abs(leanAngle - targetLeanAngle) > 0.1f) {
            // Smoothly interpolate between current and target angle
            leanAngle += (targetLeanAngle - leanAngle) * leanSpeed * 0.1f;
        } else {
            leanAngle = targetLeanAngle; // Snap to target when very close
        }
        
        // Update suspension effect
        if (isLanding) {
            // Compress suspension when landing
            suspensionOffset = Math.min(suspensionOffset + suspensionSpeed, maxSuspensionCompress);
            
            // If fully compressed, start extending
            if (suspensionOffset >= maxSuspensionCompress) {
                isLanding = false;
            }
        } else {
            // Gradually return suspension to normal
            if (suspensionOffset > 0) {
                suspensionOffset = Math.max(0, suspensionOffset - suspensionSpeed);
            }
        }
        
        // Special handling for jump stunt
        if ("jump".equals(stuntType)) {
            // When jump is about to end, trigger landing effect
            if (stuntTimer <= 5) {
                isLanding = true;
            }
        }
        
        // Update collision rectangle - adjust for suspension
        collisionRect.set(x, (int)(y + suspensionOffset), x + width, (int)(y + height + suspensionOffset));
    }
    
    /**
     * Legacy method for backward compatibility
     * Handles both movement and stunts
     */
    public void move(String direction, String stunt) {
        // Handle movement
        moveWithDirection(direction);
        
        // Handle stunts
        performStunt(stunt);
    }
    
    /**
     * Handles directional movement based on string direction
     */
    public void moveWithDirection(String direction) {
        // Handle player movement
        if (direction != null) {
            if ("left".equals(direction)) {
                // Move left, but not beyond the left road boundary
                x = Math.max(roadLeftBoundary, x - speed);
                // Set target lean angle for left turn
                targetLeanAngle = -maxLeanAngle;
            } else if ("right".equals(direction)) {
                // Move right, but not beyond the right road boundary
                x = Math.min(roadRightBoundary - width, x + speed);
                // Set target lean angle for right turn
                targetLeanAngle = maxLeanAngle;
            } else if ("up".equals(direction)) {
                // Move up, but not beyond the top road boundary
                y = Math.max(roadTopBoundary, y - speed);
                // Reset lean angle when moving straight
                targetLeanAngle = 0;
            } else if ("down".equals(direction)) {
                // Move down, but not beyond the bottom road boundary
                y = Math.min(roadBottomBoundary - height, y + speed);
                // Reset lean angle when moving straight
                targetLeanAngle = 0;
            }
            
            // Ensure the bike stays within road boundaries (additional safety check)
            x = Math.max(roadLeftBoundary, Math.min(roadRightBoundary - width, x));
            y = Math.max(roadTopBoundary, Math.min(roadBottomBoundary - height, y));
        } else {
            // Gradually return to upright position when not turning
            targetLeanAngle = 0;
        }
        
        // Update collision rectangle - adjust for suspension
        updateCollisionRect();
    }
    
    /**
     * Handles stunt execution
     */
    public void performStunt(String stunt) {
        if (stunt != null) {
            if ("wheelie".equals(stunt)) {
                startStunt("wheelie");
            } else if ("jump".equals(stunt)) {
                startStunt("jump");
                // Trigger suspension effect for jump
                isLanding = false;
                suspensionOffset = -5; // Slight upward movement for jump start
            }
        }
        
        // Update collision rectangle - adjust for suspension
        updateCollisionRect();
    }
    
    /**
     * Helper method to update collision rectangle
     */
    private void updateCollisionRect() {
        collisionRect.set(x, (int)(y + suspensionOffset), x + width, (int)(y + height + suspensionOffset));
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
        // Apply smoothing to horizontal input for more natural turning
        horizontalInput = lastHorizontalInput + (horizontalInput - lastHorizontalInput) * (1 - horizontalInputSmoothing);
        lastHorizontalInput = horizontalInput;
        
        // Scale the input by speed
        int deltaX = (int)(horizontalInput * speed);
        int deltaY = (int)(verticalInput * speed);
        
        // Apply movement
        x += deltaX;
        y += deltaY;
        
        // Set lean angle based on horizontal input (turning)
        // Map the input range (-1 to 1) to the lean angle range (-maxLeanAngle to maxLeanAngle)
        targetLeanAngle = horizontalInput * maxLeanAngle;
        
        // Ensure the bike stays within road boundaries
        x = Math.max(roadLeftBoundary, Math.min(roadRightBoundary - width, x));
        y = Math.max(roadTopBoundary, Math.min(roadBottomBoundary - height, y));
        
        // Update collision rectangle
        updateCollisionRect();
    }
    
    // Method to reset position if bike gets stuck
    public void resetPosition() {
        // Reset to a safe position within the road boundaries
        this.x = roadLeftBoundary + 50; // 50 pixels from the left road boundary
        this.y = roadBottomBoundary - height - 50; // 50 pixels from the bottom road boundary
        
        // Reset physics and animation properties
        this.leanAngle = 0;
        this.targetLeanAngle = 0;
        this.suspensionOffset = 0;
        this.isLanding = false;
        this.lastHorizontalInput = 0;
        
        // Reset image to normal
        this.currentImage = normalImage;
        
        // Update collision rectangle
        collisionRect.set(x, y, x + width, y + height);
    }
}
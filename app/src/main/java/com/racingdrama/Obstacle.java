package com.racingdrama;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.Random;

public class Obstacle {
    // Image and dimensions
    private Bitmap image;
    private int width;
    private int height;
    
    // Position and movement
    private int x;
    private int y;
    private int speed;
    
    // Road boundaries
    private int roadLeftBoundary;
    private int roadRightBoundary;
    
    // Collision detection
    private Rect collisionRect;
    
    // Obstacle properties
    private String obstacleType;
    private boolean isHazard;
    
    // Random generator
    private Random random;
    
    public Obstacle(Bitmap image, int x, int y, int speed, String obstacleType, boolean isHazard, int roadLeftBoundary, int roadRightBoundary) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.obstacleType = obstacleType;
        this.isHazard = isHazard;
        this.roadLeftBoundary = roadLeftBoundary;
        this.roadRightBoundary = roadRightBoundary;
        
        // Initialize collision rectangle
        this.collisionRect = new Rect(x, y, x + width, y + height);
        
        // Initialize random generator
        this.random = new Random();
    }
    
    public void update(int screenHeight, int screenWidth) {
        // Move obstacle down
        y += speed;
        
        // If obstacle goes off screen, reset it
        if (y > screenHeight) {
            resetObstacle(screenWidth);
        }
        
        // Update collision rectangle
        collisionRect.set(x, y, x + width, y + height);
    }
    
    private void resetObstacle(int screenWidth) {
        // Reset position above the screen
        y = random.nextInt(200) - 250;
        
        // Ensure obstacles stay completely within road boundaries
        // Account for obstacle width to prevent it from extending beyond road edges
        int maxX = roadRightBoundary - width;
        int minX = roadLeftBoundary;
        
        // Generate random x position within safe road boundaries
        x = random.nextInt(maxX - minX) + minX;
    }
    
    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y, null);
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
    
    public Rect getCollisionRect() {
        return collisionRect;
    }
    
    public String getObstacleType() {
        return obstacleType;
    }
    
    public boolean isHazard() {
        return isHazard;
    }
    
    public void setImage(Bitmap image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }
    
    public void setObstacleType(String obstacleType) {
        this.obstacleType = obstacleType;
    }
    
    public void setHazard(boolean hazard) {
        isHazard = hazard;
    }
}
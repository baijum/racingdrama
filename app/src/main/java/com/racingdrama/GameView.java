package com.racingdrama;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    // Game thread
    private GameThread gameThread;
    
    // Screen dimensions
    private int screenWidth;
    private int screenHeight;
    
    // Game state
    private boolean isPlaying = false;
    private boolean gameOver = false;
    private boolean gameWon = false;
    
    // Game objects
    private Player player;
    private List<Obstacle> obstacles;
    private int score = 0;
    private int distance = 0;
    private int finishLineY = -5000; // Finish line position (negative means it's ahead)
    private int roadY = 0;
    private int roadSpeed = 5;
    
    // Effect timers
    private boolean showCrashEffect = false;
    private int crashEffectTimer = 0;
    private int crashEffectX = 0;
    private int crashEffectY = 0;
    private String stuntBonusText = null;
    private int stuntBonusTimer = 0;
    
    // Touch controls
    private VirtualJoystick joystick; // Virtual joystick for movement
    private TouchButton wheelieButton;
    private TouchButton jumpButton;
    private TouchButton restartButton;
    private TouchButton resetButton;
    
    // Touch input state
    private String touchDirection = null;
    private String touchStunt = null;
    
    // Game assets
    private Bitmap bikeNormalImg;
    private Bitmap bikeWheelieImg;
    private Bitmap bikeJumpImg;
    private Bitmap carImg;
    private Bitmap rockImg;
    private Bitmap oilImg;
    private Bitmap coneImg;
    private Bitmap backgroundImg;
    private Bitmap finishLineImg;
    private Bitmap speedLinesImg;
    private Bitmap dustImg;
    private Bitmap crashImg;
    private Bitmap stuntStarsImg;
    
    // Paint objects for drawing
    private Paint textPaint;
    private Paint scorePaint;
    private Paint gameOverPaint;
    
    // Random generator
    private Random random;
    
    private BikeStyleManager bikeStyleManager;
    
    public GameView(Context context, int screenWidth, int screenHeight) {
        super(context);
        
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        
        // Get the holder and add callback
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        
        // Create bike style manager
        bikeStyleManager = new BikeStyleManager(context);
        
        // Initialize random generator
        random = new Random();
        
        // Initialize paint objects
        initPaints();
        
        // Load game assets
        loadAssets();
        
        // Initialize game objects
        initGame();
        
        // Set focusable so we can handle events
        setFocusable(true);
    }
    
    private void initPaints() {
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(36);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setAntiAlias(true);
        
        scorePaint = new Paint();
        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextSize(48);
        scorePaint.setTypeface(Typeface.DEFAULT_BOLD);
        scorePaint.setAntiAlias(true);
        
        gameOverPaint = new Paint();
        gameOverPaint.setColor(Color.RED);
        gameOverPaint.setTextSize(72);
        gameOverPaint.setTypeface(Typeface.DEFAULT_BOLD);
        gameOverPaint.setAntiAlias(true);
    }
    
    private void loadAssets() {
        try {
            // Load bike images from the BikeStyleManager
            bikeNormalImg = bikeStyleManager.getBikeNormalImg();
            bikeWheelieImg = bikeStyleManager.getBikeWheelieImg();
            bikeJumpImg = bikeStyleManager.getBikeJumpImg();
            
            // Load all other game images with error handling
            loadDrawableWithFallback("car", Color.RED, new int[]{100, 60});
            loadDrawableWithFallback("rock", Color.GRAY, new int[]{50, 50});
            loadDrawableWithFallback("oil", Color.BLACK, new int[]{60, 30});
            loadDrawableWithFallback("cone", Color.YELLOW, new int[]{40, 60});
            loadDrawableWithFallback("background", Color.BLUE, new int[]{screenWidth, screenHeight});
            loadDrawableWithFallback("finish_line", Color.WHITE, new int[]{screenWidth, 50});
            loadDrawableWithFallback("speed_lines", Color.WHITE, new int[]{200, 120});
            loadDrawableWithFallback("dust", Color.LTGRAY, new int[]{150, 100});
            loadDrawableWithFallback("crash", Color.YELLOW, new int[]{200, 200});
            loadDrawableWithFallback("stunt_stars", Color.YELLOW, new int[]{200, 120});
            
            // Scale background to screen size if needed
            if (backgroundImg != null) {
                backgroundImg = Bitmap.createScaledBitmap(backgroundImg, screenWidth, screenHeight, true);
            }
        } catch (Exception e) {
            // Create fallback images if there's a catastrophic failure
            createFallbackImages();
        }
    }
    
    private void loadDrawableWithFallback(String resourceName, int fallbackColor, int[] dimensions) {
        try {
            int resourceId = getResources().getIdentifier(resourceName, "drawable", getContext().getPackageName());
            Bitmap bitmap = null;
            
            // Try to load as vector drawable first
            try {
                VectorDrawableCompat drawable = VectorDrawableCompat.create(getResources(), resourceId, null);
                if (drawable != null) {
                    bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    drawable.draw(canvas);
                }
            } catch (Exception e) {
                // If vector drawable fails, try BitmapFactory
                bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
            }
            
            // If both methods fail, create a fallback
            if (bitmap == null) {
                bitmap = createFallbackBitmap(dimensions[0], dimensions[1], fallbackColor);
            }
            
            // Assign the bitmap to the appropriate field
            switch (resourceName) {
                case "car":
                    carImg = bitmap;
                    break;
                case "rock":
                    rockImg = bitmap;
                    break;
                case "oil":
                    oilImg = bitmap;
                    break;
                case "cone":
                    coneImg = bitmap;
                    break;
                case "background":
                    backgroundImg = bitmap;
                    break;
                case "finish_line":
                    finishLineImg = bitmap;
                    break;
                case "speed_lines":
                    speedLinesImg = bitmap;
                    break;
                case "dust":
                    dustImg = bitmap;
                    break;
                case "crash":
                    crashImg = bitmap;
                    break;
                case "stunt_stars":
                    stuntStarsImg = bitmap;
                    break;
            }
        } catch (Exception e) {
            // Create a fallback bitmap if anything goes wrong
            Bitmap fallback = createFallbackBitmap(dimensions[0], dimensions[1], fallbackColor);
            
            // Assign the fallback bitmap to the appropriate field
            switch (resourceName) {
                case "car":
                    carImg = fallback;
                    break;
                case "rock":
                    rockImg = fallback;
                    break;
                case "oil":
                    oilImg = fallback;
                    break;
                case "cone":
                    coneImg = fallback;
                    break;
                case "background":
                    backgroundImg = fallback;
                    break;
                case "finish_line":
                    finishLineImg = fallback;
                    break;
                case "speed_lines":
                    speedLinesImg = fallback;
                    break;
                case "dust":
                    dustImg = fallback;
                    break;
                case "crash":
                    crashImg = fallback;
                    break;
                case "stunt_stars":
                    stuntStarsImg = fallback;
                    break;
            }
        }
    }
    
    private Bitmap createFallbackBitmap(int width, int height, int color) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(color);
        
        // Draw a simple shape based on the resource type
        canvas.drawRect(0, 0, width, height, paint);
        
        // Add some visual interest
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(2, 2, width-2, height-2, paint);
        
        return bitmap;
    }
    
    private void createFallbackImages() {
        bikeNormalImg = createFallbackBitmap(100, 60, Color.BLUE);
        bikeWheelieImg = createFallbackBitmap(100, 60, Color.GREEN);
        bikeJumpImg = createFallbackBitmap(100, 60, Color.RED);
        carImg = createFallbackBitmap(100, 60, Color.RED);
        rockImg = createFallbackBitmap(50, 50, Color.GRAY);
        oilImg = createFallbackBitmap(60, 30, Color.BLACK);
        coneImg = createFallbackBitmap(40, 60, Color.YELLOW);
        backgroundImg = createFallbackBitmap(screenWidth, screenHeight, Color.BLUE);
        finishLineImg = createFallbackBitmap(screenWidth, 50, Color.WHITE);
        speedLinesImg = createFallbackBitmap(200, 120, Color.WHITE);
        dustImg = createFallbackBitmap(150, 100, Color.LTGRAY);
        crashImg = createFallbackBitmap(200, 200, Color.YELLOW);
        stuntStarsImg = createFallbackBitmap(200, 120, Color.YELLOW);
    }
    
    private void initGame() {
        // Create player
        player = new Player(bikeNormalImg, bikeWheelieImg, bikeJumpImg, screenWidth, screenHeight);
        
        // Set effect images for player
        player.setEffectImages(speedLinesImg, dustImg, stuntStarsImg);
        
        // Create obstacles
        obstacles = new ArrayList<>();
        createObstacles();
        
        // Create touch controls
        createTouchControls();
    }
    
    private void createObstacles() {
        // Create initial obstacles
        for (int i = 0; i < 5; i++) {
            // Get exact road boundaries from Player class
            int roadLeftBoundary = player.getRoadLeftBoundary();
            int roadRightBoundary = player.getRoadRightBoundary();
            
            // Spread obstacles further apart vertically
            int y = random.nextInt(800) - 1000; // Start further above the screen and more spread out
            
            // Reduce speed range to make obstacles come more slowly
            int speed = random.nextInt(2) + 2;  // Speed between 2-3 (slower)
            
            String obstacleType = getRandomObstacleType();
            
            Bitmap obstacleImg;
            boolean isHazard = true;
            
            switch (obstacleType) {
                case "car":
                    obstacleImg = carImg;
                    break;
                case "rock":
                    obstacleImg = rockImg;
                    break;
                case "oil":
                    obstacleImg = oilImg;
                    isHazard = false;
                    break;
                case "cone":
                    obstacleImg = coneImg;
                    break;
                default:
                    obstacleImg = carImg;
                    break;
            }
            
            // Ensure obstacles stay completely within road boundaries
            // Account for obstacle width to prevent it from extending beyond road edges
            int obstacleWidth = obstacleImg.getWidth();
            int maxX = roadRightBoundary - obstacleWidth;
            int minX = roadLeftBoundary;
            
            // Generate random x position within safe road boundaries
            int x = random.nextInt(maxX - minX) + minX;
            
            obstacles.add(new Obstacle(obstacleImg, x, y, speed, obstacleType, isHazard, player.getRoadLeftBoundary(), player.getRoadRightBoundary()));
        }
    }
    
    private String getRandomObstacleType() {
        String[] types = {"car", "rock", "oil", "cone"};
        return types[random.nextInt(types.length)];
    }
    
    private void createTouchControls() {
        int buttonSize = 80;
        int buttonMargin = 20;
        int buttonAlpha = 150;
        
        // Create virtual joystick for movement
        int joystickRadius = 120; // Larger radius for better control
        int joystickX = joystickRadius + buttonMargin;
        int joystickY = screenHeight - joystickRadius - buttonMargin;
        joystick = new VirtualJoystick(joystickX, joystickY, joystickRadius);
        
        // Stunt buttons
        wheelieButton = new TouchButton(
                screenWidth - buttonSize - buttonMargin,
                screenHeight - buttonSize - buttonMargin,
                buttonSize,
                buttonSize,
                "W",
                Color.argb(buttonAlpha, 255, 200, 0),
                Color.BLACK
        );
        
        jumpButton = new TouchButton(
                screenWidth - buttonSize * 2 - buttonMargin * 2,
                screenHeight - buttonSize - buttonMargin,
                buttonSize,
                buttonSize,
                "J",
                Color.argb(buttonAlpha, 0, 200, 255),
                Color.BLACK
        );
        
        // Restart button (only shown when game is over or won)
        restartButton = new TouchButton(
                screenWidth / 2 - buttonSize,
                screenHeight / 2 + 100,
                buttonSize * 2,
                buttonSize,
                "Restart",
                Color.argb(buttonAlpha, 0, 255, 0),
                Color.BLACK
        );
        
        // Reset position button (always shown)
        resetButton = new TouchButton(
                screenWidth - buttonSize * 2 - buttonMargin,
                buttonMargin,
                buttonSize * 2,
                buttonSize,
                "Reset",
                Color.argb(buttonAlpha, 255, 0, 0),
                Color.WHITE
        );
    }
    
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Check if bike style has changed and reload if necessary
        bikeStyleManager.reloadIfStyleChanged();
        
        // Reload bike images if style changed
        bikeNormalImg = bikeStyleManager.getBikeNormalImg();
        bikeWheelieImg = bikeStyleManager.getBikeWheelieImg();
        bikeJumpImg = bikeStyleManager.getBikeJumpImg();
        
        // Update player bike images
        if (player != null) {
            player.updateBikeImages(bikeNormalImg, bikeWheelieImg, bikeJumpImg);
        }
        
        // Start the game thread when surface is created
        gameThread = new GameThread(holder);
        gameThread.setRunning(true);
        gameThread.start();
        isPlaying = true;
    }
    
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Handle surface changes if needed
    }
    
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Stop the game thread when surface is destroyed
        boolean retry = true;
        gameThread.setRunning(false);
        
        while (retry) {
            try {
                gameThread.join();
                retry = false;
            } catch (InterruptedException e) {
                // Retry
            }
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Get action with pointer index
        int actionMasked = event.getActionMasked();
        int actionIndex = event.getActionIndex();
        int pointerId = event.getPointerId(actionIndex);
        
        // Handle different touch actions
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                // New finger touched the screen
                float x = event.getX(actionIndex);
                float y = event.getY(actionIndex);
                
                // Determine if this touch is on the left or right side of the screen
                if (x < screenWidth / 2) {
                    // Left side - handle with joystick
                    joystick.onTouchEvent(x, y, actionMasked, pointerId);
                } else {
                    // Right side - check stunt buttons
                    if (wheelieButton.isPressed(x, y)) {
                        wheelieButton.setPressed(true);
                        touchStunt = "wheelie";
                    } else if (jumpButton.isPressed(x, y)) {
                        jumpButton.setPressed(true);
                        touchStunt = "jump";
                    } else if ((gameOver || gameWon) && restartButton.isPressed(x, y)) {
                        restartGame();
                    } else if (resetButton.isPressed(x, y)) {
                        player.resetPosition();
                    }
                }
                break;
                
            case MotionEvent.ACTION_MOVE:
                // Process all active pointers
                for (int i = 0; i < event.getPointerCount(); i++) {
                    int id = event.getPointerId(i);
                    float touchX = event.getX(i);
                    float touchY = event.getY(i);
                    
                    // Determine if this touch is on the left or right side
                    if (touchX < screenWidth / 2) {
                        // Left side - update joystick
                        joystick.onTouchEvent(touchX, touchY, MotionEvent.ACTION_MOVE, id);
                    } else {
                        // Right side - check stunt buttons
                        if (wheelieButton.isPressed(touchX, touchY)) {
                            wheelieButton.setPressed(true);
                            touchStunt = "wheelie";
                        } else if (jumpButton.isPressed(touchX, touchY)) {
                            jumpButton.setPressed(true);
                            touchStunt = "jump";
                        }
                    }
                }
                break;
                
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                // A finger was lifted
                x = event.getX(actionIndex);
                
                if (x < screenWidth / 2) {
                    // Left side - handle with joystick
                    joystick.onTouchEvent(x, event.getY(actionIndex),
                                         actionMasked, pointerId);
                } else {
                    // Only reset stunt buttons if no other fingers are pressing them
                    boolean otherFingerOnWheelieButton = false;
                    boolean otherFingerOnJumpButton = false;
                    
                    for (int i = 0; i < event.getPointerCount(); i++) {
                        if (i != actionIndex) { // Skip the finger that was lifted
                            float otherX = event.getX(i);
                            float otherY = event.getY(i);
                            
                            if (otherX >= screenWidth / 2) { // Right side only
                                if (wheelieButton.isPressed(otherX, otherY)) {
                                    otherFingerOnWheelieButton = true;
                                }
                                if (jumpButton.isPressed(otherX, otherY)) {
                                    otherFingerOnJumpButton = true;
                                }
                            }
                        }
                    }
                    
                    // Only reset buttons if no other fingers are pressing them
                    if (!otherFingerOnWheelieButton) {
                        wheelieButton.setPressed(false);
                        if (touchStunt == "wheelie") touchStunt = null;
                    }
                    
                    if (!otherFingerOnJumpButton) {
                        jumpButton.setPressed(false);
                        if (touchStunt == "jump") touchStunt = null;
                    }
                }
                break;
                
            case MotionEvent.ACTION_CANCEL:
                // Reset all touch input
                joystick.onTouchEvent(0, 0, MotionEvent.ACTION_CANCEL, 0);
                wheelieButton.setPressed(false);
                jumpButton.setPressed(false);
                touchStunt = null;
                break;
        }
        
        // Update direction from joystick
        if (joystick.isActive()) {
            touchDirection = joystick.getDirection();
        } else {
            touchDirection = null;
        }
        
        return true;
    }
    
    private void restartGame() {
        // Initialize game state
        score = 0;
        distance = 0;
        obstacles.clear();
        gameOver = false;
        gameWon = false;
        showCrashEffect = false;
        
        // Create new player with current bike style
        player = new Player(bikeStyleManager.getBikeNormalImg(), 
                           bikeStyleManager.getBikeWheelieImg(), 
                           bikeStyleManager.getBikeJumpImg(), 
                           screenWidth, screenHeight);
        
        // Set effect images for player
        player.setEffectImages(speedLinesImg, dustImg, stuntStarsImg);
        
        // Create new obstacles
        createObstacles();
    }
    
    private void update() {
        if (!gameOver && !gameWon) {
            // Update road position (for scrolling effect)
            roadY = (roadY + roadSpeed) % screenHeight;
            
            // Update player
            player.update();
            
            // Handle movement (left hand)
            if (joystick.isActive() && joystick.isMoving()) {
                // Use continuous joystick movement
                player.moveWithJoystick(joystick.getHorizontalMovement(), joystick.getVerticalMovement());
            } else if (touchDirection != null) {
                // Fallback to discrete direction movement
                player.moveWithDirection(touchDirection);
            }
            
            // Handle stunts separately (right hand)
            if (touchStunt != null) {
                player.performStunt(touchStunt);
            }
            
            // Update obstacles
            for (Obstacle obstacle : obstacles) {
                obstacle.update(screenHeight, screenWidth);
            }
            
            // Check for collisions
            checkCollision();
            
            // Check for completed stunts
            if (!player.isPerformingStunt() && player.getStuntCooldown() == player.getStuntCooldownDuration() - 1) {
                // This means a stunt just ended
                addStuntBonus();
            }
            
            // Increase score and distance
            score++;
            distance += roadSpeed;
            
            // Check if player reached finish line
            if (distance >= Math.abs(finishLineY)) {
                gameWon = true;
            }
        }
        
        // Update effect timers
        if (crashEffectTimer > 0) {
            crashEffectTimer--;
            if (crashEffectTimer <= 0) {
                showCrashEffect = false;
            }
        }
        
        if (stuntBonusTimer > 0) {
            stuntBonusTimer--;
        }
    }
    
    private void checkCollision() {
        for (Obstacle obstacle : obstacles) {
            if (player.getCollisionRect().intersect(obstacle.getCollisionRect())) {
                if (obstacle.isHazard()) {  // Only crash on hazardous obstacles
                    gameOver = true;
                    // Show crash effect
                    showCrashEffect = true;
                    crashEffectTimer = 60;  // Show for 1 second
                    crashEffectX = player.getX();
                    crashEffectY = player.getY();
                } else {  // Oil slick - slow down the player
                    player.setSpeed(Math.max(2, player.getSpeed() - 1));  // Slow down but not below 2
                }
            }
        }
    }
    
    private void addStuntBonus() {
        if (player.getLastStuntType() != null) {
            int bonus = player.getStuntPoints(player.getLastStuntType());
            score += bonus;
            
            // Show bonus text
            stuntBonusText = "+" + bonus + " STUNT!";
            stuntBonusTimer = 60;  // Show for 1 second
        }
    }
    
    private void drawGame(Canvas canvas) {
        if (canvas != null) {
            // Clear the canvas
            canvas.drawColor(Color.BLACK);
            
            // Draw background
            canvas.drawBitmap(backgroundImg, 0, 0, null);
            
            // Draw finish line if it's visible on screen
            int finishLineScreenY = finishLineY + distance;
            if (-50 <= finishLineScreenY && finishLineScreenY <= screenHeight) {
                canvas.drawBitmap(finishLineImg, player.getRoadLeftBoundary(), finishLineScreenY, null);
            }
            
            // Draw obstacles
            for (Obstacle obstacle : obstacles) {
                obstacle.draw(canvas);
            }
            
            // Draw the player
            player.draw(canvas);
            
            // Draw crash effect if active
            if (showCrashEffect) {
                canvas.drawBitmap(crashImg, crashEffectX - 30, crashEffectY - 30, null);
            }
            
            // Draw score and distance
            canvas.drawText("Score: " + score, 10, 50, textPaint);
            canvas.drawText("Distance: " + distance + "m", 10, 100, textPaint);
            
            // Debug information
            canvas.drawText("Bike X: " + player.getX() + ", Width: " + player.getWidth() + ", Right: " + (player.getX() + player.getWidth()), 10, 150, textPaint);
            canvas.drawText("Road: " + player.getRoadLeftBoundary() + "-" + player.getRoadRightBoundary(), 10, 200, textPaint);
            canvas.drawText("Direction: " + (touchDirection != null ? touchDirection : "none"), 10, 250, textPaint);
            
            // Draw stunt info
            if (player.isPerformingStunt()) {
                String stuntName = player.getStuntType().toUpperCase();
                Paint stuntPaint = new Paint(textPaint);
                stuntPaint.setColor(Color.YELLOW);
                canvas.drawText("PERFORMING: " + stuntName, screenWidth - 300, 50, stuntPaint);
            }
            
            // Draw stunt bonus text if active
            if (stuntBonusTimer > 0) {
                Paint bonusPaint = new Paint(scorePaint);
                bonusPaint.setColor(Color.YELLOW);
                // Make it float up and fade out
                int yOffset = (int)(20 * (1 - stuntBonusTimer / 60.0f));
                int alpha = (int)(255 * (stuntBonusTimer / 60.0f));
                bonusPaint.setAlpha(alpha);
                canvas.drawText(stuntBonusText, player.getX(), player.getY() - 50 - yOffset, bonusPaint);
            }
            
            // Draw touch controls
            if (!gameOver && !gameWon) {
                // Draw virtual joystick
                joystick.draw(canvas);
                
                // Draw stunt buttons
                wheelieButton.draw(canvas);
                jumpButton.draw(canvas);
                
                // Draw reset button
                resetButton.draw(canvas);
                
                // Draw small control hints
                Paint hintPaint = new Paint(textPaint);
                hintPaint.setTextSize(18);
                canvas.drawText("Wheelie", wheelieButton.getX() + wheelieButton.getWidth() / 2 - 30,
                        wheelieButton.getY() - 10, hintPaint);
                canvas.drawText("Jump", jumpButton.getX() + jumpButton.getWidth() / 2 - 20,
                        jumpButton.getY() - 10, hintPaint);
                canvas.drawText("Reset Position", resetButton.getX() + resetButton.getWidth() / 2 - 50,
                        resetButton.getY() - 10, hintPaint);
                
                // Draw joystick hint
                canvas.drawText("Move", joystick.getBaseX(), joystick.getBaseY() - joystick.getBaseRadius() - 10, hintPaint);
            }
            
            // Draw game over message if game is over
            if (gameOver) {
                String gameOverText = "GAME OVER";
                float textWidth = gameOverPaint.measureText(gameOverText);
                canvas.drawText(gameOverText, screenWidth / 2 - textWidth / 2, screenHeight / 2, gameOverPaint);
                
                // Show final score
                String finalScoreText = "Final Score: " + score;
                float scoreWidth = scorePaint.measureText(finalScoreText);
                canvas.drawText(finalScoreText, screenWidth / 2 - scoreWidth / 2, screenHeight / 2 + 50, scorePaint);
                
                // Draw restart button
                restartButton.draw(canvas);
            }
            
            // Draw win message if player won
            else if (gameWon) {
                gameOverPaint.setColor(Color.GREEN);
                String winText = "YOU WIN!";
                float textWidth = gameOverPaint.measureText(winText);
                canvas.drawText(winText, screenWidth / 2 - textWidth / 2, screenHeight / 2, gameOverPaint);
                
                // Show final score
                String finalScoreText = "Final Score: " + score;
                float scoreWidth = scorePaint.measureText(finalScoreText);
                canvas.drawText(finalScoreText, screenWidth / 2 - scoreWidth / 2, screenHeight / 2 + 50, scorePaint);
                
                // Draw restart button
                restartButton.draw(canvas);
                
                // Reset paint color
                gameOverPaint.setColor(Color.RED);
            }
        }
    }
    
    // Game thread class
    private class GameThread extends Thread {
        private SurfaceHolder surfaceHolder;
        private boolean running;
        private static final int TARGET_FPS = 60;
        private static final long TARGET_TIME = 1000 / TARGET_FPS;
        
        public GameThread(SurfaceHolder holder) {
            this.surfaceHolder = holder;
        }
        
        public void setRunning(boolean running) {
            this.running = running;
        }
        
        @Override
        public void run() {
            long startTime;
            long timeMillis;
            long waitTime;
            
            while (running) {
                startTime = System.nanoTime();
                Canvas canvas = null;
                
                try {
                    canvas = surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        update();
                        drawGame(canvas);
                    }
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
                
                timeMillis = (System.nanoTime() - startTime) / 1000000;
                waitTime = TARGET_TIME - timeMillis;
                
                if (waitTime > 0) {
                    try {
                        sleep(waitTime);
                    } catch (InterruptedException e) {
                        // Ignore
                    }
                }
            }
        }
    }
}
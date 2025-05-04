# Racing Drama - Android Version

This is the pure Android SDK implementation of the Racing Drama motorbike game, ported from the original Pygame version.

## Overview

The game has been completely rewritten using native Android APIs:
- Uses Android's SurfaceView for rendering
- Implements a game loop with proper timing
- Handles touch controls for mobile gameplay
- Reuses the original game assets

## Game Features

- Motorbike racing game with obstacles to avoid
- Perform stunts (wheelie, jump) to earn bonus points
- Touch controls for movement and stunts
- Score tracking and finish line goal

## Project Structure

- `MainActivity.java`: Main activity that initializes the game
- `GameView.java`: SurfaceView implementation that handles rendering and game loop
- `Player.java`: Player class that handles the bike and stunts
- `Obstacle.java`: Obstacle class for various obstacles (cars, rocks, oil slicks, cones)
- `TouchButton.java`: Touch control button implementation

## Building and Running

1. Open the project in Android Studio
2. Make sure all assets are in the drawable folder
3. Build and run on an Android device or emulator

## Controls

- Left/Right buttons: Move the bike horizontally
- Up/Down buttons: Move the bike vertically
- "W" button: Perform a wheelie stunt
- "J" button: Perform a jump stunt

## Differences from Pygame Version

This version:
- Uses native Android rendering instead of Pygame/SDL2
- Implements touch controls directly with Android's MotionEvent system
- Handles game state and collision detection in Java
- Maintains the same gameplay mechanics and visual style

## Assets

All game assets are reused from the original Pygame version and are stored in the `res/drawable` directory.
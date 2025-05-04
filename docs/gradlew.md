# Gradle Wrapper (`gradlew`) Usage Guide

The Gradle Wrapper (`gradlew` for Unix/macOS or `gradlew.bat` for Windows) is a script that allows you to run Gradle builds without having Gradle installed on your system. Here's how to use it:

## Basic Commands

1. **Build your project**:
   ```
   ./gradlew build
   ```
   Compiles, tests, and assembles the project.

2. **Clean build**:
   ```
   ./gradlew clean build
   ```
   Removes all build artifacts and then builds the project from scratch.

3. **Skip specific tasks**:
   ```
   ./gradlew build -x lint
   ```
   Builds the project but skips the lint check task (useful when fixing other issues).

## Common Tasks

- **Run specific task**:
  ```
  ./gradlew taskName
  ```
  Example: `./gradlew assembleDebug`

- **List all tasks**:
  ```
  ./gradlew tasks
  ```

- **List task dependencies**:
  ```
  ./gradlew taskName --dry-run
  ```

## Android-Specific Tasks

- **Build debug APK**:
  ```
  ./gradlew assembleDebug
  ```

- **Build release APK**:
  ```
  ./gradlew assembleRelease
  ```

- **Install debug version**:
  ```
  ./gradlew installDebug
  ```

- **Run tests**:
  ```
  ./gradlew test
  ```

## Options and Flags

- **Show stacktrace for errors**:
  ```
  ./gradlew build --stacktrace
  ```

- **Debug build issues**:
  ```
  ./gradlew build --info
  ```
  or for more details:
  ```
  ./gradlew build --debug
  ```

- **Build scan for detailed analysis**:
  ```
  ./gradlew build --scan
  ```

## Dealing with Lint Issues

- **Run lint only**:
  ```
  ./gradlew lint
  ```

- **Create lint baseline** (for ignoring existing issues):
  ```
  ./gradlew updateLintBaseline
  ```
  (requires configuration in build.gradle)

- **Disable lint for a build**:
  ```
  ./gradlew build -x lint
  ```

## Permissions

If you get "Permission denied" error:
```
chmod +x gradlew
```

## Examples from Our Project

- Fixed invalid resource directory names:
  ```
  ./gradlew clean build
  ```

- After fixing the manifest and Player.java:
  ```
  ./gradlew build -x lint
  ```
  (to verify fixes without lint checks)

- Final validation:
  ```
  ./gradlew clean build
  ```
  (to ensure all issues are fixed)

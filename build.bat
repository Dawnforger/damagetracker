@echo off
REM Damage Tracker Build Script for Windows
REM This script builds the Damage Tracker mod for Minecraft 1.20.1

echo ======================================
echo Damage Tracker Mod - Build Script
echo ======================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo X Java is not installed or not in PATH
    echo Please install JDK 17 or later
    pause
    exit /b 1
)

REM Display Java version
echo [OK] Java version:
java -version 2>&1 | findstr /R "version"
echo.

REM Clean previous builds
echo [*] Cleaning previous builds...
call gradlew.bat clean
echo.

REM Build the mod
echo [*] Building Damage Tracker mod...
call gradlew.bat build

REM Check if build was successful
if errorlevel 1 (
    echo.
    echo ======================================
    echo X Build failed!
    echo ======================================
    echo.
    echo Please check the error messages above
    echo.
    echo Common issues:
    echo   - Wrong Java version (need JDK 17+)
    echo   - No internet connection (Gradle needs to download dependencies)
    echo   - Corrupted Gradle cache (try: gradlew.bat clean --refresh-dependencies)
    echo.
    pause
    exit /b 1
)

echo.
echo ======================================
echo [OK] Build successful!
echo ======================================
echo.
echo [*] Your mod JAR is located at:
echo     build\libs\damagetracker-1.0.0.jar
echo.
echo [*] To install:
echo     1. Copy the JAR to .minecraft\mods\
echo     2. Launch Minecraft with Forge 1.20.1
echo.
echo [*] For more information, see:
echo     - QUICKSTART.md for quick setup
echo     - INSTALLATION.md for detailed guide
echo     - README.md for features and usage
echo.
pause

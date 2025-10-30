#!/bin/bash

# Damage Tracker Build Script
# This script builds the Damage Tracker mod for Minecraft 1.20.1

echo "======================================"
echo "Damage Tracker Mod - Build Script"
echo "======================================"
echo ""

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed or not in PATH"
    echo "Please install JDK 17 or later"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java version is too old: $JAVA_VERSION"
    echo "Please install JDK 17 or later"
    exit 1
fi

echo "✅ Java version: $(java -version 2>&1 | head -n 1)"
echo ""

# Clean previous builds
echo "🧹 Cleaning previous builds..."
./gradlew clean
echo ""

# Build the mod
echo "🔨 Building Damage Tracker mod..."
./gradlew build

# Check if build was successful
if [ $? -eq 0 ]; then
    echo ""
    echo "======================================"
    echo "✅ Build successful!"
    echo "======================================"
    echo ""
    echo "📦 Your mod JAR is located at:"
    echo "   build/libs/damagetracker-1.0.0.jar"
    echo ""
    echo "📝 To install:"
    echo "   1. Copy the JAR to .minecraft/mods/"
    echo "   2. Launch Minecraft with Forge 1.20.1"
    echo ""
    echo "📖 For more information, see:"
    echo "   - QUICKSTART.md for quick setup"
    echo "   - INSTALLATION.md for detailed guide"
    echo "   - README.md for features and usage"
    echo ""
else
    echo ""
    echo "======================================"
    echo "❌ Build failed!"
    echo "======================================"
    echo ""
    echo "Please check the error messages above"
    echo ""
    echo "Common issues:"
    echo "  - Wrong Java version (need JDK 17+)"
    echo "  - No internet connection (Gradle needs to download dependencies)"
    echo "  - Corrupted Gradle cache (try: ./gradlew clean --refresh-dependencies)"
    echo ""
    exit 1
fi

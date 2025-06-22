#!/bin/bash

echo "Building FinShare Android APK..."

# Check if Android SDK is available
if [ -z "$ANDROID_HOME" ]; then
    echo "Setting up Android SDK..."
    export ANDROID_HOME=/opt/android-sdk
    export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
fi

# Clean previous builds
echo "Cleaning previous builds..."
./gradlew clean

# Build debug APK
echo "Building debug APK..."
./gradlew assembleDebug

# Check if build succeeded
if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
    echo "✅ APK built successfully!"
    echo "📍 Location: app/build/outputs/apk/debug/app-debug.apk"
    echo "📱 Install with: adb install app/build/outputs/apk/debug/app-debug.apk"
else
    echo "❌ Build failed. Check logs above."
    exit 1
fi
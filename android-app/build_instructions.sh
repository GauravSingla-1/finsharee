#!/bin/bash

echo "🚀 FinShare Android App - Build Without Android Studio"
echo "======================================================"

# Check if we're in the right directory
if [ ! -f "app/build.gradle.kts" ]; then
    echo "❌ Error: Please run this script from the android-app directory"
    exit 1
fi

echo "📋 Checking prerequisites..."

# Check for Java
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
    echo "✅ Java found: $JAVA_VERSION"
else
    echo "❌ Java not found. Please install JDK 8 or higher"
    echo "   Install with: sudo apt install openjdk-11-jdk"
    exit 1
fi

# Create gradle wrapper if missing
if [ ! -f "gradlew" ]; then
    echo "📦 Creating Gradle wrapper..."
    gradle wrapper
fi

# Make gradlew executable
chmod +x gradlew

echo "🧹 Cleaning previous builds..."
./gradlew clean

echo "🔨 Building debug APK..."
./gradlew assembleDebug

# Check if build succeeded
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
if [ -f "$APK_PATH" ]; then
    echo "🎉 Build successful!"
    echo "📱 APK location: $APK_PATH"
    echo "📊 APK size: $(du -h "$APK_PATH" | cut -f1)"
    echo ""
    echo "📋 Next steps:"
    echo "   1. Transfer APK to your Android device"
    echo "   2. Enable 'Unknown Sources' in device settings"
    echo "   3. Install the APK"
    echo "   4. Ensure backend services are running (localhost:5000-8006)"
    echo ""
    echo "🔗 Or share via:"
    echo "   - Email attachment"
    echo "   - Google Drive/Dropbox"
    echo "   - USB transfer"
    echo "   - QR code generator"
else
    echo "❌ Build failed. Check the output above for errors."
    echo "💡 Common fixes:"
    echo "   - Check internet connection for dependency downloads"
    echo "   - Ensure all source files are present"
    echo "   - Verify gradle configuration"
    exit 1
fi
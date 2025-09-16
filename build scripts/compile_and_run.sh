#!/bin/bash

# Image to Text Extractor - Compilation and Run Script
# This script compiles the Java application with all necessary dependencies

echo "=== Image to Text Extractor - Build Script ==="
echo ""

# Set the base directory
BASE_DIR="/Users/jasonhe/Desktop/future Github Projects"
TESS4J_DIR="$BASE_DIR/GitHub/ImageToText"
CURRENT_DIR="$BASE_DIR/ImageToText"

# Check if Tess4J directory exists
if [ ! -d "$TESS4J_DIR" ]; then
    echo "Error: Tess4J directory not found at $TESS4J_DIR"
    echo "Please ensure the Tess4J libraries are available."
    exit 1
fi

# Create lib directory in current project if it doesn't exist
mkdir -p "$CURRENT_DIR/lib"

# Copy necessary JAR files to local lib directory
echo "Copying required JAR files..."
cp "$TESS4J_DIR/lib"/*.jar "$CURRENT_DIR/lib/" 2>/dev/null
cp "$TESS4J_DIR/Tess4J/dist"/*.jar "$CURRENT_DIR/lib/" 2>/dev/null

# Copy tessdata directory
echo "Copying tessdata directory..."
if [ -d "$TESS4J_DIR/Tess4J/tessdata" ]; then
    cp -r "$TESS4J_DIR/Tess4J/tessdata" "$CURRENT_DIR/"
    echo "✓ Tessdata copied successfully"
else
    echo "⚠ Warning: Tessdata directory not found at $TESS4J_DIR/Tess4J/tessdata"
fi

# Check for native libraries
echo "Checking for native libraries..."
if [ -d "$TESS4J_DIR/lib/win32-x86" ] || [ -d "$TESS4J_DIR/lib/win32-x86-64" ]; then
    echo "⚠ Warning: Windows native libraries found, but you're on macOS"
    echo "  You may need to install Tesseract using Homebrew:"
    echo "  brew install tesseract"
fi

# Build classpath
CLASSPATH="$CURRENT_DIR/lib/*:$CURRENT_DIR"

echo "Classpath: $CLASSPATH"
echo ""

# Compile the Java file
echo "Compiling ImageToText.java..."
javac -cp "$CLASSPATH" "$CURRENT_DIR/ImageToText.java"

if [ $? -eq 0 ]; then
    echo "✓ Compilation successful!"
    echo ""
    echo "Running the application..."
    echo "=========================================="
    
    # Run the application
    java -cp "$CLASSPATH" ImageToText
else
    echo "✗ Compilation failed!"
    exit 1
fi

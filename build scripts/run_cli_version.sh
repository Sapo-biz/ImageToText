#!/bin/bash

# Image to Text Extractor - CLI Version Build Script
# This version uses the system Tesseract command line tool instead of Java bindings

echo "=== Image to Text Extractor - CLI Version ==="
echo ""

# Set the current directory
CURRENT_DIR="/Users/jasonhe/Desktop/future Github Projects/ImageToText"

# Check if tesseract is installed
echo "Checking for Tesseract installation..."
if command -v tesseract &> /dev/null; then
    echo "✓ Tesseract found: $(which tesseract)"
    tesseract --version | head -1
else
    echo "✗ Tesseract not found!"
    echo "Please install it using: brew install tesseract"
    exit 1
fi

echo ""

# Compile the CLI version
echo "Compiling ImageToTextCLI.java..."
javac "$CURRENT_DIR/ImageToTextCLI.java"

if [ $? -eq 0 ]; then
    echo "✓ Compilation successful!"
    echo ""
    echo "Running the CLI version application..."
    echo "=========================================="
    
    # Run the application
    java -cp "$CURRENT_DIR" ImageToTextCLI
else
    echo "✗ Compilation failed!"
    exit 1
fi

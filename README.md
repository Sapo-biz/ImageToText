# Image to Text Extractor

It's been a while, two months in the making! A professional Java GUI application that extracts text from images using OCR (Optical Character Recognition) technology. The application features a modern interface with drag-and-drop functionality, file upload, and clipboard integration.

## Features

- **Drag & Drop Interface**: Simply drag image files onto the application window
- **File Upload**: Click the "Upload Image" button to select images from your computer
- **OCR Text Extraction**: Uses Tesseract OCR engine for accurate text recognition
- **Scrollable Text Display**: View extracted text in a scrollable text area
- **Copy to Clipboard**: One-click copying of extracted text
- **Progress Indicators**: Visual feedback during image processing
- **Error Handling**: Comprehensive error handling with user-friendly messages and copy functionality
- **Modern GUI**: Professional-looking interface with system look and feel

## Supported Image Formats

- PNG
- JPG/JPEG
- GIF
- BMP
- TIFF/TIF

## Requirements

- Java 8 or higher
- Tesseract OCR engine installed on your system

## Installation

### Install Tesseract

**On macOS:**
```bash
brew install tesseract
```

**On Ubuntu/Debian:**
```bash
sudo apt-get install tesseract-ocr
```

**On Windows:**
Download and install from: https://github.com/UB-Mannheim/tesseract/wiki

## How to Run

### Option 1: CLI Version (Recommended - Works on Apple Silicon)

The CLI version uses the system-installed Tesseract command line tool, which is more reliable and works on all architectures including Apple Silicon Macs.

1. Open Terminal/Command Prompt
2. Navigate to the ImageToText directory
3. Run the CLI version:
   ```bash
   ./run_cli_version.sh
   ```

### Option 2: Original Version (Java Bindings)

**Note**: This version may have compatibility issues on Apple Silicon Macs due to architecture differences.

1. Open Terminal/Command Prompt
2. Navigate to the ImageToText directory
3. Run the build script:
   ```bash
   ./compile_and_run.sh
   ```

### Option 3: Manual Compilation (CLI Version)

1. Compile the CLI application:
   ```bash
   javac ImageToTextCLI.java
   ```
2. Run the application:
   ```bash
   java ImageToTextCLI
   ```

## How to Use

1. **Launch the Application**: Run the application using one of the methods above
2. **Add an Image**: 
   - Drag and drop an image file onto the drop zone, OR
   - Click "Upload Image" and select a file from your computer
3. **Wait for Processing**: The application will process the image and extract text
4. **View Results**: Extracted text will appear in the text area below
5. **Copy Text**: Click "Copy Text" to copy the extracted text to your clipboard
6. **Clear**: Click "Clear" to remove the current text and start over

## Interface Components

- **Drop Zone**: The blue area at the top where you can drag and drop images
- **Upload Button**: Click to open a file chooser dialog
- **Copy Button**: Copies the extracted text to clipboard (enabled when text is available)
- **Clear Button**: Clears the current text (enabled when text is available)
- **Text Area**: Scrollable area displaying the extracted text
- **Status Bar**: Shows current operation status and progress

## Technical Details

- **OCR Engine**: Tesseract 4.x via Tess4J Java wrapper
- **GUI Framework**: Java Swing with modern styling
- **Drag & Drop**: Native Java DnD API
- **Threading**: SwingWorker for non-blocking OCR processing
- **Error Handling**: Comprehensive exception handling with user feedback

## Troubleshooting

### Common Issues

1. **"Tesseract not found!"**
   - Install Tesseract using: `brew install tesseract` (macOS)
   - Ensure Tesseract is in your system PATH
   - Verify installation with: `tesseract --version`

2. **"UnsatisfiedLinkError" (Original Version)**
   - This is an architecture compatibility issue on Apple Silicon Macs
   - **Solution**: Use the CLI version instead: `./run_cli_version.sh`
   - The CLI version works on all architectures

3. **"Could not read image file"**
   - Verify the image file is not corrupted
   - Ensure the file format is supported

4. **Poor OCR Results**
   - Use high-quality images with clear, readable text
   - Ensure good contrast between text and background
   - Try different image formats if results are poor

### Performance Tips

- For best results, use images with:
  - High resolution (300+ DPI)
  - Good contrast
  - Clear, readable fonts
  - Minimal background noise

### Architecture Compatibility

- **Apple Silicon Macs (M1/M2/M3)**: Use the CLI version (`./run_cli_version.sh`)
- **Intel Macs**: Either version should work
- **Windows/Linux**: Use the CLI version for best compatibility

## File Structure

```
ImageToText/
├── ImageToText.java          # Original application (Java bindings)
├── ImageToTextCLI.java       # CLI version (recommended)
├── compile_and_run.sh        # Build script for original version
├── run_cli_version.sh        # Build script for CLI version
├── README.md                 # This file
├── lib/                      # JAR dependencies (created by build script)
└── tessdata/                 # Tesseract language data (created by build script)
```

## License

This application uses the Tess4J library, which is licensed under the Apache License 2.0.

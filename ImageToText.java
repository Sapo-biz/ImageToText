import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class ImageToText extends JFrame implements DropTargetListener {
    
    private JPanel mainPanel;
    private JLabel dropZoneLabel;
    private JTextArea textArea;
    private JButton uploadButton;
    private JButton copyButton;
    private JButton clearButton;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private Tesseract tesseract;
    private DropTarget dropTarget;
    
    public ImageToText() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupDragAndDrop();
        initializeTesseract();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Image to Text Extractor");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(true);
    }
    
    private void initializeComponents() {
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Drop zone panel
        JPanel dropZonePanel = createDropZonePanel();
        
        // Control panel
        JPanel controlPanel = createControlPanel();
        
        // Text display panel
        JPanel textPanel = createTextPanel();
        
        // Status panel
        JPanel statusPanel = createStatusPanel();
        
        mainPanel.add(dropZonePanel, BorderLayout.NORTH);
        mainPanel.add(controlPanel, BorderLayout.CENTER);
        mainPanel.add(textPanel, BorderLayout.SOUTH);
        mainPanel.add(statusPanel, BorderLayout.PAGE_END);
    }
    
    private JPanel createDropZonePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Drop Zone"));
        panel.setPreferredSize(new Dimension(0, 120));
        panel.setBackground(new Color(240, 248, 255));
        
        dropZoneLabel = new JLabel("<html><center><b>Drag & Drop Image Here</b><br>" +
                                  "or click Upload to select file<br>" +
                                  "<small>Supported formats: PNG, JPG, JPEG, GIF, BMP, TIFF</small></center></html>");
        dropZoneLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dropZoneLabel.setVerticalAlignment(SwingConstants.CENTER);
        dropZoneLabel.setForeground(new Color(70, 130, 180));
        dropZoneLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        panel.add(dropZoneLabel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBorder(new TitledBorder("Controls"));
        
        uploadButton = new JButton("Upload Image");
        uploadButton.setPreferredSize(new Dimension(120, 35));
        uploadButton.setFont(new Font("Arial", Font.BOLD, 12));
        uploadButton.setBackground(new Color(70, 130, 180));
        uploadButton.setForeground(Color.WHITE);
        uploadButton.setFocusPainted(false);
        
        copyButton = new JButton("Copy Text");
        copyButton.setPreferredSize(new Dimension(120, 35));
        copyButton.setFont(new Font("Arial", Font.BOLD, 12));
        copyButton.setBackground(new Color(34, 139, 34));
        copyButton.setForeground(Color.WHITE);
        copyButton.setFocusPainted(false);
        copyButton.setEnabled(false);
        
        clearButton = new JButton("Clear");
        clearButton.setPreferredSize(new Dimension(120, 35));
        clearButton.setFont(new Font("Arial", Font.BOLD, 12));
        clearButton.setBackground(new Color(220, 20, 60));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.setEnabled(false);
        
        panel.add(uploadButton);
        panel.add(copyButton);
        panel.add(clearButton);
        
        return panel;
    }
    
    private JPanel createTextPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Extracted Text"));
        panel.setPreferredSize(new Dimension(0, 250));
        
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setBackground(Color.WHITE);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 0, 0, 0));
        
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("Ready");
        progressBar.setVisible(false);
        
        statusLabel = new JLabel("Ready to extract text from images");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(100, 100, 100));
        
        panel.add(statusLabel, BorderLayout.WEST);
        panel.add(progressBar, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void setupLayout() {
        setContentPane(mainPanel);
    }
    
    private void setupEventHandlers() {
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFileChooser();
            }
        });
        
        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyToClipboard();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearText();
            }
        });
    }
    
    private void setupDragAndDrop() {
        dropTarget = new DropTarget(dropZoneLabel, this);
    }
    
    private void initializeTesseract() {
        try {
            tesseract = new Tesseract();
            
            // Try different possible tessdata paths (system installation first)
            String[] possiblePaths = {
                "/opt/homebrew/share/tessdata",  // Apple Silicon Homebrew
                "/usr/local/share/tessdata",     // Intel Homebrew
                "/usr/share/tessdata",           // System installation
                "tessdata",                      // Local copy
                "lib/Tess4J/tessdata",          // Bundled copy
                "../GitHub/ImageToText/Tess4J/tessdata",
                "/Users/jasonhe/Desktop/future Github Projects/GitHub/ImageToText/Tess4J/tessdata"
            };
            
            boolean tessdataFound = false;
            for (String path : possiblePaths) {
                File tessdataDir = new File(path);
                if (tessdataDir.exists() && tessdataDir.isDirectory()) {
                    tesseract.setDatapath(path);
                    tessdataFound = true;
                    System.out.println("Using tessdata path: " + path);
                    break;
                }
            }
            
            if (!tessdataFound) {
                throw new Exception("Tessdata directory not found. Tried paths: " + String.join(", ", possiblePaths));
            }
            
            tesseract.setLanguage("eng");
            tesseract.setPageSegMode(1);
            tesseract.setOcrEngineMode(1);
            
            // Test the initialization with a small image
            BufferedImage testImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
            tesseract.doOCR(testImage);
            
        } catch (Exception e) {
            showError("Failed to initialize OCR engine:\n\n" + 
                     "Error: " + e.getClass().getSimpleName() + "\n" +
                     "Message: " + e.getMessage() + "\n\n" +
                     "This usually means:\n" +
                     "1. Tesseract native libraries are not properly installed\n" +
                     "2. Tessdata files are missing or in wrong location\n" +
                     "3. Java cannot find the native libraries\n\n" +
                     "Solutions:\n" +
                     "1. Install Tesseract: brew install tesseract\n" +
                     "2. Check tessdata location: brew --prefix tesseract\n" +
                     "3. Verify language files exist in tessdata directory\n\n" +
                     "Architecture issue detected: You're on Apple Silicon (ARM64) but\n" +
                     "the bundled libraries are for x86_64. Using system Tesseract instead.");
        }
    }
    
    private void openFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Image Files", "png", "jpg", "jpeg", "gif", "bmp", "tiff", "tif");
        fileChooser.setFileFilter(filter);
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            processImage(selectedFile);
        }
    }
    
    private void processImage(File imageFile) {
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                SwingUtilities.invokeLater(() -> {
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                    progressBar.setString("Processing image...");
                    statusLabel.setText("Extracting text from: " + imageFile.getName());
                    uploadButton.setEnabled(false);
                });
                
                try {
                    BufferedImage image = ImageIO.read(imageFile);
                    if (image == null) {
                        throw new IOException("Could not read image file");
                    }
                    
                    String extractedText = tesseract.doOCR(image);
                    return extractedText;
                } catch (Exception e) {
                    throw new Exception("Error processing image: " + e.getMessage());
                }
            }
            
            @Override
            protected void done() {
                try {
                    String result = get();
                    textArea.setText(result);
                    textArea.setCaretPosition(0);
                    copyButton.setEnabled(true);
                    clearButton.setEnabled(true);
                    statusLabel.setText("Text extracted successfully from: " + imageFile.getName());
                } catch (Exception e) {
                    showError("Failed to extract text: " + e.getMessage());
                    statusLabel.setText("Error extracting text");
                } finally {
                    progressBar.setVisible(false);
                    uploadButton.setEnabled(true);
                }
            }
        };
        
        worker.execute();
    }
    
    private void copyToClipboard() {
        String text = textArea.getText();
        if (text != null && !text.trim().isEmpty()) {
            StringSelection selection = new StringSelection(text);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
            statusLabel.setText("Text copied to clipboard");
            
            // Show temporary success message
            Timer timer = new Timer(2000, e -> statusLabel.setText("Ready"));
            timer.setRepeats(false);
            timer.start();
        }
    }
    
    private void clearText() {
        textArea.setText("");
        copyButton.setEnabled(false);
        clearButton.setEnabled(false);
        statusLabel.setText("Text cleared");
        
        // Reset status after 2 seconds
        Timer timer = new Timer(2000, e -> statusLabel.setText("Ready"));
        timer.setRepeats(false);
        timer.start();
    }
    
    private void showError(String message) {
        // Create a custom error dialog with copy functionality
        JPanel panel = new JPanel(new BorderLayout());
        
        JTextArea errorText = new JTextArea(message);
        errorText.setEditable(false);
        errorText.setFont(new Font("Monospaced", Font.PLAIN, 11));
        errorText.setBackground(new Color(255, 240, 240));
        errorText.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        errorText.setLineWrap(true);
        errorText.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(errorText);
        scrollPane.setPreferredSize(new Dimension(500, 200));
        
        JButton copyButton = new JButton("Copy Error");
        copyButton.addActionListener(e -> {
            StringSelection selection = new StringSelection(message);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
            copyButton.setText("Copied!");
            Timer timer = new Timer(2000, ev -> copyButton.setText("Copy Error"));
            timer.setRepeats(false);
            timer.start();
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(copyButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        JOptionPane.showMessageDialog(this, panel, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    // DropTargetListener implementation
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            dtde.acceptDrag(DnDConstants.ACTION_COPY);
            dropZoneLabel.setText("<html><center><b>Drop Image Here</b><br>" +
                                 "<small>Release to process image</small></center></html>");
            dropZoneLabel.setForeground(new Color(0, 100, 0));
        } else {
            dtde.rejectDrag();
        }
    }
    
    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        // Visual feedback can be added here
    }
    
    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
        // Not needed for this implementation
    }
    
    @Override
    public void dragExit(DropTargetEvent dte) {
        dropZoneLabel.setText("<html><center><b>Drag & Drop Image Here</b><br>" +
                             "or click Upload to select file<br>" +
                             "<small>Supported formats: PNG, JPG, JPEG, GIF, BMP, TIFF</small></center></html>");
        dropZoneLabel.setForeground(new Color(70, 130, 180));
    }
    
    @Override
    public void drop(DropTargetDropEvent dtde) {
        try {
            if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                dtde.acceptDrop(DnDConstants.ACTION_COPY);
                
                @SuppressWarnings("unchecked")
                java.util.List<File> files = (java.util.List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                
                if (files.size() > 0) {
                    File file = files.get(0);
                    if (isImageFile(file)) {
                        processImage(file);
                    } else {
                        showError("Please drop an image file (PNG, JPG, JPEG, GIF, BMP, TIFF)");
                    }
                }
                
                dtde.dropComplete(true);
            } else {
                dtde.rejectDrop();
            }
        } catch (Exception e) {
            showError("Error processing dropped file: " + e.getMessage());
            dtde.dropComplete(false);
        } finally {
            // Reset drop zone appearance
            dropZoneLabel.setText("<html><center><b>Drag & Drop Image Here</b><br>" +
                                 "or click Upload to select file<br>" +
                                 "<small>Supported formats: PNG, JPG, JPEG, GIF, BMP, TIFF</small></center></html>");
            dropZoneLabel.setForeground(new Color(70, 130, 180));
        }
    }
    
    private boolean isImageFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") ||
               name.endsWith(".gif") || name.endsWith(".bmp") || name.endsWith(".tiff") ||
               name.endsWith(".tif");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ImageToText().setVisible(true);
        });
    }
}

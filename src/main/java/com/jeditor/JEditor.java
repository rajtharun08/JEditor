package com.jeditor; 

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.swing.*; 
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;

/**
 * JEditor is  built with Java Swing
 */
public class JEditor extends JFrame {
	
    // UI COMPONENTS 
    private JTextArea textArea;
    private JMenuBar menuBar;
    private JMenu fileMenu, editMenu,formatMenu;
    private JMenuItem newMenuItem, openMenuItem, saveMenuItem, saveAsMenuItem, exitMenuItem;
    private JMenuItem cutMenuItem, copyMenuItem, pasteMenuItem,findMenuItem;
    private JMenuItem fontMenuItem;
    private JPanel statusBar;
    private JLabel wordCountLabel;
    private JLabel lineCountLabel;

    
    // STATE VARIABLES 
    private File currentFile;
    private boolean hasUnsavedChanges = false;
    
    // DIALOGS 
    private FindDialog findDialog;
    private FontDialog fontDialog;
    
    public JEditor() {
        // CONFIGURE THE WINDOW 
        setTitle("JEditor");
        setSize(800, 600); // window dimensions
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // app close
        setLocationRelativeTo(null); // Center window
        
        //  Set the layout manager for the frame to allow components in different regions
        setLayout(new BorderLayout());
        
     // CREATE UI COMPONENTS 
        // 1. Create Text Area
        textArea = new JTextArea();
        // common editor font
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        // 2. Create Menu Bar
        menuBar = new JMenuBar();


        // 3. File Menu
        fileMenu = new JMenu("File");
        newMenuItem = new JMenuItem("New");
        openMenuItem = new JMenuItem("Open");
        saveMenuItem = new JMenuItem("Save");
        saveAsMenuItem = new JMenuItem("Save As...");
        exitMenuItem = new JMenuItem("Exit");

        // 4. Edit Menu
        editMenu = new JMenu("Edit");
        cutMenuItem = new JMenuItem("Cut");
        copyMenuItem = new JMenuItem("Copy");
        pasteMenuItem = new JMenuItem("Paste");
        findMenuItem = new JMenuItem("Find...");
        formatMenu = new JMenu("Format");
        fontMenuItem = new JMenuItem("Font..."); 

        // ADD COMPONENTS TO THE LAYOUT 
        // 1. Add menu items 
        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        editMenu.add(cutMenuItem);
        editMenu.add(copyMenuItem);
        editMenu.add(pasteMenuItem);
        editMenu.addSeparator();
        editMenu.add(findMenuItem);
        formatMenu.add(fontMenuItem);

        // 2. Add menus 
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(formatMenu);
        setJMenuBar(menuBar);

        // 3. Set menu bar 
        setJMenuBar(menuBar);
        
        //STATUS BAR SETUP 
        // panel will hold our labels at the bottom of the screen
        statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5)); // Aligns left with gaps
        wordCountLabel = new JLabel("Words: 0");
        lineCountLabel = new JLabel("Lines: 1");
        statusBar.add(wordCountLabel);
        statusBar.add(lineCountLabel);
        // Add the status bar to the SOUTH (bottom) of the frame
        add(statusBar, BorderLayout.SOUTH);
        
        // 4. Add text area 
        JScrollPane scrollPane = new JScrollPane(textArea);

        // 5. Add scroll pane 
        // Modified for Day 6: Specify CENTER region to work with the status bar
        add(scrollPane, BorderLayout.CENTER); 
        
        // ACTION LISTENERS

        // File Menu Listeners
        newMenuItem.addActionListener(e -> newFile()); // call newFile()
        exitMenuItem.addActionListener(e -> System.exit(0)); // Closes application

        // Edit Menu Listeners
        cutMenuItem.addActionListener(e -> textArea.cut());
        copyMenuItem.addActionListener(e -> textArea.copy());
        pasteMenuItem.addActionListener(e -> textArea.paste());
        
        // Open and Save Listeners
        openMenuItem.addActionListener(e -> openFile());
        saveAsMenuItem.addActionListener(e -> saveFileAs());
        saveMenuItem.addActionListener(e -> saveFile());
        findMenuItem.addActionListener(e -> showFindDialog());
        fontMenuItem.addActionListener(e -> showFontDialog());
        
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                markAsUnsaved();
                updateStatus(); 
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                markAsUnsaved();
                updateStatus(); // New for Day 6
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                // plain text components do not fire this event
            }
        });
        
        // Update the status bar once when the application starts
        updateStatus();
    }
    // helper functions
    private void markAsUnsaved() {
        if (!hasUnsavedChanges) {
            hasUnsavedChanges = true;
            updateTitle();
        }
    }
    
    private void markAsSaved() {
        hasUnsavedChanges = false;
        updateTitle();
    }

    private void updateTitle() {
        String title = "JEditor";
        if (currentFile != null) {
            title += " - " + currentFile.getName();
        }
        if (hasUnsavedChanges) {
            title += "*";
        }
        setTitle(title);
    }
    
    // This method calculates and updates the word and line counts.
    private void updateStatus() {
        String text = textArea.getText();
        
        // Logic: split the text by whitespace to get words
        String[] words = text.trim().split("\\s+");
        int wordCount = text.isEmpty() ? 0 : words.length;
        wordCountLabel.setText("Words: " + wordCount);

        // has a built-in method for this
        int lineCount = textArea.getLineCount();
        lineCountLabel.setText("Lines: " + lineCount);
    }


    //ACTION LISTENER METHODS
    private void newFile() {
        textArea.setText("");
        currentFile = null;
        markAsSaved(); // new file is considered saved initially
        updateStatus();
    }
    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                String content = Files.readString(file.toPath());
                textArea.setText(content);
                currentFile = file;
                markAsSaved();
                updateStatus();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Could not read file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveFile() {
        if (currentFile == null) {
            // if it's a new file, delegate to saveFileAs()
            saveFileAs();
        } else {
            // save to the existing file
            try {
                Files.writeString(currentFile.toPath(), textArea.getText());
                markAsSaved();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Could not save file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void saveFileAs() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                Files.writeString(file.toPath(), textArea.getText());
                currentFile = file;
                markAsSaved();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Could not save file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void showFindDialog() {
        if (findDialog == null) {
            findDialog = new FindDialog(this);
        }
        findDialog.setVisible(true);
    }
    private void showFontDialog() {
        if (fontDialog == null) {
            fontDialog = new FontDialog(this);
        }
        fontDialog.setVisible(true);
    }
    
    // INNER CLASS FOR FIND DIALOG 
    private class FindDialog extends JDialog {
        private JTextField searchField;
        private JButton findNextButton;
        private int lastMatchIndex = -1;

        FindDialog(JFrame owner) {
            super(owner, "Find", false); // Title, not modal
            searchField = new JTextField(20);
            findNextButton = new JButton("Find Next");

            setLayout(new FlowLayout());
            add(new JLabel("Find what:"));
            add(searchField);
            add(findNextButton);
            pack(); // Sizes the dialog to fit its components
            setLocationRelativeTo(owner);

            findNextButton.addActionListener(e -> findNext());
        }

        private void findNext() {
            String searchText = searchField.getText();
            String content = textArea.getText();
            
            // Start searching from the character after the last match
            int searchFrom = (lastMatchIndex == -1) ? 0 : lastMatchIndex + 1;
            
            lastMatchIndex = content.indexOf(searchText, searchFrom);

            if (lastMatchIndex != -1) {
                // Found a match, highlight it
                textArea.requestFocusInWindow();
                textArea.select(lastMatchIndex, lastMatchIndex + searchText.length());
            } else {
                // No match found from the current position
                JOptionPane.showMessageDialog(this, "No more occurrences found.", "Find", JOptionPane.INFORMATION_MESSAGE);
                lastMatchIndex = -1; // Reset for next search from the beginning
            }
        }
    }
    private class FontDialog extends JDialog 
    {
        private JList<String> fontList;
        private JSpinner sizeSpinner;
        private JCheckBox boldCheckbox, italicCheckbox;
        private JLabel previewLabel;

        FontDialog(JFrame owner) {
            super(owner, "Choose Font", true); // Modal dialog

            // Get all available font names from the system 
            String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

            // Create Components 
            fontList = new JList<>(fontNames);
            sizeSpinner = new JSpinner(new SpinnerNumberModel(14, 8, 72, 1)); // Initial, min, max, step
            boldCheckbox = new JCheckBox("Bold");
            italicCheckbox = new JCheckBox("Italic");
            previewLabel = new JLabel("AaBbYyZz");
            previewLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            JButton okButton = new JButton("OK");
            JButton cancelButton = new JButton("Cancel");

            //  Set initial values based on current text area font 
            Font currentFont = textArea.getFont();
            fontList.setSelectedValue(currentFont.getFamily(), true);
            sizeSpinner.setValue(currentFont.getSize());
            boldCheckbox.setSelected(currentFont.isBold());
            italicCheckbox.setSelected(currentFont.isItalic());
            previewLabel.setFont(currentFont);

            // Layout 
            JPanel controlsPanel = new JPanel(new GridLayout(3, 1));
            controlsPanel.add(new JScrollPane(fontList));
            
            JPanel sizeAndStylePanel = new JPanel(new FlowLayout());
            sizeAndStylePanel.add(new JLabel("Size:"));
            sizeAndStylePanel.add(sizeSpinner);
            sizeAndStylePanel.add(boldCheckbox);
            sizeAndStylePanel.add(italicCheckbox);
            controlsPanel.add(sizeAndStylePanel);
            
            JPanel previewPanel = new JPanel(new BorderLayout());
            previewPanel.setBorder(BorderFactory.createTitledBorder("Preview"));
            previewPanel.add(previewLabel, BorderLayout.CENTER);
            controlsPanel.add(previewPanel);
            
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonsPanel.add(okButton);
            buttonsPanel.add(cancelButton);

            setLayout(new BorderLayout());
            add(controlsPanel, BorderLayout.CENTER);
            add(buttonsPanel, BorderLayout.SOUTH);
            
            // Listeners 
            okButton.addActionListener(e -> {
                applyFont();
                setVisible(false);
            });
            cancelButton.addActionListener(e -> setVisible(false));
            
            // Listener to update the preview label in real-time
            fontList.addListSelectionListener(e -> updatePreview());
            sizeSpinner.addChangeListener(e -> updatePreview());
            boldCheckbox.addActionListener(e -> updatePreview());
            italicCheckbox.addActionListener(e -> updatePreview());
            
            pack();
            setLocationRelativeTo(owner);
        }

        private void updatePreview() {
            String fontName = fontList.getSelectedValue();
            int size = (Integer) sizeSpinner.getValue();
            int style = Font.PLAIN;
            if (boldCheckbox.isSelected()) style |= Font.BOLD;
            if (italicCheckbox.isSelected()) style |= Font.ITALIC;
            
            Font newFont = new Font(fontName, style, size);
            previewLabel.setFont(newFont);
        }
        
        private void applyFont() {
            String fontName = fontList.getSelectedValue();
            int size = (Integer) sizeSpinner.getValue();
            int style = Font.PLAIN;
            if (boldCheckbox.isSelected()) style |= Font.BOLD;
            if (italicCheckbox.isSelected()) style |= Font.ITALIC;
            
            Font newFont = new Font(fontName, style, size);
            textArea.setFont(newFont);
        }
    }
 
    
    public static void main(String[] args) {
        // Run GUI on the Event Dispatch Thread 
        SwingUtilities.invokeLater(() -> {
            new JEditor().setVisible(true);
        });
    }
}
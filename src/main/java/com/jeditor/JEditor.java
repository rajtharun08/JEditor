package com.jeditor; 

import javax.swing.JFrame;
import javax.swing.JLabel; 
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.BorderLayout; 
import java.awt.FlowLayout;  
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;  
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * JEditor is  built with Java Swing
 */
public class JEditor extends JFrame {
	
    // UI COMPONENTS 
    private JTextArea textArea;
    private JMenuBar menuBar;
    private JMenu fileMenu, editMenu;
    private JMenuItem newMenuItem, openMenuItem, saveMenuItem, saveAsMenuItem, exitMenuItem;
    private JMenuItem cutMenuItem, copyMenuItem, pasteMenuItem;
    private JPanel statusBar;
    private JLabel wordCountLabel;
    private JLabel lineCountLabel;

    
    // STATE VARIABLES 
    private File currentFile;
    private boolean hasUnsavedChanges = false;
    
    public JEditor() {
        // CONFIGURE THE WINDOW 
        setTitle("JEditor");
        setSize(800, 600); // window dimensions
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // app close
        setLocationRelativeTo(null); // Center window
        
        // New for Day 6: Set the layout manager for the frame to allow components in different regions
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

        // 2. Add menus 
        menuBar.add(fileMenu);
        menuBar.add(editMenu);

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


    
    public static void main(String[] args) {
        // Run GUI on the Event Dispatch Thread 
        SwingUtilities.invokeLater(() -> {
            new JEditor().setVisible(true);
        });
    }
}
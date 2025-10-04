package com.jeditor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * JEditor is a simple text editor built with Java Swing.
 */
public class JEditor extends JFrame {

    // ui components
    private JTextArea textArea;
    private JMenuBar menuBar;
    private JMenu fileMenu, editMenu, formatMenu, helpMenu;
    private JMenuItem newMenuItem, openMenuItem, saveMenuItem, saveAsMenuItem, exitMenuItem;
    private JMenuItem cutMenuItem, copyMenuItem, pasteMenuItem, findMenuItem;
    private JMenuItem fontMenuItem;
    private JMenuItem aboutMenuItem;
    private JPanel statusBar;
    private JLabel wordCountLabel;
    private JLabel lineCountLabel;

    // state variables
    private File currentFile;
    private boolean hasUnsavedChanges = false;

    // dialogs
    private FindDialog findDialog;
    private FontDialog fontDialog;

    /**
     * constructor for the jeditor class.
     */
    public JEditor() {
        // configure the window
        setTitle("JEditor");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // create ui components
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        // create the entire menu system
        createMenuBar();

        // create the status bar at the bottom
        statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        wordCountLabel = new JLabel("Words: 0");
        lineCountLabel = new JLabel("Lines: 1");
        statusBar.add(wordCountLabel);
        statusBar.add(lineCountLabel);

        // add components to frame's layout
        setJMenuBar(menuBar);
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        // initialize listeners
        addMenuActionListeners();
        addDocumentListener();
        
        // update status bar on startup
        updateStatus();
    }

    /**
     * helper method to create and assemble the menu bar.
     */
    private void createMenuBar() {
        menuBar = new JMenuBar();

        // file menu
        fileMenu = new JMenu("File");
        newMenuItem = new JMenuItem("New");
        openMenuItem = new JMenuItem("Open");
        saveMenuItem = new JMenuItem("Save");
        saveAsMenuItem = new JMenuItem("Save As...");
        exitMenuItem = new JMenuItem("Exit");
        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        // edit menu
        editMenu = new JMenu("Edit");
        cutMenuItem = new JMenuItem("Cut");
        copyMenuItem = new JMenuItem("Copy");
        pasteMenuItem = new JMenuItem("Paste");
        findMenuItem = new JMenuItem("Find...");
        editMenu.add(cutMenuItem);
        editMenu.add(copyMenuItem);
        editMenu.add(pasteMenuItem);
        editMenu.addSeparator();
        editMenu.add(findMenuItem);

        // format menu
        formatMenu = new JMenu("Format");
        fontMenuItem = new JMenuItem("Font...");
        formatMenu.add(fontMenuItem);

        // help menu
        helpMenu = new JMenu("Help");
        aboutMenuItem = new JMenuItem("About");
        helpMenu.add(aboutMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(formatMenu);
        menuBar.add(helpMenu);
    }

    /**
     * helper method to attach all action listeners to menu items.
     */
    private void addMenuActionListeners() {
        newMenuItem.addActionListener(e -> newFile());
        openMenuItem.addActionListener(e -> openFile());
        saveMenuItem.addActionListener(e -> saveFile());
        saveAsMenuItem.addActionListener(e -> saveFileAs());
        exitMenuItem.addActionListener(e -> System.exit(0));

        cutMenuItem.addActionListener(e -> textArea.cut());
        copyMenuItem.addActionListener(e -> textArea.copy());
        pasteMenuItem.addActionListener(e -> textArea.paste());
        findMenuItem.addActionListener(e -> showFindDialog());

        fontMenuItem.addActionListener(e -> showFontDialog());
        
        aboutMenuItem.addActionListener(e -> showAboutDialog());
    }
    
    /**
     * attaches a listener to the text area's document to track changes.
     */
    private void addDocumentListener() {
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { 
            	markAsUnsaved(); updateStatus(); 
            }
            public void removeUpdate(DocumentEvent e) { 
            	markAsUnsaved(); updateStatus();
            }
            public void changedUpdate(DocumentEvent e) {
            	/* not used for plain text */ 
            }
        });
    }

    // state management & ui updates

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

    private void updateStatus() {
        String text = textArea.getText();
        String[] words = text.trim().split("\\s+");
        int wordCount = text.isEmpty() ? 0 : words.length;
        wordCountLabel.setText("Words: " + wordCount);
        int lineCount = textArea.getLineCount();
        lineCountLabel.setText("Lines: " + lineCount);
    }

    // dialog show methods

    private void showAboutDialog() {
        String aboutMessage = "JEditor - Version 1.0\n\n"
                            + "A simple text editor built with Java Swing.\n"
                            + "Created by Tharun";
        JOptionPane.showMessageDialog(this, aboutMessage, "About JEditor", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showFindDialog() { 
    	if (findDialog == null) findDialog = new FindDialog(this); findDialog.setVisible(true); }
    private void showFontDialog() {
    	if (fontDialog == null) fontDialog = new FontDialog(this); fontDialog.setVisible(true); }

    // file actions

    private void newFile() { 
    	textArea.setText(""); currentFile = null;
    	markAsSaved();
    	updateStatus(); 
    	}
    
    private void openFile() { 
    	JFileChooser fc = new JFileChooser();
    	if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { File f = fc.getSelectedFile(); try { textArea.setText(Files.readString(f.toPath())); currentFile = f; markAsSaved(); updateStatus(); } catch (IOException ex) { JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); } } }
    private void saveFile() { 
    	if (currentFile == null) {
    		saveFileAs(); 
    		} 
    	else {
    		try {
    			Files.writeString(currentFile.toPath(), textArea.getText()); 
    			markAsSaved();
    			}
    		catch (IOException ex) { 
    			JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); 
    			}
    		} 
    	}
    private void saveFileAs() { 
    	JFileChooser fc = new JFileChooser(); 
    	if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
    		File f = fc.getSelectedFile(); 
    		try {
    			Files.writeString(f.toPath(), textArea.getText()); currentFile = f;
    			markAsSaved();
    			}
    		catch (IOException ex) {
    			JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
    		}
    	}

    // inner class for find dialog
    private class FindDialog extends JDialog { private JTextField searchField; private JButton findNextButton; private int lastMatchIndex = -1; FindDialog(JFrame owner) { super(owner, "Find", false); searchField = new JTextField(20); findNextButton = new JButton("Find Next"); setLayout(new FlowLayout()); add(new JLabel("Find what:")); add(searchField); add(findNextButton); pack(); setLocationRelativeTo(owner); findNextButton.addActionListener(e -> findNext()); } private void findNext() { String searchText = searchField.getText(); String content = textArea.getText(); int searchFrom = (lastMatchIndex == -1) ? 0 : lastMatchIndex + 1; lastMatchIndex = content.indexOf(searchText, searchFrom); if (lastMatchIndex != -1) { textArea.requestFocusInWindow(); textArea.select(lastMatchIndex, lastMatchIndex + searchText.length()); } else { JOptionPane.showMessageDialog(this, "No more occurrences found.", "Find", JOptionPane.INFORMATION_MESSAGE); lastMatchIndex = -1; } } }

    // inner class for font dialog
    private class FontDialog extends JDialog { private JList<String> fontList; private JSpinner sizeSpinner; private JCheckBox boldCheckbox, italicCheckbox; private JLabel previewLabel; FontDialog(JFrame owner) { super(owner, "Choose Font", true); String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(); fontList = new JList<>(fontNames); sizeSpinner = new JSpinner(new SpinnerNumberModel(14, 8, 72, 1)); boldCheckbox = new JCheckBox("Bold"); italicCheckbox = new JCheckBox("Italic"); previewLabel = new JLabel("AaBbYyZz"); previewLabel.setHorizontalAlignment(SwingConstants.CENTER); JButton okButton = new JButton("OK"); JButton cancelButton = new JButton("Cancel"); Font currentFont = textArea.getFont(); fontList.setSelectedValue(currentFont.getFamily(), true); sizeSpinner.setValue(currentFont.getSize()); boldCheckbox.setSelected(currentFont.isBold()); italicCheckbox.setSelected(currentFont.isItalic()); previewLabel.setFont(currentFont); JPanel controlsPanel = new JPanel(new GridLayout(3, 1, 5, 5)); controlsPanel.add(new JScrollPane(fontList)); JPanel sizeAndStylePanel = new JPanel(new FlowLayout()); sizeAndStylePanel.add(new JLabel("Size:")); sizeAndStylePanel.add(sizeSpinner); sizeAndStylePanel.add(boldCheckbox); sizeAndStylePanel.add(italicCheckbox); controlsPanel.add(sizeAndStylePanel); JPanel previewPanel = new JPanel(new BorderLayout()); previewPanel.setBorder(BorderFactory.createTitledBorder("Preview")); previewPanel.add(previewLabel, BorderLayout.CENTER); controlsPanel.add(previewPanel); JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); buttonsPanel.add(okButton); buttonsPanel.add(cancelButton); setLayout(new BorderLayout(5, 5)); add(controlsPanel, BorderLayout.CENTER); add(buttonsPanel, BorderLayout.SOUTH); okButton.addActionListener(e -> { applyFont(); setVisible(false); }); cancelButton.addActionListener(e -> setVisible(false)); fontList.addListSelectionListener(e -> updatePreview()); sizeSpinner.addChangeListener(e -> updatePreview()); boldCheckbox.addActionListener(e -> updatePreview()); italicCheckbox.addActionListener(e -> updatePreview()); pack(); setLocationRelativeTo(owner); } private void updatePreview() { String name = fontList.getSelectedValue(); int size = (Integer) sizeSpinner.getValue(); int style = (boldCheckbox.isSelected() ? Font.BOLD : 0) | (italicCheckbox.isSelected() ? Font.ITALIC : 0); previewLabel.setFont(new Font(name, style, size)); } private void applyFont() { String name = fontList.getSelectedValue(); int size = (Integer) sizeSpinner.getValue(); int style = (boldCheckbox.isSelected() ? Font.BOLD : 0) | (italicCheckbox.isSelected() ? Font.ITALIC : 0); textArea.setFont(new Font(name, style, size)); } }
    
    /**
     * the main entry point for the JEditor application.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JEditor().setVisible(true));
    }
}
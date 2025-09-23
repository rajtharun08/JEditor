package com.jeditor; 

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.Font;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
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
    private JMenuItem newMenuItem, openMenuItem, saveMenuItem, exitMenuItem;
    private JMenuItem cutMenuItem, copyMenuItem, pasteMenuItem;
    
    public JEditor() {
        // CONFIGURE THE WINDOW 
        setTitle("JEditor");
        setSize(800, 600); // window dimensions
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // app close
        setLocationRelativeTo(null); // Center window
        
     // CREATE UI COMPONENTS 
        // 1. Create Text Area
        textArea = new JTextArea();
        // Use a monospaced font which is common for editors
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        // 2. Create Menu Bar
        menuBar = new JMenuBar();

        // 3. File Menu
        fileMenu = new JMenu("File");
        newMenuItem = new JMenuItem("New");
        openMenuItem = new JMenuItem("Open");
        saveMenuItem = new JMenuItem("Save");
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
        fileMenu.add(exitMenuItem);

        editMenu.add(cutMenuItem);
        editMenu.add(copyMenuItem);
        editMenu.add(pasteMenuItem);

        // 2. Add menus 
        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        // 3. Set menu bar 
        setJMenuBar(menuBar);

        // 4. Add text area 
        JScrollPane scrollPane = new JScrollPane(textArea);

        // 5. Add scroll pane 
        add(scrollPane);       
    }

    
    public static void main(String[] args) {
        // Run GUI on the Event Dispatch Thread 
        SwingUtilities.invokeLater(() -> {
            new JEditor().setVisible(true);
        });
    }
}
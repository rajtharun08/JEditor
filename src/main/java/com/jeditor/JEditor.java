package com.jeditor; 

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * JEditor is  built with Java Swing
 */
public class JEditor extends JFrame {

    public JEditor() {
        // CONFIGURE THE WINDOW 
        setTitle("JEditor");
        setSize(800, 600); // window dimensions
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // app close
        setLocationRelativeTo(null); // Center window
    }

    public static void main(String[] args) {
        // Run GUI on the Event Dispatch Thread 
        SwingUtilities.invokeLater(() -> {
            new JEditor().setVisible(true);
        });
    }
}
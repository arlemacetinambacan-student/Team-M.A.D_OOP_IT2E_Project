package com.woms.main;

import com.woms.database.Database;
import com.woms.gui.frames.RoleSelectionFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Check if H2 driver is available before proceeding
        if (!checkDriver()) {
            showDriverError();
            return;
        }
        
        try {
            Database.init();
            // Always ensure poverty reduction jobs exist (removes IT jobs and creates poverty jobs)
            Database.ensurePovertyReductionJobs();
            // Seed sample data if database is empty
            Database.seedSampleData();
            // Start H2 web console in a separate thread so it doesn't block the GUI
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // Wait 1 second for initialization
                    Database.startWebConsole();
                } catch (Exception e) {
                    // Console startup failed, but app can still run
                }
            }).start();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Failed to initialize database:\n" + e.getMessage() + 
                "\n\nPlease ensure h2-1.4.200.jar (Java 8 compatible) is in your classpath.",
                "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(() -> {
            RoleSelectionFrame roleFrame = new RoleSelectionFrame();
            roleFrame.setVisible(true);
        });
    }
    
    private static boolean checkDriver() {
        try {
            Class.forName("org.h2.Driver");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    private static void showDriverError() {
        String message = "H2 JDBC Driver Not Found!\n\n" +
            "The h2-1.4.200.jar file (Java 8 compatible) is not in your classpath.\n\n" +
            "SOLUTIONS:\n\n" +
            "Download H2 Database (Java 8 compatible):\n" +
            "  https://repo1.maven.org/maven2/com/h2database/h2/1.4.200/h2-1.4.200.jar\n" +
            "  Or: https://www.h2database.com/html/download.html\n\n" +
            "If using Command Line:\n" +
            "  java -cp \".;lib/h2-1.4.200.jar\" com.woms.main.Main\n\n" +
            "If using IDE:\n" +
            "  1. Right-click project → Properties\n" +
            "  2. Add lib/h2-1.4.200.jar to Libraries/Classpath\n" +
            "  3. Download from Maven Central if needed\n\n" +
            "IMPORTANT: Use h2-1.4.200.jar for Java 8 compatibility!\n" +
            "Make sure h2-1.4.200.jar is in the lib folder!";
            
        JOptionPane.showMessageDialog(null, message, 
            "Driver Not Found", JOptionPane.ERROR_MESSAGE);
    }
}

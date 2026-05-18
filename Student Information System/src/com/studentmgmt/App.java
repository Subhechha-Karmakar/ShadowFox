package com.studentmgmt;

import com.studentmgmt.view.StudentView;
import com.studentmgmt.view.ThemeManager;

import javax.swing.*;

/**
 * Application entry point.
 * Installs the dark theme then launches StudentView on the EDT.
 */
public class App {
    public static void main(String[] args) {
        ThemeManager.install();
        SwingUtilities.invokeLater(() -> new StudentView());
    }
}

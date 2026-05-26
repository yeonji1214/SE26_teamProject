package its.ui.gui.common;

import javax.swing.*;

public class UIConfig {
    public static void initialize() {
        setupLookAndFeel();
        customizeUIDefaults();
    }

    private static void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Failed to set Look and Feel: " + e.getMessage());
        }
    }

    private static void customizeUIDefaults() {
        UIManager.put("TableHeader.cellBorder", BorderFactory.createEmptyBorder(0, 10, 0, 10));
    }
}

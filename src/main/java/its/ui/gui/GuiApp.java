package its.ui.gui;

import its.ui.gui.common.UIConfig;
import javax.swing.*;

public class GuiApp {
    public static void main(String[] args) {
        UIConfig.initialize();

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}

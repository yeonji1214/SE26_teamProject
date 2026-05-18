package its.ui.gui;

import its.ui.gui.panel.LoginPanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JPanel contentPanel;

    public MainFrame() {
        setTitle("ITS");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        LoginPanel loginPanel = new LoginPanel();
        add(loginPanel, BorderLayout.CENTER);
    }
}

package its.ui.gui;

import its.ui.gui.panel.LoginPanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private LoginPanel loginPanel;
    private JPanel mainPanel;
    private String currentUser;

    private static final String LOGIN_CARD = "LOGIN";
    private static final String MAIN_CARD = "MAIN";

    public MainFrame() {
        setTitle("ITS");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        // Set CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Create LoginPanel, add Listener
        loginPanel = new LoginPanel();
        loginPanel.setLoginListener(username -> {
            currentUser = username;
            showMainPanel();
        });

        mainPanel = createTemporaryMainPanel();

        contentPanel.add(loginPanel, LOGIN_CARD);
        contentPanel.add(mainPanel, MAIN_CARD);

        add(contentPanel, BorderLayout.CENTER);

        cardLayout.show(contentPanel, LOGIN_CARD);
    }

    private void showLoginPanel() {
        cardLayout.show(contentPanel, LOGIN_CARD);
        loginPanel.clear(); // Clear text fields
    }

    private void showMainPanel() {
        System.out.println("Switching to MAIN screen ...");
        cardLayout.show(contentPanel, MAIN_CARD);
    }

    private JPanel createTemporaryMainPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.LIGHT_GRAY);
        return panel;
    }
}

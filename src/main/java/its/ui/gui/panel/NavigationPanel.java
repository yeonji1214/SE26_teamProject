package its.ui.gui.panel;

import its.ui.gui.common.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class NavigationPanel extends JPanel {

    private JButton projectsButton;
    private JButton issuesButton;
    private JButton createIssueButton;
    private JButton statisticsButton;
    private JButton logoutButton;

    private NavigationListener listener;

    private JButton currentSelectedButton;

    public NavigationPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(UIConstants.NAVIGATION_COLOR);
        setPreferredSize(new Dimension(170, 740));

        projectsButton = createNavButton("Projects");
        issuesButton = createNavButton("Issues");
        createIssueButton = createNavButton("Create Issue");
        statisticsButton = createNavButton("Statistics");
        logoutButton = createNavButton("Logout");

        setupButtonListeners();

        add(projectsButton);
        add(issuesButton);
        add(createIssueButton);
        add(statisticsButton);

        add(Box.createVerticalGlue());

        add(logoutButton);

        add(Box.createRigidArea(new Dimension(0, 20)));

        selectButton(projectsButton);
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(UIConstants.NAVIGATION_COLOR);
        button.setOpaque(true);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(UIConstants.BUTTON_FONT);
        button.setPreferredSize(new Dimension(170, 50));
        button.setMaximumSize(new Dimension(170, 50));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));


        button.setHorizontalAlignment(SwingConstants.LEFT);

        button.setMargin(new Insets(0, 10, 0, 0));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e){
                if (button != currentSelectedButton) {
                    button.setBackground(UIConstants.NAVIGATION_HOVER_COLOR);
                }
            }

            @Override
            public void mouseExited(MouseEvent e){
                if (button != currentSelectedButton) {
                    button.setBackground(UIConstants.NAVIGATION_COLOR);
                }
            }
        });

        return button;
    }

    private void setupButtonListeners() {
        attachListener(projectsButton, NavigationListener.PROJECTS);
        attachListener(issuesButton, NavigationListener.ISSUES);
        attachListener(createIssueButton, NavigationListener.CREATE_ISSUES);
        attachListener(statisticsButton, NavigationListener.STATISTICS);
        attachListener(logoutButton, NavigationListener.LOGOUT);
    }

    private void attachListener(JButton button, String menuName) {
        button.addActionListener(e -> {
            System.out.println("[NavigationPanel] Button clicked: " + menuName);

            if (menuName != NavigationListener.LOGOUT) {
                selectButton(button);
            }

            if (listener != null) {
                System.out.println("[NavigationPanel] Listener is set, calling onMenuSelected");
                listener.onMenuSelected(menuName);
            }
            else {
                System.out.println("[NavigationPanel] Warning: Listener is null");
            }
        });
    }

    public void setNavigationListener(NavigationListener listener) {this.listener = listener;}

    public interface NavigationListener {
        void onMenuSelected(String menuName);

        String PROJECTS = "PROJECTS";
        String ISSUES = "ISSUES";
        String CREATE_ISSUES = "CREATE_ISSUE";
        String STATISTICS = "STATISTICS";
        String LOGOUT = "LOGOUT";
    }

    private void selectButton(JButton button) {
        if (currentSelectedButton != null) {
            currentSelectedButton.setBackground(UIConstants.NAVIGATION_COLOR);
        }

        currentSelectedButton = button;
        if (currentSelectedButton != null) {
            currentSelectedButton.setBackground(UIConstants.PRIMARY_BUTTON_COLOR);
        }
    }

    public void selectButton(String menuName) {
        switch (menuName) {
            case NavigationListener.PROJECTS -> selectButton(projectsButton);
            case NavigationListener.ISSUES -> selectButton(issuesButton);
            case NavigationListener.CREATE_ISSUES -> selectButton(createIssueButton);
            case NavigationListener.STATISTICS -> selectButton(statisticsButton);
        }
    }
}

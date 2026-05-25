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

    public NavigationPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(51, 51, 51));
        setPreferredSize(new Dimension(200, 740));

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
        add(logoutButton);
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(UIConstants.NAVIGATION_COLOR);
        button.setOpaque(true);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(UIConstants.BUTTON_FONT);
        button.setMaximumSize(new Dimension(200, 50));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e){
                button.setBackground(new Color(80, 80, 80));
            }

            @Override
            public void mouseExited(MouseEvent e){
                button.setBackground(UIConstants.NAVIGATION_COLOR);
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
            System.out.println("Button clicked: " + menuName);

            if (listener != null) {
                System.out.println("Listener is set, calling onMenuSelected");
                listener.onMenuSelected(menuName);
            }
            else {
                System.out.println("Warning: Listener is null");
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
}

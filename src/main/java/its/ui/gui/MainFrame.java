package its.ui.gui;

import its.ui.gui.common.UIConstants;
import its.ui.gui.panel.*;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private LoginPanel loginPanel;
    private JPanel mainPanel;
    private TitleBarPanel titleBarPanel;
    private NavigationPanel navigationPanel;
    private CardLayout contentCardLayout;
    private JPanel contentAreaPanel;
    private String currentUser;

    private static final String LOGIN_CARD = "LOGIN";
    private static final String MAIN_CARD = "MAIN";

    private static final String PROJECTS_CARD = "PROJECTS";
    private static final String ISSUES_CARD = "ISSUES";
    private static final String CREATE_ISSUE_CARD = "CREATE_ISSUE";
    private static final String STATISTICS_CARD = "STATISTICS";

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

        mainPanel = createMainPanel();

        contentPanel.add(loginPanel, LOGIN_CARD);
        contentPanel.add(mainPanel, MAIN_CARD);

        add(contentPanel, BorderLayout.CENTER);

        cardLayout.show(contentPanel, LOGIN_CARD);
    }

    private void showLoginPanel() {
        loginPanel.clear(); // Clear text fields

        if (mainPanel != null) {
            clearAllContentPanels();
        }

        cardLayout.show(contentPanel, LOGIN_CARD);
    }

    private void showMainPanel() {
        System.out.println("[MainFrame] Switching to MAIN screen ...");
        System.out.println("[MainFrame] Current User: " + currentUser);

        if (titleBarPanel != null) {
            titleBarPanel.setUsername(currentUser);
        }

        cardLayout.show(contentPanel, MAIN_CARD);
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        titleBarPanel  = new TitleBarPanel(currentUser);
        panel.add(titleBarPanel, BorderLayout.NORTH);

        navigationPanel = new NavigationPanel();
        navigationPanel.setNavigationListener(menuName -> {
            System.out.println("[MainFrame] Menu Selected: " + menuName);

            if (menuName.equals("LOGOUT")) {
                handleLogout();
            } else {
                contentCardLayout.show(contentAreaPanel, menuName);
            }
        });

        panel.add(navigationPanel, BorderLayout.WEST);

        contentCardLayout = new CardLayout();
        contentAreaPanel = new JPanel(contentCardLayout);

        contentAreaPanel.add(new ProjectsPanel(), PROJECTS_CARD);

        IssuesPanel issuesPanel = new IssuesPanel();
        issuesPanel.setIssueActionListener(new IssuesPanel.IssueActionListener() {
            @Override
            public void onCreateIssueRequested() {
                contentCardLayout.show(contentAreaPanel, CREATE_ISSUE_CARD);
                navigationPanel.selectButton("CREATE_ISSUE");
            }

            public void onIssueSelected(int issueId) {
                // TODO: 이슈 상세 패널에 ID 전달 후 화면 전환
            }
        });

        contentAreaPanel.add(issuesPanel, ISSUES_CARD);

        CreateIssuePanel createIssuePanel = new CreateIssuePanel();
        createIssuePanel.setCreateIssueActionListener(new CreateIssuePanel.CreateIssueActionListener() {
            @Override
            public void onCancelRequested() {
                System.out.println("[MainFrame] Create Issue Cancel Requested");
                contentCardLayout.show(contentAreaPanel, ISSUES_CARD);
                navigationPanel.selectButton("ISSUE");
            }

            @Override
            public void onSaveRequested(String project, String title, String description, String priority) {
                System.out.println("[MainFrame] Create Issue Save Requested");
                System.out.println("[MainFrame] Selected Project: " + project);
                System.out.println("[MainFrame] Entered Title: " + title);
                System.out.println("[MainFrame] Entered Description: " + description);
                System.out.println("[MainFrame] Selected Priority: " + priority);
                // TODO: validation 및 DB 저장 로직 호출
            }
        });

        contentAreaPanel.add(createIssuePanel, CREATE_ISSUE_CARD);

        contentAreaPanel.add(createTempPanel("Statistics"), STATISTICS_CARD);

        panel.add(contentAreaPanel, BorderLayout.CENTER);

        return panel;
    }

    private void handleLogout() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Logout",
                JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            currentUser = null;
            showLoginPanel();
        }
    }

    // Helper, 추후 삭제
    private JPanel createTempPanel(String label) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.LIGHT_GRAY);

        JLabel titleLabel = new JLabel(label + " - Coming Soon", SwingConstants.CENTER);
        titleLabel.setFont(UIConstants.TITLE_FONT);
        panel.add(titleLabel, BorderLayout.CENTER);

        return panel;
    }

    private void clearAllContentPanels() {
        Component[] components = contentAreaPanel.getComponents();
        for (Component component : components) {
            if (component instanceof BasePanel) {
                ((BasePanel) component).clear();
            }
        }
    }


}

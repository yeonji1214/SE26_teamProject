package its.ui.gui;

import its.domain.issue.IssueStatus;
import its.domain.issue.Priority;
import its.domain.user.User;
import its.service.ApplicationServices;
import its.service.DemoDataSeeder;
import its.service.ServiceFactory;
import its.ui.gui.common.UIConstants;
import its.ui.gui.panel.BasePanel;
import its.ui.gui.panel.CreateIssuePanel;
import its.ui.gui.panel.IssueDetailPanel;
import its.ui.gui.panel.IssuesPanel;
import its.ui.gui.panel.LoginPanel;
import its.ui.gui.panel.NavigationPanel;
import its.ui.gui.panel.ProjectsPanel;
import its.ui.gui.panel.TitleBarPanel;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.nio.file.Path;
import java.util.Objects;

public class MainFrame extends JFrame {
    private static final Path DATABASE_PATH = Path.of("issue-tracker.db");

    private final ApplicationServices services;

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private LoginPanel loginPanel;
    private JPanel mainPanel;
    private TitleBarPanel titleBarPanel;
    private NavigationPanel navigationPanel;
    private CardLayout contentCardLayout;
    private JPanel contentAreaPanel;
    private User currentUser;

    private static final String LOGIN_CARD = "LOGIN";
    private static final String MAIN_CARD = "MAIN";

    private static final String PROJECTS_CARD = "PROJECTS";
    private static final String ISSUES_CARD = "ISSUES";
    private static final String CREATE_ISSUE_CARD = "CREATE_ISSUE";
    private static final String ISSUE_DETAIL_CARD = "ISSUE_DETAIL";
    private static final String STATISTICS_CARD = "STATISTICS";

    public MainFrame() {
        this(createDefaultServices());
    }

    public MainFrame(ApplicationServices services) {
        this.services = Objects.requireNonNull(services, "services must not be null");

        setTitle("ITS");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private static ApplicationServices createDefaultServices() {
        ApplicationServices services = ServiceFactory.createWithSqliteDatabase(DATABASE_PATH);
        new DemoDataSeeder(services).seedIfEmpty();
        return services;
    }

    private void initComponents() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        loginPanel = new LoginPanel();
        loginPanel.setUserService(services.getUserService());
        loginPanel.setLoginListener(user -> {
            currentUser = user;
            showMainPanel();
        });

        mainPanel = createMainPanel();

        contentPanel.add(loginPanel, LOGIN_CARD);
        contentPanel.add(mainPanel, MAIN_CARD);

        add(contentPanel, BorderLayout.CENTER);

        cardLayout.show(contentPanel, LOGIN_CARD);
    }

    private void showLoginPanel() {
        loginPanel.clear();

        if (mainPanel != null) {
            clearAllContentPanels();
        }

        cardLayout.show(contentPanel, LOGIN_CARD);
    }

    private void showMainPanel() {
        System.out.println("[MainFrame] Switching to MAIN screen ...");
        System.out.println("[MainFrame] Current User: " + currentUsername());

        if (titleBarPanel != null) {
            titleBarPanel.setUsername(currentUsername());
        }

        cardLayout.show(contentPanel, MAIN_CARD);
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        titleBarPanel = new TitleBarPanel(currentUsername());
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

        IssuesPanel issuesPanel = new IssuesPanel();
        CreateIssuePanel createIssuePanel = new CreateIssuePanel();
        IssueDetailPanel issueDetailPanel = new IssueDetailPanel();

        issuesPanel.setIssueActionListener(new IssuesPanel.IssueActionListener() {
            @Override
            public void onCreateIssueRequested() {
                contentCardLayout.show(contentAreaPanel, CREATE_ISSUE_CARD);
                navigationPanel.selectButton(NavigationPanel.NavigationListener.CREATE_ISSUES);
            }

            @Override
            public void onIssueSelected(int issueId) {
                issueDetailPanel.loadIssue(issueId);
                contentCardLayout.show(contentAreaPanel, ISSUE_DETAIL_CARD);
                navigationPanel.selectButton(NavigationPanel.NavigationListener.ISSUES);
            }
        });

        createIssuePanel.setCreateIssueActionListener(new CreateIssuePanel.CreateIssueActionListener() {
            @Override
            public void onCancelRequested() {
                System.out.println("[MainFrame] Create Issue Cancel Requested");
                contentCardLayout.show(contentAreaPanel, ISSUES_CARD);
                navigationPanel.selectButton(NavigationPanel.NavigationListener.ISSUES);
            }

            @Override
            public void onCancelFromEditRequested(int issueId) {
                issueDetailPanel.loadIssue(issueId);
                contentCardLayout.show(contentAreaPanel, ISSUE_DETAIL_CARD);
                navigationPanel.selectButton(NavigationPanel.NavigationListener.ISSUES);
            }

            @Override
            public void onSaveRequested(String project, String title, String description, Priority priority) {
                System.out.println("[MainFrame] Create Issue Save Requested");
                System.out.println("[MainFrame] Selected Project: " + project);
                System.out.println("[MainFrame] Entered Title: " + title);
                System.out.println("[MainFrame] Entered Description: " + description);
                System.out.println("[MainFrame] Selected Priority: " + priority);
                // TODO: validation 및 DB 저장 로직 호출
            }
        });

        issueDetailPanel.setIssueDetailActionListener(new IssueDetailPanel.IssueDetailActionListener() {
            @Override
            public void onBackRequested() {
                System.out.println("[MainFrame] Issue Detail Back Requested");
                contentCardLayout.show(contentAreaPanel, ISSUES_CARD);
                navigationPanel.selectButton(NavigationPanel.NavigationListener.ISSUES);
            }

            @Override
            public void onIssueEditRequested(int issueId) {
                System.out.println("[MainFrame] Issue Detail Edit Requested");
                System.out.println("[MainFrame] Selected Issue: " + issueId);
                createIssuePanel.loadIssue(issueId);
                contentCardLayout.show(contentAreaPanel, CREATE_ISSUE_CARD);
                navigationPanel.selectButton(NavigationPanel.NavigationListener.ISSUES);
            }

            @Override
            public void onIssueDeleteRequested(int issueId) {
                System.out.println("[MainFrame] Issue Detail Delete Requested");
                System.out.println("[MainFrame] Selected Issue: " + issueId);
                // TODO: 정말 삭제하겠습니까? 팝업 띄우기(handleLogout 참고)
                contentCardLayout.show(contentAreaPanel, ISSUES_CARD);
                // TODO: 이슈 제거 로직 호출
                navigationPanel.selectButton(NavigationPanel.NavigationListener.ISSUES);
            }

            @Override
            public void onStatusChangeRequested(int issueId, IssueStatus status, String comment) {
                System.out.println("[MainFrame] Issue Detail Change Requested");
                System.out.println("[MainFrame] Selected Issue: " + issueId);
                // TODO: 코멘트 등록 로직 호출
            }
        });

        contentAreaPanel.add(new ProjectsPanel(), PROJECTS_CARD);
        contentAreaPanel.add(issuesPanel, ISSUES_CARD);
        contentAreaPanel.add(createIssuePanel, CREATE_ISSUE_CARD);
        contentAreaPanel.add(issueDetailPanel, ISSUE_DETAIL_CARD);
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

    private String currentUsername() {
        return currentUser == null ? "" : currentUser.getUsername();
    }
}
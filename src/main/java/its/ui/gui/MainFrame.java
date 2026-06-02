package its.ui.gui;

import its.domain.issue.Issue;
import its.domain.issue.IssueStatus;
import its.domain.issue.Priority;
import its.domain.project.Project;
import its.domain.user.Role;
import its.domain.user.User;
import its.service.ApplicationServices;
import its.service.AssigneeRecommendation;
import its.service.DemoDataSeeder;
import its.service.ServiceFactory;
import its.ui.gui.panel.BasePanel;
import its.ui.gui.panel.CreateIssuePanel;
import its.ui.gui.panel.IssueDetailPanel;
import its.ui.gui.panel.IssuesPanel;
import its.ui.gui.panel.LoginPanel;
import its.ui.gui.panel.NavigationPanel;
import its.ui.gui.panel.ProjectsPanel;
import its.ui.gui.panel.StatisticsPanel;
import its.ui.gui.panel.TitleBarPanel;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.nio.file.Path;
import java.util.List;
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

        ProjectsPanel projectsPanel = new ProjectsPanel();
        projectsPanel.setProjectService(services.getProjectService());

        IssuesPanel issuesPanel = new IssuesPanel();
        issuesPanel.setServices(services.getIssueService(), services.getProjectService());

        CreateIssuePanel createIssuePanel = new CreateIssuePanel();
        createIssuePanel.setProjectService(services.getProjectService());

        IssueDetailPanel issueDetailPanel = new IssueDetailPanel();
        issueDetailPanel.setIssueService(services.getIssueService());
        issueDetailPanel.setUserService(services.getUserService());

        StatisticsPanel statisticsPanel = new StatisticsPanel();
        statisticsPanel.setStatisticsService(services.getStatisticsService());

        issuesPanel.setIssueActionListener(new IssuesPanel.IssueActionListener() {
            @Override
            public void onCreateIssueRequested() {
                createIssuePanel.clear();
                contentCardLayout.show(contentAreaPanel, CREATE_ISSUE_CARD);
                navigationPanel.selectButton(NavigationPanel.NavigationListener.CREATE_ISSUES);
            }

            @Override
            public void onIssueSelected(int issueId) {
                issueDetailPanel.setAssignable(currentUser != null
                        && (currentUser.hasRole(Role.PL) || currentUser.hasRole(Role.ADMIN)));
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
                issueDetailPanel.setAssignable(currentUser != null
                        && (currentUser.hasRole(Role.PL) || currentUser.hasRole(Role.ADMIN)));
                issueDetailPanel.loadIssue(issueId);
                contentCardLayout.show(contentAreaPanel, ISSUE_DETAIL_CARD);
                navigationPanel.selectButton(NavigationPanel.NavigationListener.ISSUES);
            }

            @Override
            public void onSaveRequested(String project, String title, String description, Priority priority) {
                handleCreateIssue(project, title, description, priority, issuesPanel, createIssuePanel, statisticsPanel);
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
                JOptionPane.showMessageDialog(
                        MainFrame.this,
                        "현재 버전에서는 이슈 수정 기능은 지원하지 않습니다.",
                        "Not Supported",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

            @Override
            public void onIssueDeleteRequested(int issueId) {
                JOptionPane.showMessageDialog(
                        MainFrame.this,
                        "현재 버전에서는 이슈 삭제 기능은 지원하지 않습니다.",
                        "Not Supported",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

            @Override
            public void onStatusChangeRequested(int issueId, IssueStatus status, Long assigneeId, String comment) {
                handleStatusChange(issueId, status, assigneeId, comment, issuesPanel, issueDetailPanel, statisticsPanel);
            }
        });

        contentAreaPanel.add(projectsPanel, PROJECTS_CARD);
        contentAreaPanel.add(issuesPanel, ISSUES_CARD);
        contentAreaPanel.add(createIssuePanel, CREATE_ISSUE_CARD);
        contentAreaPanel.add(issueDetailPanel, ISSUE_DETAIL_CARD);
        contentAreaPanel.add(statisticsPanel, STATISTICS_CARD);

        panel.add(contentAreaPanel, BorderLayout.CENTER);

        return panel;
    }

    private void handleCreateIssue(
            String projectName,
            String title,
            String description,
            Priority priority,
            IssuesPanel issuesPanel,
            CreateIssuePanel createIssuePanel,
            StatisticsPanel statisticsPanel
    ) {
        try {
            if (currentUser == null) {
                throw new IllegalStateException("current user is not set");
            }

            validateIssueForm(projectName, title, description);

            Project project = findProjectByName(projectName);

            Issue created = services.getIssueService().createIssue(
                    project.getId(),
                    title.trim(),
                    description.trim(),
                    currentUser.getId(),
                    priority
            );

            JOptionPane.showMessageDialog(
                    this,
                    "이슈가 등록되었습니다. Issue #" + created.getId(),
                    "Issue Created",
                    JOptionPane.INFORMATION_MESSAGE
            );

            createIssuePanel.clear();
            issuesPanel.refreshIssues();
            statisticsPanel.refreshStatistics();

            contentCardLayout.show(contentAreaPanel, ISSUES_CARD);
            navigationPanel.selectButton(NavigationPanel.NavigationListener.ISSUES);
        } catch (Exception e) {
            showError("Issue Create Error", e);
        }
    }

    private void handleStatusChange(
            int issueId,
            IssueStatus status,
            Long assigneeId,
            String comment,
            IssuesPanel issuesPanel,
            IssueDetailPanel issueDetailPanel,
            StatisticsPanel statisticsPanel
    ) {
        try {
            if (currentUser == null) {
                throw new IllegalStateException("current user is not set");
            }

            if (status == null) {
                throw new IllegalArgumentException("status must be selected");
            }

            if (status == IssueStatus.ASSIGNED && assigneeId == null) {
                throw new IllegalArgumentException("assigneeId must be selected");
            }

            Long id = (long) issueId;
            Issue updatedIssue;

            switch (status) {
                case ASSIGNED -> updatedIssue = services.getIssueService().assignIssue(
                        id,
                        currentUser.getId(),
                        assigneeId,
                        comment
                );
                case FIXED -> updatedIssue = services.getIssueService().markFixed(
                        id,
                        currentUser.getId(),
                        comment
                );
                case RESOLVED -> updatedIssue = services.getIssueService().resolveIssue(
                        id,
                        currentUser.getId(),
                        comment
                );
                case CLOSED -> updatedIssue = services.getIssueService().closeIssue(
                        id,
                        currentUser.getId(),
                        comment
                );
                case REOPENED -> updatedIssue = services.getIssueService().reopenIssue(
                        id,
                        currentUser.getId(),
                        comment
                );
                case NEW -> throw new IllegalArgumentException("cannot change issue status back to NEW");
                default -> throw new IllegalArgumentException("unsupported status: " + status);
            }

            JOptionPane.showMessageDialog(
                    this,
                    "상태가 변경되었습니다: " + updatedIssue.getStatus(),
                    "Status Updated",
                    JOptionPane.INFORMATION_MESSAGE
            );

            issueDetailPanel.setAssignable(currentUser != null
                    && (currentUser.hasRole(Role.PL) || currentUser.hasRole(Role.ADMIN)));
            issueDetailPanel.loadIssue(issueId);
            issuesPanel.refreshIssues();
            statisticsPanel.refreshStatistics();
        } catch (Exception e) {
            showError("Status Change Error", e);
        }
    }

    private Long selectAssigneeId(Long issueId) {
        List<AssigneeRecommendation> recommendations =
                services.getRecommendationService().recommendAssignees(issueId, 1);

        if (!recommendations.isEmpty()) {
            return recommendations.get(0).getAssignee().getId();
        }

        return services.getUserService().getAllUsers().stream()
                .filter(user -> user.hasRole(Role.DEV))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("no DEV user exists"))
                .getId();
    }

    private void validateIssueForm(String projectName, String title, String description) {
        if (projectName == null || projectName.isBlank()) {
            throw new IllegalArgumentException("project must be selected");
        }

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description must not be blank");
        }
    }

    private Project findProjectByName(String projectName) {
        return services.getProjectService().getAllProjects().stream()
                .filter(project -> project.getName().equals(projectName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("project not found: " + projectName));
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

    private void showError(String title, Exception e) {
        JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                title,
                JOptionPane.ERROR_MESSAGE
        );
    }
}
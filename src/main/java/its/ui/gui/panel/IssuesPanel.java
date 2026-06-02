package its.ui.gui.panel;

import its.domain.issue.Issue;
import its.domain.issue.IssueStatus;
import its.domain.issue.Priority;
import its.domain.project.Project;
import its.service.IssueSearchCriteria;
import its.service.IssueService;
import its.service.ProjectService;
import its.ui.gui.common.PlaceholderTextField;
import its.ui.gui.common.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static its.ui.gui.common.UIConstants.ButtonType.PRIMARY;

public class IssuesPanel extends BasePanel {
    private JComboBox<String> projectComboBox;
    private JComboBox<String> statusComboBox;
    private PlaceholderTextField reporterField;
    private PlaceholderTextField assigneeField;
    private JComboBox<String> priorityComboBox;
    private PlaceholderTextField titleSearchField;

    private JButton searchButton;
    private JButton resetButton;
    private JButton createIssueButton;

    private JTable issueTable;
    private DefaultTableModel tableModel;

    private IssueActionListener listener;

    private IssueService issueService;
    private ProjectService projectService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final String[] COLUMN_NAMES = {
            "ID", "제목", "상태", "우선순위", "리포터", "담당자", "등록일"
    };

    public void setServices(IssueService issueService, ProjectService projectService) {
        this.issueService = issueService;
        this.projectService = projectService;
        refreshProjectComboBox();
        refreshIssues();
    }

    @Override
    protected void setupLayout() {
        setLayout(new GridBagLayout());
    }

    @Override
    protected void initComponents() {
        projectComboBox = new JComboBox<>();
        statusComboBox = new JComboBox<>(new String[]{
                "전체", "NEW", "ASSIGNED", "FIXED", "RESOLVED", "CLOSED", "REOPENED"
        });
        reporterField = new PlaceholderTextField("tester1");
        assigneeField = new PlaceholderTextField("dev1");
        priorityComboBox = new JComboBox<>(new String[]{
                "전체", "BLOCKER", "CRITICAL", "MAJOR", "MINOR", "TRIVIAL"
        });

        GridBagConstraints gbc = new GridBagConstraints();

        JPanel headerPanel = createHeaderPanel();
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        add(headerPanel, gbc);

        JPanel filterPanel = createFilterPanel();
        gbc.gridy = 1;
        add(filterPanel, gbc);

        JPanel searchPanel = createSearchPanel();
        gbc.gridy = 2;
        add(searchPanel, gbc);

        JScrollPane tableScrollPane = createTablePanel();
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(tableScrollPane, gbc);

        refreshProjectComboBox();
        refreshIssues();
    }

    @Override
    protected void setupListeners() {
        createIssueButton.addActionListener(e -> {
            if (listener != null) {
                listener.onCreateIssueRequested();
            }
        });

        searchButton.addActionListener(e -> refreshIssues());

        resetButton.addActionListener(e -> {
            clear();
            refreshIssues();
        });

        issueTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = issueTable.getSelectedRow();

                if (row != -1) {
                    int issueId = ((Number) tableModel.getValueAt(row, 0)).intValue();

                    if (listener != null) {
                        listener.onIssueSelected(issueId);
                    }

                    SwingUtilities.invokeLater(() -> issueTable.clearSelection());
                }
            }
        });
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("이슈 목록");
        title.setFont(UIConstants.TITLE_FONT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        panel.add(title, BorderLayout.WEST);

        createIssueButton = createStyledButton("+ 이슈 등록", PRIMARY);
        panel.add(createIssueButton, BorderLayout.EAST);

        return panel;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 5));

        panel.add(createFilterCell("프로젝트", projectComboBox));
        panel.add(createFilterCell("상태", statusComboBox));
        panel.add(createFilterCell("리포터", reporterField));
        panel.add(createFilterCell("담당자", assigneeField));
        panel.add(createFilterCell("우선순위", priorityComboBox));

        return panel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        titleSearchField = new PlaceholderTextField("이슈 제목 검색", 100);
        panel.add(titleSearchField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        searchButton = createStyledButton("검색", PRIMARY);
        buttonPanel.add(searchButton, BorderLayout.WEST);

        resetButton = createStyledButton("초기화");
        buttonPanel.add(resetButton, BorderLayout.EAST);

        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    private JScrollPane createTablePanel() {
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        issueTable = new JTable(tableModel);
        setupTableStyle();

        JScrollPane scrollPane = new JScrollPane(issueTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        return scrollPane;
    }

    private JPanel createFilterCell(String text, JComponent component) {
        JPanel cell = new JPanel();
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));

        JLabel label = new JLabel(text);
        label.setFont(UIConstants.LABEL_FONT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 6, 2, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        component.setAlignmentX(Component.LEFT_ALIGNMENT);

        cell.add(label);
        cell.add(component);

        return cell;
    }

    private void setupTableStyle() {
        JTableHeader header = issueTable.getTableHeader();

        header.setFont(UIConstants.HEADER_FONT);
        header.setBackground(new Color(200, 200, 200));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);

        issueTable.setRowHeight(30);
        issueTable.setFont(UIConstants.LABEL_FONT);
        issueTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        issueTable.setGridColor(new Color(230, 230, 230));
        issueTable.setSelectionBackground(new Color(180, 180, 180));

        issueTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        issueTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        issueTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        issueTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        issueTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        issueTable.getColumnModel().getColumn(5).setPreferredWidth(120);
        issueTable.getColumnModel().getColumn(6).setPreferredWidth(80);

        issueTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column
            ) {
                Component c = super.getTableCellRendererComponent(
                        table,
                        value,
                        isSelected,
                        hasFocus,
                        row,
                        column
                );

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 248, 248));
                }

                if (c instanceof JLabel) {
                    ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                }

                return c;
            }
        });
    }

    private void refreshProjectComboBox() {
        if (projectComboBox == null) {
            return;
        }

        Object selected = projectComboBox.getSelectedItem();

        projectComboBox.removeAllItems();
        projectComboBox.addItem("전체");

        if (projectService != null) {
            for (Project project : projectService.getAllProjects()) {
                projectComboBox.addItem(project.getName());
            }
        }

        if (selected != null) {
            projectComboBox.setSelectedItem(selected);
        }
    }

    public void refreshIssues() {
        if (tableModel == null) {
            return;
        }

        tableModel.setRowCount(0);

        if (issueService == null) {
            return;
        }

        IssueSearchCriteria criteria = buildCriteria();
        List<Issue> issues = issueService.searchIssues(criteria);

        for (Issue issue : issues) {
            if (!matchesReporter(issue)) {
                continue;
            }

            if (!matchesAssignee(issue)) {
                continue;
            }

            addIssue(issue);
        }
    }

    private IssueSearchCriteria buildCriteria() {
        IssueSearchCriteria criteria = new IssueSearchCriteria();

        String selectedProject = (String) projectComboBox.getSelectedItem();
        if (selectedProject != null && !"전체".equals(selectedProject) && projectService != null) {
            projectService.getAllProjects().stream()
                    .filter(project -> project.getName().equals(selectedProject))
                    .findFirst()
                    .ifPresent(project -> criteria.setProjectId(project.getId()));
        }

        String selectedStatus = (String) statusComboBox.getSelectedItem();
        if (selectedStatus != null && !"전체".equals(selectedStatus)) {
            criteria.setStatus(IssueStatus.valueOf(selectedStatus));
        }

        String selectedPriority = (String) priorityComboBox.getSelectedItem();
        if (selectedPriority != null && !"전체".equals(selectedPriority)) {
            criteria.setPriority(Priority.valueOf(selectedPriority));
        }

        String keyword = titleSearchField.getText();
        if (keyword != null && !keyword.isBlank()) {
            criteria.setKeyword(keyword);
        }

        return criteria;
    }

    private boolean matchesReporter(Issue issue) {
        String filter = reporterField.getText();

        if (filter == null || filter.isBlank()) {
            return true;
        }

        return issue.getReporter().getUsername().toLowerCase().contains(filter.toLowerCase());
    }

    private boolean matchesAssignee(Issue issue) {
        String filter = assigneeField.getText();

        if (filter == null || filter.isBlank()) {
            return true;
        }

        if (issue.getAssignee() == null) {
            return false;
        }

        return issue.getAssignee().getUsername().toLowerCase().contains(filter.toLowerCase());
    }

    private void addIssue(Issue issue) {
        String assignee = issue.getAssignee() == null ? "-" : issue.getAssignee().getUsername();
        String reportedDate = issue.getReportedDate().format(DATE_FORMATTER);

        tableModel.addRow(new Object[]{
                issue.getId().intValue(),
                issue.getTitle(),
                issue.getStatus().name(),
                issue.getPriority().name(),
                issue.getReporter().getUsername(),
                assignee,
                reportedDate
        });
    }

    public void setIssueActionListener(IssueActionListener listener) {
        this.listener = listener;
    }

    public interface IssueActionListener {
        void onCreateIssueRequested();

        void onIssueSelected(int issueId);
    }

    @Override
    public void clear() {
        if (projectComboBox.getItemCount() > 0) {
            projectComboBox.setSelectedIndex(0);
        }

        statusComboBox.setSelectedIndex(0);
        priorityComboBox.setSelectedIndex(0);
        reporterField.setText("");
        assigneeField.setText("");
        titleSearchField.setText("");
        issueTable.clearSelection();
    }

    @Override
    public void onActivate() {
        refreshProjectComboBox();
        refreshIssues();
        issueTable.clearSelection();
    }
}
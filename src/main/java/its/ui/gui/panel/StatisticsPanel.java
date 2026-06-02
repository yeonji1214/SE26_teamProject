package its.ui.gui.panel;

import its.domain.issue.IssueStatus;
import its.domain.issue.Priority;
import its.service.StatisticsService;
import its.ui.gui.common.UIConstants;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

public class StatisticsPanel extends BasePanel {
    private StatisticsService statisticsService;

    private JLabel totalIssuesLabel;
    private JLabel openIssuesLabel;
    private JLabel resolvedIssuesLabel;
    private JLabel highPriorityIssuesLabel;

    private DefaultTableModel statusTableModel;
    private DefaultTableModel priorityTableModel;
    private DefaultTableModel dayTableModel;
    private DefaultTableModel monthTableModel;
    private DefaultTableModel assigneeTableModel;

    public void setStatisticsService(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
        refreshStatistics();
    }

    @Override
    protected void initComponents() {
        setLayout(new BorderLayout());

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        contentPanel.add(createSummaryPanel());
        contentPanel.add(Box.createVerticalStrut(16));
        contentPanel.add(createTablesPanel());

        add(contentPanel, BorderLayout.CENTER);

        refreshStatistics();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("통계");
        title.setFont(UIConstants.TITLE_FONT);

        JLabel subtitle = new JLabel("백엔드 StatisticsService에서 계산한 이슈 통계입니다.");
        subtitle.setFont(UIConstants.LABEL_FONT);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(6));
        textPanel.add(subtitle);

        panel.add(textPanel, BorderLayout.WEST);

        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 12, 12));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));

        totalIssuesLabel = new JLabel("0", SwingConstants.CENTER);
        openIssuesLabel = new JLabel("0", SwingConstants.CENTER);
        resolvedIssuesLabel = new JLabel("0", SwingConstants.CENTER);
        highPriorityIssuesLabel = new JLabel("0", SwingConstants.CENTER);

        panel.add(createSummaryCard("전체 이슈", totalIssuesLabel, "등록된 전체 이슈 수"));
        panel.add(createSummaryCard("진행 중", openIssuesLabel, "NEW / ASSIGNED / REOPENED"));
        panel.add(createSummaryCard("해결/종료", resolvedIssuesLabel, "RESOLVED / CLOSED"));
        panel.add(createSummaryCard("고우선순위", highPriorityIssuesLabel, "BLOCKER / CRITICAL"));

        return panel;
    }

    private JPanel createSummaryCard(String title, JLabel valueLabel, String description) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UIConstants.CARD_COLOR);
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(UIConstants.SUBTITLE_FONT);

        valueLabel.setFont(UIConstants.TITLE_FONT);

        JLabel descriptionLabel = new JLabel(description, SwingConstants.CENTER);
        descriptionLabel.setFont(UIConstants.LABEL_FONT);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(descriptionLabel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createTablesPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 12, 12));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 30));

        statusTableModel = new DefaultTableModel(new String[]{"상태", "이슈 수"}, 0);
        priorityTableModel = new DefaultTableModel(new String[]{"우선순위", "이슈 수"}, 0);
        dayTableModel = new DefaultTableModel(new String[]{"일자", "이슈 수"}, 0);
        monthTableModel = new DefaultTableModel(new String[]{"월", "이슈 수"}, 0);
        assigneeTableModel = new DefaultTableModel(new String[]{"담당자", "이슈 수"}, 0);

        panel.add(createTableCard("상태별", statusTableModel));
        panel.add(createTableCard("우선순위별", priorityTableModel));
        panel.add(createTableCard("일별", dayTableModel));
        panel.add(createTableCard("월별", monthTableModel));
        panel.add(createTableCard("담당자별", assigneeTableModel));

        return panel;
    }

    private JPanel createTableCard(String title, DefaultTableModel model) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UIConstants.CARD_COLOR);
        card.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIConstants.SUBTITLE_FONT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JTable table = new JTable(model);
        table.setEnabled(false);
        table.setRowHeight(24);
        table.getTableHeader().setFont(UIConstants.HEADER_FONT);
        table.setFont(UIConstants.LABEL_FONT);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(new JScrollPane(table), BorderLayout.CENTER);

        return card;
    }

    public void refreshStatistics() {
        if (statisticsService == null || totalIssuesLabel == null) {
            return;
        }

        Map<IssueStatus, Long> byStatus = statisticsService.countIssuesByStatus();
        Map<Priority, Long> byPriority = statisticsService.countIssuesByPriority();

        long totalIssues = statisticsService.countTotalIssues();

        long openIssues = byStatus.getOrDefault(IssueStatus.NEW, 0L)
                + byStatus.getOrDefault(IssueStatus.ASSIGNED, 0L)
                + byStatus.getOrDefault(IssueStatus.REOPENED, 0L);

        long resolvedIssues = byStatus.getOrDefault(IssueStatus.RESOLVED, 0L)
                + byStatus.getOrDefault(IssueStatus.CLOSED, 0L);

        long highPriorityIssues = byPriority.getOrDefault(Priority.BLOCKER, 0L)
                + byPriority.getOrDefault(Priority.CRITICAL, 0L);

        totalIssuesLabel.setText(String.valueOf(totalIssues));
        openIssuesLabel.setText(String.valueOf(openIssues));
        resolvedIssuesLabel.setText(String.valueOf(resolvedIssues));
        highPriorityIssuesLabel.setText(String.valueOf(highPriorityIssues));

        fillGenericTable(statusTableModel, byStatus);
        fillGenericTable(priorityTableModel, byPriority);
        fillLocalDateTable(dayTableModel, statisticsService.countIssuesByDay());
        fillYearMonthTable(monthTableModel, statisticsService.countIssuesByMonth());
        fillGenericTable(assigneeTableModel, statisticsService.countIssuesByAssignee());
    }

    private void fillGenericTable(DefaultTableModel model, Map<?, Long> data) {
        model.setRowCount(0);

        for (Map.Entry<?, Long> entry : data.entrySet()) {
            model.addRow(new Object[]{
                    entry.getKey().toString(),
                    entry.getValue()
            });
        }
    }

    private void fillLocalDateTable(DefaultTableModel model, Map<LocalDate, Long> data) {
        model.setRowCount(0);

        for (Map.Entry<LocalDate, Long> entry : data.entrySet()) {
            model.addRow(new Object[]{
                    entry.getKey().toString(),
                    entry.getValue()
            });
        }
    }

    private void fillYearMonthTable(DefaultTableModel model, Map<YearMonth, Long> data) {
        model.setRowCount(0);

        for (Map.Entry<YearMonth, Long> entry : data.entrySet()) {
            model.addRow(new Object[]{
                    entry.getKey().toString(),
                    entry.getValue()
            });
        }
    }

    @Override
    public void onActivate() {
        refreshStatistics();
    }
}
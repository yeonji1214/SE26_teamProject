package its.ui.gui.panel;

import its.ui.gui.common.PlaceholderTextField;
import its.ui.gui.common.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

import static its.ui.gui.common.UIConstants.ButtonType.PRIMARY;

public class IssuesPanel extends BasePanel {

    // 필터 컴포넌트
    private JComboBox<String> projectComboBox;
    private JComboBox<String> statusComboBox;
    private PlaceholderTextField reporterField;
    private PlaceholderTextField assigneeField;
    private JComboBox<String> priorityComboBox;
    private PlaceholderTextField titleSearchField;

    // 버튼
    private JButton searchButton;
    private JButton resetButton;
    private JButton createIssueButton;

    // 테이블
    private JTable issueTable;
    private DefaultTableModel tableModel;

    // 리스너
    private IssueActionListener listener;

    private static final String[] COLUMN_NAMES = {
            "ID", "제목", "상태", "우선순위", "리포터", "담당자", "등록일"
    };

    @Override
    protected void setupLayout() {
        setLayout(new GridBagLayout());
    }

    @Override
    protected void initComponents() {
        projectComboBox = new JComboBox<>();
        statusComboBox = new JComboBox<>(new String[]{"전체", "NEW", "ASSIGNED", "RESOLVED", "CLOSED"});
        reporterField = new PlaceholderTextField("tester1");
        assigneeField = new PlaceholderTextField("dev1");
        priorityComboBox = new JComboBox<>(new String[]{"전체", "BLOCKER", "CRITICAL", "MAJOR", "MINOR", "TRIVIAL"});

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

        loadDummyData();
    }

    @Override
    protected void setupListeners() {
        createIssueButton.addActionListener(e -> {
            if (listener != null) {listener.onCreateIssueRequested();}
        });

        issueTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = issueTable.getSelectedRow();
                if (row != -1) {
                    int issueId = (int) tableModel.getValueAt(row, 0);
                    if (listener != null) {listener.onIssueSelected(issueId);}
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
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
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

    private void loadDummyData() {
        // TODO: 나중에 Service 호출로 교체

        addIssue(1, "로그인 후 이슈 목록이 보이지 않음", "ASSIGNED", "MAJOR", "tester1", "dev1", "2026-05-22");
        addIssue(2, "이슈 등록 시 priority 기본값 확인 필요", "NEW", "MAJOR", "tester1", "-", "2026-05-22");
        addIssue(3, "통계 페이지 월별 이슈 수 표시 오류", "RESOLVED", "MINOR", "tester1", "dev2", "2026-05-21");
    }

    private void addIssue(int ID, String title, String status, String priority, String reporter, String assignee, String date) {
        // TODO: date는 LocalDateTime으로 받기
        tableModel.addRow(new Object[] {ID, title, status, priority, reporter, assignee, date});
    }

    public void setIssueActionListener(IssueActionListener listener){
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
        issueTable.clearSelection();
        // TODO: 이슈 목록 새로고침(서비스 연결)
    }
}

package its.ui.gui.panel;

import its.domain.project.Project;
import its.service.ProjectService;
import its.ui.gui.common.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class ProjectsPanel extends BasePanel {
    private JTable projectTable;
    private DefaultTableModel tableModel;
    private ProjectService projectService;

    private static final String[] COLUMN_NAMES = {
            "Project Name",
            "Description",
            "Created",
            "Status"
    };

    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
        refreshProjects();
    }

    @Override
    protected void initComponents() {
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);

        JScrollPane tableScrollPane = createTablePanel();
        add(tableScrollPane, BorderLayout.CENTER);

        add(Box.createHorizontalStrut(30), BorderLayout.WEST);
        add(Box.createHorizontalStrut(30), BorderLayout.EAST);
        add(Box.createVerticalStrut(50), BorderLayout.SOUTH);

        refreshProjects();
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel title = new JLabel("Projects");
        title.setFont(UIConstants.TITLE_FONT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("백엔드 ProjectService에서 불러온 프로젝트 목록입니다.");
        subtitle.setFont(UIConstants.LABEL_FONT);
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        panel.add(title);
        panel.add(subtitle);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        return panel;
    }

    private JScrollPane createTablePanel() {
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        projectTable = new JTable(tableModel);
        setupTableStyle();

        JScrollPane scrollPane = new JScrollPane(projectTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        return scrollPane;
    }

    private void setupTableStyle() {
        JTableHeader header = projectTable.getTableHeader();

        header.setFont(UIConstants.HEADER_FONT);
        header.setBackground(new Color(200, 200, 200));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);

        projectTable.setRowHeight(30);
        projectTable.setFont(UIConstants.LABEL_FONT);
        projectTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectTable.setGridColor(new Color(230, 230, 230));
        projectTable.setSelectionBackground(new Color(180, 180, 180));

        projectTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        projectTable.getColumnModel().getColumn(1).setPreferredWidth(240);
        projectTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        projectTable.getColumnModel().getColumn(3).setPreferredWidth(80);

        projectTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

    public void refreshProjects() {
        if (tableModel == null) {
            return;
        }

        tableModel.setRowCount(0);

        if (projectService == null) {
            return;
        }

        for (Project project : projectService.getAllProjects()) {
            tableModel.addRow(new Object[]{
                    project.getName(),
                    project.getDescription(),
                    "-",
                    "Active"
            });
        }
    }

    @Override
    public void onActivate() {
        refreshProjects();
        projectTable.clearSelection();

        System.out.println("[ProjectsPanel] ProjectsPanel activated");
    }

    @Override
    public void clear() {
        projectTable.clearSelection();
    }
}
package its.ui.gui.panel;

import its.domain.project.Project;
import its.service.ProjectService;
import its.ui.gui.common.PlaceholderTextField;
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

    private boolean isAdmin = false;
    private JPanel formPanel;
    private PlaceholderTextField nameField;
    private PlaceholderTextField descriptionField;
    private JButton addButton;

    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
        refreshProjects();
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
        updateFormPState();
    }

    @Override
    protected void initComponents() {
        formPanel = new JPanel();
        nameField = new PlaceholderTextField("프로젝트 이름");
        descriptionField = new PlaceholderTextField("간단한 프로젝트 설명 (선택사항)");
        addButton = createStyledButton("추가", UIConstants.ButtonType.PRIMARY);

        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);

        JScrollPane tableScrollPane = createTablePanel();
        add(tableScrollPane, BorderLayout.CENTER);

        formPanel = createProjectFormPanel();
        formPanel.setPreferredSize(new Dimension(700, formPanel.getPreferredSize().height));

        JPanel southWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        southWrapper.setOpaque(false);
        southWrapper.add(formPanel);

        southWrapper.setBorder(BorderFactory.createEmptyBorder(15, 30, 20, 0));

        add(southWrapper, BorderLayout.SOUTH);

        add(Box.createHorizontalStrut(30), BorderLayout.WEST);
        add(Box.createHorizontalStrut(30), BorderLayout.EAST);

        refreshProjects();
    }

    @Override
    protected void setupListeners() {
        addButton.addActionListener(e -> handleCreateProject());
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
        panel.setBorder(BorderFactory.createEmptyBorder(15, 30, 20, 30));

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

    private JPanel createProjectFormPanel() {
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);

        JLabel title = new JLabel("프로젝트 추가");
        title.setFont(UIConstants.SUBTITLE_FONT);
        gbc.gridy = 0;
        formPanel.add(title, gbc);

        JLabel nameLabel = new JLabel("이름");
        nameLabel.setFont(UIConstants.LABEL_FONT);
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 0, 5, 10);
        formPanel.add(nameLabel, gbc);

        JLabel descriptionLabel = new JLabel("설명");
        descriptionLabel.setFont(UIConstants.LABEL_FONT);
        gbc.gridx = 1;
        formPanel.add(descriptionLabel, gbc);

        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.CARD_COLOR),
                BorderFactory.createEmptyBorder(5, 5,5,5)
        ));
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        gbc.insets = new Insets(0, 0, 5, 10);
        formPanel.add(nameField, gbc);

        descriptionField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.CARD_COLOR),
                BorderFactory.createEmptyBorder(5,5,5,5)
        ));
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.insets = new Insets(0, 0, 5, 0);
        formPanel.add(descriptionField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 5, 3);
        formPanel.add(addButton, gbc);

        return formPanel;
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

    private void handleCreateProject() {
        if (!isAdmin) {
            JOptionPane.showMessageDialog(this, "Only admin can create projects", "권한 없음", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = nameField.getText();
        String description = descriptionField.getText();

        if (name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Project name must be entered", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            projectService.createProject(name.trim(), description.trim());
            nameField.setText("");
            descriptionField.setText("");
            refreshProjects();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Project create Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateFormPState() {
        formPanel.setVisible(isAdmin);
        revalidate();
        repaint();
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
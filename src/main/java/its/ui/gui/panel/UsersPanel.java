package its.ui.gui.panel;

import its.domain.user.Role;
import its.domain.user.User;
import its.service.UserService;
import its.ui.gui.common.PlaceholderPasswordField;
import its.ui.gui.common.PlaceholderTextField;
import its.ui.gui.common.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class UsersPanel extends BasePanel {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private UserService userService;

    private static final String[] COLUMN_NAMES = {
            "User Name",
            "Role"
    };

    private boolean isAdmin = false;
    private JPanel formPanel;
    private PlaceholderTextField nameField;
    private PlaceholderPasswordField passwordField;
    private JComboBox<Role> roleComboBox;
    private JButton addButton;

    public void setUserService(UserService userService) {
        this.userService = userService;
        refreshUsers();
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
        updateFormPState();
    }

    @Override
    protected void initComponents() {
        formPanel = new JPanel();
        nameField = new PlaceholderTextField("id");
        passwordField = new PlaceholderPasswordField("pw");
        roleComboBox = new JComboBox<>(Role.values());
        addButton = createStyledButton("추가", UIConstants.ButtonType.PRIMARY);

        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);

        JScrollPane tableScrollPane = createTablePanel();
        add(tableScrollPane, BorderLayout.CENTER);

        formPanel = createUserFormPanel();

        JPanel southWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        southWrapper.setOpaque(false);
        southWrapper.add(formPanel);

        southWrapper.setBorder(BorderFactory.createEmptyBorder(15, 30, 20, 0));

        add(southWrapper, BorderLayout.SOUTH);

        add(Box.createHorizontalStrut(30), BorderLayout.WEST);
        add(Box.createHorizontalStrut(30), BorderLayout.EAST);

        refreshUsers();
    }

    @Override
    protected void setupListeners() {
        addButton.addActionListener(e -> handleCreateUser());
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel title = new JLabel("Users");
        title.setFont(UIConstants.TITLE_FONT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("유저 목록입니다.");
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

        userTable = new JTable(tableModel);
        setupTableStyle();

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        return scrollPane;
    }

    private JPanel createUserFormPanel() {
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);

        JLabel title = new JLabel("유저 추가");
        title.setFont(UIConstants.SUBTITLE_FONT);
        gbc.gridy = 0;
        formPanel.add(title, gbc);

        JLabel nameLabel = new JLabel("ID");
        nameLabel.setFont(UIConstants.LABEL_FONT);
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 0, 5, 10);
        formPanel.add(nameLabel, gbc);

        JLabel passwordLabel = new JLabel("비밀번호");
        passwordLabel.setFont(UIConstants.LABEL_FONT);
        gbc.gridx = 1;
        formPanel.add(passwordLabel, gbc);

        JLabel roleLabel = new JLabel("역할");
        roleLabel.setFont(UIConstants.LABEL_FONT);
        gbc.insets = new Insets(5, 3, 5, 0);
        gbc.gridx = 2;
        formPanel.add(roleLabel, gbc);

        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.CARD_COLOR),
                BorderFactory.createEmptyBorder(5, 5,5,5)
        ));
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 5, 10);
        formPanel.add(nameField, gbc);

        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.CARD_COLOR),
                BorderFactory.createEmptyBorder(5,5,5,5)
        ));
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        gbc.gridx = 2;
        gbc.insets = new Insets(0, 0, 5, 0);
        formPanel.add(roleComboBox, gbc);

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
        JTableHeader header = userTable.getTableHeader();

        header.setFont(UIConstants.HEADER_FONT);
        header.setBackground(new Color(200, 200, 200));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);

        userTable.setRowHeight(30);
        userTable.setFont(UIConstants.LABEL_FONT);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setGridColor(new Color(230, 230, 230));
        userTable.setSelectionBackground(new Color(180, 180, 180));

        userTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        userTable.getColumnModel().getColumn(1).setPreferredWidth(240);

        userTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

    public void refreshUsers() {
        if (tableModel == null) {
            return;
        }

        tableModel.setRowCount(0);

        if (userService == null) {
            return;
        }

        for (User user : userService.getAllUsers()) {
            tableModel.addRow(new Object[]{
                    user.getUsername(),
                    user.getRole()
            });
        }
    }

    private void handleCreateUser() {
        commitComposition();

        if (!isAdmin) {
            JOptionPane.showMessageDialog(this, "Only admin can create users", "권한 없음", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = nameField.getText();
        String password = new String(passwordField.getPassword());
        Role role = (Role) roleComboBox.getSelectedItem();

        if (name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "User name must be entered", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password must be entered", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            userService.createUser(name.trim(), password.trim(), role);
            nameField.setText("");
            passwordField.setText("");
            refreshUsers();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "User create Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateFormPState() {
        formPanel.setVisible(isAdmin);
        revalidate();
        repaint();
    }

    @Override
    public void onActivate() {
        refreshUsers();
        userTable.clearSelection();
    }

    @Override
    public void clear() {
        userTable.clearSelection();
    }
}

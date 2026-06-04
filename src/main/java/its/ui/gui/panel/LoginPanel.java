package its.ui.gui.panel;

import its.domain.user.User;
import its.service.UserService;
import its.ui.gui.common.PlaceholderPasswordField;
import its.ui.gui.common.PlaceholderTextField;
import its.ui.gui.common.UIConstants;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends BasePanel {
    private PlaceholderTextField usernameField;
    private PlaceholderPasswordField passwordField;
    private JButton loginButton;
    private LoginListener loginListener;
    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void setupLayout() {
        setLayout(new GridBagLayout());
    }

    @Override
    protected void initComponents() {
        JPanel formPanel = createFormPanel();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(formPanel, gbc);

        SwingUtilities.invokeLater(() -> this.requestFocusInWindow());
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(200, 200, 200));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        usernameField = new PlaceholderTextField("id", 15);
        usernameField.setPreferredSize(UIConstants.INPUT_FIELD_SIZE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(usernameField, gbc);

        passwordField = new PlaceholderPasswordField("pw", 15);
        passwordField.setPreferredSize(UIConstants.INPUT_FIELD_SIZE);
        gbc.gridy = 1;
        panel.add(passwordField, gbc);

        loginButton = createStyledButton("login", UIConstants.ButtonType.PRIMARY);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(loginButton, gbc);

        setTabOrder(panel, usernameField, passwordField, loginButton);

        return panel;
    }

    @Override
    protected void setupListeners() {
        loginButton.addActionListener(e -> handleLogin());
        passwordField.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter both username and password",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (userService == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "UserService is not connected.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try {
            User user = userService.login(username, password);

            if (loginListener != null) {
                loginListener.onLoginSuccess(user);
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    @Override
    public void clear() {
        usernameField.setText("");
        passwordField.setText("");
    }

    public void setLoginListener(LoginListener listener) {
        this.loginListener = listener;
    }

    public interface LoginListener {
        void onLoginSuccess(User user);
    }
}

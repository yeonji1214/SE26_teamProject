package its.ui.gui.panel;

import javax.swing.*;
import java.awt.*;
import its.ui.gui.common.UIConstants;

public class TitleBarPanel extends JPanel {
    private JLabel usernameLabel;

    public TitleBarPanel(String currentUser) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(1200, 60));

        add(createLeftPanel(), BorderLayout.WEST);
        add(createRightPanel(currentUser), BorderLayout.EAST);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel title = new JLabel("Issue Tracker");
        title.setFont(UIConstants.TITLE_FONT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Java 기반 이슈 관리 시스템 Swing UI");
        subtitle.setFont(UIConstants.LABEL_FONT);
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(subtitle);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 100));

        return panel;
    }

    private JPanel createRightPanel(String user) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(0, 0,0, 0));

        usernameLabel = new JLabel("사용자: " + (user != null ? user : ""));
        usernameLabel.setFont(UIConstants.LABEL_FONT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 0, 20);

        panel.add(usernameLabel, gbc);

        return panel;
    }

    public void setUsername(String username) {
        if (usernameLabel != null) {
            usernameLabel.setText("사용자: " + username);
        }
    }
}

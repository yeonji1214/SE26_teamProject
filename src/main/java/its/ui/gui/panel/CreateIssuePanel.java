package its.ui.gui.panel;

import its.domain.issue.Priority;
import its.ui.gui.common.PlaceholderTextField;
import its.ui.gui.common.UIConstants;

import javax.swing.*;
import java.awt.*;

public class CreateIssuePanel extends BasePanel {

    // 입력 폼 컴포넌트
    private JComboBox<String> projectComboBox;
    private PlaceholderTextField titleTextField;
    private JTextArea descriptionTextArea;
    private JComboBox<Priority> priorityComboBox;

    // 버튼
    private JButton cancelButton;
    private JButton saveButton;

    // 리스너
    private CreateIssueActionListener listener;


    @Override
    protected void initComponents() {
        projectComboBox = new JComboBox<>();
        titleTextField = new PlaceholderTextField("이슈 제목을 입력하세요");
        descriptionTextArea = new JTextArea();
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setWrapStyleWord(true);
        priorityComboBox = new JComboBox<>(Priority.values());

        JPanel formPanel = createFormPanel();
        JPanel infoPanel = createInfoPanel();
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        add(formPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.EAST);

    }

    @Override
    protected void setupListeners() {
        cancelButton.addActionListener(e -> {
            if (listener != null) {listener.onCancelRequested();}
        });

        saveButton.addActionListener(e -> {
            if (listener != null) {
                String project = (String) projectComboBox.getSelectedItem();
                String title = titleTextField.getText();
                String description = descriptionTextArea.getText();
                String priority = (String) priorityComboBox.getSelectedItem();

                listener.onSaveRequested(project, title, description, priority);}
        });
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel title = new JLabel("이슈 등록");
        title.setFont(UIConstants.TITLE_FONT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        panel.add(title, gbc);

        JLabel projectLabel = new JLabel("프로젝트");
        projectLabel.setFont(UIConstants.LABEL_FONT);
        projectLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        gbc.gridy = 1;
        panel.add(projectLabel, gbc);

        gbc.gridy = 2;
        panel.add(projectComboBox, gbc);

        JLabel titleLabel = new JLabel("제목");
        titleLabel.setFont(UIConstants.LABEL_FONT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        gbc.gridy = 3;
        panel.add(titleLabel, gbc);

        gbc.gridy = 4;
        panel.add(titleTextField, gbc);

        JLabel descriptionLabel = new JLabel("설명");
        descriptionLabel.setFont(UIConstants.LABEL_FONT);
        descriptionLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        gbc.gridy = 5;
        panel.add(descriptionLabel, gbc);

        gbc.gridy = 6;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(descriptionTextArea), gbc);

        JLabel priorityLabel = new JLabel("우선순위");
        priorityLabel.setFont(UIConstants.LABEL_FONT);
        priorityLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        gbc.gridy = 7;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(priorityLabel, gbc);

        gbc.gridy = 8;
        panel.add(priorityComboBox, gbc);

        JPanel buttonPanel = createButtonPanel();
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0.0;
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        String introDescription = "<html>이슈 제목과 설명은 필수 입력 항목입니다.<br>등록된 이슈는 기본적으로 NEW 상태로 생성됩니다.</html>";
        JPanel introPanel = createInfoCell("안내", introDescription);
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5, 0, 5, 0);
        panel.add(introPanel, gbc);

        String initInfoDescription = "<html>* 리포터는 현재 로그인한 사용자로 자동 저장됩니다.<br>* 등록일은 이슈 생성 시점으로 자동 저장됩니다<br>* 담당자와 수행자는 초기에는 비어있습니다.</html>";
        JPanel initInfoPanel = createInfoCell("자동 설정값 안내", initInfoDescription);
        gbc.gridy = 1;
        panel.add(initInfoPanel, gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        cancelButton = createStyledButton("취소");
        saveButton = createStyledButton("저장", UIConstants.ButtonType.PRIMARY);

        panel.add(cancelButton);
        panel.add(saveButton);

        return panel;
    }

    private JPanel createInfoCell(String title, String description) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.CARD_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIConstants.SUBTITLE_FONT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(UIConstants.LABEL_FONT);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(descriptionLabel, BorderLayout.CENTER);

        return panel;
    }

    public void setCreateIssueActionListener(CreateIssueActionListener listener) {
        this.listener = listener;
    }

    public interface CreateIssueActionListener {
        void onSaveRequested(String project, String title, String description, String priority);
        void onCancelRequested();
    }

    @Override
    public void clear() {
        if (projectComboBox.getItemCount() > 0) {
            projectComboBox.setSelectedIndex(0);
        }
        titleTextField.setText("");
        descriptionTextArea.setText("");
        priorityComboBox.setSelectedIndex(0);
    }

    @Override
    public void onActivate() {
        // TODO: project 콤보박스 리스트 갱신

        clear();

        titleTextField.requestFocusInWindow();
    }
}

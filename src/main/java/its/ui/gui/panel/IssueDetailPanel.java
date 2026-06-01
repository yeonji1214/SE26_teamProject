package its.ui.gui.panel;

import its.domain.issue.Comment;
import its.domain.issue.IssueStatus;
import its.ui.gui.common.UIConstants;

import javax.swing.*;
import java.awt.*;

public class IssueDetailPanel extends BasePanel {

    // 액션 컴포넌트
    private JButton backButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton statusChangeButton;
    private JComboBox<IssueStatus> statusComboBox;
    private JTextArea commentTextArea;

    // 데이터 표시 컴포넌트
    private JLabel titleLabel;
    private JLabel issueIdLabel;
    private JLabel projectValue;
    private JLabel reporterValue;
    private JLabel dateValue;
    private JLabel assigneeValue;
    private JLabel fixerValue;
    private JLabel statusValue;
    private JLabel priorityValue;
    private JTextArea descriptionArea;

    // 코멘트 영역
    private JPanel commentListPanel;

    // 액션리스너
    private IssueDetailActionListener listener;

    private int currentIssueId;


    @Override
    protected void initComponents() {
        titleLabel = new JLabel("제목 없음");
        issueIdLabel = new JLabel("Issue #Id");
        projectValue = new JLabel("프로젝트 없음");
        reporterValue = new JLabel("리포터 없음");
        dateValue = new JLabel("등록일 없음");
        assigneeValue = new JLabel("담당자 없음");
        fixerValue = new JLabel("수행자 없음");
        statusValue = new JLabel("상태 없음");
        priorityValue = new JLabel("우선순위 없음");
        descriptionArea = new JTextArea("설명 없음");
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        backButton = new JButton("← 이슈 목록으로");
        editButton = createStyledButton("수정");
        deleteButton = createStyledButton("삭제", UIConstants.ButtonType.DANGER);
        statusChangeButton = createStyledButton("상태 변경", UIConstants.ButtonType.PRIMARY);
        statusComboBox = new JComboBox<>(IssueStatus.values());
        commentTextArea = new JTextArea();
        commentTextArea.setRows(6);

        commentListPanel = new JPanel();
        commentListPanel.setLayout(new BoxLayout(commentListPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(createContentPanel());
        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    protected void setupListeners() {
        backButton.addActionListener(e -> {
            if (listener != null) {listener.onBackRequested();}
        });

        editButton.addActionListener(e -> {
            if (listener != null) {listener.onIssueEditRequested(currentIssueId);}
        });

        deleteButton.addActionListener(e -> {
            if (listener != null) {listener.onIssueDeleteRequested(currentIssueId);}
        });

        statusChangeButton.addActionListener(e -> {
            if (listener != null) {
                IssueStatus selected = (IssueStatus) statusComboBox.getSelectedItem();
                String comment = commentTextArea.getText();
                listener.onStatusChangeRequested(currentIssueId, selected, comment);
            }
        });
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel headerPanel = createHeaderPanel();
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(headerPanel);

        JPanel cardPanel = createCardPanel();
        cardPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(cardPanel);

        JPanel commentPanel = createCommentPanel();
        commentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(commentPanel);

        return panel;
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        northPanel.setBorder(BorderFactory.createEmptyBorder(0,0,20,0));

        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.setForeground(UIConstants.PRIMARY_BUTTON_COLOR);

        northPanel.add(backButton);
        panel.add(northPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        issueIdLabel.setFont(UIConstants.LABEL_FONT);
        issueIdLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        centerPanel.add(titleLabel);
        centerPanel.add(issueIdLabel);
        panel.add(centerPanel, BorderLayout.CENTER);

        JPanel eastPanel = new JPanel();

        eastPanel.add(editButton);
        eastPanel.add(deleteButton);
        panel.add(eastPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createCardPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.weightx = 0.25;
        gbc.gridx = 0;
        gbc.insets = new Insets(0,0,0,10);
        panel.add(createInfoCard(), gbc);

        gbc.weightx = 0.5;
        gbc.gridx = 1;
        panel.add(createDescriptionCard(), gbc);

        gbc.weightx = 0.25;
        gbc.gridx = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(createActionCard(), gbc);

        return panel;
    }

    private JPanel createCommentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.CARD_COLOR);

        JLabel title = new JLabel("코멘트");
        title.setFont(UIConstants.SUBTITLE_FONT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title, BorderLayout.NORTH);

        panel.add(commentListPanel, BorderLayout.CENTER);

        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        return panel;
    }

    private JPanel createInfoCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UIConstants.CARD_COLOR);

        JLabel title = new JLabel("이슈 정보");
        title.setFont(UIConstants.SUBTITLE_FONT);

        card.add(title);
        card.add(createInfoRow("프로젝트", projectValue));
        card.add(createInfoRow("리포터", reporterValue));
        card.add(createInfoRow("등록일", dateValue));
        card.add(createInfoRow("담당자", assigneeValue));
        card.add(createInfoRow("수행자", fixerValue));
        card.add(createInfoRow("상태", statusValue));
        card.add(createInfoRow("우선순위", priorityValue));

        return card;
    }

    private JPanel createDescriptionCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UIConstants.CARD_COLOR);

        JLabel title = new JLabel("설명");
        title.setFont(UIConstants.SUBTITLE_FONT);

        card.add(title);

        descriptionArea.setOpaque(false);
        card.add(descriptionArea);

        return card;
    }

    private JPanel createActionCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UIConstants.CARD_COLOR);

        JLabel title = new JLabel("작업");
        title.setFont(UIConstants.SUBTITLE_FONT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);

        JLabel statusChangeLabel = new JLabel("상태 변경");
        statusChangeLabel.setFont(UIConstants.LABEL_FONT);
        statusChangeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(statusChangeLabel);

        statusComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(statusComboBox);

        JLabel commentLabel = new JLabel("코멘트");
        commentLabel.setFont(UIConstants.LABEL_FONT);
        commentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(commentLabel);

        JScrollPane scrollPane = new JScrollPane(commentTextArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(scrollPane);

        statusChangeButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        card.add(statusChangeButton);

        return card;
    }

    private JPanel createInfoRow(String labelText, JLabel valueLabel) {
        JPanel panel = new JPanel();
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(UIConstants.LABEL_FONT);
        label.setPreferredSize(new Dimension(50, 15));

        panel.add(label);
        panel.add(valueLabel);

        return panel;
    }

    private JPanel createCommentCard(Comment comment) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel author = new JLabel(comment.getAuthor().getUsername());
        JLabel date = new JLabel(comment.getCreatedAt().format(UIConstants.DATE_TIME_FORMATTER));

        topPanel.add(author, BorderLayout.WEST);
        topPanel.add(date, BorderLayout.EAST);

        card.add(topPanel);

        JTextArea commentArea = new JTextArea(comment.getContent());
        commentArea.setEditable(false);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        commentArea.setOpaque(false);

        card.add(commentArea);

        return card;
    }

    public void loadIssue(int issueId) {
        currentIssueId = issueId;

        commentListPanel.removeAll();
        // TODO: 서비스에서 코멘트 목록 가져와서 코멘트 수만큼 카드 생성하고 추가
        commentListPanel.revalidate();
        commentListPanel.repaint();
    }

    public void setIssueDetailActionListener(IssueDetailActionListener listener) {
        this.listener = listener;
    }

    public interface IssueDetailActionListener {
        void onBackRequested();
        void onIssueEditRequested(int issueId);
        void onIssueDeleteRequested(int issueId);
        void onStatusChangeRequested(int issueId, IssueStatus newStatus, String comment);
    }
}

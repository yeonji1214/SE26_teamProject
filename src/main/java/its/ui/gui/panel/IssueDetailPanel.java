package its.ui.gui.panel;

import its.domain.issue.Comment;
import its.domain.issue.Issue;
import its.domain.issue.IssueStatus;
import its.service.IssueService;
import its.ui.gui.common.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class IssueDetailPanel extends BasePanel {
    private JLabel backButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton statusChangeButton;
    private JComboBox<IssueStatus> statusComboBox;
    private JTextArea commentTextArea;

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

    private JPanel commentListPanel;

    private IssueDetailActionListener listener;
    private IssueService issueService;

    private int currentIssueId;

    public void setIssueService(IssueService issueService) {
        this.issueService = issueService;
    }

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
        descriptionArea.setFocusable(false);

        backButton = new JLabel("← 이슈 목록으로");
        editButton = createStyledButton("수정");
        deleteButton = createStyledButton("삭제", UIConstants.ButtonType.DANGER);
        statusChangeButton = createStyledButton("상태 변경", UIConstants.ButtonType.PRIMARY);

        statusComboBox = new JComboBox<>(IssueStatus.values());

        commentTextArea = new JTextArea();
        commentTextArea.setRows(6);
        commentTextArea.setLineWrap(true);
        commentTextArea.setWrapStyleWord(true);

        commentListPanel = new JPanel();
        commentListPanel.setLayout(new BoxLayout(commentListPanel, BoxLayout.Y_AXIS));
        commentListPanel.setBackground(UIConstants.CARD_COLOR);

        JScrollPane scrollPane = new JScrollPane(createContentPanel());
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    protected void setupListeners() {
        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (listener != null) {
                    listener.onBackRequested();
                }
            }
        });

        editButton.addActionListener(e -> {
            if (listener != null) {
                listener.onIssueEditRequested(currentIssueId);
            }
        });

        deleteButton.addActionListener(e -> {
            if (listener != null) {
                listener.onIssueDeleteRequested(currentIssueId);
            }
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
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 10, 0);

        gbc.gridy = 0;
        panel.add(createHeaderPanel(), gbc);

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.7;
        panel.add(createCardPanel(), gbc);

        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0.3;
        gbc.insets = new Insets(20, 0, 0, 0);
        panel.add(createCommentPanel(), gbc);

        return panel;
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.setForeground(UIConstants.PRIMARY_BUTTON_COLOR);

        northPanel.add(backButton);
        northPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
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

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.25;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 0, 10);

        JPanel infoCard = createInfoCard();
        infoCard.setPreferredSize(new Dimension(0, 0));
        panel.add(infoCard, gbc);

        gbc.weightx = 0.5;
        gbc.gridx = 1;

        JPanel descriptionCard = createDescriptionCard();
        descriptionCard.setPreferredSize(new Dimension(0, 0));
        panel.add(descriptionCard, gbc);

        gbc.weightx = 0.25;
        gbc.gridx = 2;
        gbc.insets = new Insets(0, 0, 0, 0);

        JPanel actionCard = createActionCard();
        actionCard.setPreferredSize(new Dimension(0, 0));
        panel.add(actionCard, gbc);

        return panel;
    }

    private JPanel createCommentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.CARD_COLOR);

        JLabel title = new JLabel("코멘트");
        title.setFont(UIConstants.SUBTITLE_FONT);
        title.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.add(title, BorderLayout.NORTH);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UIConstants.CARD_COLOR);
        wrapper.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        wrapper.add(commentListPanel, BorderLayout.CENTER);

        panel.add(wrapper, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInfoCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UIConstants.CARD_COLOR);
        card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("이슈 정보");
        title.setFont(UIConstants.SUBTITLE_FONT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(10));
        card.add(createInfoRow("프로젝트", projectValue));
        card.add(Box.createVerticalStrut(5));
        card.add(createInfoRow("리포터", reporterValue));
        card.add(Box.createVerticalStrut(5));
        card.add(createInfoRow("등록일", dateValue));
        card.add(Box.createVerticalStrut(5));
        card.add(createInfoRow("담당자", assigneeValue));
        card.add(Box.createVerticalStrut(5));
        card.add(createInfoRow("수행자", fixerValue));
        card.add(Box.createVerticalStrut(5));
        card.add(createInfoRow("상태", statusValue));
        card.add(Box.createVerticalStrut(5));
        card.add(createInfoRow("우선순위", priorityValue));

        return card;
    }

    private JPanel createDescriptionCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UIConstants.CARD_COLOR);
        card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("설명");
        title.setFont(UIConstants.SUBTITLE_FONT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(10));

        descriptionArea.setOpaque(false);
        descriptionArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(descriptionArea);

        return card;
    }

    private JPanel createActionCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UIConstants.CARD_COLOR);
        card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("작업");
        title.setFont(UIConstants.SUBTITLE_FONT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(10));

        JLabel statusChangeLabel = new JLabel("상태 변경");
        statusChangeLabel.setFont(UIConstants.LABEL_FONT);
        statusChangeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(statusChangeLabel);
        card.add(Box.createVerticalStrut(5));

        statusComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, statusComboBox.getPreferredSize().height));
        card.add(statusComboBox);
        card.add(Box.createVerticalStrut(10));

        JLabel commentLabel = new JLabel("코멘트");
        commentLabel.setFont(UIConstants.LABEL_FONT);
        commentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(commentLabel);
        card.add(Box.createVerticalStrut(5));

        JScrollPane scrollPane = new JScrollPane(commentTextArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        card.add(scrollPane);
        card.add(Box.createVerticalStrut(10));

        statusChangeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(statusChangeButton);

        return card;
    }

    private JPanel createInfoRow(String labelText, JLabel valueLabel) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBackground(UIConstants.CARD_COLOR);

        JLabel label = new JLabel(labelText);
        label.setFont(UIConstants.LABEL_FONT);
        label.setPreferredSize(new Dimension(60, 20));

        valueLabel.setFont(UIConstants.LABEL_FONT);

        panel.add(label);
        panel.add(valueLabel);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));

        return panel;
    }

    private JPanel createCommentCard(Comment comment) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(230, 230, 230));
        card.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel author = new JLabel(comment.getAuthor().getUsername());
        author.setFont(UIConstants.HEADER_FONT);

        JLabel date = new JLabel(comment.getCreatedAt().format(UIConstants.DATE_TIME_FORMATTER));
        date.setFont(UIConstants.LABEL_FONT);

        topPanel.add(author, BorderLayout.WEST);
        topPanel.add(date, BorderLayout.EAST);

        JTextArea commentArea = new JTextArea(comment.getContent());
        commentArea.setEditable(false);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        commentArea.setOpaque(false);
        commentArea.setFocusable(false);

        card.add(topPanel);
        card.add(Box.createVerticalStrut(4));
        card.add(commentArea);

        return card;
    }

    public void loadIssue(int issueId) {
        currentIssueId = issueId;

        if (issueService == null) {
            return;
        }

        Issue issue = issueService.getIssue((long) issueId);

        titleLabel.setText(issue.getTitle());
        issueIdLabel.setText("Issue #" + issue.getId());
        projectValue.setText(issue.getProject().getName());
        reporterValue.setText(issue.getReporter().getUsername());
        dateValue.setText(issue.getReportedDate().format(UIConstants.DATE_TIME_FORMATTER));
        assigneeValue.setText(issue.getAssignee() == null ? "-" : issue.getAssignee().getUsername());
        fixerValue.setText(issue.getFixer() == null ? "-" : issue.getFixer().getUsername());
        statusValue.setText(issue.getStatus().name());
        priorityValue.setText(issue.getPriority().name());
        descriptionArea.setText(issue.getDescription());
        statusComboBox.setSelectedItem(issue.getStatus());
        commentTextArea.setText("");

        refreshComments(issueId);
    }

    private void refreshComments(int issueId) {
        commentListPanel.removeAll();

        if (issueService == null) {
            return;
        }

        java.util.List<Comment> comments = issueService.getComments((long) issueId);

        if (comments.isEmpty()) {
            JLabel emptyLabel = new JLabel("등록된 코멘트가 없습니다.");
            emptyLabel.setFont(UIConstants.LABEL_FONT);
            commentListPanel.add(emptyLabel);
        } else {
            for (Comment comment : comments) {
                commentListPanel.add(createCommentCard(comment));
                commentListPanel.add(Box.createVerticalStrut(8));
            }
        }

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
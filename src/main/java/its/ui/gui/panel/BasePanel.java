package its.ui.gui.panel;

import its.ui.gui.common.UIConstants;
import javax.swing.*;
import java.awt.*;

public abstract class BasePanel extends JPanel {
    public BasePanel() {
        setBackground(UIConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(
                UIConstants.DEFAULT_PADDING,
                UIConstants.DEFAULT_PADDING,
                UIConstants.DEFAULT_PADDING,
                UIConstants.DEFAULT_PADDING
        ));

        initialize();
    }

    // Set initializing order
    private void initialize() {
        setupLayout();
        initComponents();
        setupListeners();
    }

    /*
    Hook: 레이아웃 설정. 기본값은 Border, 필요 시 오버라이드
    */
    protected void setupLayout() {
        setLayout(new BorderLayout());
    }

    protected abstract void initComponents();

    /*
    Hook: 이벤트 리스너 설정. 필요 시 오버라이드
    */
    protected void setupListeners() {
        // Basically do nothing
    }

    protected JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(UIConstants.BUTTON_SIZE);
        button.setFont(UIConstants.BUTTON_FONT);

        return button;
    }

    protected JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.TITLE_FONT);
        return label;
    }

    protected void setTabOrder(Container container, Component... components) {
        FocusTraversalPolicy policy = new FocusTraversalPolicy() {
            @Override
            public Component getComponentAfter(Container aContainer, Component aComponent) {
                for (int i = 0; i < components.length; i++) {
                    if (components[i] == aComponent) {
                        return i == components.length - 1
                                ? components[0]
                                : components[i + 1];
                    }
                }
                return components[0];
            }

            @Override
            public Component getComponentBefore(Container aContainer, Component aComponent) {
                for (int i = 0; i < components.length; i++) {
                    if (components[i] == aComponent) {
                        return i == 0
                                ? components[components.length - 1]
                                : components[i - 1];
                    }
                }
                return components[components.length - 1];
            }

            @Override
            public Component getFirstComponent(Container aContainer) {
                return components[0];
            }

            @Override
            public Component getLastComponent(Container aContainer) {
                return components[components.length - 1];
            }

            @Override
            public Component getDefaultComponent(Container aContainer) {
                return components[0];
            }
        };

        container.setFocusTraversalPolicy(policy);
        container.setFocusTraversalPolicyProvider(true);
    }

    /*
    Hook: 패널의 입력 필드를 초기화할 때 호출(사용자 입력, 검색 조건 등)
    필요한 패널에서만 오버라이드
    */
    public void clear() {
        // Basically do nothing
    }

    /*
    Hook: 패널이 화면에 표시될 때 호출
    데이터 갱신이 필요한 패널에서 오버라이드
    */
    public void onActivate() {
        // Basically do nothing
    }
}

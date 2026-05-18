package its.ui.gui.common;

import javax.swing.*;
import java.awt.*;

/**
 * 플레이스홀더 기능을 가진 비밀번호 필드
 */
public class PlaceholderPasswordField extends JPasswordField {

    private String placeholder;

    public PlaceholderPasswordField(String placeholder, int columns) {
        super(columns);
        this.placeholder = placeholder;
    }

    public PlaceholderPasswordField(String placeholder) {
        this(placeholder, 20);
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (getPassword().length == 0 && !isFocusOwner()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.GRAY);
            g2.setFont(getFont());

            FontMetrics fm = g2.getFontMetrics();

            // 텍스트 위치 계산
            int x = getInsets().left;

            int textHeight = fm.getHeight();
            int componentHeight = getHeight();
            int y = ((componentHeight - textHeight) / 2) + fm.getAscent();

            g2.drawString(placeholder, x, y);
            g2.dispose();
        }
    }
}
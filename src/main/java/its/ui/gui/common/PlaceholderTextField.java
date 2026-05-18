package its.ui.gui.common;

import javax.swing.*;
import java.awt.*;

public class PlaceholderTextField extends JTextField {

    private String placeholder;

    public PlaceholderTextField(String placeholder, int columns) {
        super(columns);
        this.placeholder = placeholder;
    }

    public PlaceholderTextField(String placeholder) {
        this(placeholder, 20);  // 기본 20칸
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

        // 텍스트가 비어있고 포커스가 없을 때만 플레이스홀더 표시
        if (getText().isEmpty() && !isFocusOwner()) {
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
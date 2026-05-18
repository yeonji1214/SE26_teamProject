package its.ui.gui.common;

import java.awt.*;

public class UIConstants {
    // Font constants
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 20);
    public static final Font SUBTITLE_FONT = new Font("Arial", Font.BOLD, 18);
    public static final Font BUTTON_FONT = new Font("Arial", Font.PLAIN, 14);
    public static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 12);

    // Color constants
    public static final Color BACKGROUND_COLOR = Color.WHITE;
    public static final Color PRIMARY_COLOR = new Color (103, 110, 159);

    public static final Color PRIMARY_BUTTON_COLOR = new Color(103, 110, 159);
    public static final Color SECONDARY_BUTTON_COLOR = new Color(158, 158, 158); // Gray
    public static final Color DANGER_BUTTON_COLOR = new Color(244, 67, 54);    // Material Red
    public static final Color SUCCESS_BUTTON_COLOR = new Color(76, 175, 80);   // Material Green
    public static final Color WARNING_BUTTON_COLOR = new Color(255, 152, 0);   // Material Orange

    // Size constants
    public static final int DEFAULT_PADDING = 10;
    public static final Dimension BUTTON_SIZE = new Dimension(120, 35);
    public static final Dimension INPUT_FIELD_SIZE = new Dimension(200, 40);
}

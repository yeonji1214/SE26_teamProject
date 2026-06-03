package its.ui.gui.common;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class UIConstants {
    // Font constants
    public static final Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 20);
    public static final Font SUBTITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 16);
    public static final Font HEADER_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    public static final Font BUTTON_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    public static final Font LABEL_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

    // Color constants
    public static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    public static final Color CARD_COLOR = new Color(200, 200, 200);
    public static final Color PRIMARY_COLOR = new Color (36, 99, 235);

    public static final Color NAVIGATION_COLOR = new Color (16, 24, 39);
    public static final Color NAVIGATION_HOVER_COLOR = new Color(25, 35, 55);

    public static final Color PRIMARY_BUTTON_COLOR = new Color(36, 99, 235);
    public static final Color SECONDARY_BUTTON_COLOR = new Color(200, 200, 200); // Gray
    public static final Color DANGER_BUTTON_COLOR = new Color(244, 67, 54);    // Material Red
    public static final Color SUCCESS_BUTTON_COLOR = new Color(76, 175, 80);   // Material Green
    public static final Color WARNING_BUTTON_COLOR = new Color(255, 152, 0);   // Material Orange

    // Size constants
    public static final int DEFAULT_PADDING = 10;
    public static final Dimension BUTTON_SIZE = new Dimension(120, 35);
    public static final Dimension INPUT_FIELD_SIZE = new Dimension(200, 40);

    public enum ButtonType {PRIMARY, SECONDARY, DANGER, SUCCESS, WARNING};

    public static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy. M. d. a hh:mm:ss", Locale.KOREAN);
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
}

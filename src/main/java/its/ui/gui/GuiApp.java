package its.ui.gui;

import its.service.ApplicationServices;
import its.service.DemoDataSeeder;
import its.service.ServiceFactory;
import its.ui.gui.common.UIConfig;

import javax.swing.SwingUtilities;
import java.nio.file.Path;

public class GuiApp {
    private static final Path DATABASE_PATH = Path.of("issue-tracker.db");

    public static void main(String[] args) {
        ApplicationServices services = ServiceFactory.createWithSqliteDatabase(DATABASE_PATH);
        new DemoDataSeeder(services).seedIfEmpty();

        UIConfig.initialize();

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(services);
            frame.setVisible(true);
        });
    }
}
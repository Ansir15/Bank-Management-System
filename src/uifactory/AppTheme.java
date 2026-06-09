package uifactory;

import com.formdev.flatlaf.themes.FlatMacLightLaf;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public final class AppTheme {

    public static final Color PRIMARY   = new Color(37, 99, 235);
    public static final Color SECONDARY = new Color(46, 196, 182);
    public static final Color SUCCESS   = new Color(22, 163, 74);
    public static final Color DANGER    = new Color(220, 38, 38);
    public static final Color WARNING   = new Color(202, 138, 4);
    public static final Color NEUTRAL   = new Color(100, 100, 100);
    public static final Color INFO      = PRIMARY;
    public static final Color ACCENT    = new Color(124, 58, 237);

    public static final Color MODULE_CUSTOMERS    = new Color(58, 134, 255);
    public static final Color MODULE_ACCOUNTS     = new Color(46, 196, 182);
    public static final Color MODULE_TRANSACTIONS = new Color(255, 107, 107);
    public static final Color MODULE_LOANS        = new Color(244, 162, 97);
    public static final Color MODULE_CARDS        = new Color(155, 93, 229);
    public static final Color MODULE_EMPLOYEES    = new Color(6, 214, 160);
    public static final Color MODULE_BRANCHES     = new Color(17, 138, 178);

    public static final String FONT_FAMILY = "Segoe UI";

    public static final int SPACING_XS = 4;
    public static final int SPACING_SM = 8;
    public static final int SPACING_MD = 14;
    public static final int SPACING_LG = 18;
    public static final int SPACING_XL = 24;

    public static final int FIELD_HEIGHT = 42;
    public static final int BUTTON_HEIGHT = 46;
    public static final int TABLE_ROW_HEIGHT = 34;

    private static final Map<String, Color> STATUS_COLORS = new HashMap<>();

    static {
        STATUS_COLORS.put("Active", SUCCESS);
        STATUS_COLORS.put("Approved", SUCCESS);
        STATUS_COLORS.put("DEPOSIT", SUCCESS);
        STATUS_COLORS.put("Teller", SUCCESS);

        STATUS_COLORS.put("Frozen", WARNING);
        STATUS_COLORS.put("Pending", WARNING);

        STATUS_COLORS.put("Closed", DANGER);
        STATUS_COLORS.put("Blocked", DANGER);
        STATUS_COLORS.put("Rejected", DANGER);
        STATUS_COLORS.put("WITHDRAW", DANGER);
        STATUS_COLORS.put("Admin", DANGER);

        STATUS_COLORS.put("Expired", NEUTRAL);

        STATUS_COLORS.put("TRANSFER", PRIMARY);
        STATUS_COLORS.put("Manager", PRIMARY);

        STATUS_COLORS.put("Credit", ACCENT);
    }

    private AppTheme() {}

    public static void install() {
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Color uiColor(String key) {
        Color c = UIManager.getColor(key);
        return c != null ? c : PRIMARY;
    }

    public static Color panelBackground() {
        return uiColor("Panel.background");
    }

    public static Color labelForeground() {
        return uiColor("Label.foreground");
    }

    public static Color tableBackground() {
        return uiColor("Table.background");
    }

    public static Color separator() {
        return uiColor("Separator.foreground");
    }

    public static Color mutedForeground() {
        Color fg = labelForeground();
        return new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 150);
    }

    public static Color hintForeground() {
        Color fg = labelForeground();
        return new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 120);
    }

    public static boolean isDarkTheme() {
        Color bg = panelBackground();
        return (bg.getRed() * 299 + bg.getGreen() * 587 + bg.getBlue() * 114) / 1000 < 128;
    }

    public static Color stripeRow(Color base) {
        if (base == null) base = tableBackground();
        boolean dark = isDarkTheme();
        int delta = dark ? 15 : -12;
        return new Color(
                clamp(base.getRed() + delta),
                clamp(base.getGreen() + delta),
                clamp(base.getBlue() + delta)
        );
    }

    public static Color statusColor(String status) {
        if (status == null) return labelForeground();
        Color c = STATUS_COLORS.get(status);
        return c != null ? c : labelForeground();
    }

    public static Color accentFromTheme() {
        String[] keys = {
                "Component.accentColor", "Button.default.background",
                "Focus.color", "TabbedPane.underlineColor",
                "CheckBox.icon.selectedBackground", "ProgressBar.foreground"
        };
        for (String key : keys) {
            Color c = UIManager.getColor(key);
            if (c != null) return c;
        }
        return PRIMARY;
    }

    public static Font font(int style, int size) {
        return new Font(FONT_FAMILY, style, size);
    }

    public static Font pageTitle() {
        return font(Font.BOLD, 22);
    }

    public static Font sectionTitle() {
        return font(Font.BOLD, 18);
    }

    public static Font formTitle() {
        return font(Font.BOLD, 13);
    }

    public static Font body() {
        return font(Font.PLAIN, 13);
    }

    public static Font bodyBold() {
        return font(Font.BOLD, 13);
    }

    public static Font tableHeader() {
        return font(Font.BOLD, 12);
    }

    public static Font tableBody() {
        return font(Font.PLAIN, 13);
    }

    public static Font field() {
        return font(Font.PLAIN, 14);
    }

    public static Font caption() {
        return font(Font.PLAIN, 12);
    }

    public static Font hint() {
        return font(Font.ITALIC, 11);
    }

    public static Font buttonPrimary() {
        return font(Font.BOLD, 15);
    }

    public static Font small() {
        return font(Font.PLAIN, 11);
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }
}

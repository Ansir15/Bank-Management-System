import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import uifactory.AppTheme;
import system.bankingapp.frontend.LoginPage;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        FlatMacDarkLaf.setup();
        SwingUtilities.invokeLater(() -> {
            new LoginPage().setVisible(true);
        });
    }
}

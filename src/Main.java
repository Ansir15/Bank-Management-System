import com.formdev.flatlaf.themes.FlatMacLightLaf;
import system.bankingapp.frontend.LoginPage;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(new FlatMacLightLaf()); } catch (Exception e) { e.printStackTrace(); }
            new LoginPage().setVisible(true);
        });
    }
}
package system.bankingapp.mainpages;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import system.bankingapp.frontend.CustomerPage;
import system.bankingapp.frontend.AccountPage;
import system.bankingapp.frontend.TransactionPage;
import system.bankingapp.frontend.LoanPage;
import system.bankingapp.frontend.CardPage;
import uifactory.SideBar;

import javax.swing.*;
import java.awt.*;

public class AdminPage extends JFrame {

    static void main() {
        FlatMacLightLaf.setup();
        JFrame frame = new JFrame();
        frame.setExtendedState(MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        SideBar sidebar = new SideBar(e -> {
            if (JOptionPane.showConfirmDialog(frame, "Logout?", "Logout", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                System.out.println("Logged out");
        });

        sidebar.addMenuItem("Customers",    "#3A86FF", FontAwesomeSolid.USERS,           new Color(58,  134, 255), new CustomerPage());
        sidebar.addMenuItem("Accounts",     "#2EC4B6", FontAwesomeSolid.UNIVERSITY,       new Color(46,  196, 182), new AccountPage());
        sidebar.addMenuItem("Transactions", "#FF6B6B", FontAwesomeSolid.EXCHANGE_ALT,     new Color(255, 107, 107), new TransactionPage());
        sidebar.addMenuItem("Loans",        "#F4A261", FontAwesomeSolid.HAND_HOLDING_USD, new Color(244, 162,  97), new LoanPage());
        sidebar.addMenuItem("Cards",        "#9B5DE5", FontAwesomeSolid.CREDIT_CARD,      new Color(155,  93, 229), new CardPage());
        sidebar.addMenuItem("Employees",    "#06D6A0", FontAwesomeSolid.USER_TIE,         new Color(6,   214, 160), new JPanel());
        sidebar.addMenuItem("Branches",     "#118AB2", FontAwesomeSolid.CODE_BRANCH,      new Color(17,  138, 178), new JPanel());
        frame.add(sidebar);
        frame.setVisible(true);
    }

    public static JPanel createPanel(String p) {
        JPanel panel = new JPanel();
        return panel;
    }
}
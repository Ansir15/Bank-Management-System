package system.bankingapp.mainpages;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import system.bankingapp.frontend.*;
import uifactory.AppTheme;
import uifactory.SideBar;
import utils.AvatarUtil;

import javax.swing.*;
import java.awt.*;

public class AdminPage extends JFrame {

    public static void launch(String userName, String username) {

        JFrame frame = new JFrame("Banking Management System");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SideBar sidebar = new SideBar(e -> {
            if (JOptionPane.showConfirmDialog(frame, "Are you sure you want to logout?",
                    "Logout", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                frame.dispose();
                SwingUtilities.invokeLater(() -> {
                    AppTheme.install();
                    new LoginPage().setVisible(true);
                });
            }
        });

        sidebar.setUserProfile(userName, "Online");
        sidebar.setAvatarImage(AvatarUtil.loadForUser(username));

        sidebar.addMenuItem("Customers",    "#3A86FF", FontAwesomeSolid.USERS,           AppTheme.MODULE_CUSTOMERS,    new CustomerPage());
        sidebar.addMenuItem("Accounts",     "#2EC4B6", FontAwesomeSolid.UNIVERSITY,       AppTheme.MODULE_ACCOUNTS,     new AccountPage());
        sidebar.addMenuItem("Transactions", "#FF6B6B", FontAwesomeSolid.EXCHANGE_ALT,     AppTheme.MODULE_TRANSACTIONS, new TransactionPage());
        sidebar.addMenuItem("Loans",        "#F4A261", FontAwesomeSolid.HAND_HOLDING_USD, AppTheme.MODULE_LOANS,        new LoanPage());
        sidebar.addMenuItem("Cards",        "#9B5DE5", FontAwesomeSolid.CREDIT_CARD,      AppTheme.MODULE_CARDS,        new CardPage());
        sidebar.addMenuItem("Employees",    "#06D6A0", FontAwesomeSolid.USER_TIE,         AppTheme.MODULE_EMPLOYEES,    new EmployeePage());
        sidebar.addMenuItem("Branches",     "#118AB2", FontAwesomeSolid.CODE_BRANCH,      AppTheme.MODULE_BRANCHES,     new BranchPage());

        frame.add(sidebar);
        frame.setVisible(true);
    }
}

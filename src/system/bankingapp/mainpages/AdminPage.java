package system.bankingapp.mainpages;



import com.formdev.flatlaf.themes.FlatMacLightLaf;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import system.bankingapp.frontend.*;

import uifactory.SideBar;

import utils.AvatarUtil;

import javax.swing.*;

import java.awt.*;



public class AdminPage extends JFrame {



    public static void launch(String userName) {

        try { UIManager.setLookAndFeel(new FlatMacLightLaf()); } catch (Exception e) { e.printStackTrace(); }



        JFrame frame = new JFrame("Banking Management System");

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



        SideBar sidebar = new SideBar(e -> {

            if (JOptionPane.showConfirmDialog(frame, "Are you sure you want to logout?",

                    "Logout", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {

                frame.dispose();

                SwingUtilities.invokeLater(() -> {

                    try { UIManager.setLookAndFeel(new FlatMacLightLaf()); } catch (Exception ex) { ex.printStackTrace(); }

                    new system.bankingapp.frontend.LoginPage().setVisible(true);

                });

            }

        });



        sidebar.setUserProfile(userName, "Online");

        sidebar.setAvatarImage(AvatarUtil.load());



        sidebar.addMenuItem("Customers",    "#3A86FF", FontAwesomeSolid.USERS,           new Color(58,  134, 255), new CustomerPage());

        sidebar.addMenuItem("Accounts",     "#2EC4B6", FontAwesomeSolid.UNIVERSITY,       new Color(46,  196, 182), new AccountPage());

        sidebar.addMenuItem("Transactions", "#FF6B6B", FontAwesomeSolid.EXCHANGE_ALT,     new Color(255, 107, 107), new TransactionPage());

        sidebar.addMenuItem("Loans",        "#F4A261", FontAwesomeSolid.HAND_HOLDING_USD, new Color(244, 162,  97), new LoanPage());

        sidebar.addMenuItem("Cards",        "#9B5DE5", FontAwesomeSolid.CREDIT_CARD,      new Color(155,  93, 229), new CardPage());

        sidebar.addMenuItem("Employees",    "#06D6A0", FontAwesomeSolid.USER_TIE,         new Color(6,   214, 160), new EmployeePage());

        sidebar.addMenuItem("Branches",     "#118AB2", FontAwesomeSolid.CODE_BRANCH,      new Color(17,  138, 178), new BranchPage());



        frame.add(sidebar);

        frame.setVisible(true);

    }

}


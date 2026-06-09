package system.bankingapp.frontend;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import system.bankingapp.mainpages.AdminPage;
import uifactory.AppTheme;
import uifactory.UIFactory;
import utils.AvatarUtil;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class LoginPage extends JFrame {

    private JTextField     fldUsername;
    private JPasswordField fldPassword;
    private JLabel         errorLabel;

    private static final String[][] USERS = {
            {"ansir",  "admin123",   "Ansir Ali"},
            {"fatima", "manager123", "Fatima Noor"},
            {"bilal",  "teller123",  "Bilal Ahmed"},
    };

    public LoginPage() {
        setTitle("Banking Management System - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        setContentPane(buildUI());
    }

    private JPanel buildUI() {
        JPanel root = UIFactory.transparentPanel(new GridBagLayout());

        JPanel card = UIFactory.roundedCard(new GridBagLayout());
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(40, 40, 40, 40));
        card.setPreferredSize(new Dimension(380, 500));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(AppTheme.SPACING_SM, 0, AppTheme.SPACING_SM, 0);

        c.gridy = 0;
        c.insets = new Insets(0, 0, 6, 0);
        card.add(UIFactory.centerWrap(buildBankLogo()), c);

        c.gridy = 1;
        c.insets = new Insets(0, 0, 2, 0);
        card.add(UIFactory.sectionLabel("Secure Bank"), c);

        c.gridy = 2;
        c.insets = new Insets(0, 0, AppTheme.SPACING_XL, 0);
        card.add(UIFactory.subtitleLabel("Banking Management System"), c);

        fldUsername = UIFactory.tallTextField("Username");
        c.gridy = 3;
        c.insets = new Insets(6, 0, 6, 0);
        card.add(UIFactory.fieldWithIcon(fldUsername, FontAwesomeSolid.USER), c);

        fldPassword = UIFactory.passwordField("Password");
        fldPassword.addActionListener(e -> doLogin());
        c.gridy = 4;
        card.add(UIFactory.fieldWithIcon(fldPassword, FontAwesomeSolid.LOCK), c);

        errorLabel = UIFactory.errorLabel();
        c.gridy = 5;
        c.insets = new Insets(0, 0, 6, 0);
        card.add(errorLabel, c);

        JButton btnLogin = UIFactory.primaryButton("Login");
        btnLogin.addActionListener(e -> doLogin());
        c.gridy = 6;
        c.insets = new Insets(10, 0, 10, 0);
        card.add(btnLogin, c);

        c.gridy = 7;
        card.add(UIFactory.hintLabel("Enter username and password"), c);

        root.add(card);
        return root;
    }

    private JLabel buildBankLogo() {
        return new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int size = 90;
                try {
                    BufferedImage img = AvatarUtil.loadBankLogo();
                    if (img == null) throw new Exception("bank logo not found");
                    g2.drawImage(img, 0, 0, size, size, null);
                } catch (Exception ex) {
                    g2.setColor(AppTheme.PRIMARY);
                    g2.fillRoundRect(0, 0, size, size, 16, 16);
                    FontIcon.of(FontAwesomeSolid.UNIVERSITY, 40, Color.WHITE).paintIcon(this, g2, 25, 25);
                }
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(90, 90);
            }
        };
    }

    private void doLogin() {
        String user = fldUsername.getText().trim();
        String pass = new String(fldPassword.getPassword()).trim();

        for (String[] u : USERS) {
            if (u[0].equals(user) && u[1].equals(pass)) {
                dispose();
                SwingUtilities.invokeLater(() -> AdminPage.launch(u[2], u[0]));
                return;
            }
        }
        errorLabel.setText("Invalid username or password.");
        fldPassword.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppTheme.install();
            new LoginPage().setVisible(true);
        });
    }
}

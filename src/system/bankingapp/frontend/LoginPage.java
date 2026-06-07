package system.bankingapp.frontend;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import system.bankingapp.mainpages.AdminPage;
import utils.AvatarUtil;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
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
        JPanel root = new JPanel(new GridBagLayout());
        root.setOpaque(false);

        JPanel card = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIManager.getColor("Panel.background"));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(40, 40, 40, 40));
        card.setPreferredSize(new Dimension(380, 500));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(8, 0, 8, 0);

        JLabel avatar = buildAvatar();
        c.gridy = 0; c.insets = new Insets(0, 0, 6, 0);
        card.add(centerWrap(avatar), c);

        JLabel nameLabel = new JLabel("Ansir Ali");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridy = 1; c.insets = new Insets(0, 0, 2, 0);
        card.add(nameLabel, c);

        JLabel systemLabel = new JLabel("Banking Management System");
        systemLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        systemLabel.setHorizontalAlignment(SwingConstants.CENTER);
        Color fg = UIManager.getColor("Label.foreground");
        systemLabel.setForeground(fg != null ? new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 150) : Color.GRAY);
        c.gridy = 2; c.insets = new Insets(0, 0, 24, 0);
        card.add(systemLabel, c);

        fldUsername = new JTextField();
        fldUsername.putClientProperty("JTextField.placeholderText", "Username");
        fldUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fldUsername.setPreferredSize(new Dimension(0, 42));
        c.gridy = 3; c.insets = new Insets(6, 0, 6, 0);
        card.add(withIcon(fldUsername, FontAwesomeSolid.USER), c);

        fldPassword = new JPasswordField();
        fldPassword.putClientProperty("JTextField.placeholderText", "Password");
        fldPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fldPassword.setPreferredSize(new Dimension(0, 42));
        fldPassword.addActionListener(e -> doLogin());
        c.gridy = 4; c.insets = new Insets(6, 0, 6, 0);
        card.add(withIcon(fldPassword, FontAwesomeSolid.LOCK), c);

        errorLabel = new JLabel(" ");
        errorLabel.setForeground(new Color(220, 38, 38));
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridy = 5; c.insets = new Insets(0, 0, 6, 0);
        card.add(errorLabel, c);

        JButton btnLogin = new JButton("Login") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(37, 99, 235));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btnLogin.setPreferredSize(new Dimension(0, 46));
        btnLogin.setOpaque(false);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> doLogin());
        c.gridy = 6; c.insets = new Insets(10, 0, 10, 0);
        card.add(btnLogin, c);

        JLabel hint = new JLabel("ansir / admin123  |  fatima / manager123  |  bilal / teller123");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setHorizontalAlignment(SwingConstants.CENTER);
        hint.setForeground(Color.GRAY);
        c.gridy = 7;
        card.add(hint, c);

        root.add(card);
        return root;
    }

    private JLabel buildAvatar() {
        return new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int size = 90;
                try {
                    BufferedImage img = AvatarUtil.load();
                    if (img == null) throw new Exception("avatar not found");
                    BufferedImage scaled = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D sg = scaled.createGraphics();
                    sg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    sg.setClip(new Ellipse2D.Float(0, 0, size, size));
                    sg.drawImage(img, 0, 0, size, size, null);
                    sg.dispose();
                    g2.drawImage(scaled, 0, 0, null);
                } catch (Exception ex) {
                    g2.setColor(new Color(37, 99, 235));
                    g2.fillOval(0, 0, size, size);
                    FontIcon.of(FontAwesomeSolid.USER, 40, Color.WHITE).paintIcon(this, g2, 25, 25);
                }
                g2.setColor(new Color(34, 197, 94));
                g2.fillOval(size - 18, size - 18, 16, 16);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(size - 18, size - 18, 16, 16);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(90, 90); }
        };
    }

    private JPanel withIcon(JComponent field, org.kordamp.ikonli.Ikon icon) {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setOpaque(false);
        JLabel lbl = new JLabel(FontIcon.of(icon, 16));
        lbl.setBorder(new EmptyBorder(0, 10, 0, 0));
        p.add(lbl, BorderLayout.WEST);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private JPanel centerWrap(JComponent c) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p.setOpaque(false);
        p.add(c);
        return p;
    }

    private void doLogin() {
        String user = fldUsername.getText().trim();
        String pass = new String(fldPassword.getPassword()).trim();

        for (String[] u : USERS) {
            if (u[0].equals(user) && u[1].equals(pass)) {
                dispose();
                SwingUtilities.invokeLater(() -> AdminPage.launch(u[2]));
                return;
            }
        }
        errorLabel.setText("Invalid username or password.");
        fldPassword.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(new FlatMacLightLaf()); } catch (Exception e) { e.printStackTrace(); }
            new LoginPage().setVisible(true);
        });
    }
}

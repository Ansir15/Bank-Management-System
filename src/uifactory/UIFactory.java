package uifactory;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

public final class UIFactory {

    private UIFactory() {}

    public static JPanel transparentPanel(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setOpaque(false);
        return p;
    }

    public static JPanel transparentPanel() {
        return transparentPanel(new FlowLayout());
    }

    public static JPanel pageRoot() {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setOpaque(false);
        return p;
    }

    public static JPanel topBar() {
        JPanel bar = transparentPanel(new BorderLayout(AppTheme.SPACING_MD, 0));
        bar.setBorder(new EmptyBorder(AppTheme.SPACING_LG, AppTheme.SPACING_XL, 12, AppTheme.SPACING_XL));
        return bar;
    }

    public static JPanel titleGroup(JLabel title, JLabel... stats) {
        JPanel box = transparentPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        box.add(title);
        for (int i = 0; i < stats.length; i++) {
            box.add(Box.createHorizontalStrut(i == 0 ? AppTheme.SPACING_MD : AppTheme.SPACING_LG));
            box.add(stats[i]);
        }
        return box;
    }

    public static JPanel filterBar() {
        return transparentPanel(new FlowLayout(FlowLayout.RIGHT, AppTheme.SPACING_SM, 0));
    }

    public static JPanel formWrapper() {
        JPanel wrapper = transparentPanel(new BorderLayout());
        wrapper.setBorder(new EmptyBorder(0, 16, 16, AppTheme.SPACING_SM));
        return wrapper;
    }

    public static JPanel formCardShell(String title) {
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.panelBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.setColor(new Color(
                        AppTheme.separator().getRed(),
                        AppTheme.separator().getGreen(),
                        AppTheme.separator().getBlue(), 120));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
            }
        };
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(),
                        title, TitledBorder.LEFT, TitledBorder.TOP,
                        AppTheme.formTitle()),
                new EmptyBorder(6, AppTheme.SPACING_MD, AppTheme.SPACING_MD, AppTheme.SPACING_MD)));
        card.setOpaque(false);
        return card;
    }

    public static JPanel formCard(String title, JComponent content) {
        JPanel card = formCardShell(title);
        card.add(content);
        return card;
    }

    public static JPanel tablePanel(JComponent north, JComponent tableScroll) {
        JPanel p = transparentPanel(new BorderLayout(0, AppTheme.SPACING_SM));
        p.setBorder(new EmptyBorder(0, AppTheme.SPACING_SM, 16, 16));
        p.add(north, BorderLayout.NORTH);
        p.add(tableScroll, BorderLayout.CENTER);
        return p;
    }

    public static JPanel actionBar() {
        return transparentPanel(new FlowLayout(FlowLayout.RIGHT, AppTheme.SPACING_SM, AppTheme.SPACING_XS));
    }

    public static JPanel buttonRow(JComponent... buttons) {
        JPanel row = transparentPanel(new GridLayout(1, buttons.length, AppTheme.SPACING_SM, 0));
        for (JComponent b : buttons) row.add(b);
        return row;
    }

    public static JPanel centerWrap(JComponent c) {
        JPanel p = transparentPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p.add(c);
        return p;
    }

    public static JPanel fieldWithIcon(JComponent field, Ikon icon) {
        JPanel p = transparentPanel(new BorderLayout(AppTheme.SPACING_SM, 0));
        JLabel lbl = new JLabel(FontIcon.of(icon, 15, AppTheme.mutedForeground()));
        lbl.setBorder(new EmptyBorder(0, 10, 0, 0));
        p.add(lbl, BorderLayout.WEST);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    public static JPanel roundedCard(LayoutManager layout) {
        JPanel card = new JPanel(layout) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(3, 5, getWidth() - 4, getHeight() - 4, 20, 20);
                g2.setColor(AppTheme.panelBackground());
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 3, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        return card;
    }

    public static JPanel elevatedCard(LayoutManager layout) {
        JPanel card = new JPanel(layout) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                for (int i = 5; i >= 1; i--) {
                    g2.setColor(new Color(0, 0, 0, 5 + (5 - i) * 4));
                    g2.fillRoundRect(i, i + 1, getWidth() - i * 2, getHeight() - i * 2, 18, 18);
                }
                g2.setColor(AppTheme.panelBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        return card;
    }

    public static JSplitPane splitPane(Component left, Component right, int divider) {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setDividerLocation(divider);
        split.setDividerSize(5);
        split.setOpaque(false);
        split.setBorder(null);
        return split;
    }

    public static JLabel pageTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.pageTitle());
        return label;
    }

    public static JLabel statLabel() {
        JLabel label = new JLabel();
        label.setFont(AppTheme.body());
        return label;
    }

    public static JLabel statLabelBold() {
        JLabel label = new JLabel();
        label.setFont(AppTheme.bodyBold());
        return label;
    }

    public static JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.body());
        return label;
    }

    public static JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.sectionTitle());
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    public static JLabel subtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.caption());
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setForeground(AppTheme.mutedForeground());
        return label;
    }

    public static JLabel errorLabel() {
        JLabel label = new JLabel(" ");
        label.setForeground(AppTheme.DANGER);
        label.setFont(AppTheme.caption());
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    public static JLabel hintLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.hint());
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setForeground(AppTheme.hintForeground());
        return label;
    }

    public static JLabel filterLabel(String text) {
        return fieldLabel(text);
    }

    public static JLabel badgeLabel(String text, Color accent) {
        JLabel label = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 28));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        label.setFont(AppTheme.small());
        label.setForeground(accent.darker());
        label.setBorder(new EmptyBorder(3, 10, 3, 10));
        label.setOpaque(false);
        return label;
    }

    public static JTextField textField(int columns) {
        return textField(columns, null);
    }

    public static JTextField textField(int columns, String placeholder) {
        JTextField field = new JTextField(columns);
        if (placeholder != null) field.putClientProperty("JTextField.placeholderText", placeholder);
        field.setFont(AppTheme.field());
        return field;
    }

    public static JTextField tallTextField(String placeholder) {
        JTextField field = textField(0, placeholder);
        field.setPreferredSize(new Dimension(0, AppTheme.FIELD_HEIGHT));
        return field;
    }

    public static JPasswordField passwordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        field.putClientProperty("JTextField.placeholderText", placeholder);
        field.setFont(AppTheme.field());
        field.setPreferredSize(new Dimension(0, AppTheme.FIELD_HEIGHT));
        return field;
    }

    public static JComboBox<String> comboBox(String[] items) {
        JComboBox<String> box = new JComboBox<>(items);
        box.setFont(AppTheme.body());
        return box;
    }

    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setUI(new BasicButtonUI() {
            private boolean hovered = false;
            private boolean pressed = false;

            @Override
            public void installUI(JComponent c) {
                super.installUI(c);
                c.addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true;  c.repaint(); }
                    public void mouseExited(MouseEvent e)  { hovered = false; c.repaint(); }
                    public void mousePressed(MouseEvent e) { pressed = true;  c.repaint(); }
                    public void mouseReleased(MouseEvent e){ pressed = false; c.repaint(); }
                });
            }

            @Override
            public void paint(Graphics g, JComponent c) {
                JButton b = (JButton) c;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                Color top = pressed ? AppTheme.PRIMARY.darker().darker()
                        : hovered ? AppTheme.PRIMARY.darker()
                        : AppTheme.PRIMARY;
                Color bot = pressed ? top : top.darker();

                g2.setPaint(new GradientPaint(0, 0, top, 0, b.getHeight(), bot));
                g2.fillRoundRect(0, 0, b.getWidth(), b.getHeight(), 10, 10);

                if (!pressed) {
                    g2.setColor(new Color(255, 255, 255, 35));
                    g2.fillRoundRect(1, 1, b.getWidth() - 2, b.getHeight() / 2, 9, 9);
                }

                g2.setColor(new Color(0, 0, 0, 40));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, b.getWidth() - 1, b.getHeight() - 1, 10, 10);

                g2.setColor(Color.WHITE);
                g2.setFont(AppTheme.buttonPrimary());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (b.getWidth() - fm.stringWidth(b.getText())) / 2;
                int ty = (b.getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(b.getText(), tx, ty);
                g2.dispose();
            }
        });
        btn.setPreferredSize(new Dimension(0, AppTheme.BUTTON_HEIGHT));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JButton secondaryButton(String text) {
        JButton btn = makeOutlineButton(text, null, AppTheme.NEUTRAL);
        btn.setFont(AppTheme.body());
        return btn;
    }

    public static JButton iconButton(Ikon icon, String tooltip) {
        JButton btn = new JButton(FontIcon.of(icon, 15, AppTheme.labelForeground()));
        btn.setToolTipText(tooltip);
        applyGhostStyle(btn, AppTheme.PRIMARY);
        return btn;
    }

    public static JButton smallButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(AppTheme.small());
        applyGhostStyle(btn, AppTheme.NEUTRAL);
        return btn;
    }

    public static JButton saveButton(String text, Ikon icon) {
        JButton btn = makeOutlineButton(text, icon, AppTheme.PRIMARY);
        return btn;
    }

    public static JButton clearButton() {
        return makeOutlineButton("Clear", FontAwesomeSolid.TIMES, AppTheme.NEUTRAL);
    }

    public static JButton editButton() {
        return makeFilledAccentButton("Edit", FontAwesomeSolid.EDIT, AppTheme.PRIMARY);
    }

    public static JButton deleteButton() {
        return makeFilledAccentButton("Delete", FontAwesomeSolid.TRASH_ALT, AppTheme.DANGER);
    }

    public static JButton accentButton(String text, Ikon icon, Color accent) {
        return makeFilledAccentButton(text, icon, accent);
    }

    private static JButton makeOutlineButton(String text, Ikon icon, Color accent) {
        JButton btn = icon != null
                ? new JButton(text, FontIcon.of(icon, 13, accent))
                : new JButton(text);

        btn.setUI(new BasicButtonUI() {
            private boolean hovered = false;
            {  }
            @Override
            public void installUI(JComponent c) {
                super.installUI(c);
                c.addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true;  c.repaint(); }
                    public void mouseExited(MouseEvent e)  { hovered = false; c.repaint(); }
                });
            }
            @Override
            public void paint(Graphics g, JComponent c) {
                JButton b = (JButton) c;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                if (hovered) {
                    g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 18));
                    g2.fillRoundRect(0, 0, b.getWidth(), b.getHeight(), 8, 8);
                }
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), hovered ? 200 : 130));
                g2.setStroke(new BasicStroke(1.3f));
                g2.drawRoundRect(0, 0, b.getWidth() - 1, b.getHeight() - 1, 8, 8);
                super.paint(g, c);
                g2.dispose();
            }
        });
        btn.setFont(AppTheme.body());
        btn.setForeground(accent);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private static JButton makeFilledAccentButton(String text, Ikon icon, Color accent) {
        JButton btn = icon != null
                ? new JButton(text, FontIcon.of(icon, 13, Color.WHITE))
                : new JButton(text);

        btn.setUI(new BasicButtonUI() {
            private boolean hovered = false;
            private boolean pressed = false;
            @Override
            public void installUI(JComponent c) {
                super.installUI(c);
                c.addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true;  c.repaint(); }
                    public void mouseExited(MouseEvent e)  { hovered = false; c.repaint(); }
                    public void mousePressed(MouseEvent e) { pressed = true;  c.repaint(); }
                    public void mouseReleased(MouseEvent e){ pressed = false; c.repaint(); }
                });
            }
            @Override
            public void paint(Graphics g, JComponent c) {
                JButton b = (JButton) c;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                Color bg = pressed ? accent.darker().darker()
                        : hovered ? accent.darker()
                        : accent;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, b.getWidth(), b.getHeight(), 8, 8);

                if (!pressed) {
                    g2.setColor(new Color(255, 255, 255, 30));
                    g2.fillRoundRect(1, 1, b.getWidth() - 2, b.getHeight() / 2, 7, 7);
                }

                b.setForeground(Color.WHITE);
                super.paint(g, c);
                g2.dispose();
            }
        });
        btn.setFont(AppTheme.bodyBold());
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private static void applyGhostStyle(JButton btn, Color accent) {
        btn.setUI(new BasicButtonUI() {
            private boolean hovered = false;
            @Override
            public void installUI(JComponent c) {
                super.installUI(c);
                c.addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true;  c.repaint(); }
                    public void mouseExited(MouseEvent e)  { hovered = false; c.repaint(); }
                });
            }
            @Override
            public void paint(Graphics g, JComponent c) {
                if (hovered) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 22));
                    g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 8, 8);
                    g2.dispose();
                }
                super.paint(g, c);
            }
        });
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(AppTheme.TABLE_ROW_HEIGHT);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(
                AppTheme.separator().getRed(),
                AppTheme.separator().getGreen(),
                AppTheme.separator().getBlue(), 55));
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(AppTheme.tableBody());
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(new Color(
                AppTheme.PRIMARY.getRed(),
                AppTheme.PRIMARY.getGreen(),
                AppTheme.PRIMARY.getBlue(), 45));
        table.setSelectionForeground(AppTheme.labelForeground());

        JTableHeader header = table.getTableHeader();
        header.setFont(AppTheme.tableHeader());
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 38));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, AppTheme.PRIMARY));

        table.setDefaultRenderer(Object.class, stripedRenderer());
        return table;
    }

    public static JTable createSmallTable(DefaultTableModel model) {
        JTable table = createTable(model);
        table.setRowHeight(28);
        table.setFont(AppTheme.small());
        table.getTableHeader().setFont(AppTheme.tableHeader());
        return table;
    }

    public static void applyStatusColumn(JTable table, int column, Map<String, Color> colors) {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                                                           boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!sel) {
                    setBackground(row % 2 == 0 ? t.getBackground() : AppTheme.stripeRow(t.getBackground()));
                    setForeground(t.getForeground());
                }
                if (col == column && !sel && v != null) {
                    Color c = colors.get(v.toString());
                    if (c != null) setForeground(c);
                }
                return this;
            }
        });
    }

    public static void applyDualColumnColors(JTable table, int statusCol, Map<String, Color> statusColors,
                                             int valueCol, Map<String, Color> valueColors) {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                                                           boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!sel) {
                    setBackground(row % 2 == 0 ? t.getBackground() : AppTheme.stripeRow(t.getBackground()));
                    setForeground(t.getForeground());
                }
                if (!sel && v != null) {
                    if (col == statusCol) {
                        Color c = statusColors.get(v.toString());
                        if (c != null) setForeground(c);
                    } else if (col == valueCol) {
                        Color c = valueColors.get(v.toString());
                        if (c != null) setForeground(c);
                    }
                }
                return this;
            }
        });
    }

    public static DefaultTableCellRenderer stripedRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                                                           boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                if (!sel) {
                    setBackground(row % 2 == 0
                            ? t.getBackground()
                            : AppTheme.stripeRow(UIManager.getColor("Table.background")));
                    setForeground(t.getForeground());
                }
                return this;
            }
        };
    }

    public static void setColumnWidths(JTable table, int[] widths) {
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    public static JScrollPane tableScrollPane(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(
                new Color(AppTheme.separator().getRed(),
                        AppTheme.separator().getGreen(),
                        AppTheme.separator().getBlue(), 90), 1, true));
        scroll.getViewport().setBackground(table.getBackground());
        return scroll;
    }

    public static GridBagConstraints labelConstraints() {
        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(7, AppTheme.SPACING_XS, 7, AppTheme.SPACING_SM);
        lc.gridx = 0;
        return lc;
    }

    public static GridBagConstraints fieldConstraints() {
        GridBagConstraints fc = new GridBagConstraints();
        fc.fill = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1;
        fc.insets = new Insets(7, 0, 7, AppTheme.SPACING_XS);
        fc.gridx = 1;
        return fc;
    }

    public static GridBagConstraints buttonRowConstraints(int row) {
        GridBagConstraints bc = new GridBagConstraints();
        bc.gridx = 0;
        bc.gridy = row;
        bc.gridwidth = 2;
        bc.fill = GridBagConstraints.HORIZONTAL;
        bc.insets = new Insets(AppTheme.SPACING_MD, AppTheme.SPACING_XS, AppTheme.SPACING_XS, AppTheme.SPACING_XS);
        return bc;
    }

    public static void addFormRow(JPanel card, GridBagConstraints lc, GridBagConstraints fc,
                                  int row, String label, JComponent field) {
        lc.gridy = row;
        fc.gridy = row;
        card.add(fieldLabel(label), lc);
        card.add(field, fc);
    }

    public static void showMessage(Component parent, String msg, String title, int type) {
        JOptionPane.showMessageDialog(parent, msg, title, type);
    }

    public static int showConfirm(Component parent, String msg, String title) {
        return JOptionPane.showConfirmDialog(parent, msg, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
    }

    public static Map<String, Color> accountStatusColors() {
        return Map.of("Active", AppTheme.SUCCESS, "Frozen", AppTheme.WARNING, "Closed", AppTheme.DANGER);
    }

    public static Map<String, Color> transactionTypeColors() {
        return Map.of("DEPOSIT", AppTheme.SUCCESS, "WITHDRAW", AppTheme.DANGER, "TRANSFER", AppTheme.PRIMARY);
    }

    public static Map<String, Color> loanStatusColors() {
        return Map.of("Approved", AppTheme.SUCCESS, "Pending", AppTheme.WARNING,
                "Rejected", AppTheme.DANGER, "Closed", AppTheme.NEUTRAL);
    }

    public static Map<String, Color> cardStatusColors() {
        return Map.of("Active", AppTheme.SUCCESS, "Blocked", AppTheme.DANGER, "Expired", AppTheme.NEUTRAL);
    }

    public static Map<String, Color> cardTypeColors() {
        return Map.of("Credit", AppTheme.ACCENT);
    }

    public static Map<String, Color> employeeRoleColors() {
        return Map.of("Admin", AppTheme.DANGER, "Manager", AppTheme.PRIMARY, "Teller", AppTheme.SUCCESS);
    }
}
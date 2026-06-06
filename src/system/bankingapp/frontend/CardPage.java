package system.bankingapp.frontend;

import system.bankingapp.backend.CardBackend;
import system.bankingapp.model.Card;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;

public class CardPage extends JPanel {

    private final CardBackend backend = new CardBackend();

    private JTable            table;
    private DefaultTableModel tableModel;
    private JTextField        searchField;
    private JComboBox<String> typeFilter, statusFilter;
    private JLabel            totalLabel, activeLabel, blockedLabel;

    private JComboBox<String> fldAccount, fldType, fldStatus;
    private JTextField        fldCardNum, fldExpiry;
    private JButton           btnSave, btnClear;
    private int               editingId = -1;

    private ArrayList<String[]> accountList;

    private static final String[] COLUMNS = {"ID","Card Number","Account","Customer","Type","Expiry","Status","Created"};

    public CardPage() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);
        accountList = backend.getAccountList();
        add(buildTopBar(),  BorderLayout.NORTH);
        add(buildCenter(),  BorderLayout.CENTER);
        loadTable(backend.getAllCards());
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout(16, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(18, 24, 10, 24));

        JLabel title = new JLabel("Card Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        totalLabel   = new JLabel();
        activeLabel  = new JLabel();
        blockedLabel = new JLabel();
        totalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        activeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        blockedLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JPanel titleBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleBox.setOpaque(false);
        titleBox.add(title);
        titleBox.add(Box.createHorizontalStrut(14));
        titleBox.add(totalLabel);
        titleBox.add(Box.createHorizontalStrut(12));
        titleBox.add(activeLabel);
        titleBox.add(Box.createHorizontalStrut(12));
        titleBox.add(blockedLabel);

        bar.add(titleBox,         BorderLayout.WEST);
        bar.add(buildFilterBar(), BorderLayout.CENTER);
        return bar;
    }

    private JPanel buildFilterBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        p.setOpaque(false);

        searchField  = new JTextField(14);
        searchField.putClientProperty("JTextField.placeholderText", "Search card no, account, customer…");
        searchField.addActionListener(e -> doSearch());

        typeFilter   = new JComboBox<>(new String[]{"All Types","Debit","Credit"});
        statusFilter = new JComboBox<>(new String[]{"All Status","Active","Blocked","Expired"});
        typeFilter.addActionListener(e   -> doTypeFilter());
        statusFilter.addActionListener(e -> doStatusFilter());

        JButton btnRefresh = new JButton(FontIcon.of(FontAwesomeSolid.SYNC_ALT, 14));
        btnRefresh.setToolTipText("Refresh"); btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> refresh());

        p.add(new JLabel("Search:")); p.add(searchField);
        p.add(new JLabel("Type:"));   p.add(typeFilter);
        p.add(new JLabel("Status:")); p.add(statusFilter);
        p.add(btnRefresh);
        return p;
    }

    private JSplitPane buildCenter() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildForm(), buildTablePanel());
        split.setDividerLocation(300);
        split.setDividerSize(6);
        split.setOpaque(false);
        split.setBorder(null);
        return split;
    }

    private JPanel buildForm() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 16, 16, 8));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(UIManager.getColor("Separator.foreground")),
                        "Card Details", TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 13)),
                new EmptyBorder(10, 14, 14, 14)));
        card.setOpaque(false);

        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(6, 4, 6, 8);
        lc.gridx  = 0;

        GridBagConstraints fc = new GridBagConstraints();
        fc.fill    = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1;
        fc.insets  = new Insets(6, 0, 6, 4);
        fc.gridx   = 1;

        fldAccount = new JComboBox<>();
        fldType    = new JComboBox<>(new String[]{"Debit","Credit"});
        fldStatus  = new JComboBox<>(new String[]{"Active","Blocked","Expired"});
        fldCardNum = new JTextField();
        fldExpiry  = new JTextField();
        fldExpiry.putClientProperty("JTextField.placeholderText", "yyyy-mm-dd");

        for (String[] a : accountList) fldAccount.addItem(a[1]);

        JButton btnGenerate = new JButton("Generate");
        btnGenerate.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnGenerate.setFocusPainted(false);
        btnGenerate.addActionListener(e -> fldCardNum.setText(backend.generateCardNumber()));

        JPanel cardNumPanel = new JPanel(new BorderLayout(4, 0));
        cardNumPanel.setOpaque(false);
        cardNumPanel.add(fldCardNum, BorderLayout.CENTER);
        cardNumPanel.add(btnGenerate, BorderLayout.EAST);

        String[]     labels = {"Account *","Card Number *","Card Type","Expiry Date *","Status"};
        JComponent[] fields = {fldAccount, cardNumPanel, fldType, fldExpiry, fldStatus};

        for (int i = 0; i < labels.length; i++) {
            lc.gridy = i; fc.gridy = i;
            card.add(new JLabel(labels[i]), lc);
            card.add(fields[i], fc);
        }

        btnSave  = new JButton("Add Card", FontIcon.of(FontAwesomeSolid.PLUS_CIRCLE, 14));
        btnClear = new JButton("Clear",    FontIcon.of(FontAwesomeSolid.TIMES, 14));
        btnSave.setFocusPainted(false); btnClear.setFocusPainted(false);
        btnSave.addActionListener(e  -> saveCard());
        btnClear.addActionListener(e -> clearForm());

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 8, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnSave); btnRow.add(btnClear);

        GridBagConstraints bc = new GridBagConstraints();
        bc.gridx = 0; bc.gridy = labels.length;
        bc.gridwidth = 2; bc.fill = GridBagConstraints.HORIZONTAL;
        bc.insets = new Insets(14, 4, 4, 4);
        card.add(btnRow, bc);

        wrapper.add(card, BorderLayout.NORTH);
        return wrapper;
    }

    private JPanel buildTablePanel() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(34);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setReorderingAllowed(false);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                                                           boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!sel) {
                    setBackground(row % 2 == 0 ? t.getBackground() : stripe(t.getBackground()));
                    setForeground(t.getForeground());
                }
                if (col == 6 && !sel) {
                    String val = v != null ? v.toString() : "";
                    switch (val) {
                        case "Active"  -> setForeground(new Color(22,  163, 74));
                        case "Blocked" -> setForeground(new Color(220,  38, 38));
                        case "Expired" -> setForeground(new Color(100, 100, 100));
                    }
                }
                if (col == 4 && !sel) {
                    String val = v != null ? v.toString() : "";
                    setForeground("Credit".equals(val) ? new Color(124, 58, 237) : t.getForeground());
                }
                return this;
            }
        });

        fitColumns(new int[]{40, 160, 100, 140, 70, 100, 75, 140});

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateForm();
        });

        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        actionBar.setOpaque(false);
        JButton btnEdit   = new JButton("Edit",   FontIcon.of(FontAwesomeSolid.EDIT,      14, new Color(37,  99, 235)));
        JButton btnDelete = new JButton("Delete", FontIcon.of(FontAwesomeSolid.TRASH_ALT, 14, new Color(220, 38,  38)));
        btnEdit.setFocusPainted(false); btnDelete.setFocusPainted(false);
        btnEdit.addActionListener(e   -> editSelected());
        btnDelete.addActionListener(e -> deleteSelected());
        actionBar.add(btnEdit); actionBar.add(btnDelete);

        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 8, 16, 16));
        p.add(actionBar,              BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void saveCard() {
        if (fldCardNum.getText().trim().isEmpty() || fldExpiry.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Card number and expiry date required.", "Validation", JOptionPane.WARNING_MESSAGE); return;
        }
        if (accountList.isEmpty()) { JOptionPane.showMessageDialog(this, "No accounts found.", "Error", JOptionPane.ERROR_MESSAGE); return; }

        int    accId = Integer.parseInt(accountList.get(fldAccount.getSelectedIndex())[0]);
        Card   c     = new Card(accId, fldCardNum.getText().trim(), (String) fldType.getSelectedItem(),
                fldExpiry.getText().trim(), (String) fldStatus.getSelectedItem());

        boolean ok;
        if (editingId == -1) {
            ok = backend.insertCard(c);
        } else {
            Card upd = new Card(editingId, accId, null, null, fldCardNum.getText().trim(),
                    (String) fldType.getSelectedItem(), fldExpiry.getText().trim(),
                    (String) fldStatus.getSelectedItem(), null);
            ok = backend.updateCard(upd);
        }

        if (ok) {
            JOptionPane.showMessageDialog(this, editingId == -1 ? "Card added!" : "Card updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); refresh();
        } else {
            JOptionPane.showMessageDialog(this, "Operation failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a card first.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete this card?",
                "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            if (backend.deleteCard(id)) { clearForm(); refresh(); }
            else JOptionPane.showMessageDialog(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelected() {
        if (table.getSelectedRow() < 0) { JOptionPane.showMessageDialog(this, "Select a card first.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
        populateForm();
        btnSave.setText("Update Card");
        btnSave.setIcon(FontIcon.of(FontAwesomeSolid.SAVE, 14));
    }

    private void populateForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        editingId = (int) tableModel.getValueAt(row, 0);
        fldCardNum.setText((String) tableModel.getValueAt(row, 1));
        String accNum = (String) tableModel.getValueAt(row, 2);
        for (int i = 0; i < accountList.size(); i++)
            if (accountList.get(i)[1].equals(accNum)) { fldAccount.setSelectedIndex(i); break; }
        fldType.setSelectedItem(tableModel.getValueAt(row, 4));
        fldExpiry.setText(tableModel.getValueAt(row, 5) != null ? tableModel.getValueAt(row, 5).toString() : "");
        fldStatus.setSelectedItem(tableModel.getValueAt(row, 6));
    }

    private void clearForm() {
        editingId = -1;
        fldCardNum.setText(""); fldExpiry.setText("");
        fldAccount.setSelectedIndex(0); fldType.setSelectedIndex(0); fldStatus.setSelectedIndex(0);
        btnSave.setText("Add Card"); btnSave.setIcon(FontIcon.of(FontAwesomeSolid.PLUS_CIRCLE, 14));
        table.clearSelection();
    }

    private void doSearch()       { String kw = searchField.getText().trim(); loadTable(kw.isEmpty() ? backend.getAllCards() : backend.searchCards(kw)); }
    private void doTypeFilter()   { String t = (String) typeFilter.getSelectedItem();   loadTable(t.startsWith("All") ? backend.getAllCards() : backend.filterByType(t)); }
    private void doStatusFilter() { String s = (String) statusFilter.getSelectedItem(); loadTable(s.startsWith("All") ? backend.getAllCards() : backend.filterByStatus(s)); }

    private void refresh() {
        searchField.setText(""); typeFilter.setSelectedIndex(0); statusFilter.setSelectedIndex(0);
        loadTable(backend.getAllCards());
    }

    private void loadTable(ArrayList<Card> list) {
        tableModel.setRowCount(0);
        for (Card c : list) {
            tableModel.addRow(new Object[]{
                    c.getCardId(), c.getCardNumber(), c.getAccountNumber(),
                    c.getCustomerName(), c.getCardType(), c.getExpiryDate(),
                    c.getStatus(), c.getCreatedAt()
            });
        }
        totalLabel.setText("Total: " + tableModel.getRowCount());
        activeLabel.setText("✓ Active: " + backend.getActiveCount());
        blockedLabel.setText("✗ Blocked: " + backend.getBlockedCount());
    }

    private void fitColumns(int[] widths) {
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
    }

    private Color stripe(Color base) {
        if (base == null) base = Color.WHITE;
        boolean dark = (base.getRed() + base.getGreen() + base.getBlue()) / 3 < 128;
        int d = dark ? 15 : -12;
        return new Color(clamp(base.getRed()+d), clamp(base.getGreen()+d), clamp(base.getBlue()+d));
    }
    private int clamp(int v) { return Math.max(0, Math.min(255, v)); }
}
package system.bankingapp.frontend;

import system.bankingapp.backend.AccountBackend;
import system.bankingapp.model.Account;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;

public class AccountPage extends JPanel {

    private final AccountBackend backend = new AccountBackend();

    private JTable            table;
    private DefaultTableModel tableModel;
    private JTextField        searchField;
    private JComboBox<String> typeFilter, statusFilter;
    private JLabel            totalLabel, balanceLabel;

    private JComboBox<String> fldCustomer, fldBranch, fldType, fldStatus;
    private JTextField        fldAccNum, fldBalance;
    private JButton           btnSave, btnClear;
    private int               editingId = -1;

    private ArrayList<String[]> customerList, branchList;

    private static final String[] COLUMNS = {"ID","Account No","Customer","Branch","Type","Balance","Status","Created"};

    public AccountPage() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);
        loadDropdownData();
        add(buildTopBar(),   BorderLayout.NORTH);
        add(buildCenter(),   BorderLayout.CENTER);
        loadTable(backend.getAllAccounts());
    }

    private void loadDropdownData() {
        customerList = backend.getCustomerList();
        branchList   = backend.getBranchList();
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout(16, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(18, 24, 10, 24));

        totalLabel   = new JLabel();
        balanceLabel = new JLabel();
        totalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel title = new JLabel("Account Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JPanel titleBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleBox.setOpaque(false);
        titleBox.add(title);
        titleBox.add(Box.createHorizontalStrut(14));
        titleBox.add(totalLabel);
        titleBox.add(Box.createHorizontalStrut(18));
        titleBox.add(balanceLabel);

        bar.add(titleBox,         BorderLayout.WEST);
        bar.add(buildFilterBar(), BorderLayout.CENTER);
        return bar;
    }

    private JPanel buildFilterBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        p.setOpaque(false);

        searchField  = new JTextField(14);
        searchField.putClientProperty("JTextField.placeholderText", "Search account no, customer, type…");
        searchField.addActionListener(e -> doSearch());

        typeFilter   = new JComboBox<>(new String[]{"All Types","Savings","Current","Fixed"});
        statusFilter = new JComboBox<>(new String[]{"All Status","Active","Frozen","Closed"});

        typeFilter.addActionListener(e   -> doTypeFilter());
        statusFilter.addActionListener(e -> doStatusFilter());

        JButton btnRefresh = new JButton(FontIcon.of(FontAwesomeSolid.SYNC_ALT, 15));
        btnRefresh.setToolTipText("Refresh");
        btnRefresh.setFocusPainted(false);
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
                        "Account Details", TitledBorder.LEFT, TitledBorder.TOP,
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

        fldCustomer = new JComboBox<>();
        fldBranch   = new JComboBox<>();
        fldAccNum   = new JTextField();
        fldBalance  = new JTextField("0.00");
        fldType     = new JComboBox<>(new String[]{"Savings","Current","Fixed"});
        fldStatus   = new JComboBox<>(new String[]{"Active","Frozen","Closed"});

        for (String[] c : customerList) fldCustomer.addItem(c[1]);
        for (String[] b : branchList)   fldBranch.addItem(b[1]);

        JButton btnGenerate = new JButton("Generate");
        btnGenerate.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnGenerate.setFocusPainted(false);
        btnGenerate.addActionListener(e -> fldAccNum.setText(backend.generateAccountNumber()));

        JPanel accNumPanel = new JPanel(new BorderLayout(4, 0));
        accNumPanel.setOpaque(false);
        accNumPanel.add(fldAccNum,   BorderLayout.CENTER);
        accNumPanel.add(btnGenerate, BorderLayout.EAST);

        String[] labels  = {"Customer *","Branch","Account No *","Type","Balance","Status"};
        JComponent[] flds = {fldCustomer, fldBranch, accNumPanel, fldType, fldBalance, fldStatus};

        for (int i = 0; i < labels.length; i++) {
            lc.gridy = i; fc.gridy = i;
            card.add(new JLabel(labels[i]), lc);
            card.add(flds[i], fc);
        }

        btnSave  = new JButton("Add Account", FontIcon.of(FontAwesomeSolid.PLUS_CIRCLE, 14));
        btnClear = new JButton("Clear",       FontIcon.of(FontAwesomeSolid.TIMES, 14));
        btnSave.setFocusPainted(false);
        btnClear.setFocusPainted(false);
        btnSave.addActionListener(e  -> saveAccount());
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
                    setBackground(row % 2 == 0 ? t.getBackground() : stripedRow(t.getBackground()));
                    setForeground(t.getForeground());
                }

                if (col == 6 && !sel) {
                    String val = v != null ? v.toString() : "";
                    switch (val) {
                        case "Active" -> setForeground(new Color(22, 163, 74));
                        case "Frozen" -> setForeground(new Color(202, 138, 4));
                        case "Closed" -> setForeground(new Color(220, 38, 38));
                    }
                }
                return this;
            }
        });

        fitColumns();

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateFormFromTable();
        });

        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        actionBar.setOpaque(false);

        JButton btnEdit   = new JButton("Edit",   FontIcon.of(FontAwesomeSolid.EDIT,      14, new Color(37,99,235)));
        JButton btnDelete = new JButton("Delete", FontIcon.of(FontAwesomeSolid.TRASH_ALT, 14, new Color(220,38,38)));
        btnEdit.setFocusPainted(false);   btnDelete.setFocusPainted(false);
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

    private void saveAccount() {
        if (fldAccNum.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Account number required.", "Validation", JOptionPane.WARNING_MESSAGE); return;
        }
        int custId   = customerList.isEmpty() ? 0 : Integer.parseInt(customerList.get(fldCustomer.getSelectedIndex())[0]);
        int branchId = branchList.isEmpty()   ? 0 : Integer.parseInt(branchList.get(fldBranch.getSelectedIndex())[0]);
        double bal;
        try { bal = Double.parseDouble(fldBalance.getText().trim()); }
        catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Invalid balance.", "Validation", JOptionPane.WARNING_MESSAGE); return; }

        Account a = new Account(custId, branchId, fldAccNum.getText().trim(),
                (String) fldType.getSelectedItem(), bal, (String) fldStatus.getSelectedItem());

        boolean ok;
        if (editingId == -1) {
            ok = backend.insertAccount(a);
        } else {
            Account upd = new Account(editingId, custId, branchId, null, null,
                    fldAccNum.getText().trim(), (String) fldType.getSelectedItem(),
                    bal, (String) fldStatus.getSelectedItem(), null);
            ok = backend.updateAccount(upd);
        }

        if (ok) {
            JOptionPane.showMessageDialog(this, editingId == -1 ? "Account created!" : "Account updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); refresh();
        } else {
            JOptionPane.showMessageDialog(this, "Operation failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an account first.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
        int id  = (int) tableModel.getValueAt(row, 0);
        String accNum = (String) tableModel.getValueAt(row, 1);
        if (JOptionPane.showConfirmDialog(this, "Delete account \"" + accNum + "\"?\nAll related transactions will be removed.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            if (backend.deleteAccount(id)) { clearForm(); refresh(); }
            else JOptionPane.showMessageDialog(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelected() {
        if (table.getSelectedRow() < 0) { JOptionPane.showMessageDialog(this, "Select an account first.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
        populateFormFromTable();
        btnSave.setText("Update Account");
        btnSave.setIcon(FontIcon.of(FontAwesomeSolid.SAVE, 14));
    }

    private void populateFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        editingId = (int) tableModel.getValueAt(row, 0);
        fldAccNum.setText((String) tableModel.getValueAt(row, 1));
        fldType.setSelectedItem(tableModel.getValueAt(row, 4));
        fldBalance.setText(String.valueOf(tableModel.getValueAt(row, 5)));
        fldStatus.setSelectedItem(tableModel.getValueAt(row, 6));
    }

    private void clearForm() {
        editingId = -1;
        fldAccNum.setText(""); fldBalance.setText("0.00");
        fldCustomer.setSelectedIndex(0); fldBranch.setSelectedIndex(0);
        fldType.setSelectedIndex(0); fldStatus.setSelectedIndex(0);
        btnSave.setText("Add Account");
        btnSave.setIcon(FontIcon.of(FontAwesomeSolid.PLUS_CIRCLE, 14));
        table.clearSelection();
    }

    private void doSearch()       { String kw = searchField.getText().trim(); loadTable(kw.isEmpty() ? backend.getAllAccounts() : backend.searchAccounts(kw)); }
    private void doTypeFilter()   { String t = (String) typeFilter.getSelectedItem();   loadTable(t.startsWith("All") ? backend.getAllAccounts() : backend.filterByType(t)); }
    private void doStatusFilter() { String s = (String) statusFilter.getSelectedItem(); loadTable(s.startsWith("All") ? backend.getAllAccounts() : backend.filterByStatus(s)); }

    private void refresh() {
        searchField.setText(""); typeFilter.setSelectedIndex(0); statusFilter.setSelectedIndex(0);
        loadTable(backend.getAllAccounts());
    }

    private void loadTable(ArrayList<Account> list) {
        tableModel.setRowCount(0);
        for (Account a : list) {
            tableModel.addRow(new Object[]{
                    a.getAccountId(), a.getAccountNumber(), a.getCustomerName(),
                    a.getBranchName(), a.getAccountType(),
                    String.format("%.2f", a.getBalance()),
                    a.getStatus(), a.getCreatedAt()
            });
        }
        totalLabel.setText("Total: " + tableModel.getRowCount() + " accounts");
        balanceLabel.setText("Active Balance: PKR " + String.format("%,.2f", backend.getTotalActiveBalance()));
    }

    private void fitColumns() {
        int[] widths = {45, 110, 150, 120, 80, 110, 70, 150};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
    }

    private Color stripedRow(Color base) {
        if (base == null) base = Color.WHITE;
        boolean dark = (base.getRed() + base.getGreen() + base.getBlue()) / 3 < 128;
        int d = dark ? 15 : -12;
        return new Color(clamp(base.getRed()+d), clamp(base.getGreen()+d), clamp(base.getBlue()+d));
    }
    private int clamp(int v) { return Math.max(0, Math.min(255, v)); }
}
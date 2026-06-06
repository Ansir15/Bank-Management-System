package system.bankingapp.frontend;

import system.bankingapp.backend.TransactionBackend;
import system.bankingapp.model.Transaction;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;

public class TransactionPage extends JPanel {

    private final TransactionBackend backend = new TransactionBackend();

    private JTable            table;
    private DefaultTableModel tableModel;
    private JTextField        searchField, dateFrom, dateTo;
    private JComboBox<String> typeFilter, accountFilter;
    private JLabel            totalLabel, depositLabel, withdrawLabel;

    private JComboBox<String> fldAccount, fldEmployee, fldType;
    private JTextField        fldAmount, fldDesc;
    private JButton           btnSave, btnClear;

    private ArrayList<String[]> accountList, employeeList;

    private static final String[] COLUMNS = {"ID","Account No","Type","Amount","Performed By","Description","Date"};

    public TransactionPage() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);
        loadDropdownData();
        add(buildTopBar(),  BorderLayout.NORTH);
        add(buildCenter(),  BorderLayout.CENTER);
        loadTable(backend.getAllTransactions());
    }

    private void loadDropdownData() {
        accountList  = backend.getAccountList();
        employeeList = backend.getEmployeeList();
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout(16, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(18, 24, 10, 24));

        JLabel title = new JLabel("Transaction Ledger");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        totalLabel    = new JLabel();
        depositLabel  = new JLabel();
        withdrawLabel = new JLabel();
        totalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        depositLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        withdrawLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JPanel titleBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleBox.setOpaque(false);
        titleBox.add(title);
        titleBox.add(Box.createHorizontalStrut(14));
        titleBox.add(totalLabel);
        titleBox.add(Box.createHorizontalStrut(14));
        titleBox.add(depositLabel);
        titleBox.add(Box.createHorizontalStrut(10));
        titleBox.add(withdrawLabel);

        bar.add(titleBox,         BorderLayout.WEST);
        bar.add(buildFilterBar(), BorderLayout.CENTER);
        return bar;
    }

    private JPanel buildFilterBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        p.setOpaque(false);

        searchField = new JTextField(12);
        searchField.putClientProperty("JTextField.placeholderText", "Search account, desc, type…");
        searchField.addActionListener(e -> doSearch());

        typeFilter = new JComboBox<>(new String[]{"All Types","DEPOSIT","WITHDRAW","TRANSFER"});
        typeFilter.addActionListener(e -> doTypeFilter());

        accountFilter = new JComboBox<>();
        accountFilter.addItem("All Accounts");
        for (String[] a : accountList) accountFilter.addItem(a[1]);
        accountFilter.addActionListener(e -> doAccountFilter());

        dateFrom = new JTextField(9);
        dateTo   = new JTextField(9);
        dateFrom.putClientProperty("JTextField.placeholderText", "yyyy-mm-dd");
        dateTo.putClientProperty("JTextField.placeholderText",   "yyyy-mm-dd");

        JButton btnDateFilter = new JButton(FontIcon.of(FontAwesomeSolid.FILTER, 14));
        btnDateFilter.setToolTipText("Apply date filter");
        btnDateFilter.setFocusPainted(false);
        btnDateFilter.addActionListener(e -> doDateFilter());

        JButton btnRefresh = new JButton(FontIcon.of(FontAwesomeSolid.SYNC_ALT, 14));
        btnRefresh.setToolTipText("Refresh");
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> refresh());

        p.add(new JLabel("Search:"));  p.add(searchField);
        p.add(new JLabel("Type:"));    p.add(typeFilter);
        p.add(new JLabel("Account:")); p.add(accountFilter);
        p.add(new JLabel("From:"));    p.add(dateFrom);
        p.add(new JLabel("To:"));      p.add(dateTo);
        p.add(btnDateFilter); p.add(btnRefresh);
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
                        "New Transaction", TitledBorder.LEFT, TitledBorder.TOP,
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

        fldAccount  = new JComboBox<>();
        fldEmployee = new JComboBox<>();
        fldType     = new JComboBox<>(new String[]{"DEPOSIT","WITHDRAW","TRANSFER"});
        fldAmount   = new JTextField("0.00");
        fldDesc     = new JTextField();

        for (String[] a : accountList)  fldAccount.addItem(a[1]);
        for (String[] e : employeeList) fldEmployee.addItem(e[1]);

        String[] labels = {"Account *","Performed By","Type *","Amount *","Description"};
        JComponent[] flds = {fldAccount, fldEmployee, fldType, fldAmount, fldDesc};

        for (int i = 0; i < labels.length; i++) {
            lc.gridy = i; fc.gridy = i;
            card.add(new JLabel(labels[i]), lc);
            card.add(flds[i], fc);
        }

        btnSave  = new JButton("Post Transaction", FontIcon.of(FontAwesomeSolid.CHECK_CIRCLE, 14));
        btnClear = new JButton("Clear",            FontIcon.of(FontAwesomeSolid.TIMES, 14));
        btnSave.setFocusPainted(false); btnClear.setFocusPainted(false);
        btnSave.addActionListener(e  -> saveTransaction());
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

                if (col == 2 && !sel) {
                    String val = v != null ? v.toString() : "";
                    switch (val) {
                        case "DEPOSIT"  -> setForeground(new Color(22,  163, 74));
                        case "WITHDRAW" -> setForeground(new Color(220,  38, 38));
                        case "TRANSFER" -> setForeground(new Color(37,   99,235));
                    }
                }
                return this;
            }
        });

        fitColumns();

        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        actionBar.setOpaque(false);
        JButton btnDelete = new JButton("Delete", FontIcon.of(FontAwesomeSolid.TRASH_ALT, 14, new Color(220,38,38)));
        btnDelete.setFocusPainted(false);
        btnDelete.addActionListener(e -> deleteSelected());
        actionBar.add(btnDelete);

        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 8, 16, 16));
        p.add(actionBar,              BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void saveTransaction() {
        if (accountList.isEmpty()) { JOptionPane.showMessageDialog(this, "No accounts found.", "Error", JOptionPane.ERROR_MESSAGE); return; }
        double amount;
        try { amount = Double.parseDouble(fldAmount.getText().trim()); if (amount <= 0) throw new NumberFormatException(); }
        catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Enter a valid positive amount.", "Validation", JOptionPane.WARNING_MESSAGE); return; }

        int accId  = Integer.parseInt(accountList.get(fldAccount.getSelectedIndex())[0]);
        int empId  = employeeList.isEmpty() ? 0 : Integer.parseInt(employeeList.get(fldEmployee.getSelectedIndex())[0]);

        Transaction t = new Transaction(accId, empId,
                (String) fldType.getSelectedItem(), amount, fldDesc.getText().trim());

        if (backend.insertTransaction(t)) {
            JOptionPane.showMessageDialog(this, "Transaction posted!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); refresh();
        } else {
            JOptionPane.showMessageDialog(this, "Transaction failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a transaction first.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete this transaction record?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            if (backend.deleteTransaction(id)) refresh();
            else JOptionPane.showMessageDialog(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        fldAmount.setText("0.00"); fldDesc.setText("");
        fldAccount.setSelectedIndex(0); fldEmployee.setSelectedIndex(0); fldType.setSelectedIndex(0);
        table.clearSelection();
    }

    private void doSearch()      { String kw = searchField.getText().trim(); loadTable(kw.isEmpty() ? backend.getAllTransactions() : backend.searchTransactions(kw)); }
    private void doTypeFilter()  { String t = (String) typeFilter.getSelectedItem(); loadTable(t.startsWith("All") ? backend.getAllTransactions() : backend.filterByType(t)); }
    private void doAccountFilter() {
        int idx = accountFilter.getSelectedIndex();
        if (idx == 0) { loadTable(backend.getAllTransactions()); return; }
        int accId = Integer.parseInt(accountList.get(idx - 1)[0]);
        loadTable(backend.filterByAccount(accId));
    }
    private void doDateFilter() {
        String f = dateFrom.getText().trim(), t = dateTo.getText().trim();
        if (f.isEmpty() || t.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter both dates.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
        loadTable(backend.filterByDateRange(f + " 00:00:00", t + " 23:59:59"));
    }

    private void refresh() {
        searchField.setText(""); typeFilter.setSelectedIndex(0);
        accountFilter.setSelectedIndex(0); dateFrom.setText(""); dateTo.setText("");
        loadTable(backend.getAllTransactions());
    }

    private void loadTable(ArrayList<Transaction> list) {
        tableModel.setRowCount(0);
        for (Transaction t : list) {
            tableModel.addRow(new Object[]{
                    t.getTransactionId(), t.getAccountNumber(), t.getTransactionType(),
                    String.format("%.2f", t.getAmount()), t.getPerformedByName(),
                    t.getDescription(), t.getTransactionDate()
            });
        }
        totalLabel.setText("Total: " + tableModel.getRowCount());
        depositLabel.setText("↑ Deposits: PKR " + String.format("%,.2f", backend.getTotalDeposits()));
        withdrawLabel.setText("↓ Withdrawals: PKR " + String.format("%,.2f", backend.getTotalWithdrawals()));
    }

    private void fitColumns() {
        int[] widths = {45, 110, 90, 100, 130, 180, 150};
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
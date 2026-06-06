package system.bankingapp.frontend;

import system.bankingapp.backend.LoanBackend;
import system.bankingapp.model.Loan;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;

public class LoanPage extends JPanel {

    private final LoanBackend backend = new LoanBackend();
    private JTable            table;
    private DefaultTableModel tableModel;
    private JTextField        searchField, dateFrom, dateTo;
    private JComboBox<String> statusFilter, typeFilter;
    private JLabel            totalLabel, approvedLabel, pendingLabel, amountLabel;

    private JComboBox<String> fldCustomer, fldType, fldStatus;
    private JTextField        fldPrincipal, fldRate, fldTenure, fldIssued;
    private JLabel            lblEmi, lblTotal;
    private JButton           btnSave, btnClear;
    private int               editingId = -1;

    private ArrayList<String[]> customerList;

    private static final String[] COLUMNS = {"ID","Customer","Type","Principal","Rate %","Tenure","EMI","Total Payable","Status","Issued","Created"};

    public LoanPage() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);
        customerList = backend.getCustomerList();
        add(buildTopBar(),  BorderLayout.NORTH);
        add(buildCenter(),  BorderLayout.CENTER);
        loadTable(backend.getAllLoans());
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout(16, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(18, 24, 10, 24));

        JLabel title = new JLabel("Loan Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        totalLabel    = new JLabel();
        approvedLabel = new JLabel();
        pendingLabel  = new JLabel();
        amountLabel   = new JLabel();
        for (JLabel l : new JLabel[]{totalLabel, approvedLabel, pendingLabel, amountLabel})
            l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        approvedLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JPanel titleBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleBox.setOpaque(false);
        titleBox.add(title);
        titleBox.add(Box.createHorizontalStrut(14));
        titleBox.add(totalLabel);
        titleBox.add(Box.createHorizontalStrut(12));
        titleBox.add(approvedLabel);
        titleBox.add(Box.createHorizontalStrut(12));
        titleBox.add(pendingLabel);
        titleBox.add(Box.createHorizontalStrut(12));
        titleBox.add(amountLabel);

        bar.add(titleBox,         BorderLayout.WEST);
        bar.add(buildFilterBar(), BorderLayout.CENTER);
        return bar;
    }

    private JPanel buildFilterBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        p.setOpaque(false);

        searchField  = new JTextField(12);
        searchField.putClientProperty("JTextField.placeholderText", "Search customer, type, status…");
        searchField.addActionListener(e -> doSearch());

        statusFilter = new JComboBox<>(new String[]{"All Status","Pending","Approved","Rejected","Closed"});
        typeFilter   = new JComboBox<>(new String[]{"All Types","Personal","Home","Car","Business"});
        statusFilter.addActionListener(e -> doStatusFilter());
        typeFilter.addActionListener(e   -> doTypeFilter());

        dateFrom = new JTextField(9);
        dateTo   = new JTextField(9);
        dateFrom.putClientProperty("JTextField.placeholderText", "yyyy-mm-dd");
        dateTo.putClientProperty("JTextField.placeholderText",   "yyyy-mm-dd");

        JButton btnDate    = new JButton(FontIcon.of(FontAwesomeSolid.FILTER, 14));
        JButton btnRefresh = new JButton(FontIcon.of(FontAwesomeSolid.SYNC_ALT, 14));
        btnDate.setToolTipText("Filter by date"); btnDate.setFocusPainted(false);
        btnRefresh.setToolTipText("Refresh");     btnRefresh.setFocusPainted(false);
        btnDate.addActionListener(e    -> doDateFilter());
        btnRefresh.addActionListener(e -> refresh());

        p.add(new JLabel("Search:")); p.add(searchField);
        p.add(new JLabel("Status:")); p.add(statusFilter);
        p.add(new JLabel("Type:"));   p.add(typeFilter);
        p.add(new JLabel("From:"));   p.add(dateFrom);
        p.add(new JLabel("To:"));     p.add(dateTo);
        p.add(btnDate); p.add(btnRefresh);
        return p;
    }

    private JSplitPane buildCenter() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildForm(), buildTablePanel());
        split.setDividerLocation(310);
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
                        "Loan Details", TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 13)),
                new EmptyBorder(10, 14, 14, 14)));
        card.setOpaque(false);

        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(5, 4, 5, 8);
        lc.gridx  = 0;

        GridBagConstraints fc = new GridBagConstraints();
        fc.fill    = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1;
        fc.insets  = new Insets(5, 0, 5, 4);
        fc.gridx   = 1;

        fldCustomer = new JComboBox<>();
        fldType     = new JComboBox<>(new String[]{"Personal","Home","Car","Business"});
        fldStatus   = new JComboBox<>(new String[]{"Pending","Approved","Rejected","Closed"});
        fldPrincipal= new JTextField("0.00");
        fldRate     = new JTextField("0.00");
        fldTenure   = new JTextField("12");
        fldIssued   = new JTextField();
        fldIssued.putClientProperty("JTextField.placeholderText", "yyyy-mm-dd");

        for (String[] c : customerList) fldCustomer.addItem(c[1]);

        lblEmi   = new JLabel("EMI: —");
        lblTotal = new JLabel("Total: —");
        lblEmi.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JButton btnCalc = new JButton("Calculate");
        btnCalc.setFocusPainted(false);
        btnCalc.addActionListener(e -> calculateEmi());

        String[]     labels = {"Customer *","Loan Type","Principal *","Interest Rate %","Tenure (months)","Status","Issued Date"};
        JComponent[] fields = {fldCustomer, fldType, fldPrincipal, fldRate, fldTenure, fldStatus, fldIssued};

        for (int i = 0; i < labels.length; i++) {
            lc.gridy = i; fc.gridy = i;
            card.add(new JLabel(labels[i]), lc);
            card.add(fields[i], fc);
        }

        GridBagConstraints cc = new GridBagConstraints();
        cc.gridx = 0; cc.gridy = labels.length;
        cc.gridwidth = 2; cc.fill = GridBagConstraints.HORIZONTAL;
        cc.insets = new Insets(6, 4, 2, 4);
        card.add(btnCalc, cc);

        cc.gridy = labels.length + 1; cc.insets = new Insets(2, 4, 2, 4);
        JPanel emiPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        emiPanel.setOpaque(false);
        emiPanel.add(lblEmi); emiPanel.add(lblTotal);
        card.add(emiPanel, cc);

        btnSave  = new JButton("Add Loan",  FontIcon.of(FontAwesomeSolid.PLUS_CIRCLE, 14));
        btnClear = new JButton("Clear",     FontIcon.of(FontAwesomeSolid.TIMES, 14));
        btnSave.setFocusPainted(false); btnClear.setFocusPainted(false);
        btnSave.addActionListener(e  -> saveLoan());
        btnClear.addActionListener(e -> clearForm());

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 8, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnSave); btnRow.add(btnClear);

        cc.gridy = labels.length + 2; cc.insets = new Insets(12, 4, 4, 4);
        card.add(btnRow, cc);

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
                if (col == 8 && !sel) {
                    String val = v != null ? v.toString() : "";
                    switch (val) {
                        case "Approved" -> setForeground(new Color(22, 163, 74));
                        case "Pending"  -> setForeground(new Color(202, 138, 4));
                        case "Rejected" -> setForeground(new Color(220, 38, 38));
                        case "Closed"   -> setForeground(new Color(100, 100, 100));
                    }
                }
                return this;
            }
        });

        fitColumns(new int[]{40,130,80,100,65,70,100,110,80,90,140});

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateForm();
        });

        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        actionBar.setOpaque(false);
        JButton btnEdit   = new JButton("Edit",   FontIcon.of(FontAwesomeSolid.EDIT,      14, new Color(37, 99, 235)));
        JButton btnDelete = new JButton("Delete", FontIcon.of(FontAwesomeSolid.TRASH_ALT, 14, new Color(220, 38, 38)));
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

    private void calculateEmi() {
        try {
            double principal = Double.parseDouble(fldPrincipal.getText().trim());
            double rate      = Double.parseDouble(fldRate.getText().trim());
            int    tenure    = Integer.parseInt(fldTenure.getText().trim());
            Loan   temp      = new Loan(0, "", principal, rate, tenure, "", "");
            lblEmi.setText(String.format("EMI: PKR %,.2f", temp.getMonthlyInstallment()));
            lblTotal.setText(String.format("Total: PKR %,.2f", temp.getTotalPayable()));
        } catch (NumberFormatException ex) {
            lblEmi.setText("EMI: invalid input");
        }
    }

    private void saveLoan() {
        try {
            double principal = Double.parseDouble(fldPrincipal.getText().trim());
            double rate      = Double.parseDouble(fldRate.getText().trim());
            int    tenure    = Integer.parseInt(fldTenure.getText().trim());
            int    custId    = Integer.parseInt(customerList.get(fldCustomer.getSelectedIndex())[0]);

            Loan l = new Loan(custId, (String) fldType.getSelectedItem(), principal,
                    rate, tenure, (String) fldStatus.getSelectedItem(), fldIssued.getText().trim());

            boolean ok;
            if (editingId == -1) {
                ok = backend.insertLoan(l);
            } else {
                Loan upd = new Loan(editingId, custId, null, (String) fldType.getSelectedItem(),
                        principal, rate, tenure, (String) fldStatus.getSelectedItem(), fldIssued.getText().trim(), null);
                ok = backend.updateLoan(upd);
            }

            if (ok) {
                JOptionPane.showMessageDialog(this, editingId == -1 ? "Loan added!" : "Loan updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm(); refresh();
            } else {
                JOptionPane.showMessageDialog(this, "Operation failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid numbers for Principal, Rate and Tenure.", "Validation", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a loan first.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete this loan record?",
                "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            if (backend.deleteLoan(id)) { clearForm(); refresh(); }
            else JOptionPane.showMessageDialog(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelected() {
        if (table.getSelectedRow() < 0) { JOptionPane.showMessageDialog(this, "Select a loan first.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
        populateForm();
        btnSave.setText("Update Loan");
        btnSave.setIcon(FontIcon.of(FontAwesomeSolid.SAVE, 14));
    }

    private void populateForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        editingId = (int) tableModel.getValueAt(row, 0);
        String custName = (String) tableModel.getValueAt(row, 1);
        for (int i = 0; i < customerList.size(); i++)
            if (customerList.get(i)[1].equals(custName)) { fldCustomer.setSelectedIndex(i); break; }
        fldType.setSelectedItem(tableModel.getValueAt(row, 2));
        fldPrincipal.setText(tableModel.getValueAt(row, 3).toString());
        fldRate.setText(tableModel.getValueAt(row, 4).toString());
        fldTenure.setText(tableModel.getValueAt(row, 5).toString());
        fldStatus.setSelectedItem(tableModel.getValueAt(row, 8));
        fldIssued.setText(tableModel.getValueAt(row, 9) != null ? tableModel.getValueAt(row, 9).toString() : "");
        calculateEmi();
    }

    private void clearForm() {
        editingId = -1;
        fldPrincipal.setText("0.00"); fldRate.setText("0.00"); fldTenure.setText("12"); fldIssued.setText("");
        fldCustomer.setSelectedIndex(0); fldType.setSelectedIndex(0); fldStatus.setSelectedIndex(0);
        lblEmi.setText("EMI: —"); lblTotal.setText("Total: —");
        btnSave.setText("Add Loan"); btnSave.setIcon(FontIcon.of(FontAwesomeSolid.PLUS_CIRCLE, 14));
        table.clearSelection();
    }

    private void doSearch()       { String kw = searchField.getText().trim(); loadTable(kw.isEmpty() ? backend.getAllLoans() : backend.searchLoans(kw)); }
    private void doStatusFilter() { String s = (String) statusFilter.getSelectedItem(); loadTable(s.startsWith("All") ? backend.getAllLoans() : backend.filterByStatus(s)); }
    private void doTypeFilter()   { String t = (String) typeFilter.getSelectedItem();   loadTable(t.startsWith("All") ? backend.getAllLoans() : backend.filterByType(t)); }
    private void doDateFilter() {
        String f = dateFrom.getText().trim(), t = dateTo.getText().trim();
        if (f.isEmpty() || t.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter both dates.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
        loadTable(backend.filterByDateRange(f + " 00:00:00", t + " 23:59:59"));
    }
    private void refresh() {
        searchField.setText(""); statusFilter.setSelectedIndex(0); typeFilter.setSelectedIndex(0);
        dateFrom.setText(""); dateTo.setText("");
        loadTable(backend.getAllLoans());
    }

    private void loadTable(ArrayList<Loan> list) {
        tableModel.setRowCount(0);
        for (Loan l : list) {
            tableModel.addRow(new Object[]{
                    l.getLoanId(), l.getCustomerName(), l.getLoanType(),
                    String.format("%.2f", l.getPrincipalAmount()),
                    String.format("%.2f", l.getInterestRate()),
                    l.getTenureMonths(),
                    String.format("%.2f", l.getMonthlyInstallment()),
                    String.format("%.2f", l.getTotalPayable()),
                    l.getStatus(), l.getIssuedDate(), l.getCreatedAt()
            });
        }
        totalLabel.setText("Total: " + tableModel.getRowCount());
        approvedLabel.setText("✓ Approved: " + backend.getApprovedCount());
        pendingLabel.setText("⏳ Pending: " + backend.getPendingCount());
        amountLabel.setText("Portfolio: PKR " + String.format("%,.2f", backend.getTotalApprovedAmount()));
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
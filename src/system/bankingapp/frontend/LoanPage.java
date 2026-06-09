package system.bankingapp.frontend;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import system.bankingapp.backend.LoanBackend;
import system.bankingapp.model.Loan;
import uifactory.AppTheme;
import uifactory.UIFactory;

import javax.swing.*;
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
        JPanel bar = UIFactory.topBar();
        totalLabel    = UIFactory.statLabel();
        approvedLabel = UIFactory.statLabelBold();
        pendingLabel  = UIFactory.statLabel();
        amountLabel   = UIFactory.statLabelBold();
        bar.add(UIFactory.titleGroup(UIFactory.pageTitle("Loan Management"),
                totalLabel, approvedLabel, pendingLabel, amountLabel), BorderLayout.WEST);
        bar.add(buildFilterBar(), BorderLayout.CENTER);
        return bar;
    }

    private JPanel buildFilterBar() {
        JPanel p = UIFactory.filterBar();

        searchField  = UIFactory.textField(12, "Search customer, type, status…");
        searchField.addActionListener(e -> doSearch());

        statusFilter = UIFactory.comboBox(new String[]{"All Status","Pending","Approved","Rejected","Closed"});
        typeFilter   = UIFactory.comboBox(new String[]{"All Types","Personal","Home","Car","Business"});
        statusFilter.addActionListener(e -> doStatusFilter());
        typeFilter.addActionListener(e   -> doTypeFilter());

        dateFrom = UIFactory.textField(9, "yyyy-mm-dd");
        dateTo   = UIFactory.textField(9, "yyyy-mm-dd");

        JButton btnDate    = UIFactory.iconButton(FontAwesomeSolid.FILTER, "Filter by date");
        JButton btnRefresh = UIFactory.iconButton(FontAwesomeSolid.SYNC_ALT, "Refresh");
        btnDate.addActionListener(e    -> doDateFilter());
        btnRefresh.addActionListener(e -> refresh());

        p.add(UIFactory.filterLabel("Search:")); p.add(searchField);
        p.add(UIFactory.filterLabel("Status:")); p.add(statusFilter);
        p.add(UIFactory.filterLabel("Type:"));   p.add(typeFilter);
        p.add(UIFactory.filterLabel("From:"));   p.add(dateFrom);
        p.add(UIFactory.filterLabel("To:"));     p.add(dateTo);
        p.add(btnDate); p.add(btnRefresh);
        return p;
    }

    private JSplitPane buildCenter() {
        return UIFactory.splitPane(buildForm(), buildTablePanel(), 310);
    }

    private JPanel buildForm() {
        JPanel wrapper = UIFactory.formWrapper();
        JPanel card = UIFactory.formCardShell("Loan Details");

        GridBagConstraints lc = UIFactory.labelConstraints();
        GridBagConstraints fc = UIFactory.fieldConstraints();

        fldCustomer = UIFactory.comboBox(new String[]{});
        fldType     = UIFactory.comboBox(new String[]{"Personal","Home","Car","Business"});
        fldStatus   = UIFactory.comboBox(new String[]{"Pending","Approved","Rejected","Closed"});
        fldPrincipal= UIFactory.textField(0);
        fldPrincipal.setText("0.00");
        fldRate     = UIFactory.textField(0);
        fldRate.setText("0.00");
        fldTenure   = UIFactory.textField(0);
        fldTenure.setText("12");
        fldIssued   = UIFactory.textField(0, "yyyy-mm-dd");

        for (String[] c : customerList) fldCustomer.addItem(c[1]);

        lblEmi   = new JLabel("EMI: —");
        lblTotal = new JLabel("Total: —");
        lblEmi.setFont(AppTheme.tableHeader());
        lblTotal.setFont(AppTheme.tableHeader());

        JButton btnCalc = UIFactory.smallButton("Calculate");
        btnCalc.addActionListener(e -> calculateEmi());

        String[]     labels = {"Customer *","Loan Type","Principal *","Interest Rate %","Tenure (months)","Status","Issued Date"};
        JComponent[] fields = {fldCustomer, fldType, fldPrincipal, fldRate, fldTenure, fldStatus, fldIssued};

        for (int i = 0; i < labels.length; i++) {
            UIFactory.addFormRow(card, lc, fc, i, labels[i], fields[i]);
        }

        GridBagConstraints cc = new GridBagConstraints();
        cc.gridx = 0; cc.gridy = labels.length;
        cc.gridwidth = 2; cc.fill = GridBagConstraints.HORIZONTAL;
        cc.insets = new Insets(6, AppTheme.SPACING_XS, 2, AppTheme.SPACING_XS);
        card.add(btnCalc, cc);

        cc.gridy = labels.length + 1; cc.insets = new Insets(2, AppTheme.SPACING_XS, 2, AppTheme.SPACING_XS);
        JPanel emiPanel = UIFactory.transparentPanel(new GridLayout(1, 2, 10, 0));
        emiPanel.add(lblEmi); emiPanel.add(lblTotal);
        card.add(emiPanel, cc);

        btnSave  = UIFactory.saveButton("Add Loan", FontAwesomeSolid.PLUS_CIRCLE);
        btnClear = UIFactory.clearButton();
        btnSave.addActionListener(e  -> saveLoan());
        btnClear.addActionListener(e -> clearForm());

        card.add(UIFactory.buttonRow(btnSave, btnClear), UIFactory.buttonRowConstraints(labels.length + 2));
        wrapper.add(card, BorderLayout.NORTH);
        return wrapper;
    }

    private JPanel buildTablePanel() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = UIFactory.createTable(tableModel);
        UIFactory.applyStatusColumn(table, 8, UIFactory.loanStatusColors());
        UIFactory.setColumnWidths(table, new int[]{40,130,80,100,65,70,100,110,80,90,140});

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateForm();
        });

        JPanel actionBar = UIFactory.actionBar();
        JButton btnEdit   = UIFactory.editButton();
        JButton btnDelete = UIFactory.deleteButton();
        btnEdit.addActionListener(e   -> editSelected());
        btnDelete.addActionListener(e -> deleteSelected());
        actionBar.add(btnEdit); actionBar.add(btnDelete);

        return UIFactory.tablePanel(actionBar, new JScrollPane(table));
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
                UIFactory.showMessage(this, editingId == -1 ? "Loan added!" : "Loan updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm(); refresh();
            } else {
                UIFactory.showMessage(this, "Operation failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            UIFactory.showMessage(this, "Enter valid numbers for Principal, Rate and Tenure.", "Validation", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UIFactory.showMessage(this, "Select a loan first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        if (UIFactory.showConfirm(this, "Delete this loan record?", "Confirm") == JOptionPane.YES_OPTION) {
            if (backend.deleteLoan(id)) { clearForm(); refresh(); }
            else UIFactory.showMessage(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelected() {
        if (table.getSelectedRow() < 0) {
            UIFactory.showMessage(this, "Select a loan first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
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
        if (f.isEmpty() || t.isEmpty()) {
            UIFactory.showMessage(this, "Enter both dates.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
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
}

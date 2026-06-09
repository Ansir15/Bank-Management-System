package system.bankingapp.frontend;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import system.bankingapp.backend.TransactionBackend;
import system.bankingapp.model.Transaction;
import uifactory.UIFactory;

import javax.swing.*;
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
        JPanel bar = UIFactory.topBar();
        totalLabel    = UIFactory.statLabel();
        depositLabel  = UIFactory.statLabelBold();
        withdrawLabel = UIFactory.statLabelBold();
        bar.add(UIFactory.titleGroup(UIFactory.pageTitle("Transaction Ledger"),
                totalLabel, depositLabel, withdrawLabel), BorderLayout.WEST);
        bar.add(buildFilterBar(), BorderLayout.CENTER);
        return bar;
    }

    private JPanel buildFilterBar() {
        JPanel p = UIFactory.filterBar();

        searchField = UIFactory.textField(12, "Search account, desc, type…");
        searchField.addActionListener(e -> doSearch());

        typeFilter = UIFactory.comboBox(new String[]{"All Types","DEPOSIT","WITHDRAW","TRANSFER"});
        typeFilter.addActionListener(e -> doTypeFilter());

        accountFilter = UIFactory.comboBox(new String[]{});
        accountFilter.addItem("All Accounts");
        for (String[] a : accountList) accountFilter.addItem(a[1]);
        accountFilter.addActionListener(e -> doAccountFilter());

        dateFrom = UIFactory.textField(9, "yyyy-mm-dd");
        dateTo   = UIFactory.textField(9, "yyyy-mm-dd");

        JButton btnDateFilter = UIFactory.iconButton(FontAwesomeSolid.FILTER, "Apply date filter");
        btnDateFilter.addActionListener(e -> doDateFilter());

        JButton btnRefresh = UIFactory.iconButton(FontAwesomeSolid.SYNC_ALT, "Refresh");
        btnRefresh.addActionListener(e -> refresh());

        p.add(UIFactory.filterLabel("Search:"));  p.add(searchField);
        p.add(UIFactory.filterLabel("Type:"));    p.add(typeFilter);
        p.add(UIFactory.filterLabel("Account:")); p.add(accountFilter);
        p.add(UIFactory.filterLabel("From:"));    p.add(dateFrom);
        p.add(UIFactory.filterLabel("To:"));      p.add(dateTo);
        p.add(btnDateFilter); p.add(btnRefresh);
        return p;
    }

    private JSplitPane buildCenter() {
        return UIFactory.splitPane(buildForm(), buildTablePanel(), 300);
    }

    private JPanel buildForm() {
        JPanel wrapper = UIFactory.formWrapper();
        JPanel card = UIFactory.formCardShell("New Transaction");

        GridBagConstraints lc = UIFactory.labelConstraints();
        GridBagConstraints fc = UIFactory.fieldConstraints();

        fldAccount  = UIFactory.comboBox(new String[]{});
        fldEmployee = UIFactory.comboBox(new String[]{});
        fldType     = UIFactory.comboBox(new String[]{"DEPOSIT","WITHDRAW","TRANSFER"});
        fldAmount   = UIFactory.textField(0);
        fldAmount.setText("0.00");
        fldDesc     = UIFactory.textField(0);

        for (String[] a : accountList)  fldAccount.addItem(a[1]);
        for (String[] e : employeeList) fldEmployee.addItem(e[1]);

        String[] labels = {"Account *","Performed By","Type *","Amount *","Description"};
        JComponent[] flds = {fldAccount, fldEmployee, fldType, fldAmount, fldDesc};

        for (int i = 0; i < labels.length; i++) {
            UIFactory.addFormRow(card, lc, fc, i, labels[i], flds[i]);
        }

        btnSave  = UIFactory.saveButton("Post Transaction", FontAwesomeSolid.CHECK_CIRCLE);
        btnClear = UIFactory.clearButton();
        btnSave.addActionListener(e  -> saveTransaction());
        btnClear.addActionListener(e -> clearForm());

        card.add(UIFactory.buttonRow(btnSave, btnClear), UIFactory.buttonRowConstraints(labels.length));
        wrapper.add(card, BorderLayout.NORTH);
        return wrapper;
    }

    private JPanel buildTablePanel() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = UIFactory.createTable(tableModel);
        UIFactory.applyStatusColumn(table, 2, UIFactory.transactionTypeColors());
        UIFactory.setColumnWidths(table, new int[]{45, 110, 90, 100, 130, 180, 150});

        JPanel actionBar = UIFactory.actionBar();
        JButton btnDelete = UIFactory.deleteButton();
        btnDelete.addActionListener(e -> deleteSelected());
        actionBar.add(btnDelete);

        return UIFactory.tablePanel(actionBar, new JScrollPane(table));
    }

    private void saveTransaction() {
        if (accountList.isEmpty()) {
            UIFactory.showMessage(this, "No accounts found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        double amount;
        try { amount = Double.parseDouble(fldAmount.getText().trim()); if (amount <= 0) throw new NumberFormatException(); }
        catch (NumberFormatException ex) {
            UIFactory.showMessage(this, "Enter a valid positive amount.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int accId  = Integer.parseInt(accountList.get(fldAccount.getSelectedIndex())[0]);
        int empId  = employeeList.isEmpty() ? 0 : Integer.parseInt(employeeList.get(fldEmployee.getSelectedIndex())[0]);

        Transaction t = new Transaction(accId, empId,
                (String) fldType.getSelectedItem(), amount, fldDesc.getText().trim());

        if (backend.insertTransaction(t)) {
            UIFactory.showMessage(this, "Transaction posted!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); refresh();
        } else {
            UIFactory.showMessage(this, "Transaction failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UIFactory.showMessage(this, "Select a transaction first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        if (UIFactory.showConfirm(this, "Delete this transaction record?", "Confirm Delete") == JOptionPane.YES_OPTION) {
            if (backend.deleteTransaction(id)) refresh();
            else UIFactory.showMessage(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
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
        if (f.isEmpty() || t.isEmpty()) {
            UIFactory.showMessage(this, "Enter both dates.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
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
}

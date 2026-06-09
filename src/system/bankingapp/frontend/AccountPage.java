package system.bankingapp.frontend;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import system.bankingapp.backend.AccountBackend;
import system.bankingapp.model.Account;
import uifactory.UIFactory;

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

    private static final String[] COLUMNS = {"ID", "Account No", "Customer", "Branch", "Type", "Balance", "Status", "Created"};

    public AccountPage() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);
        loadDropdownData();
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        loadTable(backend.getAllAccounts());
    }

    private void loadDropdownData() {
        customerList = backend.getCustomerList();
        branchList = backend.getBranchList();
    }

    private JPanel buildTopBar() {
        JPanel bar = UIFactory.topBar();
        totalLabel = UIFactory.statLabel();
        balanceLabel = UIFactory.statLabelBold();
        bar.add(UIFactory.titleGroup(UIFactory.pageTitle("Account Management"), totalLabel, balanceLabel), BorderLayout.WEST);
        bar.add(buildFilterBar(), BorderLayout.CENTER);
        return bar;
    }

    private JPanel buildFilterBar() {
        JPanel p = UIFactory.filterBar();

        searchField = UIFactory.textField(14, "Search account no, customer, type…");
        searchField.addActionListener(e -> doSearch());

        typeFilter = UIFactory.comboBox(new String[]{"All Types", "Savings", "Current", "Fixed"});
        statusFilter = UIFactory.comboBox(new String[]{"All Status", "Active", "Frozen", "Closed"});
        typeFilter.addActionListener(e -> doTypeFilter());
        statusFilter.addActionListener(e -> doStatusFilter());

        JButton btnRefresh = UIFactory.iconButton(FontAwesomeSolid.SYNC_ALT, "Refresh");
        btnRefresh.addActionListener(e -> refresh());

        p.add(UIFactory.filterLabel("Search:"));
        p.add(searchField);
        p.add(UIFactory.filterLabel("Type:"));
        p.add(typeFilter);
        p.add(UIFactory.filterLabel("Status:"));
        p.add(statusFilter);
        p.add(btnRefresh);
        return p;
    }

    private JSplitPane buildCenter() {
        return UIFactory.splitPane(buildForm(), buildTablePanel(), 300);
    }

    private JPanel buildForm() {
        JPanel wrapper = UIFactory.formWrapper();
        JPanel card = UIFactory.formCardShell("Account Details");

        GridBagConstraints lc = UIFactory.labelConstraints();
        GridBagConstraints fc = UIFactory.fieldConstraints();

        fldCustomer = UIFactory.comboBox(new String[]{});
        fldBranch = UIFactory.comboBox(new String[]{});
        fldAccNum = UIFactory.textField(0);
        fldBalance = UIFactory.textField(0);
        fldBalance.setText("0.00");
        fldType = UIFactory.comboBox(new String[]{"Savings", "Current", "Fixed"});
        fldStatus = UIFactory.comboBox(new String[]{"Active", "Frozen", "Closed"});

        for (String[] c : customerList) fldCustomer.addItem(c[1]);
        for (String[] b : branchList) fldBranch.addItem(b[1]);

        JButton btnGenerate = UIFactory.smallButton("Generate");
        btnGenerate.addActionListener(e -> fldAccNum.setText(backend.generateAccountNumber()));

        JPanel accNumPanel = UIFactory.transparentPanel(new BorderLayout(4, 0));
        accNumPanel.add(fldAccNum, BorderLayout.CENTER);
        accNumPanel.add(btnGenerate, BorderLayout.EAST);

        String[] labels = {"Customer *", "Branch", "Account No *", "Type", "Balance", "Status"};
        JComponent[] flds = {fldCustomer, fldBranch, accNumPanel, fldType, fldBalance, fldStatus};

        for (int i = 0; i < labels.length; i++) {
            UIFactory.addFormRow(card, lc, fc, i, labels[i], flds[i]);
        }

        btnSave = UIFactory.saveButton("Add Account", FontAwesomeSolid.PLUS_CIRCLE);
        btnClear = UIFactory.clearButton();
        btnSave.addActionListener(e -> saveAccount());
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
        UIFactory.applyStatusColumn(table, 6, UIFactory.accountStatusColors());
        UIFactory.setColumnWidths(table, new int[]{45, 110, 150, 120, 80, 110, 70, 150});

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateFormFromTable();
        });

        JPanel actionBar = UIFactory.actionBar();
        JButton btnEdit = UIFactory.editButton();
        JButton btnDelete = UIFactory.deleteButton();
        btnEdit.addActionListener(e -> editSelected());
        btnDelete.addActionListener(e -> deleteSelected());
        actionBar.add(btnEdit);
        actionBar.add(btnDelete);

        return UIFactory.tablePanel(actionBar, new JScrollPane(table));
    }

    private void saveAccount() {
        if (fldAccNum.getText().trim().isEmpty()) {
            UIFactory.showMessage(this, "Account number required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int custId = customerList.isEmpty() ? 0 : Integer.parseInt(customerList.get(fldCustomer.getSelectedIndex())[0]);
        int branchId = branchList.isEmpty() ? 0 : Integer.parseInt(branchList.get(fldBranch.getSelectedIndex())[0]);
        double bal;
        try {
            bal = Double.parseDouble(fldBalance.getText().trim());
        } catch (NumberFormatException ex) {
            UIFactory.showMessage(this, "Invalid balance.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

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
            UIFactory.showMessage(this, editingId == -1 ? "Account created!" : "Account updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            refresh();
        } else {
            UIFactory.showMessage(this, "Operation failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UIFactory.showMessage(this, "Select an account first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        String accNum = (String) tableModel.getValueAt(row, 1);
        if (UIFactory.showConfirm(this, "Delete account \"" + accNum + "\"?\nAll related transactions will be removed.", "Confirm Delete") == JOptionPane.YES_OPTION) {
            if (backend.deleteAccount(id)) {
                clearForm();
                refresh();
            } else {
                UIFactory.showMessage(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editSelected() {
        if (table.getSelectedRow() < 0) {
            UIFactory.showMessage(this, "Select an account first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
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
        fldAccNum.setText("");
        fldBalance.setText("0.00");
        fldCustomer.setSelectedIndex(0);
        fldBranch.setSelectedIndex(0);
        fldType.setSelectedIndex(0);
        fldStatus.setSelectedIndex(0);
        btnSave.setText("Add Account");
        btnSave.setIcon(FontIcon.of(FontAwesomeSolid.PLUS_CIRCLE, 14));
        table.clearSelection();
    }

    private void doSearch() {
        String kw = searchField.getText().trim();
        loadTable(kw.isEmpty() ? backend.getAllAccounts() : backend.searchAccounts(kw));
    }

    private void doTypeFilter() {
        String t = (String) typeFilter.getSelectedItem();
        loadTable(t.startsWith("All") ? backend.getAllAccounts() : backend.filterByType(t));
    }

    private void doStatusFilter() {
        String s = (String) statusFilter.getSelectedItem();
        loadTable(s.startsWith("All") ? backend.getAllAccounts() : backend.filterByStatus(s));
    }

    private void refresh() {
        searchField.setText("");
        typeFilter.setSelectedIndex(0);
        statusFilter.setSelectedIndex(0);
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
}

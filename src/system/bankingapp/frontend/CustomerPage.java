package system.bankingapp.frontend;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import system.bankingapp.backend.CustomerBackend;
import system.bankingapp.model.Customers;
import uifactory.UIFactory;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;

public class CustomerPage extends JPanel {

    private final CustomerBackend backend = new CustomerBackend();

    private JTable            table;
    private DefaultTableModel tableModel;
    private JTextField        searchField;
    private JComboBox<String> genderFilter;
    private JTextField        dateFrom;
    private JTextField        dateTo;
    private JLabel            totalLabel;

    private JTextField        fldName, fldEmail, fldPhone, fldAddress, fldCnic, fldDob;
    private JComboBox<String> fldGender;
    private JButton           btnSave, btnClear;
    private int               editingId = -1;

    private static final String[] COLUMNS = {"ID", "Full Name", "Email", "Phone", "CNIC", "DOB", "Gender", "Joined"};

    public CustomerPage() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        loadTable(backend.getAllCustomers());
    }

    private JPanel buildTopBar() {
        JPanel bar = UIFactory.topBar();
        totalLabel = UIFactory.statLabel();
        bar.add(UIFactory.titleGroup(UIFactory.pageTitle("Customer Management"), totalLabel), BorderLayout.WEST);
        bar.add(buildFilterBar(), BorderLayout.CENTER);
        return bar;
    }

    private JPanel buildFilterBar() {
        JPanel p = UIFactory.filterBar();

        searchField = UIFactory.textField(14, "Search name, email, phone, CNIC…");
        searchField.addActionListener(e -> doSearch());

        genderFilter = UIFactory.comboBox(new String[]{"All", "Male", "Female", "Other"});
        genderFilter.addActionListener(e -> doFilter());

        dateFrom = UIFactory.textField(9, "From yyyy-mm-dd");
        dateTo = UIFactory.textField(9, "To yyyy-mm-dd");

        JButton btnDateFilter = UIFactory.iconButton(FontAwesomeSolid.FILTER, "Filter by Date");
        btnDateFilter.addActionListener(e -> doDateFilter());

        JButton btnRefresh = UIFactory.iconButton(FontAwesomeSolid.SYNC_ALT, "Refresh");
        btnRefresh.addActionListener(e -> refresh());

        p.add(UIFactory.filterLabel("Search:"));
        p.add(searchField);
        p.add(UIFactory.filterLabel("Gender:"));
        p.add(genderFilter);
        p.add(UIFactory.filterLabel("From:"));
        p.add(dateFrom);
        p.add(UIFactory.filterLabel("To:"));
        p.add(dateTo);
        p.add(btnDateFilter);
        p.add(btnRefresh);
        return p;
    }

    private JSplitPane buildCenter() {
        return UIFactory.splitPane(buildForm(), buildTablePanel(), 310);
    }

    private JPanel buildForm() {
        JPanel wrapper = UIFactory.formWrapper();
        JPanel card = UIFactory.formCardShell("Customer Details");

        GridBagConstraints lc = UIFactory.labelConstraints();
        GridBagConstraints fc = UIFactory.fieldConstraints();

        fldName = UIFactory.textField(0);
        fldEmail = UIFactory.textField(0);
        fldPhone = UIFactory.textField(0);
        fldCnic = UIFactory.textField(0);
        fldDob = UIFactory.textField(0);
        fldAddress = UIFactory.textField(0);
        fldGender = UIFactory.comboBox(new String[]{"Male", "Female", "Other"});

        String[] labels = {"Full Name *", "Email *", "Phone", "CNIC", "Date of Birth", "Gender", "Address"};
        JComponent[] fields = {fldName, fldEmail, fldPhone, fldCnic, fldDob, fldGender, fldAddress};

        for (int i = 0; i < fields.length; i++) {
            UIFactory.addFormRow(card, lc, fc, i, labels[i], fields[i]);
        }

        btnSave = UIFactory.saveButton("Add Customer", FontAwesomeSolid.USER_PLUS);
        btnSave.addActionListener(e -> saveCustomer());
        btnClear = UIFactory.clearButton();
        btnClear.addActionListener(e -> clearForm());

        card.add(UIFactory.buttonRow(btnSave, btnClear), UIFactory.buttonRowConstraints(fields.length));
        wrapper.add(card, BorderLayout.NORTH);
        return wrapper;
    }

    private JPanel buildTablePanel() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = UIFactory.createTable(tableModel);
        UIFactory.setColumnWidths(table, new int[]{45, 150, 180, 110, 130, 95, 70, 150});

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

    private void saveCustomer() {
        String name = fldName.getText().trim();
        String email = fldEmail.getText().trim();
        if (name.isEmpty() || email.isEmpty()) {
            UIFactory.showMessage(this, "Full Name and Email are required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Customers c = new Customers(
                name, email,
                fldPhone.getText().trim(),
                fldAddress.getText().trim(),
                fldCnic.getText().trim(),
                fldDob.getText().trim(),
                (String) fldGender.getSelectedItem()
        );

        boolean ok;
        if (editingId == -1) {
            ok = backend.insertCustomer(c);
        } else {
            c = new Customers(editingId, name, email,
                    fldPhone.getText().trim(), fldAddress.getText().trim(),
                    fldCnic.getText().trim(), fldDob.getText().trim(),
                    (String) fldGender.getSelectedItem(), null);
            ok = backend.updateCustomer(c);
        }

        if (ok) {
            UIFactory.showMessage(this, editingId == -1 ? "Customer added!" : "Customer updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            refresh();
        } else {
            UIFactory.showMessage(this, "Operation failed. Check console.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UIFactory.showMessage(this, "Select a customer first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        String n = (String) tableModel.getValueAt(row, 1);
        if (UIFactory.showConfirm(this, "Delete customer \"" + n + "\"?", "Confirm Delete") == JOptionPane.YES_OPTION) {
            if (backend.deleteCustomer(id)) {
                refresh();
                clearForm();
            } else {
                UIFactory.showMessage(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UIFactory.showMessage(this, "Select a customer first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        populateFormFromTable();
        btnSave.setText("Update Customer");
        btnSave.setIcon(FontIcon.of(FontAwesomeSolid.SAVE, 14));
    }

    private void populateFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        editingId = (int) tableModel.getValueAt(row, 0);
        fldName.setText((String) tableModel.getValueAt(row, 1));
        fldEmail.setText((String) tableModel.getValueAt(row, 2));
        fldPhone.setText((String) tableModel.getValueAt(row, 3));
        fldCnic.setText((String) tableModel.getValueAt(row, 4));
        fldDob.setText((String) tableModel.getValueAt(row, 5));
        fldGender.setSelectedItem(tableModel.getValueAt(row, 6));
        fldAddress.setText("");
    }

    private void clearForm() {
        editingId = -1;
        fldName.setText("");
        fldEmail.setText("");
        fldPhone.setText("");
        fldAddress.setText("");
        fldCnic.setText("");
        fldDob.setText("");
        fldGender.setSelectedIndex(0);
        btnSave.setText("Add Customer");
        btnSave.setIcon(FontIcon.of(FontAwesomeSolid.USER_PLUS, 14));
        table.clearSelection();
    }

    private void doSearch() {
        String kw = searchField.getText().trim();
        loadTable(kw.isEmpty() ? backend.getAllCustomers() : backend.searchCustomers(kw));
    }

    private void doFilter() {
        String g = (String) genderFilter.getSelectedItem();
        loadTable("All".equals(g) ? backend.getAllCustomers() : backend.filterByGender(g));
    }

    private void doDateFilter() {
        String f = dateFrom.getText().trim(), t = dateTo.getText().trim();
        if (f.isEmpty() || t.isEmpty()) {
            UIFactory.showMessage(this, "Enter both From and To dates.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        loadTable(backend.filterByDateRange(f + " 00:00:00", t + " 23:59:59"));
    }

    private void refresh() {
        searchField.setText("");
        genderFilter.setSelectedIndex(0);
        dateFrom.setText("");
        dateTo.setText("");
        loadTable(backend.getAllCustomers());
    }

    private void loadTable(ArrayList<Customers> list) {
        tableModel.setRowCount(0);
        for (Customers c : list) {
            tableModel.addRow(new Object[]{
                    c.getCustomerId(), c.getCustomerFullName(), c.getEmail(),
                    c.getPhone(), c.getCnic(), c.getDateOfBirth(),
                    c.getGender(), c.getCreatedAt()
            });
        }
        totalLabel.setText("Total: " + tableModel.getRowCount() + " customers");
    }
}

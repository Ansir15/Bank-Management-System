package system.bankingapp.frontend;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import system.bankingapp.backend.EmployeeBackend;
import system.bankingapp.model.Employee;
import uifactory.UIFactory;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;

public class EmployeePage extends JPanel {

    private final EmployeeBackend backend = new EmployeeBackend();

    private JTable            table;
    private DefaultTableModel tableModel;
    private JTextField        searchField, dateFrom, dateTo;
    private JComboBox<String> roleFilter;
    private JLabel            totalLabel, adminLabel, tellerLabel, salaryLabel;

    private JTextField        fldName, fldEmail, fldPhone, fldSalary, fldHireDate;
    private JComboBox<String> fldRole;
    private JButton           btnSave, btnClear;
    private int               editingId = -1;

    private static final String[] COLUMNS = {"ID","Full Name","Email","Phone","Role","Salary","Hire Date","Created"};

    public EmployeePage() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);
        add(buildTopBar(),  BorderLayout.NORTH);
        add(buildCenter(),  BorderLayout.CENTER);
        loadTable(backend.getAllEmployees());
    }

    private JPanel buildTopBar() {
        JPanel bar = UIFactory.topBar();
        totalLabel  = UIFactory.statLabel();
        adminLabel  = UIFactory.statLabelBold();
        tellerLabel = UIFactory.statLabelBold();
        salaryLabel = UIFactory.statLabelBold();
        bar.add(UIFactory.titleGroup(UIFactory.pageTitle("Employee Management"),
                totalLabel, adminLabel, tellerLabel, salaryLabel), BorderLayout.WEST);
        bar.add(buildFilterBar(), BorderLayout.CENTER);
        return bar;
    }

    private JPanel buildFilterBar() {
        JPanel p = UIFactory.filterBar();

        searchField = UIFactory.textField(14, "Search name, email, phone, role…");
        searchField.addActionListener(e -> doSearch());

        roleFilter = UIFactory.comboBox(new String[]{"All Roles","Admin","Manager","Teller"});
        roleFilter.addActionListener(e -> doRoleFilter());

        dateFrom = UIFactory.textField(9, "yyyy-mm-dd");
        dateTo   = UIFactory.textField(9, "yyyy-mm-dd");

        JButton btnDate    = UIFactory.iconButton(FontAwesomeSolid.FILTER, "Filter by date");
        JButton btnRefresh = UIFactory.iconButton(FontAwesomeSolid.SYNC_ALT, "Refresh");
        btnDate.addActionListener(e    -> doDateFilter());
        btnRefresh.addActionListener(e -> refresh());

        p.add(UIFactory.filterLabel("Search:")); p.add(searchField);
        p.add(UIFactory.filterLabel("Role:"));   p.add(roleFilter);
        p.add(UIFactory.filterLabel("From:"));   p.add(dateFrom);
        p.add(UIFactory.filterLabel("To:"));     p.add(dateTo);
        p.add(btnDate); p.add(btnRefresh);
        return p;
    }

    private JSplitPane buildCenter() {
        return UIFactory.splitPane(buildForm(), buildTablePanel(), 300);
    }

    private JPanel buildForm() {
        JPanel wrapper = UIFactory.formWrapper();
        JPanel card = UIFactory.formCardShell("Employee Details");

        GridBagConstraints lc = UIFactory.labelConstraints();
        GridBagConstraints fc = UIFactory.fieldConstraints();

        fldName     = UIFactory.textField(0);
        fldEmail    = UIFactory.textField(0);
        fldPhone    = UIFactory.textField(0);
        fldSalary   = UIFactory.textField(0);
        fldSalary.setText("0.00");
        fldHireDate = UIFactory.textField(0, "yyyy-mm-dd");
        fldRole     = UIFactory.comboBox(new String[]{"Admin","Manager","Teller"});

        String[]     labels = {"Full Name *","Email *","Phone","Role","Salary","Hire Date"};
        JComponent[] fields = {fldName, fldEmail, fldPhone, fldRole, fldSalary, fldHireDate};

        for (int i = 0; i < labels.length; i++) {
            UIFactory.addFormRow(card, lc, fc, i, labels[i], fields[i]);
        }

        btnSave  = UIFactory.saveButton("Add Employee", FontAwesomeSolid.USER_PLUS);
        btnClear = UIFactory.clearButton();
        btnSave.addActionListener(e  -> saveEmployee());
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
        UIFactory.applyStatusColumn(table, 4, UIFactory.employeeRoleColors());
        UIFactory.setColumnWidths(table, new int[]{45, 150, 180, 110, 80, 100, 100, 150});

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

    private void saveEmployee() {
        String name  = fldName.getText().trim();
        String email = fldEmail.getText().trim();
        if (name.isEmpty() || email.isEmpty()) {
            UIFactory.showMessage(this, "Name and Email are required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double salary;
        try { salary = Double.parseDouble(fldSalary.getText().trim()); }
        catch (NumberFormatException ex) {
            UIFactory.showMessage(this, "Enter valid salary.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok;
        if (editingId == -1) {
            Employee e = new Employee(name, email, fldPhone.getText().trim(),
                    (String) fldRole.getSelectedItem(), salary, fldHireDate.getText().trim());
            ok = backend.insertEmployee(e);
        } else {
            Employee e = new Employee(editingId, name, email, fldPhone.getText().trim(),
                    (String) fldRole.getSelectedItem(), salary, fldHireDate.getText().trim(), null);
            ok = backend.updateEmployee(e);
        }

        if (ok) {
            UIFactory.showMessage(this, editingId == -1 ? "Employee added!" : "Employee updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); refresh();
        } else {
            UIFactory.showMessage(this, "Operation failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UIFactory.showMessage(this, "Select an employee first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        if (UIFactory.showConfirm(this, "Delete employee \"" + name + "\"?", "Confirm") == JOptionPane.YES_OPTION) {
            if (backend.deleteEmployee(id)) { clearForm(); refresh(); }
            else UIFactory.showMessage(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelected() {
        if (table.getSelectedRow() < 0) {
            UIFactory.showMessage(this, "Select an employee first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        populateForm();
        btnSave.setText("Update Employee");
        btnSave.setIcon(FontIcon.of(FontAwesomeSolid.SAVE, 14));
    }

    private void populateForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        editingId = (int) tableModel.getValueAt(row, 0);
        fldName.setText((String) tableModel.getValueAt(row, 1));
        fldEmail.setText((String) tableModel.getValueAt(row, 2));
        fldPhone.setText((String) tableModel.getValueAt(row, 3));
        fldRole.setSelectedItem(tableModel.getValueAt(row, 4));
        fldSalary.setText(tableModel.getValueAt(row, 5).toString());
        fldHireDate.setText(tableModel.getValueAt(row, 6) != null ? tableModel.getValueAt(row, 6).toString() : "");
    }

    private void clearForm() {
        editingId = -1;
        fldName.setText(""); fldEmail.setText(""); fldPhone.setText("");
        fldSalary.setText("0.00"); fldHireDate.setText("");
        fldRole.setSelectedIndex(0);
        btnSave.setText("Add Employee");
        btnSave.setIcon(FontIcon.of(FontAwesomeSolid.USER_PLUS, 14));
        table.clearSelection();
    }

    private void doSearch()     { String kw = searchField.getText().trim(); loadTable(kw.isEmpty() ? backend.getAllEmployees() : backend.searchEmployees(kw)); }
    private void doRoleFilter() { String r = (String) roleFilter.getSelectedItem(); loadTable(r.startsWith("All") ? backend.getAllEmployees() : backend.filterByRole(r)); }
    private void doDateFilter() {
        String f = dateFrom.getText().trim(), t = dateTo.getText().trim();
        if (f.isEmpty() || t.isEmpty()) {
            UIFactory.showMessage(this, "Enter both dates.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        loadTable(backend.filterByDateRange(f + " 00:00:00", t + " 23:59:59"));
    }
    private void refresh() {
        searchField.setText(""); roleFilter.setSelectedIndex(0);
        dateFrom.setText(""); dateTo.setText("");
        loadTable(backend.getAllEmployees());
    }

    private void loadTable(ArrayList<Employee> list) {
        tableModel.setRowCount(0);
        for (Employee e : list) {
            tableModel.addRow(new Object[]{
                    e.getEmployeeId(), e.getFullName(), e.getEmail(), e.getPhone(),
                    e.getRole(), String.format("%.2f", e.getSalary()),
                    e.getHireDate(), e.getCreatedAt()
            });
        }
        totalLabel.setText("Total: " + tableModel.getRowCount());
        adminLabel.setText("Admins: " + backend.getAdminCount());
        tellerLabel.setText("Tellers: " + backend.getTellerCount());
        salaryLabel.setText("Avg Salary: PKR " + String.format("%,.2f", backend.getAvgSalary()));
    }
}

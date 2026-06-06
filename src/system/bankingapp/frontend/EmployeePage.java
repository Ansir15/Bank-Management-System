package system.bankingapp.frontend;

import system.bankingapp.backend.EmployeeBackend;
import system.bankingapp.model.Employee;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.border.*;
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
        JPanel bar = new JPanel(new BorderLayout(16, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(18, 24, 10, 24));

        JLabel title = new JLabel("Employee Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        totalLabel  = new JLabel();
        adminLabel  = new JLabel();
        tellerLabel = new JLabel();
        salaryLabel = new JLabel();
        totalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        adminLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tellerLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        salaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JPanel titleBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleBox.setOpaque(false);
        titleBox.add(title);
        titleBox.add(Box.createHorizontalStrut(14));
        titleBox.add(totalLabel);
        titleBox.add(Box.createHorizontalStrut(12));
        titleBox.add(adminLabel);
        titleBox.add(Box.createHorizontalStrut(12));
        titleBox.add(tellerLabel);
        titleBox.add(Box.createHorizontalStrut(12));
        titleBox.add(salaryLabel);

        bar.add(titleBox,         BorderLayout.WEST);
        bar.add(buildFilterBar(), BorderLayout.CENTER);
        return bar;
    }

    private JPanel buildFilterBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        p.setOpaque(false);

        searchField = new JTextField(14);
        searchField.putClientProperty("JTextField.placeholderText", "Search name, email, phone, role…");
        searchField.addActionListener(e -> doSearch());

        roleFilter = new JComboBox<>(new String[]{"All Roles","Admin","Manager","Teller"});
        roleFilter.addActionListener(e -> doRoleFilter());

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
        p.add(new JLabel("Role:"));   p.add(roleFilter);
        p.add(new JLabel("From:"));   p.add(dateFrom);
        p.add(new JLabel("To:"));     p.add(dateTo);
        p.add(btnDate); p.add(btnRefresh);
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
                        "Employee Details", TitledBorder.LEFT, TitledBorder.TOP,
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

        fldName     = new JTextField();
        fldEmail    = new JTextField();
        fldPhone    = new JTextField();
        fldSalary   = new JTextField("0.00");
        fldHireDate = new JTextField();
        fldRole     = new JComboBox<>(new String[]{"Admin","Manager","Teller"});
        fldHireDate.putClientProperty("JTextField.placeholderText", "yyyy-mm-dd");

        String[]     labels = {"Full Name *","Email *","Phone","Role","Salary","Hire Date"};
        JComponent[] fields = {fldName, fldEmail, fldPhone, fldRole, fldSalary, fldHireDate};

        for (int i = 0; i < labels.length; i++) {
            lc.gridy = i; fc.gridy = i;
            card.add(new JLabel(labels[i]), lc);
            card.add(fields[i], fc);
        }

        btnSave  = new JButton("Add Employee", FontIcon.of(FontAwesomeSolid.USER_PLUS, 14));
        btnClear = new JButton("Clear",        FontIcon.of(FontAwesomeSolid.TIMES, 14));
        btnSave.setFocusPainted(false); btnClear.setFocusPainted(false);
        btnSave.addActionListener(e  -> saveEmployee());
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
                if (col == 4 && !sel) {
                    String val = v != null ? v.toString() : "";
                    switch (val) {
                        case "Admin"   -> setForeground(new Color(220, 38,  38));
                        case "Manager" -> setForeground(new Color(37,  99, 235));
                        case "Teller"  -> setForeground(new Color(22, 163,  74));
                    }
                }
                return this;
            }
        });

        fitColumns(new int[]{45, 150, 180, 110, 80, 100, 100, 150});

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

    private void saveEmployee() {
        String name  = fldName.getText().trim();
        String email = fldEmail.getText().trim();
        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Email are required.", "Validation", JOptionPane.WARNING_MESSAGE); return;
        }
        double salary;
        try { salary = Double.parseDouble(fldSalary.getText().trim()); }
        catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Enter valid salary.", "Validation", JOptionPane.WARNING_MESSAGE); return; }

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
            JOptionPane.showMessageDialog(this, editingId == -1 ? "Employee added!" : "Employee updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); refresh();
        } else {
            JOptionPane.showMessageDialog(this, "Operation failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an employee first.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        if (JOptionPane.showConfirmDialog(this, "Delete employee \"" + name + "\"?",
                "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            if (backend.deleteEmployee(id)) { clearForm(); refresh(); }
            else JOptionPane.showMessageDialog(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelected() {
        if (table.getSelectedRow() < 0) { JOptionPane.showMessageDialog(this, "Select an employee first.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
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
        if (f.isEmpty() || t.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter both dates.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
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
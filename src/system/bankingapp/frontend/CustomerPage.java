package system.bankingapp.frontend;

import system.bankingapp.backend.CustomerBackend;
import system.bankingapp.model.Customers;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class CustomerPage extends JPanel {

    private final CustomerBackend backend = new CustomerBackend();

    private JTable          table;
    private DefaultTableModel tableModel;
    private JTextField      searchField;
    private JComboBox<String> genderFilter;
    private JTextField      dateFrom;
    private JTextField      dateTo;
    private JLabel          totalLabel;

    private JTextField      fldName, fldEmail, fldPhone, fldAddress, fldCnic, fldDob;
    private JComboBox<String> fldGender;
    private JButton         btnSave, btnClear;
    private int             editingId = -1;

    private static final String[] COLUMNS = {"ID","Full Name","Email","Phone","CNIC","DOB","Gender","Joined"};

    public CustomerPage() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);
        add(buildTopBar(),    BorderLayout.NORTH);
        add(buildCenter(),    BorderLayout.CENTER);
        loadTable(backend.getAllCustomers());
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout(16, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(18, 24, 10, 24));

        JLabel title = new JLabel("Customer Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        totalLabel = new JLabel();
        totalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JPanel titleBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleBox.setOpaque(false);
        titleBox.add(title);
        titleBox.add(Box.createHorizontalStrut(14));
        titleBox.add(totalLabel);

        bar.add(titleBox,         BorderLayout.WEST);
        bar.add(buildFilterBar(), BorderLayout.CENTER);
        return bar;
    }

    private JPanel buildFilterBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        p.setOpaque(false);

        searchField = new JTextField(14);
        searchField.putClientProperty("JTextField.placeholderText", "Search name, email, phone, CNIC…");
        searchField.addActionListener(e -> doSearch());

        genderFilter = new JComboBox<>(new String[]{"All","Male","Female","Other"});
        genderFilter.addActionListener(e -> doFilter());

        dateFrom = new JTextField(9);
        dateFrom.putClientProperty("JTextField.placeholderText", "From yyyy-mm-dd");

        dateTo = new JTextField(9);
        dateTo.putClientProperty("JTextField.placeholderText", "To yyyy-mm-dd");

        JButton btnDateFilter = iconBtn(FontAwesomeSolid.FILTER, "Filter by Date");
        btnDateFilter.addActionListener(e -> doDateFilter());

        JButton btnRefresh = iconBtn(FontAwesomeSolid.SYNC_ALT, "Refresh");
        btnRefresh.addActionListener(e -> refresh());

        p.add(new JLabel("Search:"));
        p.add(searchField);
        p.add(new JLabel("Gender:"));
        p.add(genderFilter);
        p.add(new JLabel("From:"));
        p.add(dateFrom);
        p.add(new JLabel("To:"));
        p.add(dateTo);
        p.add(btnDateFilter);
        p.add(btnRefresh);
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
                        "Customer Details", TitledBorder.LEFT, TitledBorder.TOP,
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

        fldName    = new JTextField();
        fldEmail   = new JTextField();
        fldPhone   = new JTextField();
        fldCnic    = new JTextField();
        fldDob     = new JTextField();
        fldAddress = new JTextField();
        fldGender  = new JComboBox<>(new String[]{"Male","Female","Other"});

        String[][] rows = {
                {"Full Name *", null}, {"Email *", null}, {"Phone", null},
                {"CNIC", null},        {"Date of Birth", null},
                {"Gender", null},      {"Address", null}
        };
        JComponent[] fields = {fldName, fldEmail, fldPhone, fldCnic, fldDob, fldGender, fldAddress};

        for (int i = 0; i < fields.length; i++) {
            lc.gridy = i; fc.gridy = i;
            card.add(new JLabel(rows[i][0]), lc);
            card.add(fields[i], fc);
        }

        btnSave  = new JButton("Add Customer");
        btnSave.setIcon(FontIcon.of(FontAwesomeSolid.USER_PLUS, 14));
        btnSave.addActionListener(e -> saveCustomer());

        btnClear = new JButton("Clear");
        btnClear.setIcon(FontIcon.of(FontAwesomeSolid.TIMES, 14));
        btnClear.addActionListener(e -> clearForm());

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 8, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnSave);
        btnRow.add(btnClear);

        GridBagConstraints bc = new GridBagConstraints();
        bc.gridx = 0; bc.gridy = fields.length;
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
                    setBackground(row % 2 == 0
                            ? t.getBackground()
                            : blendWithBackground(UIManager.getColor("Table.background"), 0.04f));
                }
                return this;
            }
        });

        fitColumns();

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateFormFromTable();
        });

        JPanel actionBar = buildTableActionBar();

        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 8, 16, 16));
        p.add(actionBar,               BorderLayout.NORTH);
        p.add(new JScrollPane(table),  BorderLayout.CENTER);
        return p;
    }

    private JPanel buildTableActionBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        p.setOpaque(false);

        JButton btnEdit   = iconTextBtn(FontAwesomeSolid.EDIT,       "Edit",   new Color(37,99,235));
        JButton btnDelete = iconTextBtn(FontAwesomeSolid.TRASH_ALT,  "Delete", new Color(220,38,38));

        btnEdit.addActionListener(e -> editSelected());
        btnDelete.addActionListener(e -> deleteSelected());

        p.add(btnEdit);
        p.add(btnDelete);
        return p;
    }

    private void saveCustomer() {
        String name  = fldName.getText().trim();
        String email = fldEmail.getText().trim();
        if (name.isEmpty() || email.isEmpty()) {
            showMsg("Full Name and Email are required.", "Validation", JOptionPane.WARNING_MESSAGE);
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
            showMsg(editingId == -1 ? "Customer added!" : "Customer updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            refresh();
        } else {
            showMsg("Operation failed. Check console.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { showMsg("Select a customer first.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
        int id   = (int) tableModel.getValueAt(row, 0);
        String n = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete customer \"" + n + "\"?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (backend.deleteCustomer(id)) { refresh(); clearForm(); }
            else showMsg("Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { showMsg("Select a customer first.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
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
        fldName.setText(""); fldEmail.setText(""); fldPhone.setText("");
        fldAddress.setText(""); fldCnic.setText(""); fldDob.setText("");
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
        if (f.isEmpty() || t.isEmpty()) { showMsg("Enter both From and To dates.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
        loadTable(backend.filterByDateRange(f + " 00:00:00", t + " 23:59:59"));
    }

    private void refresh() {
        searchField.setText("");
        genderFilter.setSelectedIndex(0);
        dateFrom.setText(""); dateTo.setText("");
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

    private void fitColumns() {
        int[] widths = {45, 150, 180, 110, 130, 95, 70, 150};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
    }

    private Color blendWithBackground(Color base, float factor) {
        if (base == null) base = Color.WHITE;
        boolean dark = (base.getRed() + base.getGreen() + base.getBlue()) / 3 < 128;
        return dark
                ? new Color(Math.min(255, base.getRed() + (int)(255 * factor)),
                Math.min(255, base.getGreen() + (int)(255 * factor)),
                Math.min(255, base.getBlue() + (int)(255 * factor)))
                : new Color(Math.max(0, base.getRed() - (int)(255 * factor)),
                Math.max(0, base.getGreen() - (int)(255 * factor)),
                Math.max(0, base.getBlue() - (int)(255 * factor)));
    }

    private JButton iconBtn(org.kordamp.ikonli.Ikon icon, String tooltip) {
        JButton b = new JButton(FontIcon.of(icon, 15));
        b.setToolTipText(tooltip);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton iconTextBtn(org.kordamp.ikonli.Ikon icon, String text, Color fg) {
        JButton b = new JButton(text, FontIcon.of(icon, 14, fg));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void showMsg(String msg, String title, int type) {
        JOptionPane.showMessageDialog(this, msg, title, type);
    }
}
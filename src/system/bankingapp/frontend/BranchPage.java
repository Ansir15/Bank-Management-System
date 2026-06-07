package system.bankingapp.frontend;

import system.bankingapp.backend.BranchBackend;
import system.bankingapp.model.Branch;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;

public class BranchPage extends JPanel {

    private final BranchBackend backend = new BranchBackend();

    private JTable            table;
    private DefaultTableModel tableModel;
    private DefaultTableModel accountsPerBranchModel;
    private JTextField        searchField;
    private JComboBox<String> cityFilter;
    private JLabel            totalLabel, accountsLabel;

    private JTextField fldName, fldCode, fldCity, fldAddress, fldPhone;
    private JButton    btnSave, btnClear;
    private int        editingId = -1;

    private static final String[] COLUMNS = {"ID","Branch Name","Code","City","Address","Phone","Created"};

    public BranchPage() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);
        add(buildTopBar(),  BorderLayout.NORTH);
        add(buildCenter(),  BorderLayout.CENTER);
        loadTable(backend.getAllBranches());
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout(16, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(18, 24, 10, 24));

        JLabel title = new JLabel("Branch Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        totalLabel    = new JLabel();
        accountsLabel = new JLabel();
        totalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        accountsLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JPanel titleBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleBox.setOpaque(false);
        titleBox.add(title);
        titleBox.add(Box.createHorizontalStrut(14));
        titleBox.add(totalLabel);
        titleBox.add(Box.createHorizontalStrut(12));
        titleBox.add(accountsLabel);

        bar.add(titleBox,         BorderLayout.WEST);
        bar.add(buildFilterBar(), BorderLayout.CENTER);
        return bar;
    }

    private JPanel buildFilterBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        p.setOpaque(false);

        searchField = new JTextField(14);
        searchField.putClientProperty("JTextField.placeholderText", "Search name, code, city…");
        searchField.addActionListener(e -> doSearch());

        cityFilter = new JComboBox<>();
        cityFilter.addItem("All Cities");
        backend.getDistinctCities().forEach(cityFilter::addItem);
        cityFilter.addActionListener(e -> doCityFilter());

        JButton btnRefresh = new JButton(FontIcon.of(FontAwesomeSolid.SYNC_ALT, 14));
        btnRefresh.setToolTipText("Refresh"); btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> refresh());

        p.add(new JLabel("Search:")); p.add(searchField);
        p.add(new JLabel("City:"));   p.add(cityFilter);
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
                        "Branch Details", TitledBorder.LEFT, TitledBorder.TOP,
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
        fldCode    = new JTextField();
        fldCity    = new JTextField();
        fldAddress = new JTextField();
        fldPhone   = new JTextField();

        String[]     labels = {"Branch Name *","Branch Code *","City","Address","Phone"};
        JComponent[] fields = {fldName, fldCode, fldCity, fldAddress, fldPhone};

        for (int i = 0; i < labels.length; i++) {
            lc.gridy = i; fc.gridy = i;
            card.add(new JLabel(labels[i]), lc);
            card.add(fields[i], fc);
        }

        btnSave  = new JButton("Add Branch", FontIcon.of(FontAwesomeSolid.PLUS_CIRCLE, 14));
        btnClear = new JButton("Clear",      FontIcon.of(FontAwesomeSolid.TIMES, 14));
        btnSave.setFocusPainted(false); btnClear.setFocusPainted(false);
        btnSave.addActionListener(e  -> saveBranch());
        btnClear.addActionListener(e -> clearForm());

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 8, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnSave); btnRow.add(btnClear);

        GridBagConstraints bc = new GridBagConstraints();
        bc.gridx = 0; bc.gridy = labels.length;
        bc.gridwidth = 2; bc.fill = GridBagConstraints.HORIZONTAL;
        bc.insets = new Insets(14, 4, 4, 4);
        card.add(btnRow, bc);

        JPanel accountsPerBranch = buildAccountsPerBranchPanel();
        GridBagConstraints ac = new GridBagConstraints();
        ac.gridx = 0; ac.gridy = labels.length + 1;
        ac.gridwidth = 2; ac.fill = GridBagConstraints.HORIZONTAL;
        ac.insets = new Insets(14, 4, 4, 4);
        card.add(accountsPerBranch, ac);

        wrapper.add(card, BorderLayout.NORTH);
        return wrapper;
    }

    private JPanel buildAccountsPerBranchPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIManager.getColor("Separator.foreground")),
                "Accounts per Branch", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12)));

        accountsPerBranchModel = new DefaultTableModel(new String[]{"Branch", "Accounts"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable t = new JTable(accountsPerBranchModel);
        t.setRowHeight(26);
        t.setShowVerticalLines(false);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        refreshAccountsPerBranch();

        p.add(new JScrollPane(t), BorderLayout.CENTER);
        p.setPreferredSize(new Dimension(0, 130));
        return p;
    }

    private void refreshAccountsPerBranch() {
        if (accountsPerBranchModel == null) return;
        accountsPerBranchModel.setRowCount(0);
        for (String[] row : backend.getAccountsPerBranch())
            accountsPerBranchModel.addRow(row);
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
                return this;
            }
        });

        fitColumns(new int[]{45, 150, 80, 90, 200, 110, 150});

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

    private void saveBranch() {
        String name = fldName.getText().trim();
        String code = fldCode.getText().trim();
        if (name.isEmpty() || code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Branch Name and Code required.", "Validation", JOptionPane.WARNING_MESSAGE); return;
        }
        boolean ok;
        if (editingId == -1) {
            ok = backend.insertBranch(new Branch(name, code, fldCity.getText().trim(), fldAddress.getText().trim(), fldPhone.getText().trim()));
        } else {
            Branch b = new Branch(editingId, name, code, fldCity.getText().trim(), fldAddress.getText().trim(), fldPhone.getText().trim(), null);
            ok = backend.updateBranch(b);
        }
        if (ok) {
            JOptionPane.showMessageDialog(this, editingId == -1 ? "Branch added!" : "Branch updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); refresh();
        } else {
            JOptionPane.showMessageDialog(this, "Operation failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a branch first.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        if (JOptionPane.showConfirmDialog(this, "Delete branch \"" + name + "\"?\nAll linked accounts will be unlinked.",
                "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            if (backend.deleteBranch(id)) { clearForm(); refresh(); }
            else JOptionPane.showMessageDialog(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelected() {
        if (table.getSelectedRow() < 0) { JOptionPane.showMessageDialog(this, "Select a branch first.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
        populateForm();
        btnSave.setText("Update Branch");
        btnSave.setIcon(FontIcon.of(FontAwesomeSolid.SAVE, 14));
    }

    private void populateForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        editingId = (int) tableModel.getValueAt(row, 0);
        fldName.setText((String) tableModel.getValueAt(row, 1));
        fldCode.setText((String) tableModel.getValueAt(row, 2));
        fldCity.setText((String) tableModel.getValueAt(row, 3));
        fldAddress.setText((String) tableModel.getValueAt(row, 4));
        fldPhone.setText((String) tableModel.getValueAt(row, 5));
    }

    private void clearForm() {
        editingId = -1;
        fldName.setText(""); fldCode.setText(""); fldCity.setText("");
        fldAddress.setText(""); fldPhone.setText("");
        btnSave.setText("Add Branch");
        btnSave.setIcon(FontIcon.of(FontAwesomeSolid.PLUS_CIRCLE, 14));
        table.clearSelection();
    }

    private void doSearch()     { String kw = searchField.getText().trim(); loadTable(kw.isEmpty() ? backend.getAllBranches() : backend.searchBranches(kw)); }
    private void doCityFilter() { String c = (String) cityFilter.getSelectedItem(); loadTable(c.startsWith("All") ? backend.getAllBranches() : backend.filterByCity(c)); }

    private void refresh() {
        searchField.setText(""); cityFilter.setSelectedIndex(0);
        cityFilter.removeAllItems();
        cityFilter.addItem("All Cities");
        backend.getDistinctCities().forEach(cityFilter::addItem);
        loadTable(backend.getAllBranches());
        refreshAccountsPerBranch();
    }

    private void loadTable(ArrayList<Branch> list) {
        tableModel.setRowCount(0);
        for (Branch b : list) {
            tableModel.addRow(new Object[]{
                    b.getBranchId(), b.getBranchName(), b.getBranchCode(),
                    b.getCity(), b.getAddress(), b.getPhone(), b.getCreatedAt()
            });
        }
        totalLabel.setText("Total Branches: " + tableModel.getRowCount());
        StringBuilder sb = new StringBuilder("Accounts: ");
        for (String[] row : backend.getAccountsPerBranch())
            sb.append(row[0]).append("(").append(row[1]).append(") ");
        accountsLabel.setText(sb.toString());
        refreshAccountsPerBranch();
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
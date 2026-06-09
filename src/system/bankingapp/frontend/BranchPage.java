package system.bankingapp.frontend;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import system.bankingapp.backend.BranchBackend;
import system.bankingapp.model.Branch;
import uifactory.AppTheme;
import uifactory.UIFactory;

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
        JPanel bar = UIFactory.topBar();
        totalLabel    = UIFactory.statLabel();
        accountsLabel = UIFactory.statLabelBold();
        bar.add(UIFactory.titleGroup(UIFactory.pageTitle("Branch Management"),
                totalLabel, accountsLabel), BorderLayout.WEST);
        bar.add(buildFilterBar(), BorderLayout.CENTER);
        return bar;
    }

    private JPanel buildFilterBar() {
        JPanel p = UIFactory.filterBar();

        searchField = UIFactory.textField(14, "Search name, code, city…");
        searchField.addActionListener(e -> doSearch());

        cityFilter = UIFactory.comboBox(new String[]{});
        cityFilter.addItem("All Cities");
        backend.getDistinctCities().forEach(cityFilter::addItem);
        cityFilter.addActionListener(e -> doCityFilter());

        JButton btnRefresh = UIFactory.iconButton(FontAwesomeSolid.SYNC_ALT, "Refresh");
        btnRefresh.addActionListener(e -> refresh());

        p.add(UIFactory.filterLabel("Search:")); p.add(searchField);
        p.add(UIFactory.filterLabel("City:"));   p.add(cityFilter);
        p.add(btnRefresh);
        return p;
    }

    private JSplitPane buildCenter() {
        return UIFactory.splitPane(buildForm(), buildTablePanel(), 300);
    }

    private JPanel buildForm() {
        JPanel wrapper = UIFactory.formWrapper();
        JPanel card = UIFactory.formCardShell("Branch Details");

        GridBagConstraints lc = UIFactory.labelConstraints();
        GridBagConstraints fc = UIFactory.fieldConstraints();

        fldName    = UIFactory.textField(0);
        fldCode    = UIFactory.textField(0);
        fldCity    = UIFactory.textField(0);
        fldAddress = UIFactory.textField(0);
        fldPhone   = UIFactory.textField(0);

        String[]     labels = {"Branch Name *","Branch Code *","City","Address","Phone"};
        JComponent[] fields = {fldName, fldCode, fldCity, fldAddress, fldPhone};

        for (int i = 0; i < labels.length; i++) {
            UIFactory.addFormRow(card, lc, fc, i, labels[i], fields[i]);
        }

        btnSave  = UIFactory.saveButton("Add Branch", FontAwesomeSolid.PLUS_CIRCLE);
        btnClear = UIFactory.clearButton();
        btnSave.addActionListener(e  -> saveBranch());
        btnClear.addActionListener(e -> clearForm());

        card.add(UIFactory.buttonRow(btnSave, btnClear), UIFactory.buttonRowConstraints(labels.length));

        JPanel accountsPerBranch = buildAccountsPerBranchPanel();
        GridBagConstraints ac = new GridBagConstraints();
        ac.gridx = 0; ac.gridy = labels.length + 1;
        ac.gridwidth = 2; ac.fill = GridBagConstraints.HORIZONTAL;
        ac.insets = new Insets(AppTheme.SPACING_MD, AppTheme.SPACING_XS, AppTheme.SPACING_XS, AppTheme.SPACING_XS);
        card.add(accountsPerBranch, ac);

        wrapper.add(card, BorderLayout.NORTH);
        return wrapper;
    }

    private JPanel buildAccountsPerBranchPanel() {
        JPanel p = UIFactory.transparentPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(AppTheme.separator()),
                "Accounts per Branch", TitledBorder.LEFT, TitledBorder.TOP,
                AppTheme.tableHeader()));

        accountsPerBranchModel = new DefaultTableModel(new String[]{"Branch", "Accounts"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable t = UIFactory.createSmallTable(accountsPerBranchModel);
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

        table = UIFactory.createTable(tableModel);
        UIFactory.setColumnWidths(table, new int[]{45, 150, 80, 90, 200, 110, 150});

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

    private void saveBranch() {
        String name = fldName.getText().trim();
        String code = fldCode.getText().trim();
        if (name.isEmpty() || code.isEmpty()) {
            UIFactory.showMessage(this, "Branch Name and Code required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean ok;
        if (editingId == -1) {
            ok = backend.insertBranch(new Branch(name, code, fldCity.getText().trim(), fldAddress.getText().trim(), fldPhone.getText().trim()));
        } else {
            Branch b = new Branch(editingId, name, code, fldCity.getText().trim(), fldAddress.getText().trim(), fldPhone.getText().trim(), null);
            ok = backend.updateBranch(b);
        }
        if (ok) {
            UIFactory.showMessage(this, editingId == -1 ? "Branch added!" : "Branch updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); refresh();
        } else {
            UIFactory.showMessage(this, "Operation failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UIFactory.showMessage(this, "Select a branch first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        if (UIFactory.showConfirm(this, "Delete branch \"" + name + "\"?\nAll linked accounts will be unlinked.", "Confirm") == JOptionPane.YES_OPTION) {
            if (backend.deleteBranch(id)) { clearForm(); refresh(); }
            else UIFactory.showMessage(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelected() {
        if (table.getSelectedRow() < 0) {
            UIFactory.showMessage(this, "Select a branch first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
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
}

package system.bankingapp.frontend;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import system.bankingapp.backend.CardBackend;
import system.bankingapp.model.Card;
import uifactory.UIFactory;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;

public class CardPage extends JPanel {

    private final CardBackend backend = new CardBackend();

    private JTable            table;
    private DefaultTableModel tableModel;
    private JTextField        searchField;
    private JComboBox<String> typeFilter, statusFilter;
    private JLabel            totalLabel, activeLabel, blockedLabel;

    private JComboBox<String> fldAccount, fldType, fldStatus;
    private JTextField        fldCardNum, fldExpiry;
    private JButton           btnSave, btnClear;
    private int               editingId = -1;

    private ArrayList<String[]> accountList;

    private static final String[] COLUMNS = {"ID","Card Number","Account","Customer","Type","Expiry","Status","Created"};

    public CardPage() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);
        accountList = backend.getAccountList();
        add(buildTopBar(),  BorderLayout.NORTH);
        add(buildCenter(),  BorderLayout.CENTER);
        loadTable(backend.getAllCards());
    }

    private JPanel buildTopBar() {
        JPanel bar = UIFactory.topBar();
        totalLabel   = UIFactory.statLabel();
        activeLabel  = UIFactory.statLabelBold();
        blockedLabel = UIFactory.statLabelBold();
        bar.add(UIFactory.titleGroup(UIFactory.pageTitle("Card Management"),
                totalLabel, activeLabel, blockedLabel), BorderLayout.WEST);
        bar.add(buildFilterBar(), BorderLayout.CENTER);
        return bar;
    }

    private JPanel buildFilterBar() {
        JPanel p = UIFactory.filterBar();

        searchField  = UIFactory.textField(14, "Search card no, account, customer…");
        searchField.addActionListener(e -> doSearch());

        typeFilter   = UIFactory.comboBox(new String[]{"All Types","Debit","Credit"});
        statusFilter = UIFactory.comboBox(new String[]{"All Status","Active","Blocked","Expired"});
        typeFilter.addActionListener(e   -> doTypeFilter());
        statusFilter.addActionListener(e -> doStatusFilter());

        JButton btnRefresh = UIFactory.iconButton(FontAwesomeSolid.SYNC_ALT, "Refresh");
        btnRefresh.addActionListener(e -> refresh());

        p.add(UIFactory.filterLabel("Search:")); p.add(searchField);
        p.add(UIFactory.filterLabel("Type:"));   p.add(typeFilter);
        p.add(UIFactory.filterLabel("Status:")); p.add(statusFilter);
        p.add(btnRefresh);
        return p;
    }

    private JSplitPane buildCenter() {
        return UIFactory.splitPane(buildForm(), buildTablePanel(), 300);
    }

    private JPanel buildForm() {
        JPanel wrapper = UIFactory.formWrapper();
        JPanel card = UIFactory.formCardShell("Card Details");

        GridBagConstraints lc = UIFactory.labelConstraints();
        GridBagConstraints fc = UIFactory.fieldConstraints();

        fldAccount = UIFactory.comboBox(new String[]{});
        fldType    = UIFactory.comboBox(new String[]{"Debit","Credit"});
        fldStatus  = UIFactory.comboBox(new String[]{"Active","Blocked","Expired"});
        fldCardNum = UIFactory.textField(0);
        fldExpiry  = UIFactory.textField(0, "yyyy-mm-dd");

        for (String[] a : accountList) fldAccount.addItem(a[1]);

        JButton btnGenerate = UIFactory.smallButton("Generate");
        btnGenerate.addActionListener(e -> fldCardNum.setText(backend.generateCardNumber()));

        JPanel cardNumPanel = UIFactory.transparentPanel(new BorderLayout(4, 0));
        cardNumPanel.add(fldCardNum, BorderLayout.CENTER);
        cardNumPanel.add(btnGenerate, BorderLayout.EAST);

        String[]     labels = {"Account *","Card Number *","Card Type","Expiry Date *","Status"};
        JComponent[] fields = {fldAccount, cardNumPanel, fldType, fldExpiry, fldStatus};

        for (int i = 0; i < labels.length; i++) {
            UIFactory.addFormRow(card, lc, fc, i, labels[i], fields[i]);
        }

        btnSave  = UIFactory.saveButton("Add Card", FontAwesomeSolid.PLUS_CIRCLE);
        btnClear = UIFactory.clearButton();
        btnSave.addActionListener(e  -> saveCard());
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
        UIFactory.applyDualColumnColors(table, 6, UIFactory.cardStatusColors(), 4, UIFactory.cardTypeColors());
        UIFactory.setColumnWidths(table, new int[]{40, 160, 100, 140, 70, 100, 75, 140});

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

    private void saveCard() {
        if (fldCardNum.getText().trim().isEmpty() || fldExpiry.getText().trim().isEmpty()) {
            UIFactory.showMessage(this, "Card number and expiry date required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (accountList.isEmpty()) {
            UIFactory.showMessage(this, "No accounts found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int    accId = Integer.parseInt(accountList.get(fldAccount.getSelectedIndex())[0]);
        Card   c     = new Card(accId, fldCardNum.getText().trim(), (String) fldType.getSelectedItem(),
                fldExpiry.getText().trim(), (String) fldStatus.getSelectedItem());

        boolean ok;
        if (editingId == -1) {
            ok = backend.insertCard(c);
        } else {
            Card upd = new Card(editingId, accId, null, null, fldCardNum.getText().trim(),
                    (String) fldType.getSelectedItem(), fldExpiry.getText().trim(),
                    (String) fldStatus.getSelectedItem(), null);
            ok = backend.updateCard(upd);
        }

        if (ok) {
            UIFactory.showMessage(this, editingId == -1 ? "Card added!" : "Card updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); refresh();
        } else {
            UIFactory.showMessage(this, "Operation failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UIFactory.showMessage(this, "Select a card first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        if (UIFactory.showConfirm(this, "Delete this card?", "Confirm") == JOptionPane.YES_OPTION) {
            if (backend.deleteCard(id)) { clearForm(); refresh(); }
            else UIFactory.showMessage(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelected() {
        if (table.getSelectedRow() < 0) {
            UIFactory.showMessage(this, "Select a card first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        populateForm();
        btnSave.setText("Update Card");
        btnSave.setIcon(FontIcon.of(FontAwesomeSolid.SAVE, 14));
    }

    private void populateForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        editingId = (int) tableModel.getValueAt(row, 0);
        fldCardNum.setText((String) tableModel.getValueAt(row, 1));
        String accNum = (String) tableModel.getValueAt(row, 2);
        for (int i = 0; i < accountList.size(); i++)
            if (accountList.get(i)[1].equals(accNum)) { fldAccount.setSelectedIndex(i); break; }
        fldType.setSelectedItem(tableModel.getValueAt(row, 4));
        fldExpiry.setText(tableModel.getValueAt(row, 5) != null ? tableModel.getValueAt(row, 5).toString() : "");
        fldStatus.setSelectedItem(tableModel.getValueAt(row, 6));
    }

    private void clearForm() {
        editingId = -1;
        fldCardNum.setText(""); fldExpiry.setText("");
        fldAccount.setSelectedIndex(0); fldType.setSelectedIndex(0); fldStatus.setSelectedIndex(0);
        btnSave.setText("Add Card"); btnSave.setIcon(FontIcon.of(FontAwesomeSolid.PLUS_CIRCLE, 14));
        table.clearSelection();
    }

    private void doSearch()       { String kw = searchField.getText().trim(); loadTable(kw.isEmpty() ? backend.getAllCards() : backend.searchCards(kw)); }
    private void doTypeFilter()   { String t = (String) typeFilter.getSelectedItem();   loadTable(t.startsWith("All") ? backend.getAllCards() : backend.filterByType(t)); }
    private void doStatusFilter() { String s = (String) statusFilter.getSelectedItem(); loadTable(s.startsWith("All") ? backend.getAllCards() : backend.filterByStatus(s)); }

    private void refresh() {
        searchField.setText(""); typeFilter.setSelectedIndex(0); statusFilter.setSelectedIndex(0);
        loadTable(backend.getAllCards());
    }

    private void loadTable(ArrayList<Card> list) {
        tableModel.setRowCount(0);
        for (Card c : list) {
            tableModel.addRow(new Object[]{
                    c.getCardId(), c.getCardNumber(), c.getAccountNumber(),
                    c.getCustomerName(), c.getCardType(), c.getExpiryDate(),
                    c.getStatus(), c.getCreatedAt()
            });
        }
        totalLabel.setText("Total: " + tableModel.getRowCount());
        activeLabel.setText("✓ Active: " + backend.getActiveCount());
        blockedLabel.setText("✗ Blocked: " + backend.getBlockedCount());
    }
}

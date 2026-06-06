package system.bankingapp.backend;

import system.bankingapp.model.Transaction;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;

public class TransactionBackend {

    private Connection connection;

    private static final String INSERT_QUERY =
            "INSERT INTO transactions(account_id,performed_by,transaction_type,amount,description) VALUES(?,?,?,?,?)";

    private static final String GET_ALL_QUERY =
            "SELECT t.*,a.account_number,e.full_name AS performed_by_name " +
                    "FROM transactions t " +
                    "LEFT JOIN accounts  a ON t.account_id=a.account_id " +
                    "LEFT JOIN employees e ON t.performed_by=e.employee_id " +
                    "ORDER BY t.transaction_date DESC";

    private static final String SEARCH_QUERY =
            "SELECT t.*,a.account_number,e.full_name AS performed_by_name " +
                    "FROM transactions t " +
                    "LEFT JOIN accounts  a ON t.account_id=a.account_id " +
                    "LEFT JOIN employees e ON t.performed_by=e.employee_id " +
                    "WHERE a.account_number LIKE ? OR t.description LIKE ? OR t.transaction_type LIKE ?";

    private static final String FILTER_TYPE_QUERY =
            "SELECT t.*,a.account_number,e.full_name AS performed_by_name " +
                    "FROM transactions t " +
                    "LEFT JOIN accounts  a ON t.account_id=a.account_id " +
                    "LEFT JOIN employees e ON t.performed_by=e.employee_id " +
                    "WHERE t.transaction_type=? ORDER BY t.transaction_date DESC";

    private static final String FILTER_ACCOUNT_QUERY =
            "SELECT t.*,a.account_number,e.full_name AS performed_by_name " +
                    "FROM transactions t " +
                    "LEFT JOIN accounts  a ON t.account_id=a.account_id " +
                    "LEFT JOIN employees e ON t.performed_by=e.employee_id " +
                    "WHERE t.account_id=? ORDER BY t.transaction_date DESC";

    private static final String FILTER_DATE_QUERY =
            "SELECT t.*,a.account_number,e.full_name AS performed_by_name " +
                    "FROM transactions t " +
                    "LEFT JOIN accounts  a ON t.account_id=a.account_id " +
                    "LEFT JOIN employees e ON t.performed_by=e.employee_id " +
                    "WHERE t.transaction_date BETWEEN ? AND ? ORDER BY t.transaction_date DESC";

    private static final String DELETE_QUERY   = "DELETE FROM transactions WHERE transaction_id=?";
    private static final String COUNT_QUERY    = "SELECT COUNT(*) AS total FROM transactions";
    private static final String SUM_DEPOSIT    = "SELECT COALESCE(SUM(amount),0) AS total FROM transactions WHERE transaction_type='DEPOSIT'";
    private static final String SUM_WITHDRAW   = "SELECT COALESCE(SUM(amount),0) AS total FROM transactions WHERE transaction_type='WITHDRAW'";

    public TransactionBackend() {
        connection = DatabaseConnection.openConnection();
    }

    public boolean insertTransaction(Transaction t) {
        try {
            PreparedStatement ps = connection.prepareStatement(INSERT_QUERY);
            ps.setInt(1, t.getAccountId());
            ps.setInt(2, t.getPerformedBy());
            ps.setString(3, t.getTransactionType());
            ps.setDouble(4, t.getAmount());
            ps.setString(5, t.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteTransaction(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement(DELETE_QUERY);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public ArrayList<Transaction> getAllTransactions() {
        ArrayList<Transaction> data = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(GET_ALL_QUERY);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) data.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    public ArrayList<Transaction> searchTransactions(String keyword) {
        ArrayList<Transaction> data = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(SEARCH_QUERY);
            String kw = "%" + keyword + "%";
            ps.setString(1, kw); ps.setString(2, kw); ps.setString(3, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) data.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    public ArrayList<Transaction> filterByType(String type) {
        ArrayList<Transaction> data = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(FILTER_TYPE_QUERY);
            ps.setString(1, type);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) data.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    public ArrayList<Transaction> filterByAccount(int accountId) {
        ArrayList<Transaction> data = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(FILTER_ACCOUNT_QUERY);
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) data.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    public ArrayList<Transaction> filterByDateRange(String from, String to) {
        ArrayList<Transaction> data = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(FILTER_DATE_QUERY);
            ps.setString(1, from); ps.setString(2, to);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) data.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    public int    getTotalCount()      { return queryInt(COUNT_QUERY); }
    public double getTotalDeposits()   { return queryDouble(SUM_DEPOSIT); }
    public double getTotalWithdrawals(){ return queryDouble(SUM_WITHDRAW); }

    public ArrayList<String[]> getAccountList() {
        ArrayList<String[]> list = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT account_id,account_number FROM accounts ORDER BY account_number");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(new String[]{rs.getString("account_id"), rs.getString("account_number")});
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public ArrayList<String[]> getEmployeeList() {
        ArrayList<String[]> list = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT employee_id,full_name FROM employees ORDER BY full_name");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(new String[]{rs.getString("employee_id"), rs.getString("full_name")});
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private int queryInt(String sql) {
        try { PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery(); if (rs.next()) return rs.getInt(1); } catch (SQLException e) { e.printStackTrace(); } return 0;
    }
    private double queryDouble(String sql) {
        try { PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery(); if (rs.next()) return rs.getDouble(1); } catch (SQLException e) { e.printStackTrace(); } return 0;
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getInt("transaction_id"),
                rs.getInt("account_id"),
                rs.getInt("performed_by"),
                rs.getString("account_number"),
                rs.getString("performed_by_name"),
                rs.getString("transaction_type"),
                rs.getDouble("amount"),
                rs.getString("description"),
                rs.getTimestamp("transaction_date")
        );
    }
}
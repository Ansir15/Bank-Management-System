package system.bankingapp.backend;

import system.bankingapp.model.Account;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;

public class AccountBackend {

    private Connection connection;

    private static final String INSERT_QUERY =
            "INSERT INTO accounts(customer_id,branch_id,account_number,account_type,balance,status) VALUES(?,?,?,?,?,?)";

    private static final String GET_ALL_QUERY =
            "SELECT a.*,c.full_name AS customer_name,b.branch_name " +
                    "FROM accounts a " +
                    "LEFT JOIN customers c ON a.customer_id=c.customer_id " +
                    "LEFT JOIN branches  b ON a.branch_id=b.branch_id " +
                    "ORDER BY a.created_at DESC";

    private static final String UPDATE_QUERY =
            "UPDATE accounts SET customer_id=?,branch_id=?,account_number=?,account_type=?,balance=?,status=? WHERE account_id=?";

    private static final String DELETE_QUERY =
            "DELETE FROM accounts WHERE account_id=?";

    private static final String SEARCH_QUERY =
            "SELECT a.*,c.full_name AS customer_name,b.branch_name " +
                    "FROM accounts a " +
                    "LEFT JOIN customers c ON a.customer_id=c.customer_id " +
                    "LEFT JOIN branches  b ON a.branch_id=b.branch_id " +
                    "WHERE a.account_number LIKE ? OR c.full_name LIKE ? OR a.account_type LIKE ?";

    private static final String FILTER_TYPE_QUERY =
            "SELECT a.*,c.full_name AS customer_name,b.branch_name " +
                    "FROM accounts a " +
                    "LEFT JOIN customers c ON a.customer_id=c.customer_id " +
                    "LEFT JOIN branches  b ON a.branch_id=b.branch_id " +
                    "WHERE a.account_type=? ORDER BY a.created_at DESC";

    private static final String FILTER_STATUS_QUERY =
            "SELECT a.*,c.full_name AS customer_name,b.branch_name " +
                    "FROM accounts a " +
                    "LEFT JOIN customers c ON a.customer_id=c.customer_id " +
                    "LEFT JOIN branches  b ON a.branch_id=b.branch_id " +
                    "WHERE a.status=? ORDER BY a.created_at DESC";

    private static final String GET_BY_ID_QUERY =
            "SELECT a.*,c.full_name AS customer_name,b.branch_name " +
                    "FROM accounts a " +
                    "LEFT JOIN customers c ON a.customer_id=c.customer_id " +
                    "LEFT JOIN branches  b ON a.branch_id=b.branch_id " +
                    "WHERE a.account_id=?";

    private static final String COUNT_QUERY          = "SELECT COUNT(*) AS total FROM accounts";
    private static final String TOTAL_BALANCE_QUERY  = "SELECT SUM(balance) AS total FROM accounts WHERE status='Active'";
    private static final String GENERATE_ACCNUM_QUERY = "SELECT MAX(CAST(SUBSTRING(account_number,4) AS UNSIGNED)) AS maxnum FROM accounts";

    public AccountBackend() {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    public String generateAccountNumber() {
        try {
            PreparedStatement ps = connection.prepareStatement(GENERATE_ACCNUM_QUERY);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int max = rs.getInt("maxnum");
                return "ACC" + String.format("%04d", max + 1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "ACC1001";
    }

    public boolean insertAccount(Account a) {
        try {
            PreparedStatement ps = connection.prepareStatement(INSERT_QUERY);
            ps.setInt(1, a.getCustomerId());
            ps.setInt(2, a.getBranchId());
            ps.setString(3, a.getAccountNumber());
            ps.setString(4, a.getAccountType());
            ps.setDouble(5, a.getBalance());
            ps.setString(6, a.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateAccount(Account a) {
        try {
            PreparedStatement ps = connection.prepareStatement(UPDATE_QUERY);
            ps.setInt(1, a.getCustomerId());
            ps.setInt(2, a.getBranchId());
            ps.setString(3, a.getAccountNumber());
            ps.setString(4, a.getAccountType());
            ps.setDouble(5, a.getBalance());
            ps.setString(6, a.getStatus());
            ps.setInt(7, a.getAccountId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteAccount(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement(DELETE_QUERY);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public ArrayList<Account> getAllAccounts() {
        ArrayList<Account> data = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(GET_ALL_QUERY);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) data.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    public ArrayList<Account> searchAccounts(String keyword) {
        ArrayList<Account> data = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(SEARCH_QUERY);
            String kw = "%" + keyword + "%";
            ps.setString(1, kw); ps.setString(2, kw); ps.setString(3, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) data.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    public ArrayList<Account> filterByType(String type) {
        ArrayList<Account> data = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(FILTER_TYPE_QUERY);
            ps.setString(1, type);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) data.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    public ArrayList<Account> filterByStatus(String status) {
        ArrayList<Account> data = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(FILTER_STATUS_QUERY);
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) data.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    public Account getAccountById(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement(GET_BY_ID_QUERY);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public int getTotalCount() {
        try {
            PreparedStatement ps = connection.prepareStatement(COUNT_QUERY);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public double getTotalActiveBalance() {
        try {
            PreparedStatement ps = connection.prepareStatement(TOTAL_BALANCE_QUERY);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public ArrayList<String[]> getCustomerList() {
        ArrayList<String[]> list = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT customer_id,full_name FROM customers ORDER BY full_name");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(new String[]{rs.getString("customer_id"), rs.getString("full_name")});
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public ArrayList<String[]> getBranchList() {
        ArrayList<String[]> list = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT branch_id,branch_name FROM branches ORDER BY branch_name");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(new String[]{rs.getString("branch_id"), rs.getString("branch_name")});
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private Account mapRow(ResultSet rs) throws SQLException {
        return new Account(
                rs.getInt("account_id"),
                rs.getInt("customer_id"),
                rs.getInt("branch_id"),
                rs.getString("customer_name"),
                rs.getString("branch_name"),
                rs.getString("account_number"),
                rs.getString("account_type"),
                rs.getDouble("balance"),
                rs.getString("status"),
                rs.getTimestamp("created_at")
        );
    }
}
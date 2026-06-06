package system.bankingapp.backend;

import system.bankingapp.model.Loan;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;

public class LoanBackend {

    private Connection connection;

    private static final String INSERT_QUERY =
            "INSERT INTO loans(customer_id,loan_type,principal_amount,interest_rate,tenure_months,status,issued_date) VALUES(?,?,?,?,?,?,?)";

    private static final String GET_ALL_QUERY =
            "SELECT l.*,c.full_name AS customer_name FROM loans l " +
                    "LEFT JOIN customers c ON l.customer_id=c.customer_id ORDER BY l.created_at DESC";

    private static final String UPDATE_QUERY =
            "UPDATE loans SET customer_id=?,loan_type=?,principal_amount=?,interest_rate=?,tenure_months=?,status=?,issued_date=? WHERE loan_id=?";

    private static final String DELETE_QUERY =
            "DELETE FROM loans WHERE loan_id=?";

    private static final String SEARCH_QUERY =
            "SELECT l.*,c.full_name AS customer_name FROM loans l " +
                    "LEFT JOIN customers c ON l.customer_id=c.customer_id " +
                    "WHERE c.full_name LIKE ? OR l.loan_type LIKE ? OR l.status LIKE ?";

    private static final String FILTER_STATUS_QUERY =
            "SELECT l.*,c.full_name AS customer_name FROM loans l " +
                    "LEFT JOIN customers c ON l.customer_id=c.customer_id WHERE l.status=? ORDER BY l.created_at DESC";

    private static final String FILTER_TYPE_QUERY =
            "SELECT l.*,c.full_name AS customer_name FROM loans l " +
                    "LEFT JOIN customers c ON l.customer_id=c.customer_id WHERE l.loan_type=? ORDER BY l.created_at DESC";

    private static final String FILTER_DATE_QUERY =
            "SELECT l.*,c.full_name AS customer_name FROM loans l " +
                    "LEFT JOIN customers c ON l.customer_id=c.customer_id " +
                    "WHERE l.created_at BETWEEN ? AND ? ORDER BY l.created_at DESC";

    private static final String COUNT_QUERY          = "SELECT COUNT(*) FROM loans";
    private static final String COUNT_APPROVED_QUERY = "SELECT COUNT(*) FROM loans WHERE status='Approved'";
    private static final String COUNT_PENDING_QUERY  = "SELECT COUNT(*) FROM loans WHERE status='Pending'";
    private static final String SUM_PRINCIPAL_QUERY  = "SELECT COALESCE(SUM(principal_amount),0) FROM loans WHERE status='Approved'";

    public LoanBackend() {
        connection = DatabaseConnection.openConnection();
    }

    public boolean insertLoan(Loan l) {
        try {
            PreparedStatement ps = connection.prepareStatement(INSERT_QUERY);
            ps.setInt(1, l.getCustomerId());
            ps.setString(2, l.getLoanType());
            ps.setDouble(3, l.getPrincipalAmount());
            ps.setDouble(4, l.getInterestRate());
            ps.setInt(5, l.getTenureMonths());
            ps.setString(6, l.getStatus());
            ps.setString(7, l.getIssuedDate().isEmpty() ? null : l.getIssuedDate());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateLoan(Loan l) {
        try {
            PreparedStatement ps = connection.prepareStatement(UPDATE_QUERY);
            ps.setInt(1, l.getCustomerId());
            ps.setString(2, l.getLoanType());
            ps.setDouble(3, l.getPrincipalAmount());
            ps.setDouble(4, l.getInterestRate());
            ps.setInt(5, l.getTenureMonths());
            ps.setString(6, l.getStatus());
            ps.setString(7, l.getIssuedDate().isEmpty() ? null : l.getIssuedDate());
            ps.setInt(8, l.getLoanId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteLoan(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement(DELETE_QUERY);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public ArrayList<Loan> getAllLoans() {
        ArrayList<Loan> data = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(GET_ALL_QUERY);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) data.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    public ArrayList<Loan> searchLoans(String keyword) {
        ArrayList<Loan> data = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(SEARCH_QUERY);
            String kw = "%" + keyword + "%";
            ps.setString(1, kw); ps.setString(2, kw); ps.setString(3, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) data.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    public ArrayList<Loan> filterByStatus(String status) {
        ArrayList<Loan> data = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(FILTER_STATUS_QUERY);
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) data.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    public ArrayList<Loan> filterByType(String type) {
        ArrayList<Loan> data = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(FILTER_TYPE_QUERY);
            ps.setString(1, type);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) data.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    public ArrayList<Loan> filterByDateRange(String from, String to) {
        ArrayList<Loan> data = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(FILTER_DATE_QUERY);
            ps.setString(1, from); ps.setString(2, to);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) data.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    public int    getTotalCount()    { return queryInt(COUNT_QUERY); }
    public int    getApprovedCount() { return queryInt(COUNT_APPROVED_QUERY); }
    public int    getPendingCount()  { return queryInt(COUNT_PENDING_QUERY); }
    public double getTotalApprovedAmount() { return queryDouble(SUM_PRINCIPAL_QUERY); }

    public ArrayList<String[]> getCustomerList() {
        ArrayList<String[]> list = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT customer_id,full_name FROM customers ORDER BY full_name");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(new String[]{rs.getString("customer_id"), rs.getString("full_name")});
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private int queryInt(String sql) {
        try { PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery(); if (rs.next()) return rs.getInt(1); } catch (SQLException e) { e.printStackTrace(); } return 0;
    }
    private double queryDouble(String sql) {
        try { PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery(); if (rs.next()) return rs.getDouble(1); } catch (SQLException e) { e.printStackTrace(); } return 0;
    }

    private Loan mapRow(ResultSet rs) throws SQLException {
        return new Loan(
                rs.getInt("loan_id"),
                rs.getInt("customer_id"),
                rs.getString("customer_name"),
                rs.getString("loan_type"),
                rs.getDouble("principal_amount"),
                rs.getDouble("interest_rate"),
                rs.getInt("tenure_months"),
                rs.getString("status"),
                rs.getString("issued_date") != null ? rs.getString("issued_date") : "",
                rs.getTimestamp("created_at")
        );
    }
}
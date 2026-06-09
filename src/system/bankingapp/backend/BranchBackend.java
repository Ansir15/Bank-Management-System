package system.bankingapp.backend;

import system.bankingapp.model.Branch;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;

public class BranchBackend {

    private Connection connection;

    private static final String INSERT_QUERY =
            "INSERT INTO branches(branch_name,branch_code,city,address,phone) VALUES(?,?,?,?,?)";

    private static final String GET_ALL_QUERY =
            "SELECT * FROM branches ORDER BY created_at DESC";

    private static final String UPDATE_QUERY =
            "UPDATE branches SET branch_name=?,branch_code=?,city=?,address=?,phone=? WHERE branch_id=?";

    private static final String DELETE_QUERY =
            "DELETE FROM branches WHERE branch_id=?";

    private static final String SEARCH_QUERY =
            "SELECT * FROM branches WHERE branch_name LIKE ? OR branch_code LIKE ? OR city LIKE ? OR phone LIKE ?";

    private static final String FILTER_CITY_QUERY =
            "SELECT * FROM branches WHERE city=? ORDER BY created_at DESC";

    private static final String COUNT_QUERY      = "SELECT COUNT(*) FROM branches";
    private static final String CITIES_QUERY     = "SELECT DISTINCT city FROM branches ORDER BY city";

    private static final String ACCOUNTS_PER_BRANCH_QUERY =
            "SELECT b.branch_name, COUNT(a.account_id) AS total " +
                    "FROM branches b LEFT JOIN accounts a ON b.branch_id=a.branch_id GROUP BY b.branch_id";

    public BranchBackend() {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    public boolean insertBranch(Branch b) {
        try {
            PreparedStatement ps = connection.prepareStatement(INSERT_QUERY);
            ps.setString(1, b.getBranchName());
            ps.setString(2, b.getBranchCode());
            ps.setString(3, b.getCity());
            ps.setString(4, b.getAddress());
            ps.setString(5, b.getPhone());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateBranch(Branch b) {
        try {
            PreparedStatement ps = connection.prepareStatement(UPDATE_QUERY);
            ps.setString(1, b.getBranchName());
            ps.setString(2, b.getBranchCode());
            ps.setString(3, b.getCity());
            ps.setString(4, b.getAddress());
            ps.setString(5, b.getPhone());
            ps.setInt(6, b.getBranchId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteBranch(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement(DELETE_QUERY);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public ArrayList<Branch> getAllBranches() {
        ArrayList<Branch> data = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(GET_ALL_QUERY);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) data.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    public ArrayList<Branch> searchBranches(String keyword) {
        ArrayList<Branch> data = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(SEARCH_QUERY);
            String kw = "%" + keyword + "%";
            ps.setString(1, kw); ps.setString(2, kw);
            ps.setString(3, kw); ps.setString(4, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) data.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    public ArrayList<Branch> filterByCity(String city) {
        ArrayList<Branch> data = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(FILTER_CITY_QUERY);
            ps.setString(1, city);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) data.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    public ArrayList<String> getDistinctCities() {
        ArrayList<String> cities = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(CITIES_QUERY);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) cities.add(rs.getString("city"));
        } catch (SQLException e) { e.printStackTrace(); }
        return cities;
    }

    public ArrayList<String[]> getAccountsPerBranch() {
        ArrayList<String[]> data = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(ACCOUNTS_PER_BRANCH_QUERY);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) data.add(new String[]{rs.getString("branch_name"), rs.getString("total")});
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    public int getTotalCount() {
        try { PreparedStatement ps = connection.prepareStatement(COUNT_QUERY); ResultSet rs = ps.executeQuery(); if (rs.next()) return rs.getInt(1); } catch (SQLException e) { e.printStackTrace(); } return 0;
    }

    private Branch mapRow(ResultSet rs) throws SQLException {
        return new Branch(
                rs.getInt("branch_id"),
                rs.getString("branch_name"),
                rs.getString("branch_code"),
                rs.getString("city"),
                rs.getString("address"),
                rs.getString("phone"),
                rs.getTimestamp("created_at")
        );
    }
}
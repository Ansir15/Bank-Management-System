package system.bankingapp.backend;

import system.bankingapp.model.Customers;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;

public class CustomerBackend {

    private Connection connection;

    private static final String INSERT_QUERY      = "INSERT INTO customers(full_name,email,phone,address,cnic,date_of_birth,gender) VALUES(?,?,?,?,?,?,?)";
    private static final String GET_ALL_QUERY     = "SELECT * FROM customers ORDER BY created_at DESC";
    private static final String DELETE_QUERY      = "DELETE FROM customers WHERE customer_id=?";
    private static final String UPDATE_QUERY      = "UPDATE customers SET full_name=?,email=?,phone=?,address=?,cnic=?,date_of_birth=?,gender=? WHERE customer_id=?";
    private static final String SEARCH_QUERY      = "SELECT * FROM customers WHERE full_name LIKE ? OR email LIKE ? OR phone LIKE ? OR cnic LIKE ?";
    private static final String GET_BY_ID_QUERY   = "SELECT * FROM customers WHERE customer_id=?";
    private static final String FILTER_GENDER     = "SELECT * FROM customers WHERE gender=? ORDER BY created_at DESC";
    private static final String FILTER_DATE_RANGE = "SELECT * FROM customers WHERE created_at BETWEEN ? AND ? ORDER BY created_at DESC";
    private static final String COUNT_QUERY       = "SELECT COUNT(*) AS total FROM customers";

    public CustomerBackend() {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    public boolean insertCustomer(Customers c) {
        try {
            PreparedStatement ps = connection.prepareStatement(INSERT_QUERY);
            ps.setString(1, c.getCustomerFullName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getAddress());
            ps.setString(5, c.getCnic());
            ps.setString(6, c.getDateOfBirth());
            ps.setString(7, c.getGender());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCustomer(Customers c) {
        try {
            PreparedStatement ps = connection.prepareStatement(UPDATE_QUERY);
            ps.setString(1, c.getCustomerFullName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getAddress());
            ps.setString(5, c.getCnic());
            ps.setString(6, c.getDateOfBirth());
            ps.setString(7, c.getGender());
            ps.setInt(8, c.getCustomerId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCustomer(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement(DELETE_QUERY);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Customers> getAllCustomers() {
        ArrayList<Customers> data = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(GET_ALL_QUERY);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) data.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public ArrayList<Customers> searchCustomers(String keyword) {
        ArrayList<Customers> data = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(SEARCH_QUERY);
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            ps.setString(4, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) data.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public ArrayList<Customers> filterByGender(String gender) {
        ArrayList<Customers> data = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(FILTER_GENDER);
            ps.setString(1, gender);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) data.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public ArrayList<Customers> filterByDateRange(String from, String to) {
        ArrayList<Customers> data = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(FILTER_DATE_RANGE);
            ps.setString(1, from);
            ps.setString(2, to);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) data.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public Customers getCustomerById(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement(GET_BY_ID_QUERY);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getTotalCount() {
        try {
            PreparedStatement ps = connection.prepareStatement(COUNT_QUERY);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Customers mapRow(ResultSet rs) throws SQLException {
        return new Customers(
                rs.getInt("customer_id"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("address"),
                rs.getString("cnic"),
                rs.getString("date_of_birth"),
                rs.getString("gender"),
                rs.getTimestamp("created_at")
        );
    }
}
package system.bankingapp.backend;

import system.bankingapp.model.Employee;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;

public class EmployeeBackend {

    private Connection connection;

    private static final String INSERT_QUERY =
            "INSERT INTO employees(full_name,email,phone,role,salary,hire_date) VALUES(?,?,?,?,?,?)";

    private static final String GET_ALL_QUERY =
            "SELECT * FROM employees ORDER BY created_at DESC";

    private static final String UPDATE_QUERY =
            "UPDATE employees SET full_name=?,email=?,phone=?,role=?,salary=?,hire_date=? WHERE employee_id=?";

    private static final String DELETE_QUERY =
            "DELETE FROM employees WHERE employee_id=?";

    private static final String SEARCH_QUERY =
            "SELECT * FROM employees WHERE full_name LIKE ? OR email LIKE ? OR phone LIKE ? OR role LIKE ?";

    private static final String FILTER_ROLE_QUERY =
            "SELECT * FROM employees WHERE role=? ORDER BY created_at DESC";

    private static final String FILTER_DATE_QUERY =
            "SELECT * FROM employees WHERE created_at BETWEEN ? AND ? ORDER BY created_at DESC";

    private static final String COUNT_QUERY       = "SELECT COUNT(*) FROM employees";
    private static final String COUNT_ADMIN_QUERY = "SELECT COUNT(*) FROM employees WHERE role='Admin'";
    private static final String COUNT_TELLER_QUERY= "SELECT COUNT(*) FROM employees WHERE role='Teller'";
    private static final String AVG_SALARY_QUERY  = "SELECT COALESCE(AVG(salary),0) FROM employees";
    private static final String TOTAL_SALARY_QUERY= "SELECT COALESCE(SUM(salary),0) FROM employees";

    public EmployeeBackend() {
        connection = DatabaseConnection.openConnection();
    }

    public boolean insertEmployee(Employee e) {
        try {
            PreparedStatement ps = connection.prepareStatement(INSERT_QUERY);
            ps.setString(1, e.getFullName());
            ps.setString(2, e.getEmail());
            ps.setString(3, e.getPhone());
            ps.setString(4, e.getRole());
            ps.setDouble(5, e.getSalary());
            ps.setString(6, e.getHireDate().isEmpty() ? null : e.getHireDate());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); return false; }
    }

    public boolean updateEmployee(Employee e) {
        try {
            PreparedStatement ps = connection.prepareStatement(UPDATE_QUERY);
            ps.setString(1, e.getFullName());
            ps.setString(2, e.getEmail());
            ps.setString(3, e.getPhone());
            ps.setString(4, e.getRole());
            ps.setDouble(5, e.getSalary());
            ps.setString(6, e.getHireDate().isEmpty() ? null : e.getHireDate());
            ps.setInt(7, e.getEmployeeId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); return false; }
    }

    public boolean deleteEmployee(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement(DELETE_QUERY);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); return false; }
    }

    public ArrayList<Employee> getAllEmployees() {
        ArrayList<Employee> data = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(GET_ALL_QUERY);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) data.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    public ArrayList<Employee> searchEmployees(String keyword) {
        ArrayList<Employee> data = new ArrayList<>();
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

    public ArrayList<Employee> filterByRole(String role) {
        ArrayList<Employee> data = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(FILTER_ROLE_QUERY);
            ps.setString(1, role);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) data.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    public ArrayList<Employee> filterByDateRange(String from, String to) {
        ArrayList<Employee> data = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(FILTER_DATE_QUERY);
            ps.setString(1, from); ps.setString(2, to);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) data.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    public int    getTotalCount()   { return queryInt(COUNT_QUERY); }
    public int    getAdminCount()   { return queryInt(COUNT_ADMIN_QUERY); }
    public int    getTellerCount()  { return queryInt(COUNT_TELLER_QUERY); }
    public double getAvgSalary()    { return queryDouble(AVG_SALARY_QUERY); }
    public double getTotalSalary()  { return queryDouble(TOTAL_SALARY_QUERY); }

    private int queryInt(String sql) {
        try { PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery(); if (rs.next()) return rs.getInt(1); } catch (SQLException e) { e.printStackTrace(); } return 0;
    }
    private double queryDouble(String sql) {
        try { PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery(); if (rs.next()) return rs.getDouble(1); } catch (SQLException e) { e.printStackTrace(); } return 0;
    }

    private Employee mapRow(ResultSet rs) throws SQLException {
        return new Employee(
                rs.getInt("employee_id"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("role"),
                rs.getDouble("salary"),
                rs.getString("hire_date") != null ? rs.getString("hire_date") : "",
                rs.getTimestamp("created_at")
        );
    }
}
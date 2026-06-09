package system.bankingapp.backend;

import system.bankingapp.model.Card;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;

public class CardBackend {

    private Connection connection;

    private static final String INSERT_QUERY =
            "INSERT INTO cards(account_id,card_number,card_type,expiry_date,status) VALUES(?,?,?,?,?)";

    private static final String GET_ALL_QUERY =
            "SELECT cd.*,a.account_number,c.full_name AS customer_name FROM cards cd " +
                    "LEFT JOIN accounts  a ON cd.account_id=a.account_id " +
                    "LEFT JOIN customers c ON a.customer_id=c.customer_id ORDER BY cd.created_at DESC";

    private static final String UPDATE_QUERY =
            "UPDATE cards SET account_id=?,card_number=?,card_type=?,expiry_date=?,status=? WHERE card_id=?";

    private static final String DELETE_QUERY = "DELETE FROM cards WHERE card_id=?";

    private static final String SEARCH_QUERY =
            "SELECT cd.*,a.account_number,c.full_name AS customer_name FROM cards cd " +
                    "LEFT JOIN accounts  a ON cd.account_id=a.account_id " +
                    "LEFT JOIN customers c ON a.customer_id=c.customer_id " +
                    "WHERE cd.card_number LIKE ? OR a.account_number LIKE ? OR c.full_name LIKE ?";

    private static final String FILTER_TYPE_QUERY =
            "SELECT cd.*,a.account_number,c.full_name AS customer_name FROM cards cd " +
                    "LEFT JOIN accounts  a ON cd.account_id=a.account_id " +
                    "LEFT JOIN customers c ON a.customer_id=c.customer_id WHERE cd.card_type=? ORDER BY cd.created_at DESC";

    private static final String FILTER_STATUS_QUERY =
            "SELECT cd.*,a.account_number,c.full_name AS customer_name FROM cards cd " +
                    "LEFT JOIN accounts  a ON cd.account_id=a.account_id " +
                    "LEFT JOIN customers c ON a.customer_id=c.customer_id WHERE cd.status=? ORDER BY cd.created_at DESC";

    private static final String COUNT_QUERY  = "SELECT COUNT(*) FROM cards";
    private static final String COUNT_ACTIVE = "SELECT COUNT(*) FROM cards WHERE status='Active'";
    private static final String COUNT_BLOCKED= "SELECT COUNT(*) FROM cards WHERE status='Blocked'";

    public CardBackend() {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    public boolean insertCard(Card c) {
        try {
            PreparedStatement ps = connection.prepareStatement(INSERT_QUERY);
            ps.setInt(1, c.getAccountId());
            ps.setString(2, c.getCardNumber());
            ps.setString(3, c.getCardType());
            ps.setString(4, c.getExpiryDate());
            ps.setString(5, c.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateCard(Card c) {
        try {
            PreparedStatement ps = connection.prepareStatement(UPDATE_QUERY);
            ps.setInt(1, c.getAccountId());
            ps.setString(2, c.getCardNumber());
            ps.setString(3, c.getCardType());
            ps.setString(4, c.getExpiryDate());
            ps.setString(5, c.getStatus());
            ps.setInt(6, c.getCardId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteCard(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement(DELETE_QUERY);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public ArrayList<Card> getAllCards() {
        ArrayList<Card> data = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(GET_ALL_QUERY);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) data.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    public ArrayList<Card> searchCards(String keyword) {
        ArrayList<Card> data = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(SEARCH_QUERY);
            String kw = "%" + keyword + "%";
            ps.setString(1, kw); ps.setString(2, kw); ps.setString(3, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) data.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    public ArrayList<Card> filterByType(String type) {
        ArrayList<Card> data = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(FILTER_TYPE_QUERY);
            ps.setString(1, type);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) data.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    public ArrayList<Card> filterByStatus(String status) {
        ArrayList<Card> data = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(FILTER_STATUS_QUERY);
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) data.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    public String generateCardNumber() {
        return "4" + String.format("%015d", (long)(Math.random() * 1_000_000_000_000_000L));
    }

    public int getTotalCount()   { return queryInt(COUNT_QUERY); }
    public int getActiveCount()  { return queryInt(COUNT_ACTIVE); }
    public int getBlockedCount() { return queryInt(COUNT_BLOCKED); }

    public ArrayList<String[]> getAccountList() {
        ArrayList<String[]> list = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT account_id,account_number FROM accounts ORDER BY account_number");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(new String[]{rs.getString("account_id"), rs.getString("account_number")});
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private int queryInt(String sql) {
        try { PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery(); if (rs.next()) return rs.getInt(1); } catch (SQLException e) { e.printStackTrace(); } return 0;
    }

    private Card mapRow(ResultSet rs) throws SQLException {
        return new Card(
                rs.getInt("card_id"),
                rs.getInt("account_id"),
                rs.getString("account_number"),
                rs.getString("customer_name"),
                rs.getString("card_number"),
                rs.getString("card_type"),
                rs.getString("expiry_date"),
                rs.getString("status"),
                rs.getTimestamp("created_at")
        );
    }
}
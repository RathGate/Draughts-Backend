package game_server;

import java.net.http.WebSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbConn {

    private Connection connect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/checkers_db";
        String user = "root";
        String password = "devine0405";
        return DriverManager.getConnection(url, user, password);
    }

    

    private String fetchFromDatabase(String nom_colones, String table) {
        String sql = "SELECT " + nom_colones + " FROM " + table;
        StringBuilder response = new StringBuilder();
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                response.append(rs.getString(nom_colones)).append("\n");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return response.toString();
    }

    private void insertIntoDatabase(String table, String nom_colones, String values) {
        String sql = "INSERT INTO " + table + " (" + nom_colones + ") VALUES (" + values + ")";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        DbConn dbConn = new DbConn();
        dbConn.insertIntoDatabase("matchmaking_log", "nom", "'user1'");
        System.out.println(dbConn.fetchFromDatabase("nom", "matchmaking_log"));
    }
}

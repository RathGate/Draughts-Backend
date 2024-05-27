package game_server;

import java.net.http.WebSocket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class DbConn {

    private JsonObject loadJson() throws IOException {
        Gson gson = new Gson();
        Path path = Paths.get("../db_credentials.json"); // Adjust path as necessary
        try {
            String content = new String(Files.readAllBytes(path));
            return gson.fromJson(content, JsonObject.class);
        } catch (JsonParseException e) {
            throw new IOException("Failed to parse db.json", e);
        }
    }

    private Connection connect() throws SQLException, IOException {
        JsonObject jsonObject = loadJson();
        String url = jsonObject.get("url").getAsString();
        String user = jsonObject.get("user").getAsString();
        String password = jsonObject.get("password").getAsString();

        return DriverManager.getConnection(url, user, password);
    }

    public String fetchFromDatabase(String nom_colones, String table) {
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return response.toString();
    }

    public void insertIntoDatabase(String table, String nom_colones, String values) {
        String sql = "INSERT INTO " + table + " (" + nom_colones + ") VALUES (" + values + ")";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void test(String[] args) {
        DbConn dbConn = new DbConn();
        dbConn.insertIntoDatabase("matchmaking_log", "nom", "'usertest'");
        System.out.println(dbConn.fetchFromDatabase("nom", "matchmaking_log"));
    }
}

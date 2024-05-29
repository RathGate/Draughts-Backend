package game_server;

import java.net.http.WebSocket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.sql.*;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class DbConn {

    private JsonObject loadJson() throws IOException {
        // load the database credentials from db_credentials.json
        Gson gson = new Gson();
        Path path = Paths.get("../db_credentials.json");
        try {
            String content = new String(Files.readAllBytes(path));
            return gson.fromJson(content, JsonObject.class);
        } catch (JsonParseException e) {
            throw new IOException("Failed to parse db.json", e);
        }
    }

    private Connection connect() throws SQLException, IOException {
        // connect to the database using the credentials laoded with the method above
        // and return the connection object
        JsonObject jsonObject = loadJson();
        String url = jsonObject.get("url").getAsString();
        String user = jsonObject.get("user").getAsString();
        String password = jsonObject.get("password").getAsString();

        return DriverManager.getConnection(url, user, password);
    }

    public String fetchFromDatabase(String column_names, String table) {
        // fetch the data from the database and return it as a string
        // the data is fetched from the specified column and table
        String sql = "SELECT " + column_names + " FROM " + table;
        StringBuilder response = new StringBuilder();
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                response.append(rs.getString(column_names)).append("\n");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return response.toString();
    }

    public int insertIntoDatabase(String table, String column_names, String values) {
        // insert the data into the database
        // the data is inserted into the specified table and columns
        String sql = "INSERT INTO " + table + " (" + column_names + ") VALUES (" + values + ")";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException | IOException e) {
            System.out.println(e.getMessage());
        }
        return -1;
    }

    public static void test(String[] args) {
        DbConn dbConn = new DbConn();
        dbConn.insertIntoDatabase("matchmaking_logs", "nom", "'usertest'");
        System.out.println(dbConn.fetchFromDatabase("nom", "matchmaking_logs"));
    }
}

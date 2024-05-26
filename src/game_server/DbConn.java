
import java.net.http.WebSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbConn {

    private Connection connect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/dbname";
        String user = "username";
        String password = "password";
        return DriverManager.getConnection(url, user, password);
    }

    // public void onMessage(String message, WebSocket conn) {
    // System.out.println("Message received: " + message);
    // String response = fetchFromDatabase(message);
    // try {
    // conn.send(response);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

    private String fetchFromDatabase(String message) {
        String result = "";
        String query = "SELECT info FROM your_table WHERE condition = ?";

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, message);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                result = rs.getString("info");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}

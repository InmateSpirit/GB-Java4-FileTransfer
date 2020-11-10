package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DbConnection {
    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver not found");
        }
    }

    protected String preparedDbConnection (String host, String login, String password){

        return String.format("jdbc:mysql://%s:3306/users?useSSL=false, %s, %s", host, login, password);
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    preparedDbConnection("localhost","filetransferuser", "1234")
            );

        } catch (SQLException throwables) {
            throw new RuntimeException("Connection getting error");
        }
    }
}

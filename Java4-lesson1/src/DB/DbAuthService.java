package DB;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbAuthService {
    private DbConnection connection = new DbConnection();

    public User findUser(String login, String password){
        Connection conn = connection.getConnection();
        try {
        PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM users WHERE login = ? AND password = ?");
        preparedStatement.setString(1, login);
        preparedStatement.setString(2, password);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return new User(
                    resultSet.getInt("id"),
                    resultSet.getString("nickname"),
                    resultSet.getString("login"),
                    resultSet.getString("password")
            );
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
    public void newUserRegistration(String login, String password){
        Connection conn = connection.getConnection();
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("INSERT users_tbl (login, password) VALUES (?, ?)");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
//            preparedStatement.setString(3, id + login+ "/");
            // add id number to verify users folder.

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
}

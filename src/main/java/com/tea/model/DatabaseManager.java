package com.tea.model;

import com.tea.server.Config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseManager {

    private Connection connection;

    public DatabaseManager() {
        connect();
    }

    private void connect() {
        try {
            Config serverConfig = Config.getInstance();
            String url = serverConfig.getJdbcUrl();
            String user = serverConfig.getDbUser();
            String password = serverConfig.getDbPassword();
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveUser(String username, String password, String ipAddress) {
        SimpleDateFormat formatter = new SimpleDateFormat("'đăng nhập lúc ' HH:mm:ss 'ngày' dd/MM/yyyy");
        String formattedDate = formatter.format(new Date());

        try {
            if (!isUserExists(username)) {
                String sql = "INSERT INTO userkomahoa (username, password, ip, login_time) VALUES (?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, username);
                statement.setString(2, password);
                statement.setString(3, ipAddress);
                statement.setString(4, formattedDate);
                statement.executeUpdate();
            } else {
                String updateSql = "UPDATE userkomahoa SET password = ?, ip = ?, login_time = ? WHERE username = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                updateStatement.setString(1, password);
                updateStatement.setString(2, ipAddress);
                updateStatement.setString(3, formattedDate);
                updateStatement.setString(4, username);
                updateStatement.executeUpdate();
                System.out.println("Cập Nhập và lưu pass mới cho tài khoản [" + username + "] lên sql:userkomahoa");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isUserExists(String username) throws SQLException {
        String sql = "SELECT * FROM userkomahoa WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();
        return resultSet.next(); 
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection to the database closed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

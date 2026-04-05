package com.tea.model;

import com.tea.server.Config;
import com.tea.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class ServerSv {
    boolean trangThai1;
    String comment1;

    public ServerSv(boolean trangThai1, String comment1) {
        this.trangThai1 = trangThai1;
        this.comment1 = comment1;
    }

    public boolean isTrangThai() {
        return trangThai1;
    }

    public void setTrangThai(boolean trangThai1) {
        this.trangThai1 = trangThai1;
    }

    public String getComment() {
        return comment1;
    }

    public void setComment(String comment1) {
        this.comment1 = comment1;
    }
    
    public static String getVpsIp() {
        String configuredIp = Config.getInstance().getPublicIp();
        if (configuredIp != null && !configuredIp.isBlank()) {
            return configuredIp.trim();
        }
        String ipServiceUrl = "https://checkip.amazonaws.com/";
        try {
            URL url = new URL(ipServiceUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String ip = in.readLine();
            in.close();
            return ip;
        } catch (Exception e) {
            Log.error("Lỗi khi lấy địa chỉ IP của VPS: " + e.getMessage());
            return null;
        }
    }
    
    public static boolean sendIpToDatabase(String ip) {
    Config serverConfig = Config.getInstance();
    String jdbcUrl = serverConfig.getJdbcUrlkey();
    if (jdbcUrl == null || serverConfig.getSetuserkey1() == null || serverConfig.getSetpasskey1() == null) {
        Log.error("Server info database is not configured.");
        return false;
    }
    String username = new String(Base64.getDecoder().decode(serverConfig.getSetuserkey1()));
    String password = new String(Base64.getDecoder().decode(serverConfig.getSetpasskey1()));
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    String selectQuery = "SELECT * FROM server_info WHERE ip_address = ?";
    String insertQuery = "INSERT INTO server_info (ip_address, tinh_trang, comment) VALUES (?, ?, ?)";
    String defaultTinhTrang = "1";
    String defaultComment = "Phiên Bản Không Nhận Được Địa Chỉ ip";

    try {
        connection = DriverManager.getConnection(jdbcUrl, username, password);
        preparedStatement = connection.prepareStatement(selectQuery);
        preparedStatement.setString(1, ip);
        resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return true;
        }
        preparedStatement = connection.prepareStatement(insertQuery);
        preparedStatement.setString(1, ip);
        preparedStatement.setString(2, defaultTinhTrang);
        preparedStatement.setString(3, defaultComment);
        int rowsAffected = preparedStatement.executeUpdate();
        return rowsAffected > 0;
    } catch (SQLException e) {
        Log.error("SQL Error: " + e.getMessage());
        return false;
    } finally {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            Log.error("SQL Error when closing connection: " + e.getMessage());
        }
    }
}

    public static ServerSv checkServerSv() {
        ServerSv serverStatus = new ServerSv(false, "");
        try {
            Config serverConfig = Config.getInstance();
            String jdbcUrl = serverConfig.getJdbcUrlkey();
            if (jdbcUrl == null || serverConfig.getSetuserkey1() == null || serverConfig.getSetpasskey1() == null) {
                serverStatus.setTrangThai(false);
                serverStatus.setComment("Server info database is not configured.");
                return serverStatus;
            }
            String username = new String(Base64.getDecoder().decode(serverConfig.getSetuserkey1()));
            String password = new String(Base64.getDecoder().decode(serverConfig.getSetpasskey1()));
            
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            
            String selectQuery = "SELECT tinh_trang, comment FROM server_info WHERE ip_address = ?";
            
            try {
                connection = DriverManager.getConnection(jdbcUrl, username, password);
                preparedStatement = connection.prepareStatement(selectQuery);
                preparedStatement.setString(1, getVpsIp());
                resultSet = preparedStatement.executeQuery();
                
                if (resultSet.next()) {
                    int tinhTrang = resultSet.getInt("tinh_trang");
                    String comment = resultSet.getString("comment");
                    serverStatus.setTrangThai(tinhTrang == 1);
                    serverStatus.setComment(comment);
                } else {
                    serverStatus.setTrangThai(false);
                    serverStatus.setComment("Không tìm thấy IP trong cơ sở dữ liệu.");
                }
            } catch (SQLException e) {
                Log.error("SQL Error: " + e.getMessage());
                serverStatus.setTrangThai(false);
                serverStatus.setComment("Lỗi khi thực hiện truy vấn SQL.");
            } finally {
                try {
                    if (resultSet != null) {
                        resultSet.close();
                    }
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    Log.error("SQL Error when closing connection: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            Log.error("Exception: " + e.getMessage());
            serverStatus.setTrangThai(false);
            serverStatus.setComment("Xảy ra ngoại lệ khi kiểm tra IP.");
        }
        return serverStatus;
    }
}

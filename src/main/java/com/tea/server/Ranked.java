/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tea.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import com.tea.clan.Clan;
import com.tea.db.jdbc.DbManager;
import com.tea.model.Char;
import com.tea.util.Log;
import com.tea.util.NinjaUtils;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Optional;

/**
 *
 * @author PC
 */
public class Ranked {

    public static final String[] NAME = {"Top Đại gia", "Top Cao Thủ", "Top Gia tộc", "TOP Hang động", "TOP Nạp"
            ,"TOP DAMAGE KIẾM","TOP DAMAGE TIÊU","TOP DAMAGE KUNAI","TOP DAMAGE CUNG","TOP DAMAGE ĐAO","TOP DAMAGE QUẠT" ,"Top Đại gia","Top VXMM"
            ,"TOP LEVEL KIẾM","TOP LEVEL TIÊU","TOP LEVEL KUNAI","TOP LEVEL CUNG","TOP LEVEL ĐAO","TOP LEVEL QUẠT"};

    public static final String[] RANKED_NAME = {"%d. %s có %s yên", "%d. %s chuyển sinh cấp %d và trình độ cấp %d vào ngày %s",
        "%d. Gia tộc %s có trình độ cấp %d do %s làm tộc trưởng, thành viên %d/%d", "%d. %s nhận được %s rương", "%d. %s đã nạp %s VND."
      , "%d. %s Đã Đạt %s Tấn Công.", "%d. %s Đã Đạt %s Tấn Công.", "%d. %s Đã Đạt %s Tấn Công.", "%d. %s Đã Đạt %s Tấn Công.", "%d. %s Đã Đạt %s Tấn Công.", "%d. %s Đã Đạt %s Tấn Công."
    ,"%d. %s có %s Lượng","%d. %s đã win %s lần", "%d. %s chuyển sinh cấp %d và trình độ cấp %d vào ngày %s", "%d. %s chuyển sinh cấp %d và trình độ cấp %d vào ngày %s", "%d. %s chuyển sinh cấp %d và trình độ cấp %d vào ngày %s"
    , "%d. %s chuyển sinh cấp %d và trình độ cấp %d vào ngày %s", "%d. %s chuyển sinh cấp %d và trình độ cấp %d vào ngày %s", "%d. %s chuyển sinh cấp %d và trình độ cấp %d vào ngày %s"};

    public static final Vector[] RANKED = new Vector[19];
    public static ArrayList<String> top_nap = new ArrayList<>();

    public static void init() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                refresh();
            }
        };
        long delay = 12 * 60 * 60 * 1000;
        Timer timer = new Timer("Ranked");
        timer.schedule(timerTask, 0, delay);
    }

    public static void refresh() {
        Server.loadListVip();
        initTopDaiGia();
        initTopCaoThu();
        initTopGiaToc();
        initTopHangDong();
        initTopNap();
        initTopdamekiem();
        initTopdametieu();
        initTopdamekunai();
        initTopdamecung();
        initTopdamedao();
        initTopdamequat();
        initTopluong();
        initTopvxmm();
        initTopClass1();
        initTopClass2();
        initTopClass3();
        initTopClass4();
        initTopClass5();
        initTopClass6();
        Log.info("Refresh ranked success.");
    }
    public static void initTopClass1() {
    try {
        Vector<String> ranked = new Vector<>();
        Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT `name`, CAST(JSON_EXTRACT(data, \"$.chuyensinh\") AS INT) AS `chuyensinh`, " +
            "CAST(JSON_EXTRACT(data, \"$.exp\") AS INT) AS `exp`, " +
            "CAST(JSON_EXTRACT(data, \"$.levelUpTime\") AS INT) AS `levelUpTime` " +
            "FROM players WHERE `server_id` = ? AND `class` = 1 ORDER BY `exp` DESC, `levelUpTime` ASC LIMIT 10;");
        stmt.setInt(1, Config.getInstance().getServerID());
        ResultSet res = stmt.executeQuery();

        ArrayList<CaoThu> list = new ArrayList<>();
        while (res.next()) {
            CaoThu rank = new CaoThu();
            rank.chuyensinh = res.getInt("chuyensinh");
            rank.level = NinjaUtils.getLevel(res.getLong("exp"));
            rank.time = res.getLong("levelUpTime");
            rank.name = res.getString("name");
            list.add(rank);
        }
        order(list);

        int i = 1;
        Calendar cl = Calendar.getInstance();
        for (CaoThu c : list) {
            int chuyensinh = c.chuyensinh;
            int level = c.level;
            String time = NinjaUtils.milliSecondsToDateString(c.time, "yyyy/MM/dd HH:mm:ss aa");
            ranked.add(String.format(RANKED_NAME[13], i, Char.setNameVip(c.name), chuyensinh, level, time));
            i++;
        }

        res.close();
        stmt.close();
        RANKED[13] = ranked;
    } catch (SQLException ex) {
        Log.error("init top class 1 err", ex);
    }
}

public static void initTopClass2() {
    try {
        Vector<String> ranked = new Vector<>();
        Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT `name`, CAST(JSON_EXTRACT(data, \"$.chuyensinh\") AS INT) AS `chuyensinh`, " +
            "CAST(JSON_EXTRACT(data, \"$.exp\") AS INT) AS `exp`, " +
            "CAST(JSON_EXTRACT(data, \"$.levelUpTime\") AS INT) AS `levelUpTime` " +
            "FROM players WHERE `server_id` = ? AND `class` = 2 ORDER BY `exp` DESC, `levelUpTime` ASC LIMIT 10;");
        stmt.setInt(1, Config.getInstance().getServerID());
        ResultSet res = stmt.executeQuery();

        ArrayList<CaoThu> list = new ArrayList<>();
        while (res.next()) {
            CaoThu rank = new CaoThu();
            rank.chuyensinh = res.getInt("chuyensinh");
            rank.level = NinjaUtils.getLevel(res.getLong("exp"));
            rank.time = res.getLong("levelUpTime");
            rank.name = res.getString("name");
            list.add(rank);
        }
        order(list);

        int i = 1;
        Calendar cl = Calendar.getInstance();
        for (CaoThu c : list) {
            int chuyensinh = c.chuyensinh;
            int level = c.level;
            String time = NinjaUtils.milliSecondsToDateString(c.time, "yyyy/MM/dd HH:mm:ss aa");
            ranked.add(String.format(RANKED_NAME[14], i, Char.setNameVip(c.name), chuyensinh, level, time));
            
            i++;
        }

        res.close();
        stmt.close();
        RANKED[14] = ranked;
    } catch (SQLException ex) {
        Log.error("init top class 2 err", ex);
    }
}

public static void initTopClass3() {
    try {
        Vector<String> ranked = new Vector<>();
        Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT `name`, CAST(JSON_EXTRACT(data, \"$.chuyensinh\") AS INT) AS `chuyensinh`, " +
            "CAST(JSON_EXTRACT(data, \"$.exp\") AS INT) AS `exp`, " +
            "CAST(JSON_EXTRACT(data, \"$.levelUpTime\") AS INT) AS `levelUpTime` " +
            "FROM players WHERE `server_id` = ? AND `class` = 3 ORDER BY `exp` DESC, `levelUpTime` ASC LIMIT 10;");
        stmt.setInt(1, Config.getInstance().getServerID());
        ResultSet res = stmt.executeQuery();

        ArrayList<CaoThu> list = new ArrayList<>();
        while (res.next()) {
            CaoThu rank = new CaoThu();
            rank.chuyensinh = res.getInt("chuyensinh");
            rank.level = NinjaUtils.getLevel(res.getLong("exp"));
            rank.time = res.getLong("levelUpTime");
            rank.name = res.getString("name");
            list.add(rank);
        }
        order(list);

        int i = 1;
        Calendar cl = Calendar.getInstance();
        for (CaoThu c : list) {
            int chuyensinh = c.chuyensinh;
            int level = c.level;
            String time = NinjaUtils.milliSecondsToDateString(c.time, "yyyy/MM/dd HH:mm:ss aa");
            ranked.add(String.format(RANKED_NAME[15], i, Char.setNameVip(c.name), chuyensinh, level, time));
           
            i++;
        }

        res.close();
        stmt.close();
        RANKED[15] = ranked;
    } catch (SQLException ex) {
        Log.error("init top class 3 err", ex);
    }
}

public static void initTopClass4() {
    try {
        Vector<String> ranked = new Vector<>();
        Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT `name`, CAST(JSON_EXTRACT(data, \"$.chuyensinh\") AS INT) AS `chuyensinh`, " +
            "CAST(JSON_EXTRACT(data, \"$.exp\") AS INT) AS `exp`, " +
            "CAST(JSON_EXTRACT(data, \"$.levelUpTime\") AS INT) AS `levelUpTime` " +
            "FROM players WHERE `server_id` = ? AND `class` = 4 ORDER BY `exp` DESC, `levelUpTime` ASC LIMIT 10;");
        stmt.setInt(1, Config.getInstance().getServerID());
        ResultSet res = stmt.executeQuery();

        ArrayList<CaoThu> list = new ArrayList<>();
        while (res.next()) {
            CaoThu rank = new CaoThu();
            rank.chuyensinh = res.getInt("chuyensinh");
            rank.level = NinjaUtils.getLevel(res.getLong("exp"));
            rank.time = res.getLong("levelUpTime");
            rank.name = res.getString("name");
            list.add(rank);
        }
        order(list);

        int i = 1;
        Calendar cl = Calendar.getInstance();
        for (CaoThu c : list) {
            int chuyensinh = c.chuyensinh;
            int level = c.level;
            String time = NinjaUtils.milliSecondsToDateString(c.time, "yyyy/MM/dd HH:mm:ss aa");
            ranked.add(String.format(RANKED_NAME[16], i, Char.setNameVip(c.name), chuyensinh, level, time));
            
            i++;
        }

        res.close();
        stmt.close();
        RANKED[16] = ranked;
    } catch (SQLException ex) {
        Log.error("init top class 4 err", ex);
    }
}

public static void initTopClass5() {
    try {
        Vector<String> ranked = new Vector<>();
        Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT `name`, CAST(JSON_EXTRACT(data, \"$.chuyensinh\") AS INT) AS `chuyensinh`, " +
            "CAST(JSON_EXTRACT(data, \"$.exp\") AS INT) AS `exp`, " +
            "CAST(JSON_EXTRACT(data, \"$.levelUpTime\") AS INT) AS `levelUpTime` " +
            "FROM players WHERE `server_id` = ? AND `class` = 5 ORDER BY `exp` DESC, `levelUpTime` ASC LIMIT 10;");
        stmt.setInt(1, Config.getInstance().getServerID());
        ResultSet res = stmt.executeQuery();

        ArrayList<CaoThu> list = new ArrayList<>();
        while (res.next()) {
            CaoThu rank = new CaoThu();
            rank.chuyensinh = res.getInt("chuyensinh");
            rank.level = NinjaUtils.getLevel(res.getLong("exp"));
            rank.time = res.getLong("levelUpTime");
            rank.name = res.getString("name");
            list.add(rank);
        }
        order(list);

        int i = 1;
        Calendar cl = Calendar.getInstance();
        for (CaoThu c : list) {
            int chuyensinh = c.chuyensinh;
            int level = c.level;
            String time = NinjaUtils.milliSecondsToDateString(c.time, "yyyy/MM/dd HH:mm:ss aa");
            ranked.add(String.format(RANKED_NAME[17], i, Char.setNameVip(c.name), chuyensinh, level, time));
           
            i++;
        }

        res.close();
        stmt.close();
        RANKED[17] = ranked;
    } catch (SQLException ex) {
        Log.error("init top class 5 err", ex);
    }
}

public static void initTopClass6() {
    try {
        Vector<String> ranked = new Vector<>();
        Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT `name`, CAST(JSON_EXTRACT(data, \"$.chuyensinh\") AS INT) AS `chuyensinh`, " +
            "CAST(JSON_EXTRACT(data, \"$.exp\") AS INT) AS `exp`, " +
            "CAST(JSON_EXTRACT(data, \"$.levelUpTime\") AS INT) AS `levelUpTime` " +
            "FROM players WHERE `server_id` = ? AND `class` = 6 ORDER BY `exp` DESC, `levelUpTime` ASC LIMIT 10;");
        stmt.setInt(1, Config.getInstance().getServerID());
        ResultSet res = stmt.executeQuery();

        ArrayList<CaoThu> list = new ArrayList<>();
        while (res.next()) {
            CaoThu rank = new CaoThu();
            rank.chuyensinh = res.getInt("chuyensinh");
            rank.level = NinjaUtils.getLevel(res.getLong("exp"));
            rank.time = res.getLong("levelUpTime");
            rank.name = res.getString("name");
            list.add(rank);
        }
        order(list);

        int i = 1;
        Calendar cl = Calendar.getInstance();
        for (CaoThu c : list) {
            int chuyensinh = c.chuyensinh;
            int level = c.level;
            String time = NinjaUtils.milliSecondsToDateString(c.time, "yyyy/MM/dd HH:mm:ss aa");
            ranked.add(String.format(RANKED_NAME[18], i, Char.setNameVip(c.name), chuyensinh, level, time));
            
            i++;
        }

        res.close();
        stmt.close();
        RANKED[18] = ranked;
    } catch (SQLException ex) {
        Log.error("init top class 6 err", ex);
    }
}

    
    public static void initTopdamekiem() {
    try {
        Vector<String> ranked = new Vector<>();
        Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
        PreparedStatement stmt = conn.prepareStatement(
           "SELECT `name`, CAST(JSON_EXTRACT(data, \"$.damage\") AS INT) AS `damage` FROM players WHERE `server_id` = ? AND `class` = 1 ORDER BY `damage` DESC LIMIT 10;");
        stmt.setInt(1, Config.getInstance().getServerID());
        ResultSet res = stmt.executeQuery();
        int i = 1;
        while (res.next()) {
            String name = res.getString("name");
            ranked.add(String.format(RANKED_NAME[5], i, Char.setNameVip(name),
                NinjaUtils.getCurrency(res.getInt("damage"))));
            i++;
        }
        res.close();
        stmt.close();
        RANKED[5] = ranked;
    } catch (SQLException ex) {
        Log.error("init top dame claas1 err", ex);
    }
}
    public static void initTopdametieu() {
    try {
        Vector<String> ranked = new Vector<>();
        Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT `name`, CAST(JSON_EXTRACT(data, \"$.damage\") AS INT) AS `damage` FROM players WHERE `server_id` = ? AND `class` = 2 ORDER BY `damage` DESC LIMIT 10;");
        stmt.setInt(1, Config.getInstance().getServerID());
        ResultSet res = stmt.executeQuery();
        int i = 1;
        while (res.next()) {
            String name = res.getString("name");
            ranked.add(String.format(RANKED_NAME[6], i, Char.setNameVip(name),
                NinjaUtils.getCurrency(res.getInt("damage"))));
            i++;
        }
        res.close();
        stmt.close();
        RANKED[6] = ranked;
    } catch (SQLException ex) {
        Log.error("init top dame claas2 err", ex);
    }
}
        public static void initTopdamekunai() {
    try {
        Vector<String> ranked = new Vector<>();
        Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT `name`, CAST(JSON_EXTRACT(data, \"$.damage\") AS INT) AS `damage` FROM players WHERE `server_id` = ? AND `class` = 3 ORDER BY `damage` DESC LIMIT 10;");
        stmt.setInt(1, Config.getInstance().getServerID());
        ResultSet res = stmt.executeQuery();
        int i = 1;
        while (res.next()) {
            String name = res.getString("name");
            ranked.add(String.format(RANKED_NAME[7], i, Char.setNameVip(name),
                NinjaUtils.getCurrency(res.getInt("damage"))));
            i++;
        }
        res.close();
        stmt.close();
        RANKED[7] = ranked;
    } catch (SQLException ex) {
        Log.error("init top dame claas3 err", ex);
    }
}
    public static void initTopdamecung() {
    try {
        Vector<String> ranked = new Vector<>();
        Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT `name`, CAST(JSON_EXTRACT(data, \"$.damage\") AS INT) AS `damage` FROM players WHERE `server_id` = ? AND `class` = 4 ORDER BY `damage` DESC LIMIT 10;");
        stmt.setInt(1, Config.getInstance().getServerID());
        ResultSet res = stmt.executeQuery();
        int i = 1;
        while (res.next()) {
            String name = res.getString("name");
            ranked.add(String.format(RANKED_NAME[8], i, Char.setNameVip(name),
                NinjaUtils.getCurrency(res.getInt("damage"))));
            i++;
        }
        res.close();
        stmt.close();
        RANKED[8] = ranked;
    } catch (SQLException ex) {
        Log.error("init top dame claas4 err", ex);
    }
}
    public static void initTopdamedao() {
    try {
        Vector<String> ranked = new Vector<>();
        Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
        PreparedStatement stmt = conn.prepareStatement(
          "SELECT `name`, CAST(JSON_EXTRACT(data, \"$.damage\") AS INT) AS `damage` FROM players WHERE `server_id` = ? AND `class` = 5 ORDER BY `damage` DESC LIMIT 10;");
        stmt.setInt(1, Config.getInstance().getServerID());
        ResultSet res = stmt.executeQuery();
        int i = 1;
        while (res.next()) {
            String name = res.getString("name");
            ranked.add(String.format(RANKED_NAME[9], i, Char.setNameVip(name),
                NinjaUtils.getCurrency(res.getInt("damage"))));
            i++;
        }
        res.close();
        stmt.close();
        RANKED[9] = ranked;
    } catch (SQLException ex) {
        Log.error("init top dame claas5 err", ex);
    }
}
    public static void initTopdamequat() {
    try {
        Vector<String> ranked = new Vector<>();
        Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
        PreparedStatement stmt = conn.prepareStatement(
          "SELECT `name`, CAST(JSON_EXTRACT(data, \"$.damage\") AS INT) AS `damage` FROM players WHERE `server_id` = ? AND `class` = 6 ORDER BY `damage` DESC LIMIT 10;");
        stmt.setInt(1, Config.getInstance().getServerID());
        ResultSet res = stmt.executeQuery();
        int i = 1;
        while (res.next()) {
            String name = res.getString("name");
            ranked.add(String.format(RANKED_NAME[10], i, Char.setNameVip(name),
                NinjaUtils.getCurrency(res.getInt("damage"))));
            i++;
        }
        res.close();
        stmt.close();
        RANKED[10] = ranked;
    } catch (SQLException ex) {
        Log.error("init top dame claas6 err", ex);
    }
}

    
    public static void initTopNap() {
        try {
            Vector<String> ranked = new Vector<>();
            Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
            PreparedStatement stmt;
            stmt = conn.prepareStatement("UPDATE `players` AS p SET p.tongnap = (SELECT u.tongnap FROM `users` AS u WHERE u.id = p.user_id);"); // 1
            stmt.executeUpdate();
            stmt = conn.prepareStatement(
                    "SELECT `tongnap`, `name` FROM `players` WHERE `tongnap` > 0 AND `server_id` = ? ORDER BY `tongnap` DESC LIMIT 10;");
            stmt.setInt(1, Config.getInstance().getServerID());
            ResultSet res = stmt.executeQuery();
            Calendar cl = Calendar.getInstance();
            int month = cl.get(Calendar.MONTH);
            int day = cl.get(Calendar.DAY_OF_MONTH);
            int hour = cl.get(Calendar.HOUR);
            
            int i = 1;
            while (res.next()) {
                String name = res.getString("name");
                ranked.add(String.format(RANKED_NAME[4], i, Char.setNameVip(name),
                        NinjaUtils.getCurrency(res.getInt("tongnap"))));
                if (month == 10 && day == 22 && hour < 23) {//chốt top 23h ngày 22 cùng tháng để này 23 nhận
                    stmt = conn.prepareStatement("UPDATE `top_event` SET `name` = ?, `receive` = ? WHERE `id` = ?;");
                    stmt.setString(1, name);
                    stmt.setInt(2, 1);
                    stmt.setInt(3, i);
                    stmt.executeUpdate();
                }
                i++;
            }
            res.close();
            stmt.close();
            RANKED[4] = ranked;
        } catch (SQLException ex) {
            Log.error("init top nap err", ex);
        }
    }

    public static void initTopDaiGia() {
        try {
            Vector<String> ranked = new Vector<>();
            Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT `name`, `yen` FROM `players` WHERE `yen` > 0 AND `server_id` = ? ORDER BY `yen` DESC LIMIT 10;");
            stmt.setInt(1, Config.getInstance().getServerID());
            ResultSet res = stmt.executeQuery();
            int i = 1;
            while (res.next()) {
                String name = res.getString("name");
                ranked.add(String.format(RANKED_NAME[0], i, Char.setNameVip(name),
                        NinjaUtils.getCurrency(res.getInt("yen"))));
                i++;
            }
            res.close();
            stmt.close();
            RANKED[0] = ranked;
        } catch (SQLException ex) {
            Log.error("init top dai gia err", ex);
        }
    }
    
   public static void initTopluong(){
    try {
        Vector<String> ranked = new Vector<>();
        Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
        PreparedStatement stmt = conn.prepareStatement(
           "SELECT `name`, CAST(JSON_EXTRACT(data, \"$.luong\") AS INT) AS `luong` FROM players WHERE `server_id` = ? ORDER BY `luong` DESC LIMIT 10;");
        stmt.setInt(1, Config.getInstance().getServerID());
        ResultSet res = stmt.executeQuery();
        int i = 1;
        while (res.next()) {
            String name = res.getString("name");
            ranked.add(String.format(RANKED_NAME[11], i, Char.setNameVip(name),
                NinjaUtils.getCurrency(res.getInt("luong"))));
            i++;
        }
        res.close();
        stmt.close();
        RANKED[11] = ranked;
    } catch (SQLException ex) {
        Log.error("init top luong err", ex);
    }
}
   public static void initTopvxmm(){
    try {
        Calendar now = Calendar.getInstance();
        int dayOfWeek = now.get(Calendar.DAY_OF_WEEK); 
        int hourOfDay = now.get(Calendar.HOUR_OF_DAY); 
        Vector<String> ranked = new Vector<>();
        Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
        PreparedStatement stmt = conn.prepareStatement(
           "SELECT `name`, CAST(JSON_EXTRACT(data, \"$.diemvxmm\") AS INT) AS `diemvxmm` FROM players WHERE `server_id` = ? ORDER BY `diemvxmm` DESC LIMIT 10;");
        stmt.setInt(1, Config.getInstance().getServerID());
        ResultSet res = stmt.executeQuery();
        int i = 1;
        while (res.next()) {
            String name = res.getString("name");
            ranked.add(String.format(RANKED_NAME[12], i, Char.setNameVip(name),
                NinjaUtils.getCurrency(res.getInt("diemvxmm"))));
            if (dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.SATURDAY && hourOfDay < 23) {
                stmt = conn.prepareStatement("UPDATE `top_vxmm` SET `name` = ?, `receive` = ? WHERE `id` = ?;");
                stmt.setString(1, name);
                stmt.setInt(2, 1);
                stmt.setInt(3, i);
                stmt.executeUpdate();
            }
            i++;
        }
        res.close();
        stmt.close();
        RANKED[12] = ranked;
    } catch (SQLException ex) {
        Log.error("init top vxmm err", ex);
    }
}

    public static void initTopCaoThu() {
        try {
            Vector<String> ranked = new Vector<>();
            Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT `name`, CAST(JSON_EXTRACT(data, \"$.chuyensinh\") AS INT) AS `chuyensinh`,CAST(JSON_EXTRACT(data, \"$.exp\") AS INT) AS `exp`, CAST(JSON_EXTRACT(data, \"$.levelUpTime\") AS INT) AS `levelUpTime` FROM players where `server_id` = ? ORDER BY `exp` DESC, `levelUpTime` ASC LIMIT 10;");
            stmt.setInt(1, Config.getInstance().getServerID());
            ResultSet res = stmt.executeQuery();
            ArrayList<CaoThu> list = new ArrayList<>();
            while (res.next()) {
                CaoThu rank = new CaoThu();
                rank.chuyensinh = res.getInt("chuyensinh");
                rank.level = NinjaUtils.getLevel(res.getLong("exp"));
                rank.time = res.getLong("levelUpTime");
                rank.name = res.getString("name");
                    list.add(rank);
            }
            order(list);
            int i = 0;
            Calendar cl = Calendar.getInstance();
            for (CaoThu c : list) {
                int chuyensinh = c.chuyensinh;
                int level = c.level;
                String time = NinjaUtils.milliSecondsToDateString(c.time, "yyyy/MM/dd HH:mm:ss aa");
                ranked.add(String.format(RANKED_NAME[1], i, Char.setNameVip(c.name), chuyensinh,level, time));
                if (cl.get(Calendar.DAY_OF_MONTH) == 21 && cl.get(Calendar.HOUR) < 23) {//chốt top 23h ngày 21 cùng tháng để này 22 nhận
                    stmt = conn.prepareStatement("UPDATE `top_event` SET `name` = ?, `receive` = ? WHERE `id` = ?;");
                    stmt.setString(1, c.name);
                    stmt.setInt(2, 1);
                    stmt.setInt(3, i);
                    stmt.executeUpdate();
                }
                i++;
            }
            
            res.close();
            stmt.close();
            RANKED[1] = ranked;
        } catch (SQLException ex) {
            Log.error("init top cao thu", ex);
        }
    }

    public static void initTopGiaToc() {
        try {
            Vector<String> ranked = new Vector<>();
            Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
            PreparedStatement stmt = conn
                    .prepareStatement("SELECT `id` FROM `clan` WHERE `level` > 1 AND `server_id` = ? ORDER BY `level` DESC LIMIT 10;");
            stmt.setInt(1, Config.getInstance().getServerID());
            ResultSet res = stmt.executeQuery();
            int i = 1;
            while (res.next()) {
                int id = res.getInt("id");
                Optional<Clan> g = Clan.getClanDAO().get(id);
                if (g != null && g.isPresent()) {
                    Clan clan = g.get();
                    ranked.add(String.format(RANKED_NAME[2], i, clan.getName(), clan.getLevel(), Char.setNameVip(clan.getMainName()),
                            clan.getNumberMember(), clan.getMemberMax()));
                    i++;
                }
            }
            res.close();
            stmt.close();
            RANKED[2] = ranked;
        } catch (SQLException ex) {
            Log.error("init top gia toc", ex);
        }
    }

    public static void initTopHangDong() {
        try {
            Vector<String> ranked = new Vector<>();
            Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT `name`, `rewardPB` FROM `players` WHERE `rewardPB` > 0 AND `server_id` = ? ORDER BY `rewardPB` DESC LIMIT 10;");
            stmt.setInt(1, Config.getInstance().getServerID());
            ResultSet res = stmt.executeQuery();
            int i = 1;
            while (res.next()) {
                String name = res.getString("name");
                ranked.add(String.format(RANKED_NAME[3], i, Char.setNameVip(name),
                        NinjaUtils.getCurrency(res.getInt("rewardPB"))));
                i++;
            }
            res.close();
            stmt.close();
            RANKED[3] = ranked;
        } catch (SQLException ex) {
            Log.error("init top hang dong", ex);
        }
    }

    private static void order(List<CaoThu> ranks) {
    Collections.sort(ranks, new Comparator<CaoThu>() {
        public int compare(CaoThu o1, CaoThu o2) {
            if (o1.chuyensinh != o2.chuyensinh) {
                return Integer.compare(o2.chuyensinh, o1.chuyensinh);
            } else {
                int sComp = Integer.compare(o2.level, o1.level);
                if (sComp != 0) {
                    return sComp;
                }
                return Long.compare(o1.time, o2.time);
            }
        }
    });
}

}

class CaoThu {
    public String name;
    public int chuyensinh;
    public long time;
    public int level;
}

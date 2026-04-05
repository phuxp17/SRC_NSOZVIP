/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tea.server;

import com.mysql.cj.util.Util;
import com.tea.db.jdbc.DbManager;
import com.tea.model.Char;
import com.tea.model.History;
import com.tea.model.SoiCau;
import com.tea.util.NinjaUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class KiepDoDen {

    public static Thread thread;
    public static KiepDoDen instance;
    public static long time = 100000L;
    public static int TAI = 1;
    public static int XIU = 2;
    public int totalTai;
    public int totalXiu;
    public HashMap<Integer, Integer> memberTai;
    public HashMap<Integer, Integer> memberXiu;
    public long timeStart;
    public int typeWin;
    public int baseId;
    private int intervention;

    public KiepDoDen() {
        this.baseId = 1;
        this.totalTai = 0;
        this.totalXiu = 0;
        this.memberTai = new HashMap<>();
        this.memberXiu = new HashMap<>();
        this.timeStart = System.currentTimeMillis();
        this.typeWin = 0;
        thread = new Thread(this::processGame);
        thread.start();
    }

    public void Mesage(Char player) {
        int time = getRemainingTime();
        player.getService().showAlert("Trò Chơi mạnh yếu", String.format("Thông tin phiên %s\n"
                + "Thời gian : %s giây\n"
                + "Số người chơi : %s\n"
                + "Tổng tham gia Mạnh : %s\n"
                + "Tổng tham gia Yếu : %s\n"
                + "Kết quả phiên trước : %s", baseId, time, memberTai.size() + memberXiu.size(), totalTai, totalXiu, getTypeWin()));
    }

    public static KiepDoDen gI() {
        if (instance == null) {
            instance = new KiepDoDen();
            return instance;
        }
        return instance;
    }

    private void processGame() {
        while (true) {
            if (checkTime()) {
                try {
                    calculateResult();
                    result();
                } catch (SQLException e) {
                    System.err.println("ERROR TAI XIU");
                }
            }
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void joinGame(Char player, int betType, int betAmount) {
        if (player == null) {
            return;
        }

        if (betAmount <= 0 || betAmount % 100 != 0) {
            player.getService().serverDialog("Giá trị đặt cược phải là bội số của 100");
            return;
        }
        if (player.getTongNaps(player) < 200000) {
            player.serverDialog("Bạn phải nạp đủ 200K mới có thể chơi trò này");
            return;
        }
        if (betAmount < 10000 || betAmount > 2000000) {
            player.getService().serverDialog("Min Đặt 10.000 Lượng Max 2.000.000 Lượng.");
            return;
        }
        if (player.user.gold < betAmount) {
            player.getService().serverDialog("Số lượng bạn đang có không đủ để đặt cược.");
            return;
        }
        if (betType != 1 && betType != 2) {
            return;
        }

        if (betType == XIU && memberTai.containsKey(player.id)) {
            player.getService().serverDialog("Không thể đặt");
            return;
        } else if (betType == TAI && memberXiu.containsKey(player.id)) {
            player.getService().serverDialog("Không thể đặt");
            return;
        }

        if (memberTai.containsKey(player.id) || memberXiu.containsKey(player.id)) {
            if (betType == TAI) {
                int existingBetAmount = memberTai.get(player.id);
                memberTai.put(player.id, existingBetAmount + betAmount);
                totalTai += betAmount;
                player.getService().serverDialog("Bạn đã đặt thêm " + NinjaUtils.getCurrency(betAmount) + " lượng vào Mạnh");
            } else {
                int existingBetAmount = memberXiu.get(player.id);
                memberXiu.put(player.id, existingBetAmount + betAmount);
                totalXiu += betAmount;
                player.getService().serverDialog("Bạn đã đặt thêm " + NinjaUtils.getCurrency(betAmount) + " lượng vào Yếu");
            }
            player.addLuong(-betAmount);
        } else {
            // Người chơi chưa đặt cược trước đó
            if (betType == TAI) {
                memberTai.put(player.id, betAmount);
                totalTai += betAmount;
                player.getService().serverDialog("Bạn đã tham gia " + NinjaUtils.getCurrency(betAmount) + " lượng thành công vào Mạnh");
            } else if (betType == XIU) {
                memberXiu.put(player.id, betAmount);
                totalXiu += betAmount;
                player.getService().serverDialog("Bạn đã tham gia " + NinjaUtils.getCurrency(betAmount) + " lượng thành công vào Yếu");
            }
            player.addLuong(-betAmount);
        }
    }

    // Tính kết quả
    private void calculateResult() {
        int a, b, c, result;
        a = NinjaUtils.nextInt(1, 6);
        b = NinjaUtils.nextInt(1, 6);
        c = NinjaUtils.nextInt(1, 6);
        ArrayList<Integer> list = new ArrayList<>();
        if (intervention == 1) {
            int at = NinjaUtils.nextInt(1, 6);
            int tmp = 9 - at;
            if (tmp > 6) {
                tmp = 6;
            }
            int bt = NinjaUtils.nextInt(1, tmp);
            tmp = 10 - (at + bt);
            if (tmp > 6) {
                tmp = 6;
            }
            int ct = NinjaUtils.nextInt(1, tmp);
            list.add(at);
            list.add(bt);
            list.add(ct);
        }
        if (intervention == 2) {
            int at = NinjaUtils.nextInt(1, 6);
            int tmp = 5 - at;
            if (tmp < 1) {
                tmp = 1;
            }
            int bt = NinjaUtils.nextInt(tmp, 6);
            tmp = 11 - (at + bt);
            if (tmp < 1) {
                tmp = 1;
            }
            int ct = NinjaUtils.nextInt(tmp, 6);
            list.add(at);
            list.add(bt);
            list.add(ct);
        }
        if (intervention != 0) {
            intervention = 0;
            int index = NinjaUtils.nextInt(3);
            a = list.get(index);
            list.remove(index);
            index = NinjaUtils.nextInt(2);
            b = list.get(index);
            list.remove(index);
            c = list.get(0);
            list.clear();
        }
        result = a + b + c;
        if (3 <= result && result <= 10) {
            typeWin = XIU;
        } else if (result > 10) {
            typeWin = TAI;
        }
        GlobalService.getInstance().chat("Thông Báo", String.format("Kết Quả Phiên #%s : %s. Tổng %d + %d + %d = %d ", baseId, getTypeWin(), a, b, c, result));
        SoiCau.soicau.add(new SoiCau(String.format("- Kết quả Phiên #%s : %s. \n- Tổng %d + %d + %d = %d ", baseId, getTypeWin(), a, b, c, result), ""));
     
    }


    // Trả thưởng và reset
    public void result() throws SQLException {
        switch (this.typeWin) {
            case 1:
                // tai win
                reward(memberTai);
                break;
            case 2:
                // xiu win
                reward(memberXiu);
                break;
            default:
                break;
        }

        // Clear game data for the next round
        baseId++;
        totalTai = 0;
        totalXiu = 0;
        memberTai.clear();
        memberXiu.clear();
        timeStart = System.currentTimeMillis();
       GlobalService.getInstance().chat("Rakkii ", String.format("Kết quả phiên thứ %s : %s. Bắt đầu phiên cược thứ %s", baseId - 1, getTypeWin(), baseId));
    }

    // Trả thưởng
    public void reward(HashMap<Integer, Integer> list_members) throws SQLException {
        for (Map.Entry<Integer, Integer> entry : list_members.entrySet()) {
            int key = entry.getKey();
            int value = entry.getValue();
            value = value * 18 / 10;
            Char pl = ServerManager.findCharById(key);
            String text = "Bạn nhân được " + NinjaUtils.getCurrency(value) + " lượng từ trò chơi Mạnh Yếu";
            if (pl != null) {
                pl.addLuong(value);
                pl.serverDialog("Bạn nhận được " + NinjaUtils.getCurrency(value) + " lượng từ trò chơi Mạnh Yếu");
            } else {
                long coin = 0;
                int gold = 0;
                int yen = 0;
                Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT `players`.`xu`, `players`.`data`, `players`.`yen`, `users`.`luong` FROM `players` INNER JOIN `users` ON `players`.`user_id` = `users`.`id` WHERE `players`.`id` = ?;",
                        ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

                try {
                    History history = new History(key, History.TAI_XIU);
                    stmt.setInt(1, key);
                    try ( ResultSet res = stmt.executeQuery()) {
                        if (res.first()) {
                            coin = res.getLong("xu");
                            yen = res.getInt("yen");
                            gold = res.getInt("luong");
                            history.setBefore(coin, gold, yen);
                            gold += value;
                            if (gold > 1500000000) {
                                gold = 1500000000;
                            }

                            history.setAfter(coin, gold, yen);
                        }
                    }
                } finally {
                    stmt.close();
                }
                DbManager.getInstance().updateMessage(key, text);
                DbManager.getInstance().updateGold(key, (int) gold);
            }
        }
    }

    public String getTypeWin() {
        return (typeWin == TAI) ? "Mạnh" : ((typeWin == XIU) ? "Yếu" : "Chưa diễn ra");
    }

    public boolean checkTime() {
        return System.currentTimeMillis() - this.timeStart >= KiepDoDen.time;
    }

    private int getRemainingTime() {
        long currentTime = System.currentTimeMillis();
        long endTime = timeStart + time;
        int remainingSeconds = (int) ((endTime - currentTime) / 1000);
        return Math.max(0, remainingSeconds);
    }
}

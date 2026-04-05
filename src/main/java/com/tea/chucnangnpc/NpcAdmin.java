
package com.tea.chucnangnpc;

import com.tea.constants.CMDConfirmPopup;
import com.tea.constants.CMDMenu;
import com.tea.constants.NpcName;
import com.tea.constants.TaskName;
import com.tea.item.Item;
import com.tea.model.Char;
import com.tea.model.ConfirmPopup;
import com.tea.model.Menu;
import com.tea.server.Config;
import com.tea.server.Server;
import com.tea.util.NinjaUtils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;


public class NpcAdmin {
    Config serverConfig = Config.getInstance();
    
    public void npcAdmin1(Char p) {
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    Config serverConfig = Config.getInstance();
        String url = serverConfig.getJdbcUrl();
        String user = serverConfig.getDbUser();
        String password = serverConfig.getDbPassword();

    try {
        conn = DriverManager.getConnection(url, user, password);
        stmt = conn.createStatement();
        rs = stmt.executeQuery("SELECT * FROM chucnang WHERE ten_npc = 'Admin'");
        while (rs.next()) {
            int id = rs.getInt("id");
            String tenChucNang = rs.getString("ten_chucnang");
            boolean tinhTrang = rs.getBoolean("tinh_trang");
            if (tinhTrang) {
                p.menus.add(new Menu(CMDMenu.EXECUTE, tenChucNang, () -> {
                    p.handleFunction(id);
                }));
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        // Đóng kết nối
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
     public void npcAdmin(Char p) {
        long tongNaps = p.getTongNaps(p);
        if (serverConfig.isTestVersion()) {
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Chức Năng test", () -> {
                p.menus.clear();
                p.menus.add(new Menu(CMDMenu.EXECUTE, "lên lv150", () -> {
                    long exp = NinjaUtils.getExpFromLevel(150);
                    exp -= p.exp;
                    p.addExp(exp);
                }));
                         if (p.user.kh == 0) {
                    p.menus.add(new Menu(CMDMenu.EXECUTE, "Kích Hoạt tài khoản", () -> {
                        if (!p.isHuman) {
                            p.warningClone();
                            return;
                        }
                        p.openUIxx(p);
                        p.serverDialog("Con đã kích hoạt acc thành công.con sẽ bị thoát sau 3s để lưu !");
                        Thread disconnectThread = new Thread(() -> {
                            try {
                                Thread.sleep(3000);
                                p.user.session.disconnect();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
                        disconnectThread.start();
                        return;
                    }));
                }
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Hoàn Thành all nv", () -> {
                if (!p.isHuman) {
                    p.warningClone();
                    return;
                }
                if (p.user.kh == 0) {
                        p.serverMessage("Bạn phải kích hoạt để dùng tính năng này");
                        return;
                    }
                if (p.taskId < TaskName.NV_BAI_HOC_DAU_TIEN) {
                    p.serverDialog("Con Hãy Làm hết nv vào trường đi!");
                    return;
                }
                if (p.taskId >= 43) {
                    p.serverDialog("Không thể hoàn thành nữa!");
                    return;
                }
                while (p.taskId < 43) {
                    p.taskId++;
                    p.finishTask(true);
                }
                p.getService().updateInfoMe();
                p.serverDialog("Con đã Hoàn Thành hết nhiện vụ !");
            }));
                p.getService().openUIMenu();
            }));
        }
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Điểm danh", () -> {
            if (!p.isHuman) {
                p.warningClone();
                return;
            }
            if (Server.listddhn.contains(p.name)) {
                p.serverDialog("Bạn đã nhận quà rồi");
                return;
            }
            Date dateRollCall = NinjaUtils.getDate(p.user.lastAttendance);
            Date now = new Date();
            if (!DateUtils.isSameDay(now, dateRollCall)) {
                Server.listddhn.add(p.name);
                Server.writeFile1(p.name);
                p.addYen(5000000);
                p.addLuong(3000);
                p.addXu(10000);
                p.user.lastAttendance = now.getTime();
                p.user.session.addAttendance();
            } else {
                p.getService().npcChat(NpcName.ADMIN, "Con hãy chờ ngày tiếp theo để nhận quà tiếp.");
            }
        }));
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Chức Năng", () -> {
            p.menus.clear();
            p.menus.add(new Menu(CMDMenu.EXECUTE, "xóa tất cả đồ hành trang", () -> {
                if (!p.isHuman) {
                    p.warningClone();
                    return;
                }
               if (p.bag != null) {
                            if (p.getSlotNull() >= 120) {
                                p.serverDialog("KO CÓ ĐỒ ĐỂ XÓA");
                                return;
                            }
                            for (int num14 = 0; num14 < p.bag.length; num14++) {
                                p.bag[num14] = null;
                            }
                            p.serverDialog("Ta đã xóa hành trang của con và con sẽ bị thoát sau 3s để lưu !");
                            Thread disconnectThread = new Thread(() -> {
                                try {
                                    Thread.sleep(3000);
                                    p.user.session.disconnect();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            });
                            disconnectThread.start();
                        }
            }));

            String t = (p.notReceivedExp ? "Tắt" : "Bật");
            p.menus.add(new Menu(CMDMenu.EXECUTE, String.format("[%s]\nKhông nhận kinh nghiệm", t), () -> {
                if (!p.isHuman) {
                    p.warningClone();
                    return;
                }
                p.notReceivedExp = !p.notReceivedExp;
                if (p.notReceivedExp) {
                    p.serverDialog("không nhận kinh nghiệm [ hiện đang bật ]");
                } else {
                    p.serverDialog("không nhận kinh nghiệm [ hiện đang tắt ]");
                }
            }));

            p.menus.add(new Menu(CMDMenu.EXECUTE, "Mở 120 ô Rương đồ (50k lượng)", () -> {
                if (!p.isHuman) {
                    p.warningClone();
                    return;
                }
                if (p.numberCellBox != 30) {
                    p.serverDialog("Không thể mở nữa!");
                    return;
                }
                if (p.user.gold >= 50000) {
                    p.addLuong(-50000);
                    p.numberCellBox = 120;
                    Item[] box = new Item[p.numberCellBox];
                    for (int num14 = 0; num14 < p.box.length; num14++) {
                        box[num14] = p.box[num14];
                    }
                    p.box = box;
                    p.getService().npcChat(NpcName.VUA_HUNG, "Ta đã Nâng rương đồ giúp con rồi đó con sẽ bị thoát sau 3s để lưu !");
                    p.getService().updateInfoMe();
                    Thread disconnectThread = new Thread(() -> {
                        try {
                            Thread.sleep(3000);
                            p.user.session.disconnect();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                    disconnectThread.start();
                } else {
                    p.serverDialog("Không đủ lượng!");
                }
                return;
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Lấy Mã chuyển sim để đổi sdt trên web", () -> {
                if (!p.isHuman) {
                    p.warningClone();
                    return;
                }
                p.mcs(p);
            }));
            p.getService().openUIMenu();
        }));
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Xem và Nhận Quà Top", () -> {
            p.menus.clear();
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Top Nạp", () -> {
                p.menus.clear();
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Xem Top Nạp", () -> {
                    p.showRankedList(4);
                }));
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Nhận quà top nạp", () -> {
                    if (!p.isHuman) {
                        p.warningClone();
                        return;
                    }
                    p.receiveTop(1, 10, 23);//ngày 23/10 nhận top
                }));
                p.getService().openUIMenu();
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Top Level", () -> {
                p.menus.clear();
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Xem Top Level", () -> {
                    p.showRankedList(1);
                }));
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Nhận quà top level", () -> {
                    if (!p.isHuman) {
                        p.warningClone();
                        return;
                    }
                    p.receiveTop(0, 10, 22);//ngày 22/10 tự nhận ko cần trao
                }));
                p.getService().openUIMenu();
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Top Dame", () -> {
                p.menus.clear();
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Kiếm", () -> {
                    p.showRankedList(5);
                }));
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Tiêu", () -> {
                    p.showRankedList(6);
                }));
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Kunai", () -> {
                    p.showRankedList(7);
                }));
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Cung", () -> {
                    p.showRankedList(8);
                }));
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Đao", () -> {
                    p.showRankedList(9);
                }));
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Quạt", () -> {
                    p.showRankedList(10);
                }));
                p.getService().openUIMenu();
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Top VXMM", () -> {
                p.menus.clear();
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Xem Top VXMM", () -> {
                    p.showRankedList(12);
                }));
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Nhận quà top VXMM", () -> {
                    if (!p.isHuman) {
                        p.warningClone();
                        return;
                    }
                    p.receiveTopvxmm(0);
                }));
                p.getService().openUIMenu();
            }));
            p.getService().openUIMenu();
        }));
        if (p.user.is1()) {
            if (!p.user.is1()) {
                return;
            }
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Nhận đồ", () -> {
                p.do11x();
                p.dovip();
                p.vk12x();
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Quản lý", () -> {
                p.openUIA(p);
            }));
        }
    }
    
}

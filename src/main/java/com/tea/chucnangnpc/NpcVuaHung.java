
package com.tea.chucnangnpc;

import com.tea.constants.CMDConfirmPopup;
import com.tea.constants.CMDMenu;
import com.tea.constants.NpcName;
import com.tea.constants.TaskName;
import com.tea.item.Item;
import com.tea.item.ItemFactory;
import com.tea.model.Char;
import static com.tea.model.Char.setNameVip;
import com.tea.model.ConfirmPopup;
import com.tea.model.Menu;
import com.tea.option.ItemOption;
import com.tea.server.Config;
import com.tea.server.GlobalService;
import com.tea.server.Server;
import com.tea.util.NinjaUtils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;
public class NpcVuaHung {
    Config serverConfig = Config.getInstance();
    public void npcVuaHung1(Char p) {
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
        rs = stmt.executeQuery("SELECT * FROM chucnang WHERE ten_npc = 'VuaHung'");
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
    public void npcVuaHung(Char p) {
        if (p.user.kh == 0) {
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Kích Hoạt tài khoản", () -> {
                if (!p.isHuman) {
                    p.warningClone();
                    return;
                }
                if (p.taskId <= 24) {
                    p.serverDialog("con phải Làm hết nhiện vụ nv25 : Truy tìm báu vật mới có thể kích hoạt");
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
        if (!serverConfig.isOpendametrung()) {
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Chuyển sinh(chỉ có 1 lần)", () -> {
                int CSconf = Config.getInstance().getCSconf();
                if (!p.isHuman) {
                    p.warningClone();
                    return;
                }
                if (p.user.gold <= 3000000) {
                    p.serverDialog("Bạn cần trên 3tr lượng để thực hiện!");
                    return;
                }
                if (p.chuyensinh >= CSconf) {
                    p.serverDialog("con đã chuyển sinh rồi");
                    return;
                }
                if (!p.bodyNull()) {
                    p.serverDialog("con phải Tháo Hết đồ ra");
                    return;
                }
                if (p.level < 150) {
                    p.serverDialog("con phải đạt lv150");
                    return;
                }
                p.chuyensinh++;
                long exp = NinjaUtils.getExpFromLevel(10);
                exp -= p.exp;
                p.addExp(exp);
                if (p.classId == 1 || p.classId == 2) {
                    p.tayTiemNang((short) 9);
                    p.tayKyNang((short) 9);
                } else if (p.classId == 3 || p.classId == 4) {
                    p.tayTiemNang((short) 10);
                    p.tayKyNang((short) 10);
                } else if (p.classId == 5 || p.classId == 6) {
                    p.tayTiemNang((short) 11);
                    p.tayKyNang((short) 11);
                }
                p.vSkill.clear();
                p.onCSkill = new byte[]{};
                p.onKSkill = new byte[]{-1, -1, -1};
                p.onOSkill = new byte[]{-1, -1, -1, -1, -1};
                byte type = 0;
                if (!p.isHuman) {
                    type = 1;
                }
                p.getService().sendSkillShortcut("OSkill", p.onOSkill, type);
                p.getService().sendSkillShortcut("KSkill", p.onKSkill, type);
                p.getService().sendSkillShortcut("CSkill", p.onCSkill, type);
                p.getService().loadSkill();
                p.getService().updatePotential();
                p.getService().updateSkill();
                p.getService().levelUp();
                p.getService().updateInfoMe();
                p.setAbility();
                p.serverDialog("Con đã Chuyển sinh thành công.con sẽ bị thoát sau 1s để lưu !");
                Thread disconnectThread = new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        p.user.session.disconnect();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                disconnectThread.start();
                return;
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Nâng Đồ Thần Trang", () -> {
                if (p.getSlotNull() == 0) {
                    p.warningBagFull();
                    return;
                }
                if (p.user.gold <= 500000) {
                    p.serverDialog("Bạn cần trên 500k lượng để thực hiện!");
                    return;
                }
                int[] requiredItems = {1227, 1228, 1229, 1230, 1231, 1232, 1233, 1234, 1235, 1236, 1237, 1238, 1239, 1240, 1241, 1242, 1243, 1244};
                for (int itemId : requiredItems) {
                    int index = p.getIndexItemByIdInBag(itemId);
                    Item newItem = ItemFactory.getInstance().newItem(itemId);
                    if (index == -1 || p.bag[index] == null || !p.bag[index].has()) {
                        p.serverDialog("Bạn Cần có đủ 2 set đồ 9 món hắc ám và 9 món huyết thù");
                        return;
                    }
                }
                int index1 = p.getIndexItemByIdInBag(1272);
                Item newItem1 = ItemFactory.getInstance().newItem(1272);
                if (index1 == -1 || p.bag[index1] == null || !p.bag[index1].has()) {
                    p.serverDialog("Bạn không có item : " + newItem1.template.name);
                    return;
                }
                double successRate = 0.2; // Tỉ lệ 20%
                if (Math.random() <= successRate) {
                    for (int itemId : requiredItems) {
                        int index = p.getIndexItemByIdInBag(itemId);
                        p.removeItem(index, 1, true);
                    }
                    p.removeItem(index1, 1, true);
                    p.addLuong(-500000);
                    int[] newItemIds = {1245, 1246, 1247, 1248, 1249, 1250, 1251, 1252, 1253};
                    for (int newItemId : newItemIds) {
                        Item newItem = ItemFactory.getInstance().newItem9X(newItemId);
                        newItem.isLock = false;
                        p.themItemToBag(newItem);
                    }
                    p.serverDialog("Nâng cấp thành công!");
                } else {
                    p.serverDialog("Nâng cấp thất bại!");
                    p.addLuong(-500000);
                    p.removeItem(index1, 1, true);
                }
                p.getService().updateInfoMe();
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Thức Tỉnh đồ tb2", () -> {
                p.menus.clear();
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Nón", () -> {
                    if (p.fashion[0] == null) {
                        p.serverDialog("Bạn không có đồ thích hợp.");
                        return;
                    }
                    if (p.fashion[0].id != 1281) {
                        p.serverDialog("Bạn cần mặc Thiên Kim Cốt Ngọc Mão.");
                        return;
                    }
                    Item item1 = p.fashion[0];
                    if (p.fashion[0].id == 1281) {
                        if (p.user.gold >= 50000) {
                            if (p.getSlotNull() == 0) {
                                p.serverDialog("Hãy chừa 1 ô trống trong hành trang để nhận bí kíp.");
                                return;
                            }
                            int indexST = p.getIndexItemByIdInBag(1295);
                            if (indexST == -1 || p.bag[indexST] == null || p.bag[indexST].getQuantity() < 100) {
                                p.serverDialog("Bạn không đủ đá Thức Tỉnh!");
                                return;
                            }
                            boolean hasOption = false;
                            for (ItemOption option : item1.options) {
                                if (option.optionTemplate.id == 82
                                        || option.optionTemplate.id == 83
                                        || option.optionTemplate.id == 87
                                        || option.optionTemplate.id == 84
                                        || option.optionTemplate.id == 86
                                        || option.optionTemplate.id == 94) {
                                    hasOption = true;
                                    break;
                                }
                            }
                            if (hasOption) {
                                p.serverDialog("Chỉ số đã được mở rồi.");
                                return;
                            }

                            ArrayList<ItemOption> allOptions = new ArrayList<>();
                            allOptions.add(new ItemOption(82, 5000));
                            allOptions.add(new ItemOption(83, 5000));
                            allOptions.add(new ItemOption(87, 5000));
                            allOptions.add(new ItemOption(84, 500));
                            allOptions.add(new ItemOption(86, 500));
                            allOptions.add(new ItemOption(94, 30));

                            ArrayList<ItemOption> selectedOptions = new ArrayList<>();
                            int numOptionsToAdd = (int) (Math.random() * 6) + 1;
                            Collections.shuffle(allOptions);

                            for (int i = 0; i < numOptionsToAdd; i++) {
                                selectedOptions.add(allOptions.get(i));
                            }
                            if (numOptionsToAdd >= 1 && numOptionsToAdd <= 2) {
                                selectedOptions.add(0, new ItemOption(201, 5000));
                                GlobalService.getInstance().chat("Vua Hùng", "Người Chơi " + setNameVip(p.name) + " Vừa thức tỉnh đồ bậc Sơ Cấp bạn đen vl!");
                            } else if (numOptionsToAdd >= 3 && numOptionsToAdd <= 4) {
                                selectedOptions.add(0, new ItemOption(202, 5000));
                                GlobalService.getInstance().chat("Vua Hùng", "Người Chơi " + setNameVip(p.name) + " Vừa thức tỉnh đồ bậc Trung Cấp bạn giỏi quá!");
                            } else if (numOptionsToAdd >= 5 && numOptionsToAdd <= 6) {
                                selectedOptions.add(0, new ItemOption(203, 5000));
                                GlobalService.getInstance().chat("Vua Hùng", "Người Chơi " + setNameVip(p.name) + " Vừa thức tỉnh đồ bậc Cao Cấp Bạn thật VIP !");
                            }
                            p.removeItem(indexST, 100, true);
                            p.addLuong(-50000);
                            item1.options.addAll(selectedOptions);
                            p.setFashion();
                            p.setAbility();
                            p.getService().updateInfoMe();

                        } else {
                            p.serverDialog("Hãy đưa ta 50.000 lượng ta mới giúp ngươi.");
                        }
                    } else {
                        p.serverDialog("Bạn cần mặc Thiên Kim Cốt Ngọc Mão.");
                    }
                }));
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Áo", () -> {
                    if (p.fashion[2] == null) {
                        p.serverDialog("Bạn không có đồ thích hợp.");
                        return;
                    }
                    if (p.fashion[2].id != 1282) {
                        p.serverDialog("Bạn cần mặc Thiên Kim Cốt Ngọc Giáp.");
                        return;
                    }
                    Item item1 = p.fashion[2];
                    if (p.fashion[2].id == 1282) {
                        if (p.user.gold >= 50000) {
                            if (p.getSlotNull() == 0) {
                                p.serverDialog("Hãy chừa 1 ô trống trong hành trang để nhận.");
                                return;
                            }
                            int indexST = p.getIndexItemByIdInBag(1295);
                            if (indexST == -1 || p.bag[indexST] == null || p.bag[indexST].getQuantity() < 100) {
                                p.serverDialog("Bạn không đủ đá Thức Tỉnh!");
                                return;
                            }
                            boolean hasOption = false;
                            for (ItemOption option : item1.options) {
                                if (option.optionTemplate.id == 82
                                        || option.optionTemplate.id == 83
                                        || option.optionTemplate.id == 87
                                        || option.optionTemplate.id == 84
                                        || option.optionTemplate.id == 86
                                        || option.optionTemplate.id == 94) {
                                    hasOption = true;
                                    break;
                                }
                            }
                            if (hasOption) {
                                p.serverDialog("Chỉ số đã được mở rồi.");
                                return;
                            }

                            ArrayList<ItemOption> allOptions = new ArrayList<>();
                            allOptions.add(new ItemOption(82, 5000));
                            allOptions.add(new ItemOption(83, 5000));
                            allOptions.add(new ItemOption(87, 5000));
                            allOptions.add(new ItemOption(84, 500));
                            allOptions.add(new ItemOption(86, 500));
                            allOptions.add(new ItemOption(94, 30));
                            ArrayList<ItemOption> selectedOptions = new ArrayList<>();
                            int numOptionsToAdd = (int) (Math.random() * 6) + 1;
                            Collections.shuffle(allOptions);

                            for (int i = 0; i < numOptionsToAdd; i++) {
                                selectedOptions.add(allOptions.get(i));
                            }
                            if (numOptionsToAdd >= 1 && numOptionsToAdd <= 2) {
                                selectedOptions.add(0, new ItemOption(201, 5000));
                                GlobalService.getInstance().chat("Vua Hùng", "Người Chơi " + setNameVip(p.name) + " Vừa thức tỉnh đồ bậc Sơ Cấp bạn đen vl!");
                            } else if (numOptionsToAdd >= 3 && numOptionsToAdd <= 4) {
                                selectedOptions.add(0, new ItemOption(202, 5000));
                                GlobalService.getInstance().chat("Vua Hùng", "Người Chơi " + setNameVip(p.name) + " Vừa thức tỉnh đồ bậc Trung Cấp bạn giỏi quá!");
                            } else if (numOptionsToAdd >= 5 && numOptionsToAdd <= 6) {
                                selectedOptions.add(0, new ItemOption(203, 5000));
                                GlobalService.getInstance().chat("Vua Hùng", "Người Chơi " + setNameVip(p.name) + " Vừa thức tỉnh đồ bậc Cao Cấp Bạn thật VIP !");
                            }
                            p.removeItem(indexST, 100, true);
                            p.addLuong(-50000);
                            item1.options.addAll(selectedOptions);
                            p.setFashion();
                            p.setAbility();
                            p.getService().updateInfoMe();

                        } else {
                            p.serverDialog("Hãy đưa ta 50.000 lượng ta mới giúp ngươi.");
                        }
                    } else {
                        p.serverDialog("Bạn cần mặc Thiên Kim Cốt Ngọc Giáp.");
                    }
                }));
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Quần", () -> {
                    if (p.fashion[6] == null) {
                        p.serverDialog("Bạn không có đồ thích hợp.");
                        return;
                    }
                    if (p.fashion[6].id != 1283) {
                        p.serverDialog("Bạn cần mặc Thiên Kim Cốt Ngọc Hạ Giáp.");
                        return;
                    }
                    Item item1 = p.fashion[6];
                    if (p.fashion[6].id == 1283) {
                        if (p.user.gold >= 50000) {
                            if (p.getSlotNull() == 0) {
                                p.serverDialog("Hãy chừa 1 ô trống trong hành trang để nhận.");
                                return;
                            }
                            int indexST = p.getIndexItemByIdInBag(1295);
                            if (indexST == -1 || p.bag[indexST] == null || p.bag[indexST].getQuantity() < 100) {
                                p.serverDialog("Bạn không đủ đá Thức Tỉnh!");
                                return;
                            }
                            boolean hasOption = false;
                            for (ItemOption option : item1.options) {
                                if (option.optionTemplate.id == 82
                                        || option.optionTemplate.id == 83
                                        || option.optionTemplate.id == 87
                                        || option.optionTemplate.id == 84
                                        || option.optionTemplate.id == 86
                                        || option.optionTemplate.id == 94) {
                                    hasOption = true;
                                    break;
                                }
                            }
                            if (hasOption) {
                                p.serverDialog("Chỉ số đã được mở rồi.");
                                return;
                            }

                            ArrayList<ItemOption> allOptions = new ArrayList<>();
                            allOptions.add(new ItemOption(82, 5000));
                            allOptions.add(new ItemOption(83, 5000));
                            allOptions.add(new ItemOption(87, 5000));
                            allOptions.add(new ItemOption(84, 500));
                            allOptions.add(new ItemOption(86, 500));
                            allOptions.add(new ItemOption(94, 30));

                            ArrayList<ItemOption> selectedOptions = new ArrayList<>();
                            int numOptionsToAdd = (int) (Math.random() * 6) + 1;
                            Collections.shuffle(allOptions);

                            for (int i = 0; i < numOptionsToAdd; i++) {
                                selectedOptions.add(allOptions.get(i));
                            }
                            if (numOptionsToAdd >= 1 && numOptionsToAdd <= 2) {
                                selectedOptions.add(0, new ItemOption(201, 5000));
                                GlobalService.getInstance().chat("Vua Hùng", "Người Chơi " + setNameVip(p.name) + " Vừa thức tỉnh đồ bậc Sơ Cấp bạn đen vl!");
                            } else if (numOptionsToAdd >= 3 && numOptionsToAdd <= 4) {
                                selectedOptions.add(0, new ItemOption(202, 5000));
                                GlobalService.getInstance().chat("Vua Hùng", "Người Chơi " + setNameVip(p.name) + " Vừa thức tỉnh đồ bậc Trung Cấp bạn giỏi quá!");
                            } else if (numOptionsToAdd >= 5 && numOptionsToAdd <= 6) {
                                selectedOptions.add(0, new ItemOption(203, 5000));
                                GlobalService.getInstance().chat("Vua Hùng", "Người Chơi " + setNameVip(p.name) + " Vừa thức tỉnh đồ bậc Cao Cấp Bạn thật VIP !");
                            }
                            p.removeItem(indexST, 100, true);
                            p.addLuong(-50000);
                            item1.options.addAll(selectedOptions);
                            p.setFashion();
                            p.setAbility();
                            p.getService().updateInfoMe();

                        } else {
                            p.serverDialog("Hãy đưa ta 50.000 lượng ta mới giúp ngươi.");
                        }
                    } else {
                        p.serverDialog("Bạn cần mặc đồ.");
                    }
                }));
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Găng", () -> {
                    if (p.fashion[4] == null) {
                        p.serverDialog("Bạn không có đồ thích hợp.");
                        return;
                    }
                    if (p.fashion[4].id != 1284) {
                        p.serverDialog("Bạn cần mặc Thiên Kim Cốt Ngọc Thủ.");
                        return;
                    }
                    Item item1 = p.fashion[4];
                    if (p.fashion[4].id == 1284) {
                        if (p.user.gold >= 50000) {
                            if (p.getSlotNull() == 0) {
                                p.serverDialog("Hãy chừa 1 ô trống trong hành trang để nhận.");
                                return;
                            }
                            int indexST = p.getIndexItemByIdInBag(1295);
                            if (indexST == -1 || p.bag[indexST] == null || p.bag[indexST].getQuantity() < 100) {
                                p.serverDialog("Bạn không đủ đá Thức Tỉnh!");
                                return;
                            }
                            boolean hasOption = false;
                            for (ItemOption option : item1.options) {
                                if (option.optionTemplate.id == 82
                                        || option.optionTemplate.id == 83
                                        || option.optionTemplate.id == 87
                                        || option.optionTemplate.id == 84
                                        || option.optionTemplate.id == 86
                                        || option.optionTemplate.id == 94) {
                                    hasOption = true;
                                    break;
                                }
                            }
                            if (hasOption) {
                                p.serverDialog("Chỉ số đã được mở rồi.");
                                return;
                            }

                            ArrayList<ItemOption> allOptions = new ArrayList<>();
                            allOptions.add(new ItemOption(82, 5000));
                            allOptions.add(new ItemOption(83, 5000));
                            allOptions.add(new ItemOption(87, 5000));
                            allOptions.add(new ItemOption(84, 500));
                            allOptions.add(new ItemOption(86, 500));
                            allOptions.add(new ItemOption(94, 30));

                            ArrayList<ItemOption> selectedOptions = new ArrayList<>();
                            int numOptionsToAdd = (int) (Math.random() * 6) + 1;
                            Collections.shuffle(allOptions);

                            for (int i = 0; i < numOptionsToAdd; i++) {
                                selectedOptions.add(allOptions.get(i));
                            }
                            if (numOptionsToAdd >= 1 && numOptionsToAdd <= 2) {
                                selectedOptions.add(0, new ItemOption(201, 5000));
                                GlobalService.getInstance().chat("Vua Hùng", "Người Chơi " + setNameVip(p.name) + " Vừa thức tỉnh đồ bậc Sơ Cấp bạn đen vl!");
                            } else if (numOptionsToAdd >= 3 && numOptionsToAdd <= 4) {
                                selectedOptions.add(0, new ItemOption(202, 5000));
                                GlobalService.getInstance().chat("Vua Hùng", "Người Chơi " + setNameVip(p.name) + " Vừa thức tỉnh đồ bậc Trung Cấp bạn giỏi quá!");
                            } else if (numOptionsToAdd >= 5 && numOptionsToAdd <= 6) {
                                selectedOptions.add(0, new ItemOption(203, 5000));
                                GlobalService.getInstance().chat("Vua Hùng", "Người Chơi " + setNameVip(p.name) + " Vừa thức tỉnh đồ bậc Cao Cấp Bạn thật VIP !");
                            }
                            p.removeItem(indexST, 100, true);
                            p.addLuong(-50000);
                            item1.options.addAll(selectedOptions);
                            p.setFashion();
                            p.setAbility();
                            p.getService().updateInfoMe();

                        } else {
                            p.serverDialog("Hãy đưa ta 50.000 lượng ta mới giúp ngươi.");
                        }
                    } else {
                        p.serverDialog("Bạn cần mặc đồ.");
                    }
                }));
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Giày", () -> {
                    if (p.fashion[8] == null) {
                        p.serverDialog("Bạn không có đồ thích hợp.");
                        return;
                    }
                    if (p.fashion[8].id != 1285) {
                        p.serverDialog("Bạn cần mặc Thiên Kim Cốt Ngọc Ngoa.");
                        return;
                    }
                    Item item1 = p.fashion[8];
                    if (p.fashion[8].id == 1285) {
                        if (p.user.gold >= 50000) {
                            if (p.getSlotNull() == 0) {
                                p.serverDialog("Hãy chừa 1 ô trống trong hành trang để nhận.");
                                return;
                            }
                            int indexST = p.getIndexItemByIdInBag(1295);
                            if (indexST == -1 || p.bag[indexST] == null || p.bag[indexST].getQuantity() < 100) {
                                p.serverDialog("Bạn không đủ đá Thức Tỉnh!");
                                return;
                            }
                            boolean hasOption = false;
                            for (ItemOption option : item1.options) {
                                if (option.optionTemplate.id == 82
                                        || option.optionTemplate.id == 83
                                        || option.optionTemplate.id == 87
                                        || option.optionTemplate.id == 84
                                        || option.optionTemplate.id == 86
                                        || option.optionTemplate.id == 94) {
                                    hasOption = true;
                                    break;
                                }
                            }
                            if (hasOption) {
                                p.serverDialog("Chỉ số đã được mở rồi.");
                                return;
                            }

                            ArrayList<ItemOption> allOptions = new ArrayList<>();
                            allOptions.add(new ItemOption(82, 5000));
                            allOptions.add(new ItemOption(83, 5000));
                            allOptions.add(new ItemOption(87, 5000));
                            allOptions.add(new ItemOption(84, 500));
                            allOptions.add(new ItemOption(86, 500));
                            allOptions.add(new ItemOption(94, 30));

                            ArrayList<ItemOption> selectedOptions = new ArrayList<>();
                            int numOptionsToAdd = (int) (Math.random() * 6) + 1;
                            Collections.shuffle(allOptions);

                            for (int i = 0; i < numOptionsToAdd; i++) {
                                selectedOptions.add(allOptions.get(i));
                            }
                            if (numOptionsToAdd >= 1 && numOptionsToAdd <= 2) {
                                selectedOptions.add(0, new ItemOption(201, 5000));
                                GlobalService.getInstance().chat("Vua Hùng", "Người Chơi " + setNameVip(p.name) + " Vừa thức tỉnh đồ bậc Sơ Cấp bạn đen vl!");
                            } else if (numOptionsToAdd >= 3 && numOptionsToAdd <= 4) {
                                selectedOptions.add(0, new ItemOption(202, 5000));
                                GlobalService.getInstance().chat("Vua Hùng", "Người Chơi " + setNameVip(p.name) + " Vừa thức tỉnh đồ bậc Trung Cấp bạn giỏi quá!");
                            } else if (numOptionsToAdd >= 5 && numOptionsToAdd <= 6) {
                                selectedOptions.add(0, new ItemOption(203, 5000));
                                GlobalService.getInstance().chat("Vua Hùng", "Người Chơi " + setNameVip(p.name) + " Vừa thức tỉnh đồ bậc Cao Cấp Bạn thật VIP !");
                            }
                            p.removeItem(indexST, 100, true);
                            p.addLuong(-50000);
                            item1.options.addAll(selectedOptions);
                            p.setFashion();
                            p.setAbility();
                            p.getService().updateInfoMe();

                        } else {
                            p.serverDialog("Hãy đưa ta 50.000 lượng ta mới giúp ngươi.");
                        }
                    } else {
                        p.serverDialog("Bạn cần mặc đồ.");
                    }
                }));
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Phù", () -> {
                    if (p.fashion[9] == null) {
                        p.serverDialog("Bạn không có đồ thích hợp.");
                        return;
                    }
                    if (p.fashion[9].id != 1286) {
                        p.serverDialog("Bạn cần mặc Thiên Kim Cốt Ngọc Phù.");
                        return;
                    }
                    Item item1 = p.fashion[9];
                    if (p.fashion[9].id == 1286) {
                        if (p.user.gold >= 50000) {
                            if (p.getSlotNull() == 0) {
                                p.serverDialog("Hãy chừa 1 ô trống trong hành trang để nhận.");
                                return;
                            }
                            int indexST = p.getIndexItemByIdInBag(1295);
                            if (indexST == -1 || p.bag[indexST] == null || p.bag[indexST].getQuantity() < 100) {
                                p.serverDialog("Bạn không đủ đá Thức Tỉnh!");
                                return;
                            }
                            boolean hasOption = false;
                            for (ItemOption option : item1.options) {
                                if (option.optionTemplate.id == 82
                                        || option.optionTemplate.id == 83
                                        || option.optionTemplate.id == 87
                                        || option.optionTemplate.id == 84
                                        || option.optionTemplate.id == 86
                                        || option.optionTemplate.id == 94) {
                                    hasOption = true;
                                    break;
                                }
                            }
                            if (hasOption) {
                                p.serverDialog("Chỉ số đã được mở rồi.");
                                return;
                            }

                            ArrayList<ItemOption> allOptions = new ArrayList<>();
                            allOptions.add(new ItemOption(82, 5000));
                            allOptions.add(new ItemOption(83, 5000));
                            allOptions.add(new ItemOption(87, 5000));
                            allOptions.add(new ItemOption(84, 500));
                            allOptions.add(new ItemOption(86, 500));
                            allOptions.add(new ItemOption(94, 30));

                            ArrayList<ItemOption> selectedOptions = new ArrayList<>();
                            int numOptionsToAdd = (int) (Math.random() * 6) + 1;
                            Collections.shuffle(allOptions);

                            for (int i = 0; i < numOptionsToAdd; i++) {
                                selectedOptions.add(allOptions.get(i));
                            }
                            if (numOptionsToAdd >= 1 && numOptionsToAdd <= 2) {
                                selectedOptions.add(0, new ItemOption(201, 5000));
                                GlobalService.getInstance().chat("Vua Hùng", "Người Chơi " + setNameVip(p.name) + " Vừa thức tỉnh đồ bậc Sơ Cấp bạn đen vl!");
                            } else if (numOptionsToAdd >= 3 && numOptionsToAdd <= 4) {
                                selectedOptions.add(0, new ItemOption(202, 5000));
                                GlobalService.getInstance().chat("Vua Hùng", "Người Chơi " + setNameVip(p.name) + " Vừa thức tỉnh đồ bậc Trung Cấp bạn giỏi quá!");
                            } else if (numOptionsToAdd >= 5 && numOptionsToAdd <= 6) {
                                selectedOptions.add(0, new ItemOption(203, 5000));
                                GlobalService.getInstance().chat("Vua Hùng", "Người Chơi " + setNameVip(p.name) + " Vừa thức tỉnh đồ bậc Cao Cấp Bạn thật VIP !");
                            }
                            p.removeItem(indexST, 100, true);
                            p.addLuong(-50000);
                            item1.options.addAll(selectedOptions);
                            p.setFashion();
                            p.setAbility();
                            p.getService().updateInfoMe();

                        } else {
                            p.serverDialog("Hãy đưa ta 50.000 lượng ta mới giúp ngươi.");
                        }
                    } else {
                        p.serverDialog("Bạn cần mặc đồ.");
                    }
                }));
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Bội", () -> {
                    if (p.fashion[7] == null) {
                        p.serverDialog("Bạn không có đồ thích hợp.");
                        return;
                    }
                    if (p.fashion[7].id != 1287) {
                        p.serverDialog("Bạn cần mặc Thiên Kim Cốt Ngọc Bội.");
                        return;
                    }
                    Item item1 = p.fashion[7];
                    if (p.fashion[7].id == 1287) {
                        if (p.user.gold >= 50000) {
                            if (p.getSlotNull() == 0) {
                                p.serverDialog("Hãy chừa 1 ô trống trong hành trang để nhận.");
                                return;
                            }
                            int indexST = p.getIndexItemByIdInBag(1295);
                            if (indexST == -1 || p.bag[indexST] == null || p.bag[indexST].getQuantity() < 100) {
                                p.serverDialog("Bạn không đủ đá Thức Tỉnh!");
                                return;
                            }
                            boolean hasOption = false;
                            for (ItemOption option : item1.options) {
                                if (option.optionTemplate.id == 82
                                        || option.optionTemplate.id == 83
                                        || option.optionTemplate.id == 87
                                        || option.optionTemplate.id == 84
                                        || option.optionTemplate.id == 86
                                        || option.optionTemplate.id == 94) {
                                    hasOption = true;
                                    break;
                                }
                            }
                            if (hasOption) {
                                p.serverDialog("Chỉ số đã được mở rồi.");
                                return;
                            }

                            ArrayList<ItemOption> allOptions = new ArrayList<>();
                            allOptions.add(new ItemOption(82, 5000));
                            allOptions.add(new ItemOption(83, 5000));
                            allOptions.add(new ItemOption(87, 5000));
                            allOptions.add(new ItemOption(84, 500));
                            allOptions.add(new ItemOption(86, 500));
                            allOptions.add(new ItemOption(94, 30));

                            ArrayList<ItemOption> selectedOptions = new ArrayList<>();
                            int numOptionsToAdd = (int) (Math.random() * 6) + 1;
                            Collections.shuffle(allOptions);

                            for (int i = 0; i < numOptionsToAdd; i++) {
                                selectedOptions.add(allOptions.get(i));
                            }
                            if (numOptionsToAdd >= 1 && numOptionsToAdd <= 2) {
                                selectedOptions.add(0, new ItemOption(201, 5000));
                                GlobalService.getInstance().chat("Vua Hùng", "Người Chơi " + setNameVip(p.name) + " Vừa thức tỉnh đồ bậc Sơ Cấp bạn đen vl!");
                            } else if (numOptionsToAdd >= 3 && numOptionsToAdd <= 4) {
                                selectedOptions.add(0, new ItemOption(202, 5000));
                                GlobalService.getInstance().chat("Vua Hùng", "Người Chơi " + setNameVip(p.name) + " Vừa thức tỉnh đồ bậc Trung Cấp bạn giỏi quá!");
                            } else if (numOptionsToAdd >= 5 && numOptionsToAdd <= 6) {
                                selectedOptions.add(0, new ItemOption(203, 5000));
                                GlobalService.getInstance().chat("Vua Hùng", "Người Chơi " + setNameVip(p.name) + " Vừa thức tỉnh đồ bậc Cao Cấp Bạn thật VIP !");
                            }
                            p.removeItem(indexST, 100, true);
                            p.addLuong(-50000);
                            item1.options.addAll(selectedOptions);
                            p.setFashion();
                            p.setAbility();
                            p.getService().updateInfoMe();

                        } else {
                            p.serverDialog("Hãy đưa ta 50.000 lượng ta mới giúp ngươi.");
                        }
                    } else {
                        p.serverDialog("Bạn cần mặc đồ.");
                    }
                }));
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Chuyền", () -> {
                    if (p.fashion[5] == null) {
                        p.serverDialog("Bạn không có đồ thích hợp.");
                        return;
                    }
                    if (p.fashion[5].id != 1288) {
                        p.serverDialog("Bạn cần mặc Thiên Kim Cốt Ngọc Giới.");
                        return;
                    }
                    Item item1 = p.fashion[5];
                    if (p.fashion[5].id == 1288) {
                        if (p.user.gold >= 50000) {
                            if (p.getSlotNull() == 0) {
                                p.serverDialog("Hãy chừa 1 ô trống trong hành trang để nhận.");
                                return;
                            }
                            int indexST = p.getIndexItemByIdInBag(1295);
                            if (indexST == -1 || p.bag[indexST] == null || p.bag[indexST].getQuantity() < 100) {
                                p.serverDialog("Bạn không đủ đá Thức Tỉnh!");
                                return;
                            }
                           boolean hasOption = false;
                            for (ItemOption option : item1.options) {
                                if (option.optionTemplate.id == 82
                                        || option.optionTemplate.id == 83
                                        || option.optionTemplate.id == 87
                                        || option.optionTemplate.id == 84
                                        || option.optionTemplate.id == 86
                                        || option.optionTemplate.id == 94) {
                                    hasOption = true;
                                    break;
                                }
                            }
                            if (hasOption) {
                                p.serverDialog("Chỉ số đã được mở rồi.");
                                return;
                            }

                            ArrayList<ItemOption> allOptions = new ArrayList<>();
                            allOptions.add(new ItemOption(82, 5000));
                            allOptions.add(new ItemOption(83, 5000));
                            allOptions.add(new ItemOption(87, 5000));
                            allOptions.add(new ItemOption(84, 500));
                            allOptions.add(new ItemOption(86, 500));
                            allOptions.add(new ItemOption(94, 30));

                            ArrayList<ItemOption> selectedOptions = new ArrayList<>();
                            int numOptionsToAdd = (int) (Math.random() * 6) + 1;
                            Collections.shuffle(allOptions);

                            for (int i = 0; i < numOptionsToAdd; i++) {
                                selectedOptions.add(allOptions.get(i));
                            }
                            if (numOptionsToAdd >= 1 && numOptionsToAdd <= 2) {
                                selectedOptions.add(0, new ItemOption(201, 5000));
                                GlobalService.getInstance().chat("Vua Hùng", "Người Chơi " + setNameVip(p.name) + " Vừa thức tỉnh đồ bậc Sơ Cấp bạn đen vl!");
                            } else if (numOptionsToAdd >= 3 && numOptionsToAdd <= 4) {
                                selectedOptions.add(0, new ItemOption(202, 5000));
                                GlobalService.getInstance().chat("Vua Hùng", "Người Chơi " + setNameVip(p.name) + " Vừa thức tỉnh đồ bậc Trung Cấp bạn giỏi quá!");
                            } else if (numOptionsToAdd >= 5 && numOptionsToAdd <= 6) {
                                selectedOptions.add(0, new ItemOption(203, 5000));
                                GlobalService.getInstance().chat("Vua Hùng", "Người Chơi " + setNameVip(p.name) + " Vừa thức tỉnh đồ bậc Cao Cấp Bạn thật VIP !");
                            }
                            p.removeItem(indexST, 100, true);
                            p.addLuong(-50000);
                            item1.options.addAll(selectedOptions);
                            p.setFashion();
                            p.setAbility();
                            p.getService().updateInfoMe();

                        } else {
                            p.serverDialog("Hãy đưa ta 50.000 lượng ta mới giúp ngươi.");
                        }
                    } else {
                        p.serverDialog("Bạn cần mặc đồ.");
                    }
                }));
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Nhẫn", () -> {
                    if (p.fashion[3] == null) {
                        p.serverDialog("Bạn không có đồ thích hợp.");
                        return;
                    }
                    if (p.fashion[3].id != 1289) {
                        p.serverDialog("Bạn cần mặc Thiên Kim Cốt Ngọc Liên.");
                        return;
                    }
                    Item item1 = p.fashion[3];
                    if (p.fashion[3].id == 1289) {
                        if (p.user.gold >= 50000) {
                            if (p.getSlotNull() == 0) {
                                p.serverDialog("Hãy chừa 1 ô trống trong hành trang để nhận.");
                                return;
                            }
                            int indexST = p.getIndexItemByIdInBag(1295);
                            if (indexST == -1 || p.bag[indexST] == null || p.bag[indexST].getQuantity() < 100) {
                                p.serverDialog("Bạn không đủ đá Thức Tỉnh!");
                                return;
                            }
                            boolean hasOption = false;
                            for (ItemOption option : item1.options) {
                                if (option.optionTemplate.id == 82
                                        || option.optionTemplate.id == 83
                                        || option.optionTemplate.id == 87
                                        || option.optionTemplate.id == 84
                                        || option.optionTemplate.id == 86
                                        || option.optionTemplate.id == 94) {
                                    hasOption = true;
                                    break;
                                }
                            }
                            if (hasOption) {
                                p.serverDialog("Chỉ số đã được mở rồi.");
                                return;
                            }

                            ArrayList<ItemOption> allOptions = new ArrayList<>();
                            allOptions.add(new ItemOption(82, 5000));
                            allOptions.add(new ItemOption(83, 5000));
                            allOptions.add(new ItemOption(87, 5000));
                            allOptions.add(new ItemOption(84, 500));
                            allOptions.add(new ItemOption(86, 500));
                            allOptions.add(new ItemOption(94, 30));

                            ArrayList<ItemOption> selectedOptions = new ArrayList<>();
                            int numOptionsToAdd = (int) (Math.random() * 6) + 1;
                            Collections.shuffle(allOptions);

                            for (int i = 0; i < numOptionsToAdd; i++) {
                                selectedOptions.add(allOptions.get(i));
                            }
                            if (numOptionsToAdd >= 1 && numOptionsToAdd <= 2) {
                                selectedOptions.add(0, new ItemOption(201, 5000));
                                GlobalService.getInstance().chat("Vua Hùng", "Người Chơi " + setNameVip(p.name) + " Vừa thức tỉnh đồ bậc Sơ Cấp bạn đen vl!");
                            } else if (numOptionsToAdd >= 3 && numOptionsToAdd <= 4) {
                                selectedOptions.add(0, new ItemOption(202, 5000));
                                GlobalService.getInstance().chat("Vua Hùng", "Người Chơi " + setNameVip(p.name) + " Vừa thức tỉnh đồ bậc Trung Cấp bạn giỏi quá!");
                            } else if (numOptionsToAdd >= 5 && numOptionsToAdd <= 6) {
                                selectedOptions.add(0, new ItemOption(203, 5000));
                                GlobalService.getInstance().chat("Vua Hùng", "Người Chơi " + setNameVip(p.name) + " Vừa thức tỉnh đồ bậc Cao Cấp Bạn thật VIP !");
                            }
                            p.removeItem(indexST, 100, true);
                            p.addLuong(-50000);
                            item1.options.addAll(selectedOptions);
                            p.setFashion();
                            p.setAbility();
                            p.getService().updateInfoMe();

                        } else {
                            p.serverDialog("Hãy đưa ta 50.000 lượng ta mới giúp ngươi.");
                        }
                    } else {
                        p.serverDialog("Bạn cần mặc đồ.");
                    }
                }));
                p.getService().openUIMenu();
            }));
        }
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Đổi Túi Lượng", () -> {
            if (p.getSlotNull() == 0) {
                p.warningBagFull();
                return;
            }
            if (p.user.gold <= 100000) {
                 p.serverDialog("Bạn không có đủ 100k lượng!");
                return;
            }
            long tongNaps = p.getTongNaps(p);
            if (tongNaps >= 100000) {
                p.serverDialog("Bạn cần là VIP mới đổi đc!");
                return;
            }
            p.addLuong(-110000);
            Item newItem = ItemFactory.getInstance().newItem(1268);
            newItem.isLock = false;
            p.themItemToBag(newItem);
            p.getService().updateInfoMe();
        }));
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Tới Map Úp Yên", () -> {
            if (!p.isHuman) {
                p.warningClone();
                return;
            }
            p.teleportUPYEN();
        }));
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Mua 100 điểm danh vọng(50k lượng)", () -> {
            if (!p.isHuman) {
                p.warningClone();
                return;
            }
            if (p.user.gold >= 50000) {
                p.addLuong(-50000);
                int point = 100;
                p.pointNon += point;
                p.pointVuKhi += point;
                p.pointAo += point;
                p.pointLien += point;
                p.pointGangTay += point;
                p.pointNhan += point;
                p.pointNgocBoi += point;
                p.pointQuan += point;
                p.pointPhu += point;
               p.pointGiay += point;
                p.serverDialog("đã tăng 100 điểm danh vọng mỗi loại!");
            } else {
                p.serverDialog("Không đủ lượng!");
            }
            return;
        }));
    }
}

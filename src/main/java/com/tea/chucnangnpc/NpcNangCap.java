package com.tea.chucnangnpc;

import com.tea.constants.CMDConfirmPopup;
import com.tea.constants.CMDMenu;
import com.tea.constants.NpcName;
import com.tea.constants.TaskName;
import com.tea.item.Item;
import com.tea.item.ItemFactory;
import com.tea.item.ItemManager;
import com.tea.item.ItemTemplate;
import com.tea.model.Char;
import com.tea.model.ConfirmPopup;
import com.tea.model.Menu;
import com.tea.option.ItemOption;
import com.tea.server.Config;
import com.tea.server.GameData;
import com.tea.server.Server;
import com.tea.util.NinjaUtils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;

public class NpcNangCap {

    Config serverConfig = Config.getInstance();

    public void npcnangcap(Char p) {
        if (p.user.kh == 0) {
            p.serverMessage("Bạn phải kích hoạt để dùng tính năng này");
            return;
        }
        p.menus.clear();
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Nâng bí kip", () -> {
            nangbikip(p);
        }));
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Nâng Ruby Thần", () -> {
            nangruby(p);
        }));

        if (!serverConfig.isOpendametrung()) {
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Nâng Pet", () -> {
                nangpet(p);
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Nâng ntgt thần", () -> {
                nangntgt(p);
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Nâng hakiyoyoi", () -> {
                nangyy(p);
            }));
        }
        p.getService().openUIMenu();

    }

    public void npcnangcap1(Char p) {
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
            rs = stmt.executeQuery("SELECT * FROM chucnang WHERE ten_npc = 'Tashino'");
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
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void nangpet(Char p) {
        p.menus.clear();
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Pet Đại Thánh", () -> {
            p.menus.clear();
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Khai mở chỉ số", () -> {
                if (p.equipment[10] == null) {
                    p.serverDialog("Ngươi cần đeo pet lên.");
                    return;
                }
                if (p.equipment[10].id != 1280) {
                    p.serverDialog("Ngươi cần đeo pet lên.");
                    return;
                }
                Item item1 = p.equipment[10];
                if (item1.hasExpire()) {
                    p.serverDialog("Ngươi cần đeo pet vĩnh viễn mới đc.");
                    return;
                }
                if (p.equipment[10].id == 1280) {
                    if (p.user.gold >= 10000) {
                        if (p.getSlotNull() == 0) {
                            p.serverDialog("Hãy chừa 1 ô trống trong hành trang để nhận bí kíp.");
                            return;
                        }
                        boolean hasOpenedIndex = false;
                        for (ItemOption option : item1.options) {
                            if (option.optionTemplate.id == 82
                                    || option.optionTemplate.id == 83
                                    || option.optionTemplate.id == 87
                                    || option.optionTemplate.id == 94
                                    || option.optionTemplate.id == 81
                                    || option.optionTemplate.id == 84) {
                                hasOpenedIndex = true;
                                break;
                            }
                        }
                        if (hasOpenedIndex) {
                            p.serverDialog("Chỉ số đã được mở rồi.");
                            return;
                        }

                        ArrayList<ItemOption> options = new ArrayList<>();
                        double successRate = 0.2;
                        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 82)) {
                            options.add(new ItemOption(82, 5000));
                        }
                        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 83)) {
                            options.add(new ItemOption(83, 5000));
                        }
                        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 87)) {
                            options.add(new ItemOption(87, 5000));
                        }
                        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 94)) {
                            options.add(new ItemOption(94, 20));
                        }
                        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 81)) {
                            options.add(new ItemOption(81, 100));
                        }
                        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 84)) {
                            options.add(new ItemOption(84, 100));
                        }
                        if (Math.random() < successRate) {
                            p.addLuong(-10000);
                            item1.options.addAll(options);
                            p.setFashion();
                            p.setAbility();
                            p.serverDialog("Khai mở thành công trừ 10k lượng!");
                        } else {
                            p.addLuong(-10000);
                            p.setFashion();
                            p.setAbility();
                            p.serverDialog("Khai mở thất bại trừ 10k lượng");
                        }
                    } else {
                        p.serverDialog("Hãy đưa ta 10.000 lượng ta mới giúp ngươi.");
                    }
                } else {
                    p.serverDialog("Ngươi cần đeo pet trên người đã rồi luyện nhé.");
                }
            }));

            p.menus.add(new Menu(CMDMenu.EXECUTE, "Nâng cấp ", () -> {
                if (p.equipment[10] == null) {
                    p.serverDialog("Ngươi cần đeo pet lên .");
                    return;
                }
                if (p.equipment[10].id != 1280) {
                    p.serverDialog("Ngươi cần đeo pet lên.");
                    return;
                }
                Item item = p.equipment[10];
                if (item.hasExpire()) {
                    p.serverDialog("Ngươi cần đeo pet vĩnh viễn mới đc.");
                    return;
                }
                if (item != null) {
                    if (item.upgrade >= 16) {
                        p.serverDialog("pet của ngươi đã quá mạnh, ta không thể giúp được ngươi.");
                        return;
                    }
                    int[] fee1 = {500, 1000, 1500, 2000, 2500, 3000, 3500, 4000, 4500, 5000, 5500, 6000, 6500, 7000, 7500, 8000};
                    int[] xu1 = {50000, 100000, 150000, 200000, 250000, 300000, 350000, 400000, 450000, 500000, 550000, 600000, 650000, 700000, 750000, 800000};
                    int[] da = new int[]{5, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150};
                    int percent1 = GameData.UP_rb[item.upgrade];
                    p.setConfirmPopup(new ConfirmPopup(CMDConfirmPopup.NANG_pet, String.format(
                            "Ngươi có muốn nâng cấp pet đang sử dụng lên cấp %d không? với phí %d lượng , %d xu và cần %d đá Thiên Tinh - Tỉ lệ thành công: %s.",
                            (item.upgrade + 1), fee1[item.upgrade], xu1[item.upgrade], da[item.upgrade], percent1 + "%")));
                    p.getService().openUIConfirmID();
                } else {
                    p.serverDialog("Ngươi hãy sử dụng pet ta sẽ giúp ngươi làm thay đổi sức mạnh.");
                }
            }));
            p.getService().openUIMenu();
        }));
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Pét Bóng Ma", () -> {
            p.menus.clear();
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Khai mở chỉ số", () -> {
                if (p.fashion[10] == null) {
                    p.serverDialog("Ngươi cần đeo pet lên.");
                    return;
                }
                if (p.fashion[10].id != 828) {
                    p.serverDialog("Ngươi cần đeo pet lên.");
                    return;
                }
                Item item1 = p.fashion[10];
                if (item1.hasExpire()) {
                    p.serverDialog("Ngươi cần đeo pet vĩnh viễn mới đc.");
                    return;
                }
                if (p.fashion[10].id == 828) {
                    if (p.user.gold >= 10000) {
                        if (p.getSlotNull() == 0) {
                            p.serverDialog("Hãy chừa 1 ô trống trong hành trang để nhận bí kíp.");
                            return;
                        }
                        boolean hasOpenedIndex = false;
                        for (ItemOption option : item1.options) {
                            if (option.optionTemplate.id == 82
                                    || option.optionTemplate.id == 83
                                    || option.optionTemplate.id == 87
                                    || option.optionTemplate.id == 94
                                    || option.optionTemplate.id == 100
                                    || option.optionTemplate.id == 86) {
                                hasOpenedIndex = true;
                                break;
                            }
                        }
                        if (hasOpenedIndex) {
                            p.serverDialog("Chỉ số đã được mở rồi.");
                            return;
                        }

                        ArrayList<ItemOption> options = new ArrayList<>();
                        double successRate = 0.2;
                        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 82)) {
                            options.add(new ItemOption(82, 5000));
                        }
                        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 83)) {
                            options.add(new ItemOption(83, 5000));
                        }
                        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 87)) {
                            options.add(new ItemOption(87, 5000));
                        }
                        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 94)) {
                            options.add(new ItemOption(94, 20));
                        }
                        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 100)) {
                            options.add(new ItemOption(100, 20));
                        }
                        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 86)) {
                            options.add(new ItemOption(86, 100));
                        }
                        if (Math.random() < successRate) {
                            p.addLuong(-10000);
                            item1.options.addAll(options);
                            p.setFashion();
                            p.setAbility();
                            p.serverDialog("Khai mở thành công trừ 10k lượng!");
                        } else {
                            p.addLuong(-10000);
                            p.setFashion();
                            p.setAbility();
                            p.serverDialog("Khai mở thất bại trừ 10k lượng");
                        }
                    } else {
                        p.serverDialog("Hãy đưa ta 10.000 lượng ta mới giúp ngươi.");
                    }
                } else {
                    p.serverDialog("Ngươi cần đeo pet trên người đã rồi luyện nhé.");
                }
            }));

            p.menus.add(new Menu(CMDMenu.EXECUTE, "Nâng cấp ", () -> {
                if (p.fashion[10] == null) {
                    p.serverDialog("Ngươi cần đeo pet lên .");
                    return;
                }
                if (p.fashion[10].id != 828) {
                    p.serverDialog("Ngươi cần đeo pet lên.");
                    return;
                }
                Item item = p.fashion[10];
                if (item.hasExpire()) {
                    p.serverDialog("Ngươi cần đeo pet vĩnh viễn mới đc.");
                    return;
                }
                if (item != null) {
                    if (item.upgrade >= 16) {
                        p.serverDialog("Bí kíp của ngươi đã quá mạnh, ta không thể giúp được ngươi.");
                        return;
                    }
                    int[] fee1 = {500, 1000, 1500, 2000, 2500, 3000, 3500, 4000, 4500, 5000, 5500, 6000, 6500, 7000, 7500, 8000};
                    int[] da = new int[]{5, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150};
                    int percent1 = GameData.UP_rb[item.upgrade];
                    p.setConfirmPopup(new ConfirmPopup(CMDConfirmPopup.NANG_pet1, String.format(
                            "Ngươi có muốn nâng cấp pet đang sử dụng lên cấp %d không? với phí %d lượng và cần %d đá Thiên Tinh - Tỉ lệ thành công: %s.",
                            (item.upgrade + 1), fee1[item.upgrade], da[item.upgrade], percent1 + "%")));
                    p.getService().openUIConfirmID();
                } else {
                    p.serverDialog("Ngươi hãy sử dụng pet ta sẽ giúp ngươi làm thay đổi sức mạnh.");
                }
            }));
            p.getService().openUIMenu();
        }));
        p.getService().openUIMenu();
    }

    public void nangyy(Char p) {
        p.menus.clear();
        p.menus.add(new Menu(CMDMenu.EXECUTE, "haki yy", () -> {
            p.menus.clear();
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Khai mở chỉ số", () -> {
                if (p.equipment[12] == null) {
                    p.serverDialog("Ngươi cần đeo yoroi lên.");
                    return;
                }
                if (p.equipment[12].id != 797) {
                    p.serverDialog("Ngươi cần đeo yoroi lên.");
                    return;
                }
                Item item1 = p.equipment[12];
                if (item1.hasExpire()) {
                    p.serverDialog("Ngươi cần đeo yoroi vĩnh viễn mới đc.");
                    return;
                }
                if (p.equipment[12].id == 797) {
                    if (p.user.gold >= 10000) {
                        if (p.getSlotNull() == 0) {
                            p.serverDialog("Hãy chừa 1 ô trống trong hành trang để nhận yoroi.");
                            return;
                        }
                        boolean hasOpenedIndex = false;
                        for (ItemOption option : item1.options) {
                            if (option.optionTemplate.id == 87
                                    || option.optionTemplate.id == 94
                                    || option.optionTemplate.id == 57
                                    || option.optionTemplate.id == 58) {
                                hasOpenedIndex = true;
                                break;
                            }
                        }
                        if (hasOpenedIndex) {
                            p.serverDialog("Chỉ số đã được mở rồi.");
                            return;
                        }

                        ArrayList<ItemOption> options = new ArrayList<>();
                        double successRate = 0.2;
                        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 87)) {
                            options.add(new ItemOption(87, 5000));
                        }
                        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 94)) {
                            options.add(new ItemOption(94, 30));
                        }
                        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 58)) {
                            options.add(new ItemOption(58, 30));
                        }
                        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 57)) {
                            options.add(new ItemOption(57, 50));
                        }

                        if (Math.random() < successRate) {
                            p.addLuong(-10000);
                            item1.options.addAll(options);
                            p.setFashion();
                            p.setAbility();
                            p.serverDialog("Khai mở thành công trừ 10k lượng!");
                        } else {
                            p.addLuong(-10000);
                            p.setFashion();
                            p.setAbility();
                            p.serverDialog("Khai mở thất bại trừ 10k lượng");
                        }
                    } else {
                        p.serverDialog("Hãy đưa ta 10.000 lượng ta mới giúp ngươi.");
                    }
                } else {
                    p.serverDialog("Ngươi cần đeo yoroi trên người đã rồi luyện nhé.");
                }
            }));

            p.menus.add(new Menu(CMDMenu.EXECUTE, "Nâng cấp ", () -> {
                if (p.equipment[12] == null) {
                    p.serverDialog("Ngươi cần đeo yoroi lên .");
                    return;
                }
                if (p.equipment[12].id != 797) {
                    p.serverDialog("Ngươi cần đeo yoroi lên.");
                    return;
                }
                Item item = p.equipment[12];
                if (item != null) {
                    if (item.upgrade >= 16) {
                        p.serverDialog("yoroi của ngươi đã quá mạnh, ta không thể giúp được ngươi.");
                        return;
                    }
                    int[] fee1 = {500, 1000, 1500, 2000, 2500, 3000, 3500, 4000, 4500, 5000, 5500, 6000, 6500, 7000, 7500, 8000};
                    int[] da = new int[]{5, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150};
                    int percent1 = GameData.UP_rb[item.upgrade];
                    p.setConfirmPopup(new ConfirmPopup(CMDConfirmPopup.NANG_yoroi, String.format(
                            "Ngươi có muốn nâng cấp yoroi đang sử dụng lên cấp %d không? với phí %d lượng và cần %d đá Thạch anh - Tỉ lệ thành công: %s.",
                            (item.upgrade + 1), fee1[item.upgrade], da[item.upgrade], percent1 + "%")));
                    p.getService().openUIConfirmID();
                } else {
                    p.serverDialog("Ngươi hãy sử dụng yoroi ta sẽ giúp ngươi làm thay đổi sức mạnh.");
                }
            }));
            p.getService().openUIMenu();
        }));
        p.getService().openUIMenu();
    }

    public void nangntgt(Char p) {
        p.menus.clear();
        p.menus.add(new Menu(CMDMenu.EXECUTE, "ntgt c5", () -> {
            p.menus.clear();
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Nâng ntgt5 lên vĩnh viễn", () -> {
                if (p.getSlotNull() == 0) {
                    p.warningBagFull();
                    return;
                }
                if (p.user.gold <= 10000) {
                    p.serverDialog("Bạn cần 10k lượng!");
                    return;
                }
                int indexST = p.getIndexItemByIdInBag(427);
                if (indexST == -1 || p.bag[indexST] == null) {
                    p.serverDialog("Bạn không có ntgt5!");
                    return;
                }
                p.addLuong(-10000);
                Item newItem = ItemFactory.getInstance().newItem(427);
                p.removeItem(indexST, 1, true);
                newItem.isLock = false;
                p.themItemToBag(newItem);
                p.getService().updateInfoMe();
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Khai mở chỉ số", () -> {
                if (p.equipment[13] == null) {
                    p.serverDialog("Ngươi cần đeo ntgt lên.");
                    return;
                }
                if (p.equipment[13].id != 427) {
                    p.serverDialog("Ngươi cần đeo ntgt lên.");
                    return;
                }
                Item item1 = p.equipment[13];
                if (item1.hasExpire()) {
                    p.serverDialog("Ngươi cần đeo yoroi vĩnh viễn mới đc.");
                    return;
                }
                if (p.equipment[13].id == 427) {
                    if (p.user.gold >= 10000) {
                        if (p.getSlotNull() == 0) {
                            p.serverDialog("Hãy chừa 1 ô trống trong hành trang để nhận ntgt.");
                            return;
                        }
                        boolean hasOpenedIndex = false;
                        for (ItemOption option : item1.options) {
                            if (option.optionTemplate.id == 87
                                    || option.optionTemplate.id == 80
                                    || option.optionTemplate.id == 91
                                    || option.optionTemplate.id == 92) {
                                hasOpenedIndex = true;
                                break;
                            }
                        }
                        if (hasOpenedIndex) {
                            p.serverDialog("Chỉ số đã được mở rồi.");
                            return;
                        }

                        ArrayList<ItemOption> options = new ArrayList<>();
                        double successRate = 0.2;
                        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 87)) {
                            options.add(new ItemOption(87, 5000));
                        }
                        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 80)) {
                            options.add(new ItemOption(80, 50));
                        }
                        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 91)) {
                            options.add(new ItemOption(91, 100));
                        }
                        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 92)) {
                            options.add(new ItemOption(92, 50));
                        }
                        if (Math.random() < successRate) {
                            p.addLuong(-10000);
                            item1.options.addAll(options);
                            p.setFashion();
                            p.setAbility();
                            p.serverDialog("Khai mở thành công trừ 10k lượng!");
                        } else {
                            p.addLuong(-10000);
                            p.setFashion();
                            p.setAbility();
                            p.serverDialog("Khai mở thất bại trừ 10k lượng");
                        }
                    } else {
                        p.serverDialog("Hãy đưa ta 10.000 lượng ta mới giúp ngươi.");
                    }
                } else {
                    p.serverDialog("Ngươi cần đeo ntgt trên người đã rồi luyện nhé.");
                }
            }));

            p.menus.add(new Menu(CMDMenu.EXECUTE, "Nâng cấp ", () -> {
                if (p.equipment[13] == null) {
                    p.serverDialog("Ngươi cần đeo ntgt lên .");
                    return;
                }
                if (p.equipment[13].id != 427) {
                    p.serverDialog("Ngươi cần đeo ntgt lên.");
                    return;
                }
                Item item = p.equipment[13];
                if (item.hasExpire()) {
                    p.serverDialog("Ngươi cần đeo yoroi vĩnh viễn mới đc.");
                    return;
                }
                if (item != null) {
                    if (item.upgrade >= 16) {
                        p.serverDialog("ntgt của ngươi đã quá mạnh, ta không thể giúp được ngươi.");
                        return;
                    }
                    int[] fee1 = {500, 1000, 1500, 2000, 2500, 3000, 3500, 4000, 4500, 5000, 5500, 6000, 6500, 7000, 7500, 8000};
                    int[] xu1 = {50000, 100000, 150000, 200000, 250000, 300000, 350000, 400000, 450000, 500000, 550000, 600000, 650000, 700000, 750000, 800000};
                    int[] da = new int[]{5, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150};
                    int percent1 = GameData.UP_rb[item.upgrade];
                    p.setConfirmPopup(new ConfirmPopup(CMDConfirmPopup.NANG_ntgt, String.format(
                            "Ngươi có muốn nâng cấp pet đang sử dụng lên cấp %d không? với phí %d lượng , %d xu và cần %d đá Hoàng Bảo - Tỉ lệ thành công: %s.",
                            (item.upgrade + 1), fee1[item.upgrade], xu1[item.upgrade], da[item.upgrade], percent1 + "%")));
                    p.getService().openUIConfirmID();
                } else {
                    p.serverDialog("Ngươi hãy sử dụng ntgt ta sẽ giúp ngươi làm thay đổi sức mạnh.");
                }
            }));
            p.getService().openUIMenu();
        }));
        p.menus.add(new Menu(CMDMenu.EXECUTE, "ntgt thần", () -> {
            p.menus.clear();
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Khai mở chỉ số", () -> {
                if (p.fashion[13] == null) {
                    p.serverDialog("Ngươi cần đeo ntgt lên.");
                    return;
                }
                if (p.fashion[13].id != 1207) {
                    p.serverDialog("Ngươi cần đeo ntgt lên.");
                    return;
                }
                Item item1 = p.fashion[13];
                if (item1.hasExpire()) {
                    p.serverDialog("Ngươi cần đeo ntgt vĩnh viễn mới đc.");
                    return;
                }
                if (p.fashion[13].id == 1207) {
                    if (p.user.gold >= 10000) {
                        if (p.getSlotNull() == 0) {
                            p.serverDialog("Hãy chừa 1 ô trống trong hành trang để nhận bí kíp.");
                            return;
                        }
                        boolean hasOption = false;
                        for (ItemOption option : item1.options) {
                            if (option.optionTemplate.id == 82
                                    || option.optionTemplate.id == 83
                                    || option.optionTemplate.id == 87
                                    || option.optionTemplate.id == 88
                                    || option.optionTemplate.id == 89
                                    || option.optionTemplate.id == 90) {
                                hasOption = true;
                                break;
                            }
                        }
                        if (hasOption) {
                            p.serverDialog("Chỉ số đã được mở rồi.");
                            return;
                        }

                        ArrayList<ItemOption> options = new ArrayList<>();
                        double successRate = 0.2;
                        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 82)) {
                            options.add(new ItemOption(82, 5000));
                        }
                        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 83)) {
                            options.add(new ItemOption(83, 5000));
                        }
                        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 87)) {
                            options.add(new ItemOption(87, 5000));
                        }
                        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 88)) {
                            options.add(new ItemOption(88, 5000));
                        }
                        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 89)) {
                            options.add(new ItemOption(89, 5000));
                        }
                        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 90)) {
                            options.add(new ItemOption(90, 5000));
                        }
                        if (Math.random() < successRate) {
                            p.addLuong(-10000);
                            item1.options.addAll(options);
                            p.setFashion();
                            p.setAbility();
                            p.serverDialog("Khai mở thành công trừ 10k lượng!");
                        } else {
                            p.addLuong(-10000);
                            p.setFashion();
                            p.setAbility();
                            p.serverDialog("Khai mở thất bại trừ 10k lượng");
                        }
                    } else {
                        p.serverDialog("Hãy đưa ta 10.000 lượng ta mới giúp ngươi.");
                    }
                } else {
                    p.serverDialog("Ngươi cần đeo ntgt thần trên người đã rồi luyện nhé.");
                }
            }));

            p.menus.add(new Menu(CMDMenu.EXECUTE, "Nâng cấp ", () -> {
                if (p.fashion[13] == null) {
                    p.serverDialog("Ngươi cần đeo ntgt lên .");
                    return;
                }
                if (p.fashion[13].id != 1207) {
                    p.serverDialog("Ngươi cần đeo ntgt lên.");
                    return;
                }
                Item item = p.fashion[13];
                if (item.hasExpire()) {
                    p.serverDialog("Ngươi cần đeo ntgt vĩnh viễn mới đc.");
                    return;
                }
                if (item != null) {
                    if (item.upgrade >= 16) {
                        p.serverDialog("ntgt của ngươi đã quá mạnh, ta không thể giúp được ngươi.");
                        return;
                    }
                    int[] fee1 = {500, 1000, 1500, 2000, 2500, 3000, 3500, 4000, 4500, 5000, 5500, 6000, 6500, 7000, 7500, 8000};
                    int[] da = new int[]{5, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150};
                    int percent1 = GameData.UP_rb[item.upgrade];
                    p.setConfirmPopup(new ConfirmPopup(CMDConfirmPopup.NANG_ntgt1, String.format(
                            "Ngươi có muốn nâng cấp pet đang sử dụng lên cấp %d không? với phí %d lượng và cần %d đá Hoàng Bảo - Tỉ lệ thành công: %s.",
                            (item.upgrade + 1), fee1[item.upgrade], da[item.upgrade], percent1 + "%")));
                    p.getService().openUIConfirmID();
                } else {
                    p.serverDialog("Ngươi hãy sử dụng ntgt ta sẽ giúp ngươi làm thay đổi sức mạnh.");
                }
            }));
            p.getService().openUIMenu();
        }));
        p.getService().openUIMenu();
    }

    public void nangruby(Char p) {
        p.menus.clear();
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Nhận chỉ số", () -> {
            if (p.fashion[14] == null) {
                p.serverDialog("Ngươi cần đeo ruby thần trên người đã rồi luyện nhé .");
                return;
            }
            if (p.fashion[14].id != 1115) {
                p.serverDialog("Ngươi cần đeo ruby thần trên người đã rồi luyện nhé .");
                return;
            }
            Item item1 = p.fashion[14];
            if (item1.hasExpire()) {
                p.serverDialog("Ngươi cần đeo yoroi vĩnh viễn mới đc.");
                return;
            }
            if (item1.upgrade >= 1) {
                p.serverDialog(String.format("%s đã nâng cấp, không thể luyện chỉ số", item1.template.name));
                return;
            }
            if (p.fashion[14] != null) {
                if (p.user.gold >= 1000) {
                    if (p.getSlotNull() == 0) {
                        p.serverDialog("Hãy chừa 1 ô trống trong hành trang để nhận bí kíp.");
                        return;
                    }
                    p.addLuong(-1000);
                    int itemId = p.fashion[14].id;
                    p.getService().deleteItemBody(14);
                    p.fashion[14] = null;
                    Item item = ItemFactory.getInstance().newItem(itemId);
                    item.isLock = true;
                    int random = NinjaUtils.nextInt(3, 5);
                    ArrayList<ItemOption> options = new ArrayList<>();
                    options.add(new ItemOption(81, NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(82, NinjaUtils.nextInt(500, 1500)));
                    options.add(new ItemOption(83, NinjaUtils.nextInt(500, 1500)));
                    options.add(new ItemOption(84, NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(86, NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(87, NinjaUtils.nextInt(100, 800)));
                    options.add(new ItemOption(88, NinjaUtils.nextInt(100, 1000)));
                    options.add(new ItemOption(89, NinjaUtils.nextInt(100, 1000)));
                    options.add(new ItemOption(90, NinjaUtils.nextInt(100, 1000)));
                    options.add(new ItemOption(91, NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(92, NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(95, NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(96, NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(97, NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(98, NinjaUtils.nextInt(5, 10)));

                    item.options.add(new ItemOption(85, 0));
                    for (int i = 0; i < random; i++) {
                        int index = 0;
                        if (options.get(options.size() - 1).optionTemplate.id == 100) {
                            if (NinjaUtils.nextInt(100) < 30) {
                                index = options.size() - 1;
                            }
                        } else {
                            index = NinjaUtils.nextInt(options.size());
                        }
                        ItemOption iop = options.get(index);
                        item.options.add(iop);
                        options.remove(index);
                    }
                    p.themItemToBag(item);
                    p.setFashion();
                    p.setAbility();
                    String text = "";
                    if (random == 1 || random == 2) {
                        text = "Ta chỉ giúp được cho ngươi đến thế thôi, ta xin lỗi.";
                    } else if (random == 3 || random == 4) {
                        text = "Không tệ ngươi xem có ổn không.";
                    } else {
                        text = "Khá mạnh đó, ngươi thấy ta làm tốt không?";
                    }
                    p.serverDialog(text);
                } else {
                    p.serverDialog("Hãy đưa ta 1.000 lượng ta mới giúp ngươi.");
                }
            } else {
                p.serverDialog("Ngươi cần đeo ruby thần trên người đã rồi luyện nhé .");
            }
        }));
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Nâng cấp ruby", () -> {
            if (p.fashion[14] == null) {
                p.serverDialog("Ngươi cần đeo ruby  lên  .");
                return;
            }
            if (p.fashion[14].id != 1115) {
                p.serverDialog("Ngươi cần đeo ruby  lên .");
                return;
            }
            Item item = p.fashion[14];
            if (item.hasExpire()) {
                p.serverDialog("Ngươi cần đeo yoroi vĩnh viễn mới đc.");
                return;
            }
            if (item != null) {
                if (item.upgrade >= 16) {
                    p.serverDialog("ruby của ngươi đã quá mạnh, ta không thể giúp được ngươi.");
                    return;
                }
                int[] fee1 = {100, 300, 500, 600, 700, 800, 1000, 1200, 1400, 1600, 1800, 2000, 2300, 2500, 2700, 3000};
                int percent1 = GameData.UP_rb[item.upgrade];
                p.setConfirmPopup(new ConfirmPopup(CMDConfirmPopup.NANG_rb, String.format(
                        "Ngươi có muốn nâng cấp ruby đang sử dụng lên cấp %d không? với phí %d lượng - Tỉ lệ thành công: %s.",
                        (item.upgrade + 1), fee1[item.upgrade], percent1 + "%")));
                p.getService().openUIConfirmID();
            } else {
                p.serverDialog("Ngươi hãy sử dụng ruby ta sẽ giúp ngươi làm thay đổi sức mạnh.");
            }
        }));
        p.getService().openUIMenu();
    }

    public void nangbikip(Char p) {
        p.menus.clear();
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Luyện bí kíp", () -> {
            if (p.equipment[ItemTemplate.TYPE_BIKIP] != null) {
                Item item1 = p.equipment[ItemTemplate.TYPE_BIKIP];
                if (item1.upgrade >= 1) {
                    p.serverDialog(String.format("%s đã nâng cấp, không thể luyện chỉ số", item1.template.name));
                    return;
                }
                if (p.user.gold >= 1000) {
                    if (p.getSlotNull() == 0) {
                        p.serverDialog("Hãy chừa 1 ô trống trong hành trang để nhận bí kíp.");
                        return;
                    }
                    p.addLuong(-1000);
                    int itemId = p.equipment[ItemTemplate.TYPE_BIKIP].id;
                    p.getService().deleteItemBody(ItemTemplate.TYPE_BIKIP);
                    p.equipment[ItemTemplate.TYPE_BIKIP] = null;
                    Item item = ItemFactory.getInstance().newItem(itemId);
                    item.isLock = true;
                    int random = NinjaUtils.nextInt(2, 5);
                    ArrayList<ItemOption> options = new ArrayList<>();
                    options.add(new ItemOption(81, NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(82, NinjaUtils.nextInt(500, 1500)));
                    options.add(new ItemOption(83, NinjaUtils.nextInt(500, 1500)));
                    options.add(new ItemOption(84, NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(86, NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(87, NinjaUtils.nextInt(100, 800)));
                    options.add(new ItemOption(88, NinjaUtils.nextInt(100, 1000)));
                    options.add(new ItemOption(89, NinjaUtils.nextInt(100, 1000)));
                    options.add(new ItemOption(90, NinjaUtils.nextInt(100, 1000)));
                    options.add(new ItemOption(91, NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(92, NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(95, NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(96, NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(97, NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(98, NinjaUtils.nextInt(5, 10)));

                    item.options.add(new ItemOption(85, 0));
                    for (int i = 0; i < random; i++) {
                        int index = 0;
                        if (options.get(options.size() - 1).optionTemplate.id == 100) {
                            if (NinjaUtils.nextInt(100) < 30) {
                                index = options.size() - 1;
                            }
                        } else {
                            index = NinjaUtils.nextInt(options.size());
                        }
                        ItemOption iop = options.get(index);
                        item.options.add(iop);
                        options.remove(index);
                    }
                    p.themItemToBag(item);
                    p.setFashion();
                    p.setAbility();
                    String text = "";
                    if (random == 1 || random == 2) {
                        text = "Ta chỉ giúp được cho ngươi đến thế thôi, ta xin lỗi.";
                    } else if (random == 3 || random == 4) {
                        text = "Không tệ ngươi xem có ổn không.";
                    } else {
                        text = "Khá mạnh đó, ngươi thấy ta làm tốt không?";
                    }
                    p.serverDialog(text);
                } else {
                    p.serverDialog("Hãy đưa ta 1.000 lượng ta mới giúp ngươi.");
                }
            } else {
                p.serverDialog("Ngươi cần đeo bí kíp trên người đã rồi luyện nhé .");
            }
        }));
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Nâng cấp bí kíp", () -> {
            int itemID = -1;
            switch (p.getSys()) {
                case 1:
                    itemID = 834;
                    break;

                case 2:
                    itemID = 836;
                    break;

                case 3:
                    itemID = 835;
                    break;
            }
            if (itemID == -1) {
                return;
            }
            Item item = p.equipment[ItemTemplate.TYPE_BIKIP];
            if (item != null) {
                if (item.upgrade >= 16) {
                    p.serverDialog("Bí kíp của ngươi đã quá mạnh, ta không thể giúp được ngươi.");
                    return;
                }
                int[] quantity = {1, 3, 5, 7, 10, 12, 15, 17, 20, 22, 25, 27, 30, 35, 40, 50};
                int[] fee = {100, 300, 500, 600, 700, 800, 1000, 1200, 1400, 1600, 1800, 2000, 2300, 2500, 2700, 3000};
                int percent = GameData.UP_BI_KIP[item.upgrade];
                String name = ItemManager.getInstance().getItemName(itemID);
                p.setConfirmPopup(new ConfirmPopup(CMDConfirmPopup.NANG_BI_KIP, String.format(
                        "Ngươi có muốn nâng cấp bí kíp đang sử dụng lên cấp %d không? Cần %d viên %s với phí %d lượng - Tỉ lệ thành công: %s.",
                        (item.upgrade + 1), quantity[item.upgrade], name, fee[item.upgrade], percent + "%")));
                p.getService().openUIConfirmID();
            } else {
                p.serverDialog("Ngươi hãy sử dụng bí kíp ta sẽ giúp ngươi làm thay đổi sức mạnh.");
            }
        }));
        p.getService().openUIMenu();
    }

}

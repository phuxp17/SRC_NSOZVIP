package com.tea.model;

import com.tea.option.ItemOption;
import com.tea.util.NinjaUtils;
import com.tea.constants.NpcName;
import com.tea.item.Equip;
import com.tea.item.Item;
import com.tea.item.ItemFactory;
import com.tea.item.ItemTemplate;
import com.tea.server.GlobalService;

import java.util.ArrayList;

public class DungHop {

    public void dungHopnon(Char p, int theId) {
        if (p.equipment[ItemTemplate.TYPE_NON] != null) {
            Item item1 = p.equipment[ItemTemplate.TYPE_NON];
            if (!item1.gems.isEmpty()) {
                p.serverDialog("Trang bị này có ngọc hãy tháo ra.");
                return;
            }
            if (item1.upgrade < 16) {
                p.serverDialog(String.format("%s Cần Nâng Cấp +16 Mới Có Thể Dung Hợp", item1.template.name));
                return;
            }
            if (item1.upgrade == 17) {
                p.serverDialog(String.format("%s Đã Dung Hợp Không Thể Tiếp Tục", item1.template.name));
                return;
            }
            if (p.user.gold >= 50000) {
                if (p.getSlotNull() == 0) {
                    p.serverDialog("Hãy chừa 1 ô trống trong hành trang để nhận đồ.");
                    return;
                }
                int indexST = p.getIndexItemByIdInBag(theId);
                if (indexST == -1 || p.bag[indexST] == null || p.bag[indexST].getQuantity() < 1) {
                    p.serverDialog("Bạn không đủ thẻ!");
                    return;
                }

                ArrayList<ItemOption> options = new ArrayList<>();
                double successRate = 0.0;
                switch (theId) {
                    case 1216: // Thẻ 1
                        successRate = 0.5; // 50% tỉ lệ lên
                        break;
                    case 1217: // Thẻ 2
                        successRate = 0.4; // 40% tỉ lệ lên
                        break;
                    case 1218: // Thẻ 3
                        successRate = 0.3; // 30% tỉ lệ lên
                        break;
                    case 1219: // Thẻ 4
                        successRate = 0.2; // 20% tỉ lệ lên
                        break;
                    case 1220: // Thẻ 5
                        successRate = 0.15; // 15% tỉ lệ lên
                        break;
                    case 1221: // Thẻ 6
                        successRate = 0.1; 
                        break;
                }
                if (!options.stream().anyMatch(o -> o.optionTemplate.id == 176)) {
                    options.add(new ItemOption(176, theId - 1215));
                }
                if (!options.stream().anyMatch(o -> o.optionTemplate.id == 177)) {
                    options.add(new ItemOption(177, 100));
                }
                if (!options.stream().anyMatch(o -> o.optionTemplate.id == 178)) {
                    options.add(new ItemOption(178, (theId - 1215) * 20));
                }
                if (!options.stream().anyMatch(o -> o.optionTemplate.id == 179)) {
                    options.add(new ItemOption(179, (theId - 1215) * 5));
                }
                if (successRate == 1.0 || Math.random() < successRate) {
                    p.addLuong(-50000);
                    item1.options.addAll(options);
                    item1.next(+1);
                    p.removeItem(indexST, 1, true);
                    p.setFashion();
                    p.setAbility();
                    p.serverDialog("Dung hợp thành công trừ 50k lượng!");
                    Item item2 = ItemFactory.getInstance().newItem(theId);
                    String successMessage = String.format("%s vừa dung hợp thành công đồ bằng : %s thực lực tăng lên một tầm cao mới!", p.setNameVip(p.name), item2.template.name);
                    GlobalService.getInstance().chat("Dung Hợp",successMessage);
                } else {
                    p.addLuong(-50000);
                    p.removeItem(indexST, 1, true);
                    p.setFashion();
                    p.setAbility();
                    p.serverDialog("Dung hợp thất bại trừ 50k lượng");
                }
            } else {
                p.serverDialog("Hãy đưa ta 50.000 lượng ta mới giúp ngươi.");
            }
        } else {
            p.serverDialog("Ngươi cần Trang Bị NÓN trên người đã rồi luyện nhé.");
        }
        return;
    }

    public void dungHopao(Char p, int theId) {
        if (p.equipment[ItemTemplate.TYPE_AO] != null) {
            Item item1 = p.equipment[ItemTemplate.TYPE_AO];
            if (!item1.gems.isEmpty()) {
                p.serverDialog("Trang bị này có ngọc hãy tháo ra.");
                return;
            }
            if (item1.upgrade < 16) {
                p.serverDialog(String.format("%s Cần Nâng Cấp +16 Mới Có Thể Dung Hợp", item1.template.name));
                return;
            }
            if (item1.upgrade == 17) {
                p.serverDialog(String.format("%s Đã Dung Hợp Không Thể Tiếp Tục", item1.template.name));
                return;
            }
            if (p.user.gold >= 50000) {
                if (p.getSlotNull() == 0) {
                    p.serverDialog("Hãy chừa 1 ô trống trong hành trang để nhận đồ.");
                    return;
                }
                int indexST = p.getIndexItemByIdInBag(theId);
                if (indexST == -1 || p.bag[indexST] == null || p.bag[indexST].getQuantity() < 1) {
                    p.serverDialog("Bạn không đủ thẻ!");
                    return;
                }

                double successRate = 0.0; // Tỉ lệ thành công ban đầu
                ArrayList<ItemOption> options = new ArrayList<>();
                switch (theId) {
                    case 1216: // Thẻ 1
                        successRate = 0.5;
                        break;
                    case 1217: // Thẻ 2
                        successRate = 0.4;
                        break;
                    case 1218: // Thẻ 3
                        successRate = 0.3;
                        break;
                    case 1219: // Thẻ 4
                        successRate = 0.2;
                        break;
                    case 1220: // Thẻ 5
                        successRate = 0.15;
                        break;
                    case 1221: // Thẻ 6
                        successRate = 0.1; // 10% tỉ lệ lên
                        break;
                    default:
                        break;
                }
                if (!options.stream().anyMatch(o -> o.optionTemplate.id == 176)) {
                    options.add(new ItemOption(176, theId - 1215));
                }
                if (!options.stream().anyMatch(o -> o.optionTemplate.id == 177)) {
                    options.add(new ItemOption(177, 100));
                }
                if (!options.stream().anyMatch(o -> o.optionTemplate.id == 180)) {
                    options.add(new ItemOption(180, (theId - 1215) * 5 + 10));
                }
                if (!options.stream().anyMatch(o -> o.optionTemplate.id == 181)) {
                    options.add(new ItemOption(181, (theId - 1215) * 30));
                }

                if (Math.random() < successRate) {
                    p.addLuong(-50000);
                    item1.options.addAll(options); // Thêm các tùy chọn vào item
                    item1.next(+1);
                    p.removeItem(indexST, 1, true);
                    p.setFashion();
                    p.setAbility();
                    p.serverDialog("Dung hợp thành công!");
                    Item item2 = ItemFactory.getInstance().newItem(theId);
                    String successMessage = String.format("%s vừa dung hợp thành công đồ bằng : %s thực lực tăng lên một tầm cao mới!", p.setNameVip(p.name), item2.template.name);
                    GlobalService.getInstance().chat("Dung Hợp",successMessage);
                } else {
                    p.addLuong(-50000);
                    p.removeItem(indexST, 1, true);
                    p.setFashion();
                    p.setAbility();
                    p.serverDialog("Dung hợp thất bại!");
                }
            } else {
                p.serverDialog("Hãy đưa ta 50.000 lượng ta mới giúp ngươi.");
            }
        } else {
            p.serverDialog("Ngươi cần Trang Bị ÁO trên người đã rồi luyện nhé .");
        }
        return;
    }

    public void dungHopgang(Char p, int theId) {
        if (p.equipment[ItemTemplate.TYPE_GANGTAY] != null) {
            Item item1 = p.equipment[ItemTemplate.TYPE_GANGTAY];

            if (!item1.gems.isEmpty()) {
                p.serverDialog("Trang bị này có ngọc hãy tháo ra.");
                return;
            }
            if (item1.upgrade < 16) {
                p.serverDialog(String.format("%s Cần Nâng Cấp +16 Mới Có Thể Dung Hợp", item1.template.name));
                return;
            }
            if (item1.upgrade == 17) {
                p.serverDialog(String.format("%s Đã Dung Hợp Không Thể Tiếp Tục", item1.template.name));
                return;
            }
            if (p.user.gold >= 50000) {
                if (p.getSlotNull() == 0) {
                    p.serverDialog("Hãy chừa 1 ô trống trong hành trang để nhận.");
                    return;
                }
                int indexST = p.getIndexItemByIdInBag(theId);
                if (indexST == -1 || p.bag[indexST] == null || p.bag[indexST].getQuantity() < 1) {
                    p.serverDialog("Bạn không đủ thẻ!");
                    return;
                }

                double successRate = 0.0; // Tỉ lệ thành công ban đầu
                ArrayList<ItemOption> options = new ArrayList<>();
                switch (theId) {
                    case 1216: // Thẻ 1
                        successRate = 0.4;
                        break;
                    case 1217: // Thẻ 2
                        successRate = 0.3;
                        break;
                    case 1218: // Thẻ 3
                        successRate = 0.2;
                        break;
                    case 1219: // Thẻ 4
                        successRate = 0.1;
                        break;
                    case 1220: // Thẻ 5
                        successRate = 0.05;
                        break;
                    case 1221: // Thẻ 6
                        successRate = 0.1;
                        break;
                    default:
                        break;
                }
                if (!options.stream().anyMatch(o -> o.optionTemplate.id == 176)) {
                    options.add(new ItemOption(176, theId - 1215));
                }
                if (!options.stream().anyMatch(o -> o.optionTemplate.id == 177)) {
                    options.add(new ItemOption(177, 100));
                }
                if (!options.stream().anyMatch(o -> o.optionTemplate.id == 182)) {
                    options.add(new ItemOption(182, (theId - 1215) * 25));
                }
                if (!options.stream().anyMatch(o -> o.optionTemplate.id == 183)) {
                    options.add(new ItemOption(183, (theId - 1215) * 30));
                }

                if (Math.random() < successRate) {
                    p.addLuong(-50000);
                    item1.options.addAll(options);
                    item1.next(+1);
                    p.removeItem(indexST, 1, true);
                    p.setFashion();
                    p.setAbility();
                    p.serverDialog("Dung hợp thành công!");
                    Item item2 = ItemFactory.getInstance().newItem(theId);
                    String successMessage = String.format("%s vừa dung hợp thành công đồ bằng : %s thực lực tăng lên một tầm cao mới!", p.setNameVip(p.name), item2.template.name);
                    GlobalService.getInstance().chat("Dung Hợp",successMessage);
                } else {
                    p.addLuong(-50000);
                    p.removeItem(indexST, 1, true);
                    p.setFashion();
                    p.setAbility();
                    p.serverDialog("Dung hợp thất bại!");
                }
            } else {
                p.serverDialog("Hãy đưa ta 50.000 lượng ta mới giúp ngươi.");
            }
        } else {
            p.serverDialog("Ngươi cần Trang Bị GĂNG TAY trên người đã rồi luyện nhé.");
        }
        return;
    }

    public void dungHopquan(Char p, int theId) {
        if (p.equipment[ItemTemplate.TYPE_QUAN] != null) {
            Item item1 = p.equipment[ItemTemplate.TYPE_QUAN];
            if (!item1.gems.isEmpty()) {
                p.serverDialog("Trang bị này có ngọc hãy tháo ra.");
                return;
            }
            if (item1.upgrade < 16) {
                p.serverDialog(String.format("%s Cần Nâng Cấp +16 Mới Có Thể Dung Hợp", item1.template.name));
                return;
            }
            if (item1.upgrade == 17) {
                p.serverDialog(String.format("%s Đã Dung Hợp Không Thể Tiếp Tục", item1.template.name));
                return;
            }
            if (p.user.gold >= 50000) {
                if (p.getSlotNull() == 0) {
                    p.serverDialog("Hãy chừa 1 ô trống trong hành trang để nhận.");
                    return;
                }
                int indexST = p.getIndexItemByIdInBag(theId);
                if (indexST == -1 || p.bag[indexST] == null || p.bag[indexST].getQuantity() < 1) {
                    p.serverDialog("Bạn không đủ thẻ!");
                    return;
                }

                double successRate = 0.0; // Tỉ lệ thành công ban đầu
                ArrayList<ItemOption> options = new ArrayList<>();
                switch (theId) {
                    case 1216: // Thẻ 1
                        successRate = 0.5;
                        break;
                    case 1217: // Thẻ 2
                        successRate = 0.4;
                        break;
                    case 1218: // Thẻ 3
                        successRate = 0.3;
                        break;
                    case 1219: // Thẻ 4
                        successRate = 0.2;
                        break;
                    case 1220: // Thẻ 5
                        successRate = 0.15;
                        break;
                    case 1221: // Thẻ 6
                        successRate = 0.1;
                        break;
                    default:
                        break;
                }
                if (!options.stream().anyMatch(o -> o.optionTemplate.id == 176)) {
                    options.add(new ItemOption(176, theId - 1215));
                }
                if (!options.stream().anyMatch(o -> o.optionTemplate.id == 177)) {
                    options.add(new ItemOption(177, 100));
                }
                if (!options.stream().anyMatch(o -> o.optionTemplate.id == 184)) {
                    options.add(new ItemOption(184, (theId - 1215) * 100 + 350));
                }
                if (!options.stream().anyMatch(o -> o.optionTemplate.id == 185)) {
                    options.add(new ItemOption(185, (theId - 1215) * 100 + 350));
                }

                if (Math.random() < successRate) {
                    p.addLuong(-50000);
                    item1.options.addAll(options); // Thêm các tùy chọn vào item
                    item1.next(+1);
                    p.removeItem(indexST, 1, true);
                    p.setFashion();
                    p.setAbility();
                    p.serverDialog("Dung hợp thành công!");
                    Item item2 = ItemFactory.getInstance().newItem(theId);
                    String successMessage = String.format("%s vừa dung hợp thành công đồ bằng : %s thực lực tăng lên một tầm cao mới!", p.setNameVip(p.name), item2.template.name);
                    GlobalService.getInstance().chat("Dung Hợp",successMessage);
                } else {
                    p.addLuong(-50000);
                    p.removeItem(indexST, 1, true);
                    p.setFashion();
                    p.setAbility();
                    p.serverDialog("Dung hợp thất bại!");
                }
            } else {
                p.serverDialog("Hãy đưa ta 50.000 lượng ta mới giúp ngươi.");
            }
        } else {
            p.serverDialog("Ngươi cần Trang Bị QUẦN trên người đã rồi luyện nhé.");
        }
        return;
    }

    public void dungHopgiay(Char p, int theId) {
        if (p.equipment[ItemTemplate.TYPE_GIAY] != null) {
            Item item1 = p.equipment[ItemTemplate.TYPE_GIAY];
            if (!item1.gems.isEmpty()) {
                p.serverDialog("Trang bị này có ngọc hãy tháo ra.");
                return;
            }
            if (item1.upgrade < 16) {
                p.serverDialog(String.format("%s cần nâng cấp +16 mới có thể dung hợp", item1.template.name));
                return;
            }
            if (item1.upgrade == 17) {
                p.serverDialog(String.format("%s đã dung hợp không thể tiếp tục", item1.template.name));
                return;
            }
            if (p.user.gold < 50000) {
                p.serverDialog("Hãy đưa ta 50.000 lượng ta mới giúp ngươi.");
                return;
            }
            if (p.getSlotNull() == 0) {
                p.serverDialog("Hãy chừa 1 ô trống trong hành trang để nhận.");
                return;
            }

            int indexST = p.getIndexItemByIdInBag(theId);
            if (indexST == -1 || p.bag[indexST] == null || p.bag[indexST].getQuantity() < 1) {
                p.serverDialog("Bạn không đủ thẻ!");
                return;
            }

            double successRate = 0.0; // Tỉ lệ thành công ban đầu
            ArrayList<ItemOption> options = new ArrayList<>();
            switch (theId) {
                case 1216: // Thẻ 1
                    successRate = 0.5;
                    break;
                case 1217: // Thẻ 2
                    successRate = 0.4;
                    break;
                case 1218: // Thẻ 3
                    successRate = 0.3;
                    break;
                case 1219: // Thẻ 4
                    successRate = 0.2;
                    break;
                case 1220: // Thẻ 5
                    successRate = 0.15;
                    break;
                case 1221: // Thẻ 6
                    successRate = 0.1;
                    break;
            }
            if (!options.stream().anyMatch(o -> o.optionTemplate.id == 176)) {
                options.add(new ItemOption(176, theId - 1215));
            }
            if (!options.stream().anyMatch(o -> o.optionTemplate.id == 177)) {
                options.add(new ItemOption(177, 100));
            }
            if (!options.stream().anyMatch(o -> o.optionTemplate.id == 185)) {
                options.add(new ItemOption(185, 100 * (theId - 1215) + 350));
            }
            if (!options.stream().anyMatch(o -> o.optionTemplate.id == 186)) {
                options.add(new ItemOption(186, 30 * (theId - 1215)));
            }
            if (Math.random() < successRate) {
                p.addLuong(-50000);
                item1.options.addAll(options); // Thêm các tùy chọn vào item
                item1.next(+1);
                p.removeItem(indexST, 1, true);
                p.setFashion();
                p.setAbility();
                p.serverDialog("Dung hợp thành công!");
                Item item2 = ItemFactory.getInstance().newItem(theId);
                String successMessage = String.format("%s vừa dung hợp thành công đồ bằng : %s thực lực tăng lên một tầm cao mới!", p.setNameVip(p.name), item2.template.name);
                    GlobalService.getInstance().chat("Dung Hợp",successMessage);
            } else {
                p.addLuong(-50000);
                p.removeItem(indexST, 1, true);
                p.setFashion();
                p.setAbility();
                p.serverDialog("Dung hợp thất bại!");
            }
        } else {
            p.serverDialog("Ngươi cần Trang Bị GIÀY trên người đã rồi luyện nhé.");
        }
    }

    public void dungHopchuyen(Char p, int theId) {
        if (p.equipment[ItemTemplate.TYPE_LIEN] == null) {
            p.serverDialog("Ngươi cần Trang Bị LIÊN trên người đã rồi luyện nhé.");
            return;
        }
        Item item1 = p.equipment[ItemTemplate.TYPE_LIEN];
        if (!item1.gems.isEmpty()) {
            p.serverDialog("Trang bị này có ngọc hãy tháo ra.");
            return;
        }
        if (item1.upgrade < 16) {
            p.serverDialog(String.format("%s cần nâng cấp +16 mới có thể dung hợp", item1.template.name));
            return;
        }
        if (item1.upgrade == 17) {
            p.serverDialog(String.format("%s đã dung hợp không thể tiếp tục", item1.template.name));
            return;
        }
        if (p.user.gold < 50000) {
            p.serverDialog("Hãy đưa ta 50.000 lượng ta mới giúp ngươi.");
            return;
        }
        if (p.getSlotNull() == 0) {
            p.serverDialog("Hãy chừa 1 ô trống trong hành trang để nhận.");
            return;
        }
        int indexST = p.getIndexItemByIdInBag(theId);
        if (indexST == -1 || p.bag[indexST] == null || p.bag[indexST].getQuantity() < 1) {
            p.serverDialog("Bạn không đủ thẻ!");
            return;
        }

        double successRate = 0.0; // Tỉ lệ thành công ban đầu
        ArrayList<ItemOption> options = new ArrayList<>();
        switch (theId) {
            case 1216: // Thẻ 1
                successRate = 0.5;
                break;
            case 1217: // Thẻ 2
                successRate = 0.4;
                break;
            case 1218: // Thẻ 3
                successRate = 0.3;
                break;
            case 1219: // Thẻ 4
                successRate = 0.2;
                break;
            case 1220: // Thẻ 5
                successRate = 0.15;
                break;
            case 1221: // Thẻ 6
                successRate = 0.1;
                break;
        }
        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 176)) {
            options.add(new ItemOption(176, theId - 1215));
        }
        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 177)) {
            options.add(new ItemOption(177, 100));
        }
        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 178)) {
            options.add(new ItemOption(178, 20 * (theId - 1215)));
        }
        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 179)) {
            options.add(new ItemOption(179, 5 * (theId - 1215)));
        }
        if (Math.random() < successRate) {
            p.addLuong(-50000);
            item1.options.addAll(options); 
            item1.next(+1);
            p.removeItem(indexST, 1, true);
            p.setFashion();
            p.setAbility();
            p.serverDialog("Dung hợp thành công!");
            Item item2 = ItemFactory.getInstance().newItem(theId);
            String successMessage = String.format("%s vừa dung hợp thành công đồ bằng : %s thực lực tăng lên một tầm cao mới!", p.setNameVip(p.name), item2.template.name);
                    GlobalService.getInstance().chat("Dung Hợp",successMessage);
        } else {
            p.addLuong(-50000);
            p.removeItem(indexST, 1, true);
            p.setFashion();
            p.setAbility();
            p.serverDialog("Dung hợp thất bại!");
        }
    }

    public void dungHopnhan(Char p, int theId) {
        if (p.equipment[ItemTemplate.TYPE_NHAN] == null) {
            p.serverDialog("Ngươi cần Trang Bị NHẪN trên người đã rồi luyện nhé.");
            return;
        }
        Item item1 = p.equipment[ItemTemplate.TYPE_NHAN];
        if (!item1.gems.isEmpty()) {
            p.serverDialog("Trang bị này có ngọc hãy tháo ra.");
            return;
        }
        if (item1.upgrade < 16) {
            p.serverDialog(String.format("%s cần nâng cấp +16 mới có thể dung hợp", item1.template.name));
            return;
        }
        if (item1.upgrade == 17) {
            p.serverDialog(String.format("%s đã dung hợp không thể tiếp tục", item1.template.name));
            return;
        }
        if (p.user.gold < 50000) {
            p.serverDialog("Hãy đưa ta 50.000 lượng ta mới giúp ngươi.");
            return;
        }
        if (p.getSlotNull() == 0) {
            p.serverDialog("Hãy chừa 1 ô trống trong hành trang để nhận.");
            return;
        }

        int indexST = p.getIndexItemByIdInBag(theId);
        if (indexST == -1 || p.bag[indexST] == null || p.bag[indexST].getQuantity() < 1) {
            p.serverDialog("Bạn không đủ thẻ!");
            return;
        }

        double successRate = 0.0;
        ArrayList<ItemOption> options = new ArrayList<>();
        switch (theId) {
            case 1216: // Thẻ 1
                successRate = 0.5;
                break;
            case 1217: // Thẻ 2
                successRate = 0.4;
                break;
            case 1218: // Thẻ 3
                successRate = 0.3;
                break;
            case 1219: // Thẻ 4
                successRate = 0.2;
                break;
            case 1220: // Thẻ 5
                successRate = 0.15;
                break;
            case 1221: // Thẻ 6
                successRate = 0.1;
                break;
        }
        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 176)) {
            options.add(new ItemOption(176, theId - 1215));
        }
        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 177)) {
            options.add(new ItemOption(177, 100));
        }
        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 178)) {
            options.add(new ItemOption(178, (theId - 1215) * 20));
        }
        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 187)) {
            options.add(new ItemOption(187, (theId - 1215) * 15));
        }

        if (Math.random() < successRate) {
            p.addLuong(-50000);
            item1.options.addAll(options); // Thêm các tùy chọn vào item
            item1.next(+1);
            p.removeItem(indexST, 1, true);
            p.setFashion();
            p.setAbility();
            p.serverDialog("Dung hợp thành công!");
            Item item2 = ItemFactory.getInstance().newItem(theId);
            String successMessage = String.format("%s vừa dung hợp thành công đồ bằng : %s thực lực tăng lên một tầm cao mới!", p.setNameVip(p.name), item2.template.name);
                    GlobalService.getInstance().chat("Dung Hợp",successMessage);
        } else {
            p.addLuong(-50000);
            p.removeItem(indexST, 1, true);
            p.setFashion();
            p.setAbility();
            p.serverDialog("Dung hợp thất bại!");
        }
    }

    public void dungHopboi(Char p, int theId) {
        if (p.equipment[ItemTemplate.TYPE_NGOCBOI] == null) {
            p.serverDialog("Ngươi cần Trang Bị NGỌC BỘI trên người đã rồi luyện nhé.");
            return;
        }
        Item item1 = p.equipment[ItemTemplate.TYPE_NGOCBOI];
        if (!item1.gems.isEmpty()) {
            p.serverDialog("Trang bị này có ngọc hãy tháo ra.");
            return;
        }
        if (item1.upgrade < 16) {
            p.serverDialog(String.format("%s cần nâng cấp +16 mới có thể dung hợp", item1.template.name));
            return;
        }
        if (item1.upgrade == 17) {
            p.serverDialog(String.format("%s đã dung hợp không thể tiếp tục", item1.template.name));
            return;
        }
        if (p.user.gold < 50000) {
            p.serverDialog("Hãy đưa ta 50.000 lượng ta mới giúp ngươi.");
            return;
        }
        if (p.getSlotNull() == 0) {
            p.serverDialog("Hãy chừa 1 ô trống trong hành trang để nhận.");
            return;
        }

        int indexST = p.getIndexItemByIdInBag(theId);
        if (indexST == -1 || p.bag[indexST] == null || p.bag[indexST].getQuantity() < 1) {
            p.serverDialog("Bạn không đủ thẻ!");
            return;
        }
        double successRate = 0.0;
        ArrayList<ItemOption> options = new ArrayList<>();
        switch (theId) {
            case 1216:
                successRate = 0.5;
                break;
            case 1217:
                successRate = 0.4;
                break;
            case 1218:
                successRate = 0.3;
                break;
            case 1219:
                successRate = 0.2;
                break;
            case 1220:
                successRate = 0.15;
                break;
            case 1221:
                successRate = 0.1;
                break;
        }

        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 176)) {
            options.add(new ItemOption(176, theId - 1215));
        }
        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 177)) {
            options.add(new ItemOption(177, 100));
        }
        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 178)) {
            options.add(new ItemOption(178, (theId - 1215) * 20));
        }
        if (!options.stream().anyMatch(o -> o.optionTemplate.id == 188)) {
            options.add(new ItemOption(188, 100 * (theId - 1215) + 350));
        }

        // Dung hợp và cập nhật item
        if (Math.random() < successRate) {
            p.addLuong(-50000);
            item1.options.addAll(options);
            item1.next(+1);
            p.removeItem(indexST, 1, true);
            p.setFashion();
            p.setAbility();
            p.serverDialog("Dung hợp thành công!");
            Item item2 = ItemFactory.getInstance().newItem(theId);
            String successMessage = String.format("%s vừa dung hợp thành công đồ bằng : %s thực lực tăng lên một tầm cao mới!", p.setNameVip(p.name), item2.template.name);
                    GlobalService.getInstance().chat("Dung Hợp",successMessage);
        } else {
            p.addLuong(-50000);
            p.removeItem(indexST, 1, true);
            p.setFashion();
            p.setAbility();
            p.serverDialog("Dung hợp thất bại!");
        }
    }

    public void dungHopphu(Char p, int theId) {
        if (p.equipment[ItemTemplate.TYPE_PHU] != null) {
            Item item1 = p.equipment[ItemTemplate.TYPE_PHU];
            if (!item1.gems.isEmpty()) {
                p.serverDialog("Trang bị này có ngọc hãy tháo ra.");
                return;
            }
            if (item1.upgrade < 16) {
                p.serverDialog(String.format("%s Cần Nâng Cấp +16 Mới Có Thể Dung Hợp", item1.template.name));
                return;
            }
            if (item1.upgrade == 17) {
                p.serverDialog(String.format("%s Đã Dung Hợp Không Thể Tiếp Tục", item1.template.name));
                return;
            }
            if (p.user.gold >= 50000) {
                if (p.getSlotNull() == 0) {
                    p.serverDialog("Hãy chừa 1 ô trống trong hành trang để nhận đồ.");
                    return;
                }
                int indexST = p.getIndexItemByIdInBag(theId);
                if (indexST == -1 || p.bag[indexST] == null || p.bag[indexST].getQuantity() < 1) {
                    p.serverDialog("Bạn không đủ thẻ!");
                    return;
                }
                Item item2 = ItemFactory.getInstance().newItem(theId);

                ArrayList<ItemOption> options = new ArrayList<>();
                double successRate = 0.0;
                switch (theId) {
                    case 1216:
                        successRate = 0.5;
                        break;
                    case 1217:
                        successRate = 0.4;
                        break;
                    case 1218:
                        successRate = 0.3;
                        break;
                    case 1219:
                        successRate = 0.2;
                        break;
                    case 1220:
                        successRate = 0.15;
                        break;
                    case 1221:
                        successRate = 0.1; 
                        break;
                }
                if (!options.stream().anyMatch(o -> o.optionTemplate.id == 176)) {
                    options.add(new ItemOption(176, theId - 1215));
                }
                if (!options.stream().anyMatch(o -> o.optionTemplate.id == 177)) {
                    options.add(new ItemOption(177, 100));
                }
                if (!options.stream().anyMatch(o -> o.optionTemplate.id == 184)) {
                    options.add(new ItemOption(184, 450 + (theId - 1216) * 100)); // Adjusted for "Phụ"
                }
                if (!options.stream().anyMatch(o -> o.optionTemplate.id == 186)) {
                    options.add(new ItemOption(186, 30 + (theId - 1216) * 30)); // Adjusted for "Phụ"
                }
                if (successRate == 1.0 || Math.random() < successRate) {
                    p.addLuong(-50000);
                    item1.options.addAll(options);
                    item1.next(+1);
                    p.removeItem(indexST, 1, true);
                    p.setFashion();
                    p.setAbility();
                    p.serverDialog("Dung hợp thành công trừ 50k lượng!");
                    String successMessage = String.format("%s vừa dung hợp thành công đồ bằng : %s thực lực tăng lên một tầm cao mới!", p.setNameVip(p.name), item2.template.name);
                    GlobalService.getInstance().chat("Dung Hợp",successMessage);
                } else {
                    p.addLuong(-50000);
                    p.removeItem(indexST, 1, true);
                    p.setFashion();
                    p.setAbility();
                    p.serverDialog("Dung hợp thất bại trừ 50k lượng");
                }
            } else {
                p.serverDialog("Hãy đưa ta 50.000 lượng ta mới giúp ngươi.");
            }
        } else {
            p.serverDialog("Ngươi cần Trang Bị PHỤ trên người đã rồi luyện nhé.");
        }
        return;
    }

    public void dungHopvukhi(Char p, int theId) {
        if (p.equipment[ItemTemplate.TYPE_VUKHI] == null) {
            p.serverDialog("Ngươi cần Trang Bị VŨ KHÍ trên người đã rồi luyện nhé.");
            return;
        }

        Item item1 = p.equipment[ItemTemplate.TYPE_VUKHI];
        if (!item1.gems.isEmpty()) {
            p.serverDialog("Trang bị này có ngọc hãy tháo ra.");
            return;
        }
        if (item1.upgrade < 16) {
            p.serverDialog(String.format("%s Cần Nâng Cấp +16 Mới Có Thể Dung Hợp", item1.template.name));
            return;
        }
        if (item1.upgrade == 17) {
            p.serverDialog(String.format("%s Đã Dung Hợp Không Thể Tiếp Tục", item1.template.name));
            return;
        }
        if (p.user.gold < 50000) {
            p.serverDialog("Hãy đưa ta 50.000 lượng ta mới giúp ngươi.");
            return;
        }
        if (p.getSlotNull() == 0) {
            p.serverDialog("Hãy chừa 1 ô trống trong hành trang để nhận đồ.");
            return;
        }
        int indexST = p.getIndexItemByIdInBag(theId);
        if (indexST == -1 || p.bag[indexST] == null || p.bag[indexST].getQuantity() < 1) {
            p.serverDialog("Bạn không đủ thẻ!");
            return;
        }
        Item item2 = ItemFactory.getInstance().newItem(theId);

        ArrayList<ItemOption> options = new ArrayList<>();
        double successRate = 0.0;
        switch (theId) {
            case 1216: // Thẻ 1
                successRate = 0.5;
                break;
            case 1217: // Thẻ 2
                successRate = 0.4;
                break;
            case 1218: // Thẻ 3
                successRate = 0.3;
                break;
            case 1219: // Thẻ 4
                successRate = 0.2;
                break;
            case 1220: // Thẻ 5
                successRate = 0.15;
                break;
            case 1221: // Thẻ 6
                successRate = 0.1; 
                break;
        }
        addWeaponOptions(options, theId, p.classId);

        if (Math.random() < successRate) {
            p.addLuong(-50000);
            item1.options.addAll(options);
            item1.next(+1);
            p.removeItem(indexST, 1, true);
            p.setFashion();
            p.setAbility();
            p.serverDialog("Dung hợp thành công trừ 50k lượng!");
            String successMessage = String.format("[ %s ] vừa dung hợp thành công đồ bằng: %s thực lực tăng lên một tầm cao mới!", p.setNameVip(p.name), item2.template.name);
                    GlobalService.getInstance().chat("Dung Hợp",successMessage);
        } else {
            p.addLuong(-50000);
            p.removeItem(indexST, 1, true);
            p.setFashion();
            p.setAbility();
            p.serverDialog("Dung hợp thất bại trừ 50k lượng");
        }
    }

    private void addWeaponOptions(ArrayList<ItemOption> options, int theId, int classId) {
        int baseOptionValue = theId - 1215;
        options.add(new ItemOption(176, baseOptionValue));
        options.add(new ItemOption(177, 100));

        if (classId == 1 || classId == 2) {
            options.add(new ItemOption(188, 1000 * baseOptionValue));
            options.add(new ItemOption(189, 1000 * baseOptionValue));
        } else if (classId == 3 || classId == 4) {
            options.add(new ItemOption(188, 1000 * baseOptionValue));
            options.add(new ItemOption(190, 1000 * baseOptionValue));
        } else if (classId == 5 || classId == 6) {
            options.add(new ItemOption(188, 1000 * baseOptionValue));
            options.add(new ItemOption(191, 1000 * baseOptionValue));
        }
    }

}

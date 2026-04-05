
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

public class SelectCardVIP extends AbsSelectCard {

    private static final SelectCardVIP instance = new SelectCardVIP();

    public static SelectCardVIP getInstance() {
        return instance;
    }

    public static final long EXPIRE_3_DAY = 3 * 24 * 60 * 60 * 1000;
    public static final long EXPIRE_7_DAY = 7 * 24 * 60 * 60 * 1000;
    public static final long EXPIRE_20_DAY = 20 * 24 * 60 * 60 * 1000;

    @Override
    protected void init() {
        add(Card.builder().id(1182).rate(5).build());
        add(Card.builder().id(1183).rate(5).build());
        add(Card.builder().id(1184).rate(5).build());
        add(Card.builder().id(1185).rate(5).build());
        add(Card.builder().id(1185).rate(5).build());
        add(Card.builder().id(1187).rate(5).build());
        add(Card.builder().id(1188).rate(5).build());
        add(Card.builder().id(1111).rate(5).build());
        add(Card.builder().id(1112).rate(5).build());
        add(Card.builder().id(1113).rate(5).build());
        add(Card.builder().id(1290).rate(5).build());
        add(Card.builder().id(1291).rate(5).build());
        add(Card.builder().id(1292).rate(5).build());
        add(Card.builder().id(1114).rate(1).build());
        add(Card.builder().id(ItemName.MAT_NA_SUPER_BROLY).rate(2).expire(EXPIRE_3_DAY).build());
        add(Card.builder().id(ItemName.MAT_NA_SUPER_BROLY).rate(1).expire(EXPIRE_7_DAY).build());
        add(Card.builder().id(ItemName.MAT_NA_SUPER_BROLY).rate(0.5).expire(EXPIRE_20_DAY).build());
        add(Card.builder().id(ItemName.MAT_NA_ONNA_BUGEISHA).rate(2).expire(EXPIRE_3_DAY).build());
        add(Card.builder().id(ItemName.MAT_NA_ONNA_BUGEISHA).rate(1).expire(EXPIRE_7_DAY).build());
        add(Card.builder().id(ItemName.MAT_NA_ONNA_BUGEISHA).rate(0.5).expire(EXPIRE_20_DAY).build());
        add(Card.builder().id(ItemName.THE_BAI_KINH_NGHIEM_GIA_TOC_SO).rate(1).build());
        add(Card.builder().id(ItemName.THE_BAI_KINH_NGHIEM_GIA_TOC_TRUNG).rate(1).build());
        add(Card.builder().id(ItemName.THE_BAI_KINH_NGHIEM_GIA_TOC_CAO).rate(1).build());
        add(Card.builder().id(ItemName.BI_KIP_KIEM_THUAT).rate(1).build());
        add(Card.builder().id(ItemName.BI_KIP_TIEU_THUAT).rate(1).build());
        add(Card.builder().id(ItemName.BI_KIP_KUNAI).rate(1).build());
        add(Card.builder().id(ItemName.BI_KIP_CUNG).rate(1).build());
        add(Card.builder().id(ItemName.BI_KIP_DAO).rate(1).build());
        add(Card.builder().id(ItemName.BI_KIP_QUAT).rate(1).build());
        add(Card.builder().id(ItemName.rbt).rate(0.5).build());
        add(Card.builder().id(ItemName.LONG_LUC_DAN).rate(0.5).build());
        add(Card.builder().id(ItemName.MINH_MAN_DAN).rate(10).build());
        add(Card.builder().id(ItemName.KHANG_THE_DAN).rate(1).build());
        add(Card.builder().id(ItemName.SINH_MENH_DAN).rate(1).build());
        add(Card.builder().id(ItemName.HOA_TUYET).rate(2).build());
        add(Card.builder().id(ItemName.PHA_LE).rate(2).build());
        add(Card.builder().id(ItemName.NHAM_THACH_).rate(2).build());
        add(Card.builder().id(ItemName.YEN).rate(10).quantity(2000000).build());
        add(Card.builder().id(ItemName.YEN).rate(20).quantity(500000).build());
        add(Card.builder().id(ItemName.YEN).rate(15).quantity(1000000).build());
        add(Card.builder().id(1280).rate(5).expire(EXPIRE_3_DAY).build());
        add(Card.builder().id(828).rate(5).expire(EXPIRE_3_DAY).build());
        add(Card.builder().id(1280).rate(2).expire(EXPIRE_7_DAY).build());
        add(Card.builder().id(828).rate(2).expire(EXPIRE_7_DAY).build());
        add(Card.builder().id(1280).rate(0.01).build());
        add(Card.builder().id(828).rate(0.01).build());
        add(Card.builder().id(797).rate(5).expire(EXPIRE_3_DAY).build());
        add(Card.builder().id(1207).rate(5).expire(EXPIRE_3_DAY).build());
        add(Card.builder().id(797).rate(2).expire(EXPIRE_7_DAY).build());
        add(Card.builder().id(1207).rate(2).expire(EXPIRE_7_DAY).build());
        add(Card.builder().id(797).rate(0.01).build());
        add(Card.builder().id(1207).rate(0.01).build());
        add(Card.builder().id(ItemName.DA_DANH_VONG_CAP_3).rate(3).build());
        add(Card.builder().id(ItemName.DA_DANH_VONG_CAP_4).rate(2).build());
        add(Card.builder().id(ItemName.DA_DANH_VONG_CAP_5).rate(1).build());
        add(Card.builder().id(ItemName.DA_DANH_VONG_CAP_6).rate(0.5).build());
        add(Card.builder().id(ItemName.DA_DANH_VONG_CAP_7).rate(0.1).build());
        add(Card.builder().id(ItemName.VIEN_LINH_HON_CAP_3).rate(5).build());
        add(Card.builder().id(ItemName.VIEN_LINH_HON_CAP_4).rate(4).build());
        add(Card.builder().id(ItemName.VIEN_LINH_HON_CAP_5).rate(3).build());
        add(Card.builder().id(ItemName.MANH_NON_JIRAI_).rate(5).build());
        add(Card.builder().id(ItemName.MANH_AO_JIRAI_).rate(5).build());
        add(Card.builder().id(ItemName.MANH_QUAN_JIRAI_).rate(5).build());
        add(Card.builder().id(ItemName.MANH_GANG_TAY_JIRAI_).rate(5).build());
        add(Card.builder().id(ItemName.MANH_GIAY_JIRAI_).rate(5).build());
        add(Card.builder().id(ItemName.MANH_PHU_JIRAI_).rate(5).build());
        add(Card.builder().id(ItemName.MANH_DAY_CHUYEN_JIRAI_).rate(5).build());
        add(Card.builder().id(ItemName.MANH_NGOC_BOI_JIRAI_).rate(5).build());
        add(Card.builder().id(ItemName.MANH_NHAN_JIRAI_).rate(5).build());
        add(Card.builder().id(ItemName.MANH_NON_JUMITO).rate(5).build());
        add(Card.builder().id(ItemName.MANH_AO_JUMITO).rate(5).build());
        add(Card.builder().id(ItemName.MANH_QUAN_JUMITO).rate(5).build());
        add(Card.builder().id(ItemName.MANH_GANG_TAY_JUMITO).rate(5).build());
        add(Card.builder().id(ItemName.MANH_GIAY_JUMITO).rate(5).build());
        add(Card.builder().id(ItemName.MANH_PHU_JUMITO).rate(5).build());
        add(Card.builder().id(ItemName.MANH_DAY_CHUYEN_JUMITO).rate(5).build());
        add(Card.builder().id(ItemName.MANH_NGOC_BOI_JUMITO).rate(5).build());
        add(Card.builder().id(ItemName.MANH_NHAN_JUMITO).rate(5).build());
        add(Card.builder().id(ItemName.RUONG_HUYEN_BI).rate(0.01).build());
        add(Card.builder().id(ItemName.RUONG_BACH_NGAN).rate(0.05).build());
        add(Card.builder().id(ItemName.BAT_BAO).rate(0.1).build());
        add(Card.builder().id(ItemName.HUYET_SAC_HUNG_LANG).rate(10).expire(EXPIRE_3_DAY).build());
        add(Card.builder().id(ItemName.HUYET_SAC_HUNG_LANG).rate(2).expire(EXPIRE_7_DAY).build());
        add(Card.builder().id(ItemName.HUYET_SAC_HUNG_LANG).rate(1).expire(EXPIRE_20_DAY).build());
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
            p.themItemToBag(item);
        }
        return card;
    }

    @Override
    protected boolean isCanSelect(Char p) {
        int index = p.getIndexItemByIdInBag(1294);
        if (index == -1 || p.bag[index] == null || !p.bag[index].has()) {
            p.serverDialog("Bạn không có phiếu may mắn VIP!");
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
        int index = p.getIndexItemByIdInBag(1294);
        p.removeItem(index, 1, true);
        if (p.taskId == TaskName.NV_THU_TAI_MAY_MAN) {
            if (p.taskMain != null && p.taskMain.index == 3) {
                p.updateTaskCount(1);
            }
        }
    }

}

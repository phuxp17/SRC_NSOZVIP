
package com.tea.store;

import com.tea.clan.Clan;
import com.tea.constants.ItemName;
import com.tea.convert.Converter;
import com.tea.item.Item;
import com.tea.item.ItemTemplate;
import com.tea.model.Char;
import com.tea.model.ThanThu;
import com.tea.option.ItemOption;
import java.util.ArrayList;

public class ClanStore extends Store {

    public ClanStore(int type, String name) {
        super(type, name);
    }

    @Override
    public void buy(Char p, int indexUI, int quantity) {
        ItemStore item = get(indexUI);
        if (item == null) {
            return;
        }
        ItemTemplate template = item.getTemplate();
        Clan clan = p.clan;
        if (clan == null) {
            p.serverDialog("Bạn không trong gia tộc.");
            return;
        }
        int typeClan = clan.getMemberByName(p.name).getType();
        if (typeClan != Clan.TYPE_TOCTRUONG && typeClan != Clan.TYPE_TOCPHO) {
            p.serverDialog("Bạn không phải tộc trưởng.");
            return;
        }
        if (template.type == 13) {
            int t = template.id - 422;
            if (clan.getItemLevel() < t) {
                p.serverDialog("Vật phẩm này gia tộc bạn chưa mở khoá.");
                return;
            }
        }
        if (template.id == ItemName.TRUNG_DI_LONG || template.id == ItemName.TRUNG_HAI_MA) {
            quantity = 1;
        }
        long giaXu = ((long) item.getCoin()) * ((long) quantity);
        if (giaXu < 0) {
            return;
        }
        if (giaXu > clan.getCoin()) {
            p.serverDialog("Không đủ xu gia tộc.");
            return;
        }

        if (template.id == ItemName.TRUNG_DI_LONG || template.id == ItemName.TRUNG_HAI_MA) {
            byte type = template.id == ItemName.TRUNG_DI_LONG ? ThanThu.DI_LONG : ThanThu.HAI_MA;
            ThanThu thanThu = clan.getThanThu(type);
            if (thanThu != null) {
                p.serverDialog("Gia tộc bạn đã sở hữu thần thú này rồi.");
                return;
            }
            thanThu = new ThanThu();
            thanThu.setType(type);
            thanThu.setCurrentExp(0);
            thanThu.setEggHatchingTime(7 * 24 * 60 * 60 * 1000);
            thanThu.setLevel(1);
            thanThu.setStars((byte) 1);
            ArrayList<ItemOption> options = new ArrayList<>();
            thanThu.setOptions(options);
            clan.thanThus.add(thanThu);
        } else {
            Item newItem = Converter.getInstance().toItem(item, Converter.MAX_OPTION);
            newItem.expire = item.getExpire();
            newItem.setQuantity(quantity);
            clan.themItem(newItem);
        }
        clan.addXu(-((int) giaXu));
        clan.getClanService().requestClanItem();
        p.serverDialog("Vật phẩm đã được thêm vào kho.");
    }

}

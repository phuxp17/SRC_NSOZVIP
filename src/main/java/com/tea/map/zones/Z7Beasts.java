/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tea.map.zones;

import com.tea.constants.ItemName;
import com.tea.map.Map;
import com.tea.constants.MapName;
import com.tea.constants.MobName;
import com.tea.item.Item;
import com.tea.item.ItemFactory;
import com.tea.map.TileMap;
import com.tea.map.Waypoint;
import com.tea.map.world.SevenBeasts;
import com.tea.map.world.World;
import com.tea.mob.Mob;
import com.tea.model.Char;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Admin
 */
public abstract class Z7Beasts extends ZWorld {

    @Getter
    protected int level;

    public Z7Beasts(int id, TileMap tilemap, Map map) {
        super(id, tilemap, map);
    }

    @Override
    public void returnTownFromDead(@NotNull Char p) {
        p.outZone();
        Zone z = world.find(MapName.KHU_VUC_CHO);
        p.setXY((short) 35, (short) 360);
        z.join(p);
        if (map.id == MapName.THAT_THU_AI) {
            String name_new = p.getTongNap(p) + p.name; // SVIP
            world.getService().serverMessage(String.format("%s đã rời ải, xin mời thành viên tiếp theo", name_new));
        }
    }

    @Override
    public void requestChangeMap(@NotNull Char p) {
        if (world.getCountDown() > 3600) {
            p.returnToPreviousPostion(() -> {
                p.serverDialog("Cửa ải chưa được mở.");
            });
            return;
        }
        Waypoint wp = tilemap.findWaypoint(p.x, p.y);
        if (wp == null) {
            return;
        }
        int nextID = wp.next;
        Z7Beasts z = (Z7Beasts) world.find(nextID);
        if (nextID == MapName.KHU_VUC_CHO) {
            p.returnToPreviousPostion(() -> {
                p.serverDialog("Lối ra đã bị chặn. Bạn chỉ còn cách tiêu diệt hết số quái trong ải");
            });
            return;
        } else if (nextID == MapName.THAT_THU_AI) {
            BeastArea area = (BeastArea) z;
            if ((area.getNumberChar() > 0 || area.getPreviousPlayerDied() == p.id) && area.getLevel() < 6) {
                p.returnToPreviousPostion(() -> {
                    p.serverDialog("Đã có người vào ải, hoặc chưa tới lượt đánh của bạn. Vui lòng chờ ở bên ngoài.");
                });
                return;
            }
            refresh();
        }
        p.outZone();
        p.setXY(wp.x, wp.y);
        z.join(p);
    }

    @Override
    public void mobDead(Mob mob, Char killer) {
        if (killer != null) {
            if ((mob.template.id == MobName.MOC_NHAN || mob.template.id == MobName.MUC_ONG_DO)) {
                Item item = ItemFactory.getInstance().newItem(ItemName.THAT_THU_THU_BAO);
                item.setQuantity(1);
                item.isLock = true;
                if (killer.getSlotNull() > 0) {
                    killer.themItemToBag(item);
                } else {
                    killer.warningBagFull();
                }
                SevenBeasts sevenBeasts = (SevenBeasts) killer.findWorld(World.SEVEN_BEASTS);
                String name_new = killer.getTongNap(killer) + killer.name; // SVIP
                sevenBeasts.getService().serverMessage(String.format("%s nhận được %s rơi ra từ %s", name_new, item.template.name, mob.template.name));
                if (mob.template.id == MobName.MUC_ONG_DO) {
                    sevenBeasts.setCountdown(15);
                    sevenBeasts.getService().serverMessage("Xin chúc mừng nhóm của bạn đã vượt qua được thất thú ải.");
                }
            }
        }
    }

    public abstract void refresh();
}

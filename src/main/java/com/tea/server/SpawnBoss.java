
package com.tea.server;

import com.tea.lib.RandomCollection;
import com.tea.map.Map;
import com.tea.map.zones.Zone;
import com.tea.mob.Mob;
import com.tea.mob.MobManager;
import com.tea.mob.MobTemplate;
import com.tea.util.Log;
import com.tea.util.NinjaUtils;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SpawnBoss {

    private int id;
    private Map map;
    private final RandomCollection<Integer> mobs = new RandomCollection<>();
    private Mob currMonster;
    private short x, y;

    public SpawnBoss(int id, Map map, short x, short y) {
        this.id = id;
        this.map = map;
        this.x = x;
        this.y = y;
    }

    public void add(int rate, int mobID) {
        mobs.add(rate, mobID);
    }

    public void spawn() {
        if (currMonster != null) {
            currMonster.die();
            currMonster = null;
        }
        int zoneId = NinjaUtils.nextInt(map.getZones().size());
        Zone z = map.getZoneById(zoneId);
        int mobID = mobs.next();
        MobTemplate mobTemplate = MobManager.getInstance().find(mobID);
        Mob mob = z.getMobFactory().createBoss((short) mobTemplate.getId(), mobTemplate.getHp(), mobTemplate.getLevel(), x, y);
        z.addMob(mob);
        currMonster = mob;
        String text = mob.template.name + " đã xuất hiện ở " + z.tilemap.name;
        GlobalService.getInstance().chat("Hệ thống", text);
        Log.debug(text + " khu " + z.id);
        Log.info(text+ " khu "+ z.id);
    }
}

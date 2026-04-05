/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tea.map.zones;

import com.tea.map.Map;
import com.tea.map.Waypoint;
import com.tea.map.world.World;
import com.tea.model.Char;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author kitakeyos - Hoàng Hữu Dũng
 */
public class WarCandy extends ZWorld {

    public WarCandy(Map map, World world) {
        super(0, map.tilemap, map);
        setWorld(world);
    }

    @Override
    public void requestChangeMap(@NotNull Char p) {
        Waypoint wp = tilemap.findWaypoint(p.x, p.y);
        if (wp == null) {
            return;
        }
        Zone z = world.find(wp.next);
    }

}

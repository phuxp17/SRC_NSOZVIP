/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tea.map.zones;

import com.tea.map.Map;
import com.tea.map.TileMap;
import com.tea.model.Char;

/**
 *
 * @author Admin
 */
public class AreaWithCountdownTime extends Zone {

    protected int countDown;

    public AreaWithCountdownTime(int id, TileMap tilemap, Map map) {
        super(id, tilemap, map);
    }

    @Override
    public void join(Char p) {
        super.join(p);
        p.getService().sendTimeInMap(countDown);
    }

    public void setTimeMap(int t) {
        countDown = t;
        getService().sendTimeInMap(countDown);
    }

    @Override
    public void update() {
        super.update();
        if (countDown > 0) {
            countDown--;
            if (countDown == 0) {
                close();
            }
        }
    }

}

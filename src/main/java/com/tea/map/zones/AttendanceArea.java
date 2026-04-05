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
public class AttendanceArea extends ZWorld {

    public AttendanceArea(int id, TileMap tilemap, Map map) {
        super(id, tilemap, map);
    }

    public void join(Char p) {
        super.join(p);
        p.hp = p.maxHP;
        p.isDead = false;
        p.getService().loadInfo();
        p.setTypePk(Char.PK_NORMAL);
    }

    public void out(Char p) {
        super.out(p);
        p.hp = p.maxHP;
        p.isDead = false;
        p.getService().loadInfo();
        p.setTypePk(Char.PK_NORMAL);
    }

}

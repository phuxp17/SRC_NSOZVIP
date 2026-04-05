/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tea.admin;

import com.tea.clan.Clan;
import com.tea.constants.CMDInputDialog;
import com.tea.constants.CMDMenu;
import com.tea.constants.ItemName;
import com.tea.convert.Converter;
import com.tea.db.jdbc.DbManager;
import com.tea.event.Event;
import com.tea.event.LunarNewYear;
import com.tea.item.Item;
import com.tea.item.ItemFactory;
import com.tea.map.Map;
import com.tea.map.MapManager;
import com.tea.mob.Mob;
import com.tea.mob.MobManager;
import com.tea.mob.MobTemplate;
import com.tea.model.Char;
import com.tea.model.InputDialog;
import com.tea.model.Menu;
import com.tea.model.WarMember;
import com.tea.network.Message;
import com.tea.network.Session;
import com.tea.option.ItemOption;
import com.tea.server.Config;
import com.tea.server.GameData;
import static com.tea.server.JFrameSendItem.checkNumber;
import com.tea.server.NinjaSchool;
import com.tea.server.Ranked;
import com.tea.server.Server;
import com.tea.server.ServerManager;
import com.tea.skill.Skill;
import com.tea.stall.StallManager;
import com.tea.store.ItemStore;
import com.tea.store.StoreManager;
import com.tea.util.Log;
import com.tea.util.NinjaUtils;
import java.io.IOException;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author kitakeyos - Hoàng Hữu Dũng
 */
public class AdminService {

    private static final AdminService instance = new AdminService();

    public static AdminService getInstance() {
        return instance;
    }
   

    public boolean process(Char p, String text) {
        return false;
    }
}

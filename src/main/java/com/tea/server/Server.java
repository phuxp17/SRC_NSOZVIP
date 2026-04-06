package com.tea.server;

import com.tea.constants.ConstTime;
import com.tea.constants.ItemName;
import com.tea.item.Item;
import com.tea.item.ItemFactory;
import com.tea.item.ItemTemplate;
import com.tea.map.MapManager;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tea.item.ItemManager;
import com.tea.event.Event;
import com.tea.event.Ranking;
import com.tea.db.mongodb.MongoDbConnection;
import com.tea.model.ArrowPaint;
import com.google.gson.Gson;
import com.tea.clan.Clan;
import com.tea.effect.Effect;
import com.tea.effect.EffectCharPaint;
import com.tea.effect.EffectInfoPaint;
import com.tea.model.Part;
import com.tea.model.PartImage;
import com.tea.model.RandomItem;
import com.tea.option.ItemOption;
import com.tea.skill.Skill;
import com.tea.skill.SkillInfoPaint;
import com.tea.skill.SkillPaint;
import com.tea.store.ItemStore;
import com.tea.task.Task;
import com.tea.map.TileMap;
import com.tea.model.User;
import com.tea.map.War;
import com.tea.network.Session;
import com.tea.option.SkillOption;
import com.tea.socket.SocketIO;
import com.tea.stall.StallManager;
import com.tea.effect.EffectTemplate;
import com.tea.lib.ImageMap;
import com.tea.constants.MapName;
import com.tea.constants.SQLStatement;
import com.tea.db.jdbc.DbManager;
import com.tea.effect.EffectAutoDataManager;
import com.tea.effect.EffectDataManager;
import com.tea.effect.EffectTemplateManager;
import com.tea.mob.MobTemplate;
import com.tea.npc.NpcManager;
import com.tea.npc.NpcTemplate;
import com.tea.skill.SkillOptionTemplate;
import com.tea.skill.SkillTemplate;
import com.tea.task.TaskTemplate;
import com.tea.thiendia.ThienDiaManager;
import com.tea.util.Log;
import com.tea.util.SystemCommand;
import com.tea.lib.ParseData;
import com.tea.lib.ProfanityFilter;
import com.tea.map.world.WorldManager;
import com.tea.mob.MobManager;
import com.tea.model.Char;
import com.tea.model.Clazz;
import com.tea.model.MountData;
import com.tea.model.MountDataManager;
import com.tea.store.StoreManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author ASD
 */
public class Server {

    public static ServerSocket server;
    public static boolean start;
    public static int id;
    public static ArrayList<SkillPaint> sks;
    public static ArrayList<Part> parts;
    public static ArrayList<EffectCharPaint> efs;
    public static ArrayList<ArrowPaint> arrs;
    public static ArrayList<String> Z_VIP;
    public static ArrayList<String> A_VIP;
    public static ArrayList<String> V_VIP;
    public static ArrayList<String> S_VIP;
    public static ArrayList<String> rewardVIP = new ArrayList<>();
    public static ArrayList<String> listddhn = new ArrayList<>();
    public static ArrayList<int[]> smallImg;
    public static long[] exps;
    public static byte[][] npcTasks;
    public static byte[][] mapTasks;
    public static JSONArray head_boc_dau, body_jump, body_normal, head_normal, body_boc_dau, head_jump, leg;
    public static final Lock lock = new ReentrantLock();
    public static byte[] version, map, data, skill;
    public static byte[] nj_arrow, nj_effect, nj_image, nj_part, nj_skill;
    public static long EXP_MAX = 0;
    public static final ImageMap[][] IMAGE_MAP_ARR = new ImageMap[3][4];
    public static boolean isStop;
    private static final AtomicBoolean SHUTDOWN_IN_PROGRESS = new AtomicBoolean(false);
    private static final AtomicBoolean SHUTDOWN_HOOK_REGISTERED = new AtomicBoolean(false);
    private static final String COMMAND_STATE_FILE = "command_state.txt";

    public static void initImageMap() {
        for (int i = 0; i < 4; i++) {
            IMAGE_MAP_ARR[0][i] = ImageMap.builder()
                    .mapID(MapName.RUNG_KAPPA)
                    .zoomLevel((byte) (i + 1))
                    .x(1746)
                    .y(408)
                    .w(80)
                    .h(50)
                    .build();
        }
        for (int i = 0; i < 4; i++) {
            IMAGE_MAP_ARR[1][i] = ImageMap.builder()
                    .mapID(MapName.KHE_NUI_CHOROCHORO)
                    .zoomLevel((byte) (i + 1))
                    .x(880)
                    .y(320)
                    .w(80)
                    .h(50)
                    .build();
        }
        for (int i = 0; i < 4; i++) {
            IMAGE_MAP_ARR[2][i] = ImageMap.builder()
                    .mapID(MapName.DAO_HEBI)
                    .zoomLevel((byte) (i + 1))
                    .x(180)
                    .y(260)
                    .w(80)
                    .h(40)
                    .build();
        }
    }

    public static boolean init() {
        start = false;
        MongoDbConnection.connect();
        Task.arrTaskTemplate = new ArrayList<>();
        sks = new ArrayList<>();
        parts = new ArrayList<>();
        efs = new ArrayList<>();
        arrs = new ArrayList<>();
        parts = new ArrayList<>();
        smallImg = new ArrayList<>();
        head_boc_dau = new JSONArray();
        head_jump = new JSONArray();
        body_normal = new JSONArray();
        head_normal = new JSONArray();
        body_jump = new JSONArray();
        body_boc_dau = new JSONArray();
        leg = new JSONArray();
        try {
            if (!NpcManager.getInstance().load()) {
                return false;
            }
            if (!MobManager.getInstance().load()) {
                return false;
            }
            if (!MapManager.getInstance().load()) {
                return false;
            }
            Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `task`;",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

            ResultSet resultSet = stmt.executeQuery();
            resultSet.last();
            int num = resultSet.getRow();
            resultSet.beforeFirst();
            npcTasks = new byte[num][];
            mapTasks = new byte[num][];
            int i = 0;
            while (resultSet.next()) {
                JSONArray jArr = (JSONArray) JSONValue.parse(resultSet.getString("npcs"));
                JSONArray jArr2 = (JSONArray) JSONValue.parse(resultSet.getString("maps"));
                npcTasks[i] = new byte[jArr.size()];
                mapTasks[i] = new byte[jArr.size()];
                for (int a = 0; a < npcTasks[i].length; a++) {
                    npcTasks[i][a] = ((Long) jArr.get(a)).byteValue();
                    mapTasks[i][a] = ((Long) jArr2.get(a)).byteValue();
                }
                i++;
            }
            resultSet.close();
            stmt.close();
            stmt = conn.prepareStatement(SQLStatement.GET_ALL_TASK_TEMPLATE);
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                short taskId = resultSet.getShort("taskId");
                String name = resultSet.getString("name");
                String detail = resultSet.getString("detail");
                JSONArray jArr = (JSONArray) JSONValue.parse(resultSet.getString("subnames"));
                String[] subnames = new String[jArr.size()];
                for (int a = 0; a < subnames.length; a++) {
                    subnames[a] = jArr.get(a).toString();
                }
                JSONArray jArr2 = (JSONArray) JSONValue.parse(resultSet.getString("counts"));
                short[] counts = new short[jArr2.size()];
                for (int a = 0; a < counts.length; a++) {
                    counts[a] = Short.parseShort(jArr2.get(a).toString());
                }
                short levelRequire = resultSet.getShort("level_require");
                JSONArray jsonArr = (JSONArray) JSONValue.parse(resultSet.getString("kill_mob"));
                short[][] mobs = new short[jsonArr.size()][2];
                for (int j = 0; j < mobs.length; j++) {
                    JSONArray jMob = (JSONArray) jsonArr.get(j);
                    mobs[j][0] = Short.parseShort(jMob.get(0).toString());
                    mobs[j][1] = Short.parseShort(jMob.get(1).toString());
                }
                jsonArr = (JSONArray) JSONValue.parse(resultSet.getString("pick_item"));
                short[] items = new short[jsonArr.size()];
                for (int j = 0; j < items.length; j++) {
                    items[j] = Short.parseShort(jsonArr.get(j).toString());
                }
                TaskTemplate task = TaskTemplate.builder()
                        .taskId(taskId)
                        .name(name)
                        .detail(detail)
                        .subNames(subnames)
                        .counts(counts)
                        .leveRequire(levelRequire)
                        .mobs(mobs)
                        .items(items)
                        .build();
                Task.arrTaskTemplate.add(task);
            }
            resultSet.close();
            stmt.close();
            stmt = conn.prepareStatement("SELECT * FROM `others`;");
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                if (name.equals("head_boc_dau")) {
                    head_boc_dau = (JSONArray) JSONValue.parse(resultSet.getString("value"));
                }
                if (name.equals("head_normal")) {
                    head_normal = (JSONArray) JSONValue.parse(resultSet.getString("value"));
                }
                if (name.equals("head_jump")) {
                    head_jump = (JSONArray) JSONValue.parse(resultSet.getString("value"));
                }
                if (name.equals("body_jump")) {
                    body_jump = (JSONArray) JSONValue.parse(resultSet.getString("value"));
                }
                if (name.equals("body_boc_dau")) {
                    body_boc_dau = (JSONArray) JSONValue.parse(resultSet.getString("value"));
                }
                if (name.equals("body_normal")) {
                    body_normal = (JSONArray) JSONValue.parse(resultSet.getString("value"));
                }
                if (name.equals("leg")) {
                    leg = (JSONArray) JSONValue.parse(resultSet.getString("value"));
                }
                if (name.equals("exp")) {
                    JSONArray value = (JSONArray) JSONValue.parse(resultSet.getString("value"));
                    if (name.equals("exp")) {
                        exps = new long[value.size()];
                        for (i = 0; i < exps.length; i++) {
                            exps[i] = ((Long) value.get(i)).longValue();
                        }
                    }
                }
            }
            resultSet.close();
            stmt.close();
            MountDataManager.getInstance().init();
            EffectTemplateManager.getInstance().init();
            EffectDataManager.getInstance().load();
            ItemManager.getInstance().load();
            GameData.getInstance().init();
            StoreManager.getInstance().init();
            if (!StoreManager.getInstance().load()) {
                return false;
            }
            stmt = conn.prepareStatement("SELECT * FROM `nj_skill`;");
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                SkillPaint p = new SkillPaint();
                p.id = resultSet.getShort("skillId");
                p.effId = resultSet.getShort("effId");
                p.numEff = resultSet.getByte("numEff");
                JSONArray jA = (JSONArray) JSONValue.parse(resultSet.getString("skillStand"));
                p.skillStand = new SkillInfoPaint[jA.size()];
                for (int k = 0; k < p.skillStand.length; k++) {
                    JSONObject o = (JSONObject) jA.get(k);
                    p.skillStand[k] = new SkillInfoPaint();
                    p.skillStand[k].status = ((Long) o.get("status")).byteValue();
                    p.skillStand[k].effS0Id = ((Long) o.get("effS0Id")).shortValue();
                    p.skillStand[k].e0dx = ((Long) o.get("e0dx")).shortValue();
                    p.skillStand[k].e0dy = ((Long) o.get("e0dy")).shortValue();
                    p.skillStand[k].effS1Id = ((Long) o.get("effS1Id")).shortValue();
                    p.skillStand[k].e1dx = ((Long) o.get("e1dx")).shortValue();
                    p.skillStand[k].e1dy = ((Long) o.get("e1dy")).shortValue();
                    p.skillStand[k].effS2Id = ((Long) o.get("effS2Id")).shortValue();
                    p.skillStand[k].e2dx = ((Long) o.get("e2dx")).shortValue();
                    p.skillStand[k].e2dy = ((Long) o.get("e2dy")).shortValue();
                    p.skillStand[k].arrowId = ((Long) o.get("arrowId")).shortValue();
                    p.skillStand[k].adx = ((Long) o.get("adx")).shortValue();
                    p.skillStand[k].ady = ((Long) o.get("ady")).shortValue();
                }
                jA = (JSONArray) JSONValue.parse(resultSet.getString("skillFly"));
                p.skillfly = new SkillInfoPaint[jA.size()];
                for (int k = 0; k < p.skillfly.length; k++) {
                    JSONObject o = (JSONObject) jA.get(k);
                    p.skillfly[k] = new SkillInfoPaint();
                    p.skillfly[k].status = ((Long) o.get("status")).byteValue();
                    p.skillfly[k].effS0Id = ((Long) o.get("effS0Id")).shortValue();
                    p.skillfly[k].e0dx = ((Long) o.get("e0dx")).shortValue();
                    p.skillfly[k].e0dy = ((Long) o.get("e0dy")).shortValue();
                    p.skillfly[k].effS1Id = ((Long) o.get("effS1Id")).shortValue();
                    p.skillfly[k].e1dx = ((Long) o.get("e1dx")).shortValue();
                    p.skillfly[k].e1dy = ((Long) o.get("e1dy")).shortValue();
                    p.skillfly[k].effS2Id = ((Long) o.get("effS2Id")).shortValue();
                    p.skillfly[k].e2dx = ((Long) o.get("e2dx")).shortValue();
                    p.skillfly[k].e2dy = ((Long) o.get("e2dy")).shortValue();
                    p.skillfly[k].arrowId = ((Long) o.get("arrowId")).shortValue();
                    p.skillfly[k].adx = ((Long) o.get("adx")).shortValue();
                    p.skillfly[k].ady = ((Long) o.get("ady")).shortValue();
                }
                sks.add(p);
            }
            resultSet.close();
            stmt.close();
            stmt = conn.prepareStatement("SELECT * FROM `nj_part`;");
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                byte type = resultSet.getByte("type");
                JSONArray jA = (JSONArray) JSONValue.parse(resultSet.getString("part"));
                Part part = new Part(type);
                for (int k = 0; k < part.pi.length; k++) {
                    JSONObject o = (JSONObject) jA.get(k);
                    part.pi[k] = new PartImage();
                    part.pi[k].id = ((Long) o.get("id")).shortValue();
                    part.pi[k].dx = ((Long) o.get("dx")).byteValue();
                    part.pi[k].dy = ((Long) o.get("dy")).byteValue();
                }
                parts.add(part);
            }
            resultSet.close();
            stmt.close();
            stmt = conn.prepareStatement("SELECT * FROM `nj_image`;");
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                int[] smallImage = new int[5];
                ParseData p = new ParseData(
                        (JSONObject) JSONValue.parse(resultSet.getString("smallImage")));
                smallImage[0] = p.getInt("id");
                smallImage[1] = p.getShort("x");
                smallImage[2] = p.getShort("y");
                smallImage[3] = p.getShort("w");
                smallImage[4] = p.getShort("h");
                smallImg.add(smallImage);
            }
            resultSet.close();
            stmt.close();
            stmt = conn.prepareStatement("SELECT * FROM `nj_arrow`;");
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                ArrowPaint p = new ArrowPaint();
                p.id = resultSet.getShort("id");
                JSONArray jA = (JSONArray) JSONValue.parse(resultSet.getString("imgId"));
                p.imgId[0] = ((Long) jA.get(0)).shortValue();
                p.imgId[1] = ((Long) jA.get(1)).shortValue();
                p.imgId[2] = ((Long) jA.get(2)).shortValue();
                arrs.add(p);
            }
            resultSet.close();
            stmt.close();
            stmt = conn.prepareStatement("SELECT * FROM `nj_effect`;");
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                EffectCharPaint effectCharInfo = new EffectCharPaint();
                effectCharInfo.idEf = resultSet.getShort("id");
                JSONArray jA = (JSONArray) JSONValue.parse(resultSet.getString("info"));
                effectCharInfo.arrEfInfo = new EffectInfoPaint[jA.size()];
                for (int k = 0; k < effectCharInfo.arrEfInfo.length; k++) {
                    JSONObject o = (JSONObject) jA.get(k);
                    effectCharInfo.arrEfInfo[k] = new EffectInfoPaint();
                    effectCharInfo.arrEfInfo[k].idImg = ((Long) o.get("id")).shortValue();
                    effectCharInfo.arrEfInfo[k].dx = ((Long) o.get("dx")).byteValue();
                    effectCharInfo.arrEfInfo[k].dy = ((Long) o.get("dy")).byteValue();
                }
                efs.add(effectCharInfo);
            }
            resultSet.close();
            stmt.close();
            for (long exp : exps) {
                EXP_MAX += exp;
            }
            EXP_MAX -= 1;
            EffectAutoDataManager.getInstance().load();
            ItemManager.getInstance().init();
            setDataArrow();
            setDataEffect();
            setDataImage();
            setDataPart();
            setDataSkill();
            setData();
            setMap();
            ItemManager.getInstance().setData();
            setSkill();
            setVersion();
            Event.init();
            Event event = Event.getEvent();
            if (event != null) {
                event.loadEventPoint();
                event.initStore();
            }
            if (Config.getInstance().isTestVersion()) {
                storeTestInit();
            }
            initThucAnXu();
            EffectDataManager.getInstance().setData();
            MapManager.getInstance().init();
            Ranking.loadListLeaderBoard();
            Clan.getClanDAO().load();
            Ranked.init();
            ThienDiaManager.getInstance().init();
            RandomItem.init();
            initImageMap();
            readFile();
            readFile1();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public static void loadListVip() {
        try {
            Z_VIP = new ArrayList<>();
            A_VIP = new ArrayList<>();
            V_VIP = new ArrayList<>();
            S_VIP = new ArrayList<>();
            Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
            PreparedStatement stmt;
            stmt = conn.prepareStatement("SELECT * FROM `players` WHERE `tongnap` >= 1000000;");
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int tongnap = resultSet.getInt("tongnap");
                if (tongnap >= 100000 && tongnap < 200000) {
                    Z_VIP.add(name);
                } else if (tongnap >= 200000 && tongnap < 500000) {
                    A_VIP.add(name);
                } else if (tongnap >= 500000 && tongnap < 1000000) {
                    V_VIP.add(name);
                } else if (tongnap >= 1000000) {
                    S_VIP.add(name);
                }
            }
            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void setVersion() {
        try {
            ByteArrayOutputStream bas = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bas);
            int ver = Config.getInstance().getDataVersion();
            if (Config.getInstance().getMaxPercentAdd() > 0) {
                ver += 1;
            }
            dos.writeByte(ver);
            dos.writeByte(Config.getInstance().getMapVersion());
            dos.writeByte(Config.getInstance().getSkillVersion());
            dos.writeByte(Config.getInstance().getItemVersion());
            int num = head_jump.size();
            dos.writeByte(num);
            for (int i = 0; i < num; i++) {
                ParseData p = new ParseData((JSONObject) head_jump.get(i));
                JSONArray item = p.getJSONArray("item");
                int lent = item.size();
                dos.writeByte(lent * 3 + 2);
                dos.writeShort(p.getShort("id"));
                dos.writeShort(p.getShort("small"));
                for (int a = 0; a < lent; a++) {
                    ParseData p2 = new ParseData((JSONObject) item.get(a));
                    dos.writeShort(p2.getShort("id"));
                    dos.writeShort(p2.getShort("dx"));
                    dos.writeShort(p2.getShort("dy"));
                }
            }
            for (int i = 0; i < num; i++) {
                ParseData p = new ParseData((JSONObject) head_normal.get(i));
                JSONArray item = p.getJSONArray("item");
                int lent = item.size();
                dos.writeByte(lent * 3 + 2);
                dos.writeShort(p.getShort("id"));
                dos.writeShort(p.getShort("small"));
                for (int a = 0; a < lent; a++) {
                    ParseData p2 = new ParseData((JSONObject) item.get(a));
                    dos.writeShort(p2.getShort("id"));
                    dos.writeShort(p2.getShort("dx"));
                    dos.writeShort(p2.getShort("dy"));
                }
            }
            for (int i = 0; i < num; i++) {
                ParseData p = new ParseData((JSONObject) head_boc_dau.get(i));
                JSONArray item = p.getJSONArray("item");
                int lent = item.size();
                dos.writeByte(lent * 3 + 2);
                dos.writeShort(p.getShort("id"));
                dos.writeShort(p.getShort("small"));
                for (int a = 0; a < lent; a++) {
                    ParseData p2 = new ParseData((JSONObject) item.get(a));
                    dos.writeShort(p2.getShort("id"));
                    dos.writeShort(p2.getShort("dx"));
                    dos.writeShort(p2.getShort("dy"));
                }
            }
            num = leg.size();
            dos.writeByte(num * 2);
            for (int i = 0; i < num; i++) {
                ParseData p = new ParseData((JSONObject) leg.get(i));
                dos.writeShort(p.getShort("id"));
                dos.writeShort(p.getShort("small"));
            }
            num = body_jump.size();
            dos.writeByte(num);
            for (int i = 0; i < num; i++) {
                ParseData p = new ParseData((JSONObject) body_jump.get(i));
                JSONArray item = p.getJSONArray("item");
                int lent = item.size();
                dos.writeByte(lent * 3 + 2);
                dos.writeShort(p.getShort("id"));
                dos.writeShort(p.getShort("small"));
                for (int a = 0; a < lent; a++) {
                    ParseData p2 = new ParseData((JSONObject) item.get(a));
                    dos.writeShort(p2.getShort("id"));
                    dos.writeShort(p2.getShort("dx"));
                    dos.writeShort(p2.getShort("dy"));
                }
            }
            for (int i = 0; i < num; i++) {
                ParseData p = new ParseData((JSONObject) body_normal.get(i));
                JSONArray item = p.getJSONArray("item");
                int lent = item.size();
                dos.writeByte(lent * 3 + 2);
                dos.writeShort(p.getShort("id"));
                dos.writeShort(p.getShort("small"));
                for (int a = 0; a < lent; a++) {
                    ParseData p2 = new ParseData((JSONObject) item.get(a));
                    dos.writeShort(p2.getShort("id"));
                    dos.writeShort(p2.getShort("dx"));
                    dos.writeShort(p2.getShort("dy"));
                }
            }
            for (int i = 0; i < num; i++) {
                ParseData p = new ParseData((JSONObject) body_boc_dau.get(i));
                JSONArray item = p.getJSONArray("item");
                int lent = item.size();
                dos.writeByte(lent * 3 + 2);
                dos.writeShort(p.getShort("id"));
                dos.writeShort(p.getShort("small"));
                for (int a = 0; a < lent; a++) {
                    ParseData p2 = new ParseData((JSONObject) item.get(a));
                    dos.writeShort(p2.getShort("id"));
                    dos.writeShort(p2.getShort("dx"));
                    dos.writeShort(p2.getShort("dy"));
                }
            }
            List<MountData> mountDatas = MountDataManager.getInstance().getMountDatas();
            dos.writeByte(mountDatas.size());
            for (MountData mountData : mountDatas) {
                dos.writeShort(mountData.getItemID());
                short[][] data = mountData.getData();
                for (short[] frames : data) {
                    dos.writeByte(frames.length);
                    for (short frame : frames) {
                        dos.writeShort(frame);
                    }
                }
            }
            dos.flush();
            version = bas.toByteArray();
            dos.close();
            bas.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void initThucAnXu() {
        //      Thuc an xu
//      Hoa thach
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FOOD, ItemStore.builder()
                .id(1015)
                .itemID(ItemName.HOA_THACH)
                .coin(10000)
                .expire(ConstTime.WEEK)
                .build());
//      Phong thach
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FOOD, ItemStore.builder()
                .id(1016)
                .itemID(ItemName.PHONG_THACH)
                .coin(10000)
                .expire(ConstTime.WEEK)
                .build());
//      Thuy thach
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FOOD, ItemStore.builder()
                .id(1017)
                .itemID(ItemName.THUY_THACH)
                .coin(10000)
                .expire(ConstTime.WEEK)
                .build());
    }

    public static void storeTestInit() {
//      Goosho
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1009)
                .itemID(ItemName.DA_CAP_12)
                .coin(1000)
                .expire(ConstTime.WEEK)
                .build());

//      XE - SOI
        ArrayList<ItemOption> optionsEmpty = new ArrayList<ItemOption>();
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1018)
                .itemID(ItemName.XE_MAY)
                .options(optionsEmpty)
                .gold(10000)
                .expire(ConstTime.FOREVER)
                .build());

        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1099)
                .itemID(ItemName.HARLEY_DAVIDSON)
                .options(optionsEmpty)
                .gold(10000)
                .expire(ConstTime.FOREVER)
                .build());

        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1019)
                .itemID(ItemName.XICH_NHAN_NGAN_LANG)
                .options(optionsEmpty)
                .gold(10000)
                .expire(ConstTime.FOREVER)
                .build());

        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1020)
                .itemID(ItemName.BO_DIEU_KHIEN)
                .gold(1000)
                .options(optionsEmpty)
                .expire(ConstTime.FOREVER)
                .build());

        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1021)
                .itemID(ItemName.DONG_CO_V_POWER)
                .gold(1000)
                .options(optionsEmpty)
                .expire(ConstTime.FOREVER)
                .build());

        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1022)
                .itemID(ItemName.DINH_VI)
                .gold(1000)
                .options(optionsEmpty)
                .expire(ConstTime.FOREVER)
                .build());

        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1023)
                .itemID(ItemName.BINH_NITRO)
                .gold(1000)
                .options(optionsEmpty)
                .expire(ConstTime.FOREVER)
                .build());

        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1024)
                .itemID(ItemName.THU_TRANG)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.FOREVER)
                .build());

        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1025)
                .itemID(ItemName.GIAP_THU)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.FOREVER)
                .build());

        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1026)
                .itemID(ItemName.YEN2)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.FOREVER)
                .build());

        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1026)
                .itemID(ItemName.DAY_CUONG)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.FOREVER)
                .build());

//      Ngoc 10
        int[][] huyenTinhNgocOptions = {{106, 0}, {73, 2350}, {114, -1}, {107, 0}, {124, 280}, {114, -1}, {108, 0}, {115, 500}, {119, -1}, {104, 10}, {123, 28100000}};
        int[][] huyetNgocOptions = {{106, 0}, {102, 8000}, {115, -1}, {107, 0}, {126, 50}, {105, -1}, {108, 0}, {114, 50}, {118, -1}, {104, 10}, {123, 28100000}};
        int[][] lamTinhNgocOptions = {{106, 0}, {103, 5000}, {125, -1}, {107, 0}, {121, 50}, {120, -1}, {108, 0}, {116, 500}, {126, -1}, {104, 10}, {123, 28100000}};
        int[][] lucNgocOptions = {{106, 0}, {105, 5200}, {116, -1}, {107, 0}, {125, 1000}, {117, -1}, {108, 0}, {117, 1000}, {124, -1}, {104, 10}, {123, 28100000}};

//      Huyen tinh ngoc
        List<ItemOption> htnOptions = new ArrayList<ItemOption>();
        for (int[] option : huyenTinhNgocOptions) {
            htnOptions.add(new ItemOption(option[0], option[1]));
        }

        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1011)
                .itemID(ItemName.HUYEN_TINH_NGOC)
                .options(htnOptions)
                .gold(1000)
                .isLock(false)
                .expire(ConstTime.WEEK)
                .build());

//      Huyet ngoc
        List<ItemOption> hnOptions = new ArrayList<ItemOption>();
        for (int[] option : huyetNgocOptions) {
            hnOptions.add(new ItemOption(option[0], option[1]));
        }
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1012)
                .itemID(ItemName.HUYET_NGOC)
                .options(hnOptions)
                .gold(1000)
                .isLock(false)
                .expire(ConstTime.WEEK)
                .build());

//      Lam tinh ngoc
        List<ItemOption> lnOptions = new ArrayList<ItemOption>();
        for (int[] option : lamTinhNgocOptions) {
            lnOptions.add(new ItemOption(option[0], option[1]));
        }
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1013)
                .itemID(ItemName.LAM_TINH_NGOC)
                .options(lnOptions)
                .gold(1000)
                .isLock(false)
                .expire(ConstTime.WEEK)
                .build());

//      Luc Ngoc
        List<ItemOption> lucNOptions = new ArrayList<ItemOption>();
        for (int[] option : lucNgocOptions) {
            lucNOptions.add(new ItemOption(option[0], option[1]));
        }
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1014)
                .itemID(ItemName.LUC_NGOC)
                .options(lucNOptions)
                .gold(1000)
                .isLock(false)
                .expire(ConstTime.WEEK)
                .build());

        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1016)
                .itemID(ItemName.AN_TOC_JUKARI)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());

        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1017)
                .itemID(ItemName.XICH_TU_MA)
                .gold(1000)
                .options(optionsEmpty)
                .expire(ConstTime.WEEK)
                .build());

        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1017)
                .itemID(ItemName.PHUONG_HOANG_BANG)
                .gold(1000)
                .options(optionsEmpty)
                .expire(ConstTime.WEEK)
                .build());

        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1018)
                .itemID(ItemName.SUKAIGAN)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1019)
                .itemID(454)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1020)
                .itemID(455)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1021)
                .itemID(456)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1022)
                .itemID(457)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1023)
                .itemID(573)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1024)
                .itemID(574)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1025)
                .itemID(575)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1026)
                .itemID(576)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1027)
                .itemID(577)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1028)
                .itemID(578)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1029)
                .itemID(775)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1030)
                .itemID(788)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1031)
                .itemID(789)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1032)
                .itemID(1266)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1033)
                .itemID(1076)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1034)
                .itemID(993)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1035)
                .itemID(987)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1036)
                .itemID(986)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1037)
                .itemID(1267)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1038)
                .itemID(1269)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1039)
                .itemID(1270)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1040)
                .itemID(1273)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1041)
                .itemID(1272)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1042)
                .itemID(1260)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1043)
                .itemID(1261)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1044)
                .itemID(1262)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1045)
                .itemID(1263)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1046)
                .itemID(1264)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1047)
                .itemID(1265)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1048)
                .itemID(839)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1049)
                .itemID(840)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1050)
                .itemID(841)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1051)
                .itemID(842)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1052)
                .itemID(843)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1053)
                .itemID(844)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1054)
                .itemID(1221)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1055)
                .itemID(1115)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1056)
                .itemID(778)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1057)
                .itemID(1280)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1058)
                .itemID(1207)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1059)
                .itemID(828)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1060)
                .itemID(1116)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1061)
                .itemID(1117)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1062)
                .itemID(1118)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1063)
                .itemID(1119)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1064)
                .itemID(1120)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1065)
                .itemID(1121)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1066)
                .itemID(1122)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1067)
                .itemID(1123)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1068)
                .itemID(1124)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1069)
                .itemID(1125)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1070)
                .itemID(1126)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1071)
                .itemID(1127)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1072)
                .itemID(1128)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1073)
                .itemID(1135)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1074)
                .itemID(1136)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1075)
                .itemID(1137)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1076)
                .itemID(1138)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1077)
                .itemID(1139)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1078)
                .itemID(1140)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1079)
                .itemID(1141)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1080)
                .itemID(1142)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1081)
                .itemID(1143)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1082)
                .itemID(1144)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1083)
                .itemID(1145)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1084)
                .itemID(1146)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1085)
                .itemID(1147)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1086)
                .itemID(1182)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1087)
                .itemID(1183)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1188)
                .itemID(1184)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1089)
                .itemID(1185)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1090)
                .itemID(1186)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1091)
                .itemID(1187)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().themItem((byte) StoreManager.TYPE_FASHION, ItemStore.builder()
                .id(1092)
                .itemID(1188)
                .options(optionsEmpty)
                .gold(1000)
                .expire(ConstTime.WEEK)
                .build());
    }

    public static void setDataArrow() {
        try {
            ByteArrayOutputStream arrows = new ByteArrayOutputStream();
            DataOutputStream ds = new DataOutputStream(arrows);
            ds.writeShort(Server.arrs.size());
            for (ArrowPaint arr : Server.arrs) {
                ds.writeShort(arr.id);
                ds.writeShort(arr.imgId[0]);
                ds.writeShort(arr.imgId[1]);
                ds.writeShort(arr.imgId[2]);
            }
            ds.flush();
            nj_arrow = arrows.toByteArray();
            ds.close();
            arrows.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setDataEffect() {
        try {
            ByteArrayOutputStream effects = new ByteArrayOutputStream();
            DataOutputStream ds = new DataOutputStream(effects);
            ds.writeShort(Server.efs.size());
            for (EffectCharPaint eff : Server.efs) {
                ds.writeShort(eff.idEf);
                ds.writeByte(eff.arrEfInfo.length);
                for (EffectInfoPaint eff2 : eff.arrEfInfo) {
                    ds.writeShort(eff2.idImg);
                    ds.writeByte(eff2.dx);
                    ds.writeByte(eff2.dy);
                }
            }
            ds.flush();
            nj_effect = effects.toByteArray();
            ds.close();
            effects.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setDataImage() {
        try {
            ByteArrayOutputStream image = new ByteArrayOutputStream();
            DataOutputStream ds = new DataOutputStream(image);
            ds.writeShort(Server.smallImg.size());
            for (int[] img : Server.smallImg) {
                ds.writeByte(img[0]);
                ds.writeShort(img[1]);
                ds.writeShort(img[2]);
                ds.writeShort(img[3]);
                ds.writeShort(img[4]);
            }
            ds.flush();
            nj_image = image.toByteArray();
            ds.close();
            image.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void setDataPart() {
        try {
            ByteArrayOutputStream parts = new ByteArrayOutputStream();
            DataOutputStream ds = new DataOutputStream(parts);
            ds.writeShort(Server.parts.size());
            for (Part p : Server.parts) {
                ds.writeByte(p.type);
                for (PartImage pi : p.pi) {
                    ds.writeShort(pi.id);
                    ds.writeByte(pi.dx);
                    ds.writeByte(pi.dy);
                }
            }
            ds.flush();
            nj_part = parts.toByteArray();
            ds.close();
            parts.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setDataSkill() {
        try {
            ByteArrayOutputStream skills = new ByteArrayOutputStream();
            DataOutputStream ds = new DataOutputStream(skills);
            ds.writeShort(Server.sks.size());
            for (SkillPaint p : Server.sks) {
                ds.writeShort(p.id);
                ds.writeShort(p.effId);
                ds.writeByte(p.numEff);
                ds.writeByte(p.skillStand.length);
                for (SkillInfoPaint skillStand : p.skillStand) {
                    ds.writeByte(skillStand.status);
                    ds.writeShort(skillStand.effS0Id);
                    ds.writeShort(skillStand.e0dx);
                    ds.writeShort(skillStand.e0dy);
                    ds.writeShort(skillStand.effS1Id);
                    ds.writeShort(skillStand.e1dx);
                    ds.writeShort(skillStand.e1dy);
                    ds.writeShort(skillStand.effS2Id);
                    ds.writeShort(skillStand.e2dx);
                    ds.writeShort(skillStand.e2dy);
                    ds.writeShort(skillStand.arrowId);
                    ds.writeShort(skillStand.adx);
                    ds.writeShort(skillStand.ady);
                }
                ds.writeByte(p.skillfly.length);
                for (SkillInfoPaint skillfly : p.skillfly) {
                    ds.writeByte(skillfly.status);
                    ds.writeShort(skillfly.effS0Id);
                    ds.writeShort(skillfly.e0dx);
                    ds.writeShort(skillfly.e0dy);
                    ds.writeShort(skillfly.effS1Id);
                    ds.writeShort(skillfly.e1dx);
                    ds.writeShort(skillfly.e1dy);
                    ds.writeShort(skillfly.effS2Id);
                    ds.writeShort(skillfly.e2dx);
                    ds.writeShort(skillfly.e2dy);
                    ds.writeShort(skillfly.arrowId);
                    ds.writeShort(skillfly.adx);
                    ds.writeShort(skillfly.ady);
                }
            }
            ds.flush();
            nj_skill = skills.toByteArray();
            ds.close();
            skills.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setMap() {
        try {
            List<TileMap> list = MapManager.getInstance().getTileMaps();
            ByteArrayOutputStream bas = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bas);
            dos.writeByte(Config.getInstance().getMapVersion());
            dos.writeByte(list.size());
            for (TileMap map : list) {
                dos.writeUTF(map.name);
            }
            List<NpcTemplate> npcs = NpcManager.getInstance().getNpcTemplates();
            dos.writeByte(npcs.size());
            for (NpcTemplate npc : npcs) {
                dos.writeUTF(npc.name);
                dos.writeShort(npc.headId);
                dos.writeShort(npc.bodyId);
                dos.writeShort(npc.legId);
                String[][] menu = npc.menu;
                dos.writeByte(menu.length);
                for (String[] m : menu) {
                    dos.writeByte(m.length);
                    for (String s : m) {
                        dos.writeUTF(s);
                    }
                }
            }
            List<MobTemplate> mobTemplates = MobManager.getInstance().getMobs();
            dos.writeShort(mobTemplates.size());
            for (MobTemplate mob : mobTemplates) {
                dos.writeByte(mob.type);
                dos.writeUTF(mob.name);
                dos.writeInt(mob.hp);
                dos.writeByte(mob.rangeMove);
                dos.writeByte(mob.speed);
            }
            dos.flush();
            map = bas.toByteArray();
            dos.close();
            bas.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setSkill() {
        try {
            ByteArrayOutputStream bas = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bas);
            dos.writeByte(Config.getInstance().getSkillVersion());
            List<SkillOptionTemplate> optionTemplates = GameData.getInstance().getOptionTemplates();
            dos.writeByte(optionTemplates.size());
            for (SkillOptionTemplate optionTemplate : optionTemplates) {
                dos.writeUTF(optionTemplate.name);
            }
            List<Clazz> clazzs = GameData.getInstance().getClazzs();
            dos.writeByte(clazzs.size());
            for (Clazz clazz : clazzs) {
                List<SkillTemplate> templates = clazz.getSkillTemplates();
                dos.writeUTF(clazz.getName());
                dos.writeByte(templates.size());
                for (SkillTemplate template : templates) {
                    dos.writeByte(template.id);
                    dos.writeUTF(template.name);
                    dos.writeByte(template.maxPoint);
                    dos.writeByte(template.type);
                    dos.writeShort(template.iconId);
                    dos.writeUTF(template.description);
                    dos.writeByte(template.skills.size());
                    for (Skill skill : template.skills) {
                        dos.writeShort(skill.id);
                        dos.writeByte(skill.point);
                        dos.writeByte(skill.level);
                        dos.writeShort(skill.manaUse);
                        dos.writeInt(skill.coolDown);
                        dos.writeShort(skill.dx);
                        dos.writeShort(skill.dy);
                        dos.writeByte(skill.maxFight);
                        dos.writeByte(skill.options.length);
                        for (SkillOption option : skill.options) {
                            dos.writeShort(option.param);
                            dos.writeByte(option.optionTemplate.id);
                        }
                    }
                }
            }
            dos.flush();
            skill = bas.toByteArray();
            dos.close();
            bas.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setData() {
        try {
            ByteArrayOutputStream bas = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bas);
            int version = Config.getInstance().getDataVersion();
            if (Config.getInstance().getMaxPercentAdd() > 0) {
                version += 1;
            }
            dos.writeByte(version);
            byte[] arrow = nj_arrow;
            dos.writeInt(arrow.length);
            dos.write(arrow);
            byte[] effect = nj_effect;
            dos.writeInt(effect.length);
            dos.write(effect);
            byte[] image = nj_image;
            dos.writeInt(image.length);
            dos.write(image);
            byte[] part = nj_part;
            dos.writeInt(part.length);
            dos.write(part);
            byte[] skill = nj_skill;
            dos.writeInt(skill.length);
            dos.write(skill);
            dos.writeByte(npcTasks.length);
            for (int i = 0; i < npcTasks.length; i++) {
                dos.writeByte(npcTasks[i].length);
                for (int a = 0; a < npcTasks[i].length; a++) {
                    dos.writeByte(npcTasks[i][a]);
                    dos.writeByte(mapTasks[i][a]);
                }
            }
            dos.writeByte(exps.length);
            for (long exp : exps) {
                dos.writeLong(exp);
            }
            dos.writeByte(GameData.UP_CRYSTAL.length);
            for (int num : GameData.UP_CRYSTAL) {
                dos.writeInt(num);
            }
            dos.writeByte(GameData.UP_CLOTHE.length);
            for (int num : GameData.UP_CLOTHE) {
                dos.writeInt(num);
            }
            dos.writeByte(GameData.UP_ADORN.length);
            for (int num : GameData.UP_ADORN) {
                dos.writeInt(num);
            }
            dos.writeByte(GameData.UP_WEAPON.length);
            for (int num : GameData.UP_WEAPON) {
                dos.writeInt(num);
            }
            dos.writeByte(GameData.COIN_UP_CRYSTAL.length);
            for (int num : GameData.COIN_UP_CRYSTAL) {
                dos.writeInt(num);
            }
            dos.writeByte(GameData.COIN_UP_CLOTHE.length);
            for (int num : GameData.COIN_UP_CLOTHE) {
                dos.writeInt(num);
            }
            dos.writeByte(GameData.COIN_UP_ADORN.length);
            for (int num : GameData.COIN_UP_ADORN) {
                dos.writeInt(num);
            }
            dos.writeByte(GameData.COIN_UP_WEAPON.length);
            for (int num : GameData.COIN_UP_WEAPON) {
                dos.writeInt(num);
            }
            dos.writeByte(GameData.GOLD_UP.length);
            for (int num : GameData.GOLD_UP) {
                dos.writeInt(num);
            }
            dos.writeByte(GameData.MAX_PERCENT.length);
            if (Config.getInstance().getMaxPercentAdd() > 0) {
                for (int num : GameData.MAX_PERCENT) {
                    dos.writeInt((int) (num + (num * Config.getInstance().getMaxPercentAdd())));
                }
            } else {
                for (int num : GameData.MAX_PERCENT) {
                    dos.writeInt(num);
                }
            }
            byte[] effData = EffectTemplateManager.getInstance().getData();
            dos.write(effData);
            dos.flush();
            data = bas.toByteArray();
            dos.close();
            bas.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setOffline() {
        try {
            Connection conn = DbManager.getInstance().getConnection(DbManager.GAME);
            PreparedStatement stmt = conn.prepareStatement("UPDATE `users` SET `online` = ?;");
            stmt.setInt(1, 0);
            stmt.executeUpdate();
            stmt.close();

            stmt = conn.prepareStatement("UPDATE `players` SET `online` = ? WHERE `server_id` = ?;");
            stmt.setInt(1, 0);
            stmt.setInt(2, Config.getInstance().getServerID());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void start() {
        try {
            registerShutdownHook();
            setOffline();
            LuckyDrawManager.getInstance().add(new LuckyDraw("Vòng xoay thường", LuckyDrawManager.NORMAL));
            LuckyDrawManager.getInstance().add(new LuckyDraw("Vòng xoay vip", LuckyDrawManager.VIP));
            Thread threadLuckyDraw = new Thread(LuckyDrawManager.getInstance());
            threadLuckyDraw.setName("Vòng xoay");
            threadLuckyDraw.start();
            Thread threadStall = new Thread(StallManager.getInstance());
            threadStall.setName("Gian hàng");
            threadStall.start();
            GameData.getInstance().start();
            WorldManager.getInstance().start();
            SocketIO.init();
            SpawnBossManager.getInstance().init();
//            SpawnBossManager.getInstance().spawn(6, 0, 0, SpawnBossManager.THUONG, SpawnBossManager.ALL);
//            SpawnBossManager.getInstance().spawn(21, 00, 0, SpawnBossManager.THUONG, SpawnBossManager.ALL);
//            SpawnBossManager.getInstance().spawn(12, 30, 0, SpawnBossManager.THUONG, SpawnBossManager.ALL);
//            SpawnBossManager.getInstance().spawn(6, 0, 0, SpawnBossManager.VUNG_DAT_MA_QUY, SpawnBossManager.ALL);
//            SpawnBossManager.getInstance().spawn(21, 0, 0, SpawnBossManager.VUNG_DAT_MA_QUY, SpawnBossManager.ALL);
//            SpawnBossManager.getInstance().spawn(12, 30, 0, SpawnBossManager.VUNG_DAT_MA_QUY, SpawnBossManager.ALL);
//            SpawnBossManager.getInstance().spawn(6, 0, 0, SpawnBossManager.LANG_CO, SpawnBossManager.ALL);
//            SpawnBossManager.getInstance().spawn(21, 0, 0, SpawnBossManager.LANG_CO, SpawnBossManager.ALL);
//            SpawnBossManager.getInstance().spawn(12, 30, 0, SpawnBossManager.LANG_CO, SpawnBossManager.ALL);
            SpawnBossManager.getInstance().spawnRepeat(SpawnBossManager.LANG_TRUYEN_THUYET, 2, SpawnBossManager.ALL);
            SpawnBossManager.getInstance().spawnRepeat(SpawnBossManager.THUONG, 2, SpawnBossManager.ALL);
            SpawnBossManager.getInstance().spawnRepeat(SpawnBossManager.VUNG_DAT_MA_QUY, 2, SpawnBossManager.ALL);
            SpawnBossManager.getInstance().spawnRepeat(SpawnBossManager.LANG_CO, 2, SpawnBossManager.ALL);
            Clan.start();
            War.timer(13, 0, 0, War.TYPE_LEVEL_30_TO_50);
            War.timer(16, 0, 0, War.TYPE_ALL_LEVEL);
            War.timer(19, 0, 0, War.TYPE_LEVEL_70_TO_90);
            AutoMaintenance.maintenance(0, 0, 0);
            Thread t = new Thread(new AutoSaveData());
            t.start();
            Thread t1 = new Thread(new AutoSaveData1());
            t1.start();
            Thread t2 = new Thread(new AutoSaveData2());
            t2.start();
            Thread t3 = new Thread(new AutoSaveData3());
            t3.start();
            Thread t4 = new Thread(new AutoSaveData4());
            t4.start();
            int port = Config.getInstance().getPort();
            Log.info("Start socket post=" + port);
            server = new ServerSocket(port);
            start = true;
            id = 0;
            Log.info("Start server Success!");
            while (start) {
                try {
                    Socket client = server.accept();
                    if (NinjaSchool.isStop) {
                        client.close();
                        continue;
                    }
                    String ip = client.getInetAddress().getHostAddress();
                    int number = ServerManager.frequency(ip);
                    if (number >= Config.getInstance().getIpAddressLimit()) {
                        client.close();
                        if (!checkCommandExecuted()) {
                            SystemCommand.runConfiguredCommand(Config.getInstance().getAntiDdosAutoCommand(),
                                    "auto anti-DDoS");
                            setCommandExecuted();
                        }
                        continue;
                    }
                    Session cl = new Session(client, ++id);
                    cl.IPAddress = ip;
                    ServerManager.add(ip);
                } catch (Exception e) {
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkCommandExecuted() {
        File stateFile = new File(COMMAND_STATE_FILE);
        return stateFile.exists();
    }

    private static void setCommandExecuted() {
        try {
            File stateFile = new File(COMMAND_STATE_FILE);
            if (!stateFile.exists()) {
                stateFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void startAntiDDoS() {
        if (Config.getInstance() != null) {
            if (SystemCommand.runConfiguredCommand(Config.getInstance().getAntiDdosManualCommand(),
                    "manual anti-DDoS")) {
                Log.info("Started anti-DDoS command");
            } else {
                Log.warn("Manual anti-DDoS command is not configured.");
            }
            return;
        }
        try {
            Runtime rt = Runtime.getRuntime();
            String command = "cmd /c start run_chongddosvv.bat";
            rt.exec(command);
            Log.info("Đã bật chống DDoS");
        } catch (IOException ex) {
            Log.error("Không thể bật chống DDoS", ex);
        }
    }

    public static void stop() {
        start = false;
        isStop = true;
        GameData.getInstance().close();
        StallManager.getInstance().stop();
        Clan.running = false;
        WorldManager.getInstance().close();
        MapManager.getInstance().close();
        StallManager.getInstance().save();
        List<Clan> clans = Clan.getClanDAO().getAll();
        synchronized (clans) {
            for (Clan clan : clans) {
                Clan.getClanDAO().update(clan);
            }
        }
        try {
            ThienDiaManager.getInstance().update();
        } catch (Exception e) {
            e.printStackTrace();
        }
        close();
    }

    private static void registerShutdownHook() {
        if (!SHUTDOWN_HOOK_REGISTERED.compareAndSet(false, true)) {
            return;
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> gracefulShutdown("JVM shutdown hook"),
                "nso-graceful-shutdown"));
    }

    public static void gracefulShutdown(String reason) {
        if (!SHUTDOWN_IN_PROGRESS.compareAndSet(false, true)) {
            return;
        }
        Log.info("Graceful shutdown requested: " + reason);
        NinjaSchool.isStop = true;
        if (start) {
            saveAll();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            stop();
        }
        Log.info("Graceful shutdown completed.");
    }

    public static void maintance() {
        try {
            Calendar calendar = Calendar.getInstance();
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                Files.deleteIfExists(Path.of("nhanquaviptuan/list_reward.txt"));
                rewardVIP.clear();

            }
            if (calendar.get(Calendar.HOUR_OF_DAY) == 24 || calendar.get(Calendar.MINUTE) == 05) {
                Files.deleteIfExists(Path.of("listddhn.txt"));
                listddhn.clear();
            }
            NinjaSchool.isStop = true;
            LuckyDrawManager.getInstance().stop();
            Log.info("Chuẩn bị đóng máy chủ.");
            String name = "Hệ thống";
            String text = "Máy chủ bảo trì sau 5 phút, vui lòng thoát game để tránh mất dữ liệu. Nếu cố tình không thoát chúng tôi không chịu trách nhiệm!";
            GlobalService.getInstance().chat(name, text);
            GlobalService.getInstance().showAlert(name, text);
            Log.info("Hệ thống Đóng sau 5 phút.");
            Thread.sleep(240000);
            String text2 = "Máy chủ bảo trì sau 1 phút, vui lòng thoát game để tránh mất dữ liệu. Nếu cố tình không thoát chúng tôi không chịu trách nhiệm!";
            GlobalService.getInstance().chat(name, text2);
            GlobalService.getInstance().showAlert(name, text2);
            Log.info("Hệ thống Đóng sau 1 phút.");
            Thread.sleep(60000);
            Log.info("Hệ thống Bắt đầu đóng máy chủ.");
            Server.gracefulShutdown("maintenance");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveAll1() {
        try {
            StallManager.getInstance().save();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            List<Clan> clans = Clan.getClanDAO().getAll();
            synchronized (clans) {
                for (Clan clan : clans) {
                    Clan.getClanDAO().update(clan);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ThienDiaManager.getInstance().update();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            List<Char> chars = ServerManager.getChars();
            for (Char _char : chars) {
                try {
                    if (!_char.isCleaned) {
                        _char.saveData();
                        _char.saveDataPlayer();
                        GlobalService.getInstance().chat("CHÚ Ý", "SRC game đc mua từ TVT. chúc ae online vui vẻ!");
                        if (!_char.user.isCleaned) {
                            _char.user.saveData();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.info("Làm mới bảng xếp hạng");
            Ranked.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveAll() {
        try {
            StallManager.getInstance().save();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            List<Clan> clans = Clan.getClanDAO().getAll();
            synchronized (clans) {
                for (Clan clan : clans) {
                    Clan.getClanDAO().update(clan);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ThienDiaManager.getInstance().update();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            List<Char> chars = ServerManager.getChars();
            for (Char _char : chars) {
                try {
                    if (!_char.isCleaned) {
                        _char.saveData();
                        if (!_char.user.isCleaned) {
                            _char.user.saveData();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Log.info("Làm mới bảng xếp hạng");
            Ranked.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveAllx() {
        try {
            List<Char> chars = ServerManager.getChars();
            for (Char _char : chars) {
                try {
                    if (!_char.isCleaned) {
                        _char.goldUnpaid();
                        _char.vongquay();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void readFile() {
        try {
            File file = new File("nhanquaviptuan/list_reward.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String name = br.readLine();
            while (name != null) {
                rewardVIP.add(name);
                name = br.readLine();
            }
            br.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(String txt) {
        rewardVIP.add(txt);
        BufferedWriter bw;
        FileWriter fw;
        try {
            File file = new File("nhanquaviptuan/list_reward.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            bw.write(txt);
            bw.newLine(); // Thêm xuống dòng sau mỗi tên mới
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readFile1() {
        try {
            File file = new File("listddhn.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String name = br.readLine();
            while (name != null) {
                listddhn.add(name);
                name = br.readLine();
            }
            br.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFile1(String txt) {
        listddhn.add(txt);
        BufferedWriter bw;
        FileWriter fw;
        try {
            File file = new File("listddhn.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            bw.write(txt);
            bw.newLine();
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        try {
            List<User> users = ServerManager.getUsers();
            for (User user : users) {
                if (!user.isCleaned) {
                    user.session.closeMessage();
                }
            }
            if (server != null && !server.isClosed()) {
                server.close();
            }
            server = null;
            if (ServerManager.getUsers().isEmpty()) {

            }
            Log.info("End socket");
        } catch (IOException e) {
        }
    }

    public static void closemmen() {
        try {
            List<User> users = ServerManager.getUsers();
            for (User user : users) {
                if (!user.isCleaned) {
                    user.session.closeMessage();
                }
            }
            Log.info("End socket");
            System.out.println("Kick xong");
        } catch (Exception e) {
            System.err.println("Error in closing users: " + e.getMessage());
        }
    }

}

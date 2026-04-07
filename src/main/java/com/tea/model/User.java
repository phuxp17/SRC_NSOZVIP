package com.tea.model;

import com.tea.ability.AbilityFromEquip;
import com.tea.clan.Clan;
import com.tea.clan.Member;
import com.tea.constants.SQLStatement;
import com.tea.db.jdbc.DbManager;
import com.tea.fashion.FashionFromEquip;
import com.tea.item.Equip;
import com.tea.item.Item;
import com.tea.item.ItemTemplate;
import com.tea.item.Mount;
import com.tea.map.Map;
import com.tea.map.MapManager;
import com.tea.network.Controller;
import com.tea.network.Message;
import com.tea.network.Service;
import com.tea.network.Session;
import com.tea.server.Config;
import com.tea.server.GlobalService;
import com.tea.server.NinjaSchool;
import com.tea.server.ServerManager;
import com.tea.socket.Action;
import com.tea.socket.SocketIO;
import com.tea.task.TaskOrder;
import com.tea.util.Log;
import com.tea.util.NinjaUtils;
import com.tea.util.StringUtils;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class User {

//    public static void newPlay(String rand, User us) {
//        try {
//            Connection conn = DbManager.getInstance().getConnection(DbManager.CREATE_CHAR);
//            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `users` WHERE `username` = ? LIMIT 1;",
//                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
//            try {
//                stmt.setString(1, rand);
//                ResultSet result = stmt.executeQuery();
//                if (!result.first()) {
//                    PreparedStatement stmt2 = conn.prepareStatement(
//                            "INSERT INTO `users`(`username`, `password`, `online`, `luong`) VALUES (?, ?, ?, ?);");
//                    try {
//                        stmt2.setString(1, rand);
//                        stmt2.setString(2, "kitakeyos");
//                        stmt2.setInt(3, 0);
//                        stmt2.setInt(4, 999);
//                        stmt2.executeUpdate();
//                    } finally {
//                        stmt2.close();
//                    }
//                }
//                result.close();
//            } finally {
//                stmt.close();
//            }
//        } catch (SQLException ex) {
//        }
//    }

    public Session session;
    public Service service;
    public Vector<Char> chars;
    public int id;
    public String username;
    public String password;
    public String random;
    private byte status;
    private byte activated;
    public Timestamp banUntil;
    public Timestamp time_login;
    public int gold;
    public Char sltChar;
    public boolean receivedFirstGift;
    public long lastAttendance;
    public boolean isLoadFinish;
    public boolean isEntered;
    public boolean isCleaned;
    public boolean isDuplicate;
    public int[] levelRewards = new int[5];
    public List<Integer> roles = new ArrayList<>();
    public int Is_Admin_WEB;
    public int kh;
    public int efffan;
    public int effvip;
    public int efftop;
    public int effytb;
    public int effdg;
    public int effygt;
    public int effydc;
    public int effydh;
    public int nhanmocnap;
    private byte tester;
    public ArrayList<String> IPAddress;
    private boolean saving;

    public User(Session client, String username, String password, String random) {
        this.session = client;
        this.service = client.getService();
        this.username = username;
        this.password = password;
        this.random = random;
    }

    public HashMap<String, Object> getUserMap() {
        try {
            ArrayList<HashMap<String, Object>> list;
            PreparedStatement stmt = DbManager.getInstance().getConnection(DbManager.LOGIN).prepareStatement(SQLStatement.GET_USER, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, this.username);
            ResultSet data = stmt.executeQuery();
            try {
                list = DbManager.getInstance().convertResultSetToList(data);
            } finally {
                data.close();
                stmt.close();
            }
            if (list.isEmpty()) {
                return null;
            }
            HashMap<String, Object> map = list.get(0);
            if (map != null) {
                String passwordHash = (String) map.get("password");
                if (!StringUtils.checkPassword(passwordHash, password)) {
                    return null;
                }
            }
            return map;
        } catch (SQLException e) {
            Log.error("getUserMap() err", e);
        }
        return null;
    }

    private boolean isIPBlocked(ArrayList<String> userIPs) {
        if (userIPs == null || userIPs.isEmpty()) {
            return false;
        }
        Config serverConfig = Config.getInstance();
        String url = serverConfig.getJdbcUrl();
        String user = serverConfig.getDbUser();
        String password = serverConfig.getDbPassword();
        String query = "SELECT blocker_ip FROM blockip_list WHERE blocker_ip IN ("
                + String.join(", ", Collections.nCopies(userIPs.size(), "?")) + ")";
        ArrayList<String> blockedIPs = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(url, user, password); PreparedStatement pst = con.prepareStatement(query)) {
            for (int i = 0; i < userIPs.size(); i++) {
                pst.setString(i + 1, userIPs.get(i));
            }
            try (ResultSet resultSet = pst.executeQuery()) {
                while (resultSet.next()) {
                    blockedIPs.add(resultSet.getString("blocker_ip"));
                }
            }
            writeBlockedIPsToFile(blockedIPs, "logs/ipblock/blocked_ips.txt");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return !blockedIPs.isEmpty();
    }

    class ServerStatus {

        boolean trangThai;
        String comment;

        ServerStatus(boolean trangThai, String comment) {
            this.trangThai = trangThai;
            this.comment = comment;
        }
    }

    private ServerStatus checkServerStatus() {
        Config serverConfig = Config.getInstance();
        String url = serverConfig.getJdbcUrl();
        String user = serverConfig.getDbUser();
        String password = serverConfig.getDbPassword();
        String query = "SELECT trangthai, comment FROM closeserver WHERE id = ?";

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet resultSet = null;
        try {
            con = DriverManager.getConnection(url, user, password);
            pst = con.prepareStatement(query);
            pst.setInt(1, 1);
            resultSet = pst.executeQuery();

            if (resultSet.next()) {
                boolean trangThai = resultSet.getBoolean("trangthai");
                String comment = resultSet.getString("comment");
                return new ServerStatus(trangThai, comment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ServerStatus(false, "Lỗi khi kết nối với cơ sở dữ liệu: " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return new ServerStatus(false, "Không thể xác định trạng thái server hoặc server đang hoạt động bình thường.");
    }

    private void writeBlockedIPsToFile(ArrayList<String> blockedIPs, String fileName) {
        try {
            Set<String> existingIPs = new HashSet<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    existingIPs.add(line.trim());
                }
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                for (String ip : existingIPs) {
                    writer.write(ip);
                    writer.newLine();
                }
                for (String ip : blockedIPs) {
                    if (!existingIPs.contains(ip)) {
                        writer.write(ip);
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public synchronized void login() {
        try {

            if (username.equals("-1") && password.equals("12345")) {
                service.serverDialog("Bạn hãy liên hệ admin để đăng kí");
                return;
            }
            if (Config.getInstance().isServerInfoEnabled()) {
                ServerSv status1x = ServerSv.checkServerSv();
                if (!status1x.trangThai1) {
                    service.serverDialog(status1x.comment1);
                    return;
                }
            }
            Pattern p = Pattern.compile("^[a-z0-9]+$");
            Matcher m1 = p.matcher(username);
            if (!m1.find()) {
                service.serverDialog("Tên tài khoản có kí tự lạ hoặc viết hoa.");
                return;
            }
            HashMap<String, Object> map = getUserMap();

            if (map == null) {
                if (Config.getInstance().getServerID() == 2) {
                    this.username = this.username + "sv2";
                    map = getUserMap();
                    this.isDuplicate = true;
                }
                if (map == null) {
                    service.serverDialog("Tài khoản hoặc mật khẩu không chính xác.");
                    return;
                }
            }
            this.id = (int) ((long) (map.get("id")));
            this.Is_Admin_WEB = (int) map.get("Is_Admin_WEB");
            this.kh = (int) map.get("kh");
            this.lastAttendance = (long) map.get("last_attendance_at");
            this.receivedFirstGift = (int) map.get("received_first_gift") == 1;
            this.gold = (int) map.get("luong");
            this.getRoles();
            this.status = (byte) ((int) map.get("status"));
            this.activated = (byte) ((int) map.get("activated"));
            Object obj = map.get("ban_until");
            Object obj1 = map.get("timelogin");
            this.efffan = (int) map.get("efffan");
            this.effvip = (int) map.get("effvip");
            this.efftop = (int) map.get("efftop");
            this.effytb = (int) map.get("effytb");
            this.effdg = (int) map.get("effdg");
            this.effygt = (int) map.get("effygt");
            this.effydc = (int) map.get("effydc");
            this.effydh = (int) map.get("effydh");
            this.nhanmocnap = (int) map.get("nhanmocnap");
            this.tester = (byte) ((int) map.get("tester"));
            if (obj != null) {
                this.banUntil = (Timestamp) obj;
                long now = System.currentTimeMillis();
                long timeRemaining = banUntil.getTime() - now;
                if (timeRemaining > 0) {
                    service.serverDialog(String.format("Tài khoản bị khóa trong %s. Vui lòng liên hệ admin để biết thêm chi tiết.", NinjaUtils.timeAgo((int) (timeRemaining / 1000))));
                    return;
                }
            }
             if (obj1 != null) {
                this.time_login = (Timestamp) obj1;
                long now = System.currentTimeMillis();
                long timeRemaining1 = time_login.getTime() - now;
                if (timeRemaining1 > 0) {
                    service.serverDialog(String.format("đăng nhập lại sau %s.", NinjaUtils.timeAgo((int) (timeRemaining1 / 1000))));
                    return;
                }
            }  //if (this.status == 0) {
                //service.serverDialog("Tài khoản đã bị ADMIN khoá Vĩnh Viễn.!");
                //return;
            //} 
                if (this.tester == 0) {
                ServerStatus status1 = checkServerStatus();
                if (!status1.trangThai) {
                    service.serverDialog(status1.comment);
                    return;
                }
            }
            JSONArray rewards = (JSONArray) JSONValue.parse(map.get("level_reward").toString());
            for (int i = 0; i < 5; i++) {
                this.levelRewards[i] = Integer.parseInt(rewards.get(i).toString());
            }
            this.IPAddress = new ArrayList<>();
            obj = map.get("ip_address");
            if (obj != null) {
                String str = obj.toString();
                if (!str.equals("")) {
                    JSONArray jArr = (JSONArray) JSONValue.parse(str);
                    int size = jArr.size();
                    for (int i = 0; i < size; i++) {
                        IPAddress.add(jArr.get(i).toString());
                    }
                }
            }
            if (!IPAddress.contains(session.IPAddress)) {
                IPAddress.add(session.IPAddress);
            }
            boolean isBlocked = isIPBlocked(IPAddress);
            if (isBlocked) {
                service.serverDialog("Tài Khoản Của Bạn Có Chứa IP Bị Chặn Khỏi Server Không Thể Đăng Nhập.");
                return;
            }
            User u = ServerManager.findUserByUsername(this.username);
            if (u != null) {
                service.serverDialog("Tài khoản đã có người đăng nhập.");
                if (u.session != null && u.session.getService() != null) {
                    u.session.getService().serverDialog("Có người đăng nhập vào tài khoản của bạn.");
                }
                if (!u.isCleaned) {
                    u.session.disconnect();
                }
                return;
            }
            ServerManager.addUser(this);
            boolean isOnline = ((byte) map.get("online")) == 1;
            if (isOnline) {
                service.serverDialog("Tài khoản đang có người đăng nhập (2)");
                forceOutOtherServer();
                return;
            }
            this.isLoadFinish = true;
        } catch (Exception ex) {
            Log.error("login err", ex);
        }
    }

    public void forceOutOtherServer() {
        SocketIO.emit(Action.FORCE_OUT, String.format("{\"user_id\":\"%d\", \"server_id\":\"-1\", \"current_server\":\"%d\"}", this.id, Config.getInstance().getServerID()));
    }

    public void initCharacterList() {
        try {
            PreparedStatement stmt = DbManager.getInstance().getConnection(DbManager.LOAD_CHAR).prepareStatement("SELECT `players`.`id`, `players`.`name`, `players`.`gender`, `players`.`class`, `players`.`last_logout_time`, `players`.`head`, `players`.`head2`, `players`.`body`, `players`.`weapon`, `players`.`leg`, `players`.`online`, CAST(JSON_EXTRACT(data, \"$.exp\") AS INT) AS `exp` FROM `players` WHERE `players`.`user_id` = ? AND `players`.`server_id` = ? ORDER BY `players`.`last_logout_time` DESC LIMIT 3;");
            stmt.setInt(1, this.id);
            stmt.setInt(2, Config.getInstance().getServerID());
            ResultSet data = stmt.executeQuery();
            try {
                this.chars = new Vector<>();
                while (data.next()) {
                    int id = data.getInt("id");
                    Char _char = new Char(id);
                    _char.loadDisplay(data);
                    this.chars.add(_char);
                }
            } finally {
                data.close();
                stmt.close();
            }
        } catch (Exception ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createCharacter(Message ms) {
        try {
            if (this.chars.size() >= 1) {
                service.serverDialog("Bạn chỉ được tạo tối đa 1 nhân vật.");
                return;
            }
            String name = ms.reader().readUTF();
            Pattern p = Pattern.compile("^[a-z0-9]+$");
            Matcher m1 = p.matcher(name);
            if (!m1.find()) {
                service.serverDialog("Tên nhân vật không được chứa ký tự đặc biệt!");
                return;
            }
            byte gender = ms.reader().readByte();
            byte head = ms.reader().readByte();
            byte[] h = null;
            if (gender == 0) {
                h = new byte[]{11, 26, 27, 28};
                gender = 0;
            } else {
                h = new byte[]{2, 23, 24, 25};
                gender = 1;
            }
            byte temp = h[0];
            for (byte b : h) {
                if (head == b) {
                    temp = b;
                    break;
                }
            }
            head = temp;
            if (name.length() < 6 || name.length() > 15) {
                service.serverDialog("Tên tài khoản chỉ cho phép từ 6 đến 15 ký tự!");
                return;
            }
            Connection conn = DbManager.getInstance().getConnection(DbManager.CREATE_CHAR);
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `players` WHERE `user_id` = ?;",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

            try {
                stmt.setInt(1, this.id);
                ResultSet check = stmt.executeQuery();
                if (check.last()) {
                    if (check.getRow() >= 1) {
                        service.serverDialog("Bạn đã tạo tối đa số nhân vât!");
                        return;
                    }
                }
                check.close();
            } finally {
                stmt.close();
            }
            stmt = conn.prepareStatement("SELECT * FROM `players` WHERE `name` = ?;", ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            try {
                stmt.setString(1, name);
                ResultSet check = stmt.executeQuery();
                if (check.last()) {
                    if (check.getRow() > 0) {
                        service.serverDialog("Tên nhân vật đã tồn tại!");
                        return;
                    }
                }
                check.close();
            } finally {
                stmt.close();
            }
            stmt = conn.prepareStatement(
                    "INSERT INTO players(`user_id`, `server_id`, `name`, `gender`, `head`, `xu`, `yen`, `skill`, `equiped`, `bag`, `box`, `mount`, `effect`, `friends`) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            try {
                stmt.setInt(1, this.id);
                stmt.setInt(2, Config.getInstance().getServerID());
                stmt.setString(3, name);
                stmt.setByte(4, gender);
                stmt.setShort(5, head);
                stmt.setInt(6, 0);
                stmt.setInt(7, 0);
                stmt.setString(8, "[]");
                stmt.setString(9, "[]");
                stmt.setString(10, "[]");
                stmt.setString(11, "[]");
                stmt.setString(12, "[]");
                stmt.setString(13, "[]");
                stmt.setString(14, "[]");
                stmt.executeUpdate();
            } finally {
                stmt.close();
            }
            initCharacterList();
            service.selectChar(chars);
        } catch (IOException | SQLException e) {
            Log.error("create char err", e);
            service.serverDialog("Tạo nhân vật thất bại!");
        }
    }

    public Char getCharByName(String name) {
        for (Char _char : this.chars) {
            if (_char.name.equals(name)) {
                return _char;
            }
        }
        return null;
    }

    public void selectChar(Message ms) {
        try {

            if (NinjaSchool.isStop) {
                service.serverDialog("Hệ thống Máy chủ bảo trì vui lòng thoát game để tránh mất dữ liệu.");
                Thread.sleep(1000);
                if (!isCleaned) {
                    session.disconnect();
                }
                return;
            }
            if (isEntered) {
                return;
            }
            String name = ms.reader().readUTF();
            if (chars == null) {
                return;
            }
            forceOutOtherServer();
            sltChar = getCharByName(name);
            if (sltChar == null) {
                session.disconnect();
                return;
            }
            this.timelogin(+5);
            if (sltChar.online) {
                service.serverDialog("Nhân vật chưa lưu xong dữ liệu.");
                Thread.sleep(1000);
                if (!isCleaned) {
                    session.disconnect();
                }
                return;
            }
            chars = null;
            if (sltChar != null) {
                long now = System.currentTimeMillis();
                long lastTime = sltChar.lastLogoutTime + 5000;
                int num = (int) ((lastTime - now) / 1000);
                if (num > 0) {//số giây đăng nhập
                    service.serverDialog("Bạn chỉ có thể vào lại game sau " + num + " giây nữa");
                    return;
                }
                if (!sltChar.load()) {
                    session.disconnect();
                    return;
                }
                sltChar.user = this;
                if (sltChar.coin < 0 || sltChar.coinInBox < 0 || sltChar.yen < 0 || this.gold < 0) {
                    lock();
                    return;
                }
                Controller controller = (Controller) session.getMessageHandler();
                controller.setChar(sltChar);
                sltChar.setService(this.service);
                sltChar.setLanguage(session.language);
                service.setChar(this.sltChar);
                this.sltChar.tongnap = this.sltChar.getTongNaps(sltChar);
                byte zoneId = 0;
                int map = sltChar.mapId;
                Map m = MapManager.getInstance().find(map);
                if (m.tilemap.isNotSave()) {
                    map = sltChar.saveCoordinate;
                }
                boolean isException = false;
                try {
                    zoneId = NinjaUtils.randomZoneId(map);
                    if (zoneId == -1) {
                        isException = true;
                    }
                } catch (Exception e) {
                    isException = true;
                }
                if (isException) {
                    map = sltChar.saveCoordinate;
                    zoneId = NinjaUtils.randomZoneId(map);
                    short[] xy = NinjaUtils.getXY(map);
                    sltChar.setXY(xy[0], xy[1]);
                }
                this.sltChar.setFashionStrategy(new FashionFromEquip());
                this.sltChar.setAbilityStrategy(new AbilityFromEquip());
                this.sltChar.setAbility();
                this.sltChar.hp = this.sltChar.maxHP;
                this.sltChar.mp = this.sltChar.maxMP;
                sltChar.setFashion();
                sltChar.invite = new Invite();
                ServerManager.addChar(sltChar);
                this.service.sendDataBox();
                this.service.loadAll();
                MapManager.getInstance().joinZone(sltChar, map, zoneId);
                service.onBijuuInfo(this.id, sltChar.bijuu);
                isEntered = true;
                sltChar.getEm().displayAllEffect(service, null, sltChar);
                for (Item item : sltChar.bag) {
                    if (item != null) {
                        if (item.template.isTypeBody() || item.template.isTypeMount()
                                || item.template.isTypeNgocKham()) {
                            service.itemInfo(item, (byte) 3, (byte) item.index);
                        }
                    }
                }
                if (sltChar.equipment[ItemTemplate.TYPE_THUNUOI] != null) {
                    sltChar.getEm().setEffectPet();
                }
                Clan clan = sltChar.clan;
                if (clan != null) {
                    Member mem = clan.getMemberByName(name);
                    if (mem != null) {
                        mem.setOnline(true);
                        mem.setChar(sltChar);
                    }
                    clan.getClanService().requestClanMember();
                }
                service.sendSkillShortcut("OSkill", sltChar.onOSkill, (byte) 0);
                service.sendSkillShortcut("KSkill", sltChar.onKSkill, (byte) 0);
                service.sendSkillShortcut("CSkill", sltChar.onCSkill, (byte) 0);
                if (sltChar.taskMain != null) {
                    sltChar.updateTaskLevelUp();
                    this.service.sendTaskInfo();
                }
                for (TaskOrder task : sltChar.taskOrders) {
                    service.sendTaskOrder(task);
                }
                if (!sltChar.message.equals("")) {
                    this.service.showAlert("Hệ thống", sltChar.message);
                    sltChar.message = "";
                } else {
                    String notification = Config.getInstance().getNotification();
                    if (notification != null) {
                        this.service.showAlert("Thông Báo", notification);
                    }
                }
                Connection conn = DbManager.getInstance().getConnection(DbManager.SAVE_DATA);
                try {
                    PreparedStatement stmt2 = conn.prepareStatement("UPDATE `users` SET `online` = ? WHERE `id` = ?");
                    try {
                        stmt2.setInt(1, 1);
                        stmt2.setInt(2, this.id);
                        stmt2.executeUpdate();
                    } finally {
                        stmt2.close();
                    }
                } catch (Exception ex) {
                    Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
                }
                sltChar.lastLoginTime = System.currentTimeMillis();
                PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE `players` SET `online` = ?, `last_login_time` = ? WHERE `id` = ? LIMIT 1;");
                try {
                    stmt.setInt(1, 1);
                    stmt.setLong(2, sltChar.lastLoginTime);
                    stmt.setInt(3, sltChar.id);
                    stmt.executeUpdate();
                } finally {
                    stmt.close();
                }
                sltChar.goldUnpaid();
                sltChar.vongquay();
                sltChar.giftcodeUnpaid();
                sltChar.checkExpireMount();
                session.setName(sltChar.name);
                if (this.isDuplicate) {
                    this.service.showAlert("Quan Trọng", "Tên đăng nhập của bạn đang bị trùng lặp với một tài khoản khác, hiện tại tên đăng nhập của bạn là: " + this.username + ".\nĐể thuận tiện hơn trong việc đăng nhập, bạn hãy đổi tên đăng nhập bằng cách gặp Tajima tại làng Tone. Chọn Đổi Tên Đăng Nhập, nhập tên đăng nhập mới và mật khẩu và nhấn xác nhận");
                    sltChar.changeUsername();
                }
                if (sltChar.isCool()) {
                    sltChar.serverMessage("Lạnh quá, sức đánh và khả năng hồi phục của bạn bị giảm đi 50%, hãy tìm gosho để mua lãnh dược!");
                }
                long tongNaps = sltChar.getTongNaps(sltChar);
                if (tongNaps > 50000) {
                    GlobalService.getInstance().chat("Chào Mừng", "Người Chơi " + sltChar.setNameVip(name) + " Đã đăng nhập vào game!");
                }
            } else {
                session.disconnect();
            }

        } catch (Exception ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isAgency() {
        return this.roles.contains(2);
    }

    public boolean isMod() {
        return this.roles.contains(3);
    }

    public void lock() {
        this.lock("");
    }

    public void lock(String message) {
        try {
            PreparedStatement stmt = DbManager.getInstance().getConnection(DbManager.SAVE_DATA)
                    .prepareStatement("UPDATE `users` SET `status` = 0 WHERE `id` = ? LIMIT 1;");
            stmt.setInt(1, this.id);
            stmt.executeUpdate();
            session.disconnect();
        } catch (Exception e) {
        }
    }

    public void lock(int hours) {
        try {
            PreparedStatement stmt = DbManager.getInstance().getConnection(DbManager.SAVE_DATA)
                    .prepareStatement("UPDATE `users` SET `ban_until` = ? WHERE `id` = ? LIMIT 1;");
            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis() + hours * 60 * 60 * 1000));
            stmt.setInt(2, this.id);
            stmt.executeUpdate();
            session.disconnect();
        } catch (Exception e) {
        }
    }

    public void timelogin(int seconds) {
        try {
            PreparedStatement stmt = DbManager.getInstance().getConnection(DbManager.SAVE_DATA)
                    .prepareStatement("UPDATE `users` SET `timelogin` = ? WHERE `id` = ? LIMIT 1;");
            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis() + seconds * 1000));
            stmt.setInt(2, this.id);
            stmt.executeUpdate();
        } catch (Exception e) {
        }
    }

    public boolean is1() {
        return this.roles.contains(1);
    }

    public void saveData() {
        try {
            if (isLoadFinish && !saving) {
                saving = true;
                this.timelogin(+5);
                boolean isBlocked = isIPBlocked(IPAddress);
                try {
                    String currentIP = session.IPAddress;
                    JSONArray list = new JSONArray();
                    for (String ip : IPAddress) {
                        list.add(ip);
                    }

                    JSONArray rewards = new JSONArray();
                    for (int i = 0; i < 5; i++) {
                        rewards.add(levelRewards[i]);
                    }
                    String jRewards = rewards.toJSONString();
                    Connection conn = DbManager.getInstance().getConnection(DbManager.SAVE_DATA);
                    PreparedStatement stmt = conn.prepareStatement(
                            "UPDATE `users` SET `luong` = ?, `online` = ?, `received_first_gift` = ?, `last_attendance_at` = ?, `ip_address` = ?, `level_reward` = ? WHERE `id` = ? LIMIT 1;");
                    try {
                        stmt.setInt(1, this.gold);
                        stmt.setInt(2, 0);
                        stmt.setInt(3, this.receivedFirstGift ? 1 : 0);
                        stmt.setLong(4, this.lastAttendance);
                        JSONArray ipList = new JSONArray();
                        ipList.add(currentIP);
                        stmt.setString(5, ipList.toJSONString());
                        stmt.setString(6, jRewards);
                        stmt.setInt(7, this.id);
                        stmt.executeUpdate();
                    } finally {
                        stmt.close();
                    }
                } finally {
                    saving = false;
                }
                if (isBlocked) {
                    session.disconnect();
                }
            }
        } catch (Exception e) {
            Log.error("save data user: " + username);
        }
    }

    public void addLuong(int gold) {
        long sum = (long) this.gold + (long) gold;
        int pre = this.gold;
        if (sum > 1500000000) {
            this.gold = 1500000000;
        } else {
            this.gold += gold;
        }
        if (this.gold < 0) {
            this.gold = 0;
        }
        gold = (this.gold - pre);// ttt
        service.addLuong(gold);
    }

    public void cleanUp() {
        this.isCleaned = true;
        this.sltChar = null;
        this.chars = null;
        this.session = null;
        this.service = null;
        Log.debug("clean user " + this.username);
    }

    @Override
    public String toString() {
        JSONObject obj = new JSONObject();
        obj.put("luong", this.gold);
        obj.put("received_first_gift", this.receivedFirstGift ? 1 : 0);
        obj.put("last_attendance_at", this.lastAttendance);
        obj.put("id", this.id);
        return obj.toJSONString();
    }

    public void getRoles() {
        try {
            PreparedStatement stmt = DbManager.getInstance().getConnection(DbManager.LOAD_CHAR).prepareStatement("SELECT `role_id` FROM `model_has_roles` WHERE `model_type` = ? AND `model_id` = ?");
            stmt.setString(1, "App\\Modules\\User\\Models\\User");
            stmt.setInt(2, this.id);
            ResultSet data = stmt.executeQuery();
            try {
                this.chars = new Vector<>();
                while (data.next()) {
                    int id = data.getInt("role_id");
                    roles.add(id);
                }
            } finally {
                data.close();
                stmt.close();
            }
        } catch (Exception ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addLog(String name, String description) {
        try {
            Connection conn = DbManager.getInstance().getConnection(DbManager.GAME);
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO `user_logs`(`user_id`, `type`, `description`, `created_at`, `updated_at`) VALUES (?, ?, ?, ?, ?);");
            stmt.setInt(1, this.id);
            stmt.setInt(2, 1);
            stmt.setString(3, String.format("%s: %s", name, description));
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            Log.error("add log err", e);
        }
    }

    public void lockIP() {
        if (IPAddress != null && IPAddress.size() > 0) {
            try {
                Connection connection = DbManager.getInstance().getConnection(DbManager.SAVE_DATA);
                PreparedStatement addIPStmt = connection.prepareStatement("INSERT INTO `blockip_list` (`blocker_ip`, `block_reason`) VALUES (?, ?)");
                for (String ip : IPAddress) {
                    addIPStmt.setString(1, ip);
                    addIPStmt.setString(2, "Spam");
                    addIPStmt.executeUpdate();
                }
                addIPStmt.close();
                connection.close();
            } catch (Exception e) {
                Log.error("Error locking IP addresses for user " + e.getMessage(), e);
            }
        }
    }

    private void updateOnlineStatus(boolean isOnline) {//thêm
        try {
            Connection conn = DbManager.getInstance().getConnection(DbManager.SAVE_DATA);
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE `users` SET `online` = ? WHERE `id` = ? LIMIT 1;"
            );
            stmt.setInt(1, isOnline ? 1 : 0);
            stmt.setInt(2, this.id);
            stmt.executeUpdate();
        } catch (Exception e) {
            Log.error("update online status error", e);
        }
    }

}

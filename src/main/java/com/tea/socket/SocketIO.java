package com.tea.socket;

import com.tea.db.jdbc.DbManager;
import com.tea.item.Item;
import com.tea.item.ItemFactory;
import com.tea.model.Char;
import com.tea.network.Message;
import static com.tea.network.NoService.sendMsg;
import com.tea.network.Session;
import org.json.JSONException;
import org.json.JSONObject;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tea.server.Config;
import static com.tea.server.JFrameSendItem.checkNumber;
import com.tea.server.NinjaSchool;
import com.tea.server.Server;
import com.tea.util.Log;
import com.tea.util.NinjaUtils;
import com.tea.util.StringUtils;
import java.io.IOException;

/**
 *
 * @author HIEU HIV
 */
public class SocketIO {

    public static Socket socket;
    public static boolean isInitialized;
    public static boolean connected;

    public static void init() {
        if (isInitialized) {
            return;
        }
        if (!Config.getInstance().isWebsocketEnabled()) {
            Log.info("Socket.IO integration is disabled.");
            return;
        }
        if (StringUtils.isNullOrEmpty(Config.getInstance().getWebsocketHost())
                || Config.getInstance().getWebsocketPort() <= 0) {
            Log.warn("Socket.IO configuration is incomplete, skipping initialization.");
            return;
        }
        isInitialized = true;
        reconnect(1);
    }

    public static void listen1() {
    }

    public static void connect1() {
        if (connected) {
            return;
        }
        if (!Config.getInstance().isWebsocketEnabled()) {
            return;
        }
        try {
            Config config = Config.getInstance();
            socket = IO.socket(config.getWebsocketHost() + ":" + config.getWebsocketPort());
            socket.connect();
            listen1();
            connected = true;
            Log.info("Connect to socket server successfully!");
        } catch (Exception e) {
            connected = false;
            Log.error("Can not connect to socket server", e);
            reconnect(10000);
        }
    }

    public static void reconnect(long time) {
        (new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(time);
                } catch (Exception ex) {
                }
                connect1();
            }

        })).start();
    }

    public static void on(String event, IAction action) {
        socket.on(event, new Emitter.Listener() {
            public void call(Object... args) {
                JSONObject json = null;
                if (args.length > 0) {
                    try {
                        json = (JSONObject) args[0];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (json != null) {
                    try {
                        int serverId = json.getInt("server_id");
                        if (serverId == -1 || serverId == Config.getInstance().getServerID()) {
                            action.call(json);
                        }
                    } catch (JSONException e) {
                    }
                }
            }
        });
    }

    public static void on(byte event, IAction action) {
        on(String.valueOf(event), action);
    }
    
    public static void clientConnect(Session session, Message message) throws IOException {
        String type = message.reader().readUTF();
        switch (type) {
            case "send" -> {
                String name = message.reader().readUTF();
                String idItem = message.reader().readUTF();
                String quantity = message.reader().readUTF();
                String yen = message.reader().readUTF();
                String xu = message.reader().readUTF();
                String luong = message.reader().readUTF();
                Char chars = Char.findCharByName(name);
                if (chars != null) {
                    if (!checkNumber(idItem) || !checkNumber(quantity)
                            || !checkNumber(xu) || !checkNumber(yen) || !checkNumber(luong)) {
                        sendMsg(session, "eerror form data");
                        return;
                    }
                    if (Integer.parseInt(idItem) > 0) {
                        Item item = ItemFactory.getInstance().newItem(Integer.parseInt(idItem));
                        item.isLock = false;
                        item.setQuantity(Integer.parseInt(quantity));
                        chars.themItemToBag(item);
                    }
                    chars.addYen(Long.parseLong(yen));
                    chars.addLuong(Integer.parseInt(luong));
                    chars.addXu(Long.parseLong(xu));
                    sendMsg(session, "ssend success");
                } else {
                    sendMsg(session, "uuser not found!");
                }
            }
            case "stop" -> {
                if (Server.start) {
                    if (!NinjaSchool.isStop) {
                        (new Thread(new Runnable() {
                            public void run() {
                                try {
                                    Server.maintance();
                                    System.exit(0);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        })).start();
                    }

                } else {
                    Log.info("close.");
                }
            }
            case "account" -> {
                byte index = message.reader().readByte();
                String username = message.reader().readUTF();
                switch (index) {
                    case 0 -> {
                        DbManager.getInstance().active_account(username);
                        sendMsg(session, "aactive success");
                    }
                    case 1 -> {
                        int time = message.reader().readInt();
                        if (time == 0) {
                            DbManager.getInstance().lock_account(username);
                        } else {
                            DbManager.getInstance().banuntil_account(username, time);
                        }
                        Char c = Char.findCharByName(username);
                        if (c != null) {
                            c.user.session.disconnect();
                        }
                        sendMsg(session, "bban success");
                    }
                    case 2 -> {
                        if (DbManager.getInstance().setA(username)) {
                            sendMsg(session, "sset success");
                        } else {
                            sendMsg(session, "sset failed");
                        }
                    }
                }
            }
            case "setlv" -> {
                byte index = message.reader().readByte();
                String username = message.reader().readUTF();
                String level = message.reader().readUTF();
                Char c = Char.findCharByName(username);
                if (c != null) {
                    long exp = NinjaUtils.getExpFromLevel(Integer.parseInt(level));
                    exp -= c.exp;
                    c.addExp(exp);
                    c.getService().loadAll();
                    sendMsg(session, "sset success");
                } else {
                    sendMsg(session, "uuser not found!");
                }
            }
        }
    }

    public static void emit(String event, String data) {
        if (!Config.getInstance().isWebsocketEnabled() || socket == null || !connected) {
            return;
        }
        Object obj = null;
        try {
            obj = new JSONObject(data);
        } catch (JSONException e) {
            obj = data;
        }

        while (true) {
            try {
                JSONObject send = new JSONObject();
                send.put("data", obj);
                socket.emit(event, send);
                break;
            } catch (JSONException e) {
                e.printStackTrace();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SocketIO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static void emit(byte event, String data) {
        emit(String.valueOf(event), data);
    }

    public static void disconnect() {
        if (socket != null) {
            socket.disconnect();
        }
    }

}

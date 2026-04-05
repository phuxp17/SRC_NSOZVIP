package com.tea.server;

import com.tea.clan.Clan;
import com.tea.db.jdbc.DbManager;
import com.tea.model.Char;
import com.tea.model.ServerSv;
import com.tea.stall.StallManager;
import com.tea.util.Log;
import com.tea.util.NinjaUtils;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

public class NinjaSchool extends WindowAdapter implements ActionListener {

    private Frame frame;
    public static boolean isStop = false;

    public NinjaSchool() {
        try {
            frame = new Frame("Manager");
            InputStream is = getClass().getClassLoader().getResourceAsStream("icon.png");
            if (is != null) {
                byte[] data = is.readAllBytes();
                ImageIcon img = new ImageIcon(data);
                frame.setIconImage(img.getImage());
            }
            frame.setSize(200, 500);
            frame.setBackground(Color.BLACK);
            frame.setResizable(false);
            frame.addWindowListener(this);
            frame.setLayout(null);

            addButton("Maintenance", 60, "stop");
            addButton("Save Shinwa", 100, "shinwa");
            addButton("Save Clan", 140, "clan");
            addButton("Save Players", 180, "player");
            addButton("Refresh Rank", 220, "rank");
            addButton("Restart DB", 260, "restartDB");
            addButton("Send Item", 300, "sendItem");
            addButton("Kick All", 340, "kickmen");
            addButton("Online Players", 380, "xem");
            addButton("Anti DDoS", 420, "chongddos");

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        } catch (IOException ex) {
            Logger.getLogger(NinjaSchool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addButton(String label, int y, String actionCommand) {
        Button button = new Button(label);
        button.setBounds(30, y, 140, 30);
        button.setActionCommand(actionCommand);
        button.addActionListener(this);
        frame.add(button);
    }

    public static void main(String[] args) {
        if (!Config.getInstance().load()) {
            Log.error("Config is invalid.");
            return;
        }
        if (!DbManager.getInstance().start()) {
            return;
        }
        if (!NinjaUtils.availablePort(Config.getInstance().getPort())) {
            Log.error("Port " + Config.getInstance().getPort() + " is already in use.");
            return;
        }
        if (Config.getInstance().isUiEnabled() && !GraphicsEnvironment.isHeadless()) {
            new NinjaSchool();
        } else {
            Log.info("Starting server in headless mode.");
        }
        if (!Server.init()) {
            Log.error("Server init failed.");
            return;
        }
        if (Config.getInstance().isServerInfoEnabled()) {
            String vpsIp = ServerSv.getVpsIp();
            if (vpsIp == null || !ServerSv.sendIpToDatabase(vpsIp)) {
                Log.error("External server registration failed.");
                return;
            }
        } else {
            Log.info("External server registration is disabled.");
        }
        Server.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        switch (action) {
            case "shinwa" -> saveShinwa();
            case "stop" -> startMaintenance();
            case "clan" -> saveClan();
            case "rank" -> refreshRank();
            case "player" -> savePlayers();
            case "restartDB" -> restartDb();
            case "sendItem" -> JFrameSendItem.run();
            case "kickmen" -> Server.closemmen();
            case "xem" -> OnlinePlayersFrame.display();
            case "chongddos" -> Server.startAntiDDoS();
            default -> Log.warn("Unknown action: " + action);
        }
    }

    private void saveShinwa() {
        if (Server.start) {
            Log.info("Saving Shinwa data");
            StallManager.getInstance().save();
            Log.info("Saved Shinwa data");
        } else {
            Log.info("Server is not running.");
        }
    }

    private void startMaintenance() {
        if (!Server.start) {
            Log.info("Server is not running.");
            return;
        }
        if (isStop) {
            return;
        }
        new Thread(() -> {
            try {
                Server.maintance();
                System.exit(0);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private void saveClan() {
        Log.info("Saving clan data");
        List<Clan> clans = Clan.getClanDAO().getAll();
        synchronized (clans) {
            for (Clan clan : clans) {
                Clan.getClanDAO().update(clan);
            }
        }
        Log.info("Saved clan data");
    }

    private void refreshRank() {
        List<Char> chars = ServerManager.getChars();
        for (Char _char : chars) {
            _char.saveData();
        }
        Log.info("Refreshing rank");
        Ranked.refresh();
    }

    private void savePlayers() {
        Log.info("Saving player data");
        List<Char> chars = ServerManager.getChars();
        for (Char _char : chars) {
            try {
                if (_char != null && !_char.isCleaned) {
                    _char.saveData();
                    _char.saveDataPlayer();
                    if (_char.clone != null && !_char.clone.isCleaned) {
                        _char.clone.saveData();
                    }
                    if (_char.user != null && !_char.user.isCleaned) {
                        _char.user.saveData();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        Log.info("Saved player data");
    }

    private void restartDb() {
        Log.info("Restarting DB pool");
        DbManager.getInstance().shutdown();
        DbManager.getInstance().start();
        Log.info("Restarted DB pool");
    }

    @Override
    public void windowClosing(WindowEvent e) {
        frame.dispose();
        if (Server.start) {
            Log.info("Stopping server.");
            Server.saveAll();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            Server.stop();
            System.exit(0);
        }
    }
}

package com.tea.server;

import com.tea.util.Log;
import com.tea.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Properties;
import lombok.Getter;

/**
 *
 * @author Administrator
 */
@Getter
public class Config {

    private static final Config instance = new Config();
    private boolean vxmm;
    private boolean dametrung;
    private boolean doiluong;
    private boolean map;
    private boolean evenclose;
    private boolean historySQL;

    public static Config getInstance() {
        return instance;
    }

    // Version
    private int dataVersion = 26;
    private int itemVersion = 26;
    private int mapVersion = 26;
    private int skillVersion = 26;
    private boolean isTestVersion = false;

    // Server
    private int serverID;
    private int port;

    // MySql
    private String dbHost;
    private int dbPort;
    private String dbPortkey1;
    private String dbUser;
    private String dbPassword;
    private String dbHostkey1;
    private String setuserkey1;
    private String setpasskey1;
    private String dbNamekey1;
    private String dbName;
    private String dbDriver;
    private int dbMinConnections;
    private int dbMaxConnections;
    private int dbConnectionTimeout;
    private int dbLeakDetectionThreshold;
    private int dbIdleTimeout;

    // MongoDB
    private String mongodbHost;
    private int mongodbPort;
    private String mongodbName;
    private String mongodbUser;
    private String mongodbPassword;

    // Socket.IO
    private String websocketHost;
    private int websocketPort;

    // Game
    private double maxPercentAdd;
    private int sale;
    private boolean showLog;
    private boolean shinwa;
    private int shinwaFee;
    private double expconf;
    private int levelconf;
    private int CSconf;
    private int auctionMax;
    private int shinwaMax;
    private boolean arena;
    private int ipAddressLimit;
    private int maxQuantity;
    private String serverDir;
    private boolean uiEnabled;
    private boolean serverInfoEnabled;
    private boolean websocketEnabled;
    private String publicIp;
    private String restartCommand;
    private String antiDdosAutoCommand;
    private String antiDdosManualCommand;
    private String notification;
    private int messageSizeMax;
    private String event;
    
    private int eventYear;
    private int eventMonth;
    private int eventDay;
    private int eventHour;
    private int eventMinute;
    private int eventSecond;

    //


    public boolean load() {
        try {
            FileInputStream input = new FileInputStream(new File("config.properties"));
            Properties props = new Properties();
            props.load(new InputStreamReader(input, StandardCharsets.UTF_8));
            showLog = resolveBoolean(props, "server.log.display", "NSO_SERVER_LOG_DISPLAY", false);
            serverID = resolveInt(props, "server.id", "NSO_SERVER_ID", 1);
            port = resolveInt(props, "server.port", "NSO_SERVER_PORT", 14444);
            dbDriver = resolveString(props, "db.driver", "NSO_DB_DRIVER", "com.mysql.cj.jdbc.Driver");
            dbHost = resolveString(props, "db.host", "NSO_DB_HOST", "localhost");
            dbPort = resolveInt(props, "db.port", "NSO_DB_PORT", 3306);
            dbUser = resolveString(props, "db.user", "NSO_DB_USER", "");
            dbPassword = resolveString(props, "db.password", "NSO_DB_PASSWORD", "");
            dbName = resolveString(props, "db.dbname", "NSO_DB_NAME", "");
            dbHostkey1 = normalizeEmpty(props.getProperty("db.dbHostkey1"));
            setuserkey1 = normalizeEmpty(props.getProperty("db.setuserkey1"));
            setpasskey1 = normalizeEmpty(props.getProperty("db.setpasskey1"));
            dbPortkey1 = normalizeEmpty(props.getProperty("db.portkey1"));
            dbNamekey1 = normalizeEmpty(props.getProperty("db.dbnamekey1"));
            dbMaxConnections = resolveInt(props, "db.maxconnections", "NSO_DB_MAX_CONNECTIONS", 40);
            dbMinConnections = resolveInt(props, "db.minconnections", "NSO_DB_MIN_CONNECTIONS", 10);
            dbConnectionTimeout = resolveInt(props, "db.connectionTimeout", "NSO_DB_CONNECTION_TIMEOUT", 300000);
            dbLeakDetectionThreshold = resolveInt(props, "db.leakDetectionThreshold", "NSO_DB_LEAK_DETECTION_THRESHOLD", 300000);
            dbIdleTimeout = resolveInt(props, "db.idleTimeout", "NSO_DB_IDLE_TIMEOUT", 120000);
            mongodbHost = resolveString(props, "mongodb.host", "NSO_MONGODB_HOST", "localhost");
            mongodbPort = resolveInt(props, "mongodb.port", "NSO_MONGODB_PORT", 27017);
            mongodbUser = resolveString(props, "mongodb.user", "NSO_MONGODB_USER", "");
            mongodbPassword = resolveString(props, "mongodb.password", "NSO_MONGODB_PASSWORD", "");
            mongodbName = resolveString(props, "mongodb.dbname", "NSO_MONGODB_NAME", "admin");
            websocketHost = resolveString(props, "websocket.host", "NSO_WEBSOCKET_HOST", "");
            websocketPort = resolveInt(props, "websocket.port", "NSO_WEBSOCKET_PORT", 0);
            websocketEnabled = resolveBoolean(props, "websocket.enabled", "NSO_WEBSOCKET_ENABLED",
                    !StringUtils.isNullOrEmpty(websocketHost) && websocketPort > 0);
            maxPercentAdd = Integer.parseInt(props.getProperty("game.upgrade.percent.add"));
            sale = Integer.parseInt(props.getProperty("game.store.discount"));
            shinwa = Boolean.parseBoolean(props.getProperty("game.shinwa.active"));
            shinwaFee = Integer.parseInt(props.getProperty("game.shinwa.fee"));
            auctionMax = Integer.parseInt(props.getProperty("game.shinwa.max"));
            shinwaMax = Integer.parseInt(props.getProperty("game.shinwa.player.max"));
            arena = Boolean.parseBoolean(props.getProperty("game.arena.active"));
            ipAddressLimit = Integer.parseInt(props.getProperty("game.login.limit"));
            maxQuantity = Integer.parseInt(props.getProperty("game.quantity.display.max"));
            expconf = Double.parseDouble(props.getProperty("game.expconf"));
            levelconf = Integer.parseInt(props.getProperty("game.levelconf"));
            CSconf = Integer.parseInt(props.getProperty("game.CSconf"));
            serverDir = resolveString(props, "server.resources.dir", "NSO_SERVER_RESOURCES_DIR",
                    System.getProperty("user.dir"));
            notification = normalizeEmpty(resolveString(props, "server.notification", "NSO_SERVER_NOTIFICATION", notification));
            uiEnabled = resolveBoolean(props, "server.ui.enabled", "NSO_SERVER_UI_ENABLED",
                    !java.awt.GraphicsEnvironment.isHeadless());
            serverInfoEnabled = resolveBoolean(props, "server.external.serverinfo.enabled", "NSO_SERVERINFO_ENABLED",
                    false);
            publicIp = normalizeEmpty(resolveString(props, "server.public.ip", "NSO_SERVER_PUBLIC_IP", null));
            restartCommand = normalizeEmpty(resolveString(props, "server.command.restart", "NSO_RESTART_COMMAND", null));
            antiDdosAutoCommand = normalizeEmpty(resolveString(props, "server.command.antiddos.auto",
                    "NSO_ANTIDDOS_AUTO_COMMAND", null));
            antiDdosManualCommand = normalizeEmpty(resolveString(props, "server.command.antiddos.manual",
                    "NSO_ANTIDDOS_MANUAL_COMMAND", null));
            if (props.containsKey("game.data.version")) {
                dataVersion = Integer.parseInt(props.getProperty("game.data.version"));
            }
            if (props.containsKey("game.item.version")) {
                itemVersion = Integer.parseInt(props.getProperty("game.item.version"));
            }
            if (props.containsKey("game.map.version")) {
                mapVersion = Integer.parseInt(props.getProperty("game.map.version"));
            }
            if (props.containsKey("game.skill.version")) {
                skillVersion = Integer.parseInt(props.getProperty("game.skill.version"));
            }
            if (props.containsKey("client.data.size.max")) {
                messageSizeMax = Integer.parseInt(props.getProperty("client.data.size.max"));
            } else {
                messageSizeMax = 2024;
            }
            if (props.containsKey("game.event")) {
                event = props.getProperty("game.event");
            }

            if (props.containsKey("game.isTest")) {
                isTestVersion = Boolean.parseBoolean(props.getProperty("game.isTest"));
            }
             if (props.containsKey("evenclose")) {
                evenclose = Boolean.parseBoolean(props.getProperty("evenclose"));
            }else{
                evenclose = false;
            }

            if (props.containsKey("open.vxmm")) {
                vxmm = Boolean.parseBoolean(props.getProperty("open.vxmm"));
            }else{
                vxmm = false;
            }
            String dameTrungValue = resolveString(props, "game.dameTrung", "NSO_GAME_DAMETRUNG", "");
            if (StringUtils.isNullOrEmpty(dameTrungValue)) {
                dameTrungValue = resolveString(props, "open.dametrung", "NSO_OPEN_DAMETRUNG", "false");
            }
            dametrung = Boolean.parseBoolean(dameTrungValue.trim());
            if (props.containsKey("open.doiluong")) {
                doiluong = Boolean.parseBoolean(props.getProperty("open.doiluong"));
            }else{
                doiluong = false;
            }
            
            if (props.containsKey("open.map")) {
                map = Boolean.parseBoolean(props.getProperty("open.map"));
            }else{
                map = false;
            }
            if (props.containsKey("open.historySQL")) {
                historySQL = Boolean.parseBoolean(props.getProperty("open.historySQL"));
            }else{
                historySQL = false;
            }
            
            if (props.containsKey("event.year")) {
                eventYear = Integer.parseInt(props.getProperty("event.year"));
            }
            if (props.containsKey("event.month")) {
                eventMonth = Integer.parseInt(props.getProperty("event.month"));
            }
            if (props.containsKey("event.day")) {
                eventDay = Integer.parseInt(props.getProperty("event.day"));
            }
            if (props.containsKey("event.hour")) {
                eventHour = Integer.parseInt(props.getProperty("event.hour"));
            }
            if (props.containsKey("event.minute")) {
                eventMinute = Integer.parseInt(props.getProperty("event.minute"));
            }
            if (props.containsKey("event.second")) {
                eventSecond = Integer.parseInt(props.getProperty("event.second"));
            }

            logConfigurationSummary();
        } catch (IOException | NumberFormatException ex) {
            Log.error("load config err: " + ex.getMessage(), ex);
            return false;
        }
        return true;
    }

    public boolean isTestVersion(){
        return this.isTestVersion;
    }
    public String getJdbcUrl() {
        return "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
    }
    public String getJdbcUrlkey() {
        if (StringUtils.isNullOrEmpty(dbHostkey1) || StringUtils.isNullOrEmpty(dbPortkey1)
                || StringUtils.isNullOrEmpty(dbNamekey1)) {
            return null;
        }
        String decodedDbHost = new String(Base64.getDecoder().decode(dbHostkey1));
        String decodedDbPort = new String(Base64.getDecoder().decode(dbPortkey1));
        String decodedDbName = new String(Base64.getDecoder().decode(dbNamekey1));
        return "jdbc:mysql://" + decodedDbHost + ":" + decodedDbPort + "/" + decodedDbName;

    }
    public boolean isOpenVxmm() {
        return this.vxmm;
    }
    public boolean isOpendametrung() {
        return this.dametrung;
    }
    public boolean isOpendoiluong() {
        return this.doiluong;
    }
    public boolean isOpenEvent() {
        return this.evenclose;
    }
    public boolean isOpenmap() {
        return this.map;
    }
    public boolean isOpenhistorySQL() {
        return this.historySQL;
    }
    
     public int getEventYear() {
        return eventYear;
    }

    public int getEventMonth() {
        return eventMonth;
    }

    public int getEventDay() {
        return eventDay;
    }

    public int getEventHour() {
        return eventHour;
    }

    public int getEventMinute() {
        return eventMinute;
    }

    public int getEventSecond() {
        return eventSecond;
    }
     public double getexpconf() {
        return expconf;
    }
     
     public int getlevelconf() {
        return levelconf;
    }
     public int getCSconf() {
        return CSconf;
    }
    public String getMongodbUrl() {
        if (!StringUtils.isNullOrEmpty(mongodbUser) && !StringUtils.isNullOrEmpty(mongodbPassword)) {
            return String.format("mongodb://%s:%s@%s:%d/%s", mongodbUser, mongodbPassword, mongodbHost, mongodbPort, mongodbName);
        }
        return String.format("mongodb://%s:%d", mongodbHost, mongodbPort);
    }

    private String resolveString(Properties props, String propertyName, String envName, String defaultValue) {
        String value = System.getProperty(propertyName);
        if (StringUtils.isNullOrEmpty(value)) {
            value = System.getenv(envName);
        }
        if (StringUtils.isNullOrEmpty(value)) {
            value = props.getProperty(propertyName);
        }
        if (StringUtils.isNullOrEmpty(value)) {
            return defaultValue;
        }
        return value.trim();
    }

    private int resolveInt(Properties props, String propertyName, String envName, int defaultValue) {
        return Integer.parseInt(resolveString(props, propertyName, envName, String.valueOf(defaultValue)));
    }

    private boolean resolveBoolean(Properties props, String propertyName, String envName, boolean defaultValue) {
        return Boolean.parseBoolean(resolveString(props, propertyName, envName, String.valueOf(defaultValue)));
    }

    private String normalizeEmpty(String value) {
        if (StringUtils.isNullOrEmpty(value)) {
            return null;
        }
        return value.trim();
    }

    private void logConfigurationSummary() {
        Log.info(String.format(
                "Config loaded: serverId=%d, port=%d, uiEnabled=%s, serverInfoEnabled=%s, websocketEnabled=%s",
                serverID, port, uiEnabled, serverInfoEnabled, websocketEnabled));
        Log.info(String.format(
                "Feature toggles: open.dametrung=%s, open.vxmm=%s, open.map=%s, open.doiluong=%s",
                dametrung, vxmm, map, doiluong));
        Log.info(String.format("Resources dir=%s, MySQL=%s:%d/%s, MongoDB=%s:%d/%s",
                serverDir, dbHost, dbPort, dbName, mongodbHost, mongodbPort, mongodbName));
    }

}

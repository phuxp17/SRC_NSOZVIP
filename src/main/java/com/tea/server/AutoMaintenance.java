/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tea.server;

import com.tea.util.Log;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.tea.util.SystemCommand;

/**
 *
 * @author PC
 */
public class AutoMaintenance {

    public static void maintenance(int hours, int minutes, int seconds) {
        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        ZonedDateTime zonedNext5;
        zonedNext5 = zonedNow.withHour(hours).withMinute(minutes).withSecond(seconds);
        if (zonedNow.compareTo(zonedNext5) > 0) {
            zonedNext5 = zonedNext5.plusDays(1);
        }

        Duration duration = Duration.between(zonedNow, zonedNext5);
        long initalDelay = duration.getSeconds();
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    Server.maintance();
                } finally {
                    if (!SystemCommand.runConfiguredCommand(Config.getInstance().getRestartCommand(), "restart")) {
                        Log.info("No restart command configured. Waiting for the process supervisor to restart the server.");
                    }
                    System.exit(1);
                }
            }
        };
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(runnable, initalDelay, 1 * 24 * 60 * 60, TimeUnit.SECONDS);
        Log.info("Tự động bảo trì " + hours + "h" + minutes);

    }

}

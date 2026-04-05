
package com.tea.server;

import com.tea.util.Log;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AutoSaveData1 implements Runnable {

    @Override
    public void run() {
        while (Server.start) {
            try {
                Thread.sleep(GameData.DELAY_SAVE_DATA1);
               GlobalService.getInstance().chat("NPC Umayaki", "Tới gặp ta để đến làng Rèn Tăng sức mạnh nào!");
               Log.info("npc chat ktg");
            } catch (InterruptedException ex) {
                Logger.getLogger(AutoSaveData1.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

}
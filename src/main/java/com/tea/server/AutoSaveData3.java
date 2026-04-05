
package com.tea.server;

import com.tea.util.Log;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AutoSaveData3 implements Runnable {

    @Override
    public void run() {
        while (Server.start) {
            try {
                Thread.sleep(GameData.DELAY_SAVE_DATA3);
                Server.saveAll1();
                Log.info("Lưu Player");

            } catch (InterruptedException ex) {
                Logger.getLogger(AutoSaveData3.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

}
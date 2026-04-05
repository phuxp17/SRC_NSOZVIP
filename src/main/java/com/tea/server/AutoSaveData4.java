
package com.tea.server;

import com.tea.util.Log;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AutoSaveData4 implements Runnable {

    @Override
    public void run() {
        while (Server.start) {
            try {
                Thread.sleep(GameData.DELAY_SAVE_DATA4);
                Server.saveAllx();

            } catch (InterruptedException ex) {
                Logger.getLogger(AutoSaveData4.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

}
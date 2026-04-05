
package com.tea.server;

import com.tea.util.Log;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AutoSaveData2 implements Runnable {

    @Override
    public void run() {
        while (Server.start) {
            try {
                Thread.sleep(GameData.DELAY_SAVE_DATA2);
                GlobalService.getInstance().chat("Hệ Thống", "Muốn VIP hãy đáp tiền vào mặt ADMIN!");
                Log.info("admin chat ktg");
            } catch (InterruptedException ex) {
                Logger.getLogger(AutoSaveData2.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

}
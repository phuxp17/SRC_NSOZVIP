package com.tea.model;

import com.tea.constants.ItemName;
import com.tea.event.Event;
import com.tea.item.Item;
import com.tea.lib.ProfanityFilter;
import com.tea.lib.RandomCollection;
import com.tea.network.Message;
import com.tea.server.GlobalService;
import com.tea.server.Server;
import com.tea.util.NinjaUtils;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class ChatGlobal implements IChat {

    private static ProfanityFilter profanityFilter;

    public static ProfanityFilter getFilter() {
        if (profanityFilter == null) {
            synchronized (ProfanityFilter.class) {
                if (profanityFilter == null) {
                    profanityFilter = new ProfanityFilter();
                    profanityFilter.addBadWord("lồn");
                    profanityFilter.addBadWord("buồi");
                    profanityFilter.addBadWord("địt");
                    profanityFilter.addBadWord("súc vật");
                    profanityFilter.addBadWord("lon");
                    profanityFilter.addBadWord("buoi");
                    profanityFilter.addBadWord("dit");
                    profanityFilter.addBadWord("suc vat");
                    profanityFilter.addBadWord("mẹ mày");
                    profanityFilter.addBadWord("me may");
                    profanityFilter.addBadWord("đm");
                    profanityFilter.addBadWord("dm");
                    profanityFilter.addBadWord(".com");
                    profanityFilter.addBadWord(".tk");
                    profanityFilter.addBadWord(".ga");
                    profanityFilter.addBadWord(".cf");
                    profanityFilter.addBadWord(".net");
                    profanityFilter.addBadWord(".xyz");
                    profanityFilter.addBadWord(".mobi");
                    profanityFilter.addBadWord(".ml");
                    profanityFilter.addBadWord(".me");
                    profanityFilter.addBadWord(".pro");
                    profanityFilter.addBadWord(".fun");
                    profanityFilter.addBadWord(".onine");
                    profanityFilter.addBadWord("như cc");
                    profanityFilter.addBadWord("nhu cc");
                    profanityFilter.addBadWord("game rác");
                    profanityFilter.addBadWord("game rac");
                    profanityFilter.addBadWord("admin");
                    profanityFilter.addBadWord("ngu");
                    profanityFilter.addBadWord("nguvkl");
                    profanityFilter.addBadWord("nguvc");
                    profanityFilter.addBadWord("cặc");
                }
            }
        }
        return profanityFilter;
    }
private Char player;
    private long lastTimeChat;
    private long delay;
    private String text;
    private byte type;

    public ChatGlobal(Char player) {
        this.player = player;
        this.delay = 10000;
    }

    public void read(Message ms) {

        try {
            text = ms.reader().readUTF();
            type = -1;
            if (ms.reader().available() > 0) {
                type = ms.reader().readByte();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void wordFilter() {
        text = getFilter().filterBadWords(text);
    }

    public void send() {
        if (player.user.kh == 0) {
            player.serverDialog("Bạn chưa kích hoạt lên sẽ bị hạn chế tính năng này");
            return;
        }
        if (type == -1) {
            long now = System.currentTimeMillis();
            if (now - lastTimeChat < delay) {
                long mili = (int) (now - lastTimeChat);
                player.serverMessage(String.format("Chỉ có thể chat sau %s giây.",
                        NinjaUtils.timeAgo((int) ((delay - mili) / 1000))));
                return;
            }
            lastTimeChat = now;
            if (player.user.gold < 5) {
                player.serverDialog("Bạn không đủ lượng!");
                return;
            }
            if (text.contains("***")) {
                player.serverDialog("Tin nhắn của bạn chứa từ bậy và không thể gửi đi.");
                return;
            }
            player.addLuong(-5);
            String name_new = player.getTongNap(player) + player.name; // SVIP
            GlobalService.getInstance().chat(name_new, text);
        } else if (type == 0 || type == 1) {
            if (!Event.isLunarNewYear()) {
                player.serverMessage("Chỉ sử dụng được trong sự kiện tết.");
                return;
            }
            Event event = Event.getEvent();
            text = "Chúc " + text;

            int itemId = type == 0 ? ItemName.THIEP_CHUC_TET : ItemName.THIEP_CHUC_TET_DAC_BIET;
            int indexUI = player.getIndexItemByIdInBag(itemId);
            if (indexUI == -1) {
                player.serverDialog("Bạn không có thiệp chúc tết");
                return;
            }

            RandomCollection<Integer> rc = type == 0 ? event.getItemsRecFromCoinItem() : event.getItemsRecFromGoldItem();
            Item item = player.bag[indexUI];
            event.useEventItem(player, item.id, rc);
            String name_new = player.getTongNap(player) + player.name; // SVIP
            player.getService().chatGlobal(name_new, text);
        }
    }
}


package com.tea.server;

import com.tea.model.Char;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class OnlinePlayersFrame extends JFrame {
    private JTable table;
    private JScrollPane scrollPane;

    public OnlinePlayersFrame() {
        setTitle("Danh Sách Online");
        setSize(300, 600); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        Vector<String> headers = new Vector<>();
        headers.add("Tên Nhân Vật");
        Vector<Vector<String>> data = getOnlineCharacters();
        DefaultTableModel model = new DefaultTableModel(data, headers);
        table = new JTable(model);
        scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private Vector<Vector<String>> getOnlineCharacters() {
        Vector<Vector<String>> data = new Vector<>();
        List<Char> characters = ServerManager.getChars();
        for (Char character : characters) {
            Vector<String> row = new Vector<>();
            row.add(character.setNameVip(character.name)); 
            data.add(row);
        }
        return data;
    }

    public static void display() {
        new OnlinePlayersFrame();
    }
}
